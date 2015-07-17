package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.library.utils.Log;
import be.bendem.manga.library.utils.NumberUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MangaViewCtrl implements Initializable {

    @FXML private VBox imageContainer;
    @FXML private HBox buttonContainer;
    @FXML private ImageView image;

    private final MangaLibrary app;

    private String currentManga;
    private Path currentMangaPath;

    private TreeMap<String, Path> chapters;
    private String currentChapter;
    private List<Path> images;
    private int index;

    public MangaViewCtrl(MangaLibrary app) {
        this.app = app;
        chapters = new TreeMap<>(NumberUtil::compare);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageContainer.autosize();

        image.setPreserveRatio(true);
        image.setSmooth(true);
    }

    public MangaViewCtrl setManga(String manga) {
        currentManga = manga;
        currentMangaPath = Paths
            .get(app.getConfigManager().getApplicationConfig().<String>get("libraryLocation"))
            .resolve(manga);

        chapters.clear();
        try {
            chapters.putAll(
                Files
                    .walk(currentMangaPath, 1)
                    .skip(1)
                    .collect(Collectors.toMap(
                        path -> path.getFileName().toString(),
                        path -> path
                    ))
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public MangaViewCtrl setChapter(String chapter, boolean last) {
        currentChapter = chapter;
        return setChapter(chapters.get(chapter), last);
    }

    private MangaViewCtrl setChapter(Path chapter, boolean last) {
        try {
            images = Files
                .walk(chapter, 1)
                .skip(1)
                .sorted(NumberUtil::compare)
                .collect(Collectors.toList());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        index = last ? images.size() - 1 : 0;

        setImage(images.get(index));
        return this;
    }

    private void setImage(Path img) {
        InputStream is;
        try {
            is = Files.newInputStream(img);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        // TODO Loading bar using img.progressProperty()!
        image.setFitHeight(imageContainer.getHeight() - buttonContainer.getHeight() - imageContainer.getSpacing());
        image.setFitWidth(imageContainer.getWidth());
        image.setImage(new Image(is));

        try {
            is.close();
        } catch(IOException e) {
            Log.err("Could not close image stream", e);
        }
    }

    public void onPrevAction(ActionEvent event) {
        if(index == 0) {
            Map.Entry<String, Path> previous = chapters.lowerEntry(currentChapter);
            if(previous != null) {
                app.getController().setSelection(currentManga, previous.getKey());
                setChapter(previous.getKey(), true);
            }
            return;
        }
        setImage(images.get(--index));
    }

    public void onNextAction(ActionEvent event) {
        if(index == images.size() - 1) {
            Map.Entry<String, Path> next = chapters.higherEntry(currentChapter);
            if(next != null) {
                app.getController().setSelection(currentManga, next.getKey());
                setChapter(next.getKey(), false);
            }
            return;
        }
        setImage(images.get(++index));
    }
}
