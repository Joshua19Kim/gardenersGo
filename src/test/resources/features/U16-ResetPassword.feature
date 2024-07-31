Feature: U16 - As Sarah, I want to be able to change my password over email, so that I can still access my account even if I forget my password.

  @U16
  Scenario: #AC1
    Given I am on the login page
    When I hit the forgot your password link
    Then I am taken to the forgot your password page

  @U16
  Scenario Outline: #AC2
    Given I am on the forgot your password page
    And I am on a deployment "http://localhost:8080/forgotPassword"
    And I enter the empty or malformed email address <email>
    When I submit the invalid email
    Then an error message says "Email address must be in the form 'jane@doe.nz'"
    Examples:
      |email|
      |""|
      |"@doe.nz"|
      |"jane@.nz"|
      |"jane@doe"|
      |"jane!@doe.nz"|
      |"jane.doe.nz"|
      |"jane@@doe.nz"|

  @U16
  Scenario: #AC3
    Given I am on the forgot your password page
    And I am on a deployment "http://localhost:8080"
    And I enter a valid email that is not known to the system
    When I submit the valid email that is not known to the system
    Then a confirmation message tells me "An email was sent to the address if it was recognised"

  @U16
  Scenario: #AC4
    Given I am on the forgot your password page
    And I am on a deployment "http://localhost:8080"
    And I enter an email that is known to the system
    When I submit the email that is known to the system
    Then a confirmation message tells me "An email was sent to the address if it was recognised"
    And an email is sent to the email address


  @U16
  Scenario: #AC5 - Test Deployment
    Given I am on the forgot your password page
    And I am on a deployment "https://csse-seng302-team700.canterbury.ac.nz/test/forgotPassword"
    And I enter an email that is known to the system
    When I submit the email that is known to the system
    Then a confirmation message tells me "An email was sent to the address if it was recognised"
    And an email is sent to the email address
    And the link in the email contains the URL "https://csse-seng302-team700.canterbury.ac.nz/test"

  @U16
  Scenario: #AC5 - Local Deployment
    Given I am on the forgot your password page
    And I am on a deployment "http://localhost:8080/forgotPassword"
    And I enter an email that is known to the system
    When I submit the email that is known to the system
    Then a confirmation message tells me "An email was sent to the address if it was recognised"
    And an email is sent to the email address
    And the link in the email contains the URL "http://localhost:8080"