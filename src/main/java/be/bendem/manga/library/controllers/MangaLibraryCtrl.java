package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.library.utils.NumberUtil;
import be.bendem.manga.library.utils.Tuple;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MangaLibraryCtrl implements Initializable {

    @FXML private Accordion accordion;
    @FXML private AnchorPane main;

    private final MangaLibrary app;
    private final Map<String, Tuple<Parent, Object>> mainCache;
    private String mainCurrentFxml;
    private Object mainCurrentCtrl;

    public MangaLibraryCtrl(MangaLibrary app) {
        this.app = app;
        mainCache = new HashMap<>();
        mainCurrentFxml = "";
    }

    @Override
    @SuppressWarnings("")
    public void initialize(URL location, ResourceBundle resources) {
        setMain("search.fxml");

        String libraryLocation = app.getConfigManager().getApplicationConfig().get("libraryLocation");
        Path library = Paths.get(libraryLocation);
        collectDirectories(library)
            .forEachOrdered(manga ->
                addManga(
                    manga.getFileName().toString(),
                    collectDirectories(manga)
                        .map(subPath -> subPath.getFileName().toString())
                        .sorted(NumberUtil::compare)
                        .collect(Collectors.toList())
                )
            );
    }

    Stream<Path> collectDirectories(Path path) {
        if(!Files.isDirectory(path)) {
            return Stream.empty();
        }

        try {
            return Files.walk(path, 1)
                .filter(subPath -> !subPath.equals(path))
                .filter(Files::isDirectory);
        } catch(IOException e) {
            throw new RuntimeException("Failed to collect directories from " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T setMain(String fxml) {
        if(mainCurrentFxml.equals(fxml)) {
            return (T) mainCurrentCtrl;
        }

        Tuple<Parent, Object> parentCtrlTuple = mainCache.get(fxml);

        if(parentCtrlTuple == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
            loader.setControllerFactory(app.controllerFactory);
            try {
                parentCtrlTuple = new Tuple<>(loader.load(), loader.getController());
                mainCache.put(fxml, parentCtrlTuple);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMain(parentCtrlTuple.getLeft());
        app.pushHistory(mainCurrentFxml);
        mainCurrentFxml = fxml;
        mainCurrentCtrl = parentCtrlTuple.getRight();
        if(mainCurrentCtrl instanceof PostInitializable) {
            ((PostInitializable) mainCurrentCtrl).afterInitialization();
        }

        return (T) mainCurrentCtrl;
    }

    private void setMain(Parent parent) {
        main.getChildren().setAll(parent);
    }

    private void addManga(String name, List<String> chapters) {
        ListView<String> content = new ListView<>(FXCollections.observableArrayList(chapters));

        content.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> this
                .<MangaViewCtrl>setMain("manga-view.fxml")
                .setManga(name)
                .setChapter(newVal, false)
        );

        // TODO Load that value, default to -1
        SimpleIntegerProperty readMarkerIndex = new SimpleIntegerProperty(-1);
        content.getSelectionModel().selectedIndexProperty().addListener(
            (obs, oldVal, newVal) -> {
                if(newVal.intValue() > readMarkerIndex.get()) {
                    // TODO Save the new value
                    readMarkerIndex.setValue(newVal);
                }
            }
        );
        content.setCellFactory(listView -> new MarkableTextFieldListCell(readMarkerIndex, "read-marker"));

        TitledPane pane = new TitledPane(name, content);
        pane.setFont(new Font(15));

        accordion.getPanes().add(pane);
    }

    public void setSelection(String manga, String chapter) {
        Node content = accordion.getPanes().stream()
            .filter(pane -> pane.getText().equals(manga))
            .findFirst()
            .get()
            .getContent();
        ListView<String> listView = (ListView<String>) content;
        listView.getSelectionModel().select(chapter);
        listView.scrollTo(chapter);
    }

    public void onSearchAction(ActionEvent event) {
        setMain("search.fxml");
    }

    public void onDownloadsAction(ActionEvent event) {
        setMain("downloads.fxml");
    }

    public void onConfigAction(ActionEvent event) {
        setMain("config.fxml");
    }

    public void onImportAction(ActionEvent event) {
        // TODO Open file chooser
    }

    private class MarkableTextFieldListCell<T> extends TextFieldListCell<T> {

        private final IntegerPropertyBase index;
        private final String styleClass;
        private boolean marked = false;

        public MarkableTextFieldListCell(IntegerPropertyBase index, String styleClass) {
            this.index = index;
            this.styleClass = styleClass;

            index.addListener((obs, oldVal, newVal) -> {
                if(marked) {
                    getStyleClass().remove(styleClass);
                    marked = false;
                    this.applyCss();
                } else if(this.getIndex() == newVal.intValue()) {
                    getStyleClass().add(styleClass);
                    marked = true;
                    this.applyCss();
                }
            });

            indexProperty().addListener((obs, oldVal, newVal) -> {
                if(newVal.intValue() == index.get()) {
                    getStyleClass().add(styleClass);
                    marked = true;
                } else {
                    getStyleClass().remove(styleClass);
                    marked = false;
                }
            });
        }

    }
}
