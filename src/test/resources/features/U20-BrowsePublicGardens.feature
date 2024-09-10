Feature: U20 - As Inaya, I want to be able to browse gardens that other users have made public so that I can learn from what other gardeners are doing.

  @U20
  Scenario: AC3 A user attempts to search for for a garden
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And there is a garden with the name "Apple Orchard"
    And I input the search term "Apple"
    When I press the search button
    Then The gardens with matching results are shown


