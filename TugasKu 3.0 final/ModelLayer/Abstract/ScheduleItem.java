package ModelLayer.Abstract;

// ===== Abstract Class Declaration =====
public abstract class ScheduleItem {
    
    // ===== Attributes =====
    protected int itemId;    // Typically matched to an auto-increment PK
    protected String title;

    // ===== Constructors =====
    public ScheduleItem(int itemId, String title) {
        this.itemId = itemId;
        this.title = title;
    }

    // No-argument constructor
    public ScheduleItem() {
    }

    // ===== Getters =====
    public int getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    // ===== Setters =====
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // ===== Abstract Methods =====
    public abstract String getItemType();
    public abstract String getDisplaySummary();

    // ===== Utility Methods =====
    @Override
    public String toString() {
        return "ScheduleItem{" +
               "itemId=" + itemId +
               ", title='" + title + '\'' +
               '}';
    }
}