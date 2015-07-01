package be.bendem.manga.library;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MangaLibraryController implements Initializable {

    @FXML private Accordion accordion;
    @FXML private AnchorPane main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMain("search.fxml");

        // TODO Load existing mangas
        addManga("One piece", new ArrayList<String>() {{ add("Chapter 1"); }});
    }

    /* package */ void addManga(String name, List<String> chapters) {
        TitledPane pane = new TitledPane(name, new ListView<>(FXCollections.observableArrayList(chapters)));
        pane.setFont(new Font(15));

        accordion.getPanes().add(pane);
    }

    /* package */ void setMain(String fxml) {
        try {
            setMain(FXMLLoader.<Parent>load(getClass().getClassLoader().getResource(fxml)));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* package */ void setMain(Parent content) {
        main.getChildren().setAll(content);
    }

    public void handleClick(Event event) {
        System.out.println("hi " + event);
    }

}
