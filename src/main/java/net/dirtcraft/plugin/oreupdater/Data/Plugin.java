package net.dirtcraft.plugin.oreupdater.Data;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.nio.file.Path;
import java.util.Optional;

@ConfigSerializable
public class Plugin {

    private String description;
    private Optional<Path> directory;

    @Setting (value = "Plugin Name")
    private String name;
    @Setting (value = "Current Version")
    private String currentVersion;
    @Setting (value = "New Version")
    private String newVersion;
    @Setting (value = "Plugin ID")
    private String id;
    @Setting (value = "Automatic Updates")
    private boolean automaticUpdates = false;

    public static Plugin of(String name, String id, String description, String currentVersion, String newVersion, Optional<Path> directory) {
        Plugin plugin = new Plugin();
        plugin.name = name;
        plugin.id = id;
        plugin.description = description;
        plugin.currentVersion = currentVersion;
        plugin.newVersion = newVersion;
        plugin.directory = directory;
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String version) {
        this.currentVersion = version;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public Optional<Path> getDirectory() {
        return directory;
    }

    public boolean canAutomaticallyUpdate() {
        return automaticUpdates;
    }
}
