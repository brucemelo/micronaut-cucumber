package com.brucemelo.infrastructure;

import com.brucemelo.domain.Student;
import com.brucemelo.domain.StudentRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentTableRepository studentTableRepository;

    public StudentRepositoryImpl(StudentTableRepository studentTableRepository) {
        this.studentTableRepository = studentTableRepository;
    }

    @Override
    public Student save(Student student) {
        var studentTable = StudentTableBuilder.builder()
                .firstName(student.firstName())
                .lastName(student.lastName())
                .age(student.age())
                .build();

        var savedStudentTable = studentTableRepository.save(studentTable);

        return new Student(
            savedStudentTable.firstName(),
            savedStudentTable.lastName(),
            savedStudentTable.age()
        );
    }

    @Override
    public List<Student> findByName(String name) {
        List<StudentTable> studentTables = studentTableRepository.findByFirstNameContainsOrLastNameContains(name, name);

        return studentTables.stream()
                .map(st -> new Student(st.firstName(), st.lastName(), st.age()))
                .collect(Collectors.toList());
    }
}
