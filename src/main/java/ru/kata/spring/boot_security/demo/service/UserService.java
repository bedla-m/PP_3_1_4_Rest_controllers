package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;


public interface UserService extends UserDetailsService {
    void saveUser(User user);

    void deleteUser(Long id);

    User getUser(Long id);

    User findByUsername(String username);

    List<User> getAllUser();

    boolean isAdmin(Authentication auth);

    void saveUserWithRolesAndPassword(User user, List<Long> roleIds);

    User getCurrentUserFromContext();

    User getUserByIdWithRoles(Long id);

    User updateUser(User userData, Long id);

}
