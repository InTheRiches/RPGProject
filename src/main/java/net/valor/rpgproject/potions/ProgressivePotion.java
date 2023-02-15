package net.valor.rpgproject.potions;

import org.bukkit.Material;
import net.valor.rpgproject.players.RPGPlayer;

public abstract class ProgressivePotion extends Potion {

    public ProgressivePotion(String type, String id, Material materialType, int customModelData) {
        super(type, id, materialType, customModelData);
    }

    abstract public void use(RPGPlayer player, int duration, int totalhealth);

    public void use(RPGPlayer player) {
        // nothing
    }
}