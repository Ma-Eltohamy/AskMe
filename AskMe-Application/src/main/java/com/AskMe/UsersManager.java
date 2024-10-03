package com.AskMe;

public class UsersManager {
    private static Integer userId = null;

    public static void doLogin(){
        Field validator;
        System.out.println("Enter username: ");
        String userName = Utilities.scan.nextLine();

        validator = new Validator();
        if(!validator.validateCommonRules(userName, "username"))
            return;

        System.out.println("Enter password: ");
        String password = Utilities.scan.nextLine();

        if(password.isEmpty()) {
            System.out.println("==> Password can't be empty.");
            return;
        }

        Integer retrievedUserId = Database.isRegistered(userName, password);
        if(retrievedUserId != null) {
            setUserId(retrievedUserId);
        } else
            System.out.println("==> Invalid username or password");
    }

    public static void doSignup(){
        User newUser = new User();
        if (newUser.getValidUserName() && newUser.getValidPassword() && newUser.getValidName() && newUser.getValidEmail() &&
            newUser.getAllowAnonymousQues()){
            Integer newUserId = Database.addUser(newUser);

            if(newUserId != null)
                setUserId(newUserId);
        }
    }

    public static void doLogout(){
        setUserId(null);
    }

    public static Integer getUserId() {
        return userId;
    }

    public static void setUserId(Integer userId) {
        UsersManager.userId = userId;
    }
}
