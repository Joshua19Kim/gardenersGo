Feature: As Jackie Wei, I enjoy collecting achievements and want to share my progress with my followers. I want to display and receive badges for collecting species. Milestones: Collect a new species (1,10,25,50,100)

  @U7009B
  Scenario Outline:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have collected <initialSpeciesCount> species,
    When I have collected another species
    Then I will be shown the species badge with name <expectedName>
    Examples:
      |initialSpeciesCount|expectedName|
      |0|"1st Species Found"|
      |9|"10th Species Found"|
      |24|"25th Species Found"|
      |49|"50th Species Found"|
      |99|"100th Species Found"|