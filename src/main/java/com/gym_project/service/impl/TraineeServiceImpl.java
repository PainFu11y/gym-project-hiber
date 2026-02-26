package com.gym_project.service.impl;


import com.gym_project.dto.create.TraineeCreateDto;
import com.gym_project.dto.filter.TraineeTrainingFilterDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.response.TrainingResponseDto;
import com.gym_project.dto.update.TraineeUpdateDto;
import com.gym_project.entity.Trainee;
import com.gym_project.entity.Training;
import com.gym_project.mapper.TraineeMapper;
import com.gym_project.mapper.TrainingMapper;
import com.gym_project.repository.TraineeRepository;
import com.gym_project.service.TraineeService;

import com.gym_project.utils.PasswordGenerator;
import com.gym_project.utils.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;

    @Override
    public TraineeResponseDto create(TraineeCreateDto dto) {

        validate(dto);

        String base = dto.getFirstName() + "." + dto.getLastName();

        List<String> existingUsernames =
                traineeRepository.findUsernamesStartingWith(base);

        String generatedUsername =
                UsernameGenerator.generate(dto.getFirstName(), dto.getLastName(), existingUsernames);

        Trainee trainee = TraineeMapper.toEntity(dto);
        trainee.setUsername(generatedUsername);
        trainee.setPassword(PasswordGenerator.generate());

        traineeRepository.save(trainee);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponseDto getByUsername(String username) {

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        return TraineeMapper.toDto(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeResponseDto> getAll() {

        return traineeRepository.findAll()
                .stream()
                .map(TraineeMapper::toDto)
                .toList();
    }

    @Override
    public TraineeResponseDto update(String username, TraineeUpdateDto dto) {

        validateUpdate(dto);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        TraineeMapper.updateEntity(trainee, dto);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    public void deleteByUsername(String username) {

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        traineeRepository.delete(trainee);
    }

    @Override
    public TraineeResponseDto activate(String username) {

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        trainee.setActive(true);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    public TraineeResponseDto deactivate(String username) {

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        trainee.setActive(false);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        traineeRepository.changePassword(username, newPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponseDto> getTrainings(String traineeUsername, TraineeTrainingFilterDto filter) {
        List<Training> trainings = traineeRepository.findTrainingsByTraineeAndFilter(traineeUsername, filter);
        return trainings.stream()
                .map(TrainingMapper::toDto)
                .toList();
    }

    private void validateUpdate(TraineeUpdateDto dto) {

        if (dto.getFirstName() != null && dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (dto.getLastName() != null && dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }

        if (dto.getDateOfBirth() != null &&
                dto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must be in the past");
        }

        if (dto.getAddress() != null && dto.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address cannot be blank");
        }
    }

    private void validate(TraineeCreateDto dto) {

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }

        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        if (dto.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }

        if (dto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must be in the past");
        }

        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
    }
}