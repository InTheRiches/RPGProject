package net.valor.rpgproject.potions;

import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class ProgressivePotionBundleBuilder {
    private int potion1Buff;
    private int potion2Buff;
    private int potion3Buff;

    private int potion1Uses;
    private int potion2Uses;
    private int potion3Uses;

    private int potion1Duration;
    private int potion2Duration;
    private int potion3Duration;

    private final Potion[] potions;

    public ProgressivePotionBundleBuilder(Potion... potions) {
        this.potions = potions;
    }

    public ProgressivePotionBundleBuilder setPotion1Buff(int potion1Buff) {
        this.potion1Buff = potion1Buff;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion2Buff(int potion2Buff) {
        this.potion2Buff = potion2Buff;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion3Buff(int potion3Buff) {
        this.potion3Buff = potion3Buff;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion1Uses(int potion1Uses) {
        this.potion1Uses = potion1Uses;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion2Uses(int potion2Uses) {
        this.potion2Uses = potion2Uses;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion3Uses(int potion3Uses) {
        this.potion3Uses = potion3Uses;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion1Duration(int potion1Duration) {
        this.potion1Duration = potion1Duration;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion2Duration(int potion2Duration) {
        this.potion2Duration = potion2Duration;
        return this;
    }

    public ProgressivePotionBundleBuilder setPotion3Duration(int potion3Duration) {
        this.potion3Duration = potion3Duration;
        return this;
    }

    public ItemStack build() {
        String potion1Name = potions[1].getFormattedString();
        String potion2Name = potions[2].getFormattedString();
        String potion3Name = potions[3].getFormattedString();

        ItemStack bundle = new ItemConstructor(Material.valueOf(RPGProject.getInstance().getConfig().getString("potions." + potions[0].getId() + ".bundle-material")))
            .setCustomModelData(RPGProject.getInstance().getConfig().getInt("potions." + potions[0].getId() + ".bundle-custom-model-data"))
            .setName(ChatColor.translateAlternateColorCodes('&', RPGProject.getInstance().getConfig().getString("potions." + potions[0].getId() + ".bundle-title")))
            .setLore(RPGProject.getInstance().getConfig().getStringList("potions." + potions[0].getId() + ".bundle-lore").stream().map(s -> {
                String colorCoded = ChatColor.translateAlternateColorCodes('&', s);
                if (colorCoded.contains("potion-3") && (potion3Name == null || potion3Name == "")) return "";

                colorCoded = colorCoded.replaceAll("%potion-1%", potion1Name);
                colorCoded = colorCoded.replaceAll("%potion-2%", potion2Name);
                colorCoded = colorCoded.replaceAll("%potion-3%", potion3Name);

                colorCoded = colorCoded.replaceAll("%potion-1-buff%", String.valueOf(potion1Buff));
                colorCoded = colorCoded.replaceAll("%potion-2-buff%", String.valueOf(potion2Buff));
                colorCoded = colorCoded.replaceAll("%potion-3-buff%", String.valueOf(potion3Buff));
            
                colorCoded = colorCoded.replaceAll("%potion-1-uses%", String.valueOf(potion1Uses));
                colorCoded = colorCoded.replaceAll("%potion-2-uses%", String.valueOf(potion2Uses));
                colorCoded = colorCoded.replaceAll("%potion-3-uses%", String.valueOf(potion3Uses));

                colorCoded = colorCoded.replaceAll("%potion-1-duration%", String.valueOf(potion1Duration));
                colorCoded = colorCoded.replaceAll("%potion-2-duration%", String.valueOf(potion2Duration));
                colorCoded = colorCoded.replaceAll("%potion-3-duration%", String.valueOf(potion3Duration));

                return colorCoded;
            }).collect(Collectors.toList()));

        PersistentDataContainer newContainer = bundle.getItemMeta().getPersistentDataContainer();
        // set a amount buffed key for each potion in the bundle
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER, potion1Buff);
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-amount-buffed"), PersistentDataType.INTEGER, potion2Buff);
        if (potion3Buff != 0) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-amount-buffed"), PersistentDataType.INTEGER, potion3Buff);
        // set a uses key for each potion in the bundle
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-uses"), PersistentDataType.INTEGER, potion1Uses);
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-uses"), PersistentDataType.INTEGER, potion2Uses);
        if (potion3Uses != 0) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-uses"), PersistentDataType.INTEGER, potion3Uses);

        // set a duration key for each potion in the bundle
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-1-duration"), PersistentDataType.INTEGER, potion1Duration);
        newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-2-duration"), PersistentDataType.INTEGER, potion2Duration);
        if (potion3Duration != 0) newContainer.set(new NamespacedKey(RPGProject.getInstance(), "potion-3-duration"), PersistentDataType.INTEGER, potion3Duration);

    }

    public static ProgressivePotionBundleBuilder fromItem(ItemStack item) {
        ProgressivePotionBundleBuilder builder = new ProgressivePotionBundleBuilder();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        builder.setPotion1Buff(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-amount-buffed"), PersistentDataType.INTEGER));
        builder.setPotion2Buff(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-amount-buffed"), PersistentDataType.INTEGER));
        builder.setPotion3Buff(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-amount-buffed"), PersistentDataType.INTEGER));

        builder.setPotion1Uses(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-uses"), PersistentDataType.INTEGER));
        builder.setPotion2Uses(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-uses"), PersistentDataType.INTEGER));
        builder.setPotion3Uses(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-uses"), PersistentDataType.INTEGER));

        builder.setPotion1Duration(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-1-duration"), PersistentDataType.INTEGER));
        builder.setPotion2Duration(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-2-duration"), PersistentDataType.INTEGER));
        builder.setPotion3Duration(container.get(new NamespacedKey(RPGProject.getInstance(), "potion-3-duration"), PersistentDataType.INTEGER));

        return builder;
    }
}