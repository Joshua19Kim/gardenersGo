Feature: As Kaia, I want to make sure that users who repeatedly trying to add inappropriate tags are
  prevented to use the app for one week so that they can reflect on their behaviour.

  @U23
  Scenario: #AC1
    Given I am logged in with email "b@gmail.com" and password "Password1!"
    And I have added inappropriate words four times and am adding one more time
    When I try to submit the tag
    Then the system shows the warning message
    And send an email.

  @U23
  Scenario: #AC2
    Given I am logged in with email "b@gmail.com" and password "Password1!"
    Given I have added 5 inappropriate tags
    When I add another inappropriate tag
    Then I am unlogged from the system
    And I receive an email confirming my account has been blocked for one week


  @U23
  Scenario: #AC3
    Given I am banned
    When I try to log in with email "b@gmail.com" and password "Password1!"
    Then I am not logged in


