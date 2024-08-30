Feature: U7002 - As Jackie Wei, I want to catalog identified plants into my personal collection, so that I can keep track of the plants I've discovered.


  @U7002
  Scenario: #AC3 - Save an identified plant
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have an image of a plant
    When the app identifies the plant image
    And I click the add button
    And I input a valid name and description
    And I click the save plant button
    Then I can see my plant in the collection

