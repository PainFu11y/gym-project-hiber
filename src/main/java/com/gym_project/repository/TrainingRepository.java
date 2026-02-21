package com.gym_project.repository;

import com.gym_project.entity.Training;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.TrainingType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Training training) {
        entityManager.persist(training);
    }

    @Transactional
    public Training update(Training training) {
        return entityManager.merge(training);
    }

    @Transactional
    public void delete(Training training) {
        entityManager.remove(entityManager.contains(training) ? training : entityManager.merge(training));
    }

    @Transactional(readOnly = true)
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Transactional(readOnly = true)
    public List<Training> findAll() {
        return entityManager.createQuery("SELECT t FROM Training t", Training.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Training> findByTrainee(Trainee trainee) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainee = :trainee", Training.class)
                .setParameter("trainee", trainee)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Training> findByTrainer(Trainer trainer) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainer = :trainer", Training.class)
                .setParameter("trainer", trainer)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Training> findByTrainingType(TrainingType trainingType) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainingType = :type", Training.class)
                .setParameter("type", trainingType)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Training> findByDate(LocalDate date) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainingDate = :date", Training.class)
                .setParameter("date", date)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Training> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return entityManager.createQuery(
                        "SELECT t FROM Training t WHERE t.trainingDate BETWEEN :start AND :end", Training.class)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();
    }
}