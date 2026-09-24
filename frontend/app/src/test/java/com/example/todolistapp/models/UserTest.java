package com.example.todolistapp.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testUserGettersAndSetters() {
        User user = new User();
        user.setId("u123");
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("secret123");
        user.setRole("admin");

        assertEquals("u123", user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertEquals("admin", user.getRole());
        assertTrue(user.isAdmin());
    }

    @Test
    public void testUserIsAdminCheck() {
        User regularUser = new User("1", "alice", "alice@example.com", "pass", "user");
        assertFalse(regularUser.isAdmin());

        User adminUser = new User("2", "bob", "bob@example.com", "pass", "ADMIN");
        assertTrue(adminUser.isAdmin());
    }

    @Test
    public void testUserEqualsAndHashCode() {
        User user1 = new User("1", "alice", "alice@example.com", "pass", "user");
        User user2 = new User("1", "alice", "alice@example.com", "pass", "user");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
