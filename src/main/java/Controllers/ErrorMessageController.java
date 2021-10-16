package Controllers;

import Utils.ErrorMessageType;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.layout.HBox;

public class ErrorMessageController extends GeneralController {
    private String msg;
    private ErrorMessageType errorType;
    private int duration;
    private javafx.animation.Timeline errorMessageTimer;
    private Label errorTextLabel;
    private Label errorTitleLabel;

    ErrorMessageController(String msg, ErrorMessageType errorType, int duration, MainController mainController) {
        this.msg = msg;
        this.errorType = errorType;
        this.duration = duration;
        setParentController(mainController);
        try {
            HBox errorViewMessage = FXMLLoader.load(getClass().getResource("/views/ErrorMessage.fxml"));
            Button closeButton = (Button) errorViewMessage.lookup("#ExitButton");
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    hideError();
                }
            });
            errorTitleLabel = (Label) errorViewMessage.lookup("#ErrorMessageText");
            errorTextLabel =(Label) errorViewMessage.lookup("#ErrorMessageText1");
            setView(errorViewMessage);

            refreshView();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void hideError() {
        if(getView()!=null) {
            MainController mainController = (MainController) getParentController();
            mainController.getMainGraphicElement().getChildren().remove(getView());
            if(errorMessageTimer!=null) {
                errorMessageTimer.stop();
                errorMessageTimer = null;
            }
        }
    }

    //hide errorMessage after 5 seconds
    private void setErrorMessageTimer() {
        if(errorMessageTimer==null) {
            errorMessageTimer = new javafx.animation.Timeline(new KeyFrame(Duration.seconds(duration), ev -> {
                hideError();
            }));
            errorMessageTimer.setCycleCount(1);
            errorMessageTimer.play();
        }
    }

    @Override
    public void refreshView() {
        errorTextLabel.setText(msg);
        if(errorType == ErrorMessageType.WARNING){
            errorTitleLabel.setText("Warning!");
            getView().setStyle("-fx-background-color: #f4a460;-fx-background-radius: 10;-fx-padding: 1");
        }else {
            getView().setStyle("-fx-background-color:  #f2a8a2;-fx-background-radius: 10;-fx-padding: 1");
            errorTitleLabel.setText("Error!");

        }
        setErrorMessageTimer();
        MainController mainController = (MainController) getParentController();
        if(mainController.getMainGraphicElement().getChildren().contains(getView()) == false) {
            mainController.getMainGraphicElement().getChildren().add(getView());
            mainController.getMainGraphicElement().setMargin(getView(), new Insets(50,0,0,0));
        }
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setErrorType(ErrorMessageType errorType) {
        this.errorType = errorType;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
