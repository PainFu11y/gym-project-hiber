package com.gym_project.service;

import com.gym_project.dto.create.TrainerCreateDto;
import com.gym_project.dto.response.TrainerResponseDto;
import com.gym_project.dto.update.TrainerUpdateDto;

import java.util.List;

public interface TrainerService {

    TrainerResponseDto create(TrainerCreateDto dto);

    TrainerResponseDto getByUsername(String username);

    List<TrainerResponseDto> getAll();

    TrainerResponseDto update(String username, TrainerUpdateDto dto);

    void deleteByUsername(String username);

    TrainerResponseDto activate(String username);

    TrainerResponseDto deactivate(String username);
}