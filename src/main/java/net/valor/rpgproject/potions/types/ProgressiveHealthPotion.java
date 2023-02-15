package net.valor.rpgproject.potions.types;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.players.RPGPlayer;
import net.valor.rpgproject.potions.ProgressivePotion;

// TODO CONSIDER MAKING A PROGRESSIVEPOTION CLASS THAT EXTENDS POTION
public class ProgressiveHealthPotion extends ProgressivePotion {

    public ProgressiveHealthPotion(String id, Material materialType, int customModelData) {
        super("progressive-health", id, materialType, customModelData);
    }

    public void use(RPGPlayer player, int duration, int totalhealth) {
        AtomicInteger amountToHeal = new AtomicInteger(totalhealth);
        int amountEachIncrement = duration / totalhealth;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (amountToHeal.get() <= 0) {
                    cancel();
                    return;
                }

                player.addHealth(amountEachIncrement);

                amountToHeal.set(amountToHeal.get() - amountEachIncrement);
            }
        }.runTaskTimer(RPGProject.getInstance(), 0, 20L);
    }
}