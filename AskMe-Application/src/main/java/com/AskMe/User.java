package com.AskMe;



public class User {
    int id;
    private String userName;
    private String password;
    private String name;
    private String email;
    private boolean allowAnonymous;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }

    public static String getValidUserData(String inputLabel) {
        String userInput = null;
        Field field = null;

        String inputMessage = "Please enter your " + inputLabel + ": ";

        if (inputLabel.equals("email"))
            field = new EmailValidator();
        else if (inputLabel.equals("username"))
            field = new UserNameValidator();
        else if(inputLabel.equals("allow anonymous")) {
            inputMessage = "Do you allow anonymous questions (yes/no): ";
            field = new AnonymousValidator();
        }

        if(field == null)
            field = new Validator();


        int attemptsRemaining = 5;
        while (attemptsRemaining > 0) {
            System.out.print(inputMessage);
            userInput = Utilities.scan.nextLine();

            if (!field.validateCommonRules(userInput, inputLabel)) {
                attemptsRemaining--;
                continue;
            }

            if (!field.isValid(userInput)) {
                attemptsRemaining--;
                continue;
            }

            return userInput;
        }

        System.out.println("Too many attempts. Please try again later.");
        return null;
    }

    public boolean getValidName() {
        String name = getValidUserData("name");
        if(name == null)
            return false;
        this.name = name;
        return true;
    }

    public boolean getValidPassword() {
        String password = getValidUserData("password");
        if(password == null)
            return false;
        this.password = password;
        return true;
    }

    public  boolean getValidEmail() {
        String email = getValidUserData("email");
        if(email == null)
            return false;
        this.email = email;
        return true;
    }

    public boolean getValidUserName() {
        String userName = getValidUserData("username");
        if(userName == null)
            return false;
        this.userName = userName;
        return true;
    }

    public boolean getAllowAnonymousQues() {
        String isAllowAnonymous = getValidUserData("allow anonymous");
        if(isAllowAnonymous == null)
            return false;

        isAllowAnonymous.toLowerCase();
        if(isAllowAnonymous.equals("yes"))
            this.allowAnonymous = true;
        else
            this.allowAnonymous = false;
        return true;
    }
}
