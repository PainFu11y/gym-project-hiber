package com.gym_project.service.impl;

import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.update.TrainerUpdateDto;
import com.gym_project.entity.Trainer;
import com.gym_project.mapper.TrainerMapper;
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