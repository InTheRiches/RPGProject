package net.valor.rpgproject.potions.types;

import org.bukkit.Material;

import net.valor.rpgproject.players.RPGPlayer;
import net.valor.rpgproject.potions.Potion;

public class HealthPotion extends Potion {

    public HealthPotion(String id, Material materialType, int customModelData) {
        super("health", id, materialType, customModelData);
    }

    public HealthPotion(String type, String id, Material materialType, int customModelData) {
        super(type, id, materialType, customModelData);
    }

    public void use(RPGPlayer player, int health) {
        player.setHealth(player.getHealth() + health);
    }
}