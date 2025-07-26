package com.brucemelo.domain;

import java.util.List;

public interface StudentRepository {

    Student save(Student student);

    List<Student> findByName(String name);

}
