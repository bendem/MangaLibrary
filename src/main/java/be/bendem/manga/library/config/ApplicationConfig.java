package be.bendem.manga.library.config;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationConfig implements ConfigMap {

    private final Map<String, String> config;
    private final Path path;

    public ApplicationConfig(Path path) {
        this.path = path;
        try {
            config = createOrParseConfig();
        } catch(IOException e) {
            throw new RuntimeException("Could not load config", e);
        }
    }

    private Map<String, String> createOrParseConfig() throws IOException {
        if(!Files.isRegularFile(path)) {
            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            writer.write(
                "libraryLocation=" + Paths.get(System.getProperty("user.home"))
                    .resolve("MangaLibrary")
                    .toAbsolutePath()
                    .toString()
            );
            writer.newLine();
            writer.close();
        }

        return Files.lines(path, StandardCharsets.UTF_8)
            .filter(line -> !line.isEmpty())
            .filter(line -> !line.startsWith("#"))
            .filter(line -> {
                if(line.contains("=")) {
                    return true;
                } else {
                    System.err.println("Invalid line ignored " + line);
                    return false;
                }
            })
            .map(line -> line.split("="))
            .collect(Collectors.toMap(
                lineParts -> lineParts[0],
                lineParts -> lineParts[1],
                (a, b) -> a
            ));
    }

    public ApplicationConfig save() {
        try {
            Files.write(
                path,
                config.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.toList()),
                StandardCharsets.UTF_8
            );
        } catch(IOException e) {
            throw new RuntimeException("Could not save application config", e);
        }

        return this;
    }

    @Override
    public <T> T get(String key) {
        return (T) config.get(key);
    }

    @Override
    public <T> ApplicationConfig set(String key, T value) {
        // TODO Not use toString?
        config.put(key, value.toString());

        // TODO Offthread?
        save();

        return this;
    }
}
