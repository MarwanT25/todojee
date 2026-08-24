package com.example.todo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.beans.Transient;
public class TodoTest {
    
    @Test
    void todoGettersAndSettersWork(){
        Todo todo = new Todo();
        todo.setTitle("DevopsPrac");
        assertFalse(todo.isCompleted());
        assertEquals("DevopsPrac", todo.getTitle());
    }
}
