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
    protected final int tier1Buff;
    protected final int tier2Buff;
    protected final int tier3Buff;

    public Potion(String type, String id, Material materialType, int customModelData, int tier1Buff, int tier2Buff, int tier3Buff) {
        this.type = type;
        this.id = id;
        this.materialType = materialType;
        this.customModelData = customModelData;
        this.tier1Buff = tier1Buff;
        this.tier2Buff = tier2Buff;
        this.tier3Buff = tier3Buff;
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

    public int getTier1Buff() {
        return tier1Buff;
    }

    public int getTier2Buff() {
        return tier2Buff;
    }

    public int getTier3Buff() {
        return tier3Buff;
    }

    abstract public void use(RPGPlayer player, int tier);
}