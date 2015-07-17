package be.bendem.manga.library;

import be.bendem.manga.library.config.ConfigManager;
import be.bendem.manga.library.controllers.MangaLibraryCtrl;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

public class MangaLibrary extends Application {

    public final Callback<Class<?>, Object> controllerFactory;
    private final ConfigManager configManager;
    private final Stack<String> mainHistory;
    private MangaLibraryCtrl controller;

    public MangaLibrary() {
        controllerFactory = clazz -> {
            try {
                return clazz.getConstructor(MangaLibrary.class).newInstance(this);
            } catch(InstantiationException | IllegalAccessException
                | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("Could not instantiate controller for " + clazz, e);
            }
        };
        configManager = new ConfigManager();
        mainHistory = new Stack<>();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("app.fxml"));
        loader.setControllerFactory(controllerFactory);
        Parent app = loader.load();
        controller = loader.getController();

        app.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

        double width = Double.valueOf(configManager.getApplicationConfig().get("applicationWidth", "-1"));
        double height = Double.valueOf(configManager.getApplicationConfig().get("applicationHeight", "-1"));

        Scene scene = new Scene(app, width, height);
        stage.setTitle("Manga Library");
        stage.setScene(scene);
        stage.show();

        stage.widthProperty().addListener(this::onWidthChange);
        stage.heightProperty().addListener(this::onHeightChange);
    }

    private void onWidthChange(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
        configManager.getApplicationConfig().set("applicationWidth", newVal);
    }

    private void onHeightChange(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
        configManager.getApplicationConfig().set("applicationHeight", newVal);
    }

    @Override
    public void stop() {
        configManager.getApplicationConfig().save();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MangaLibraryCtrl getController() {
        return controller;
    }

    public void pushHistory(String fxml) {
        mainHistory.push(fxml);
    }

    public void popHistory() {
        if(mainHistory.isEmpty()) {
            return;
        }

        controller.setMain(mainHistory.pop());
    }

}
