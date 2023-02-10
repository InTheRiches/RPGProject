package net.valor.rpgproject.players;

import net.valor.rpgproject.utils.Database;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Projekt Valor
 * @since 2/9/2023
 */
public class PlayerHandler {
    private final List<RPGPlayer> players;
    private static PlayerHandler instance;

    public PlayerHandler() {
        this.players = new ArrayList<>();
    }

    public static PlayerHandler getInstance() {
        if (instance == null) {
            instance = new PlayerHandler();
        }
        return instance;
    }

    public void addPlayer(Player player) {
        if (!Database.getInstance().doesPlayerExist(player)) {
            Database.getInstance().setupPlayer(player);
        }

        int level = Database.getInstance().getLevel(player);
        int experience = Database.getInstance().getXP(player);

        players.add(new RPGPlayer(player, level, experience));
    }

    public void removePlayer(Player player) {
        RPGPlayer rpgPlayer = getRPGPlayer(player).get();
        players.remove(rpgPlayer);

        Database.getInstance().setLevel(player, rpgPlayer.getLevel());
        Database.getInstance().setXP(player, rpgPlayer.getExperience());
    }

    public Optional<RPGPlayer> getRPGPlayer(Player player) {
        return players.stream().filter(rpgPlayer -> rpgPlayer.getPlayer().equals(player)).findFirst();
    }
}
