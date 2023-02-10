package net.valor.rpgproject;

import net.valor.rpgproject.commands.LevelCommand;
import net.valor.rpgproject.players.PlayerListener;
import net.valor.rpgproject.utils.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGProject extends JavaPlugin {

    private static RPGProject instance;

    @Override
    public void onEnable() {
        instance = this;

        // Plugin startup logic
        initialize();
        registerCommands();
        registerListeners();
    }

    private void initialize() {
        if (!(new File(getDataFolder(), "config.yml").exists()))
            this.saveResource("config.yml", false);
        
        // load basic utilities
        Database.getInstance();
        ClassLoader.getInstance();
    }

    private void registerCommands() {
        // register commands
        new LevelCommand();
    }

    private void registerListeners() {
        // register listeners
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static RPGProject getInstance() {
        return instance;
    }
}
