package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.library.utils.Log;
import be.bendem.manga.library.utils.NumberUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MangaViewCtrl implements Initializable {

    @FXML private VBox imageContainer;
    @FXML private ImageView image;

    private final MangaLibrary app;

    public MangaViewCtrl(MangaLibrary app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageContainer.autosize();
    }

    public void setChapter(String manga, String chapter) {
        Log.debug("Loading " + manga + ": " + chapter);
        Path location = Paths
            .get(app
                    .getConfigManager()
                    .getApplicationConfig()
                    .<String>get("libraryLocation")
            )
            .resolve(manga)
            .resolve(chapter);


        InputStream is;
        try {
            Path path = Files
                .walk(location)
                .skip(1)
                .sorted(NumberUtil::compare)
                .findFirst()
                .get();
            //Log.info(path.toString());
            is = Files.newInputStream(path);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        // TODO Loading bar using img.progressProperty()!
        Log.debug("w" + imageContainer.getWidth() + " h" + imageContainer.getHeight());
        Log.debug(imageContainer.getBoundsInLocal().toString());

        image.setSmooth(true);
        image.setPreserveRatio(true);
        image.setFitHeight(imageContainer.getHeight());
        image.setFitWidth(imageContainer.getWidth());
        image.setImage(new Image(is));
    }

    public void onPrevAction(ActionEvent event) {
        // TODO
    }

    public void onNextAction(ActionEvent event) {
        // TODO
    }

}
