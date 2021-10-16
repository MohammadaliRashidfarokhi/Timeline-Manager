package Controllers;
import javafx.fxml.FXML;

import Models.Event;
import Models.Timeline;
import Utils.PopupContentType;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;

public class TimelineController extends GeneralController {

    Label timelineNameTxt, timelineAuthorTxt;
    Button editTimelineBtn, addEventBtn;
    TextFlow timelineDescriptionTxt;
    ScrollPane timelineScrollPane;
    AnchorPane timelinePage;

    private boolean editMode;
    private Timeline timeline;
    private Event event;

    private StackPane timelinePositioner;
    private HBox timelineBackgroundHolder;


    TimelineController(Timeline timeline, boolean editMode, MainController parent) {
        this.editMode = editMode;
        this.timeline = timeline;
        //this.event = event;
        //the parent is needed to open popus (so we can have access to the openPopUP method
        setParentController(parent);
        //load fxml resource and get access to GUI elements for future use
        try {
            timelinePage = FXMLLoader.load(getClass().getResource("/views/TimelinePage.fxml"));
            timelineNameTxt = (Label) timelinePage.lookup("#timeline_name");
            editTimelineBtn = (Button) timelinePage.lookup("#edit_timeline_button");
            addEventBtn = (Button) timelinePage.lookup("#add_event_button");
            timelineDescriptionTxt = (TextFlow) timelinePage.lookup("#timeline_description");
            timelineAuthorTxt = (Label) timelinePage.lookup("#timeline_author");
            timelineScrollPane = (ScrollPane) timelinePage.lookup("#timeline_scroll_pane");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //UPDATE GUI WITH OR WITHOUT EDIT+ADD BUTTONS
        setEditMode(editMode);
        setView(timelinePage);
        refreshView();

            //EDIT Timeline BUTTON
            editTimelineBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    //TODO: open popup with edit timeline form
                    MainController parent = (MainController) getParentController();
                    parent.openPopUP(PopupContentType.EDIT_TIMELINE, timeline);
                }
            });

            //ADD EVENT BUTTON
            addEventBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    //TODO: open popup with create event form
                    MainController parent = (MainController) getParentController();
                   // Object event = (Object) new Event();
                    parent.openPopUP(PopupContentType.CREATE_EVENT, event);

                }
            });

        createTimeline();
    }

    public void setTimelineTitle(String title) {
        timelineNameTxt.setText(title);
    }

    public void setTimelineDescription(String description) {
        timelineDescriptionTxt.getChildren().add(new Text(description));
    }

    public void setTimelineAuthorName(String authorName) {
        timelineAuthorTxt.setText("by "+authorName);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        //UPDATE GUI WITH OR WITHOUT EDIT+ADD BUTTONS
        if (!editMode) {
            editTimelineBtn.setVisible(false);
            addEventBtn.setVisible(false);
        } else {
            editTimelineBtn.setVisible(true);
            addEventBtn.setVisible(true);
        }
    }

    //called to update info displayed
    @Override
    public void refreshView() {
        if(timeline!=null) {
            timelineNameTxt.setText(timeline.getTitle());
            timelineDescriptionTxt.getChildren().clear();
            Text description = new Text(timeline.getDescription());
            description.setFill(Paint.valueOf("#707070"));
            timelineDescriptionTxt.getChildren().add(description);
            if(timeline.getCreatedBy()!=null) {
                timelineAuthorTxt.setText("by "+timeline.getCreatedBy().getUsername());
            } else {
                timelineAuthorTxt.setText("by ----");
            }
        }
    }


    ////Timeline system////
    private boolean busy = false; //when loading events make it busy
    private String unitLeft = ""; //fixed unit/label on the left
    private String unitRight = ""; //fixed unit/label on the right
    private double resolution = 1.0; //1 int or 1 second == to pixels => in the absolute scale this is calculated after defining the "scale"
    private String timelineType = "absolute"; //absolute or relative
    private long timelineTimeStart = 0; //to subtract to events and position them
    private long leftViewportEdge = 0; //when scrolling to the left we need to know where is the edge
    private double viewportWidth = 1000;
    private String scale = ""; //day (with hours and minutes); month (with days); year (with months); decade (with years)
    private boolean directionRight = true; //right or left; direction of the scroll
    //to calculate scrolled length by the user
    private double timelineWidth = 1000.0; //updated when we recreate the timeline
    private double timelinePageWidth = 0; //calculated after creating the first timeline page
    //create 4 pages of the background => each page have the representation of the selected scale

    //1. start by inserting 4 pages on the timeline (calculate width of the timelie)
    //2. load events with date / int staring in that range

    StackPane[] backgroundPages = new StackPane[4];

    private void createTimeline() {
        //CREATE HOLDERS (Add them to scene builder later)
        timelinePositioner = new StackPane();
        timelinePositioner.minHeight(510);
        timelineBackgroundHolder = new HBox();
        timelineBackgroundHolder.setMaxHeight(40);
        timelineBackgroundHolder.setMinHeight(40);
        timelineBackgroundHolder.setPrefHeight(40);
        timelinePositioner.getChildren().add(timelineBackgroundHolder);
        timelinePositioner.setAlignment(timelineBackgroundHolder,Pos.CENTER_LEFT);
        timelinePositioner.setMargin(timelineBackgroundHolder,new Insets(235,0,0,0));
        timelineScrollPane.setContent(timelinePositioner);
        //END SCENEBUILDER STUFF

        //create event listeners
        timelineScrollPane.hvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            double scrolledLenght = newValue.doubleValue() * timelineWidth;
            System.out.println("User scrolled: "+scrolledLenght);
            //TODO: Load event elements into the Scroll Pane
            //TODO: load data from DB (async) => then change the content (org.riversun.promise.Promise promises??)
            //and event should only be loaded if its in the current (or very adjacent) view frame -> more memory efficient
            //we can create 3 "pages of timeline that are going to be updated and repositioned while the user scrolls
            //each "page" has the width of the "view frame" and inside a timeline bar + event elements (inside a StackPane)
        });

        //FOR TESTING
        Calendar startTimelineDate = GregorianCalendar.getInstance(); // creates a new calendar instance
        startTimelineDate.setTime(new Date());
        //Make it start at midnight
        startTimelineDate.set(Calendar.HOUR_OF_DAY,0);
        startTimelineDate.set(Calendar.MINUTE,0);
        startTimelineDate.set(Calendar.SECOND,0);

        //where our timeline is going to live on
        //StackPane timelineplacer = new StackPane();
        //timelineplacer.setMinHeight(510);

        //StackPane timelinePositioner;
        //HBox timelineBackgroundHolder;
        for(int i = 0; i<4; i++) {
            backgroundPages[i] = createTimelineBackgroundPage(startTimelineDate.getTime(), "hour", i);
            //Increase total width of the timeline
            timelineWidth+=timelinePageWidth;
        }
        timelineBackgroundHolder.getChildren().addAll(backgroundPages);
        //from today midnight to 4 days after (use the background pages for this)
        //loadEvents()

    }

    StackPane createTimelineBackgroundPage(Date start, String scale, int page) {
        System.out.println("CREATING PAGE "+page+" of the scale");
        StackPane timeline = new StackPane();
        timeline.setStyle("-fx-background-color: #434FE2");


        boolean showSmallMarkers = true;
        double pageWidth = 1000;
        //if(timelinePageWidth>0) {
        //    pageWidth = timelinePageWidth;
        //}
        //increase the total width of the timeline

        double pageHeight = 40;

        timeline.setMinHeight(pageHeight);
        timeline.setMaxHeight(pageHeight);
        //timeline.setMinWidth(pageWidth);

        double spaceBetweenMarkers = 5.0;
        HBox top_timemarkers = new HBox(spaceBetweenMarkers); // spacing = 8
        HBox bottom_timemarkers = new HBox(spaceBetweenMarkers); // spacing = 8


        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(start);
        //Make it start at midnight
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);

        if(scale.equals("hour") && page>0) {
            //each page is one day
            calendar.add(Calendar.DAY_OF_MONTH,page);
        }

        int bigMarkerFromXandX = 30;
        List<Line> top_markers = new ArrayList<>();
        List<Line> bottom_markers = new ArrayList<>();
        double left = 0;
        int hour = 0;
        int numberOfMarkerPerPage = 24; //number of hours a day
        for (int i = 0; i < (30 * numberOfMarkerPerPage); i++) {
            Line lnTop;
            Line lnBottom;
            if((i +1) % bigMarkerFromXandX == 0) {
                lnTop = new Line(0.0,0.0,0.0,10.0);
                lnTop.translateYProperty().set(-14.0);
                lnTop.setStrokeWidth(2);

                lnBottom = new Line(0.0,0.0,0.0,10.0);
                lnBottom.translateYProperty().set(14.0);
                lnBottom.setStrokeWidth(2);

                Pane txtPane = new Pane();
                double txtWidth = 100.0;
                double txtHeigth = 29.0;
                double offsetx = 2;
                txtPane.setPrefSize(txtWidth, txtHeigth);

                String hstr = ""+hour;
                if(hour<10) {
                    hstr = "0" + hstr;
                }
                String label = hstr + ":00";
                String day = null;
                if(label.equals("00:00")) {
                    day = calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH)+1);
                }
                Label text = new Label(label);
                text.setPrefWidth(txtWidth);
                text.setMinWidth(txtWidth);
                text.setMaxWidth(txtWidth);
                text.setAlignment(Pos.CENTER);
                text.setStyle("-fx-text-fill: #F7F7F7");
                if(scale.equals("hour")) {
                    hour++;
                    calendar.add(Calendar.HOUR,1);
                    txtPane.getChildren().add(text);
                    if(day!=null) {
                        Label daylabel = new Label(day);
                        daylabel.setStyle("-fx-font-size: 10; -fx-text-fill: #F7F7F7");
                        daylabel.setPrefWidth(txtWidth);
                        daylabel.setMinWidth(txtWidth);
                        daylabel.setMaxWidth(txtWidth);
                        daylabel.setAlignment(Pos.CENTER);

                        Pane txtPane2 = new Pane();
                        txtPane2.setPrefSize(txtWidth, 15);
                        txtPane2.getChildren().add(daylabel);
                        timeline.getChildren().add(txtPane2);
                        //Day on bottom
                        //timeline.setMargin(txtPane2, new Insets(pageHeight-15, pageWidth - (left + offsetx - txtWidth), 0, left + offsetx - (txtWidth/2)));
                        //Day on the side
                        timeline.setMargin(txtPane2, new Insets(pageHeight-27, pageWidth - (left + offsetx - txtWidth), 0, left + offsetx - (txtWidth-10)));
                    }
                }
                timeline.getChildren().addAll(txtPane);
                System.out.println(hstr+":00 -> left: "+left+1);
                timeline.setMargin(txtPane, new Insets(pageHeight-txtHeigth, pageWidth - (left + offsetx - txtWidth), 0, left + offsetx - (txtWidth/2)));
                left+=2+spaceBetweenMarkers;
                lnTop.setStroke(Color.web("#F7F7F7"));
                lnBottom.setStroke(Color.web("#F7F7F7"));
                top_markers.add(lnTop);
                bottom_markers.add(lnBottom);
            } else if((i +1) % (bigMarkerFromXandX / 3) == 0) {
                lnTop = new Line(0.0,0.0,0.0,7.0);
                lnTop.translateYProperty().set(-16.0);
                lnTop.setStrokeWidth(1);
                top_markers.add(lnTop);
                lnBottom = new Line(0.0,0.0,0.0,7.0);
                lnBottom.translateYProperty().set(16.0);
                lnBottom.setStrokeWidth(1);
                lnTop.setStroke(Color.rgb(255,255,255,0.7));
                lnBottom.setStroke(Color.rgb(255,255,255,0.7));
                bottom_markers.add(lnBottom);
                left+=1+spaceBetweenMarkers;
            } else {
                lnBottom = new Line(0.0,0.0,0.0,3.0);
                    lnTop =  new Line(0.0,0.0,0.0,3.0);
                lnTop.translateYProperty().set(-18.0);
                lnBottom.translateYProperty().set(18.0);
                lnTop.setStrokeWidth(1);
                lnBottom.setStrokeWidth(1);
                if(showSmallMarkers) {
                    lnTop.setStroke(Color.rgb(255,255,255,0.3));
                    lnBottom.setStroke(Color.rgb(255,255,255,0.3));
                } else {
                    lnTop.setStroke(Color.rgb(0,0,0,0));
                    lnBottom.setStroke(Color.rgb(0,0,0,0));
                }
                top_markers.add(lnTop);
                bottom_markers.add(lnBottom);
                left+=1+spaceBetweenMarkers;
            }
        }
        System.out.println("TOTAL LEFT: "+left);
        top_timemarkers.getChildren().addAll(top_markers);
        top_timemarkers.setAlignment(Pos.CENTER_LEFT);

        bottom_timemarkers.getChildren().addAll(bottom_markers);
        bottom_timemarkers.setAlignment(Pos.CENTER_LEFT);

        timeline.getChildren().addAll(top_timemarkers,bottom_timemarkers);

        //the left value is generated by the loop
        pageWidth = left;
        if(pageWidth>timelinePageWidth) {
            timelinePageWidth=pageWidth;
        }
        return timeline;
    }

    void loadEvents(Date from, Date to) {
        //end date must be further in the future than the last searched date
        if(busy==false) {
            System.out.println("Loading events");
            busy = true;
            try {
                List<Event> resEvent = timeline.getEventsBetween(from, to);
                if (resEvent != null) {
                    int vertPos = 0; //vertical position so it doesn't overlap
                    for (Event event : resEvent) {
                        addEventGUI(event, vertPos++);
                        System.out.println(event);
                        if (vertPos >= 3) {
                            vertPos = 0;
                        }
                    }
                }
            }catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private void addEventGUI(Event event, int verticalPosition) {

    }
}
