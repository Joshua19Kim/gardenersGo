Feature: U7004 - As Jackie Wei, I can edit a plants information in my collection

  @U7004
  Scenario: #AC1
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have a catalogued plant
    When I click the edit button
    Then I see a form for editing the plant

  @U7004
  Scenario Outline: #AC2 - Valid info
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have a catalogued plant
    And I am on the edit plant form for the catalogued plant
    And I input a <name> and <description>
    When I submit the edit plant form
    Then the information is updated
    Examples:
      |name|description|
      |"PlantName"|"description"|
      |"PlantName"|""           |
      |"P-L'Ant"|"description"|
      |"Plant"    |"D!3s ? crip"|


  @U7004
  Scenario Outline: #AC2 - Invalid info
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have a catalogued plant
    And I am on the edit plant form for the catalogued plant
    And I input a <name> and <description>
    When I submit the edit plant form
    Then I get the error message <error>
    Examples:
      |name|description|error|
      |""|"description"|"Plant name cannot be empty <br/>Plant name must include at least one letter"|
      |"PlantName"|"!"           |"Plant description must be 512 characters or less and contain some text <br/>"|
      |"PlantName"|"wellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellove"|"Plant description must be 512 characters or less <br/>"|
      |"Hey!"    |""|"Plant name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      |"ThisistoomanycharactersforahumbleplantnameIdonotknowwhyyouwouldevenwantthismanyitisquitealot"|""|"Plant name must be 64 characters long or less <br/>"                                                                                                                                                                                                                                                                                                                                                                                                                                   |

#  @U7004
#  Scenario: #AC4 - Cancel Form
#    Given I am logged in with email "a@gmail.com" and password "Password1!"
#    And I have a catalogued plant
#    And I am on the edit plant form for the catalogued plant
#    When I click the cancel button
#    Then the information is not saved