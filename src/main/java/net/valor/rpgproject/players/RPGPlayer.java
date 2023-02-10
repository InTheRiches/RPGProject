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

    public RPGPlayer(Player player, int level, int experience, int health) {
        this.player = player;
        this.level = level;
        this.experience = experience;
        this.health = health;
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
