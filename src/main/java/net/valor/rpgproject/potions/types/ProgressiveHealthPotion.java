package net.valor.rpgproject.potions.types;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.players.RPGPlayer;

// TODO CONSIDER MAKING A PROGRESSIVEPOTION CLASS THAT EXTENDS POTION
public class ProgressiveHealthPotion extends HealthPotion {

    private final int tier1Duration;
    private final int tier2Duration;
    private final int tier3Duration;

    public ProgressiveHealthPotion(String id, Material materialType, int customModelData, int tier1Buff, int tier2Buff, int tier3Buff, int tier1Duration, int tier2Duration, int tier3Duration) {
        super("progressive-health", id, materialType, customModelData, tier1Buff, tier2Buff, tier3Buff);

        this.tier1Duration = tier1Duration;
        this.tier2Duration = tier2Duration;
        this.tier3Duration = tier3Duration;
    }

    /**
     * Gets the amount of time required to heal in seconds.
     * 
     * @param tier The tier of the potion.
     * @return The amount of time required to heal in seconds.
     */
    public int getHealTime(int tier) {
        return switch (tier) {
            case 1 -> tier1Duration;
            case 2 -> tier2Duration;
            case 3 -> tier3Duration;
            default -> 0;
        };
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