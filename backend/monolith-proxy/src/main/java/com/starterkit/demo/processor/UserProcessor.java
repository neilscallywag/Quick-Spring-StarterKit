/* (C)2024 */
package com.starterkit.demo.processor;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.starterkit.demo.exception.InvalidRequestException;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;

public class UserProcessor {
    public static Set<EnumRole> VALID_ROLES = EnumSet.allOf(EnumRole.class);

    public static Function<User, User> validateRoles() {
        return user -> {
            Set<EnumRole> userRoles =
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
            if (!VALID_ROLES.containsAll(userRoles)) {
                throw new IllegalArgumentException("Invalid role(s) found: " + userRoles);
            }
            return user;
        };
    }

    public static Function<User, User> validateUsername(
            Function<String, Optional<User>> findByUsername) {
        return user -> {
            Optional<User> existingUser = findByUsername.apply(user.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new InvalidRequestException("Username is already taken.");
            }
            return user;
        };
    }

    public static Function<User, User> validateEmail(Function<String, Optional<User>> findByEmail) {
        return user -> {
            Optional<User> existingUser = findByEmail.apply(user.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new InvalidRequestException("Email is already taken.");
            }
            return user;
        };
    }
}
