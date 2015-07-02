package be.bendem.manga.library;

import be.bendem.manga.library.utils.Tuple;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

public class MangaLibraryController implements Initializable {

    // TODO Find another way, that's horrible
    public static MangaLibraryController instance;

    @FXML private Accordion accordion;
    @FXML private AnchorPane main;

    private final Map<String, Tuple<Parent, Object>> mainCache;
    private final Stack<String> mainHistory;
    private Object mainCurrentCtrl;
    private String mainCurrentFxml;

    public MangaLibraryController() {
        mainCache = new HashMap<>();
        mainHistory = new Stack<>();
        mainCurrentFxml = "";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this; // Damn son

        setMain("search.fxml");

        // TODO Load existing mangas
        addManga("One piece", new ArrayList<String>() {{
            add("Chapter 1");
        }});
    }

    /* package */ void addManga(String name, List<String> chapters) {
        TitledPane pane = new TitledPane(name, new ListView<>(FXCollections.observableArrayList(chapters)));
        pane.setFont(new Font(15));

        accordion.getPanes().add(pane);
    }

    @SuppressWarnings("unchecked")
    /* package */ <T> T setMain(String fxml) {
        if(mainCurrentFxml.equals(fxml)) {
            return (T) mainCurrentCtrl;
        }

        Tuple<Parent, Object> parentCtrlTuple = mainCache.get(fxml);

        if(parentCtrlTuple == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
            try {
                parentCtrlTuple = new Tuple<>(loader.load(), loader.getController());
                mainCache.put(fxml, parentCtrlTuple);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMain(parentCtrlTuple.getLeft());
        mainHistory.push(mainCurrentFxml);
        mainCurrentFxml = fxml;
        mainCurrentCtrl = parentCtrlTuple.getRight();

        return (T) mainCurrentCtrl;
    }

    private void setMain(Parent content) {
        main.getChildren().setAll(content);
        // TODO Slide animation?
    }

    /* package */ void popHistory() {
        if(mainHistory.isEmpty()) {
            return;
        }

        setMain(mainHistory.pop());
    }

    public void onSearchAction(ActionEvent event) {
        setMain("search.fxml");
    }

    public void onDownloadsAction(ActionEvent event) {
        setMain("downloads.fxml");
    }

    public void onConfigAction(ActionEvent event) {
        setMain("config.fxml");
    }

    public void onImportAction(ActionEvent event) {
        setMain("import.fxml");
    }

}
