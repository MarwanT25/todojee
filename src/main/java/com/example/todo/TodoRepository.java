package com.example.todo;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class TodoRepository {

    @PersistenceContext(unitName = "todoPU")
    private EntityManager em;

    public List<Todo> findAll() {
        return em.createQuery("SELECT t FROM Todo t ORDER BY t.id", Todo.class).getResultList();
    }

    public Todo findById(Long id) {
        return em.find(Todo.class, id);
    }

    public Todo create(Todo todo) {
        em.persist(todo);
        return todo;
    }

    public Todo update(Long id, Todo changes) {
        Todo managed = em.find(Todo.class, id);
        if (managed == null) {
            return null;
        }

        if (changes.getTitle() != null) {
            managed.setTitle(changes.getTitle());
        }
        managed.setCompleted(changes.isCompleted());

        return managed;
    }

    public boolean delete(Long id) {
        Todo managed = em.find(Todo.class, id);
        if (managed == null) {
            return false;
        }

        em.remove(managed);
        return true;
    }
}