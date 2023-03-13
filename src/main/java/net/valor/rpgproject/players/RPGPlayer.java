package net.valor.rpgproject.players;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.armor.Armor;
import net.valor.rpgproject.armor.ArmorLoader;
import net.valor.rpgproject.players.classes.Class;
import net.valor.rpgproject.potions.*;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.misc.EventListener;
import redempt.redlib.misc.Task;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

                if (System.currentTimeMillis() - lastImpacted.get() <= 30000 || !(health < maxHealth))
                    return;

                addHealth((0.005f + regerationBuff) * maxHealth);

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

            if (e.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER)) {
                e.setCancelled(true);

                if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RPGProject.getInstance(), "potion-1-duration"), PersistentDataType.INTEGER)) {
                    // its progressive
                    ProgressivePotionBundleBuilder builder = PotionBundleBuilder.fromItem(e.getItem());

                    builder.use(e.getPlayer());

                    e.setItemInMainHand(builder.build());
                    return;
                }

                // its not progressive
                PotionBundleBuilder builder = PotionBundleBuilder.fromItem(e.getItem());

                builder.use(e.getPlayer());
                e.setItemInMainHand(builder.build());
                return;
            }

            Optional<Potion> potionOptional = PotionHandler.getInstance().getPotion(e.getItem().getType(), e.getItem().getItemMeta().getCustomModelData());
            Optional<ProgressivePotion> progressivePotionOptional = PotionHandler.getInstance().getProgressivePotion(e.getItem().getType(), e.getItem().getItemMeta().getCustomModelData());
            if (potionOptional.isEmpty() && progressivePotionOptional.isEmpty())
                return;

            NamespacedKey amountBuffedKey = new NamespacedKey(RPGProject.getInstance(), "amount-buffed");
            NamespacedKey usesKey = new NamespacedKey(RPGProject.getInstance(), "uses");
            NamespacedKey durationKey = new NamespacedKey(RPGProject.getInstance(), "duration");

            ItemStack clone = e.getItem().clone();
            ItemMeta itemMeta = clone.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            int amountBuffed = 0;
            int uses = 0;

            if(container.has(amountBuffedKey , PersistentDataType.INTEGER)) {
                amountBuffed = container.get(amountBuffedKey, PersistentDataType.INTEGER);
            }
            if (container.has(usesKey, PersistentDataType.INTEGER)) {
                uses = container.get(usesKey, PersistentDataType.INTEGER);
            }

            if (progressivePotionOptional.isPresent() && container.has(durationKey, PersistentDataType.INTEGER)) {
                progressivePotionOptional.get().use(this, container.get(durationKey, PersistentDataType.INTEGER), amountBuffed);
            }
            else
                potionOptional.get().use(this, amountBuffed);

            uses--;

            if (uses <= 0) {
                e.getPlayer().getInventory().remove(e.getItem());
                e.setCancelled(true);
                return;
            }
            else {
                container.set(usesKey, PersistentDataType.INTEGER, uses);
            }

            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("potions." + potionOptional.get().getId() + ".title").replaceAll("%buff%", String.valueOf(amountBuffed)).replaceAll("%duration%", String.valueOf(amountBuffed)).replaceAll("%uses%", String.valueOf(uses)).replaceAll("%tier%", itemMeta.getPersistentDataContainer().get(new NamespacedKey(RPGProject.getInstance(), "tier"), PersistentDataType.STRING))));
            int finalAmountBuffed = amountBuffed;
            int finalUses = uses;

            itemMeta.setLore(RPGProject.getInstance().getConfig().getStringList("potions." + potionOptional.get().getId() + ".lore").stream().map(s -> ChatColor.translateAlternateColorCodes('&', s.replaceAll("%buff%", String.valueOf(finalAmountBuffed)).replaceAll("%duration%", String.valueOf(finalAmountBuffed)).replaceAll("%uses%", String.valueOf(finalUses)))).collect(Collectors.toList()));

            clone.setItemMeta(itemMeta);

            // refresh the item in the players inventory
            e.getPlayer().getInventory().setItemInMainHand(clone);
            e.setCancelled(true);
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

        new EventListener<>(RPGProject.getInstance(), EntityPickupItemEvent.class, (l, e) -> {
            if (this.disabled) {
                l.unregister();
                return;
            }

            if (e.getEntity() != this.player)
                return;

            if (e.getItem().getItemStack().getItemMeta() == null)
                return;
            
            if (!e.getItem().getItemStack().getItemMeta().hasCustomModelData())
                return;
            
            Optional<Potion> potionOptional = PotionHandler.getInstance().getPotion(e.getItem().getItemStack().getType(), e.getItem().getItemStack().getItemMeta().getCustomModelData());
            if (potionOptional.isEmpty())
                return;

            // check if the potion has its effects already determined
            if (e.getItem().getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RPGProject.getInstance(), "amount-buffed"), PersistentDataType.INTEGER))
                return;

            int tier = 1;
            // if the item is tier 1, its name will contain T1, if its tier 2, it will contain T2, etc.
            if (e.getItem().getItemStack().getItemMeta().getDisplayName().contains("T2"))
                tier = 2;
            else if (e.getItem().getItemStack().getItemMeta().getDisplayName().contains("T3"))
                tier = 3;

            int uses = ThreadLocalRandom.current().nextInt(RPGProject.getInstance().getConfig().getInt("potions." + potionOptional.get().getId() + ".tier-" + tier + ".min-uses"), RPGProject.getInstance().getConfig().getInt("potions." + potionOptional.get().getId() + ".tier-" + tier + ".max-uses") + 1);

            int amountBuffed = ThreadLocalRandom.current().nextInt(RPGProject.getInstance().getConfig().getInt("potions." + potionOptional.get().getId() + ".tier-" + tier + ".min-buff"), RPGProject.getInstance().getConfig().getInt("potions." + potionOptional.get().getId() + ".tier-" + tier + ".max-buff") + 1);

            NamespacedKey usesKey = new NamespacedKey(RPGProject.getInstance(), "uses");
            NamespacedKey amountBuffedKey = new NamespacedKey(RPGProject.getInstance(), "amount-buffed");
            NamespacedKey tierKey = new NamespacedKey(RPGProject.getInstance(), "tier");
            ItemMeta itemMeta = e.getItem().getItemStack().getItemMeta();
            itemMeta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, uses);
            itemMeta.getPersistentDataContainer().set(amountBuffedKey, PersistentDataType.INTEGER, amountBuffed);
            itemMeta.getPersistentDataContainer().set(tierKey, PersistentDataType.INTEGER, tier);

            if (potionOptional.get().getId().contains("progressive")) {
                NamespacedKey durationKey = new NamespacedKey(RPGProject.getInstance(), "duration");
                int duration = ThreadLocalRandom.current().nextInt(RPGProject.getInstance().getConfig().getInt("potions." + potionOptional.get().getId() + ".tier-" + tier + ".min-duration"), RPGProject.getInstance().getConfig().getInt("potions." + potionOptional.get().getId() + ".tier-" + tier + ".max-duration") + 1);

                itemMeta.getPersistentDataContainer().set(durationKey, PersistentDataType.INTEGER, duration);
            }

            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("potions." + potionOptional.get().getId() + ".title").replaceAll("%buff%", String.valueOf(amountBuffed)).replaceAll("%duration%", String.valueOf(amountBuffed)).replaceAll("%uses%", String.valueOf(uses)).replaceAll("%tier%", String.valueOf(tier))));
            int finalTier = tier;
            itemMeta.setLore(RPGProject.getInstance().getConfig().getStringList("potions." + potionOptional.get().getId() + ".lore").stream().map(s -> ChatColor.translateAlternateColorCodes('&', s.replaceAll("%buff%", String.valueOf(amountBuffed)).replaceAll("%duration%", String.valueOf(amountBuffed)).replaceAll("%uses%", String.valueOf(uses)).replaceAll("%tier%", String.valueOf(finalTier)))).collect(Collectors.toList()));

            e.getItem().getItemStack().setItemMeta(itemMeta);
        });

        new EventListener<>(RPGProject.getInstance(), InventoryClickEvent.class, (l, e) -> {
            if (e.getWhoClicked() != this.player) return;
            if (e.getClickedInventory() == null) return;

            System.out.println(e.getAction());
            if (e.getAction() != InventoryAction.SWAP_WITH_CURSOR) return;

            // check to make sure both potions are valid
            if (e.getCursor().getItemMeta() == null || e.getCurrentItem().getItemMeta() == null) return;
            if (!e.getCursor().getItemMeta().hasCustomModelData() || !e.getCurrentItem().getItemMeta().hasCustomModelData()) return;

            // check for if one of the items is a potion bundle
            if (e.getCursor().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER)) {
                if (!PotionHandler.getInstance().isItemPotion(e.getCurrentItem())) return;

                Optional<Potion> potionOptional = PotionHandler.getInstance().getPotion(e.getCursor().getType(), e.getCursor().getItemMeta().getCustomModelData());
                if (potionOptional.isEmpty()) {
                    throw new IllegalStateException("Potion is not valid");
                }

                PotionBundleBuilder bundle = PotionBundleBuilder.fromItem(e.getCursor());
                if (bundle.getSize() == 3) return;

                bundle.addPotion(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RPGProject.getInstance(), "amount-buffed"), PersistentDataType.INTEGER), e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RPGProject.getInstance(), "uses"), PersistentDataType.INTEGER), e.getCursor().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RPGProject.getInstance(), "tier"), PersistentDataType.INTEGER), potionOptional.get());

                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BUNDLE_INSERT, 1, 1);

                e.setCancelled(true);
                e.getView().setCursor(null);
                e.setCurrentItem(bundle.build());
                
                return;
            }
            // TODO ADD PROGRESSIVE POTION BUNDLES
            else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER)) {
                if (!PotionHandler.getInstance().isItemPotion(e.getCursor())) return;

                Optional<Potion> potionOptional = PotionHandler.getInstance().getPotion(e.getCursor().getType(), e.getCursor().getItemMeta().getCustomModelData());
                if (potionOptional.isEmpty()) {
                    throw new IllegalStateException("Potion is not valid");
                }

                PotionBundleBuilder bundle = PotionBundleBuilder.fromItem(e.getCurrentItem());
                if (bundle.getSize() == 3) return;

                bundle.addPotion(e.getCursor().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RPGProject.getInstance(), "amount-buffed"), PersistentDataType.INTEGER), e.getCursor().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RPGProject.getInstance(), "uses"), PersistentDataType.INTEGER), e.getCursor().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RPGProject.getInstance(), "tier"), PersistentDataType.INTEGER), potionOptional.get());

                e.setCancelled(true);
                e.getView().setCursor(null);
                e.setCurrentItem(bundle.build());

                return;
            }

            if (!PotionHandler.getInstance().isItemPotion(e.getCursor()) || !PotionHandler.getInstance().isItemPotion(e.getCurrentItem())) return;
            
            NamespacedKey amountBuffedKey = new NamespacedKey(RPGProject.getInstance(), "amount-buffed");
            NamespacedKey usesKey = new NamespacedKey(RPGProject.getInstance(), "uses");
            NamespacedKey durationKey = new NamespacedKey(RPGProject.getInstance(), "duration");
            NamespacedKey tierKey = new NamespacedKey(RPGProject.getInstance(), "tier");

            Potion cursorPotion = PotionHandler.getInstance().getPotion(e.getCursor().getType(), e.getCursor().getItemMeta().getCustomModelData()).get();
            Potion currentItemPotion = PotionHandler.getInstance().getPotion(e.getCurrentItem().getType(), e.getCurrentItem().getItemMeta().getCustomModelData()).get();

            if (PotionHandler.getInstance().isPotionRegular(cursorPotion) && PotionHandler.getInstance().isPotionRegular(currentItemPotion)) {
                if (!cursorPotion.getId().equals(currentItemPotion.getId())) return;

                PersistentDataContainer cursorContainer = e.getCursor().getItemMeta().getPersistentDataContainer();
                PersistentDataContainer currentItemContainer = e.getCurrentItem().getItemMeta().getPersistentDataContainer();

                // String potion3Name = currentItemPotionOptional.get().getFormattedString();
                int potion1Buff = cursorContainer.get(amountBuffedKey, PersistentDataType.INTEGER);
                int potion2Buff = currentItemContainer.get(amountBuffedKey, PersistentDataType.INTEGER);
                int potion1Uses = cursorContainer.get(usesKey, PersistentDataType.INTEGER);
                int potion2Uses = currentItemContainer.get(usesKey, PersistentDataType.INTEGER);
                int potion1Tier = cursorContainer.get(tierKey, PersistentDataType.INTEGER);
                int potion2Tier = currentItemContainer.get(tierKey, PersistentDataType.INTEGER);

                // create new regular potion bundle
                ItemStack potionBundle = new PotionBundleBuilder()
                        .addPotion(potion1Buff, potion1Uses, potion1Tier, cursorPotion)
                        .addPotion(potion2Buff, potion2Uses, potion2Tier, currentItemPotion)
                        .build();

                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BUNDLE_INSERT, 1, 1);

                // remove both items, and add the new bundle
                e.setCancelled(true);
                e.getView().setCursor(null);
                e.getInventory().setItem(e.getSlot(), potionBundle);
            }
            else if (PotionHandler.getInstance().isPotionProgressive(cursorPotion) && PotionHandler.getInstance().isPotionProgressive(currentItemPotion)) {
                // create a new progressive potion bundle
                if (!cursorPotion.getId().equals(currentItemPotion.getId())) return;

                PersistentDataContainer cursorContainer = e.getCursor().getItemMeta().getPersistentDataContainer();
                PersistentDataContainer currentItemContainer = e.getCurrentItem().getItemMeta().getPersistentDataContainer();

                int potion1Buff = cursorContainer.get(amountBuffedKey, PersistentDataType.INTEGER);
                int potion2Buff = currentItemContainer.get(amountBuffedKey, PersistentDataType.INTEGER);
                int potion1Uses = cursorContainer.get(usesKey, PersistentDataType.INTEGER);
                int potion2Uses = currentItemContainer.get(usesKey, PersistentDataType.INTEGER);
                int potion1Duration = cursorContainer.get(durationKey, PersistentDataType.INTEGER);
                int potion2Duration = currentItemContainer.get(durationKey, PersistentDataType.INTEGER);
                int potion1Tier = cursorContainer.get(tierKey, PersistentDataType.INTEGER);
                int potion2Tier = currentItemContainer.get(tierKey, PersistentDataType.INTEGER);

                // create new regular potion bundle
                ItemStack potionBundle = new ProgressivePotionBundleBuilder()
                        .addPotion(potion1Buff, potion1Uses, potion1Duration, potion1Tier, cursorPotion)
                        .addPotion(potion2Buff, potion2Uses, potion2Duration, potion2Tier, currentItemPotion)
                        .build();

                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_BUNDLE_INSERT, 1, 1);

                // remove both items, and add the new bundle
                e.setCancelled(true);
                e.getView().setCursor(null);
                e.getInventory().setItem(e.getSlot(), potionBundle);
            }
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
