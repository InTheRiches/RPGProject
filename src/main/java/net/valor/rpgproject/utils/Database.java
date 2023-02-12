package net.valor.rpgproject.utils;

import com.thepepeyt.databasehelper.DatabaseHelper;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.thepepeyt.databasehelper.database.AbstractSQLDatabase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Projekt Valor
 * @since 2/9/2023
 */
public class Database {
    private static Database instance;

    private final AbstractSQLDatabase db;

    public Database() {
        db = DatabaseHelper.mySQLBuilder()
                .user("root")
                .password("root")
                .host("localhost")
                .port(3306)
                .database("rpgproject")
                .build();

        try {
            db.connect();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        db.createTable().table("PLAYERS").columns("id TEXT", "class TEXT", "level INT", "exp INT", "health INT", "coins INT", "abilityPoints INT", "resourceBag TEXT").executeAsync();

        try {
            db.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getPlayerValue(Player player, String valueID) {
        AtomicReference<Object> atomicObject = new AtomicReference<>(null);

        try {
            var data = this.db.getData().table("PLAYERS")
                    .columns("id", valueID)
                    .where("id", player.getUniqueId().toString()).completeAsync();

            AtomicBoolean isFirst = new AtomicBoolean(true);

            data.getObservable().forEach(object -> {
                if (isFirst.getAndSet(false)) return;

                atomicObject.set(object);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return atomicObject.get();
    }

    public int getLevel(Player player) {
        return (int) getPlayerValue(player, "level");
    }

    public int getHealth(Player player) {
        return (int) getPlayerValue(player, "health");
    }

    public int getEXP(Player player) {
        return (int) getPlayerValue(player, "exp");
    }

    public int getCoins(Player player) {
        return (int) getPlayerValue(player, "coins");
    }

    public String getClassID(Player player) {
        return (String) getPlayerValue(player, "class");
    }

    public int getAbilityPoints(Player player) {
        return (int) getPlayerValue(player, "abilityPoints");
    }

    public ItemStack[] getResourceBag(Player player) {
        String base64 = (String) getPlayerValue(player, "resourceBag");
        try {
            return BukkitSerialization.fromBase64(base64);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClassID(Player player, String classID) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("class", classID).executeAsync();
    }

    public void setCoins(Player player, int coins) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("coins", coins).executeAsync();
    }

    public void setLevel(Player player, int level) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("level", level).executeAsync();
    }

    public void setEXP(Player player, int exp) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("exp", exp).executeAsync();
    }

    public void setHealth(Player player, int health) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("health", health).executeAsync();
    }

    public void setResourceBag(Player player, ItemStack[] resourceBag) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("resourceBag", BukkitSerialization.toBase64(resourceBag)).executeAsync();
    }

    public boolean doesPlayerExist(Player p) {
        try {
            var data = this.db.getData().table("PLAYERS")
                    .columns("id")
                    .where("id", p.getUniqueId().toString()).completeAsync();
            return data.getObservable().count().blockingGet() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setupPlayer(Player p, String classID) {
        db.insertData().table("PLAYERS")
                .insert("id", p.getUniqueId().toString())
                .insert("level", 1)
                .insert("exp", 0)
                .insert("health", 20)
                .insert("coins", 350)
                .insert("class", classID)
                .insert("abilityPoints", 0)
                .insert("resourceBag", BukkitSerialization.toBase64(new ItemStack[28])) // TODO REMEMBER TO CHANGE THIS WHEN THE RESOURCE BAG SIZE CHANGES
                .executeAsync();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}
