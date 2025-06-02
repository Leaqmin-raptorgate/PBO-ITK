package Validator;

import Validator.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskInputValidator {

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500; // Example limit

    /**
     * Validates the input data for creating or updating a Task.
     * Throws ValidationException if any validation rule is violated.
     *
     * @param title         The title of the task.
     * @param description   The description of the task.
     * @param deadlineStr   The deadline as a String (expected format "YYYY-MM-DD"). Can be null or empty if optional.
     * @param priority      The priority as an int (expected 0, 1, or 2).
     * @throws ValidationException if validation fails.
     */
    public static void validateTaskData(String title, String description, String deadlineStr, int priority)
            throws ValidationException {

        // 1. Validate Title (Mandatory, specific length)
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Task title cannot be empty.");
        }
        if (title.trim().length() > MAX_TITLE_LENGTH) {
            throw new ValidationException("Task title cannot exceed " + MAX_TITLE_LENGTH + " characters.");
        }

        // 2. Validate Description (Optional, but if provided, check length)
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Task description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters.");
        }

        // 3. Validate Deadline (Format if provided)
        // Note: Your Task model uses LocalDate, so the DB stores deadline as TEXT "YYYY-MM-DD"
        // If deadlineStr is meant to be optional, the UI should pass null or empty.
        // If deadline is mandatory, the first check here should be for non-emptiness.
        // For this example, let's assume deadline string can be empty/null if it's optional.
        if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
            try {
                LocalDate deadline = LocalDate.parse(deadlineStr); // Assumes "YYYY-MM-DD"
                // Optional: Further business rule, e.g., deadline cannot be in the past for *new* tasks.
                // if (isNewTask && deadline.isBefore(LocalDate.now())) {
                //     throw new ValidationException("Deadline for a new task cannot be in the past.");
                // }
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid deadline format. Please use YYYY-MM-DD.");
            }
        }
        // If deadline was mandatory and deadlineStr is empty/null:
        // else if (isDeadlineMandatory) {
        //    throw new ValidationException("Deadline is required.");
        // }


        // 4. Validate Priority (Integer range: 0, 1, 2)
        // Assuming 0: Low, 1: Medium, 2: High
        if (priority < 0 || priority > 2) {
            throw new ValidationException("Invalid priority value. Must be 0 (Low), 1 (Medium), or 2 (High).");
        }

        // Add more validations as needed (e.g., for idUser, idJm if they come from user selection)
    }
}

//     // Example usage from a UI action (conceptual)
//     public static void main(String[] args) {
//         // --- Simulate valid input ---
//         try {
//             System.out.println("Validating valid data...");
//             validateTaskData("Finish Report", "Compile Q3 data", "2025-12-31", 2);
//             System.out.println("Valid data: OK");
//         } catch (ValidationException e) {
//             System.err.println("Validation Error (should not happen here): " + e.getMessage());
//         }

//         // --- Simulate invalid title ---
//         try {
//             System.out.println("\nValidating invalid title (empty)...");
//             validateTaskData("", "Desc", "2025-12-31", 1);
//         } catch (ValidationException e) {
//             System.err.println("Validation Error: " + e.getMessage()); // Expected
//         }

//         // --- Simulate invalid deadline format ---
//         try {
//             System.out.println("\nValidating invalid deadline format...");
//             validateTaskData("Valid Title", "Desc", "31-12-2025", 1);
//         } catch (ValidationException e) {
//             System.err.println("Validation Error: " + e.getMessage()); // Expected
//         }

//         // --- Simulate invalid priority ---
//         try {
//             System.out.println("\nValidating invalid priority...");
//             validateTaskData("Valid Title", "Desc", "2025-12-31", 5);
//         } catch (ValidationException e) {
//             System.err.println("Validation Error: " + e.getMessage()); // Expected
//         }
//     }
// }
