package com.gym_project.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private boolean active;

    private LocalDate dateOfBirth;
    private String address;
}