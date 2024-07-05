package com.starterkit.demo.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import java.util.HashSet;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id", unique = true)
    private String providerId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    public enum AuthProvider {
        LOCAL,
        GOOGLE,
        FACEBOOK
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    // @Default(value = AuthProvider.LOCAL)
    private AuthProvider authProvider;
}
