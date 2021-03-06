package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.library.utils.Log;
import be.bendem.manga.library.utils.NumberUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MangaViewCtrl implements PostInitializable {

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

    private ImageView previous;
    private ImageView current;
    private ImageView next;

    public MangaViewCtrl(MangaLibrary app) {
        this.app = app;
        chapters = new TreeMap<>(NumberUtil::compare);
    }

    @Override
    public void afterInitialization() {
        imageContainer.autosize();
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

        current = getImage(images.get(index));
        setImage(current);

        // Load previous / next image
        if(images.size() == 1) {
            previous = null;
            next = null;
        } else {
            if(last) {
                previous = getImage(images.get(index - 1));
                next = null;
            } else {
                previous = null;
                next = getImage(images.get(index + 1));
            }
        }
        return this;
    }

    private ImageView getImage(Path img) {
        Log.debug("loading " + img);
        Image image;
        try(InputStream is = Files.newInputStream(img)) {
            image = new Image(is);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        ImageView imageView = new ImageView(image);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void setImage(ImageView imageView) {
        // TODO Loading bar using img.progressProperty()!
        imageView.setFitHeight(imageContainer.getHeight() - buttonContainer.getHeight() - imageContainer.getSpacing());
        imageView.setFitWidth(imageContainer.getWidth());
        imageContainer.getChildren().set(1, imageView);
    }

    public void onPrevAction(ActionEvent event) {
        if(previous == null) { // index == 0
            Map.Entry<String, Path> previous = chapters.lowerEntry(currentChapter);
            if(previous != null) {
                app.getController().setSelection(currentManga, previous.getKey());
                setChapter(previous.getKey(), true);
            }
            return;
        }

        setImage(previous);
        next = current;
        current = previous;
        --index;

        if(index < 1) {
            previous = null;
        } else {
            previous = getImage(images.get(index - 1));
        }
    }

    public void onNextAction(ActionEvent event) {
        if(next == null) { // index == images.size() - 1
            Map.Entry<String, Path> next = chapters.higherEntry(currentChapter);
            if(next != null) {
                app.getController().setSelection(currentManga, next.getKey());
                setChapter(next.getKey(), false);
            }
            return;
        }

        setImage(next);
        previous = current;
        current = next;
        ++index;

        if(index == images.size() - 1) {
            next = null;
        } else {
            next = getImage(images.get(index + 1));
        }
    }
}
