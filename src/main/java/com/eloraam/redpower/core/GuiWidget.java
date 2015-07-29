package com.eloraam.redpower.core;

import net.minecraft.client.gui.Gui;

public class GuiWidget extends Gui {

   public int width;
   public int height;
   public int top;
   public int left;


   public GuiWidget(int x, int y, int w, int h) {
      this.left = x;
      this.top = y;
      this.width = w;
      this.height = h;
   }

   protected void drawRelRect(int x, int y, int w, int h, int c) {
      drawRect(x, y, x + w, y + h, c | '\uf000');
   }
}
