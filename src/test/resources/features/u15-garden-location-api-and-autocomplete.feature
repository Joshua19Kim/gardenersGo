Feature: U15 As Kaia, I want to specify an actual address for my different recorded gardens,
  so that I can get I can get consistent information relevant to my area.

  @U15_AC5
  Scenario: A user attempts to create a garden without specifying the city and country
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have provided the street number and name "30 Maidstone Road" and the country "New Zealand"
    When I submit the create new garden form
    Then an error message tells me "City is required"

  @U15_AC5
  Scenario: A user attempts to create a garden without specifying the city and country
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have provided the street number and name "21 Gladstone Road" and the city "Auckland"
    When I submit the create new garden form
    Then an error message tells me "Country is required"

