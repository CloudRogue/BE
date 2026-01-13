package org.example.auth.domain;

public enum UserRole {
    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String authority() {
        return authority;
    }
}
