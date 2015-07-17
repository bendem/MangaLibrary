package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ConfigCtrl implements Initializable {

    @FXML private TextField libraryLocationField;
    @FXML private Button saveButton;

    private final MangaLibrary app;

    public ConfigCtrl(MangaLibrary app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initValues();

        libraryLocationField.textProperty().addListener((obs, old, newVal) ->
            validateDirectoryExists(newVal, libraryLocationField.getStyleClass()));
    }

    private void initValues() {
        libraryLocationField.setText(app.getConfigManager().getApplicationConfig().get("libraryLocation"));
    }

    public void onSaveAction(ActionEvent event) {
        if(!Files.isDirectory(Paths.get(libraryLocationField.getText()))) {
            libraryLocationField.setTooltip(new Tooltip("Directory does not exist"));
            return;
        }

        app
            .getConfigManager()
            .getApplicationConfig()
            .set("libraryLocation", libraryLocationField.getText())
            .save();
    }

    private void validateDirectoryExists(String path, ObservableList<String> classList) {
        if(!path.isEmpty() && Files.isDirectory(Paths.get(path))) {
            classList.remove("error");
        } else {
            if(!classList.contains("error")) {
                classList.add("error");
            }
        }
    }

}
