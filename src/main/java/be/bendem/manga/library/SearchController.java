package be.bendem.manga.library;

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
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML private Button downloadButton;
    @FXML private TextField searchField;
    @FXML private ListView<String> searchResult;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchResult.getSelectionModel().selectedItemProperty().addListener((obj, oldVal, newVal) ->
            downloadButton.setDisable(newVal == null || newVal.isEmpty())
        );
    }

    public void searchFieldKeyPressed(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER) {
            return;
        }

        searchResult.getItems().clear();
        searchResult.getItems().addAll(new MangaEdenScraper().search(searchField.getText()).keySet());
    }

    public void onDownload(ActionEvent event) {
        System.out.println(event);
    }

    public void searchResultKeyPressed(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER) {
            return;
        }

        downloadButton.getOnAction().handle(new ActionEvent(searchResult, downloadButton));
    }
}
