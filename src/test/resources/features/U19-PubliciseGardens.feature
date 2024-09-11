Feature: U19 - As Inaya, I want to be able to make my garden public so that others can see what I’m growing.

#  Scenario: #AC1 - Writing in sprint #3, I cannot finish this as we deferred the story for browsing gardens.
#    Given I click the one of the gardens from the list which I own
#    When I tick the checkbox labelled "Make my garden public"
#    Then My garden will be visible in search results.


@U19
Scenario: #AC2
  Given I am on Create New Garden form
  When I add valid description of the garden
  And I submit the create form
  Then The new description is persisted.

@U19
Scenario: #AC3
  Given I am on Create New Garden form
  When I do not add any description
  And I submit the create form
  Then The new description is persisted.

@U19
Scenario: #AC4
  Given I am on the Edit Garden form for one of the existing garden
  When I add valid description of the garden to update
  And I submit the edit form
  Then The updated description is persisted.

@U19
Scenario: #AC5
  Given I am on the Edit Garden form for one of the existing garden
  When I delete the current description and leave it empty to update
  And I submit the edit form
  Then The updated description is persisted.

@U19
Scenario Outline: #AC6
  Given I am on Create New Garden form
  When I submit the create form with invalid <description> of the garden
  Then The error message for invalid description comes up.
  Examples:
    | description                               |
#  Only special characters
    | "#$^#$^"                                  |
#  513 letters
    | "aaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkakfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfasfgafdgadfhyfsafdbafdhbdafasdfs" |
#  598 letters
    | "aaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfjkasjdhflkjasdlkfjasdvkjahdkfjbvhakjfdkakfjasdvkjahdkfjbvhakjfdkaaaisjhdfkjaslfdkjsalkdjflaksjdflkasjdlfkjsadfasdfashatrjahkjahskdjhflkajshdlkfjaskdfasfgafdgadfhyfsafdbafdhbdafasakjhsdfljahsdflkjhasdflkjahsdflkjahslkfdjhalkdsjfhlkajvlkjdsabkuiibbboasjhdflkjahsdkjfdf" |

@U19
Scenario Outline: #AC7
  Given I am on Create New Garden form
  When I submit the create form with <description> including bad words for the garden description
  Then The error message for inappropriate words comes up.
  Examples:
    | description                                                  |
    | "The fucking greatest garden"                                |
    | "røvskæg dont know the meaning? but definitely bad words" |
    | "this is bad word fotze as well" |
    | "from danish, pikhår" |
