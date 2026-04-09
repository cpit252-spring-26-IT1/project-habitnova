package com.habitnova;

import com.habitnova.controller.HabitController;
import com.habitnova.factory.HabitFactory;
import com.habitnova.model.*;

public class HabitNovaTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("═══ HabitNova Tests ═══\n");

        testFactoryCreatesCorrectTypes();
        testFactoryRejectsUnknownCategory();
        testHabitCompletion();
        testStreakTracking();
        testControllerOperations();
        testEditAndDelete();

        System.out.printf("%n═══ Results: %d passed, %d failed ═══%n", passed, failed);
        if (failed > 0) System.exit(1);
    }

    static void testFactoryCreatesCorrectTypes() {
        HabitFactory.resetIdCounter();

        Habit health = HabitFactory.createHabit("health", "Exercise", "30 min workout");
        Habit study = HabitFactory.createHabit("study", "Read", "Read 10 pages");
        Habit lifestyle = HabitFactory.createHabit("lifestyle", "Meditate", "10 min meditation");

        check("Factory creates HealthHabit", health instanceof HealthHabit);
        check("Factory creates StudyHabit", study instanceof StudyHabit);
        check("Factory creates LifestyleHabit", lifestyle instanceof LifestyleHabit);
        check("Health category is correct", health.getCategory().equals("Health"));
        check("Study category is correct", study.getCategory().equals("Study"));
        check("Lifestyle category is correct", lifestyle.getCategory().equals("Lifestyle"));
        check("IDs are unique", !health.getId().equals(study.getId()));
    }

    static void testFactoryRejectsUnknownCategory() {
        boolean threw = false;
        try {
            HabitFactory.createHabit("gaming", "Play", "Play games");
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("Factory rejects unknown category", threw);
    }

    static void testHabitCompletion() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Morning run");
        check("Not completed initially", !habit.isCompletedForToday());

        habit.markCompleted();
        check("Completed after marking", habit.isCompletedForToday());

        habit.unmarkCompleted();
        check("Not completed after unmarking", !habit.isCompletedForToday());
    }

    static void testStreakTracking() {
        Habit habit = HabitFactory.createHabit("study", "Read", "Read daily");
        check("Streak starts at 0", habit.getCurrentStreak() == 0);

        habit.markCompleted();
        check("Streak is 1 after first completion", habit.getCurrentStreak() == 1);
        check("Best streak is 1", habit.getBestStreak() == 1);
    }

    static void testControllerOperations() {
        HabitController ctrl = new HabitController();

        ctrl.addHabit("health", "Stretch", "Morning stretches");
        ctrl.addHabit("study", "Algo", "Study algorithms");
        ctrl.addHabit("lifestyle", "Journal", "Write journal");

        check("Controller has 3 habits", ctrl.getTotalHabitCount() == 3);
        check("0 completed today initially", ctrl.getCompletedTodayCount() == 0);

        Habit first = ctrl.getAllHabits().get(0);
        ctrl.completeHabit(first.getId());
        check("1 completed today after marking", ctrl.getCompletedTodayCount() == 1);

        check("Filter by health returns 1", ctrl.getHabitsByCategory("health").size() == 1);
        check("Filter by study returns 1", ctrl.getHabitsByCategory("study").size() == 1);
    }

    static void testEditAndDelete() {
        HabitController ctrl = new HabitController();
        Habit h = ctrl.addHabit("health", "Walk", "Walk 30 min");

        ctrl.editHabit(h.getId(), "Power Walk", "Walk 45 min");
        check("Name updated after edit", ctrl.findById(h.getId()).get().getName().equals("Power Walk"));

        ctrl.deleteHabit(h.getId());
        check("Habit removed after delete", ctrl.getTotalHabitCount() == 0);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  ✓ " + name);
            passed++;
        } else {
            System.out.println("  ✗ FAIL: " + name);
            failed++;
        }
    }
}
