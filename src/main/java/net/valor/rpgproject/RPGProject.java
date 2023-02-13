package net.valor.rpgproject;

import com.codingforcookies.armorequip.ArmorListener;
import dev.jorel.commandapi.CommandAPICommand;
import net.valor.rpgproject.armor.ArmorLoader;
import net.valor.rpgproject.commands.EXPCommand;
import net.valor.rpgproject.commands.LevelCommand;
import net.valor.rpgproject.players.PlayerHandler;
import net.valor.rpgproject.players.PlayerListener;
import net.valor.rpgproject.players.PlayerPAPIExpansion;
import net.valor.rpgproject.players.classes.ClassHandler;
import net.valor.rpgproject.potions.PotionHandler;
import net.valor.rpgproject.utils.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
        ClassHandler.getInstance();
        PotionHandler.getInstance();
        ArmorLoader.getInstance();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayerPAPIExpansion().register();
        }
    }

    private void registerCommands() {
        // register commands
        new LevelCommand();
        new EXPCommand();
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
