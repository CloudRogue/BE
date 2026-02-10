package org.example.auth.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StaticUserUlidStore {
    private final Map<String, String> ulidByUsername = Map.of(
            "user",  "01JABCDE1234567890ABCDEFG1",
            "userS", "01JABCDE1234567890ABCDEFG2",
            "userA", "01JABCDE1234567890ABCDEFG3"
    );

    public String getUlid(String username) {
        String ulid = ulidByUsername.get(username);
        if (ulid == null) {
            throw new IllegalArgumentException("No ULID mapping for username=" + username);
        }
        return ulid;
    }
}


