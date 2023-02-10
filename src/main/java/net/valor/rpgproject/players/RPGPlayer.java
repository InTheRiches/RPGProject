package net.valor.rpgproject.players;

import org.bukkit.entity.Player;

/**
 * @author Projekt Valor
 * @since 2/9/2023
 */
public class RPGPlayer {
    private final Player player;
    private int level;
    private int experience;
    private int health;
    private int coins;

    public RPGPlayer(Player player, int level, int experience, int health, int coins) {
        this.player = player;
        this.level = level;
        this.experience = experience;
        this.health = health;
        this.coins = coins;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getHealth() {
        return health;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public void removeHealth(int healthToRemove) {
        // makes sure it doesnt go below 0
        if (this.health - healthToRemove <= 0) {
            this.health = 0;

            // TODO MAKE PLAYER DIE
            
            return;
        }

        this.health -= healthToRemove;
    }

    public void addHealth(int healthToAdd) {
        // makes sure it doesnt go above 20
        if (this.health + healthToAdd >= 20) {
            this.health = 20;
            return;
        }

        this.health += healthToAdd;
    }

    public void setHealth(int health) {
        // makes sure it doesnt go above 20
        if (health >= 20) {
            this.health = 20;
            return;
        }

        this.health = health;
    } 

    public void setLevel(int level) {
        this.level = level;
    }

    public void addExperience(int experience) {
        this.experience += experience;
    }

    public void addLevel(int level) {
        this.level += level;
    }
}
