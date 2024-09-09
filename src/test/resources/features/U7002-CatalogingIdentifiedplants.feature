Feature: U7002 - As Jackie Wei, I want to catalog identified plants into my personal collection, so that I can keep track of the plants I've discovered.

  @7002
    Scenario: #AC2 - View plants belonging to a species
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    When I click on an entry for a plant on my collections page
    Then I see a list of all the unique plants that I have collected before that belong to that plant species

  @U7002
  Scenario Outline: #AC3 - Save an identified plant
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have an image of a plant
    When the app identifies the plant image
    And the app displays the name and relevant details after identifying
    And I input a <name> and <description>
    Then my plant is saved
    Examples:
      | name      | description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
      | "Timothy" | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | "Timothy" | "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus e" |
      | "Timothy" | ""                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |


  @U7002
  Scenario Outline: #AC3 - Invalid name or description
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have an image of a plant
    When the app identifies the plant image
    And the app displays the name and relevant details after identifying
    And I input a <name> and <description>
    Then If the details are invalid, I get the appropriate <message>
    Examples:
      | name                                                                | description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | message                                                                                                                                                   |
      | "Timothy"                                                           | "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus el" | "Plant description must be less than 512 characters"                                                                                                      |
      | "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean@" | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>Plant name must be less than 64 characters" |
      | "😀"                                                                | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>"                                           |
