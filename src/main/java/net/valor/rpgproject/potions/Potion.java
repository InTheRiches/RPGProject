package net.valor.rpgproject.potions;

import net.valor.rpgproject.players.RPGPlayer;

public abstract class Potion {
    protected final String type;
    protected final String id;
    protected final String name;
    protected final String description;

    public Potion(String type, String id, String name, String description) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    abstract public void use(RPGPlayer player, int tier);
}