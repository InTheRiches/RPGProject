package net.valor.rpgproject.players;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Projekt Valor
 * @since 2/12/2023
 */
public class PlayerPAPIExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "rpgproject";
    }

    @Override
    public @NotNull String getAuthor() {
        return "riches";
    }

    @Override
    public @NotNull String getVersion() {
        return "A.01";
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        Optional<RPGPlayer> rpgPlayer = PlayerHandler.getInstance().getRPGPlayer(p);
        if (rpgPlayer.isEmpty()) return null;

        RPGPlayer player = rpgPlayer.get();

        switch (identifier) {
            case "health" -> {
                return "" + player.getHealth();
            }
            case "max_health" -> {
                return "" + player.getMaxHealth();
            }
        }

        return null;
    }
}
