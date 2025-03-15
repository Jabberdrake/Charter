package dev.jabberdrake.charter;

import dev.jabberdrake.charter.commands.CharterCommand;
import dev.jabberdrake.charter.commands.SettlementCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.logging.Logger;

public final class Charter extends JavaPlugin {
    private final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("Starting up Charter!");
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Shutting down Charter!");
    }

    public void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CharterCommand.buildCommand("charter"), "An all-purpose command for the Charter plugin!");
            commands.registrar().register(SettlementCommand.buildCommand("settlement"), "Manages settlement interactions!");
        });
    }
}
