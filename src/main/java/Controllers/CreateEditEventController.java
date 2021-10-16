package Controllers;

import Models.DateTimePicker;
import Models.Event;
import Models.Timeline;
import Utils.ErrorMessageType;
import Utils.PopupContentType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CreateEditEventController extends ContentPopupController {
    private PopupContentType formMode;

    private Event event; //event to be edited or created
    private Timeline timeline;
    private File selectedFile;
    //formMode can have the values: PopupContentType.CREATE_EVENT or PopupContentType.EDIT_EVENT
    //this is to reuse the code to create or edit an event (no need for double of the work ;) )

    Label windowTitle;
    Button selectImageButton;
    Button saveEventButton , cancelButton;
    TextField eventNameText;
    TextArea eventDescriptionText;
    ImageView eventImageView;
//    DatePicker selectStartingDate;
//    DatePicker selectEndingDate;
//    Spinner startingTime,endingTime;
    String eventName;
    String description;
    String imageFile;
    DateTimePicker start = new DateTimePicker();
    DateTimePicker end = new DateTimePicker();





    public CreateEditEventController(PopupContentType formMode, Event event) {
        this.formMode = formMode;
        //TODO: if event is null create a new event object and assign it to this.event
        if (event == null){
            this.event = new Event();
        }
        else {
            this.event = event; //event to be edited
            //load fxml resource
        }
        try {
            //Directing the path of fxml
            AnchorPane createEventForm = FXMLLoader.load(getClass().getResource("/views/EventCreation.fxml"));

            windowTitle = (Label) createEventForm.lookup("#eventWindowTitle");
            selectImageButton = (Button) createEventForm.lookup("#selectImageButton");
            saveEventButton = (Button) createEventForm.lookup("#savingEventButton");
            cancelButton = (Button) createEventForm.lookup("#cancelButton");
            eventImageView = (ImageView) createEventForm.lookup("#eventImageView");
            eventNameText =  (TextField) createEventForm.lookup("#eventNameText");
            start = (DateTimePicker) createEventForm.lookup("#dateTimePickerStart");
            end = (DateTimePicker) createEventForm.lookup("#dateTimePickerEnd");
//            selectStartingDate= (DatePicker) createEventForm.lookup("#startingDate");
//            selectEndingDate = (DatePicker) createEventForm.lookup("#endingDate");
//            startingTime = (Spinner) createEventForm.lookup("#startingTime") ;
//            endingTime = (Spinner) createEventForm.lookup("#endingTime") ;
            eventDescriptionText = (TextArea) createEventForm.lookup("#eventDescText");


            //TODO: make the previous available in the properties of this class
            //TODO: if the content of a textfield changes call setDirty(true) method (from ContentPopupController -> already implemented)
            setView(createEventForm);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println(CreateEditAccountController.class.getName() + ": FXML resource not found");
        }

        selectImageButton.setOnAction(actionEvent -> {
            try {
                fileChooser();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });

        eventNameText.textProperty().addListener((observable , oldValue , newValue) -> {
          //  if(!eventNameText.getText().isEmpty()){
             //   if(!oldValue.equals(newValue))
                    setDirty(true);
          //  }
            createEventName();
        });

        cancelButton.setOnAction(ActionEvent -> closePopup());

        saveEventButton.setOnAction(actionEvent -> {
            saveContent();
        });

        eventDescriptionText.textProperty().addListener((observable , oldValue , newValue) ->{

                    setDirty(true);
           // }
                      createDescription();
        } );


        //TODO: when it set as this, popup window dose not show need to fix later
//       start.valueProperty().addListener((observable, oldValue , newValue) ->{
//          // if(oldValue!= null && !oldValue.equals(newValue))
//               setDirty(true);
//       } );
//
//       end.valueProperty().addListener((observableValue, oldValue, newValue) -> {
//          //  if(!oldValue.equals(newValue))
//                setDirty(true);
//       });
    }





    public void fileChooser() throws MalformedURLException {
        FileChooser filePicChooser = new FileChooser();
        filePicChooser.setTitle ("Select  Image");


        filePicChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files", "*.png")
                ,new FileChooser.ExtensionFilter("JPG files", "*.jpg")
        );
        selectedFile = filePicChooser.showOpenDialog(new Stage());
        this.imageFile = selectedFile.getName();
        eventImageView.setImage(new Image(selectedFile.toURI().toString()));
       // this.eventImageView = new ImageView(new Image(selectedFile.toURI().toString()));

    }




    //TODO: implement saveContent() method (if success follow the save Event protocol; if error call showError(String msg))
    //User presses save/create/edit button
    //use setters on the event object to set the data you collected from the form
    //update setters on the Event Model to throw Exceptions if the data is not valid
    //if no exception catch follow the protocol to create or update an event in the DB (only if the data is valid!)
    //PROTOCOL TO SAVE EVENT IN DB: call this methods in this order
    //1. event.save() => save the Event object into the DB
    //2. this.dataSaved(event) => tell the rest of the system that this object was changed
    //3. this.closePopup();
    //To get the current user use: getCurrentUser(); this returns the object you should use in createdBy property of an event

    public void saveContent() {
        if(!validInput()) {
            System.out.println("rnterd");
            showError(errMessage(), ErrorMessageType.WARNING, 5);
        }
        else {
            try {

                event.setName(eventNameText.getText());
                event.setDescription(eventDescriptionText.getText());
                ZoneId defaultZoneId = ZoneId.systemDefault();
//                event.setCreationDate(Date.from(selectStartingDate.getValue().atStartOfDay(defaultZoneId).toInstant()));
                //event.setStartDate(Date.from(start.getDateTimeValue().atZone(defaultZoneId).toInstant()));
                event.setStartDate(start.getDateTimeValue());
                ZoneId defaultZoneId1 = ZoneId.systemDefault();
//                event.setEndDate(Date.from(selectEndingDate.getValue().atStartOfDay(defaultZoneId1).toInstant()));
                //event.setEndDate(Date.from(end.getDateTimeValue().atZone(defaultZoneId1).toInstant()));
                event.setEndDate(end.getDateTimeValue());
                event.setImage(selectedFile.toURI().toURL().toString());
                event.setCreatedBy(getCurrentUser());
                event.save();
                this.dataSaved(event);
                setDirty(false);
                closePopup();
            } catch (Exception e) {

                showError(e.getMessage(), ErrorMessageType.WARNING);
            }
        }
        /*
    Not completely implemented yet!
     */

    }

    public void createDescription() {
        this.description = eventDescriptionText.getText();
    }

    public void createEventName() {
        this.eventName = eventNameText.getText();
    }
    //TODO: create constructor that have the TimelineID as parameter (so we know which timeline to edit) -> create property timelineID;


    @Override
    public void setView(Node view) {
        //We change the form here to create an "edit form" or a "create form"
        if(formMode == PopupContentType.CREATE_EVENT) {
            //change labels and text so it's a create event form
        } else {
            windowTitle.setText("Edit Event");
            saveEventButton.setText("Save");
            eventNameText.setText(event.getName());
            //change labels and text so it's a edit event form
        }
        super.setView(view);
    }

    private String errMessage(){
        if(eventNameText.getText().isEmpty())
            return "Event must have a name";
        if(start.getValue()==null)
            return "Please select the starting date";
        if(end.getValue()==null)
            return "Please select the ending date";
        if(eventDescriptionText.getText().isEmpty())
            return  "Event must have a description";
        else return "";
    }

     private boolean validInput(){
        return !(eventNameText.getText().isEmpty() || start.getValue() == null
                || end.getValue() == null || eventDescriptionText.getText().isEmpty());
     }
    @Override
    public void refreshView() {
        //No need implementation in this class
    }
}