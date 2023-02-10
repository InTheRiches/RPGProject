package net.valor.rpgproject.players.classes;

public class Class {

    private final String id;
    private final String name;
    private final String description;

    public Class(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}