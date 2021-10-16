package Controllers;

import Models.Timeline;
import Utils.ErrorMessageType;
import Utils.PopupContentType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.mariadb.jdbc.internal.com.read.resultset.SelectResultSet;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

//UC1 - Create timeline
public class CreateEditTimelineController extends ContentPopupController {

    Label newTimeline;
    Button cancelBtn, createBtn;
    TextField timelineNewName;
    ComboBox timeUnit;
    DatePicker startDate, endDate;

    private PopupContentType formMode;
    private Timeline timeline;
    private PopupController popupController;

    //formMode can have the values: PopupContentType.CREATE_TIMELINE or PopupContentType.EDIT_TIMELINE
    //this is to reuse the code to create or edit an event (no need for double of the work ;) )
    public CreateEditTimelineController(PopupContentType formMode, Timeline timeline) {
        this.formMode = formMode;
        //TODO: if timeline is null create a new timeline object and assign it to this.timeline
        this.timeline = timeline;
        //load fxml resource
        try {
            //Load new timeline / edit timeline form
            VBox createEditForm = FXMLLoader.load(getClass().getResource("/views/TimelinePopup.fxml"));

            //Extract GUI elements that are going to be target of user actions: TextFields, buttons, etc
            newTimeline = (Label) createEditForm.lookup("#WindowTitle");
            cancelBtn = (Button) createEditForm.lookup("#cancelButton");
            cancelBtn.setOnAction(event -> {
               // if(isDirty()){

               // }
                closePopup();

            });
            createBtn = (Button) createEditForm.lookup("#createButton");
         //   createBtn.setDisable(true);
         //   if(!notValidInput())
         //       createBtn.setDisable(false);
            //we can make button disable if user didnt fill one/some fields
            createBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    saveContent();
                }
            });
            timelineNewName = (TextField) createEditForm.lookup("#timelineName");
            timeUnit = (ComboBox) createEditForm.lookup("#timeUnitBox");
            startDate = (DatePicker) createEditForm.lookup("#startDatePicker");
            endDate = (DatePicker) createEditForm.lookup("#endDatePicker");
            //TODO: Set listeners on TextFields and call setDirty(true)
            //the setDirty(true) method is already implemented (in ContentPopupController)
            timelineNewName.textProperty().addListener((observable,oldValue,newValue)->{
                if(!timelineNewName.getText().isEmpty()) {
                    if (!newValue.equals(oldValue))
                        setDirty(true);
                }
              //   if(!timelineNewName.getText().isEmpty())
                //     setDirty(true);
            });
            timeUnit.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue)->{

                if(timeUnit.getValue() != null && oldValue != null) {
                    if (!oldValue.equals(newValue))
                        setDirty(true);
                }
            });
            startDate.valueProperty().addListener((ov, oldValue , newValue)->{
                if(!newValue.equals(oldValue))
                    setDirty(true);
              //  if(startDate.getValue()==null)
                //    setDirty(true);
            });

            endDate.valueProperty().addListener((ov, oldValue , newValue)->{
               if(endDate.getValue()!=null) {
                   if (!newValue.equals(oldValue))
                       setDirty(true);
               }
              //  if(endDate.getValue()==null)
                //    setDirty(true);
            });

            setView(createEditForm);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println(CreateEditAccountController.class.getName() + ": FXML resource not found");
        }
    }

    //TODO: implement saveContent() method (if success call FOLLOW THE NEW SAVE TIMELINE PROTOCOL; if error call showError(String msg))
    //User presses save/create/edit button
    //use setters on the Timeline object to set the data you collected from the form
    //update setters on the Timeline Model if needed to throw Exceptions if the data is not valid
    //if no exception was catch follow the protocol to create or update an timeline in the DB (only if the data is valid!)
    //SAVE TIMELINE PROTOCOL: call this methods in this order
    //1. timeline.save() => save the Timeline object into the DB
    //2. this.dataSaved(timeline) => tell the rest of the system that this object was changed
    //3. this.closePopup();
    //To get the current user use: getCurrentUser(); this returns the object you should use in createdBy property of an timeline

    public void saveContent() {

        if (!validInput()) {

         showError(errMsg(),ErrorMessageType.WARNING);
        } else {
            try {
                if (timeline == null) {
                    timeline = new Timeline();
                }
                timeline.setTitle(timelineNewName.getText());
                timeline.setCreatedBy(getCurrentUser());
                ZoneId defaultZoneId = ZoneId.systemDefault();

                timeline.setTimeUnit(timeUnit.getValue().toString());
               // System.out.println("time unit is" + timeUnit.getValue().toString());
                // Date startDate1 = Date.from(startDate.getValue().atStartOfDay(defaultZoneId).toInstant());
                timeline.setStartDate(Date.from(startDate.getValue().atStartOfDay(defaultZoneId).toInstant()));
              //  if (endDate.getValue() != null) {
                    ZoneId defaultZoneId1 = ZoneId.systemDefault();
                    Date endDate1 = Date.from(endDate.getValue().atStartOfDay(defaultZoneId1).toInstant());
                    timeline.setEndDate(endDate1);
               // }
               // if(formMode == PopupContentType.CREATE_TIMELINE)
                    setDirty(false);
                 timeline.save();
                 this.dataSaved(timeline);

                closePopup();
            } catch (RuntimeException ex) {
                showError(ex.getMessage(), ErrorMessageType.ERROR);
            }catch(Exception e){
                showError(e.getMessage(), ErrorMessageType.ERROR);
            }
            //TODO:missing endDAte

        }
    }
    private String errMsg() {
        if (timelineNewName.getText().isEmpty())
            return "check timeline name";
       else if(timeUnit.getSelectionModel().isEmpty())
            return "check time unit";
       else if(startDate.getValue()==null)
            return "check start date";
       else if(endDate.getValue()==null)
           return "check end date";
       else return "";

    }

    //TODO: I think is preferable to use the setters of the Timeline Model
    //(if the data is not valid, the setter should throw an Exception that would be catch in this controller)
    //returns true if all inputs are valid
  public boolean validInput() {
      /* if(timelineNewName.getText().isEmpty())
           throw new RuntimeException("Timeline must has a name(happy now????)");
       if(timeUnit.getValue()==null)
           throw new RuntimeException("Time unit can not be empty");
       else if(startDate.getValue()==null)
           throw new RuntimeException("Start date can not be empty");
       else if(endDate.getValue().isBefore(startDate.getValue()))
           throw new RuntimeException("End date should be after start date ");*/
        return !(timeUnit.getSelectionModel().isEmpty()||
               startDate.getValue()==null || endDate.getValue()==null);
     //  else
       //    return true;
    }


    @Override
    public void setView(Node view) {//should be implemented in the next sprint
        //We change the form here to create an "edit form" or a "create form"
        if (formMode == PopupContentType.CREATE_TIMELINE) {//creatButton.setDisable(true) if one/more fields is empty/not selected
            //change labels and text so it's a create account form

        } else {
            //change labels and text so it's a edit account form
            newTimeline.setText("Edit Timeline");
            createBtn.setText("Save");
            timelineNewName.setText(timeline.getTitle());
            timeUnit.getSelectionModel().select(timeline.getTimeUnit());
            Instant instant = timeline.getStartDate().toInstant();
            ZoneId defaultZoneId = ZoneId.systemDefault();
            LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
            startDate.setValue(localDate);
            if(endDate.getValue()!=null) {
                Instant instant1 = timeline.getStartDate().toInstant();
                ZoneId defaultZoneId1 = ZoneId.systemDefault();
                LocalDate localDate1 = instant1.atZone(defaultZoneId1).toLocalDate();
                endDate.setValue(localDate1);
            }
        }
        super.setView(view);
    }

    @Override
    public void refreshView() {
        //no needed to be implemented in this case
    }

    public void createButtonHandler(ActionEvent actionEvent) {
    }

    public void cancelButtonHandler(ActionEvent actionEvent) {
    }

    public void textInputByUser(KeyEvent keyEvent) {
    }

    //we need to have a popup confirmation message to ask user "do u want to save ur changes?"
}

