package net.valor.rpgproject.potions;

/**
 * @author Projekt Valor
 * @since 3/7/2023
 */
public class InstancedProgressivePotion {
    private int uses;
    private final int buff;
    private final int tier;
    private final int duration;
    private final ProgressivePotion potion;

    public ProgressiveInstancedPotion(int buff, int uses, int duration, int tier, ProgressivePotion potion) {
        this.buff = buff;
        this.uses = uses;
        this.tier = tier; 
        this.duration = duration;
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

    public ProgressivePotion getPotion() {
        return potion;
    }

    public int getDuration() {
        return this.duration;
    }

    public void use(RPGPlayer player) {
        potion.use(player, this.duration, this.buff);
        this.uses -= 1;
    }
}
