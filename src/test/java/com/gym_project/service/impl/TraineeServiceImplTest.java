package com.gym_project.service.impl;

import com.gym_project.dto.create.TraineeCreateDto;
import com.gym_project.dto.response.TraineeResponseDto;
import com.gym_project.dto.update.TraineeUpdateDto;
import com.gym_project.entity.Trainee;
import com.gym_project.repository.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    private TraineeRepository traineeRepository;
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        traineeService = new TraineeServiceImpl(traineeRepository);
    }

    @Test
    void update_shouldChangeFields() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setFirstName("Old");
        trainee.setLastName("Name");

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAddress("Yerevan");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));

        TraineeResponseDto response = traineeService.update("john.doe", dto);

        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals("Yerevan", trainee.getAddress());
        assertEquals(LocalDate.of(2000, 1, 1), trainee.getDateOfBirth());
    }

    @Test
    void deleteByUsername_shouldCallDelete() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.deleteByUsername("john.doe");

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void activate_shouldSetActiveTrue() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setActive(false);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeResponseDto dto = traineeService.activate("john.doe");

        assertTrue(trainee.isActive());
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setActive(true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeResponseDto dto = traineeService.deactivate("john.doe");

        assertFalse(trainee.isActive());
    }

    @Test
    void changePassword_shouldCallRepository() {
        String newPassword = "newPass123";
        traineeService.changePassword("john.doe", newPassword);
        verify(traineeRepository).changePassword("john.doe", newPassword);
    }

    @Test
    void changePassword_shouldThrowOnBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                traineeService.changePassword("john.doe", ""));
    }

    @Test
    void validateCredentials_shouldReturnDtoWhenCorrect() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("pass");
        trainee.setActive(true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeResponseDto dto = traineeService.validateCredentials("john.doe", "pass");

        assertEquals("john.doe", dto.getUsername());
    }

    @Test
    void validateCredentials_shouldThrowWhenInactive() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("pass");
        trainee.setActive(false);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThrows(RuntimeException.class, () ->
                traineeService.validateCredentials("john.doe", "pass"));
    }

    @Test
    void validateCredentials_shouldThrowWhenPasswordIncorrect() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("pass");
        trainee.setActive(true);

        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThrows(RuntimeException.class, () ->
                traineeService.validateCredentials("john.doe", "wrong"));
    }

    @Test
    void create_shouldThrowWhenFieldsInvalid() {
        TraineeCreateDto dto = new TraineeCreateDto();
        assertThrows(IllegalArgumentException.class, () ->
                traineeService.create(dto));
    }

    @Test
    void update_shouldThrowWhenFieldsInvalid() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setFirstName("");
        assertThrows(IllegalArgumentException.class, () ->
                traineeService.update("john.doe", dto));
    }
}