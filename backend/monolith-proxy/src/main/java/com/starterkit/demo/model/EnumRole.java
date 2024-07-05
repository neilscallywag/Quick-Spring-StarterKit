package com.starterkit.demo.model;

/**
 * Enum representing the roles of users in the system.
 */
public enum EnumRole {
    /**
     * Role representing a regular user.
     */
    ROLE_USER,

    /**
     * Role representing an officer.
     * Officers have more privileges than regular users.
     */
    ROLE_OFFICER,

    /**
     * Role representing a manager.
     * Managers have the most privileges.
     */
    ROLE_MANAGER
}
