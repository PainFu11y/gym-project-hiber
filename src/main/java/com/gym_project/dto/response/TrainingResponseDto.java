package com.gym_project.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingResponseDto {
    private Long id;

    private String traineeUsername;
    private String trainerUsername;
    private String trainingTypeName;

    private String trainingName;
    private LocalDate trainingDate;
    private Integer trainingDuration;
}