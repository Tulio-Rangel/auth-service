package com.tulio.authservice.controller;

import com.tulio.authservice.model.User;
import com.tulio.authservice.service.MessageProducerService;
import com.tulio.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class UserController {

    private final UserService userService;
    private final MessageProducerService messageProducerService;

    public UserController(UserService userService, MessageProducerService messageProducerService) {
        this.userService = userService;
        this.messageProducerService = messageProducerService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
    	try {
	    	User createdUser = userService.createUser(user);
	    	
	        messageProducerService.sendMessage("user.registration", "Nuevo usuario registrado: " + user.getEmail(), true);
	
	        return ResponseEntity.ok(createdUser);
    	} catch (Exception e){
    		messageProducerService.sendMessage("user.registration", "Error en registro de usuario: " + user.getEmail(), false);
    		return ResponseEntity.badRequest().body("Error creating user: " + e.getMessage());
    	}
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User userDetails) {
        User user = userService.findById(userId);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        // Si se proporciona una nueva contraseña, actualizarla
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

}
