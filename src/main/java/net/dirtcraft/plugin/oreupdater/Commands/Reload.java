package net.dirtcraft.plugin.oreupdater.Commands;

import net.dirtcraft.plugin.oreupdater.OreUpdater;
import net.dirtcraft.plugin.oreupdater.Utils.Utility;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;

public class Reload implements CommandExecutor {

    private OreUpdater main;

    public Reload(OreUpdater main) {
        this.main = main;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {

        main.saveConfig();
        main.loadConfig();
        if (source instanceof Player) ((Player) source).playSound(SoundTypes.BLOCK_NOTE_CHIME, ((Player) source).getPosition(), 2D);
        source.sendMessage(Utility.format("&eOre &6Updater&7 has been reloaded &asuccessfully"));

        return CommandResult.success();
    }

}
