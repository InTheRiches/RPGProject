package net.valor.rpgproject.potions;

import java.util.ArrayList;
import java.util.List;

public class PotionHandler {
    private static PotionHandler instance;
    
    private final List<Potion> potions;
    
    public PotionHandler() {
        this.potions = new ArrayList<>();
        
    }
}