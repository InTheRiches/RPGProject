package net.valor.rpgproject.potions;

import net.valor.rpgproject.players.RPGPlayer;
import org.bukkit.Material;

/**
 * @author Projekt Valor
 * @since 3/7/2023
 */
public class InstancedPotion {
    private final int buff;
    private final int uses;
    private final Potion potion;

    public InstancedPotion(int buff, int uses, Potion potion) {
        this.buff = buff;
        this.uses = uses;
        this.potion = potion;
    }

    public int getBuff() {
        return buff;
    }

    public int getUses() {
        return uses;
    }

    public Potion getPotion() {
        return potion;
    }
}
