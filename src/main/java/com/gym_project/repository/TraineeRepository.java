package com.gym_project.repository;

import com.gym_project.entity.Trainee;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository {

    void save(Trainee trainee);

    public Trainee update(Trainee trainee);

    void delete(Trainee trainee);

    public Optional<Trainee> findById(Long id);

    public List<Trainee> findAll();

    public List<Trainee> findByAddress(String address);

    public Optional<Trainee> findByUsernameAndPassword(String username, String password);

    public Optional<Trainee> findByUsername(String username);

    public void changePassword(String username, String newPassword);

    public void activate(String username);

    public void deactivate(String username);

    public void deleteByUsername(String username);

}