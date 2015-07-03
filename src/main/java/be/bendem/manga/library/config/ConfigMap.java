package be.bendem.manga.library.config;

public interface ConfigMap {

    <T> T get(String key);
    <T> ConfigMap set(String key, T value);

}
