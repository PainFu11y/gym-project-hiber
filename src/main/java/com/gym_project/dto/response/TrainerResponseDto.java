package com.gym_project.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TrainerResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private boolean active;

    private String specialization;

    private Set<String> traineeUsernames;
}