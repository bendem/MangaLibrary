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
                (a, b) -> b
            ));
    }

    @Override
    public ApplicationConfig save() {
        // TODO Offthread?
        try {
            Files.write(
                path,
                config.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .sorted()
                    .collect(Collectors.toList()),
                StandardCharsets.UTF_8
            );
        } catch(IOException e) {
            throw new RuntimeException("Could not save application config", e);
        }

        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String get(String key) {
        return config.get(key);
    }

    @Override
    public String get(String key, String def) {
        String val = get(key);
        return val == null ? def : val;
    }

    @Override
    public ApplicationConfig set(String key, Object value) {
        // TODO Not use toString?
        config.put(key, value.toString());

        return this;
    }
}
