package com.brucemelo.infrastructure;

import com.brucemelo.domain.SaveNewStudent;
import com.brucemelo.domain.Student;
import com.brucemelo.domain.StudentRepository;
import jakarta.inject.Singleton;

@Singleton
public class SaveNewStudentImpl implements SaveNewStudent {
    
    private final StudentRepository studentRepository;
    
    public SaveNewStudentImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }
}