package net.valor.rpgproject.potions.types;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.scheduler.BukkitRunnable;

import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.players.RPGPlayer;

public class ProgressiveHealthPotion extends HealthPotion {
    public ProgressiveHealthPotion(String id, String name, String description) {
        super("progressive-health", id, name, description);
    }

    /**
     * Gets the amount of time required to heal in seconds.
     * 
     * @param tier The tier of the potion.
     * @return The amount of time required to heal in seconds.
     */
    public int getHealTime(int tier) {
        switch (tier) {
            case 1:
                return 3;
            case 2:
                return 10;
            case 3:
                return 25;
            default:
                return 0;
        }
    }

    public void use(RPGPlayer player, int tier) {
        AtomicInteger amountToHeal = new AtomicInteger(getHealth(tier));
        int amountEachIncrement = getHealTime(tier) / getHealth(tier);

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