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
        And I uploaded an invalid image
        When the app cannot identify the image
        Then I should be informed that the app failed to identify plant

