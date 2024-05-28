Feature: U16 - As Sarah, I want to be able to change my password over email, so that I can still access my account even if I forget my password.

Scenario: AC1
  Given I am on the login page
  When I hit the "Forgot your password?" link
  Then I see a form asking me for my email address