package net.valor.rpgproject.potions.types;

import org.bukkit.Material;

import net.valor.rpgproject.players.RPGPlayer;
import net.valor.rpgproject.potions.Potion;

public class HealthPotion extends Potion {

    public HealthPotion(String id, Material materialType, int customModelData, int tier1Buff, int tier2Buff, int tier3Buff) {
        super("health", id, materialType, customModelData, tier1Buff, tier2Buff, tier3Buff);
    }

    public HealthPotion(String type, String id, Material materialType, int customModelData, int tier1Buff, int tier2Buff, int tier3Buff) {
        super(type, id, materialType, customModelData, tier1Buff, tier2Buff, tier3Buff);
    }

    public int getHealth(int tier) {
        switch (tier) {
            case 1:
                return tier1Buff;
            case 2:
                return tier2Buff;
            case 3:
                return tier3Buff;
            default:
                return 0;
        }
    }

    public void use(RPGPlayer player, int tier) {
        player.setHealth(player.getHealth() + getHealth(tier));
    }
}