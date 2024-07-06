package com.starterkit.demo.dto;

import lombok.Data;
import java.util.Date;

import com.starterkit.demo.model.User;

@Data
public class NewUserRequestDTO {

    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String password;

    public static User toUser(NewUserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setPassword(dto.getPassword());
        user.setAuthProvider(User.AuthProvider.LOCAL);
        user.setEmailVerified(false);
        return user;
    }
}
