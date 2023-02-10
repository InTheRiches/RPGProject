package net.valor.rpgproject.potions;

import net.valor.rpgproject.players.RPGPlayer;

public class HealthPotion extends Potion {

    public HealthPotion(String id, String name, String description) {
        super(id, name, description);
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