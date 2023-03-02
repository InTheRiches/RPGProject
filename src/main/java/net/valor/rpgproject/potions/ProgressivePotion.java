package net.valor.rpgproject.potions;

import org.bukkit.Material;
import net.valor.rpgproject.players.RPGPlayer;

public abstract class ProgressivePotion {

    protected final String type;
    protected final String formattedString;
    protected final String id;
    protected final Material materialType;
    protected final int customModelData;

    public ProgressivePotion(String type, String formattedString, String id, Material materialType, int customModelData) {
        this.type = type;
        this.id = id;
        this.formattedString = formattedString;
        this.materialType = materialType;
        this.customModelData = customModelData;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getFormattedString() {
        return formattedString;
    }

    public Material getMaterialType() {
        return materialType;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    abstract public void use(RPGPlayer player, int duration, int totalBuff);
}