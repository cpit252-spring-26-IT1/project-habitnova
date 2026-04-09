package com.habitnova.view;

import com.habitnova.controller.HabitController;
import com.habitnova.model.Habit;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * View in the MVC architecture.
 * Console-based user interface for interacting with the HabitNova system.
 */
public class ConsoleView {

    private final HabitController controller;
    private final Scanner scanner;

    private static final String RESET  = "\033[0m";
    private static final String BOLD   = "\033[1m";
    private static final String GREEN  = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String CYAN   = "\033[36m";
    private static final String RED    = "\033[31m";
    private static final String PURPLE = "\033[35m";

    public ConsoleView(HabitController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main application loop.
     */
    public void start() {
        displayWelcome();
        boolean running = true;

        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": handleAddHabit(); break;
                case "2": handleViewHabits(); break;
                case "3": handleCompleteHabit(); break;
                case "4": handleEditHabit(); break;
                case "5": handleDeleteHabit(); break;
                case "6": handleViewProgress(); break;
                case "7":
                    running = false;
                    displayGoodbye();
                    break;
                default:
                    System.out.println(RED + "  Invalid option. Please try again." + RESET);
            }
        }
        scanner.close();
    }

    private void displayWelcome() {
        System.out.println();
        System.out.println(CYAN + BOLD + "╔══════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD + "║                                              ║" + RESET);
        System.out.println(CYAN + BOLD + "║       ✦  H A B I T N O V A  ✦               ║" + RESET);
        System.out.println(CYAN + BOLD + "║       Smart Habit Tracker                    ║" + RESET);
        System.out.println(CYAN + BOLD + "║                                              ║" + RESET);
        System.out.println(CYAN + BOLD + "╚══════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    private void displayMenu() {
        System.out.println();
        System.out.println(BOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
        System.out.printf("  Habits: %d  |  Completed Today: %d%n",
                controller.getTotalHabitCount(),
                controller.getCompletedTodayCount());
        System.out.println(BOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
        System.out.println("  1. " + GREEN + "Add Habit" + RESET);
        System.out.println("  2. " + CYAN + "View All Habits" + RESET);
        System.out.println("  3. " + YELLOW + "Complete Habit" + RESET);
        System.out.println("  4. " + PURPLE + "Edit Habit" + RESET);
        System.out.println("  5. " + RED + "Delete Habit" + RESET);
        System.out.println("  6. " + CYAN + "View Progress" + RESET);
        System.out.println("  7. Exit");
        System.out.print("\n  Choose an option: ");
    }

    private void handleAddHabit() {
        System.out.println();
        System.out.println(GREEN + BOLD + "  ── Add New Habit ──" + RESET);
        System.out.println("  Categories: [health] [study] [lifestyle]");
        System.out.print("  Category: ");
        String category = scanner.nextLine().trim();

        System.out.print("  Habit Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("  Description: ");
        String description = scanner.nextLine().trim();

        try {
            Habit habit = controller.addHabit(category, name, description);
            System.out.println(GREEN + "  ✓ Habit created: " + habit.getName()
                    + " [" + habit.getCategory() + "] (ID: " + habit.getId() + ")" + RESET);
        } catch (IllegalArgumentException e) {
            System.out.println(RED + "  ✗ " + e.getMessage() + RESET);
        }
    }

    private void handleViewHabits() {
        List<Habit> habits = controller.getAllHabits();
        System.out.println();
        System.out.println(CYAN + BOLD + "  ── Your Habits ──" + RESET);

        if (habits.isEmpty()) {
            System.out.println("  No habits yet. Add one to get started!");
            return;
        }

        for (Habit h : habits) {
            String status = h.isCompletedForToday() ? GREEN + "✓" + RESET : "○";
            System.out.printf("  %s  %-10s %-20s %-30s  Streak: %d%n",
                    status, h.getId(), "[" + h.getCategory() + "] " + h.getName(),
                    h.getDescription(), h.getCurrentStreak());
        }
    }

    private void handleCompleteHabit() {
        List<Habit> habits = controller.getAllHabits();
        if (habits.isEmpty()) {
            System.out.println(YELLOW + "  No habits to complete." + RESET);
            return;
        }

        handleViewHabits();
        System.out.print("\n  Enter Habit ID to complete: ");
        String id = scanner.nextLine().trim();

        if (controller.completeHabit(id)) {
            Optional<Habit> habit = controller.findById(id);
            habit.ifPresent(h -> {
                System.out.println(GREEN + "  ✓ Completed: " + h.getName() + RESET);
                System.out.println(YELLOW + "  " + h.getMotivationalMessage() + RESET);
            });
        } else {
            System.out.println(RED + "  ✗ Habit not found with ID: " + id + RESET);
        }
    }

    private void handleEditHabit() {
        handleViewHabits();
        System.out.print("\n  Enter Habit ID to edit: ");
        String id = scanner.nextLine().trim();

        System.out.print("  New Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("  New Description: ");
        String desc = scanner.nextLine().trim();

        if (controller.editHabit(id, name, desc)) {
            System.out.println(PURPLE + "  ✓ Habit updated." + RESET);
        } else {
            System.out.println(RED + "  ✗ Habit not found with ID: " + id + RESET);
        }
    }

    private void handleDeleteHabit() {
        handleViewHabits();
        System.out.print("\n  Enter Habit ID to delete: ");
        String id = scanner.nextLine().trim();

        if (controller.deleteHabit(id)) {
            System.out.println(RED + "  ✓ Habit deleted." + RESET);
        } else {
            System.out.println(RED + "  ✗ Habit not found with ID: " + id + RESET);
        }
    }

    private void handleViewProgress() {
        List<Habit> habits = controller.getAllHabits();
        System.out.println();
        System.out.println(CYAN + BOLD + "  ── Progress Dashboard ──" + RESET);

        if (habits.isEmpty()) {
            System.out.println("  No habits to show progress for.");
            return;
        }

        System.out.printf("  Overall Completion Rate: %.1f%%%n", controller.getOverallCompletionRate());
        System.out.printf("  Completed Today: %d / %d%n",
                controller.getCompletedTodayCount(), controller.getTotalHabitCount());
        System.out.println();

        for (Habit h : habits) {
            String bar = buildProgressBar(h.getCompletionRate());
            System.out.printf("  %-25s %s %.1f%%  (Streak: %d | Best: %d)%n",
                    h.getName(), bar, h.getCompletionRate(),
                    h.getCurrentStreak(), h.getBestStreak());
            System.out.println(YELLOW + "    " + h.getMotivationalMessage() + RESET);
        }
    }

    private String buildProgressBar(double percentage) {
        int filled = (int) (percentage / 5);
        int empty = 20 - filled;
        return GREEN + "█".repeat(Math.max(0, filled)) + RESET
                + "░".repeat(Math.max(0, empty));
    }

    private void displayGoodbye() {
        System.out.println();
        System.out.println(CYAN + BOLD + "  ✦ Keep building great habits! See you next time. ✦" + RESET);
        System.out.println();
    }
}
