Feature: U22 - As Kaia, I want to make sure that tags added to gardens do not contain any inappropriate words so that the sensibilities of other gardeners are not offended

  @U22
  Scenario: # AC1
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I am adding a valid tag
    When I confirm the tag
    Then The tag is checked for offensive or inappropriate words
  @U22
  Scenario: # AC2
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I add an inappropriate tag
    When I confirm the tag
    Then the tag is not added to the list of user-defined tags
  @U22
  Scenario: # AC5
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I add an inappropriate tag
    When I confirm the tag
    Then the users bad word counter is incremented by one