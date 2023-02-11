package net.valor.rpgproject.potions.types;

import net.valor.rpgproject.players.RPGPlayer;
import net.valor.rpgproject.potions.Potion;

public class HealthPotion extends Potion {

    public HealthPotion(String id, String name, String description) {
        super("health", id, name, description);
    }

    public HealthPotion(String type, String id, String name, String description) {
        super(type, id, name, description);
    }

    public int getHealth(int tier) {
        switch (tier) {
            case 1:
                return 24;
            case 2:
                return 10;
            case 3:
                return 5;
            default:
                return 0;
        }
    }

    public void use(RPGPlayer player, int tier) {
        player.setHealth(player.getHealth() + getHealth(tier));
    }
}