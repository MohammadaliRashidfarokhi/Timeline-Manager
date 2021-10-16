package Controllers;

import Models.User;

import javax.lang.model.type.ErrorType;

//This is the Popup Content Controller (is a parent class of all the forms that are injected in the Popup
//This allows a form to show errors to the user, setDirty (to show confirmation to the user on closing the popup with UNSAVED data)
//It's abstract so we don't implement here the refresView method
public abstract class ContentPopupController extends GeneralController {
    //isDirty is to see if there are changes not saved, so when the user tries to
    //close popup we can emit a warning to confirm action
    private boolean isDirty = false;

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public void closePopup() {
        //parent controller can be a MainController (if inside the login/signup page) or PopupController if inside a Popup
        //Only close the popup if this Controller is inside a popup
        if(getParentController() instanceof PopupController) {
            PopupController popupController = (PopupController) getParentController();
            popupController.closePopup();
        }
    }
}
