package net.valor.rpgproject.players;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import net.projektcontingency.titanium.gui.InventoryMenu;
import net.valor.rpgproject.players.classes.Class;
import net.valor.rpgproject.players.classes.ClassHandler;
import net.valor.rpgproject.utils.Database;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Projekt Valor
 * @since 2/9/2023
 */
public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Database.getInstance().doesPlayerExist(e.getPlayer())) {
            PlayerHandler.getInstance().addPlayer(e.getPlayer());
            return;
        }

        InventoryMenu menu = new InventoryMenu(18, "Choose a class");
        menu.setPreventClose(true);
        menu.setPreventClosePredicate(player -> !Database.getInstance().doesPlayerExist(player));

        List<Class> classes = ClassHandler.getInstance().getClasses();
        for (int i = 0, classesSize = classes.size(); i < classesSize; i++) {
            Class clazz = classes.get(i);

            List<String> lore = new ArrayList<>(List.of(ChatColor.translateAlternateColorCodes('&', clazz.getDescription()).split("\n")));
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click to select.");

            menu.addButton(i, ItemButton.create(new ItemBuilder(Material.GLASS_BOTTLE)
                    .setName(ChatColor.GREEN + clazz.getName())
                    .setLore(lore.toArray(new String[0])), (event) -> {
                System.out.println("Player selected class with id " + clazz.getId());
                PlayerHandler.getInstance().setupPlayer(e.getPlayer(), clazz.getId());
                e.getPlayer().closeInventory();
                e.getPlayer().sendMessage(ChatColor.GREEN + "You have selected the " + clazz.getName() + " class!");
            }));
        }

        menu.open(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerHandler.getInstance().removePlayer(e.getPlayer());
    }
}
