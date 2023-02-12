package net.valor.rpgproject.players;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.projektcontingency.titanium.gui.InventoryMenu;
import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.players.classes.Class;
import net.valor.rpgproject.potions.ResourceBag;
import net.valor.rpgproject.utils.Permissions;
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
    private final Class playerClass;
    private boolean disabled;
    // TODO IF IT OVERFILLS, SEND A ITEM CREATION PACKET TO PLAYER, AND THEN LISTEN FOR A ITEM PICKUP PACKET
    private ResourceBag resourceBag;

    public RPGPlayer(Player player, Class playerClass, int level, int experience, int health, int coins, ItemStack[] resourceBag) {
        this.player = player;
        this.playerClass = playerClass;
        this.level = level;
        this.experience = experience;
        this.health = health;
        this.coins = coins;
        this.disabled = false;
        this.resourceBag = new ResourceBag(this, resourceBag);

        this.setup();
    }

    private void setup() {
        // look to see if the player has a 14, 21, or 28 size resource bag through permissions, and assign the size to a variable



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
