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

public class ProgressivePotionBundleBuilder {
    private final InstancedProgressivePotion[] potions;

    public ProgressivePotionBundleBuilder() {
        this.potions = new InstancedProgressivePotion[2];
    }

    public ProgressivePotionBundleBuilder addPotion(int buff, int uses, int duration, int tier, Potion potion) {
        if (potions[0] == null) {
            potions[0] = new InstancedProgressivePotion(buff, uses, duration, tier, potion);
        } else if (potions[1] == null) {
            potions[1] = new InstancedProgressivePotion(buff, uses, duration, tier, potion);
        } else if (potions[2] == null) {
            potions[2] = new InstancedProgressivePotion(buff, uses, duration, tier, potion);
        }
        return this;
    }

    public int getSize() {
        return potions.length;
    }

    public ProgressivePotionBundleBuilder usePotion(RPGPlayer player) {
        for (int i = 0; i < potions.length; i++) {
            if (potions[i] == null)
                continue;
            
            potions[i].use(player);
            if (potions[i].getUses() <= 0) {
                // if the first potion is null, then make the second the first, and third the second
                if (i == 0) {
                    potions[0] = potions[1];
                    potions[1] = potions[2];
                    potions[2] = null;
                }
                RPGProject.getInstance().getLogger().severe("ProgressivePotionBundleBuider.java#usePotion(RPGPRoject player): i is not 0. Report this immediately to the developer.");
            }
            return this;
        }

        RPGProject.getInstance().getLogger().severe("ProgressivePotionBundleBuider.java#usePotion(RPGPRoject player): No potions were found. Report this immediately to the developer.");
        return this;
    }

    public ItemStack build() {
        if (potions[0] != null && potions[1] == null && potions[2] == null) {
            ItemStack item = new ItemStack(potions[0].getPotion().getMaterialType());
            ItemMeta itemMeta = item.getItemMeta();

            NamespacedKey usesKey = new NamespacedKey(RPGProject.getInstance(), "uses");
            NamespacedKey amountBuffedKey = new NamespacedKey(RPGProject.getInstance(), "amount-buffed");
            NamespacedKey tierKey = new NamespacedKey(RPGProject.getInstance(), "tier");
            NamespacedKey durationKey = new NamespacedKey(RPGProject.getInstance(), "duration");

            itemMeta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, potions[0].getUses());
            itemMeta.getPersistentDataContainer().set(amountBuffedKey, PersistentDataType.INTEGER, potions[0].getBuff());
            itemMeta.getPersistentDataContainer().set(tierKey, PersistentDataType.INTEGER, potions[0].getTier());
            itemMeta.getPersistentDataContainer().set(durationKey, PersistentDataType.INTEGER, potions[0].getDuration());

            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("potions." + potions[0].getPotion().getId() + ".title").replaceAll("%buff%", String.valueOf(potions[0].getBuff())).replaceAll("%uses%", String.valueOf(potions[0].getUses())).replaceAll("%tier%", String.valueOf(potions[0].getTier())).replaceAll("%duration%", potions[0].getDuration())));
            int finalTier = potions[0].getTier();
            itemMeta.setLore(RPGProject.getInstance().getConfig().getStringList("potions." + potions[0].getPotion().getId() + ".lore").stream().map(s -> ChatColor.translateAlternateColorCodes('&', s.replaceAll("%buff%", String.valueOf(potions[0].getBuff())).replaceAll("%uses%", String.valueOf(potions[0].getUses())).replaceAll("%tier%", String.valueOf(finalTier)).replaceAll("%duration%", potions[0].getDuration()))).collect(Collectors.toList()));

            item.setItemMeta(itemMeta);
            return item;
        }

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

                    colorCoded = colorCoded.replaceAll("%potion-1-duration%", String.valueOf(potions[0].getDuration()));
                    colorCoded = colorCoded.replaceAll("%potion-2-duration%", String.valueOf(potions[1].getDuration()));
                    colorCoded = colorCoded.replaceAll("%potion-3-duration%", String.valueOf(potions[2].getDuration()));

                    colorCoded = colorCoded.replaceAll("%potion-1-tier%", String.valueOf(potions[0].getTier()));
                    colorCoded = colorCoded.replaceAll("%potion-2-tier%", String.valueOf(potions[1].getTier()));
                    colorCoded = colorCoded.replaceAll("%potion-3-tier%", String.valueOf(potions[2].getTier()));

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

        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-duration"), PersistentDataType.INTEGER, potions[0].getDuration());
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-duration"), PersistentDataType.INTEGER, potions[1].getDuration());
        if (potions[2] != null) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-duration"), PersistentDataType.INTEGER, potions[2].getDuration());

        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-tier"), PersistentDataType.INTEGER, potions[0].getTier());
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-tier"), PersistentDataType.INTEGER, potions[1].getTier());
        if (potions[2] != null) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-tier"), PersistentDataType.INTEGER, potions[2].getTier());

        return bundle;
    }

    public static ProgressivePotionBundleBuilder fromItem(ItemStack item) {
        ProgressivePotionBundleBuilder builder = new ProgressivePotionBundleBuilder();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        int buff = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER);
        int uses = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-uses"), PersistentDataType.INTEGER);
        int duration = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-duration"), PersistentDataType.INTEGER);
        int tier = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-tier"), PersistentDataType.INTEGER);
        String id = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-id"), PersistentDataType.STRING);
        Optional<Potion> potion = PotionHandler.getInstance().getPotion(id);

        if (potion.isEmpty()) {
            throw new IllegalArgumentException("Potion with id " + id + " does not exist! PotionBundleBuilder.fromItem()");
        }

        builder.addPotion(buff, uses, duration, tier, potion.get());

        int buff2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-amount-buffed"), PersistentDataType.INTEGER);
        int uses2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-uses"), PersistentDataType.INTEGER);
        int duration2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-duration"), PersistentDataType.INTEGER);
        int tier2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-tier"), PersistentDataType.INTEGER);
        String id2 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-id"), PersistentDataType.STRING);
        Optional<Potion> potion2 = PotionHandler.getInstance().getPotion(id2);

        if (potion2.isEmpty()) {
            throw new IllegalArgumentException("Potion with id " + id + " does not exist! PotionBundleBuilder.fromItem()");
        }

        builder.addPotion(buff2, uses2, duration2, tier2, potion2.get());

        if (!container.has(new NamespacedKey(RPGProject.getInstance(), "potion-3-id"), PersistentDataType.STRING)) return builder;

        int buff3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-amount-buffed"), PersistentDataType.INTEGER);
        int uses3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-uses"), PersistentDataType.INTEGER);
        int duration3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-duration"), PersistentDataType.INTEGER);
        int tier3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-tier"), PersistentDataType.INTEGER);
        String id3 = container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-id"), PersistentDataType.STRING);
        Optional<Potion> potion3 = PotionHandler.getInstance().getPotion(id3);

        if (potion3.isEmpty()) {
            throw new IllegalArgumentException("Potion with id " + id + " does not exist! PotionBundleBuilder.fromItem()");
        }

        builder.addPotion(buff3, uses3, duration3, tier3, potion3.get());

        return builder;
    }
}