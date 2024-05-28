Feature: U19 - As Inaya, I want to be able to make my garden public so that others can see what I’m growing.

#  Scenario: #AC1 - Writing in sprint #3, I cannot finish this as we deferred the story for browsing gardens.
#    Given I click the one of the gardens from the list which I own
#    When I tick the checkbox labelled "Make my garden public"
#    Then My garden will be visible in search results.

Scenario: #AC2
  Given I on Create New Garden form
  When I add valid description of the garden, and I submit the create form
  Then The new description is persisted.

Scenario: #AC3
  Given I on Create New Garden form
  When I do not add any description, and I submit the create form
  Then The new description is persisted.


Scenario: #AC4
  Given I am on the Edit Garden form for one of the existing garden
  When I add valid description of the garden, and I submit the edit form
  Then The updated description is persisted.

Scenario: #AC5
  Given I am on the Edit Garden form for one of the existing garden
  When I do not add any description, and I submit the edit form
  Then The updated description is persisted.

Scenario Outline: #AC6
  Given I on Create New Garden form
  When I add invalid <description> of the garden, and I submit the create form
  Then The error message comes up.
  Examples:
    | description                               |
#  Only special characters
    | "#$^#$^"                                  |
#  513 letters
    | "aaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkakfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfasfgafdgadfhyfsafdbafdhbdafasdfs" |
#  598 letters
    | "aaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkakfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfasfgafdgadfhyfsafdbafdhbdafasakjhsdfljahsdflkjhasdflkjahsdflkjahslkfdjhalkdsjfhlkajvlkjdsabkuiibbboasjhdflkjahsdkjfdf" |


