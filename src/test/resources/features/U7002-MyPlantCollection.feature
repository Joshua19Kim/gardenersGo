Feature: U7002 As Jackie Wei, I want to catalog identified plants into my personal collection, so that I can keep track of the plants I've discovered.

  @U7002
  Scenario: AC1 - A user attempts to view their plant collection
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have added the plant species with name "Berries" and count "2"
    And I have added the plant species with name "Apples" and count "4"
    When I go to my collection page
    Then I should be able to see all the identified plant species