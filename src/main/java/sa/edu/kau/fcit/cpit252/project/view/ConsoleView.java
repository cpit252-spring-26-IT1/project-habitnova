package sa.edu.kau.fcit.cpit252.project.view;

import sa.edu.kau.fcit.cpit252.project.controller.HabitController;
import sa.edu.kau.fcit.cpit252.project.decorator.PriorityDecorator;
import sa.edu.kau.fcit.cpit252.project.decorator.ReminderDecorator;
import sa.edu.kau.fcit.cpit252.project.model.Habit;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
                case "6": handleSetReminder(); break;
                case "7": handleSetPriority(); break;
                case "8": handleViewProgress(); break;
                case "9": handleViewActivityLog(); break;
                case "10":
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

        // Show high-priority alerts
        List<Habit> urgent = controller.getHighPriorityPending();
        if (!urgent.isEmpty()) {
            System.out.println(RED + "  🔴 " + urgent.size()
                    + " high-priority habit(s) pending!" + RESET);
        }

        System.out.println(BOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
        System.out.println("  1. " + GREEN  + "Add Habit" + RESET);
        System.out.println("  2. " + CYAN   + "View All Habits" + RESET);
        System.out.println("  3. " + YELLOW + "Complete Habit" + RESET);
        System.out.println("  4. " + PURPLE + "Edit Habit" + RESET);
        System.out.println("  5. " + RED    + "Delete Habit" + RESET);
        System.out.println("  6. " + YELLOW + "Set Reminder" + RESET + "  ⏰");
        System.out.println("  7. " + YELLOW + "Set Priority" + RESET + "  🔴");
        System.out.println("  8. " + CYAN   + "View Progress" + RESET);
        System.out.println("  9. " + PURPLE + "View Activity Log" + RESET + "  📋");
        System.out.println(" 10. Exit");
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
            String extras = getDecoratorInfo(h);
            System.out.printf("  %s  %-10s %-20s %-30s  Streak: %d%s%n",
                    status, h.getId(), "[" + h.getCategory() + "] " + h.getName(),
                    h.getDescription(), h.getCurrentStreak(), extras);
        }
    }


    private String getDecoratorInfo(Habit h) {
        StringBuilder info = new StringBuilder();

        if (h instanceof PriorityDecorator) {
            info.append("  | ").append(((PriorityDecorator) h).getPriority().getLabel());
        }
        if (h instanceof ReminderDecorator) {
            info.append("  | ⏰ ").append(((ReminderDecorator) h).getReminderTime());
        }

        if (h instanceof ReminderDecorator) {
            Habit directInner = ((ReminderDecorator) h).getDirectWrapped();
            if (directInner instanceof PriorityDecorator) {
                info.append("  | ").append(((PriorityDecorator) directInner).getPriority().getLabel());
            }
        }
        if (h instanceof PriorityDecorator) {
            Habit directInner = ((PriorityDecorator) h).getDirectWrapped();
            if (directInner instanceof ReminderDecorator) {
                info.append("  | ⏰ ").append(((ReminderDecorator) directInner).getReminderTime());
            }
        }

        return info.toString();
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

    private void handleSetReminder() {
        List<Habit> habits = controller.getAllHabits();
        if (habits.isEmpty()) {
            System.out.println(YELLOW + "  No habits available." + RESET);
            return;
        }

        handleViewHabits();
        System.out.println();
        System.out.println(YELLOW + BOLD + "  ── Set Reminder (Decorator Pattern) ──" + RESET);
        System.out.print("  Enter Habit ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("  Reminder time (HH:MM, e.g., 07:30): ");
        String time = scanner.nextLine().trim();

        if (controller.addReminder(id, time)) {
            System.out.println(GREEN + "  ✓ Reminder set for " + time + RESET);
        } else {
            System.out.println(RED + "  ✗ Habit not found with ID: " + id + RESET);
        }
    }

    private void handleSetPriority() {
        List<Habit> habits = controller.getAllHabits();
        if (habits.isEmpty()) {
            System.out.println(YELLOW + "  No habits available." + RESET);
            return;
        }

        handleViewHabits();
        System.out.println();
        System.out.println(YELLOW + BOLD + "  ── Set Priority (Decorator Pattern) ──" + RESET);
        System.out.print("  Enter Habit ID: ");
        String id = scanner.nextLine().trim();

        System.out.println("  Priority levels: [high] [medium] [low]");
        System.out.print("  Priority: ");
        String input = scanner.nextLine().trim().toUpperCase();

        try {
            PriorityDecorator.Priority priority = PriorityDecorator.Priority.valueOf(input);
            if (controller.setPriority(id, priority)) {
                System.out.println(GREEN + "  ✓ Priority set to " + priority.getLabel() + RESET);
            } else {
                System.out.println(RED + "  ✗ Habit not found with ID: " + id + RESET);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(RED + "  ✗ Invalid priority. Use: high, medium, or low" + RESET);
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

    private void handleViewActivityLog() {
        System.out.println();
        System.out.println(PURPLE + BOLD + "  ── Activity Log (Observer Pattern) ──" + RESET);
        System.out.println("  Events captured by the CompletionLogObserver:");
        System.out.println();

        var log = controller.getCompletionLogObserver().getLog();

        if (log.isEmpty()) {
            System.out.println(YELLOW + "  No activity recorded yet. Complete a habit to see events." + RESET);
            return;
        }

        var recent = controller.getCompletionLogObserver().getRecent(20);
        for (String entry : recent) {
            if (entry.contains("COMPLETED ")) {
                System.out.println(GREEN + "  " + entry + RESET);
            } else {
                System.out.println(YELLOW + "  " + entry + RESET);
            }
        }

        System.out.println();
        System.out.printf(CYAN + "  Total events logged: %d%n" + RESET, log.size());
    }

    private void displayGoodbye() {
        System.out.println();
        System.out.println(CYAN + BOLD + "  ✦ Keep building great habits! See you next time. ✦" + RESET);
        System.out.println();
    }
}
