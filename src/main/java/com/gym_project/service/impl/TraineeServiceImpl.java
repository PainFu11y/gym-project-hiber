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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;

    @Override
    public TraineeResponseDto create(TraineeCreateDto dto) {
        log.info("Creating trainee: {} {}", dto.getFirstName(), dto.getLastName());

        validate(dto);

        String base = dto.getFirstName() + "." + dto.getLastName();
        List<String> existingUsernames = traineeRepository.findUsernamesStartingWith(base);
        String generatedUsername =
                UsernameGenerator.generate(dto.getFirstName(), dto.getLastName(), existingUsernames);

        Trainee trainee = TraineeMapper.toEntity(dto);
        trainee.setUsername(generatedUsername);
        trainee.setPassword(PasswordGenerator.generate());

        traineeRepository.save(trainee);

        log.info("Trainee created successfully: {}", generatedUsername);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name")
    public TraineeResponseDto getByUsername(String username) {

        log.debug("Fetching trainee by username: {}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        return TraineeMapper.toDto(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('TRAINEE', 'TRAINER')")
    public List<TraineeResponseDto> getAll() {
        log.debug("Fetching all trainees");
        return traineeRepository.findAll()
                .stream()
                .map(TraineeMapper::toDto)
                .toList();
    }

    @Override
    @PreAuthorize("#username == authentication.name")
    public TraineeResponseDto update(String username, TraineeUpdateDto dto) {

        log.info("Updating trainee: {}", username);

        validateUpdate(dto);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found for update: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        TraineeMapper.updateEntity(trainee, dto);

        log.info("Trainee updated successfully: {}", username);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    @PreAuthorize("#username == authentication.name")
    public void deleteByUsername(String username) {

        log.info("Deleting trainee: {}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found for deletion: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        traineeRepository.delete(trainee);

        log.info("Trainee deleted: {}", username);

    }

    @Override
    @PreAuthorize("#username == authentication.name or hasRole('TRAINER')")
    public TraineeResponseDto activate(String username) {
        log.info("Activating trainee: {}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        trainee.setActive(true);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    @PreAuthorize("#username == authentication.name or hasRole('TRAINER')")
    public TraineeResponseDto deactivate(String username) {
        log.info("Deactivating trainee: {}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        trainee.setActive(false);

        return TraineeMapper.toDto(trainee);
    }

    @Override
    @PreAuthorize("#username == authentication.name")
    public void changePassword(String username, String newPassword) {
        log.info("Changing password for trainee: {}", username);

        if (newPassword == null || newPassword.isBlank()) {
            log.warn("Attempt to set blank password for trainee: {}", username);
            throw new IllegalArgumentException("Password cannot be blank");
        }
        traineeRepository.changePassword(username, newPassword);

        log.info("Password changed successfully for trainee: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name or hasRole('TRAINEE')")
    public List<TrainingResponseDto> getTrainings(String traineeUsername, TraineeTrainingFilterDto filter) {
        List<Training> trainings = traineeRepository.findTrainingsByTraineeAndFilter(traineeUsername, filter);
        return trainings.stream()
                .map(TrainingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponseDto validateCredentials(String username, String password) {

        log.debug("Validating credentials for trainee: {}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Login failed - trainee not found: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        if (!trainee.isActive()) {
            log.warn("Login attempt for deactivated trainee: {}", username);
            throw new RuntimeException("Trainee is deactivated");
        }


        if (!trainee.getPassword().equals(password)) {
            log.warn("Invalid password for trainee: {}", username);
            throw new RuntimeException("Invalid password");
        }

        log.info("Trainee successfully authenticated: {}", username);

        return TraineeMapper.toDto(trainee);
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