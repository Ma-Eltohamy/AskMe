package com.AskMe;

public interface Field {
    boolean isValid(String userInput);

    default boolean isStartWithNum(String userInput, String inputLabel){
        if(userInput.charAt(0) >= '0' && userInput.charAt(0) <= '9') {
            System.out.println("The " + inputLabel + " field should not start with numbers. Please try again.");
            return true;
        }
        return false;
    }

    default  boolean isContainSpace(String userInput, String inputLabel){
        if (userInput.contains(" ")) {
            System.out.println("The " + inputLabel + " field should not contain spaces. Please try again.");
            return true;
        }
        return false;
    }

    default boolean validateCommonRules(String userInput, String inputLabel) {
        if (userInput.isEmpty()) {
            System.out.println("The " + inputLabel + " field should not be empty. Please try again.");
            return false;
        }

        if ((inputLabel.equals("email") || inputLabel.equals("username")) && isContainSpace(userInput, inputLabel))
            return false;

        if ((!inputLabel.equals("password")) && isStartWithNum(userInput, inputLabel))
             return false;

        if((inputLabel.equals("name") || inputLabel.equals("allow_anonymous")) && userInput.trim().isEmpty()){
            System.out.println("The " + inputLabel + " field should not be empty. Please try again.");
            return false;
        }

        return true;
    }
}

class Validator implements Field{
    @Override
    public boolean isValid(String userInput){
        return true;
    }
}

class EmailValidator implements Field {
    @Override
    public boolean isValid(String userInput) {
        if(userInput == null)
            return false;

        if (!Database.isColumnValueAvailable("email", userInput)) {
            System.out.println("==> Sorry, this email is already in use.");
            return false;
        }
        return true;
    }
}

class UserNameValidator implements Field{
    @Override
    public boolean isValid(String userInput){
        if(userInput == null)
            return false;

        if(!Database.isColumnValueAvailable("username", userInput)) {
            System.out.println("==> Sorry, this user name is already in use.");
            return false;
        }
        return true;
    }
}

class AnonymousValidator implements Field {
    @Override
    public boolean isValid(String userInput) {
        if(userInput == null)
            return false;

        userInput.toLowerCase();
        if (!userInput.equals("yes") && !userInput.equals("no")) {
            System.out.println("==> Please answer with 'yes' or 'no'.");
            return false;
        }
        return true;
    }
}

class QuestionValidator implements Field{
    @Override
    public boolean isValid(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("Question can't be empty. Please try again.");
            return false;
        }
        return true;
    }
}
