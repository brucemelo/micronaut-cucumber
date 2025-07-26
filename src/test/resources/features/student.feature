Feature: Student Management
  As a system administrator
  I want to manage student information
  So that I can maintain accurate records and control access

  # Age Validation Scenarios
  Scenario: Valid adult student
    Given a student with age 20
    When I validate the student's age
    Then the student should be identified as an adult

  Scenario: Invalid minor student
    Given a student with age 17
    When I validate the student's age
    Then the student should not be identified as an adult

  # Save New Student Scenarios
  Scenario: Save a new student successfully
    Given a new student with first name "Mario", last name "Nintendo", and age 25
    When I save the student
    Then the student should be saved successfully
    And the saved student should have first name "Mario", last name "Nintendo", and age 25

  Scenario: Save a new student with validation
    Given a new student with first name "Link", last name "Hyrule", and age 17
    When I save the student
    Then the student should be saved successfully
    And the saved student should not be identified as an adult

  Scenario Outline: Save students with different ages
    Given a new student with first name "<firstName>", last name "<lastName>", and age <age>
    When I save the student
    Then the student should be saved successfully
    And the saved student should have first name "<firstName>", last name "<lastName>", and age <age>
    And the student adult status should be <isAdult>

    Examples:
      | firstName | lastName   | age | isAdult |
      | Sonic     | Hedgehog   | 16  | false   |
      | Master    | Chief      | 41  | true    |
      | Kratos    | Sparta     | 30  | true    |
      | Zelda     | Hyrule     | 17  | false   |


  # Search Student by Single Name Parameter Scenarios
  Scenario: Find a student by partial name in first name
    Given the following students exist:
      | firstName | lastName   | age |
      | Mario     | Nintendo   | 25  |
      | Luigi     | Nintendo   | 23  |
      | Peach     | Mushroom   | 22  |
    When I search for students with name "Mar"
    Then I should find 1 student
    And the found student should have first name "Mario", last name "Nintendo", and age 25

  Scenario: Find a student by partial name in last name
    Given the following students exist:
      | firstName | lastName   | age |
      | Mario     | Nintendo   | 25  |
      | Luigi     | Nintendo   | 23  |
      | Peach     | Mushroom   | 22  |
    When I search for students with name "Mush"
    Then I should find 1 student
    And the found student should have first name "Peach", last name "Mushroom", and age 22

  Scenario: Find multiple students with the same partial name
    Given the following students exist:
      | firstName | lastName   | age |
      | Mario     | Nintendo   | 25  |
      | Luigi     | Nintendo   | 23  |
      | Peach     | Mushroom   | 22  |
    When I search for students with name "Nin"
    Then I should find 2 students

  Scenario: No students found with the given partial name
    Given the following students exist:
      | firstName | lastName   | age |
      | Mario     | Nintendo   | 25  |
      | Luigi     | Nintendo   | 23  |
    When I search for students with name "Bowser"
    Then I should find 0 students
