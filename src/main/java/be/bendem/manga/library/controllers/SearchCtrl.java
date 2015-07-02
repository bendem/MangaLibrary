package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.scraper.implementations.MangaEdenScraper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class SearchCtrl implements Initializable {

    @FXML private Button selectChapters;
    @FXML private TextField searchField;
    @FXML private ListView<String> searchResult;

    private final MangaLibrary app;
    private Map<String, String> currentSearchResult;

    public SearchCtrl(MangaLibrary app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchResult.getSelectionModel().selectedItemProperty().addListener((obj, oldVal, newVal) ->
            selectChapters.setDisable(newVal == null || newVal.isEmpty())
        );
    }

    public void searchFieldKeyPressed(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER) {
            return;
        }

        searchResult.getItems().clear();
        currentSearchResult = new MangaEdenScraper().search(searchField.getText());
        searchResult.getItems().addAll(currentSearchResult.keySet());
    }

    public void searchResultKeyPressed(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER) {
            return;
        }

        selectChapters.getOnAction().handle(new ActionEvent(searchResult, selectChapters));
    }

    public void onChapterSelect(ActionEvent event) {
        String selected = searchResult.getSelectionModel().getSelectedItem();
        app
            .getController()
            .<ChapterSelectionCtrl>setMain("chapter-selection.fxml")
            .setManga(selected, currentSearchResult.get(selected));
    }

}
