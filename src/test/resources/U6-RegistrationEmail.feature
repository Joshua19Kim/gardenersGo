Feature: U6 - Registration email
  @U6
  Scenario: #AC 1
    Given I submit a fully valid registration form
    When when I click the “Register” button
    Then A confirmation email is sent to my email address
    And A unique registration token is included in the email in the form of a unique signup code
    And I’m presented with a page asking for the signup code
  @U6
  Scenario: #AC 2 (modified)
    Given I have a signup code
    When The signup code is deleted
    And I try to use the signup code
    Then I see an error message “Signup code invalid”
  @U6
  Scenario: #AC 3 (modified)
    Given I have a signup code
    When I try to use the signup code
    Then I am logged in
