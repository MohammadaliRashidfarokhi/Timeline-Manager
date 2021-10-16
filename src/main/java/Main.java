import Controllers.MainController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String mainFxmlPathString = "/views/Main.fxml";

        MainController controller = new MainController();
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getResource(mainFxmlPathString));

        primaryStage.setTitle("Sparkling new Timeline Manager");
        StackPane main = loader.load();
        main.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(main, controller.WINDOW_WIDTH, controller.WINDOW_HEIGHT);
        //ESC key listener to close popup
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE: controller.closePopup(); break;
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}

