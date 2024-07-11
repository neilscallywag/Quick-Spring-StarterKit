/* (C)2024 */
package com.starterkit.demo.model.base;

import java.io.Serializable;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntity<T extends Serializable> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
