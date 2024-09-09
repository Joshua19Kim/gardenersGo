Feature: U21 - As Inaya, I want to be able to browse gardens by different user-specified tags so that I can browse for gardens that match my interests.

  @U21
  Scenario: # AC5 - valid
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I add a valid tag
    When I confirm the tag name
    Then the tag is added to the garden tags
  @U21
  Scenario Outline: # AC6,7
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I add an invalid "<tag>"
    When I confirm the tag name
    Then the tag is not added to the garden tags
    And an error message displays <message>

    Examples:
      | tag                        | message                                                                    |
      | This@Invalid | "The tag name must only contain alphanumeric characters, spaces, -, _, ', or \" <br/>"                                                        |
      | ThisTagNameIsWayTooInvalid              | "A tag cannot exceed 25 characters <br/>"                    |
      | -                          | "The tag name must contain at least one alphanumeric character"     |
      | $$$$                       | "The tag name must only contain alphanumeric characters, spaces, -, _, ', or \" <br/>The tag name must contain at least one alphanumeric character"    |
      | @@@@@@@@@@@@@@@@@@@@@@@@@@ | "A tag cannot exceed 25 characters <br/>The tag name must only contain alphanumeric characters, spaces, -, _, ', or \" <br/>The tag name must contain at least one alphanumeric character" |
