# (Some of the step definitions are in BrowsePublicGardens.java)
Feature: U70011 - As Nikora Foxton, I can follow public gardens I find interesting or inspiring.

  @U70011
  Scenario:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And there is a garden with the name "Apple Orchard"
    And I am on the browse gardens page to search
    When I click on the follow button
    Then I see a notification telling me I am now following that garden
    And I am now following the garden


