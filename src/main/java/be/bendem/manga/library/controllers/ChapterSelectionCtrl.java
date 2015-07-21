package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.scraper.MangaScraper;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChapterSelectionCtrl implements Controller, Initializable {

    private final MangaLibrary app;
    @FXML private Label title;
    @FXML private Button selectAllButton;
    @FXML private Button selectNoneButton;
    @FXML private Button downloadButton;
    @FXML private ListView<String> chapters;
    @FXML private Button backButton;

    public ChapterSelectionCtrl(MangaLibrary app) {
        this.app = app;
    }

    public void setManga(MangaScraper scrapper, String name, String url) {
        System.out.println("Feeding data");
        title.setText(name);
        chapters.getItems().setAll(
            scrapper
                .getChapters(url, Throwable::printStackTrace).stream()
                .map(chapter -> chapter.name)
                .collect(Collectors.toList())
        );
        System.out.println("Fed");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chapters.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) c ->
            downloadButton.setDisable(c.getList().isEmpty()));

        chapters.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void onBack(ActionEvent event) {
        app.popHistory();
    }

    public void onSelectAll(ActionEvent event) {
        chapters.getSelectionModel().selectAll();
    }

    public void onSelectNone(ActionEvent event) {
        chapters.getSelectionModel().clearSelection();
    }

    public void onDownload(ActionEvent event) {
        System.out.println("YOU WISH!");
    }

}
