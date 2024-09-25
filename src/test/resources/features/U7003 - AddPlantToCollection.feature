Feature: U7003 - As Jackie Wei, I want to add plants to my collection when I am not out and about

  @U7003
  Scenario: AC2 - A user attempts to add a plant to collection with valid values
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I am manually adding a plant to my collection
    When I enter the plant name "Rose"
    And I enter the plant species "Rose species"
    And I enter the plant description "This is a cool rose"
    And I enter the uploaded date "12/02/2024"
    And I upload no image
    And I save the plant to my collection
    Then the plant is added to my collection

  @U7003
  Scenario: AC3 - A user attempts to add a plant to collection with invalid values
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I am manually adding a plant to my collection
    When I enter the plant name "Rose #1"
    And I enter the plant species "Rose species #1"
    And I enter the plant description "This is a cool rose"
    And I enter the uploaded date "12/02/2024"
    And I upload no image
    And I save the plant to my collection
    Then the plant is not added to my collection
    And I get the plant name error message "Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>"
    And I get the plant species error message "Scientific name must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>"


  @U7003
  Scenario: AC2 - A user attempts to add a plant to collection with valid values(Coordinates)
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I am manually adding a plant to my collection
    When I enter the plant name "Rose"
    And I enter the plant species "Rose species"
    And I enter the plant description "This is a cool rose"
    And I enter the uploaded date "12/02/2024"
    And I enter valid location
    And I upload no image
    And I save the plant to my collection
    Then the plant is added to my collection

  @U7003
  Scenario: AC3 - A user attempts to add a plant to collection with invalid values(Coordinates)
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I am manually adding a plant to my collection
    When I enter the plant name "Rose #1"
    And I enter the plant species "Rose species #1"
    And I enter the plant description "This is a cool rose"
    And I enter the uploaded date "12/02/2024"
    And I enter invalid location
    And I upload no image
    And I save the plant to my collection
    Then the plant is not added to my collection
    And I get the plant name error message "Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>"
    And I get the plant species error message "Scientific name must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>"
    And I get the location error message "Invalid Location"