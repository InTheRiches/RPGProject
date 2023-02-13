package net.valor.rpgproject.potions;

import org.bukkit.Material;

import net.valor.rpgproject.players.RPGPlayer;

/**
 * These potion classes are used to specific types of potions, but not actual potions in possession of the player.
 *
 * @author Projekt Valor
 * @since 2/10/2023
 */
public abstract class Potion {
    protected final String type;
    protected final String id;
    protected final Material materialType;
    protected final int customModelData;

    public Potion(String type, String id, Material materialType, int customModelData) {
        this.type = type;
        this.id = id;
        this.materialType = materialType;
        this.customModelData = customModelData;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Material getMaterialType() {
        return materialType;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    abstract public void use(RPGPlayer player, int tier);
}