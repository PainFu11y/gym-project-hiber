package com.gym_project.service.impl;

import com.gym_project.dto.create.TrainingCreateDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.entity.*;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.*;
import com.gym_project.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public TrainingResponseDto create(TrainingCreateDto dto) {

        validate(dto);

        Trainee trainee = traineeRepository.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        Trainer trainer = trainerRepository.findByUsername(dto.getTrainerUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        TrainingType trainingType = trainingTypeRepository.findById(dto.getTrainingTypeId())
                .orElseThrow(() -> new RuntimeException("Training type not found"));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName(dto.getTrainingName());
        training.setTrainingDate(dto.getTrainingDate());
        training.setTrainingDuration(dto.getTrainingDuration());

        trainingRepository.save(training);

        return TrainingMapper.toDto(training);
    }

    private void validate(TrainingCreateDto dto) {

        if (dto.getTraineeUsername() == null || dto.getTraineeUsername().isBlank()) {
            throw new IllegalArgumentException("Trainee username is required");
        }

        if (dto.getTrainerUsername() == null || dto.getTrainerUsername().isBlank()) {
            throw new IllegalArgumentException("Trainer username is required");
        }

        if (dto.getTrainingTypeId() == null) {
            throw new IllegalArgumentException("Training type is required");
        }

        if (dto.getTrainingName() == null || dto.getTrainingName().isBlank()) {
            throw new IllegalArgumentException("Training name is required");
        }

        if (dto.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }

        if (dto.getTrainingDuration() == null || dto.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }
}