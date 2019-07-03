package net.dirtcraft.plugin.oreupdater;

import net.dirtcraft.plugin.oreupdater.Commands.CommandRegistry;
import net.dirtcraft.plugin.oreupdater.Configuration.PluginConfiguration;
import net.dirtcraft.plugin.oreupdater.Data.Plugin;
import net.dirtcraft.plugin.oreupdater.Utils.Fetch;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;

public class LifeCycle {

    private final OreUpdater main;
    private final Fetch fetch;

    public LifeCycle(OreUpdater main, Fetch fetch) {
        this.main = main;
        this.fetch = fetch;
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        new CommandRegistry(main, fetch);
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        Task.builder()
                .async()
                .execute(() -> {
                    final ArrayList<Plugin> orePlugins = fetch.getPlugins();
                    final ArrayList<Plugin> pluginUpdates = fetch.hasUpdatePlugins(orePlugins);

                    if (pluginUpdates.size() > 0) {
                        main.getLogger().info("----- Updates Available -----");
                        for (Plugin plugin : pluginUpdates) {
                            main.getLogger().info("- " + plugin.getName());
                        }
                    } else {
                        main.getLogger().info("----- All Plugins Are Up To Date! -----");
                    }

                    boolean hasLoaded = false;
                    boolean needsSave = false;
                    for (Plugin plugin : orePlugins) {
                        if (!containsPlugin(PluginConfiguration.plugins, plugin)) {
                            if (!hasLoaded) {
                                main.loadConfig();
                                hasLoaded = true;
                            }
                            PluginConfiguration.plugins.add(plugin);
                            for (Plugin pluginsArray : PluginConfiguration.plugins) {
                                main.getLogger().info("- " + pluginsArray.getName());
                            }

                            if (!needsSave) needsSave = true;
                        }
                    }
                    if (needsSave) {
                        main.saveConfig();
                        main.loadConfig();
                    }
                })
                .submit(main);
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        main.loadConfig();

        ArrayList<Plugin> updatePlugins = fetch.hasUpdatePlugins();
        if (PluginConfiguration.automaticUpdates) {
            for (Plugin plugin : updatePlugins) {
                if (plugin.getCurrentVersion().equalsIgnoreCase(plugin.getNewVersion())) continue;
                if (fetch.downloadPlugin(plugin)) {
                    main.getLogger().warn("The plugin \"" + plugin.getName() + "\" has been updated to version " + plugin.getNewVersion());
                } else {
                    main.getLogger().error("Could not update the plugin \"" + plugin.getName() + "\"! Contact Julian immediately!");
                }
            }
        } else {
            boolean needsSaving = false;
            for (Plugin plugin : PluginConfiguration.plugins) {
                if (plugin.getCurrentVersion().equalsIgnoreCase(plugin.getNewVersion())) continue;
                if (!plugin.canAutomaticallyUpdate()) continue;
                if (fetch.downloadPlugin(plugin)) {
                    plugin.setCurrentVersion(plugin.getNewVersion());
                    needsSaving = true;
                    main.getLogger().warn("The plugin \"" + plugin.getName() + "\" has been updated to version " + plugin.getNewVersion());
                } else {
                    main.getLogger().error("Could not update the plugin \"" + plugin.getName() + "\"! Contact Julian immediately!");
                }
            }
            if (needsSaving) {
                main.saveConfig();
                main.loadConfig();
            }
        }
    }

    private boolean containsPlugin(ArrayList<Plugin> pluginsArray, Plugin pluginObject) {
        for (Plugin plugin : pluginsArray) {
            if (plugin.getId().equalsIgnoreCase(pluginObject.getId())) return true;
        }
        return false;
    }

}
