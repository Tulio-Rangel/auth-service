package com.tulio.authservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;


    private List<String> accountIds;

    public User(String id, String name, String password, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}