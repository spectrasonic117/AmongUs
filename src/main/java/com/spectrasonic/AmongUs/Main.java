// Main.java actualizado
package com.spectrasonic.AmongUs;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.AmongUs.Commands.Commands;
import com.spectrasonic.AmongUs.Utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerCommands();
        registerEvents();
        MessageUtils.sendStartupMessage(this);
    }

    @Override
    public void onDisable() {
        MessageUtils.sendShutdownMessage(this);
    }

    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new Commands(this));
    }

    public void registerEvents() {
        // Eventos si son necesarios
    }
}
