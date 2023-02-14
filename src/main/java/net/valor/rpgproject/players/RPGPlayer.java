package net.valor.rpgproject.players;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.armor.Armor;
import net.valor.rpgproject.armor.ArmorLoader;
import net.valor.rpgproject.players.classes.Class;
import net.valor.rpgproject.potions.Potion;
import net.valor.rpgproject.potions.PotionHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.misc.EventListener;
import redempt.redlib.misc.Task;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Projekt Valor
 * @since 2/9/2023
 */
public class RPGPlayer {
    private final Player player;
    private int level;
    private int experience;
    private float health;
    private int maxHealth;
    private int coins;
    private final Class playerClass;
    private boolean disabled;
    // TODO IF IT OVERFILLS, SEND A ITEM CREATION PACKET TO PLAYER, AND THEN LISTEN FOR A ITEM PICKUP PACKET
    private ResourceBag resourceBag;
    private float regerationBuff;

    public RPGPlayer(Player player, Class playerClass, int level, int experience, float health, int coins, ItemStack[] resourceBag) {
        this.player = player;
        this.playerClass = playerClass;
        this.level = level;
        this.experience = experience;
        this.health = health;
        this.coins = coins;
        this.disabled = false;
        this.resourceBag = new ResourceBag(this, resourceBag);

        this.setup();
        this.refreshArmorBuffs();
    }

    public void refreshArmorBuffs() {
        int additionalArmorBuffs = 0;
        this.regerationBuff = 0;
        this.maxHealth = 100;

        for (ItemStack item : this.player.getInventory().getArmorContents()) {
            if (item == null)
                continue;

            if (item.getItemMeta() == null)
                continue;

            Optional<Armor> optionalArmor = ArmorLoader.getInstance().getArmor(item.getType(), item.getItemMeta().getCustomModelData());

            if (optionalArmor.isEmpty())
                continue;

            Armor armor = optionalArmor.get();
            additionalArmorBuffs += armor.getHealthBuff();
            this.regerationBuff += armor.getRegenerationBuff();
        }

        if (this.health >= this.maxHealth) {
            System.out.println(health + " >= " + maxHealth);
            this.health = 100 + additionalArmorBuffs;
        }

        this.maxHealth = 100 + additionalArmorBuffs;
    }

    private void setup() {
        AtomicLong lastImpacted = new AtomicLong(100000);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (disabled) {
                    cancel();
                    return;
                }

                // TODO MAKE SURE THEY HAVENT BEEN DAMAGED FOR 30s
//                if (System.currentTimeMillis() - lastImpacted.get() <= 30000 || !(health < maxHealth))
//                    return;
//
//                addHealth((0.005f + regerationBuff) * maxHealth);

                player.sendTitle(" ", ChatColor.GREEN + "Health: " + ChatColor.WHITE + (int) health + ChatColor.GREEN + "/" + ChatColor.WHITE + maxHealth, 0, 20, 0);
            }
        }.runTaskTimerAsynchronously(RPGProject.getInstance(), 0, 20);

        new EventListener<>(RPGProject.getInstance(), ArmorEquipEvent.class, (l, e) -> {
            if (this.disabled) {
                l.unregister();
                return;
            }

            if (e.getPlayer() != this.player)
                return;

            Task.syncDelayed(this::refreshArmorBuffs);
        });

        new EventListener<>(RPGProject.getInstance(), PlayerItemConsumeEvent.class, (l, e) -> {
            if (this.disabled) {
                l.unregister();
                return;
            }

            if (e.getPlayer() != this.player)
                return;

            if (e.getItem().getItemMeta() == null)
                return;

            if (!e.getItem().getItemMeta().hasCustomModelData())
                return;

            Optional<Potion> potionOptional = PotionHandler.getInstance().getPotion(e.getItem().getType(), e.getItem().getItemMeta().getCustomModelData());
            if (potionOptional.isEmpty())
                return;

            int tier = 1;
            // if the item is tier 1, its name will contain T1, if its tier 2, it will contain T2, etc.
            if (e.getItem().getItemMeta().getDisplayName().contains("T2"))
                tier = 2;
            else if (e.getItem().getItemMeta().getDisplayName().contains("T3"))
                tier = 3;

            potionOptional.get().use(this, tier);
        });

        new EventListener<>(RPGProject.getInstance(), EntityDamageEvent.class, (l, e) -> {
            if (this.disabled) {
                l.unregister();
                return;
            }

            if (e.getEntity() != this.player)
                return;

            this.removeHealth((float) e.getFinalDamage());

            e.setCancelled(true);
        });

        new EventListener<>(RPGProject.getInstance(), EntityDamageByEntityEvent.class, (l, e) -> {
            if (this.disabled) {
                l.unregister();
                return;
            }

            if (e.getEntity() != this.player && e.getDamager() != this.player)
                return;

            // save this, so I can track how long ago they were damaged
            lastImpacted.set(System.currentTimeMillis());
        });
    }

    public Player getPlayer() {
        return player;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public float getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCoins() {
        return coins;
    }

    public ResourceBag getResourceBag() {
        return resourceBag;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public Class getPlayerClass() {
        return this.playerClass;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public void removeHealth(float healthToRemove) {
        // makes sure it doesn't go below 0
        if (this.health - healthToRemove <= 0) {
            this.health = 0;

            // TODO MAKE PLAYER DIE
            
            return;
        }

        this.health -= healthToRemove;
    }

    public void addHealth(float healthToAdd) {
        // makes sure it doesn't go above 100
        if (this.health + healthToAdd >= this.maxHealth) {
            this.health = this.maxHealth;
            return;
        }

        this.health += healthToAdd;
    }

    public void setHealth(float healthToSet) {
        // makes sure it doesn't go above 20
        if (healthToSet >= this.maxHealth) {
            this.health = this.maxHealth;
            return;
        }

        this.health = healthToSet;
    } 

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Adds experience to the player
     * 
     * Levels 1 through 22
     * 
     * (y/(0.45-y/100))^2.15
     * 
     * Levels 23 - 35
     *
     * (y/0.23)^2.15
     *
     * Levels 36 - 100
     *
     * (y/0.95)^3
     * 
     * @param experienceToAdd the amount of experience to add
     * @return the amount of experience that was not added
     */

    public void addExperience(int experienceToAdd) {
        int experianceNeedForLevelUp = 0;
        if (this.level <= 22) {
            experianceNeedForLevelUp = (int) Math.pow((this.level / (0.45 - this.level / 100)), 2.15);
        } else if (this.level <= 35) {
            experianceNeedForLevelUp = (int) Math.pow((this.level / 0.23), 2.15);
        } else {
            experianceNeedForLevelUp = (int) Math.pow((this.level / 0.95), 3);
        }
        if (this.experience + experienceToAdd >= experianceNeedForLevelUp) {
            this.level++;
            this.experience = (this.experience + experienceToAdd) - experianceNeedForLevelUp;
            return;
        }

        this.experience += experienceToAdd;
    }

    public void addLevel(int level) {
        this.level += level;
    }

    public void disable() {
        this.disabled = true;
    }
}
