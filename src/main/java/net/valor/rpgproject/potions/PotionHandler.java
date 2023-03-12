package net.valor.rpgproject.potions;

import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.potions.types.HealthPotion;
import net.valor.rpgproject.potions.types.ProgressiveHealthPotion;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionHandler {
    private static PotionHandler instance;
    
    private final List<Potion> potions;
    private final List<ProgressivePotion> progressivePotions;
    
    public PotionHandler() {
        this.potions = new ArrayList<>();
        this.progressivePotions = new ArrayList<>();

        ConfigurationSection ms = RPGProject.getInstance().getConfig().getConfigurationSection("potions");

        for (String str : ms.getKeys(false)) {
            switch(ms.getString(str + ".type")) {
                case "health" ->
                        potions.add(new HealthPotion(str, ms.getString(str + ".formatted-name"), Material.valueOf(ms.getString(str + ".material")), ms.getInt(str + ".custom-model-data")));
                case "progressive-health" ->
                        progressivePotions.add(new ProgressiveHealthPotion(str, ms.getString(str + ".formatted-name"), Material.valueOf(ms.getString(str + ".material")), ms.getInt(str + ".custom-model-data")));
                default ->
                    throw new IllegalStateException("Unexpected value: " + ms.getString(str + ".type"));
            }
        }
    }

    public Optional<Potion> getPotion(Material material, int customModelData) {
        return potions.stream().filter(potion -> potion.getMaterialType() == material && potion.getCustomModelData() == customModelData).findFirst();
    }

    public Optional<Potion> getPotion(String id) {
        return potions.stream().filter(potion -> potion.getId().equals(id)).findFirst();
    }

    public Optional<ProgressivePotion> getProgressivePotion(Material material, int customModelData) {
        return progressivePotions.stream().filter(potion -> potion.getMaterialType() == material && potion.getCustomModelData() == customModelData).findFirst();
    }

    public static PotionHandler getInstance() {
        if (instance == null)
            instance = new PotionHandler();

        return instance;
    }

    public boolean isItemPotion(ItemStack item) {
        Optional<Potion> potion = getPotion(item.getType(), item.getItemMeta().getCustomModelData());
        Optional<ProgressivePotion> progressivePotion = getProgressivePotion(item.getType(), item.getItemMeta().getCustomModelData());

        return potion.isPresent() || progressivePotion.isPresent();
    }

    public boolean isPotionProgressive(ItemStack item) {
        Optional<ProgressivePotion> progressivePotion = getProgressivePotion(item.getType(), item.getItemMeta().getCustomModelData());

        return progressivePotion.isPresent();
    }

    public boolean isPotionRegular(ItemStack item) {
        Optional<Potion> potion = getPotion(item.getType(), item.getItemMeta().getCustomModelData());

        return potion.isPresent();
    }

    public boolean isPotionProgressive(Potion potion) {
        return progressivePotions.contains(potion);
    }

    public boolean isPotionRegular(Potion potion) {
        return potions.contains(potion);
    }
}