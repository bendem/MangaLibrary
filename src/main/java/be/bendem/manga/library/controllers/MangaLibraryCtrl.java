package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
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

public class MangaLibraryCtrl implements Initializable {

    @FXML private Accordion accordion;
    @FXML private AnchorPane main;

    private final MangaLibrary app;
    private final Map<String, Tuple<Parent, Object>> mainCache;
    private String mainCurrentFxml;
    private Object mainCurrentCtrl;

    public MangaLibraryCtrl(MangaLibrary app) {
        this.app = app;
        mainCache = new HashMap<>();
        mainCurrentFxml = "";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMain("search.fxml");

        // TODO Load existing mangas
        addManga("One piece", new ArrayList<String>() {{
            add("Chapter 1");
        }});
    }

    @SuppressWarnings("unchecked")
    public <T> T setMain(String fxml) {
        if(mainCurrentFxml.equals(fxml)) {
            return (T) mainCurrentCtrl;
        }

        Tuple<Parent, Object> parentCtrlTuple = mainCache.get(fxml);

        if(parentCtrlTuple == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
            loader.setControllerFactory(app.controllerFactory);
            try {
                parentCtrlTuple = new Tuple<>(loader.load(), loader.getController());
                mainCache.put(fxml, parentCtrlTuple);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMain(parentCtrlTuple.getLeft());
        app.pushHistory(mainCurrentFxml);
        mainCurrentFxml = fxml;
        mainCurrentCtrl = parentCtrlTuple.getRight();

        return (T) mainCurrentCtrl;
    }

    public void addManga(String name, List<String> chapters) {
        TitledPane pane = new TitledPane(name, new ListView<>(FXCollections.observableArrayList(chapters)));
        pane.setFont(new Font(15));

        accordion.getPanes().add(pane);
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

    public void setMain(Parent parent) {
        main.getChildren().setAll(parent);
    }
}
