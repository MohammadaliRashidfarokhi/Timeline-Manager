package Controllers;

import Models.User;
import Utils.ErrorMessageType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

//controller for the Login Form
public class LoginController extends GeneralController {

    //TODO: load fxml resource with the login form (initialize a property with it, create setters and getters)
    //TODO: get GUI elements (lookup("#ID") and expose them as properties
    //TODO: handle LOGIN button click
            //query DB to see if there is a user with the combination of username + password
            //(optional) we can hash the password to increase confidentiality of the pass
            //the user was found (call the method setCurrentUser of the MainController) -> if it doesn't exist, create it
            //the user was not found call showError(msg) of the MainController -> you can also change the style of the textfields
            // to make them red or whatever
    Button button;
    TextField userNameTextField;
    PasswordField userNamePasswordField;

    public LoginController(MainController mainController){
        setParentController(mainController);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/LoginForm.fxml"));
            setView(root);
            button= (Button) root.lookup("#LogInButton");
            userNameTextField= (TextField) root.lookup("#UserNameTextField");
            userNamePasswordField= (PasswordField) root.lookup("#UserNamePasswordField");

            button.setOnAction(actionEvent -> {
                loginPress();
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println();
        }


    }

    public void loginPress(){
        String userName = userNameTextField.getText();
        String password = userNamePasswordField.getText();
        MainController mainController = (MainController) getParentController();
        if (userName.length()==0 || password.length()==0){
            mainController.showError("There is no password or username inserted" , ErrorMessageType.WARNING);
        }else {
            User user = User.login(userName,password);
            if(user==null){
                //user not found
                mainController.showError("The password or username is incorrect",ErrorMessageType.ERROR);
            }else{
                //Success login
                dataSaved(user);
                mainController.removeLoginPage();

            }
        }
    }

    @Override
    public void refreshView() {
        //nothing to update in this view
    }
}
