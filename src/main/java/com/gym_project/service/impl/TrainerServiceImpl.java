package com.gym_project.service.impl;

import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.filter.TrainerTrainingFilterDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.TraineeTrainersUpdateDto;
import com.gym_project.dto.update.TrainerUpdateDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Trainer;
import com.gym_project.entity.Training;
import com.gym_project.mapper.TrainerMapper;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.repository.TrainerRepository;
import com.gym_project.service.TrainerService;
import com.gym_project.utils.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public TrainerResponseDto create(TrainerCreateDto dto) {

        validateCreate(dto);

        String base = dto.getFirstName() + "." + dto.getLastName();
        List<String> existingUsernames = trainerRepository.findUsernamesStartingWith(base);
        String generatedUsername = UsernameGenerator.generate(dto.getFirstName(), dto.getLastName(), existingUsernames);

        Trainer trainer = TrainerMapper.toEntity(dto);
        trainer.setUsername(generatedUsername);

        trainerRepository.save(trainer);

        return TrainerMapper.toDto(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerResponseDto getByUsername(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        return TrainerMapper.toDto(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponseDto> getAll() {
        return trainerRepository.findAll().stream()
                .map(TrainerMapper::toDto)
                .toList();
    }

    @Override
    public TrainerResponseDto update(String username, TrainerUpdateDto dto) {

        validateUpdate(dto);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        TrainerMapper.updateEntity(trainer, dto);

        return TrainerMapper.toDto(trainer);
    }

    @Override
    public void deleteByUsername(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        trainerRepository.delete(trainer);
    }

    @Override
    public TrainerResponseDto activate(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        trainer.setActive(true);
        return TrainerMapper.toDto(trainer);
    }

    @Override
    public TrainerResponseDto deactivate(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        trainer.setActive(false);
        return TrainerMapper.toDto(trainer);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        trainerRepository.changePassword(username, newPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponseDto> getTrainings(String trainerUsername, TrainerTrainingFilterDto filter) {
        List<Training> trainings = trainerRepository.findTrainingsByTrainerAndFilter(trainerUsername, filter);
        return trainings.stream()
                .map(TrainingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponseDto> getUnassignedTrainersByTraineeUsername(String traineeUsername) {

        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Trainee username must not be blank");
        }

        List<Trainer> trainers =
                trainerRepository.findUnassignedTrainersByTraineeUsername(traineeUsername);

        return trainers.stream()
                .map(TrainerMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<TrainerResponseDto> updateTraineeTrainers(String traineeUsername,
                                                          TraineeTrainersUpdateDto dto) {

        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Trainee username must not be blank");
        }

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        if (trainee.getTrainers() != null) {
            for (Trainer trainer : trainee.getTrainers()) {
                trainer.getTrainees().remove(trainee);
            }
            trainee.getTrainers().clear();
        }

        List<Trainer> newTrainers = dto.getTrainerUsernames().stream()
                .map(username -> trainerRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Trainer not found: " + username)))
                .toList();

        for (Trainer trainer : newTrainers) {
            trainer.getTrainees().add(trainee);
            trainee.getTrainers().add(trainer);
        }

        return newTrainers.stream()
                .map(TrainerMapper::toDto)
                .toList();
    }

    @Override
    public TrainerResponseDto validateCredentials(String username, String password) {

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        if (!trainer.isActive()) {
            throw new RuntimeException("Trainer is deactivated");
        }

        if (!trainer.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return TrainerMapper.toDto(trainer) ;
    }

    private void validateCreate(TrainerCreateDto dto) {
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (dto.getSpecialization() == null || dto.getSpecialization().isBlank()) {
            throw new IllegalArgumentException("Specialization cannot be empty");
        }
    }

    private void validateUpdate(TrainerUpdateDto dto) {
        if (dto.getFirstName() != null && dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (dto.getLastName() != null && dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
        if (dto.getSpecialization() != null && dto.getSpecialization().isBlank()) {
            throw new IllegalArgumentException("Specialization cannot be blank");
        }
    }

}