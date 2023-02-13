package net.valor.rpgproject.players;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.armor.Armor;
import net.valor.rpgproject.armor.ArmorLoader;
import net.valor.rpgproject.players.classes.Class;
import net.valor.rpgproject.potions.ResourceBag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.misc.EventListener;
import redempt.redlib.misc.Task;

import java.util.Optional;

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
    private float armorRegenerationBuff;

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
        this.armorRegenerationBuff = 0;

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
            this.armorRegenerationBuff += armor.getRegenerationBuff();
        }

        if (this.health >= this.maxHealth) {
            this.health = 100 + additionalArmorBuffs;
        }

        this.maxHealth = 100 + additionalArmorBuffs;
    }

    private void setup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (disabled) {
                    cancel();
                    return;
                }

                // TODO MAKE SURE THEY HAVENT BEEN DAMAGED FOR 30s

                if (health < maxHealth) {
                    addHealth((0.005f + armorRegenerationBuff) * maxHealth);
                }

                player.sendTitle(" ", ChatColor.RED + "" + health + " / " + maxHealth);
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

//        new EventListener<>(RPGProject.getInstance(), InventoryClickEvent.class, (l, e) -> {
//            if (this.disabled) {
//                l.unregister();
//                return;
//            }
//
//            if (e.getWhoClicked() != this.player)
//                return;
//
//            if (e.getClickedInventory() == null)
//                return;
//
//            if (!e.getClickedInventory().getType().equals(InventoryType.PLAYER))
//                return;
//
//            System.out.println(e.getAction());
//
//            if (!e.getAction().equals(InventoryAction.PLACE_ALL) && !e.getAction().equals(InventoryAction.PLACE_ONE) && !e.getAction().equals(InventoryAction.PLACE_SOME) && !e.getAction().equals(InventoryAction.PICKUP_ALL) && !e.getAction().equals(InventoryAction.PICKUP_HALF) && !e.getAction().equals(InventoryAction.PICKUP_ONE) && !e.getAction().equals(InventoryAction.PICKUP_SOME) && !e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR) && !e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
//                return;
//
//            // check if the slot clicked was an equipment slot, 36-39
//            if (e.getSlot() < 36 || e.getSlot() > 39)
//                return;
//
//            System.out.println("armor slot clicked");
//
//            // refresh max health because the player equipped a new armor piece
//            Task.syncDelayed(this::refreshArmorBuffs);
//        });

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

    public void addExperience(int experience) {
        this.experience += experience;
    }

    public void addLevel(int level) {
        this.level += level;
    }

    public void disable() {
        this.disabled = true;
    }
}
