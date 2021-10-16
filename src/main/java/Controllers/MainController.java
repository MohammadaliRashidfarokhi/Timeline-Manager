package Controllers;

import Utils.DatabaseController;
import Models.Timeline;
import Utils.ErrorMessageType;
import Utils.PopupContentType;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import Utils.Utils;


import java.io.File;
import java.util.List;


import Models.Event;
import Models.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.Node;

import java.util.stream.Collectors;
//MainController is passed to the FXML loader and then we fetch the FXML (Main.fxml) resource;
//The properties with @FXML annotation are injected by the FXML loader so we can access those GUI elements;

public class MainController extends GeneralController {

    //constants
    public final double WINDOW_WIDTH = 1050.0;
    public final double WINDOW_HEIGHT = 650.0;
    CreateEditAccountController Account;

    //timeline Controller is responsible of the view with the details of
    //a timeline and the ScrollPane with the actual timeline
    private TimelineController timelineController;

    //Popup Controller shows the popup and inserts the content of the popup inside
    //is also responsible to close the popup; allows communication between Main controller and ContentPopupController
    private PopupController popupController;


    //current loggedin user; null if not logged in
    private User currentUser; // = User.getTestUser();

    //list of timelines
    private List<Timeline> timelineList;

    //to indicate if we are in edit mode or view mode
    private boolean editMode = false;

    //Login page
    AnchorPane loginPage;
    // the confirmation popUp
    Parent confirmationPopUp;

    private  boolean userDropMenuShown = false;


    Button editAccount;
    Button logOut;

    //errorViewMessage graphic element (when displaying)
    ErrorMessageController errorMessageController;

    ////////////////////////////////////
    ///   FXML injected properties   ///
    ////////////////////////////////////

    @FXML // fx:id="main"
    private StackPane main;

    @FXML // fx:id="add_timeline_btn"
    private Button add_timeline_btn;

    @FXML // fx:id="timelines_list"
    private ListView<Timeline> timelines_list;

    @FXML
    private StackPane AvatarBox;

    @FXML
    private Label AvatarLetter;

    @FXML // fx:id="timeline_name"
    private Label timeline_name;

    @FXML // fx:id="edit_timeline_button"
    private Button edit_timeline_button;

    @FXML // fx:id="add_event_button"
    private Button add_event_button;

    @FXML // fx:id="loginButton"
    private Button loginButton;

    @FXML // fx:id="timeline_description"
    private TextFlow timeline_description;

    @FXML // fx:id="timeline_author"
    private Label timeline_author;

    @FXML // fx:id="view_edit_toggle"
    private StackPane view_edit_toggle;

    @FXML // fx:id="timeline_scroll_pane"
    private ScrollPane timeline_scroll_pane;

    @FXML // fx:id="timeline_page_box"
    private StackPane timeline_page_box;

    @FXML
    private Pane switch_head;

    @FXML
    private Label switch_edit_text;

    @FXML
    private Label switch_view_text;
    @FXML
    private Circle UserImageCircle;

    ///// DEBUG BUTTONS ON THE BOTTOM RIGHT //////
    @FXML
    private Button debugButton1;
    @FXML
    private Button debugButton2;
    @FXML
    private Button debugButton3;
    @FXML
    private Button debugButton4;
    //// END DEBUG BUTTONS


    public MainController() {
        //START DATABASE (before GUI)
        try {
            DatabaseController.startDB();
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    //the initialize() method is called after the constructor
    //and after the @FXML annotated properties being injected
    @FXML
    public void initialize() {
        //TODO: (optional) check if user is logged in (if we want for a "user session" to persist after the program being closed)
        AvatarBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showDropDown();
            }
        });
        //TODO: load list of timelines (async) => call open openTimeline(int timelineID) when an element is clicked/selected

        //Load list of timelines from DB
        System.out.println("load from DB");
        timelineList = Timeline.loadAll();

