Feature: U7001 - As Jackie Wei, I want to scan plants using the app to identify them, so that I can quickly learn about the plants I encounter.

    @U7001
    Scenario: #AC1 - Identify plant from scan
        Given I am logged in with email "a@gmail.com" and password "Password1!"
        And I have an image of a plant
        When the app identifies the plant image
        Then the app displays the name and relevant details after identifying


    @U7001
    Scenario: #AC3 - Cannot identify plant from scan
        Given I am logged in with email "a@gmail.com" and password "Password1!"
        And I have an invalid image
        When I upload the image of the plant which cant be identified
        Then I should be informed that no species was identified

