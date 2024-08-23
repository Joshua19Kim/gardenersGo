Feature: U7001 - As Jackie Wei, I want to scan plants using the app to identify them, so that I can quickly learn about the plants I encounter.

    @U7001
    Scenario: #AC1 - Identify plant from scan
        Given I have an image of a plant
        When I upload the image of the plant
        Then the app should accurately identify the plant species
        And display the name and relevant details

    @U7001
    Scenario: #AC3 - Cannot identify plant from scan
        Given I have a bad image of a plant
        When I upload the image of the plant
        Then I should be informed that no species was identified

    @U7001
    Scenario: #AC5 - Attribution
        Given I am on the plant identification page
        Then I see appropriate attribution text