package Controllers;

import Models.User;
import Utils.ErrorMessageType;
import javafx.scene.Node;
//This is a General controller shared by all the controllers of the system
//it saves and gets the GUI view that was loaded using the FXML loader

public abstract class GeneralController {
    private Node view;
    private GeneralController parentController;

    GeneralController() {
    }

    GeneralController(Node view) {
        this.view = view;
    }

    public Node getView() {
        return view;
    }

    public void setView(Node view) {
        this.view = view;
    }

    public GeneralController getParentController() {
        return parentController;
    }

    public void setParentController(GeneralController parentController) {
        this.parentController = parentController;
    }

    //to update view (some controllers don't need to implement this if they don't need)
    abstract public void refreshView();

    //Communication down -> up
    public void showError(String msg, ErrorMessageType errorType) {
        //call parent's method until reaches the mainController
        if (parentController != null) {
            parentController.showError(msg, errorType);
        }
    }

    //Communication down -> up
    public void showError(String msg, ErrorMessageType errorType, int duration) {
        //call parent's method until reaches the mainController
        if (parentController != null) {
            parentController.showError(msg, errorType, duration);
        }
    }

    //Communication down -> up
    public void dataSaved(Object elementSaved) {
        //this will refresh the GUI where is needed
        //this method is overrided in the main controller to actually do something, and update other parts of the system
        if (getParentController() != null) {
            getParentController().dataSaved(elementSaved);
        }
    }

    public void addConfirmationPopUp(String title, String text) {
        if (getParentController() != null) {
            System.out.println("add Confirmation Popup General Controller");
            getParentController().addConfirmationPopUp(title, text);
        }
    }

        //Communication up -> down
        public User getCurrentUser () {
            return getParentController().getCurrentUser();

        }
    }

