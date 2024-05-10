Feature: NF9 - The product must accept all valid characters - including accentuated letters
  such as macrons (e.g. Māori, Müller, Frédéric, ß)

  Scenario Outline:
    Given I enter a <name> in the first name input field less than 65 characters
    When I click the register button of the form
    Then If the name uses invalid characters, I get the appropriate <message>
    Examples:
      | name         | message                                                                                   |
      | "Timothy"    | No message                                                                                |
      | "Māori"      | No message                                                                                |
      | "Müller"     | No message                                                                                |
      | "Frédéric"   | No message                                                                                |
      | "ßimon"      | No message                                                                                |
      | "1"          | First name cannot be empty and must only include letters, spaces, hyphens or apostrophes  |
      | "😀"         | First name cannot be empty and must only include letters, spaces, hyphens or apostrophes  |
      | "$"          | First name cannot be empty and must only include letters, spaces, hyphens or apostrophes  |
      | "$%^&*()"    | First name cannot be empty and must only include letters, spaces, hyphens or apostrophes  |
      | "asdf23$"    | First name cannot be empty and must only include letters, spaces, hyphens or apostrophes  |
      | "ѮѺОуЛфхцчшщ"| No message                                                                                |
      | "你好人"      | No message                                                                                |
      | "مرحبا شخص"  | No message                                                                                |
