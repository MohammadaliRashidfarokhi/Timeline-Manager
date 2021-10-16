package Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class PopupController extends GeneralController {

    private ContentPopupController contentController;

    public PopupController(ContentPopupController contentController, MainController parentController) {
        try {
            Pane popup = FXMLLoader.load(getClass().getResource("/views/Popup.fxml"));
            Pane popupContentBox = (Pane) popup.lookup("#popup_content");
            StackPane popupbackbone = (StackPane) popup.lookup("#popupbackbone");
            Button closePopupButton = (Button) popup.lookup("#closePopupBtn");

            closePopupButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    closePopup();
                }
            });

            //TODO: When the user clicks on the background the popup should close
            popupbackbone.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    System.out.println("Click outside");
                    //closePopup();
                }
            });

            //TODO: get other elements that we need to add actions into (like dark background click, close button click)

            setView(popup);
            //set the view of the content Contr
            //contentController.setView(popupContentBox);

            //set parent controller of the child (contentController)
            //pass this object to the content controller (so it can call closePopup() or showError()
            contentController.setParentController(this);

            //set parent controller of the Popup controller
            setParentController(parentController);

            //we set the contentController
            //and inject the view of the content inside popupContentBox
            setContentController(contentController);

        } catch (Exception e) {
            System.out.println("FXML resource not found");
        }
    }

    public Node getPopupContentView() {
        return contentController.getView();
    }

    public Node getPopupView() {
        return super.getView();
    }

    public void setContentController(ContentPopupController contentController) {
        this.contentController = contentController;
        //change content of the popup
        injectContentInsidePopup(contentController);
    }

    private void injectContentInsidePopup(ContentPopupController contentController) {
        Pane popupContentBox = (Pane) this.getView();
        Pane boxToInjectContent = (Pane) popupContentBox.lookup("#popup_content");
        boxToInjectContent.getChildren().clear();
        boxToInjectContent.getChildren().add(contentController.getView());
    }

    public void closePopup() {
        //TODO: check if popup content isDirty (show confirmation window in that case)
        if(contentController != null && contentController.isDirty()) {
            //show confirmation window
                addConfirmationPopUp("Are you sure you want to leave?","The data not saved is going to be erased.");
        } else {
            MainController mainController = (MainController) getParentController();
            mainController.removePopup();
        }
    }

    @Override
    public void refreshView() {
        //not needed here
    }
}
