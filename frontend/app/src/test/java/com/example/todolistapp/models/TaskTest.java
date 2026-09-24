package com.example.todolistapp.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void testTaskGettersAndSetters() {
        Task task = new Task();
        task.setId("t1");
        task.setTitle("Fitness");
        task.setDescription("Exercise and gym");
        task.setTimeRange("6:00 - 7:30");
        task.setCategory("Sport");
        task.setDate("14 Sept");
        task.setCompleted(true);
        task.setUserId("u123");

        assertEquals("t1", task.getId());
        assertEquals("Fitness", task.getTitle());
        assertEquals("Exercise and gym", task.getDescription());
        assertEquals("6:00 - 7:30", task.getTimeRange());
        assertEquals("Sport", task.getCategory());
        assertEquals("14 Sept", task.getDate());
        assertTrue(task.isCompleted());
        assertEquals("u123", task.getUserId());
    }

    @Test
    public void testTaskEqualsAndHashCode() {
        Task task1 = new Task("t1", "Fitness", "Exercise", "6:00 - 7:30", "Sport", "14 Sept", true, "u1");
        Task task2 = new Task("t1", "Fitness", "Exercise", "6:00 - 7:30", "Sport", "14 Sept", true, "u1");

        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }
}
