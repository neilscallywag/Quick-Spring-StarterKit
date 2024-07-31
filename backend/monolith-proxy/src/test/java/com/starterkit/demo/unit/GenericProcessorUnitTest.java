/* (C)2024 */
package com.starterkit.demo.unit;

import org.junit.jupiter.api.Test;

import com.starterkit.demo.model.User;
import com.starterkit.demo.processor.GenericProcessor;

import static org.junit.jupiter.api.Assertions.*;

class GenericProcessorUnitTest {

    @Test
    void testValidation() {
        User user = new User();
        user.setUsername("validUser");

        GenericProcessor<User> processor =
                GenericProcessor.of(user)
                        .validate(
                                u -> {
                                    if (u.getUsername() == null || u.getUsername().isEmpty()) {
                                        throw new IllegalArgumentException(
                                                "Username cannot be null or empty");
                                    }
                                    return u;
                                });

        assertDoesNotThrow(processor::process);
    }

    @Test
    void testTransformation() {
        User user = new User();
        user.setUsername("oldUsername");

        GenericProcessor<User> processor =
                GenericProcessor.of(user)
                        .transform(
                                u -> {
                                    u.setUsername("newUsername");
                                    return u;
                                });

        User processedUser = processor.process();
        assertEquals("newUsername", processedUser.getUsername());
    }

    @Test
    void testValidationFailure() {
        User user = new User();

        GenericProcessor<User> processor =
                GenericProcessor.of(user)
                        .validate(
                                u -> {
                                    if (u.getUsername() == null || u.getUsername().isEmpty()) {
                                        throw new IllegalArgumentException(
                                                "Username cannot be null or empty");
                                    }
                                    return u;
                                });

        assertThrows(IllegalArgumentException.class, processor::process);
    }
}
