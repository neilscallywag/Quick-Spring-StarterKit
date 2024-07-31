/* (C)2024 */
package com.starterkit.demo.unit;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.processor.UserProcessor;

import static org.junit.jupiter.api.Assertions.*;

class UserProcessorUnitTest {

    @Test
    void testValidateRoles() {
        User user = new User();
        user.setRoles(Collections.singleton(new Role(EnumRole.ROLE_USER)));

        Function<User, User> validation = UserProcessor.validateRoles();

        assertDoesNotThrow(() -> validation.apply(user));
    }

    @Test
    void testValidateRolesFailure() {
        User user = new User();

        // Using a valid role and modifying the VALID_ROLES set for testing purposes.
        Set<Role> invalidRoles = Collections.singleton(new Role(EnumRole.ROLE_MANAGER));
        user.setRoles(invalidRoles);

        Set<EnumRole> validRolesBeforeTest = UserProcessor.VALID_ROLES;

        // Remove the ROLE_MANAGER from the valid roles to simulate an invalid role scenario
        UserProcessor.VALID_ROLES = EnumSet.of(EnumRole.ROLE_USER, EnumRole.ROLE_OFFICER);

        Function<User, User> validation = UserProcessor.validateRoles();

        assertThrows(IllegalArgumentException.class, () -> validation.apply(user));

        // Restore the valid roles after the test
        UserProcessor.VALID_ROLES = validRolesBeforeTest;
    }
}
