package com.eloraam.redpower.core;


public class OreStack {

   public String material;
   public int quantity;


   public OreStack(String mat, int qty) {
      this.material = mat;
      this.quantity = qty;
   }

   public OreStack(String mat) {
      this.material = mat;
      this.quantity = 1;
   }
}
