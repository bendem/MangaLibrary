package be.bendem.manga.library;

import be.bendem.manga.library.config.ConfigManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MangaLibrary extends Application {

    public MangaLibrary() {
        new ConfigManager();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app.fxml"));
        Parent app = loader.load();

        stage.setTitle("Manga Library");
        stage.setScene(new Scene(app));
        stage.setMinWidth(400);
        stage.setMinHeight(100);
        stage.show();
    }

}
