package net.valor.rpgproject.armor;

import org.bukkit.Material;

/**
 * @author Projekt Valor
 * @since 2/12/2023
 */
public class Armor {
    private final String id;
    private final Material material;
    private final int customModelData;
    private final int healthBuff;
    private final float regenerationBuff;

    public Armor(String id, Material material, int customModelData, int healthBuff, float regenerationBuff) {
        this.id = id;
        this.material = material;
        this.customModelData = customModelData;
        this.healthBuff = healthBuff;
        this.regenerationBuff = regenerationBuff;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public int getHealthBuff() {
        return healthBuff;
    }

    public float getRegenerationBuff() {
        return regenerationBuff;
    }
}
