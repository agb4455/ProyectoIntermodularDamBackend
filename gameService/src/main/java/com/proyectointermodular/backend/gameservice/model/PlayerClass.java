package com.proyectointermodular.backend.gameservice.model;

import java.util.List;

public class PlayerClass {
   private String name;
   private int baseHealth;
   List<Troop> availableTroops;

   public String getName() {
       return name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public int getBaseHealth() {
       return baseHealth;
   }

   public void setBaseHealth(int baseHealth) {
       this.baseHealth = baseHealth;
   }

   public List<Troop> getAvailableTroops() {
       return availableTroops;
   }

   public void setAvailableTroops(List<Troop> availableTroops) {
       this.availableTroops = availableTroops;
   }
}
