package net.valor.rpgproject.potions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.projektcontingency.titanium.gui.InventoryMenu;
import net.projektcontingency.titanium.items.ItemUtilities;
import net.valor.rpgproject.RPGProject;
import net.valor.rpgproject.players.RPGPlayer;
import net.valor.rpgproject.utils.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;
import redempt.redlib.misc.EventListener;
import redempt.redlib.misc.Task;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Projekt Valor
 * @since 2/12/2023
 */
public class ResourceBag {
    private RPGPlayer player;
    private final ItemStack[] resourceBag;
    private ItemStack resourceBagItem;

    public ResourceBag(RPGPlayer player, ItemStack[] resourceBag) {
        this.resourceBag = resourceBag;
        this.player = player;

        int size = getResourceBagSize();

        this.resourceBagItem = new ItemBuilder(Material.valueOf(RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".material")))
                .setName(RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".name"))
                .setLore(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".lore")).split("\n"))
                .setCustomModelData(RPGProject.getInstance().getConfig().getInt("static-items.resource-bag-" + size + ".custom-model-data"));

        EventBus eventBus = LuckPermsProvider.get().getEventBus();

        // Open resource bag event.
        new EventListener<>(RPGProject.getInstance(), InventoryClickEvent.class, (l, e) -> {
            if (this.player.isDisabled()) {
                l.unregister();
                return;
            }

            if (!player.getPlayer().getUniqueId().equals(this.player.getPlayer().getUniqueId())) return;

            if (e.getView().getTitle().equals("Resource Bag") && (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.PLACE_SOME))) {
                e.setCancelled(true);
                return;
            }
            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
            if (e.getView().getTitle().equals("Resource Bag") && (e.getAction().equals(InventoryAction.PLACE_ALL) || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(InventoryAction.PLACE_SOME))) {
                e.setCancelled(false);
                return;
            }

            if (!e.getCurrentItem().getItemMeta().getDisplayName().equals(resourceBagItem.getItemMeta().getDisplayName()) || !e.getCurrentItem().getItemMeta().getLore().equals(resourceBagItem.getItemMeta().getLore()) || e.getCurrentItem().getItemMeta().getCustomModelData() != resourceBagItem.getItemMeta().getCustomModelData()) return;
            e.setCancelled(true);

            Task.syncDelayed(this::openResourceBag);
        });
        // Close resource bag event
        new EventListener<>(RPGProject.getInstance(), InventoryCloseEvent.class, (l, e) -> {
            if (this.player.isDisabled()) {
                l.unregister();
                return;
            }

            if (!player.getPlayer().getUniqueId().equals(this.player.getPlayer().getUniqueId())) return;
            if (!e.getView().getTitle().equals("Resource Bag")) return;
            if (e.getView().getCursor() == null) return;

            // stop the cursor item from being added to the player's inventory.
            e.getView().setCursor(null);
        });

        new EventListener<>(RPGProject.getInstance(), PlayerDropItemEvent.class, (l, e) -> {
            if (this.player.isDisabled()) {
                l.unregister();
                return;
            }

            if (!player.getPlayer().getUniqueId().equals(this.player.getPlayer().getUniqueId())) return;

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
            if (this.player.isDisabled()) {
                l.unregister();
                return;
            }

            if (!(e.getEntity() instanceof Player eventPlayer)) return;
            if (!eventPlayer.getUniqueId().equals(this.player.getPlayer().getUniqueId())) return;
            if (e.getItem().getItemStack().getItemMeta() == null) return;

            int newsize = getResourceBagSize();

            boolean itemCanBePickedUp = false;

            for (String str : RPGProject.getInstance().getConfig().getStringList("resource-bag-pickup-items")) {
                Material material = Material.valueOf(str.split(":")[0]);
                int data = Integer.parseInt(str.split(":")[1]);

                if (!e.getItem().getItemStack().getType().equals(material) || e.getItem().getItemStack().getItemMeta().getCustomModelData() != data) return;

                itemCanBePickedUp = true;

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

                        if (i >= newsize) break;

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

            if (!itemCanBePickedUp) return;

            e.setCancelled(true);

            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.COLLECT);
            packet.getIntegers().write(0, e.getItem().getEntityId());
            packet.getIntegers().write(1, this.player.getPlayer().getEntityId());
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player.getPlayer(), packet);
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }

            e.getItem().remove();
        });

        eventBus.subscribe(RPGProject.getInstance(), NodeAddEvent.class, (e) -> {
            if (!this.player.getPlayer().getDisplayName().equals(e.getTarget().getFriendlyName())) return;
            if (!e.getNode().getKey().contains("rpgproject.resourcebag.size.")) return;

            verifyResourceBagItem();
        });

        this.player.getPlayer().getInventory().setItem(13, resourceBagItem);
    }

    public void openResourceBag() {
        // make getResourceBagSize() a multiple of 9
        int size = getResourceBagSize();
        if (size % 9 != 0) {
            size = (size / 9) * 9 + 9;
        }

        InventoryMenu menu = new InventoryMenu(size, "Resource Bag");

        ItemStack[] bag = this.resourceBag;
        for (int i = 0, bagLength = bag.length; i < bagLength; i++) {
            ItemStack item = bag[i];

            ItemButton button = ItemButton.create(item, (e) -> {
                e.setCancelled(false); // TODO REMEMBER THAT BY STOPPING THE EVENT FROM BEING CANCELLED, THE ITEM WILL BE ADDED TO THE PLAYER'S INVENTORY, AND THE SLOTS WITHOUT THIS NULL ITEM WILL NOT ALLOW PLAYERS TO PUT ITEMS INTO THE SLOT
            });

            menu.addButton(i, button);
        }

        menu.open(this.player.getPlayer());
    }

    public ItemStack[] getContents() {
        return this.resourceBag;
    }

    public int getResourceBagSize() {
        int size = 14;
        if (player.getPlayer().hasPermission(Permissions.RESOURCE_BAG_SIZE_21.getPermission())) {
            System.out.println("has " + Permissions.RESOURCE_BAG_SIZE_21.getPermission());
            size = 21;
        }
        if (player.getPlayer().hasPermission(Permissions.RESOURCE_BAG_SIZE_28.getPermission())) {
            System.out.println("has " + Permissions.RESOURCE_BAG_SIZE_28.getPermission());
            size = 28;
        }

        return size;
    }

    public void verifyResourceBagItem() {
        int size = this.getResourceBagSize();

        if (resourceBagItem == null || resourceBagItem.getItemMeta() == null || resourceBagItem.getItemMeta().getLore() == null || resourceBagItem.getItemMeta().getDisplayName().equals(RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".name")) && resourceBagItem.getItemMeta().getLore().equals(List.of(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".lore")).split("\n")))) {
            // print each of the conditions above so I can debug which ones are failing
            return;
        }

        // update the resource bag item
        this.resourceBagItem = new ItemBuilder(Material.valueOf(RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".material")))
                .setName(RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".name"))
                .setLore(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("static-items.resource-bag-" + size + ".lore")).split("\n"))
                .setCustomModelData(RPGProject.getInstance().getConfig().getInt("static-items.resource-bag-" + size + ".custom-model-data"));

        player.getPlayer().getInventory().setItem(13, resourceBagItem);
    }
}
