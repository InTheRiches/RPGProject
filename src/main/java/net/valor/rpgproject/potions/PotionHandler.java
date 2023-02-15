package net.valor.rpgproject.potions;

import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.potions.types.HealthPotion;
import net.valor.rpgproject.potions.types.ProgressiveHealthPotion;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionHandler {
    private static PotionHandler instance;
    
    private final List<Potion> potions;
    
    public PotionHandler() {
        this.potions = new ArrayList<>();

        ConfigurationSection ms = RPGProject.getInstance().getConfig().getConfigurationSection("potions");

        for (String str : ms.getKeys(false)) {
            switch(ms.getString(str + ".type")) {
                case "health" ->
                        potions.add(new HealthPotion(str, Material.valueOf(ms.getString(str + ".material")), ms.getInt(str + ".custom-model-data")));
                case "progressive-health" ->
                        potions.add(new ProgressiveHealthPotion(str, Material.valueOf(ms.getString(str + ".material")), ms.getInt(str + ".custom-model-data")));
                default ->
                    throw new IllegalStateException("Unexpected value: " + ms.getString(str + ".type"));
            }
        }
    }

    public Optional<Potion> getPotion(Material material, int customModelData) {
        return potions.stream().filter(potion -> potion.getMaterialType() == material && potion.getCustomModelData() == customModelData).findFirst();
    }

    public static PotionHandler getInstance() {
        if (instance == null)
            instance = new PotionHandler();

        return instance;
    }
}