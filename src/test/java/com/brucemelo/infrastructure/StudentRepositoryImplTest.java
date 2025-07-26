package com.brucemelo.infrastructure;

import com.brucemelo.domain.Student;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class StudentRepositoryImplTest {

    @Inject
    private StudentRepositoryImpl studentRepository;

    @Inject
    private StudentTableRepository studentTableRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data
        studentTableRepository.deleteAll();

        // Save some test students
        studentRepository.save(new Student("John", "Doe", 20));
        studentRepository.save(new Student("Jane", "Doe", 22));
        studentRepository.save(new Student("John", "Smith", 25));
    }
}
