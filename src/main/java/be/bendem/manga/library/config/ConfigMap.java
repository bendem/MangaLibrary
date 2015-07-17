package be.bendem.manga.library.config;

/**
 * TODO Use enum for keys
 */
public interface ConfigMap {

    <T> T get(String key);
    <T> T get(String key, T def);
    <T> ConfigMap set(String key, T value);
    ConfigMap save();

}
