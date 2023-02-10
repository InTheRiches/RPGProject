package net.valor.rpgproject.players.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import net.valor.rpgproject.RPGProject;

public class ClassLoader {

    private static ClassLoader instance;

    private final List<Class> classes;

    public ClassLoader() {
        this.classes = new ArrayList<>();

        ConfigurationSection ms = RPGProject.getInstance().getConfig().getConfigurationSection("classes");

        for (String str : ms.getKeys(false)) {
            classes.add(new Class(str, ms.getString(str + ".name"), ms.getString(str + ".description")));
        }
    }

    public Optional<Class> getClass(String id) {
        return classes.stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<Class> getClasses() {
        return classes;
    }

    public static ClassLoader getInstance() {
        if (instance == null)
            instance = new ClassLoader();

        return instance;
    }
}