package donutauction;

/** A finished auction. Fields are public for Gson. */
public final class HistoryEntry {
    public String itemId = "";
    public int quantity = 1;
    public String winner; // null = no valid bids
    public double price;
    public double worthTotal;
    public long endedAt;
    public boolean cancelled;
}
