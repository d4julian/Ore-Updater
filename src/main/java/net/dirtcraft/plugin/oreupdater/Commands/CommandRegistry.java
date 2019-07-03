package net.dirtcraft.plugin.oreupdater.Commands;

import net.dirtcraft.plugin.oreupdater.OreUpdater;
import net.dirtcraft.plugin.oreupdater.Permissions;
import net.dirtcraft.plugin.oreupdater.Utils.Fetch;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;

public class CommandRegistry {

    private final OreUpdater main;
    private final Fetch fetch;

    public CommandRegistry(OreUpdater main, Fetch fetch) {
        this.main = main;
        this.fetch = fetch;
        Sponge.getCommandManager().register(main, this.base(), "update", "updates", "checkupdates");
    }

    private CommandSpec base() {
        return CommandSpec.builder()
                .executor(new Base(main, fetch))
                .permission(Permissions.CHECK_UPDATES)
                .child(this.reload(), "reload")
                .build();
    }

    public CommandSpec reload() {
        return CommandSpec.builder()
                .executor(new Reload(main))
                .permission(Permissions.RELOAD)
                .build();
    }

}
