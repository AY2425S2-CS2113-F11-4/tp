# Felix Yuen Pin Qi - Project Portfolio Page

## Project: FlashCLI
### Overview
FlashCLI is a command-line flashcard application designed specifically for CS2113 Software Engineering students. It helps students create, manage, and review flashcards efficiently, making it easier to retain important concepts in Java, software design, and coding best practices.

Given below are my contributions to the project:
### Summary of Contributions

- Code contributed: [RepoSense link](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=felfelyuen&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-02-21&tabOpen=true&tabType=authorship&tabAuthor=felfelyuen&tabRepo=AY2425S2-CS2113-F11-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Features Implemented

#### **Critical Feature**: Added the ability to view question per flashcard.

- **What it does:** Allows the user to view the flashcard's question for a specific index.
- **Justification:**  This feature allows the user to view the flashcard's information at once, which would be useful for them to track their learning progress.

#### **Critical Feature**: Added the ability to list all the flashcard questions
- **What it does:** Allows the user to list for all the flashcard questions in the deck.
- **Justification:** This feature allows the user to identify each flashcard by the index more easily. This helps them get a clearer picture of their overall learning progress.

#### **New Feature**: Added the ability to quiz the flashcards.
- **What it does:**  Allows the user to enter quiz mode, quizzing the unlearned flashcards in the deck one by one, and giving the total time taken to finish the quiz. Correctly answered flashcards would be marked as learned, and would not be quizzed again.
- **Justification:** This feature improves the product significantly, as the quiz mode can help the user test whether they have memorised the information well, and see which areas need improvement and which do not. 

#### **New Feature**: Added the ability to mark a flashcard as learned or unlearned.
What it does: Allows the user to mark a flashcard as learned or unlearned.
Justification: This feature allows the user to customise which flashcards they would like to be tested in quiz mode, and which flashcards they do not want. This could be particularly useful for the user to quiz on specific topics.

### Other Aspects
#### **Project Management / Contribution to Team-Based Tasks:**
- Opened up Github team organisation and repository, and managed issue tracker and milestones setup for 1.0 and 2.0
- Released 2.0 version.
- Reviewed 18+ PRs, giving non-trivial comments to PR #58.

#### **Documentation:**
- User Guide:
  - Added documentation for the features I completed: `list`, `qn` ,`mark_learned`, `mark_unlearned` and `quiz` 
- Developer's Guide:
  - Added documentation (including implementation and necessary diagrams) for the features I completed. 
  - Added Glossary in the appendix.

#### **Mentoring/Team Contributions**
- Coordinated with teammates in explaining code and debugging their code, notably the quiz_mode codes, and helped rectify checkstyle errors when spotted.
- Checked teammates' documentation and gave feedback through Telegram, such as class diagrams mistakes and typos.

- #### **Contributions Beyond the Project Team**
- Reported 19 bugs in other teams’ tP.
- Gave structured and constructive feedback on other teams' DG.