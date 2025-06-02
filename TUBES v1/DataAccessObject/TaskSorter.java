package DataAccessObject;

import ModelLayer.Class.Task;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskSorter {

    /**
     * Sorts a list of tasks primarily by priority (higher integer value first)
     * and secondarily by deadline (earlier deadline first).
     *
     * @param tasks The list of Task objects to be sorted.
     */
    public static void sortTasks(List<Task> tasks) {
        if (tasks == null) {
            return;
        }
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                // 1. Compare by priority (descending: higher number means higher priority)
                // Integer.compare(y, x) gives descending order for x vs y
                int priorityCompare = Integer.compare(t2.getPriority(), t1.getPriority());
                if (priorityCompare != 0) {
                    return priorityCompare;
                }

                // 2. Priorities are the same, compare by deadline (ascending: earlier date first)
                LocalDate deadline1 = t1.getDeadline();
                LocalDate deadline2 = t2.getDeadline();

                // Handle null deadlines: tasks with no deadline might go last or first based on preference.
                // Here, we'll sort tasks with null deadlines after those with deadlines.
                if (deadline1 == null && deadline2 == null) {
                    return 0; // Both null, consider equal in terms of deadline
                }
                if (deadline1 == null) {
                    return 1; // t1's null deadline comes after t2's non-null deadline
                }
                if (deadline2 == null) {
                    return -1; // t1's non-null deadline comes before t2's null deadline
                }
                return deadline1.compareTo(deadline2); // Natural ascending order for LocalDate
            }
        });
    }
}