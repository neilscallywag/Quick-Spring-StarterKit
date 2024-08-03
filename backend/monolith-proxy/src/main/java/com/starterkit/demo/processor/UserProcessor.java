package com.starterkit.demo.processor;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Objects;

import com.starterkit.demo.exception.InvalidRequestException;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;

public class UserProcessor {

    public static Set<EnumRole> VALID_ROLES = EnumSet.allOf(EnumRole.class);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    public static Function<User, User> validateRoles() {
        return user -> {
            Set<EnumRole> userRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
            if (!VALID_ROLES.containsAll(userRoles)) {
                throw new IllegalArgumentException("Invalid role(s) found: " + userRoles);
            }
            return user;
        };
    }

    public static Function<User, User> validateUsername(
            Function<String, Optional<User>> findByUsername) {
        Objects.requireNonNull(findByUsername);
        return user -> {
            Optional<User> existingUser = findByUsername.apply(user.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new InvalidRequestException("Username is already taken.");
            }
            return user;
        };
    }

    public static Function<User, User> validateEmail(Function<String, Optional<User>> findByEmail) {
        Objects.requireNonNull(findByEmail);
        return user -> {
            Optional<User> existingUser = findByEmail.apply(user.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new InvalidRequestException("Email is already taken.");
            }
            return user;
        };
    }

    public static Function<User, User> validatePassword() {
        return user -> {
            if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
                throw new InvalidRequestException("Password must be at least 8 characters long and include a number, a lowercase letter, and an uppercase letter.");
            }
            return user;
        };
    }

    public static Function<User, User> validatePhoneNumber() {
        return user -> {
            if (user.getPhoneNumber() != null && !PHONE_PATTERN.matcher(user.getPhoneNumber()).matches()) {
                throw new InvalidRequestException("Invalid phone number format.");
            }
            return user;
        };
    }

    public static Function<User, User> validateEmailDomain(Set<String> allowedDomains) {
        return user -> {
            String domain = user.getEmail().substring(user.getEmail().indexOf("@") + 1);
            if (!allowedDomains.contains(domain)) {
                throw new InvalidRequestException("Email domain is not allowed.");
            }
            return user;
        };
    }
    public static Function<User, User> validateDateOfBirth(int minimumAge) {
        return user -> {
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.YEAR, -minimumAge);
            Date minimumDateOfBirth = calendar.getTime();

            if (user.getDateOfBirth() != null && user.getDateOfBirth().after(minimumDateOfBirth)) {
                throw new InvalidRequestException("User must be at least " + minimumAge + " years old.");
            }
            return user;
        };
    }
}
