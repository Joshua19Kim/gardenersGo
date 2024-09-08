Feature: U21 - As Inaya, I want to be able to browse gardens by different user-specified tags so that I can browse for gardens that match my interests.

  @U21
  Scenario: # AC5 - valid
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I am adding a valid tag
    When I confirm the tag
    Then the tag is added to the garden tags
  @U21
  Scenario: # AC6 - invalid character
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I add an invalid tag
    When I confirm the tag
    Then the tag is not added to the list of user-defined tags
  @U21
  Scenario: # AC7 - invalid length
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I add an invalid tag
    When I confirm the tag
    Then the tag is not added the garden tags
    And an error message displays "A tag cannot exceed 25 characters <br/>"