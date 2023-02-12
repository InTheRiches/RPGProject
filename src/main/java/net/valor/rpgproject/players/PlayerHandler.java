package net.valor.rpgproject.players;

import net.valor.rpgproject.players.classes.Class;
import net.valor.rpgproject.players.classes.ClassHandler;
import net.valor.rpgproject.utils.Database;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.xml.crypto.Data;
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
        Optional<Class> playerClass = ClassHandler.getInstance().getClass(Database.getInstance().getClassID(player));
        if (!playerClass.isPresent()) {
            throw new IllegalStateException("Player class '" + Database.getInstance().getClassID(player) + "' is not present for " + player.getUniqueId() + "!");
        }

        int level = Database.getInstance().getLevel(player);
        int experience = Database.getInstance().getEXP(player);
        int health = Database.getInstance().getHealth(player);
        int coins = Database.getInstance().getCoins(player);
        ItemStack[] resourceBag = Database.getInstance().getResourceBag(player);

        players.add(new RPGPlayer(player, playerClass.get(), level, experience, health, coins, resourceBag));
    }

    public void setupPlayer(Player player, String classID) {
        if (getRPGPlayer(player).isPresent() || Database.getInstance().doesPlayerExist(player)) {
            throw new IllegalStateException("Player is already in the player list!");
        }

        Database.getInstance().setupPlayer(player, classID);

        addPlayer(player);
    }

    public void removePlayer(Player player) {
        RPGPlayer rpgPlayer = getRPGPlayer(player).get();
        players.remove(rpgPlayer);

        Database.getInstance().setLevel(player, rpgPlayer.getLevel());
        Database.getInstance().setEXP(player, rpgPlayer.getExperience());
        Database.getInstance().setHealth(player, rpgPlayer.getHealth());
        Database.getInstance().setCoins(player, rpgPlayer.getCoins());
        Database.getInstance().setResourceBag(player, rpgPlayer.getResourceBag());
    }

    public Optional<RPGPlayer> getRPGPlayer(Player player) {
        return players.stream().filter(rpgPlayer -> rpgPlayer.getPlayer().equals(player)).findFirst();
    }
}
