package donutauction;

/** A saved auction setup. Fields are public for Gson. */
public final class Preset {
    public String name = "";
    public String itemId = "";
    public int quantity = 1;
    public double minBid;
    public int timerSec = 60;
    public double worthEach;

    public Preset() {}

    public Preset(String name, String itemId, int quantity, double minBid, int timerSec, double worthEach) {
        this.name = name;
        this.itemId = itemId;
        this.quantity = quantity;
        this.minBid = minBid;
        this.timerSec = timerSec;
        this.worthEach = worthEach;
    }
}
