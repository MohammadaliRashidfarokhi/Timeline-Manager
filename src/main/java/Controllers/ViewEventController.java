package Controllers;

import Models.Event;
import Utils.ErrorMessageType;
import Utils.PopupContentType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class ViewEventController extends ContentPopupController {
    private Event event;
    private VBox viewEventForm;
    Label eventName, eventTime, userName;
    ImageView eventImage, userImage;
    TextFlow eventDescription;

    //formMode can be viewEvent
    private PopupContentType formMode;

    //A event is going to be shown in a popup
    @Override
    public void refreshView() {
        //TODO: update view with the content from the event object
        eventName.setText(event.getName());
        eventTime.setText("The start date: " + event.getStartDate().toString() + " The end date: "
                + event.getEndDate().toString());
        Text text = new Text();
        text.setText(event.getDescription());
        eventDescription.getChildren().add(text);
        userName.setText(event.getCreatedBy().getUsername());
        Image eventPicture = new Image(event.getImage());
        eventImage.setImage(eventPicture);
        Image userPicture = new Image(event.getCreatedBy().getImage());
        userImage.setImage(userPicture);
    }

    public ViewEventController(PopupContentType formMode, Event event) throws IOException {
        //TODO: event cannot be null
        //if the event is empty, there shouldn't be popup window, so put check method in openPopup method

        this.event = event;
        this.formMode = formMode;
        //load fxml file
        try {
            //Extract GUI elements that are going to be target of user actions: TextFields, buttons, etc
            viewEventForm = FXMLLoader.load(getClass().getResource("/views/ViewEvent.fxml"));
            eventName = (Label) viewEventForm.lookup("#eventTitle");
            eventImage = (ImageView) viewEventForm.lookup("#eventPicture");
            eventTime = (Label) viewEventForm.lookup("#eventDate");
            userName = (Label) viewEventForm.lookup("#userName");
            userImage = (ImageView) viewEventForm.lookup("#userPicture");
            eventDescription = (TextFlow) viewEventForm.lookup("#eventDescription");

            setView(viewEventForm);
            refreshView();
        } catch (Exception e) {
            System.out.println("FXML resource not found");
        }
    }
}