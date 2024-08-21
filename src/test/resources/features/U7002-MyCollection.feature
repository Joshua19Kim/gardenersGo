Feature: Jackie Wei, I want to catalog identified plants into my personal collection, so that I can keep track of the plants I've discovered.

  @AC1
  Scenario:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    When I am on the my collections page
    Then The plants in my collection are shown