package com.tulio.authservice.service;

import com.tulio.authservice.model.User;
import com.tulio.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Función pura que crea un nuevo User con password encriptado
    private User prepareNewUser(User userInput) {
        return new User(
                userInput.getId(),
                userInput.getName(),
                passwordEncoder.encode(userInput.getPassword()),
                userInput.getEmail()
        );
    }

    // Método que maneja el efecto secundario (persistencia)
    public User createUser(User userInput) {
        User preparedUser = prepareNewUser(userInput);  // Primero la transformación pura
        return userRepository.save(preparedUser);       // Luego el efecto secundario
    }

    // Función pura que prepara el usuario para actualización
    private User prepareUserUpdate(User userInput) {
        String newPassword = userInput.getPassword() != null && !userInput.getPassword().isEmpty()
                ? passwordEncoder.encode(userInput.getPassword())
                : userInput.getPassword();

        return new User(
                userInput.getId(),
                userInput.getName(),
                newPassword,
                userInput.getEmail()
        );
    }

    // Método que maneja el efecto secundario (persistencia)
    public User updateUser(User userInput) {
        User preparedUser = prepareUserUpdate(userInput);  // Primero la transformación pura
        return userRepository.save(preparedUser);          // Luego el efecto secundario
    }


    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}