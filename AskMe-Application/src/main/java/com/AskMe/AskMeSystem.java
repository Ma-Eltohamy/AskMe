package com.AskMe;


public class AskMeSystem {
    public AskMeSystem() {
        run();
    }

    public static void run() {
        Database.checkDatabase();
        Database.checkUsersTable();
        Database.checkQuestionsTable();

        while (true) {
            int userChoice;
            if (UsersManager.getUserId() == null) {
                UI.printMenu(UI.guestMenu);
                userChoice = Utilities.getMenuChoice(UI.guestMenu.size());

                switch (userChoice) {
                    case 1:
                        UsersManager.doLogin();
                        break;
                    case 2:
                        UsersManager.doSignup();
                        break;
                    case 3:
                        return;
                }
            } else {
                UI.printMenu(UI.userMenu);
                userChoice = Utilities.getMenuChoice(UI.userMenu.size());

                switch (userChoice) {
                    case 1:
                        UI.listFeeds(true);
                        break;
                    case 2:
                        UI.listFromMeQuestions();
                        break;
                    case 3:
                        QuestionsManager.answerQuestion();
                        break;
                    case 4:
                        QuestionsManager.deleteQuestion();
                        break;
                    case 5:
                        QuestionsManager.askQuestion();
                        break;
                    case 6:
                        UI.listSystemUsers();
                        break;
                    case 7:
                        UI.listFeeds(false);
                        break;
                    case 8:
                        UsersManager.doLogout();
                        break;
                    case 9:
                        return;
                }
            }
        }
    }
}
