package net.dirtcraft.plugin.oreupdater.Configuration;

import net.dirtcraft.plugin.oreupdater.Data.Plugin;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class PluginConfiguration {

    @Setting(value = "Automatic Updates", comment = "Automatically update plugins from Ore on shut down for ALL plugins. If enabled, this will override \"Plugins\"")
    public static boolean automaticUpdates = false;

    @Setting(value = "Plugins", comment = "Choose which plugins you want to automatically update. This will override \"Automatic Updates\"")
    public static ArrayList<Plugin> plugins = new ArrayList<>();

}
