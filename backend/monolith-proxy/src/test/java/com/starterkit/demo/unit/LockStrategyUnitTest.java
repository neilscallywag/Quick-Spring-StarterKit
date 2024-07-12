package com.starterkit.demo.unit;

import com.starterkit.demo.model.User;
import com.starterkit.demo.service.LockStrategy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockStrategyTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private LockStrategy lockStrategy;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
    }

    @Test
    void testLockUser() {
        when(entityManager.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(user);

        User lockedUser = lockStrategy.lockUser(userId, LockModeType.PESSIMISTIC_WRITE);

        assertNotNull(lockedUser);
        assertEquals(userId, lockedUser.getId());
        verify(entityManager, times(1)).find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    void testUpdateUserWithLock() {
        when(entityManager.find(User.class, userId, LockModeType.OPTIMISTIC)).thenReturn(user);
        when(entityManager.merge(any(User.class))).thenReturn(user);

        User updatedUser = lockStrategy.updateUserWithLock(user, LockModeType.OPTIMISTIC);

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        verify(entityManager, times(1)).find(User.class, userId, LockModeType.OPTIMISTIC);
        verify(entityManager, times(1)).merge(user);
    }

    @Test
    void testUpdateUserWithLockThrowsException() {
        User newUser = new User(); // new user without ID

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            lockStrategy.updateUserWithLock(newUser, LockModeType.OPTIMISTIC)
        );

        assertEquals("Entity does not exist.", exception.getMessage());
        verify(entityManager, never()).merge(any());
    }
}
