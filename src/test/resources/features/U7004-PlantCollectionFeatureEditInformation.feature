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
    And I input a <name>, <description>, <latitude> and <longitude>
    When I submit the edit plant form
    Then the information is updated
    Examples:
      |name|description| latitude | longitude |
      |"PlantName"|"description"| "75" | "175" |
      |"PlantName"|""           | "50" | "150" |
      |"P-L'Ant"|"description"| "-80"  | "30"  |
      |"Plant"    |"D!3s ? crip"| "34" | "-70" |


  @U7004
  Scenario Outline: #AC2 - Invalid info
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have a catalogued plant
    And I am on the edit plant form for the catalogued plant
    And I input a <name>, <description>, <latitude> and <longitude>
    When I submit the edit plant form
    Then I get the error message <error>
    Examples:
      |name|description| latitude | longitude | error|
      |""|"description"| "20"     | "50"      |"Plant name cannot be empty <br/>Plant name must include at least one letter"|
      |"PlantName"|"!"           | "30" | "-40" |"Plant description must be 512 characters or less and contain some text <br/>"|
      |"PlantName"|"wellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellover512charswellove"| "60" | "150" |"Plant description must be 512 characters or less <br/>"|
      |"Hey!"    |""| "20"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | "40"  | "Plant name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      |"ThisistoomanycharactersforahumbleplantnameIdonotknowwhyyouwouldevenwantthismanyitisquitealot"|""| "30"                                                                                                                                                                                                                                                                                                                                                                                                                                                     | "45"  |"Plant name must be 64 characters long or less <br/>"                                                                                                                                                                                                                                                                                                                                                                                                                                   |
      | "plant name" | "my plant" | "180" | "150" | "Invalid Location" |
      | "plant name" | "my plant" | "50" | "300" | "Invalid Location" |
      | "plant name" | "my plant" | "sgsdgdahad" | "sdgsdgsDG" | "Invalid Location" |

