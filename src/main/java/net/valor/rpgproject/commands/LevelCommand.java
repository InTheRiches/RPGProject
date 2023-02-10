package net.valor.rpgproject.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.valor.rpgproject.players.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Level;

/**
 * @author Projekt Valor
 * @since 2/9/2023
 */
public class LevelCommand {
    public LevelCommand() {
        CommandAPICommand addLevelCommand = new CommandAPICommand("add")
//                .withPermission("rpgproject.level.add")
                .withArguments(new PlayerArgument("player"), new IntegerArgument("amount"))
                .executesPlayer((sender, args) -> {
                    PlayerHandler.getInstance().getRPGPlayer((Player) args[0]).ifPresent(rpgPlayer -> {
                        rpgPlayer.addLevel((int) args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou gave" + ((Player) args[0]).getName() + " &e" + args[1] + " &alevels!"));
                    });
                });

        new CommandAPICommand("level")
                .withSubcommand(addLevelCommand)
                .executesPlayer((sender, args) -> {
                    PlayerHandler.getInstance().getRPGPlayer(sender).ifPresent(rpgPlayer -> {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYour level is &e" + rpgPlayer.getLevel() + "&a and your experience is &e" + rpgPlayer.getExperience()));
                    });
                })
                .register();
    }
}
