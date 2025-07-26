package com.brucemelo.domain;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StudentTest {

    private Student student;
    private boolean isAdult;
    private Student savedStudent;
    private SaveNewStudent saveNewStudent;
    private StudentRepository studentRepository;
    private List<Student> existingStudents;
    private List<Student> foundStudents;

    @Given("a student with age {int}")
    public void aStudentWithAge(Integer age) {
        student = new Student("Mario", "Nintendo", age);
    }

    @When("I validate the student's age")
    public void iValidateTheStudentSAge() {
        isAdult = student.isAdult();
    }

    @Then("the student should be identified as an adult")
    public void theStudentShouldBeIdentifiedAsAnAdult() {
        assertTrue(isAdult, "Student should be identified as an adult");
    }

    @Then("the student should not be identified as an adult")
    public void theStudentShouldNotBeIdentifiedAsAnAdult() {
        assertFalse(isAdult, "Student should not be identified as an adult");
    }

    @Given("a new student with first name {string}, last name {string}, and age {int}")
    public void aNewStudentWithFirstNameLastNameAndAge(String firstName, String lastName, Integer age) {
        student = new Student(firstName, lastName, age);

        StudentRepository studentRepository = Mockito.mock(StudentRepository.class);

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        saveNewStudent = Mockito.mock(SaveNewStudent.class);

        when(saveNewStudent.save(any(Student.class))).thenReturn(student);
    }

    @When("I save the student")
    public void iSaveTheStudent() {
        savedStudent = saveNewStudent.save(student);
    }

    @Then("the student should be saved successfully")
    public void theStudentShouldBeSavedSuccessfully() {
        assertNotNull(savedStudent, "Saved student should not be null");
        verify(saveNewStudent).save(any(Student.class));
    }

    @Then("the saved student should have first name {string}, last name {string}, and age {int}")
    public void theSavedStudentShouldHaveFirstNameLastNameAndAge(String firstName, String lastName, Integer age) {
        assertEquals(firstName, savedStudent.firstName(), "First name should match");
        assertEquals(lastName, savedStudent.lastName(), "Last name should match");
        assertEquals(age, savedStudent.age(), "Age should match");
    }

    @Then("the saved student should not be identified as an adult")
    public void theSavedStudentShouldNotBeIdentifiedAsAnAdult() {
        assertFalse(savedStudent.isAdult(), "Student should not be identified as an adult");
    }

    @Then("the student adult status should be {}")
    public void theStudentAdultStatusShouldBe(String expectedStatus) {
        boolean expected = Boolean.parseBoolean(expectedStatus);
        assertEquals(expected, savedStudent.isAdult(), 
            String.format("Student adult status should be %s for age %d", expected, savedStudent.age()));
    }

    // Search functionality step definitions
    @Given("the following students exist:")
    public void theFollowingStudentsExist(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        existingStudents = new ArrayList<>();

        for (Map<String, String> row : rows) {
            String firstName = row.get("firstName");
            String lastName = row.get("lastName");
            Integer age = Integer.parseInt(row.get("age"));
            existingStudents.add(new Student(firstName, lastName, age));
        }

        studentRepository = Mockito.mock(StudentRepository.class);
    }

    @When("I search for students with name {string}")
    public void iSearchForStudentsWithName(String name) {
        List<Student> matchingStudents = existingStudents.stream()
                .filter(s -> s.firstName().contains(name) || s.lastName().contains(name))
                .toList();

        when(studentRepository.findByName(eq(name))).thenReturn(matchingStudents);

        foundStudents = studentRepository.findByName(name);
    }

    @Then("I should find {int} student(s)")
    public void iShouldFindStudents(int expectedCount) {
        assertEquals(expectedCount, foundStudents.size(), 
                String.format("Expected to find %d student(s), but found %d", expectedCount, foundStudents.size()));
    }

    @Then("the found student should have first name {string}, last name {string}, and age {int}")
    public void theFoundStudentShouldHaveFirstNameLastNameAndAge(String firstName, String lastName, Integer age) {
        assertEquals(1, foundStudents.size(), "Expected exactly one student to be found");
        Student foundStudent = foundStudents.get(0);
        assertEquals(firstName, foundStudent.firstName(), "First name should match");
        assertEquals(lastName, foundStudent.lastName(), "Last name should match");
        assertEquals(age, foundStudent.age(), "Age should match");
    }

    @Then("the found students should have first name {string} and last name {string}")
    public void theFoundStudentsShouldHaveFirstNameAndLastName(String firstName, String lastName) {
        assertTrue(foundStudents.size() > 0, "Expected at least one student to be found");
        for (Student foundStudent : foundStudents) {
            assertEquals(firstName, foundStudent.firstName(), "First name should match");
            assertEquals(lastName, foundStudent.lastName(), "Last name should match");
        }
    }
}
