Feature: As Jackie Wei, I enjoy collecting achievements and want to share my progress with my followers. I want to display and receive badges for collecting plants. Milestones: Collect a new plant (1,10,25,50,100)

  @U7009A
  Scenario Outline:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have collected <initialPlantCount> plants,
    When I have collected another plant
    Then I will be shown the earned badge with name <expectedName>
    Examples:
    |initialPlantCount|expectedName|
    |0|"1st Plant Found"|
    |9|"10th Plant Found"|
    |24|"25th Plant Found"|
    |49|"50th Plant Found"|
    |99|"100th Plant Found"|
