import Utils.DatabaseController;
import Models.Event;
import Models.Timeline;
import Models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class testDB {
    public static void main(String[] args) {
        try {
            DatabaseController.startDB();
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }

        User userTest = User.getTestUser();
        userTest.save();

        Timeline timeline = new Timeline();
        timeline.setTitle("Timeline test DB");
        timeline.setTimeUnit("hour");
        timeline.setCreatedBy(userTest);
        timeline.save();

        Event event = new Event();
        event.setName("Test event");
        event.setDescription("Description of the test event");
        event.setTimeline(timeline);

        event.setStartDate(LocalDateTime.now());
        event.setEndDate(LocalDateTime.now());
        event.setCreatedBy(userTest);
        event.save();
        Timeline.loadAll().forEach(timeline2 -> System.out.println(timeline2.getTitle()));
        System.out.println("FINISHED TESTE");
    }
}
