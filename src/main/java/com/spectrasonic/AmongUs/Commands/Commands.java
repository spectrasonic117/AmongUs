// Commands.java
package com.spectrasonic.AmongUs.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.spectrasonic.AmongUs.Main;
import com.spectrasonic.AmongUs.Managers.GameManager;
import org.bukkit.command.CommandSender;

@CommandAlias("amongus|au")
public class Commands extends BaseCommand {
    private final GameManager gameManager;

    public Commands(Main plugin) {
        this.gameManager = new GameManager(plugin);
    }

    @Subcommand("game start")
    @CommandPermission("amongus.admin")
    public void onStart(CommandSender sender) {
        gameManager.startGame(sender);
    }

    @Subcommand("game stop")
    @CommandPermission("amongus.admin")
    public void onStop(CommandSender sender) {
        gameManager.stopGame(sender);
    }

    @Subcommand("reload")
    @CommandPermission("amongus.admin")
    public void onReload(CommandSender sender) {
        gameManager.reloadConfig(sender);
    }

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage("§6AmongUs §7- §eComandos disponibles:");
        sender.sendMessage("§6/amongus game start §7- §eInicia el minijuego");
        sender.sendMessage("§6/amongus game stop §7- §eDetiene el minijuego");
        sender.sendMessage("§6/amongus reload §7- §eRecarga la configuración");
    }
}
