package com.peatroxd.bulletinboardproject.security;

public enum Role {
    USER,
    ADMIN,
    VIEWER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
