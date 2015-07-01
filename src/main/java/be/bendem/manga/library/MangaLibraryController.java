package be.bendem.manga.library;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MangaLibraryController implements Initializable {

    @FXML private Accordion accordion;
    @FXML private AnchorPane main;
    @FXML private TitledPane searchPane;

    public MangaLibraryController() {
    }

    public void handleClick(Event event) {
        System.out.println("hi " + event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Parent search;
        try {
            search = FXMLLoader.load(getClass().getClassLoader().getResource("search.fxml"));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        main.getChildren().setAll(search);

        accordion.setExpandedPane(searchPane);
    }

}
