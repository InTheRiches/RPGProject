package net.valor.rpgproject.potions;

import net.projektcontingency.titanium.items.ItemConstructor;
import net.valor.rpgproject.RPGProject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.stream.Collectors;

public class PotionBundleBuilder {
    private final InstancedPotion[] potions;

    public PotionBundleBuilder() {
        this.potions = new InstancedPotion[2];
    }

    public PotionBundleBuilder addPotion(int buff, int uses, Potion potion) {
        if (potions[0] == null) {
            potions[0] = new InstancedPotion(buff, uses, potion);
        } else if (potions[1] == null) {
            potions[1] = new InstancedPotion(buff, uses, potion);
        } else if (potions[2] == null) {
            potions[2] = new InstancedPotion(buff, uses, potion);
        }
        return this;
    }

    public int getSize() {
        return potions.length;
    }

    public ItemStack build() {
        String potion1Name = potions[0].getPotion().getFormattedString();
        String potion2Name = potions[1].getPotion().getFormattedString();
        String potion3Name = potions[2].getPotion().getFormattedString();

        ItemStack bundle = new ItemConstructor(Material.valueOf(RPGProject.getInstance().getConfig().getString("potions." + potions[0].getPotion().getId() + ".bundle-material")))
            .setCustomModelData(RPGProject.getInstance().getConfig().getInt("potions." + potions[0].getPotion().getId() + ".bundle-custom-model-data"))
            .setName(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("potions." + potions[0].getPotion().getId() + ".bundle-title")))
            .setLore(RPGProject.getInstance().getConfig().getStringList("potions." + potions[0].getPotion().getId() + ".bundle-lore").stream().map(s -> {
                String colorCoded = ChatColor.translateAlternateColorCodes('&', s);
                if (colorCoded.contains("potion-3") && (potion3Name == null || potion3Name == "")) return "";

                colorCoded = colorCoded.replaceAll("%potion-1%", potion1Name);
                colorCoded = colorCoded.replaceAll("%potion-2%", potion2Name);
                colorCoded = colorCoded.replaceAll("%potion-3%", potion3Name);

                colorCoded = colorCoded.replaceAll("%potion-1-buff%", String.valueOf(potions[0].getBuff()));
                colorCoded = colorCoded.replaceAll("%potion-2-buff%", String.valueOf(potions[1].getBuff()));
                colorCoded = colorCoded.replaceAll("%potion-3-buff%", String.valueOf(potions[2].getBuff()));
            
                colorCoded = colorCoded.replaceAll("%potion-1-uses%", String.valueOf(potions[0].getUses()));
                colorCoded = colorCoded.replaceAll("%potion-2-uses%", String.valueOf(potions[1].getUses()));
                colorCoded = colorCoded.replaceAll("%potion-3-uses%", String.valueOf(potions[2].getUses()));

                return colorCoded;
            }).collect(Collectors.toList()));

        PersistentDataContainer newContainer = bundle.getItemMeta().getPersistentDataContainer();
        // set an amount buffed key for each potion in the bundle
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER, potions[0].getBuff());
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-amount-buffed"), PersistentDataType.INTEGER, potions[1].getBuff());
        if (potions[2] != null) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-amount-buffed"), PersistentDataType.INTEGER, potions[2].getBuff());
        // set an uses key for each potion in the bundle
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-uses"), PersistentDataType.INTEGER, potions[0].getUses());
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-uses"), PersistentDataType.INTEGER, potions[1].getUses());
        if (potions[2] != null) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-uses"), PersistentDataType.INTEGER, potions[2].getUses());

        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-id"), PersistentDataType.STRING, potions[0].getPotion().getId());
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-id"), PersistentDataType.STRING, potions[1].getPotion().getId());
        if (potions[2] != null) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-id"), PersistentDataType.STRING, potions[2].getPotion().getId());

        return bundle;
    }

    public static PotionBundleBuilder fromItem(ItemStack item) {
        PotionBundleBuilder builder = new PotionBundleBuilder();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        int buff = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER);
        int uses = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-uses"), PersistentDataType.INTEGER);
        String id = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-id"), PersistentDataType.STRING);
        Optional<Potion> potion = PotionHandler.getInstance().getPotion(id);

        if (potion.isEmpty()) {
            throw new IllegalArgumentException("Potion with id " + id + " does not exist! PotionBundleBuilder.fromItem()");
        }

        builder.addPotion(buff, uses, potion.get());

        int buff2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-amount-buffed"), PersistentDataType.INTEGER);
        int uses2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-uses"), PersistentDataType.INTEGER);
        String id2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-id"), PersistentDataType.STRING);
        Optional<Potion> potion2 = PotionHandler.getInstance().getPotion(id2);

        if (potion2.isEmpty()) {
            throw new IllegalArgumentException("Potion with id " + id + " does not exist! PotionBundleBuilder.fromItem()");
        }

        builder.addPotion(buff2, uses2, potion2.get());

        if (!container.has(new NamespacedKey(RPGProject.getInstance(), "potion-3-id"), PersistentDataType.STRING)) return builder;

        int buff3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-amount-buffed"), PersistentDataType.INTEGER);
        int uses3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-uses"), PersistentDataType.INTEGER);
        String id3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-id"), PersistentDataType.STRING);
        Optional<Potion> potion3 = PotionHandler.getInstance().getPotion(id3);

        if (potion3.isEmpty()) {
            throw new IllegalArgumentException("Potion with id " + id + " does not exist! PotionBundleBuilder.fromItem()");
        }

        builder.addPotion(buff3, uses3, potion3.get());

        return builder;
    }
}