package net.valor.rpgproject.players;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.projektcontingency.titanium.gui.InventoryMenu;
import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.players.classes.Class;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.misc.EventListener;
import redempt.redlib.misc.Task;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Projekt Valor
 * @since 2/9/2023
 */
public class RPGPlayer {
    private final Player player;
    private int level;
    private int experience;
    private int health;
    private int coins;
    private Class playerClass;
    private boolean disabled;
    // TODO IF IT OVERFILLS, SEND A ITEM CREATION PACKET TO PLAYER, AND THEN LISTEN FOR A ITEM PICKUP PACKET
    private ItemStack[] resourceBag; // maximum size of 14 items

    public RPGPlayer(Player player, Class playerClass, int level, int experience, int health, int coins, ItemStack[] resourceBag) {
        this.player = player;
        this.playerClass = playerClass;
        this.level = level;
        this.experience = experience;
        this.health = health;
        this.coins = coins;
        this.disabled = false;
        this.resourceBag = resourceBag;

        this.setup();
    }

    private void setup() {
        ItemStack resourceBag = new ItemBuilder(Material.valueOf(RPGProject.getInstance().getConfig().getString("static-items.resource-bag.material")))
                .setName(RPGProject.getInstance().getConfig().getString("static-items.resource-bag.name"))
                .setLore(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("static-items.resource-bag.lore")).split("\n"))
                .setCustomModelData(RPGProject.getInstance().getConfig().getInt("static-items.resource-bag.custom-model-data"));

        // Open resource bag event.
        new EventListener<>(RPGProject.getInstance(), InventoryClickEvent.class, (l, e) -> {
            if (this.disabled) {
                l.unregister();
                return;
            }

            if (!player.getUniqueId().equals(this.player.getUniqueId())) return;

            if (e.getView().getTitle().equals("Resource Bag") && (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.PLACE_SOME))) {
                e.setCancelled(true);

                return;
            }
            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
            if (!e.getCurrentItem().getType().equals(resourceBag.getType()) || e.getCurrentItem().getItemMeta().getCustomModelData() != RPGProject.getInstance().getConfig().getInt("static-items.resource-bag.custom-model-data")) return;

            e.setCancelled(true);

            Task.syncDelayed(this::openResourceBag);
        });

        // Close resource bag event
        new EventListener<>(RPGProject.getInstance(), InventoryCloseEvent.class, (l, e) -> {
            if (this.disabled) {
                l.unregister();
                return;
            }

            if (!player.getUniqueId().equals(this.player.getUniqueId())) return;
            if (!e.getView().getTitle().equals("Resource Bag")) return;
            if (e.getView().getCursor() == null) return;

            // stop the cursor item from being added to the player's inventory.
            e.getView().setCursor(null);
        });

        new EventListener<>(RPGProject.getInstance(), PlayerDropItemEvent.class, (l, e) -> {
            if (disabled) {
                l.unregister();
                return;
            }

            if (!player.getUniqueId().equals(this.player.getUniqueId())) return;

            // check if the item was any of the items inside the resource bag, if so remove it from the resource bag
            ItemStack[] bag = this.resourceBag;
            for (int i = 0, bagLength = bag.length; i < bagLength; i++) {
                ItemStack item = bag[i];

                if (item == null) continue;
                if (item.isSimilar(e.getItemDrop().getItemStack())) {
                    if (this.resourceBag[i].getAmount() - e.getItemDrop().getItemStack().getAmount() <= 0) {
                        this.resourceBag[i] = null;
                        return;
                    }

                    this.resourceBag[i].setAmount(this.resourceBag[i].getAmount() - e.getItemDrop().getItemStack().getAmount());
                }
            }
        });

        new EventListener<>(RPGProject.getInstance(), EntityPickupItemEvent.class, (l, e) -> {
            System.out.println("EntityPickupItemEvent");
            if (disabled) {
                l.unregister();
                return;
            }

            if (!(e.getEntity() instanceof Player player)) return;
            if (!player.getUniqueId().equals(this.player.getUniqueId())) return;
            if (e.getItem().getItemStack().getItemMeta() == null) return;

            e.setCancelled(true);

            for (String str : RPGProject.getInstance().getConfig().getStringList("resource-bag-pickup-items")) {
                Material material = Material.valueOf(str.split(":")[0]);
                int data = Integer.parseInt(str.split(":")[1]);

                if (!e.getItem().getItemStack().getType().equals(material) || e.getItem().getItemStack().getItemMeta().getCustomModelData() != data) return;

                // add the item to the resource bag
                boolean foundSlot = false;

                ItemStack[] bag = this.resourceBag;
                for (int i = 0, bagLength = bag.length; i < bagLength; i++) {
                    ItemStack item = bag[i];

                    if (item == null) continue;

                    if (item.isSimilar(e.getItem().getItemStack()) && this.resourceBag[i].getAmount() + e.getItem().getItemStack().getAmount() <= 64) {
                        this.resourceBag[i].setAmount(this.resourceBag[i].getAmount() + e.getItem().getItemStack().getAmount());
                        foundSlot = true;
                        break;
                    }
                }

                if (!foundSlot) {
                    for (int i = 0, bagLength = bag.length; i < bagLength; i++) {
                        ItemStack item = bag[i];

                        if (item == null) {
                            this.resourceBag[i] = e.getItem().getItemStack();
                            foundSlot = true;
                            break;
                        }
                    }
                }

                if (!foundSlot) {
                    // TODO DROP THE ITEM
                }
            }

            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.COLLECT);
            packet.getIntegers().write(0, e.getItem().getEntityId());
            packet.getIntegers().write(1, this.player.getEntityId());
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, packet);
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }

            e.getItem().remove();
        });

        this.player.getInventory().setItem(8, resourceBag);
    }

    public void openResourceBag() {
        InventoryMenu menu = new InventoryMenu(18, "Resource Bag");

        ItemStack[] bag = this.resourceBag;
        for (int i = 0, bagLength = bag.length; i < bagLength; i++) {
            ItemStack item = bag[i];

            ItemButton button = ItemButton.create(item, (e) -> {
                e.setCancelled(false);
            });

            menu.addButton(i, button);
        }

        menu.open(player);
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

    public int getHealth() {
        return health;
    }

    public int getCoins() {
        return coins;
    }

    public ItemStack[] getResourceBag() {
        return resourceBag;
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

    public void removeHealth(int healthToRemove) {
        // makes sure it doesn't go below 0
        if (this.health - healthToRemove <= 0) {
            this.health = 0;

            // TODO MAKE PLAYER DIE
            
            return;
        }

        this.health -= healthToRemove;
    }

    public void addHealth(int healthToAdd) {
        // makes sure it doesn't go above 100
        if (this.health + healthToAdd >= 100) {
            this.health = 100;
            return;
        }

        this.health += healthToAdd;
    }

    public void setHealth(int health) {
        // makes sure it doesn't go above 20
        if (health >= 20) {
            this.health = 20;
            return;
        }

        this.health = health;
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
