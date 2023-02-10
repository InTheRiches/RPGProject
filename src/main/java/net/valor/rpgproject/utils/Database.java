package net.valor.rpgproject.utils;

import com.thepepeyt.databasehelper.DatabaseHelper;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

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

        db.createTable().table("PLAYERS").columns("id TEXT", "level INT", "xp INT", "health INT").executeAsync();

        try {
            db.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database successfully created");
    }

    public int getLevel(Player player) {
        AtomicInteger blocks = new AtomicInteger(0);
        try {
            var data = this.db.getData().table("PLAYERS")
                    .columns("id", "level")
                    .where("id", player.getUniqueId().toString()).completeAsync();
            data.getObservable().forEach(object -> {
                try {
                    String uuid = (String) object;
                } catch (Exception e) {
                    int value = (int) object;
                    blocks.set(value);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blocks.get();
    }

    public int getHealth(Player player) {
        AtomicInteger health = new AtomicInteger(0);
        try {
            var data = this.db.getData().table("PLAYERS")
                    .columns("id", "health")
                    .where("id", player.getUniqueId().toString()).completeAsync();
            data.getObservable().forEach(object -> {
                try {
                    String uuid = (String) object;
                } catch (Exception e) {
                    int value = (int) object;
                    health.set(value);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return health.get();
    }

    public int getXP(Player player) {
        AtomicInteger xp = new AtomicInteger(0);
        try {
            var data = this.db.getData().table("PLAYERS")
                    .columns("id", "xp")
                    .where("id", player.getUniqueId().toString()).completeAsync();
            data.getObservable().forEach(object -> {
                try {
                    String uuid = (String) object;
                } catch (Exception e) {
                    int value = (int) object;
                    xp.set(value);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xp.get();
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
                .executeAsync();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}
