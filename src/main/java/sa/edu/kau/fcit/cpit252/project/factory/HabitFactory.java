package sa.edu.kau.fcit.cpit252.project.factory;

import sa.edu.kau.fcit.cpit252.project.model.Habit;
import sa.edu.kau.fcit.cpit252.project.model.HealthHabit;
import sa.edu.kau.fcit.cpit252.project.model.LifestyleHabit;
import sa.edu.kau.fcit.cpit252.project.model.StudyHabit;


public class HabitFactory {
    private static int idCounter = 0;

    public static Habit createHabit(String category, String name, String description) {
        String id = generateId();

        switch (category.toLowerCase()) {
            case "health":
                return new HealthHabit(id, name, description);
            case "study":
                return new StudyHabit(id, name, description);
            case "lifestyle":
                return new LifestyleHabit(id, name, description);
            default:
                throw new IllegalArgumentException(
                        "Unknown habit category: " + category +
                                ". Valid categories: health, study, lifestyle"
                );
        }
    }

    public static Habit createHabit(String category, String name, String description, String extraParam, int extraValue) {
        String id = generateId();

        switch (category.toLowerCase()) {
            case "health":
                return new HealthHabit(id, name, description, extraValue);
            case "study":
                return new StudyHabit(id, name, description, extraParam, extraValue);
            case "lifestyle":
                return new LifestyleHabit(id, name, description, extraParam);
            default:
                throw new IllegalArgumentException(
                        "Unknown habit category: " + category
                );
        }
    }

    private static String generateId() {
        idCounter++;
        return "HABIT-" + String.format("%04d", idCounter);
    }

    public static void resetIdCounter() {
        idCounter = 0;
    }
}
