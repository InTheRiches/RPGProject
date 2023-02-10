package net.valor.rpgproject.utils;

import com.thepepeyt.databasehelper.DatabaseHelper;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.thepepeyt.databasehelper.database.AbstractSQLDatabase;
import org.bukkit.entity.Player;

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

        db.createTable().table("PLAYERS").columns("id TEXT", "level INT", "xp INT", "health INT", "coins INT").executeAsync();

        try {
            db.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database successfully created");
    }

    public Object getPlayerValue(Player player, String valueID) {
        AtomicReference<Object> atomicObject = new AtomicReference<>(null);

        try {
            var data = this.db.getData().table("PLAYERS")
                    .columns("id", valueID)
                    .where("id", player.getUniqueId().toString()).completeAsync();
            data.getObservable().forEach(object -> {
                try {
                    String uuid = (String) object;
                } catch (Exception e) {
                    int value = (int) object;
                    atomicObject.set(value);
                }
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

    public int getXP(Player player) {
        return (int) getPlayerValue(player, "xp");
    }

    public int getCoins(Player player) {
        return (int) getPlayerValue(player, "coins");
    }

    public void setCoins(Player player, int coins) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("coins", coins).executeAsync();
    }

    public void setLevel(Player player, int level) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("level", level).executeAsync();
    }

    public void setXP(Player player, int xp) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("xp", xp).executeAsync();
    }

    public void setHealth(Player player, int health) {
        db.updateData().table("PLAYERS").where("id", player.getUniqueId().toString()).column("health", health).executeAsync();
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

    public void setupPlayer(Player p) {
        db.insertData().table("PLAYERS")
                .insert("id", p.getUniqueId().toString())
                .insert("level", 1)
                .insert("xp", 0)
                .insert("health", 20)
                .insert("coins", 350)
                .executeAsync();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}
