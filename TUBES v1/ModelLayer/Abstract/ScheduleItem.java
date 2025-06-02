package ModelLayer.Abstract;

public abstract class ScheduleItem {

    protected int itemId;    // Typically matched to an auto-increment PK
    protected String title;

    // Constructor for subclasses
    public ScheduleItem(int itemId, String title) {
        this.itemId = itemId;
        this.title = title;
    }

    // No-argument constructor
    public ScheduleItem() {
    }

    public int getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Abstract methods to be implemented by child classes
    public abstract String getItemType();
    public abstract String getDisplaySummary();

    @Override
    public String toString() {
        return "ScheduleItem{" +
               "itemId=" + itemId +
               ", title='" + title + '\'' +
               '}';
    }
}