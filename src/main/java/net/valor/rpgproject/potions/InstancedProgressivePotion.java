package net.valor.rpgproject.potions;

/**
 * @author Projekt Valor
 * @since 3/7/2023
 */
public class InstancedProgressivePotion extends InstancedPotion {
    private final int duration;

    public InstancedProgressivePotion(int buff, int uses, int duration, int tier, Potion potion) {
        super(buff, uses, tier, potion);

        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
