@U7010
Feature: As Jackie Wei, I want to share my progress with my followers. I want to display badges for cataloging all the plants I encounter.

  @AC1
  Scenario: AC1 - View badges on profile
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have earned a badge
    When I view my profile
    Then The earned badges should be prominently displayed on my profile, including badge images and names.


  @AC3
  Scenario: AC3 - Only recent badges shown
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have more than five badges
    When I view my profile
    Then I should see the five most recently acquired badges

  @AC4
  Scenario: AC4 - View Locked and Unlocked badges
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have more than five badges
    When I view all badges
    Then I should be shown all badges sectioned into those that are achieved and those that are not

  @AC5
  Scenario: AC5 - No badges unlocked
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have no badges
    When I view my profile
    Then I should see nothing in the badges section
