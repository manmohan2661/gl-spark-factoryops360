package com.factoryops.common.enums;

/**
 * Generic status flag reusable wherever a service needs a simple
 * active/inactive style state without defining its own enum.
 */
public enum Status {
    ACTIVE,
    INACTIVE,
    PENDING,
    ARCHIVED
}
