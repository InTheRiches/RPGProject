package net.valor.rpgproject.potions.types;

import org.bukkit.Material;

import net.valor.rpgproject.players.RPGPlayer;
import net.valor.rpgproject.potions.Potion;

public class HealthPotion extends Potion {

    public HealthPotion(String id, String formattedString, Material materialType, int customModelData) {
        super("health", formattedString, id, materialType, customModelData);
    }

    public HealthPotion(String type, String formattedString, String id, Material materialType, int customModelData) {
        super(type, formattedString, id, materialType, customModelData);
    }

    public void use(RPGPlayer player, int health) {
        player.setHealth(player.getHealth() + health);
    }
}