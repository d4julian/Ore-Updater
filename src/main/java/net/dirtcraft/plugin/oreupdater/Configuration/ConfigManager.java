package net.dirtcraft.plugin.oreupdater.Configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

public class ConfigManager {
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationOptions options;
    private ConfigurationNode node;

    public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
        options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
        this.load();
    }

    public void load() {
        try {
            node = loader.load(options);
            node.getValue(TypeToken.of(PluginConfiguration.class), new PluginConfiguration());
            this.save();
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }

    public void save() {
        try {
            node.setValue(TypeToken.of(PluginConfiguration.class), new PluginConfiguration());
            loader.save(node);
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }
}