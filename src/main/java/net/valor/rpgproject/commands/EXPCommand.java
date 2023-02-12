package net.valor.rpgproject.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.valor.rpgproject.players.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.itemutils.ItemBuilder;

/**
 * @author Projekt Valor
 * @since 2/10/2023
 */
public class EXPCommand {
    public EXPCommand() {
        CommandAPICommand addExpCommand = new CommandAPICommand("add")
                .withArguments(new IntegerArgument("amount"))
                .withPermission("rpgproject.exp.add")
                .executesPlayer((sender, args) -> {
                    PlayerHandler.getInstance().getRPGPlayer((Player) args[0]).ifPresent(rpgPlayer -> {
                        rpgPlayer.addExperience((int) args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou gave" + ((Player) args[0]).getName() + " &e" + args[1] + " &aexperiance!"));
                    });
                });

        new CommandAPICommand("exp")
                .withSubcommand(addExpCommand)
                .executesPlayer((sender, args) -> {
                    PlayerHandler.getInstance().getRPGPlayer(sender).ifPresent(rpgPlayer -> {
                        sender.getWorld().dropItem(sender.getLocation(), new ItemBuilder(Material.IRON_INGOT).setCustomModelData(1));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYour level is &e" + rpgPlayer.getLevel() + "&a and your experience is &e" + rpgPlayer.getExperience()));
                    });
                })
                .register();
    }
}
