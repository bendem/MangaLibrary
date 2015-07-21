package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class DownloadCtrl {

    @FXML private ListView<String> downloadList;

    private final MangaLibrary app;

    public DownloadCtrl(MangaLibrary app) {
        this.app = app;
    }

    public void onPauseResumeAction(ActionEvent event) {

    }

    public void onClearDownloadsAction(ActionEvent event) {

    }

}
