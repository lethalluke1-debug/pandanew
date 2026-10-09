package donutauction;

public final class Preset {
   public String name = "";
   public String itemId = "";
   public int quantity = 1;
   public double minBid;
   public int timerSec = 60;
   public double worthEach;

   public Preset() {
   }

   public Preset(String var1, String var2, int var3, double var4, int var6, double var7) {
      this.name = var1;
      this.itemId = var2;
      this.quantity = var3;
      this.minBid = var4;
      this.timerSec = var6;
      this.worthEach = var7;
   }
}
