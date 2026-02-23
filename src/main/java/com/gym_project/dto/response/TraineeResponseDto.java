package com.gym_project.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
public class TraineeResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private boolean active;

    private LocalDate dateOfBirth;
    private String address;

    private Set<String> trainerUsernames;
}