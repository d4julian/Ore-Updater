package net.dirtcraft.plugin.oreupdater.Commands;

import net.dirtcraft.plugin.oreupdater.Data.Plugin;
import net.dirtcraft.plugin.oreupdater.OreUpdater;
import net.dirtcraft.plugin.oreupdater.Permissions;
import net.dirtcraft.plugin.oreupdater.Utils.Fetch;
import net.dirtcraft.plugin.oreupdater.Utils.Utility;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Base implements CommandExecutor {

    private ArrayList<String> updatedPlugins = new ArrayList<>();

    private final OreUpdater main;
    private final Fetch fetch;

    public Base(OreUpdater main, Fetch fetch) {
        this.main = main;
        this.fetch = fetch;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {

        Task.builder()
                .async()
                .execute(() -> {
                    source.sendMessage(Utility.format("&7&oChecking For Updates..."));
                    ArrayList<Plugin> plugins = fetch.hasUpdatePlugins();
                    ArrayList<Text> contents = new ArrayList<>();

                    final ArrayList<String> hover = new ArrayList<String>() {{
                        add("&7Plugin Name&8: &6%plugin_name%");
                        add("&7Plugin ID&8: &6%plugin_id%");
                        add("&7Plugin Description&8: &6%plugin_description%");
                        add("&r");
                        add("&7New Version&8: &a%new_version%");
                        add("&7Current Version&8: &c%current_version%");
                    }};

                    if (plugins.size() > 0) {
                        for (Plugin plugin : plugins) {

                            String hoverInfo = String.join("\n", hover);
                            hoverInfo = hoverInfo.replace("%plugin_name%", plugin.getName());
                            hoverInfo = hoverInfo.replace("%plugin_id%", plugin.getId());
                            hoverInfo = hoverInfo.replace("%plugin_description%", plugin.getDescription());
                            hoverInfo = hoverInfo.replace("%new_version%", plugin.getNewVersion());
                            hoverInfo = hoverInfo.replace("%current_version%", plugin.getCurrentVersion());

                            Text.Builder text = Text.builder();

                            if (!updatedPlugins.contains(plugin.getId())) text.append(Utility.format("&8» &a" + plugin.getName()));
                            else text.append(Utility.format("&8» &e&m" + plugin.getName()));

                            if (source.hasPermission(Permissions.DOWNLOAD_UPDATE)) {
                                hoverInfo += "\n&d&oClick To Download The Update";
                                text.onClick(TextActions.executeCallback(downloadUpdate(plugin)));
                            } else {
                                hoverInfo += "\n&c&oYou do not have permission to download updates!";
                            }
                            text.onHover(TextActions.showText(Utility.format(hoverInfo)));

                            contents.add(text.build());
                        }
                    } else {
                        contents.add(Utility.format("\n&aAll of your plugins are up to date!\n"));
                    }

                    PaginationList.Builder pagination = PaginationList.builder()
                            .title(Utility.format("&eOre &6Updater"))
                            .padding(Utility.format("&7&m-"))
                            .contents(contents);

                    if (source.hasPermission(Permissions.DOWNLOAD_UPDATE)) {
                        pagination.footer(Utility.format("&d&oHover For Information"));
                    }

                    pagination.build().sendTo(source);
                })
                .submit(main);

        return CommandResult.success();
    }

    private Consumer<CommandSource> downloadUpdate(Plugin plugin) {
        return source -> {
            source.sendMessage(Utility.format("&7&oDownloading the update for &6&o" + plugin.getName() + "&7&o..."));
            if (fetch.downloadPlugin(plugin)) {
                updatedPlugins.add(plugin.getId());
                source.sendMessage(Utility.format("&aThe update for " + plugin.getName() + " has successfully been downloaded! Please restart the server for changes to take effect."));
            } else source.sendMessage(Utility.format("&cThe update for &e" + plugin.getName() + "&c could not be downloaded! Contact Julian immediately!"));
        };
    }

}
