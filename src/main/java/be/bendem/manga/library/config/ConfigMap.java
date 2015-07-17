package be.bendem.manga.library.config;

/**
 * TODO Use enum for keys
 */
public interface ConfigMap {

    String get(String key);
    String get(String key, String def);
    ConfigMap set(String key, Object value);
    ConfigMap save();

}
