package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    public User getUser(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public void saveUserWithRolesAndPassword(User user, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(new HashSet<>(roleRepository.findAllById(roleIds)));
        } else {
            user.setRoles(new HashSet<>());
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else if (user.getId() != null) {
            User emptyPass = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(emptyPass.getPassword());
        }
        userRepository.save(user);
    }

    public User getCurrentUserFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByIdWithRoles(Long id) {
        return userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public User updateUser(User userData, Long id) {
        User editUser = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userData.getName() != null && !userData.getName().isEmpty()) {
            editUser.setName(userData.getName());
        }

        if (userData.getSurname() != null && !userData.getSurname().isEmpty()) {
            editUser.setSurname(userData.getSurname());
        }

        if (userData.getAge() != null) {
            editUser.setAge(userData.getAge());
        }

        if (userData.getUsername() != null && !userData.getUsername().isEmpty()) {
            editUser.setUsername(userData.getUsername());
        }
        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
            editUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        if (userData.getRoleIds() != null && !userData.getRoleIds().isEmpty()) {
            editUser.setRoles(new HashSet<>(roleRepository.findAllById(userData.getRoleIds())));
        }
        return userRepository.save(editUser);
    }
}
