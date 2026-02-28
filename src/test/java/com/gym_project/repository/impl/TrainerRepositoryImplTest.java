package com.gym_project.repository.impl;

import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerRepositoryImplTest {

    private EntityManager entityManager;
    private TrainerRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        entityManager = mock(EntityManager.class);
        repository = new TrainerRepositoryImpl();

        var field = TrainerRepositoryImpl.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(repository, entityManager);
    }

    @Test
    void save_shouldCallPersist() {
        Trainer trainer = new Trainer();
        repository.save(trainer);
        verify(entityManager).persist(trainer);
    }

    @Test
    void update_shouldCallMerge() {
        Trainer trainer = new Trainer();
        when(entityManager.merge(trainer)).thenReturn(trainer);
        Trainer updated = repository.update(trainer);
        assertEquals(trainer, updated);
        verify(entityManager).merge(trainer);
    }

    @Test
    void delete_shouldCallRemoveWithMergeWhenNotContained() {
        Trainer trainer = new Trainer();
        when(entityManager.contains(trainer)).thenReturn(false);
        when(entityManager.merge(trainer)).thenReturn(trainer);

        repository.delete(trainer);

        verify(entityManager).merge(trainer);
        verify(entityManager).remove(trainer);
    }

    @Test
    void delete_shouldCallRemoveDirectlyWhenContained() {
        Trainer trainer = new Trainer();
        when(entityManager.contains(trainer)).thenReturn(true);

        repository.delete(trainer);

        verify(entityManager, never()).merge(trainer);
        verify(entityManager).remove(trainer);
    }

    @Test
    void findById_shouldReturnOptional() {
        Trainer trainer = new Trainer();
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        Optional<Trainer> result = repository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    void findAll_shouldReturnList() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = repository.findAll();
        assertEquals(1, result.size());
        verify(query).getResultList();
    }

    @Test
    void findBySpecialization_shouldReturnList() {
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.specialization = :spec", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("spec", "Yoga")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = repository.findBySpecialization("Yoga");
        assertEquals(1, result.size());
        verify(query).getResultList();
    }

    @Test
    void changePassword_shouldCallMerge() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john");

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "john")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        repository.changePassword("john", "newpass");

        assertEquals("newpass", trainer.getPassword());
        verify(entityManager).merge(trainer);
    }

    @Test
    void activate_shouldSetActiveTrue() {
        Trainer trainer = new Trainer();

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "john")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        repository.activate("john");

        assertTrue(trainer.isActive());
        verify(entityManager).merge(trainer);
    }


    @Test
    void deactivate_shouldSetActiveFalse() {
        Trainer trainer = new Trainer();
        trainer.setActive(true);

        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class))
                .thenReturn(query);
        when(query.setParameter("username", "john")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        repository.deactivate("john");

        assertFalse(trainer.isActive());
        verify(entityManager).merge(trainer);
    }

    @Test
    void findUsernamesStartingWith_shouldReturnList() {
        TypedQuery<String> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT t.username FROM Trainer t WHERE t.username LIKE :pattern", String.class))
                .thenReturn(query);
        when(query.setParameter("pattern", "jo%")).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of("john"));

        List<String> result = repository.findUsernamesStartingWith("jo");
        assertEquals(1, result.size());
    }

    @Test
    void findTrainingsByTrainerAndFilter_shouldReturnList() {
        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Training()));

        TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
        filter.setFromDate(LocalDate.now());
        filter.setToDate(LocalDate.now());

        List<Training> result = repository.findTrainingsByTrainerAndFilter("john", filter);
        assertEquals(1, result.size());
    }
}