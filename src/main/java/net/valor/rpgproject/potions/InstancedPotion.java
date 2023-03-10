package net.valor.rpgproject.potions;

import net.valor.rpgproject.players.RPGPlayer;
import org.bukkit.Material;

/**
 * @author Projekt Valor
 * @since 3/7/2023
 */
public class InstancedPotion {
    private int uses;
    private final int buff;
    private final int tier;
    private final Potion potion;

    public InstancedPotion(int buff, int uses, int tier, Potion potion) {
        this.buff = buff;
        this.uses = uses;
        this.tier = tier; 
        this.potion = potion;
    }

    public int getBuff() {
        return buff;
    }

    public int getUses() {
        return uses;
    }

    public int getTier() {
        return tier;
    }

    public Potion getPotion() {
        return potion;
    }

    public void use(RPGPlayer player) {
        potion.use(player, buff);
        this.uses -= 1;
    }
}
