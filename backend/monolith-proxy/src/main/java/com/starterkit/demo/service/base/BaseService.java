/* (C)2024 */
package com.starterkit.demo.service.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.starterkit.demo.aspect.LoggingAndProfilingAspect.TransactionEvent;
import com.starterkit.demo.aspect.LoggingAndProfilingAspect.TransactionType;
import com.starterkit.demo.exception.ResourceNotFoundException;
import com.starterkit.demo.model.base.BaseEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

public abstract class BaseService<E extends BaseEntity<ID>, ID extends Serializable> {

    @Autowired 
    private ApplicationEventPublisher applicationEventPublisher;
    @PersistenceContext 
    private EntityManager entityManager;

    public abstract JpaRepository<E, ID> getRepository();

    @Transactional(readOnly = true)
    public E findById(final ID id) {
        return this.getRepository()
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
    }

    @Transactional(readOnly = true)
    public Optional<E> findByIdOptional(final ID id) {
        return this.getRepository().findById(id);
    }

    @Transactional(readOnly = true)
    public Page<E> findAll(final Pageable pageable) {
        return this.getRepository().findAll(pageable);
    }

    @Transactional
    public E create(final E entity) {
        if (entity.getId() != null) {
            throw new IllegalStateException("Entity already exists.");
        }
        this.getRepository().save(entity);
        this.publishTxLogEvent(TransactionType.CREATE, entity.getId());
        return entity;
    }

    @Transactional
    public E update(final E entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("Entity does not exist.");
        }
        this.getRepository().save(entity);
        this.publishTxLogEvent(TransactionType.UPDATE, entity.getId());
        return entity;
    }

    @Transactional
    public void delete(final ID id) {
        E entity =
                this.getRepository()
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        this.getRepository().delete(entity);
        this.publishTxLogEvent(TransactionType.DELETE, id);
    }

    protected void publishTxLogEvent(TransactionType type, ID entityId) {
        this.applicationEventPublisher.publishEvent(
                new TransactionEvent(type, getEntityName(), entityId.toString()));
    }

    protected String getEntityName() {
        final Class<E> entityModelClass =
                (Class<E>)
                        ((ParameterizedType) this.getClass().getGenericSuperclass())
                                .getActualTypeArguments()[0];
        final jakarta.persistence.Table annotation =
                entityModelClass.getAnnotation(jakarta.persistence.Table.class);
        return annotation.name();
    }

    @Transactional
    public E findByIdWithLock(final ID id, LockModeType lockModeType) {
        return entityManager.find(getEntityClass(), id, lockModeType);
    }

    @Transactional
    public E updateWithLock(final E entity, LockModeType lockModeType) {
        if (entity.getId() == null) {
            throw new IllegalStateException("Entity does not exist.");
        }
        E managedEntity = entityManager.find(getEntityClass(), entity.getId(), lockModeType);
        entityManager.merge(entity);
        this.publishTxLogEvent(TransactionType.UPDATE, entity.getId());
        return managedEntity;
    }

    private Class<E> getEntityClass() {
        return (Class<E>)
                ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
