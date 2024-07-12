package com.starterkit.demo.service;

import com.starterkit.demo.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LockStrategy {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public User lockUser(UUID id, LockModeType lockModeType) {
        return entityManager.find(User.class, id, lockModeType);
    }

    @Transactional
    public User updateUserWithLock(User user, LockModeType lockModeType) {
        if (user.getId() == null) {
            throw new IllegalStateException("Entity does not exist.");
        }
        User managedUser = entityManager.find(User.class, user.getId(), lockModeType);
        entityManager.merge(user);
        return managedUser;
    }
}
