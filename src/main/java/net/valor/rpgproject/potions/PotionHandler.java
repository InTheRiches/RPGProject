package net.valor.rpgproject.potions;

import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.potions.types.HealthPotion;
import net.valor.rpgproject.potions.types.ProgressiveHealthPotion;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class PotionHandler {
    private static PotionHandler instance;
    
    private final List<Potion> potions;
    
    public PotionHandler() {
        this.potions = new ArrayList<>();

        ConfigurationSection ms = RPGProject.getInstance().getConfig().getConfigurationSection("potions");

        for (String str : ms.getKeys(false)) {
            switch(ms.getString(str + ".type")) {
                case "health" -> {
                    potions.add(new HealthPotion(str, ms.getString(str + ".name"), ms.getString(str + ".description")));
                }
                case "progressive-health" -> {
                    potions.add(new ProgressiveHealthPotion(str, ms.getString(str + ".name"), ms.getString(str + ".description")));
                }
                default ->
                    throw new IllegalStateException("Unexpected value: " + ms.getString(str + ".type"));
            }
        }
    }

    public static PotionHandler getInstance() {
        if (instance == null)
            instance = new PotionHandler();

        return instance;
    }
}