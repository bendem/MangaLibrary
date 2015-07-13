package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.library.ScraperImplementation;
import be.bendem.manga.library.utils.Log;
import be.bendem.manga.scraper.Scraper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class SearchCtrl implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<ScraperImplementation> hostComboBox;
    @FXML private Button searchButton;
    @FXML private Button selectChapters;
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

        hostComboBox.getItems().setAll(ScraperImplementation.values());
    }

    public void searchFieldKeyPressed(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER) {
            return;
        }

        searchButton.getOnAction().handle(new ActionEvent(searchField, searchButton));
    }

    public void onSearchButtonAction(ActionEvent event) {
        String text = searchField.getText();

        if(text.isEmpty()) {
            // TODO Visual effect
            Log.info("No search provided");
            return;
        }

        ScraperImplementation selectedHost = hostComboBox.getSelectionModel().getSelectedItem();
        if(selectedHost == null) {
            // TODO Visual effect
            Log.info("No host selected");
            return;
        }

        Scraper scraper;
        try {
            scraper = selectedHost.getScraperClass().newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            // TODO Error handling
            throw new RuntimeException(e);
        }

        searchResult.getItems().clear();
        currentSearchResult = scraper.search(text);
        if(currentSearchResult.isEmpty()) {
            // TODO Visual effect
            return;
        }
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
