package be.bendem.manga.library.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {

    private final Path configPath;
    private final Path applicationPath;

    public ConfigManager() {
        Path configFolder = getConfigFolder();
        configPath = configFolder.resolve("config.json");
        applicationPath = configFolder.resolve("application.json");
    }

    private Path getConfigFolder() {
        Path home = Paths.get(System.getProperty("user.home"));
        Path config = home.resolve(".mangalibrary");
        if(!Files.isDirectory(config)) {
            try {
                Files.createDirectory(config);
                Files.setAttribute(config, "dos:hidden", true);
            } catch(IOException e) {
                throw new RuntimeException("Could not create software directory", e);
            }
        }
        return config;
    }

}
