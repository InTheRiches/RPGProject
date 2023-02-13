package net.valor.rpgproject.armor;

import net.valor.rpgproject.RPGProject;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Projekt Valor
 * @since 2/12/2023
 */
public class ArmorLoader {

    private static ArmorLoader instance;
    private List<Armor> armor;

    public ArmorLoader() {
        this.armor = new ArrayList<>();

        ConfigurationSection ms = RPGProject.getInstance().getConfig().getConfigurationSection("armor");

        for (String str : ms.getKeys(false)) {
            armor.add(new Armor(str, Material.valueOf(ms.getString(str + ".material")), ms.getInt(str + ".custom-model-data"), ms.getInt(str + ".health-buff"), (float) ms.getDouble(str + ".regeneration-buff")));
        }
    }

    public List<Armor> getArmor() {
        return armor;
    }

    public Optional<Armor> getArmor(String id) {
        for (Armor armor : this.armor) {
            if (armor.getId().equalsIgnoreCase(id))
                return Optional.of(armor);
        }

        return Optional.empty();
    }

    public Optional<Armor> getArmor(Material material, int customModelData) {
        for (Armor armor : this.armor) {
            if (armor.getMaterial().equals(material) && armor.getCustomModelData() == customModelData)
                return Optional.of(armor);
        }

        return Optional.empty();
    }

    public static ArmorLoader getInstance() {
        if (instance == null)
            instance = new ArmorLoader();

        return instance;
    }
}
