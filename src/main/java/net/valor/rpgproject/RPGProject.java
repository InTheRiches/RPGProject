package net.valor.rpgproject;

import net.valor.rpgproject.commands.LevelCommand;
import net.valor.rpgproject.players.PlayerListener;
import net.valor.rpgproject.utils.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGProject extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        initialize();
        registerCommands();
        registerListeners();
    }

    private void initialize() {
        new Database();
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
}
