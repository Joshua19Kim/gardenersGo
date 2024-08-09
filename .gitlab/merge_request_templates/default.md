## REVIEWEE CHECKLIST
- - [ ] Merged dev into your branch 
- - [ ] Fixed merge conflicts
- - [ ] All tests passed 
- - [ ] Did relevant manual testing and specified it in the manual testing column
- - [ ] Add merge description to the request
- - [ ] Make sure delete branch is checked
- - [ ] Below NFR Checklist is completed


## MERGE DESCRIPTION

## MANUAL TESTING 
https://docs.google.com/spreadsheets/d/1DNgNIImW8IDq6zZwMjNLyR00aWvIsctb-yj8jZbuT8E/edit?usp=sharing

Story tested:

ACS tested: 

## MERGE REQUEST CHECKLIST
- - [ ] Make sure that that the merge request is merging into DEV (do not merge into MAIN!!)
- - [ ] The task has met all of the measurable outcomes that were set. 
- - [ ] Task meets the relevant ACs 
- - [ ] The task has been tested (unit and/or acceptance testing) and the tests are passing. 
- - [ ] Javadoc is thorough 
- - [ ] Consistent code style and naming conventions 
- - [ ] No debugging code 
- - [ ] No code smells / bugs 
- - [ ] Inline comments for complex code

## TESTABLE NFR CHECKLIST
- - [ ] NFR 4. The product must maintain a consistent and accessible look and feel (e.g., colours,
placement of buttons, fonts), must offer a consistent user experience (e.g., naming and
effects of buttons, usage of buttons v. links), and must have a styling appropriate for the
vision.
- - [ ] NFR 5. The product must be both user friendly and fool-proof: users must be supported in their
tasks by identifying input mistakes and helping users to correct these mistakes.
- - [ ] NFR 6. All forms in the product must explicitly highlight all fields that are invalid when
submitting, such as by highlighting the text field with a different colour.
- - [ ] NFR 7. If a form contained errors and needs to be resubmitted, the form shouldn’t be cleared
after an unsuccessful submission.
- - [ ] NFR 8. The product must be responsive to different sizes of display, ranging from mobile phones
to desktop computers.
- - [ ] NFR 9. The product must accept all valid characters, included accentuated letters such as
macrons (e.g., Māori, Müller, ...).
- - [ ] NFR 12. When interacting with a text field on a form, pressing the enter key must not submit the
form, unless (potentially) when reaching the end of the form.
- - [ ] NFR 13. When interacting with any highlightable element on the page (e.g., text fields, button),
pressing tab must move the user to the next element in an ordered manner. For
example, pressing tab move down fields on a form, but does not move the cursor
randomly between the different inputs.
- - [ ] NFR 14. When hitting the “enter” key on a button / link, it must trigger the click action associated
with that button / link, e.g., the submission of a form.


## REMAINING NFR'S 

NFR 1. There must be appropriate default user accounts to explore all functionality of your
application, with credentials easily accessible in your GitLab Wiki.

NFR 2. There must be an appropriate amount of sensical data to show all functionality works,
i.e. if an AC requires 10 or more items for pagination, then there must be more than 10
items to show the pagination feature works.

NFR 3. As your product grows, the data in the production (live) instance must stay intact
between releases, i.e. you may need to migrate data if you change your database
schema, and you may not wipe your database without explicit approval from the PO.

NFR 10. The product must be configurable for different languages, i.e. follow the design principles
required for internationalisation (i18n).

NFR 11. Passwords must not be stored in plain text and must be encrypted in a non-recoverable
way (i.e. hashed).

