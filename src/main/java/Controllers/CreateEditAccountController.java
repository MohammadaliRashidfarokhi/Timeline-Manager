package Controllers;

import Models.User;
import Utils.DatabaseController;
import Utils.ErrorMessageType;
import Utils.PopupContentType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class CreateEditAccountController extends ContentPopupController {
    private PopupContentType formMode;
    //if we are editing the information of a user, we use the user getters to get the information to edit
    private User user;
    private MainController mainController = (MainController) getParentController();
    private File selectedFile;
    
    Button selectFileButton, createAccountButton;
    TextField usernameField, pwdHintField, fullNameField;
    PasswordField pwdField;
    Label selectFileLabel, createOrEditLabel;
    ImageView profileImage;

    //formMode argument can have the values: PopupContentType.CREATE_ACCOUNT or PopupContentType.EDIT_ACCOUNT
    //this is to reuse the code to create or edit an event (no need for double of the work ;) )
    public CreateEditAccountController(PopupContentType formMode, User user, MainController mainController) {
    	setParentController(mainController);
        this.formMode = formMode;
        //TODO: if user is null create a new user object and assign it to this.user

		//TODO: be carefull with null pointer
		if(user==null) {
			user = new User();
		}

        //load fxml resource
        try {
            //Load fxml resource path to the actual create/edit account form
            StackPane createEditForm = FXMLLoader.load(getClass().getResource("/views/createAccount.fxml"));

            //Extract GUI elements and expose them as properties (lookup("#ID")): labels, buttons, textfields
            selectFileButton = (Button) createEditForm.lookup("#selectFileButton");
            createAccountButton = (Button) createEditForm.lookup("#buttonCreateAccount");
            usernameField = (TextField) createEditForm.lookup("#createAccountUsername");
            fullNameField = (TextField) createEditForm.lookup("#createAccountFullName");
            pwdHintField = (TextField) createEditForm.lookup("#createAccountPasswordHint");
            pwdField = (PasswordField) createEditForm.lookup("#createAccountPassword");
            selectFileLabel = (Label) createEditForm.lookup("#fileNotSelectedLabel");
            profileImage = (ImageView) createEditForm.lookup("#imageViewCreateAccount");
            createOrEditLabel = (Label) createEditForm.lookup("#createAccountLabel");

			//TODO: if the content of a textfield changes call setDirty(true) method (from ContentPopupController -> already implemented) (create a listener)
            setView(createEditForm);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println(CreateEditAccountController.class.getName() + ": FXML resource not found");
        }

		this.user = user;

        selectFileButton.setOnAction(actionEvent -> {
        	
				selectedFile = FileController.fileChooser();
				if (selectedFile != null) {
		            try {
		                String imageUrl = selectedFile.toURI().toURL().toExternalForm();
		                Image image = new Image(imageUrl);
		                profileImage.setImage(image); 
		            } catch (MalformedURLException ex) {
		                throw new IllegalStateException(ex);
		            }
				}
				selectFileLabel.setText(selectedFile.getName());
				
        });
        
        createAccountButton.setOnAction(actionEvent -> {
        	if (createAccountButton.getText().equals("Edit Account")) {
        		try {
					editAccount();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	} else
				try {
					createAccount();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        });
    }

    //TODO: implement saveContent() method (if success call closePopup(); if error call showError(String msg))
    
    //User presses save/create/edit button																		DONE
    //use setters on the User object to set the data you collected from the form
    
    //update setters on the User Model to throw Exceptions if the data is not valid								COMING SOON
    //if no exception catch follow the protocol to create or update an User in the DB (only if the data is valid!)
    
    //PROTOCOL TO SAVE USER IN DB: call this methods in this order												DONE
    //1. user.save() => save the User object into the DB
    //2. this.dataSaved(user) => tell the rest of the system that this object was changed
    //3. this.closePopup();

    //If we are editing the current user account use: getCurrentUser(); to get the current user logged in;		DONE

    //TODO: create constructor that have the TimelineID as parameter (so we know which timeline to edit) -> create property timelineID;
    public void saveContent () {
    	
    }

    @Override
    public void setView(Node view) {
        //We change the form here to create an "edit form" or a "create form"
        if(formMode == PopupContentType.CREATE_ACCOUNT ) {
            //change labels and text so it's a create account form

        } else {
            //change labels and text so it's a edit account form
        	createOrEditLabel.setText("Edit Account");
        	usernameField.setVisible(false);
        	pwdField.setPromptText("Insert New Password");
        	pwdHintField.setPromptText("Insert New Password Hint");
        	createAccountButton.setText("Edit Account");
        }
        super.setView(view);
    }

    @Override
    public void refreshView() {

    }

    public void createAccount() throws IOException {
    	// get information from boxes
    	String username = usernameField.getText();
    	String fullName = fullNameField.getText();
    	String pwd = pwdField.getText();
    	String pwdHint = pwdHintField.getText();
    	
    	if (username.isEmpty() || fullName.isEmpty() || pwd.isEmpty() || pwdHint.isEmpty())
    		showErrorMessage("Fill all spaces!");
    	
    	// if username is taken print error message
    	else if (DatabaseController.usernameTaken(username))
    		showErrorMessage("That username already taken!");
    	
    	// create user
    	else {
    		if (!selectFileButton.isPressed())
    			user.setImage("");
    		else {
    			FileController.moveFileToDir(selectedFile);
        		user.setImage(System.getProperty("user.home") + "timeline_manager" + selectedFile.getName());
    		}
    		
        	user.setUsername(username);
            user.setFullname(fullName);
            user.setPassword(pwd);
            user.setPasswordHint(pwdHint);
            	
            user.save();
            this.dataSaved(user);
            
            if(getParentController() instanceof MainController)
        		this.closePopup();
        	else
        		mainController.removeLoginPage();
    	}
    }
    
    public void editAccount() throws IOException {
    	getCurrentUser();
    	String fullName = fullNameField.getText();
    	String pwd = pwdField.getText();
    	String pwdHint = pwdHintField.getText();
    	
    	if (fullName.isEmpty() || pwd.isEmpty() || pwdHint.isEmpty()) {
    		showErrorMessage("Fill all spaces");
    	}
    	else
    		closePopup();
    	
    	FileController.moveFileToDir(selectedFile);
    	getCurrentUser().setImage(System.getProperty("user.home") + "timeline_manager" + selectedFile.getName());
    	getCurrentUser().setFullname(fullName);
    	getCurrentUser().setPassword(pwd);
    	getCurrentUser().setPasswordHint(pwdHint);
    	
    	getCurrentUser().save();
    	this.dataSaved(getCurrentUser());
    	
    	if(getParentController() instanceof MainController) {
    		this.closePopup();
    	}
    	else
    		mainController.removeLoginPage();
    }
    
    // error message method
    public void showErrorMessage(String msg) {
    	showError(msg, ErrorMessageType.WARNING);
    }
}