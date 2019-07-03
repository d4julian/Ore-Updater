package net.dirtcraft.plugin.oreupdater;

import com.google.inject.Inject;
import net.dirtcraft.plugin.oreupdater.Configuration.ConfigManager;
import net.dirtcraft.plugin.oreupdater.Utils.Fetch;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(
        id = "ore-updater",
        name = "Ore Updater",
        description = "Plugin that updates all of your plugins from the Ore repository",
        url = "https://dirtcraft.net/",
        authors = {
                "juliann"
        }
)
public class OreUpdater {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private ConfigManager configManager;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    private Fetch fetch;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        configManager = new ConfigManager(loader);
        fetch = new Fetch(this);
        Sponge.getEventManager().registerListeners(this, new LifeCycle(this, fetch));
    }

    public void saveConfig() {
        configManager.save();
    }

    public void loadConfig() {
        configManager.load();
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getContainer() {
        return container;
    }
}
