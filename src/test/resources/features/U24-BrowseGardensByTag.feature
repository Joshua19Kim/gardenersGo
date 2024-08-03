Feature: U24 As Inaya, I want to be able to browse gardens by different tags so that I can browse for gardens that match my interests.

  @U24
  Scenario: AC4 - A user attempts to add a tag to filter by typing out the tag
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And there is a garden with tags
    And I am on the browse gardens page
    And I type out the tag "berries"
    When I press the enter key
    Then the tag is added to my current selection
    And the text field is cleared

  @U24
  Scenario: AC5 -  A user attempts to add a tag that does not exist to their current selection
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And there is a garden with tags
    And I am on the browse gardens page
    And I type out the tag "spices"
    When I press the enter key
    Then the tag is not added to my current selection
    And the text field is not cleared
    And an error message on the browse gardens page tells me "No tag matching spices"
