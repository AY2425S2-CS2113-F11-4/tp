# Li Shunyang - Project Portfolio Page

## Project: FlashCLI
## Overview
FlashCLI is a command-line flashcard application designed specifically for CS2113 Software Engineering students. It helps students create, manage, and review flashcards efficiently, making it easier to retain important concepts in Java, software design, and coding best practices.

Given below are my contributions to the project
### Summary of Contributions
- **New Feature**: Add the ability to output quiz result upon completion of a quiz.
    - What it does: Allows the user to view his/her quiz result to check mistakes. 
      The incorrectly answered flashcard will be outputted for user's review. If 
    - user doesn't have any mistakes, the terminal will respond with a celebrate tone.
    - The method can only be executed when a quiz is completed. 
    - Justification: This feature improves the product significantly because a user can access their
      their mistakes and review them accordingly. This feature will be extremely useful during exam period.
    - Highlights: A stretch goal method was supposed to be merged to the upstream, which is to give user a second chance to take a test with only wrongly answered flashcards.
      The stretch objective is also very helpful to serve the purpose because it enables a iterative process of revision. 
      The wrongly answered flashcards will be a global variable assigned to the test deck. However, developer struggled to debug the ridiculous issue encountered in IDE, which could
      not be resolved even after he consulted his teammate. The break point and other debugging techniques couldn't work for the issue. Due to time concern, the developer decided to drop the stretch goal. 
    - Credits: 

- **New Feature**: Added a loading effect before execution of each command.
    - What it does: Add a colorful and interactive colored loading progress bar to notify user
      that the command is being processed. This will not determine if the command is illegal because
      the checking is allocated to be at the back.
    - Credits:


- **New Feature(Not implemented upon failure of gradle test)**: Added a colorful ASCIl text effect for opening title.
    - What it does: It output a colored fading UI effect for the title to make the application more interesting.
    - Highlights: The feature cannot be merged to the main repo because it constantly failed the gradle i/o test
      when developer exhausted its debugging skills and asked peer, TA and prof.
      https://github.com/AY2425S2-CS2113-F11-4/tp/actions?page=3
    - Credits: ChatGPT for organizing color ASCIl constants

### Other aspects
#### **Project Management / Contribution to Team-Based Tasks:**
1. Updating issue tracker by closing 6 issues already completed based on teammates' PR
2. Triaging 5 bugs transferred from the PE Dry Run by assigning members, tagging priority and bug type
3. Reviewing over 11 of the team's PRs
4. Set up GitHub repo together as a team
5. Released 2.1 version.
6. Executing system and acceptance test for the entire codebase and find 5 bugs of teammates to edit,
   ranging from medium to low leve serverity.

- Code contributed: [RepoSense link](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=manz9802&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2025-02-21&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other)
- **Documentation:**
    - User Guide:
        - Added documentation for the features `View_res`
    - Developer's Guide:
        - Added implementation details, sequence diagram and class diagram for the `View_res`. [#68](https://github.com/AY2425S2-CS2113-F11-4/tp/pull/68)
        - Added manual test instruction in the appendix. [#62](https://github.com/AY2425S2-CS2113-F11-4/tp/pull/62)