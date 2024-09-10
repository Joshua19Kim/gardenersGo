# All of the step definitions are in BrowsePublicGardens.java because it makes sense
Feature: U70012 - As Kaia, I can see who is following my public gardens on the garden details page.

  @AC1
  Scenario:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have a public garden with the name "Apple Orchard" that has at least one follower
    When I navigate to the garden details page
    Then I see the followers section showing the followers of the garden

  @AC3
  Scenario:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have a public garden with the name "Apple Orchard" that has at no followers
    When I navigate to the garden details page
    Then It should display a message indicating "No followers yet"