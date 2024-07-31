Feature: As Kaia, I want to make sure that users who repeatedly trying to add inappropriate tags are
  prevented to use the app for one week so that they can reflect on their behaviour.

  @U23
  Scenario: #AC1
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have added inappropriate words four times and am adding one more time
    When I try to submit the tag
    Then the system shows the warning message and send an warning email.
