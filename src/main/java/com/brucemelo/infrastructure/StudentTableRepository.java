package com.brucemelo.infrastructure;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
interface StudentTableRepository extends CrudRepository<StudentTable, Long> {

    List<StudentTable> findByFirstNameContainsOrLastNameContains(String firstName, String lastName);
}
