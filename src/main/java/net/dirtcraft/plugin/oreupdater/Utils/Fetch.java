package net.dirtcraft.plugin.oreupdater.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.dirtcraft.plugin.oreupdater.Configuration.PluginConfiguration;
import net.dirtcraft.plugin.oreupdater.Data.Plugin;
import net.dirtcraft.plugin.oreupdater.OreUpdater;
import org.apache.commons.io.FileUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Fetch {

    private final OreUpdater main;

    public Fetch(OreUpdater main) {
        this.main = main;
    }

    public boolean downloadPlugin(Plugin plugin) {
        final String pluginName = (plugin.getName() != null ? plugin.getName() : plugin.getId().toLowerCase()).trim();

        try {
            File pluginFolder = null;
            if (PluginConfiguration.pluginDirectory.length() == 0) {
                try {
                    if (plugin.getDirectory().isPresent())
                        pluginFolder = plugin.getDirectory().get().getParent().toFile();
                } catch (NullPointerException exception) {
                    if (main.getContainer().getSource().isPresent())
                        pluginFolder = main.getContainer().getSource().get().getParent().toFile();
                }

                if (pluginFolder == null) {
                    main.getLogger().error("Could not get the plugins directory. Please contact Julian immediately and/or specify it manually in configuration!");
                    return false;
                }
            } else pluginFolder = new File(PluginConfiguration.pluginDirectory);

            try {
                for (File file : pluginFolder.listFiles()) {
                    if (file.getName().toLowerCase().trim().replace(" ", "").replace("-", "").contains(pluginName.toLowerCase().replace(" ", "").replace("-", ""))) {
                        if (!file.delete()) return false;
                    }
                }
            } catch (NullPointerException exception) {
                main.getLogger().error("The plugin directory returned null while attempting to list files. Please contact Julian immediately!");
                exception.printStackTrace();
                return false;
            }

            final JsonObject downloadJson = JsonUtils.getJsonObjFromString(JsonUtils.getStringFromURL(getFinalURL(new URL(Repository.DOWNLOAD_URL.replace("%plugin_id%", plugin.getId().toLowerCase().trim().replace(" ", "-")))).toString()));
            final String url = downloadJson.get("url").getAsString();

            FileUtils.copyURLToFile(getFinalURL(new URL(url)), new File(pluginFolder.getCanonicalPath() + File.separator + pluginName.replace(" ", "-") + "-" + plugin.getNewVersion() + ".jar"));

            main.getLogger().warn("The plugin \"" + pluginName + "\" has been downloaded successfully!");
            return true;
        } catch (IOException exception) {
            main.getLogger().error("Could not download the plugin \"" + pluginName + "\". Please report this to Julian immediately!");
            return false;
        }
    }

    public ArrayList<Plugin> hasUpdatePlugins() {
        ArrayList<Plugin> plugins = new ArrayList<>();
        for (Plugin plugin : getPlugins()) {
            if (!plugin.getCurrentVersion().equals(plugin.getNewVersion())) plugins.add(plugin);
        }
        return plugins;
    }

    public ArrayList<Plugin> hasUpdatePlugins(ArrayList<Plugin> pluginsArray) {
        ArrayList<Plugin> plugins = new ArrayList<>();
        for (Plugin plugin : pluginsArray) {
            if (!plugins.contains(plugin) && !plugin.getCurrentVersion().equals(plugin.getNewVersion())) plugins.add(plugin);
        }
        return plugins;
    }

    public ArrayList<Plugin> getPlugins() {
        ArrayList<Plugin> plugins = new ArrayList<>();
        for (PluginContainer plugin : Sponge.getPluginManager().getPlugins()) {
            try {
                if (!plugin.getVersion().isPresent()) {
                    main.getLogger().info("The plugin \"" + plugin.getName() + "\" does not have a version! Skipping...");
                    continue;
                }

                final JsonObject json = JsonUtils.getJsonObjFromString(JsonUtils.getStringFromURL(Repository.API_URL + plugin.getId()));
                final String oreVersion = json.get("recommended").getAsJsonObject().get("name").getAsString();
                final String description = plugin.getDescription().isPresent() ? plugin.getDescription().get() : "N/A";

                Plugin pluginObject = Plugin.of(plugin.getName(), plugin.getId(), description, plugin.getVersion().get(), oreVersion, plugin.getSource());
                plugins.add(pluginObject);

            } catch (JsonSyntaxException exception) {
                main.getLogger().debug("The plugin \"" + plugin.getName() + "\" is not available on Ore! Skipping...");
            }
        }
        return plugins;
    }

    private URL getFinalURL(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");
            con.setInstanceFollowRedirects(false);
            con.addRequestProperty("User-Agent", "Mozilla");
            con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            con.addRequestProperty("Referer", "https://www.google.com/");
            con.connect();
            int resCode = con.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_SEE_OTHER
                    || resCode == HttpURLConnection.HTTP_MOVED_PERM
                    || resCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String Location = con.getHeaderField("Location");
                if (Location.startsWith("/")) {
                    Location = url.getProtocol() + "://" + url.getHost() + Location;
                }
                return getFinalURL(new URL(Location));
            }
            con.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return url;
    }

}
