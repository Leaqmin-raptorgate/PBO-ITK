package ModelLayer.Class;

public class CourseMaterial extends ModelLayer.Abstract.ScheduleItem {

    private String materialDescription;
    // This class represents a "view" of a Matkul for a given context (e.g., "today's material")
    // It might be constructed using data primarily from a Matkul object.

    public CourseMaterial(String id_matkul, String title, String materialDescription) {
        super(Integer.parseInt(id_matkul), title);
        this.materialDescription = materialDescription;
    }

    public CourseMaterial() {
        super(0, null);
    }

    // Getter
    public String getMaterialDescription() {
        return materialDescription;
    }

    // Setter
    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    // Implementation for abstract methods from ScheduleItem
    @Override
    public String getItemType() {
        return "Course Material";
    }

    @Override
    public String getDisplaySummary() {
        return getTitle() + (materialDescription != null && !materialDescription.isEmpty() ? ": " + materialDescription : "");
    }

    @Override
    public String toString() {
        return "CourseMaterial{" +
               "materialId='" + getItemId() + '\'' +
               ", courseTitle='" + getTitle() + '\'' +
               ", description='" + materialDescription + '\'' +
               '}';
    }
}