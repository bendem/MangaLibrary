package be.bendem.manga.library.controllers;

import be.bendem.manga.library.MangaLibrary;
import be.bendem.manga.library.utils.NumberUtil;
import be.bendem.manga.library.utils.Tuple;
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

public class MangaLibraryCtrl implements Controller, Initializable {

    @FXML private Accordion accordion;
    @FXML private AnchorPane main;

    private final MangaLibrary app;
    private final Map<String, Tuple<Parent, Controller>> mainCache;
    private String mainCurrentFxml;
    private Controller mainCurrentCtrl;

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

        Tuple<Parent, Controller> parentCtrlTuple = mainCache.get(fxml);

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
        mainCurrentCtrl.afterInitialization();

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

}
