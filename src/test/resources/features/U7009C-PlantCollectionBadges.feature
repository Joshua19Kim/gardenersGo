Feature: As Jackie Wei, I enjoy collecting achievements and want to share my progress with my followers. I want to display and receive badges for collecting regions. Milestones: Collect a new region (1,5,10,17)

  @U7009B
  Scenario Outline:
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have collected <initialRegionCount> regions,
    When I have collected another region
    Then I will be shown the region badge with name <expectedName>
    Examples:
      |initialRegionCount|expectedName|
      |0|"1st Region Found"|
      |4|"5th Region Found"|
      |9|"10th Region Found"|
      |16|"17th Region Found"|