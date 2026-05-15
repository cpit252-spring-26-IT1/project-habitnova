package sa.edu.kau.fcit.cpit252.project.observer;

import sa.edu.kau.fcit.cpit252.project.model.Habit;

import java.util.HashMap;
import java.util.Map;


public class StreakBreakObserver implements HabitObserver {

    private static final String RED    = "\033[31m";
    private static final String YELLOW = "\033[33m";
    private static final String BOLD   = "\033[1m";
    private static final String RESET  = "\033[0m";

    private final Map<String, Integer> breakCounts = new HashMap<>();

    @Override
    public void update(Habit habit, HabitEvent event) {
        if (event != HabitEvent.STREAK_BROKEN) {
            return;
        }

        breakCounts.merge(habit.getId(), 1, Integer::sum);

        System.out.println();
        System.out.println(RED + BOLD + "  ⚠  Streak broken for: " + habit.getName() + RESET);
        System.out.println(YELLOW + "    Don't worry — you can start a new streak today.");
        System.out.println("    Your best ever was " + habit.getBestStreak()
                + " days. You've got this!" + RESET);
    }


    public int getBreakCount(String habitId) {
        return breakCounts.getOrDefault(habitId, 0);
    }
}
