package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("api/controller")
public class UserRestController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUser(@PathVariable Long id) {
        return userService.getUserByIdWithRoles(id);
    }

    @GetMapping("/me")
    public User getCurrentUser() {
        return userService.getCurrentUserFromContext();
    }

    @GetMapping("/roles")
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public void AddUser(@RequestBody User user) {
        userService.saveUserWithRolesAndPassword(user, user.getRoleIds());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void editUser(@RequestBody User user, @PathVariable Long id) {
        System.out.println("Получен пользователь: " + user);
        userService.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
