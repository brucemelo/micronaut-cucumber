package com.brucemelo.infrastructure;

import com.brucemelo.domain.Student;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class StudentServiceImplTest {

    @Inject
    private SaveNewStudentImpl studentService;

    @Test
    void save() {
        // Given
        var student = new Student("Jane", "Smith", 22);

        // When
        var savedStudent = studentService.save(student);

        // Then
        assertNotNull(savedStudent);
        assertEquals("Jane", savedStudent.firstName());
        assertEquals("Smith", savedStudent.lastName());
        assertEquals(22, savedStudent.age());
    }
}
