package be.bendem.manga.library;

import be.bendem.manga.scraper.MangaScraper;
import be.bendem.manga.scraper.implementations.MangaEdenScraper;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChapterSelectionController implements Initializable {

    @FXML private Button selectAllButton;
    @FXML private Button selectNoneButton;
    @FXML private Button downloadButton;
    @FXML private ListView<String> chapters;
    @FXML private Button backButton;

    public void setMangaUrl(String mangaUrl) {
        System.out.println("Feeding data");
        chapters.getItems().addAll(
            new MangaScraper(new MangaEdenScraper())
                .getChapters(mangaUrl).stream()
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
