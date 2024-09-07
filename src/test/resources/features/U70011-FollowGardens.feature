# All of the step definitions are in BrowsePublicGardens.java because it makes sense
Feature: U70011 - As Nikora Foxton, I can follow public gardens I find interesting or inspiring.

  Background:
    Given There is a test gardener which has a garden that the current user can follow

  @AC1
  Scenario:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And there is a garden with the name "Apple Orchard"
    And I am on the browse gardens page to search
    And I want to follow a garden with the name "Rose Garden"
    When I click on the follow button
    Then I see a notification telling me I am now following that garden
    And I am now following the garden

  @AC5
  Scenario:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And there is a garden with the name "Apple Orchard"
    And I am on the browse gardens page to search
    And I am following the garden "Apple Orchard"
    When I click on the follow button
    Then I see a notification telling me I am no longer following that garden
    And I am no longer following the garden

  @AC3
  Scenario:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And there is a garden with the name "Apple Orchard"
    And I am on the browse gardens page to search
    And I am following the garden "Apple Orchard"
    When I navigate to the followed gardens section of the My Gardens page
    Then I see "Apple Orchard" shown in the followed gardens section list
    And I can view details of "Apple Orchard" by selecting it from the list

#  @AC4
#  Scenario:
#    Given I am logged in with email "a@gmail.com" and password "Password1!"
#    And there are no followed gardens
#    When I navigate to the followed gardens section of the My Gardens page
#    Then I see a message saying "You are not following any gardens yet."