        /////TESTING /////
        //for(int i=0; i<10; i++) {
        //    timelineList.add(Timeline.getTimelineTest());
        //}
        /////END TESTING /////

        //Refresh View after loading
        this.refreshView();

        //timelines_list.setItems(FXCollections.observableArrayList(timelineList));

        //create actions
        add_timeline_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //TODO: Load actually new timeline form
              Object timeLine=(Object) new Timeline();
                openPopUP(PopupContentType.CREATE_TIMELINE, timeLine);
            }
        });

        //Timeline List Selector
        timelines_list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        timelines_list.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Timeline> ov, Timeline old_val, Timeline new_val) -> {
            Timeline selectedItem = timelines_list.getSelectionModel().getSelectedItem();
            //int index = timelines_list.getSelectionModel().getSelectedIndex();
            openTimeline(selectedItem);
        });
        timelines_list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Timeline t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) {
                    setText(null);
                    setStyle("");
                } else {
                    //setText(t.getTitle());
                    VBox container = new VBox();
                    container.setPadding(new Insets(10,10,10,10));
                    Text title = new Text();
                    if(t.getTitle()!=null) {
                        title.setText(t.getTitle());
                    }
                    Font font = Font.font(23.0);
                    title.setFont(font);
                    Label authorName = new Label();
                    authorName.setStyle("-fx-label-padding: 0 10 0 0");
                    //protect against null pointers
                    if(t.getCreatedBy()!=null && t.getCreatedBy().getFullname() != null) {
                        authorName.setText("by "+ Utils.getFirstLastName(t.getCreatedBy().getFullname()));
                    }
                    container.getChildren().addAll(title,authorName);
                    setGraphic(container);
                }
            }
        });


        //delete on press of d on timeline
        // listener on list w/ selected item and "d" pressed
        //check if d pressed -> check if userid and timeline_fk match, if they do, crud query to delete and remove from list
        //possibly confirmation?
        //possibly replaced with button
        //replace 294 condition with current user logged in
        timelines_list.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().getChar().toLowerCase().equalsIgnoreCase("d")) {
                    if (User.getTestUser().getId() == timelines_list.getSelectionModel().getSelectedItem().getCreatedBy().getId()) {
                        Timeline toRemove = timelines_list.getSelectionModel().getSelectedItem();
                        timelineList.remove(toRemove);
                        toRemove.delete();
                        refreshView();
                    }
                }
            }
        });


        //toggle EDIT/VIEW mode
        view_edit_toggle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                toggleEditMode();
            }
        });

        //login Button
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showLoginSignupPage();
            }
        });

        ///// DEBUG BUTTONS ON THE BOTTOM RIGHT //////

        debugButton1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Object timeLine=(Object) new Timeline();
                openPopUP(PopupContentType.EDIT_TIMELINE, Timeline.getTimelineTest());
            }
        });
       
        debugButton2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //openTimeline(new Timeline("End of the world timeline :)", "What were you expenting?", null, null, "hour", 0, 0, 0, User.getTestUser()));
                openPopUP(PopupContentType.EDIT_TIMELINE,Timeline.getTimelineTest());
            }
        });

        //place show event temporary in debugButton3, this will be placed in timeline later when an event be clicked
        debugButton3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //openTimeline(new Timeline("End of the world timeline :)", "What were you expenting?", null, null, "hour", 0, 0, 0, User.getTestUser()));
                Object event=(Object) new Event();
                //event = null;
                openPopUP(PopupContentType.VIEW_EVENT,event);

            }
        });
        
        debugButton4.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent actionEvent) {
        		openPopUP(PopupContentType.EDIT_ACCOUNT,currentUser);
        	}
        });

        ///// END DEBUG BUTTONS //////
    }

    //contentToOpen defines which form or content we want to open (this can be changed to Enum later on)
    public void openPopUP(PopupContentType contentToOpen, Object editingObject) {
        //TODO: to be decided, callback function to update the view after the object being edited???
        // or object that has a predefined method that we call after an update?

        //this only for check whether the event is empty
        boolean emptyEvent = false;
        try {
            ContentPopupController popupContentController;
            if (contentToOpen == PopupContentType.CREATE_EVENT || contentToOpen == PopupContentType.EDIT_EVENT) {
                // the same controller and view is used for both creation and edition
                // this allows component reuse
                popupContentController = new CreateEditEventController(contentToOpen, (Event) editingObject);
            } else if (contentToOpen==PopupContentType.CREATE_ACCOUNT || contentToOpen == PopupContentType.EDIT_ACCOUNT) {
                // the same controller and view is used for both creation and edition
                // this allows component reuse
                popupContentController = new CreateEditAccountController(contentToOpen, (User) editingObject,null);
            } else if(contentToOpen == PopupContentType.CREATE_TIMELINE || contentToOpen == PopupContentType.EDIT_TIMELINE) {
                // the same controller and view is used for both creation and edition
                // this allows component reuse
                popupContentController = new CreateEditTimelineController(contentToOpen, (Timeline) editingObject);
            }
            else if (contentToOpen == PopupContentType.VIEW_EVENT){
                // the same controller and view is used for both creation and edition
                // this allows component reuse
                //check event is empty
                if (editingObject == null){
                    showError("No such event",ErrorMessageType.ERROR);
                    emptyEvent = true;
                }
                popupContentController = new ViewEventController(contentToOpen, (Event) editingObject);
            }
            else {
                throw new Exception("Not implemented");
            }
            // We pass the content controller and the Main controller (this) to the Popupcontroller
            // by passing the reference os this object, we can call methods of the Main controller inside the popupcontroler
            // ex: to remove the popup from the GUI after closing it
            popupController = new PopupController(popupContentController, this);
            //We add the popup element to the GUI

            if (!emptyEvent)
            addPopup(popupController.getPopupView());
        }catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    //used by ESCAPE key press to close popup
    public void closePopup() {
        if(popupController!=null) {
            popupController.closePopup();
            popupController = null;
        }
    }

    //We add the popup element to the GUI
    private void addPopup(Node popupView) {
        StackPane back = (StackPane) main.lookup("#main");
        back.getChildren().add(popupView);
    }

    @Override
    public void addConfirmationPopUp(String title, String text) {
        //We add the popup element to the GUI
        if(confirmationPopUp==null) {
        System.out.println("add Confirmation Popup MAin Controller");
        try {
            confirmationPopUp = FXMLLoader.load(getClass().getResource("/views/ConfirmationMessage.fxml"));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
            Text titleLabel = (Text) confirmationPopUp.lookup("#ConfirmationTitle");
            Label textLabel = (Label) confirmationPopUp.lookup("#OptionalId");
            Button buttonNo = (Button) confirmationPopUp.lookup("#ButtonNo");
            Button buttonYes = (Button) confirmationPopUp.lookup("#ButtonYes");
            titleLabel.setText(title);
            textLabel.setText(text);
            buttonNo.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    removeConfirmationPopUp();
                }
            });
            buttonYes.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //closePopup();
                    removePopup();
                    removeConfirmationPopUp();
                }
            });
            StackPane back = (StackPane) main.lookup("#main");
            back.getChildren().add(confirmationPopUp);
        }else{
            System.err.println("A confirmation popup is already shown! You cannot show 2 confirmation popups at the same time");
        }
    }

    //remove the confirmation popup element from GUI
    public void removeConfirmationPopUp(){
        if(confirmationPopUp!=null) {
            main.getChildren().remove(confirmationPopUp);
            confirmationPopUp = null;
        }
    }

    //the popup controller should be able to call this method so it removes itself from the GUI
    public void removePopup() {
        main.getChildren().remove(main.lookup("#popup_container"));
    }

    //By creating a method showError here we can show an error to the user in any part of the system
    //example: the forms to create or edit something can call this method
    //by centralizing the error message in one method to the whole system we reduce duplication of code
    //and its easier for "maintenance of code"
    @Override
    public void showError(String msg, ErrorMessageType errorType) {
        showError(msg, errorType, 5);
    }

    @Override
    public void showError(String msg, ErrorMessageType errorType, int duration) {
        //CREATE OR UPDATED CONTROLLER
        if(errorMessageController == null) {
            errorMessageController = new ErrorMessageController(msg, errorType, duration, this);
        } else {
            errorMessageController.setErrorType(errorType);
            errorMessageController.setMsg(msg);
            errorMessageController.refreshView();
            errorMessageController.setDuration(duration);
        }
    }

    public void hideError() {
        if(errorMessageController!=null) {
            errorMessageController.hideError();
        }
    }

    //after clicking in one of the timelines we load it and show it's contents on the right of the screen + timeline itself
    public void openTimeline(Timeline timeline) {
        //create controller (pass timeline id to controller)
        if(timelineController!=null && timelineController.getView()!=null) {
            //remove the previous view first
            timeline_page_box.getChildren().removeAll(timelineController.getView());
        }
        timelineController = new TimelineController(timeline, editMode, this);
        timeline_page_box.getChildren().add(timelineController.getView());
    }

    //handle the click of an user in the switch Edit/View mode
    public void toggleEditMode() {
        //TODO: should a user be logged in to edit? or must be an admin?
        if(currentUser != null) { //this just checks is the user id logged it or not
            editMode = !editMode;
            //pass edit mode to timelineController (so it can addapt it self)
            if(timelineController!=null) {
                timelineController.setEditMode(editMode);
            }
            //TODO: change styling of toogle
            if(editMode) {
                view_edit_toggle.setMargin(switch_head, new Insets(0, 46, 0, 0));
                switch_edit_text.setStyle("-fx-text-fill: #FFFFFF");
                switch_view_text.setStyle("-fx-text-fill: #888888");
            } else {
                view_edit_toggle.setMargin(switch_head, new Insets(0, 2, 0, 0));
                switch_edit_text.setStyle("-fx-text-fill: #888888");
                switch_view_text.setStyle("-fx-text-fill: #FFFFFF");
            }
        } else {
            //TODO:
            //show login page?
            //show error that say the user need to be logged in?
            showLoginSignupPage();
            showError("You must be logged in to edit",ErrorMessageType.WARNING);
        }
    }

    //show login + create account form
    public void showLoginSignupPage() {
        AnchorPane loginCreateAccountPage = new AnchorPane();
        loginCreateAccountPage.setStyle("-fx-background-color: white");

        LoginController loginController = new LoginController(this);
        CreateEditAccountController createAccountController = new CreateEditAccountController(PopupContentType.CREATE_ACCOUNT,null,this);

        Node loginForm = loginController.getView();
        Node createAccountForm = createAccountController.getView();

        Button backButton = new Button();
        backButton.setText("<  Back");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 15px;");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeLoginPage();
            }
        });

        loginCreateAccountPage.getChildren().addAll(loginForm,createAccountForm,backButton);

        //Login form position
        loginCreateAccountPage.setLeftAnchor(loginForm,0.0);
        loginCreateAccountPage.setTopAnchor(loginForm,0.0);
        loginCreateAccountPage.setBottomAnchor(loginForm,0.0);
        loginCreateAccountPage.setRightAnchor(loginForm,WINDOW_WIDTH/2);

        //Create Account position
        loginCreateAccountPage.setRightAnchor(createAccountForm,0.0);
        loginCreateAccountPage.setTopAnchor(createAccountForm,0.0);
        loginCreateAccountPage.setBottomAnchor(createAccountForm,0.0);
        loginCreateAccountPage.setLeftAnchor(createAccountForm,WINDOW_WIDTH/2);

        //Back button position
        loginCreateAccountPage.setLeftAnchor(backButton, 40.0);
        loginCreateAccountPage.setTopAnchor(backButton, 10.0);

        loginPage = loginCreateAccountPage;
        //add login page to view
        main.getChildren().add(loginCreateAccountPage);
    }

    public void removeLoginPage() {
        if(loginPage!=null) {
            main.getChildren().removeAll(loginPage);
        }
    }

    //TODO: After popup world
    //callback when an object is saved on the DB
    //this method is called from the Forms Controllers
    //here, we refresh all the views that that object can be used

    @Override
    public void dataSaved(Object elementSaved) {
        if(elementSaved instanceof Event) {
            //update Timeline Scroll Pane (update or add event, if it's in the viewport)
        } else if(elementSaved instanceof User) {
            //update currentUser and main GUI
            setUser((User) elementSaved);
            this.refreshView();
        } else if(elementSaved instanceof Timeline) {
            //refresh main view and Timelinepage
            if(timelineList.contains(elementSaved) == false) {
                timelineList.add((Timeline) elementSaved);
                openTimeline((Timeline) elementSaved);
            }else{
                timelineController.refreshView();
            }
            this.refreshView();
        }
    }

    public void setUser(User user){
        this.currentUser=user;
    }

    @Override
    public User getCurrentUser() {
        return this.currentUser;
    }

    //handle the click of the user in the logout button

    public void logoutUser() {
        setUser(null);
    }

    @Override
    public void refreshView() {
        //after login we need to update this view
            //hide login button
            //show avatar of user + username
        //show the new list view

        if (currentUser!=null){
            AvatarBox.setVisible(true);
            loginButton.setVisible(false);
            if (currentUser.getImage()==null|| currentUser.getImage().equals("")){
                AvatarLetter.setText(String.valueOf(currentUser.getFullname().charAt(0)));
                UserImageCircle.setVisible(false);
            }else {
                AvatarLetter.setVisible(false);
                
                /* some TESTING
                Image image = new Image(currentUser.getImage());
                ImagePattern userImagePattern= new ImagePattern(image);
                UserImageCircle.setFill(userImagePattern); */
                
                File userImageFile = new File(currentUser.getImage());
                ImagePattern userImagePattern= new ImagePattern(new Image(userImageFile.toURI().toString()));
                UserImageCircle.setFill(userImagePattern);
            }
        }else {
            AvatarBox.setVisible(false);
            loginButton.setVisible(true);
        }
        
        ObservableList<Timeline> items = FXCollections.observableArrayList(timelineList.stream().collect(Collectors.toList()));
        timelines_list.setItems(items);
    }

    //is inside this element we insert error messages and popups
    public StackPane getMainGraphicElement() {
        return main;
    }

    // Little Menu for userLogOut
    public void showDropDown(){
        if (userDropMenuShown==false){

            try {


                VBox root = FXMLLoader.load(getClass().getResource("/views/UserOptionsDropDownMenu.fxml"));

                StackPane back = (StackPane) main.lookup("#main");
                back.getChildren().add(root);
                back.setMargin(root,new Insets(30,0,0,0));
                back.setAlignment(root,Pos.TOP_LEFT);

                editAccount = (Button) root.lookup("#EditAccountButton");
                logOut = (Button) root.lookup("#LogOutButton");

            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            editAccount.setOnAction(event -> {
                // It was tested and worked.
                openPopUP(PopupContentType.EDIT_ACCOUNT,currentUser);
                showDropDown();
                refreshView();
            } );
            logOut.setOnAction(event -> {
                // It was tested and worked.
                logoutUser();
                showDropDown();
                refreshView();
            });
            userDropMenuShown =true;

        }else{
            main.getChildren().remove(main.lookup("#DropDownUserMenu"));
            userDropMenuShown =false;
        }
    }
}
