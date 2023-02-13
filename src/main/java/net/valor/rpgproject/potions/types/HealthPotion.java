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

    public int getHealth(int tier) {
        switch (tier) {
            case 1:
                return 24;
            case 2:
                return 10;
            case 3:
                return 5;
            default:
                return 0;
        }
    }

    public void use(RPGPlayer player, int tier) {
        player.setHealth(player.getHealth() + getHealth(tier));
    }
}