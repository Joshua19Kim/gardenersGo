Feature: U22 - As Kaia, I want to make sure that tags added to gardens do not contain any inappropriate words so that the sensibilities of other gardeners are not offended

  @U22
  Scenario: # AC1
    Given I am adding a valid tag
    When I confirm the tag
    Then The tag is checked for offensive or inappropriate words
  @U22
  Scenario: # AC2
    Given I add an inappropriate tag
    When I confirm the tag
    Then the tag is not added to the list of user-defined tags
  @U22
  Scenario: # AC5
    Given the evaluation of a user-defined tag was delayed
    When the tag has been evaluated as inappropriate
    Then the tag is removed from the garden it was assigned to
    And it is not added to the list of user-defined tags
    And the user’s count of inappropriate tags is increased by 1.