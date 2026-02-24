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

    Trainee update(Trainee trainee);

    void delete(Trainee trainee);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();

    List<Trainee> findByAddress(String address);

    Optional<Trainee> findByUsernameAndPassword(String username, String password);

    Optional<Trainee> findByUsername(String username);

    boolean existsByUsername(String username);

    List<String> findUsernamesStartingWith(String prefix);

    void activate(String username);

    void deactivate(String username);

    void deleteByUsername(String username);

    void changePassword(String username, String newPassword);

}