package sa.edu.kau.fcit.cpit252.project.observer;

import sa.edu.kau.fcit.cpit252.project.model.Habit;

import java.util.ArrayList;
import java.util.List;


public class MilestoneObserver implements HabitObserver {

    private static final String GREEN  = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String BOLD   = "\033[1m";
    private static final String RESET  = "\033[0m";


    private final List<String> celebratedMilestones = new ArrayList<>();

    @Override
    public void update(Habit habit, HabitEvent event) {
        if (event != HabitEvent.MILESTONE_REACHED) {
            return;
        }

        int streak = habit.getCurrentStreak();
        String key = habit.getId() + ":" + streak;

        if (celebratedMilestones.contains(key)) return;
        celebratedMilestones.add(key);

        String banner = buildCelebrationBanner(habit.getName(), streak);
        System.out.println(banner);
    }

    private String buildCelebrationBanner(String habitName, int streak) {
        String medal;
        String tier;

        if (streak >= 100) {
            medal = "🏆";
            tier = "LEGENDARY";
        } else if (streak >= 30) {
            medal = "🥇";
            tier = "GOLD";
        } else {
            medal = "🥈";
            tier = "SILVER";
        }

        return String.format("%n%s%s╔══════════════════════════════════════════════╗%s%n" +
                        "%s%s║  %s  MILESTONE UNLOCKED — %s TIER         ║%s%n" +
                        "%s%s║  \"%s\" — %d day streak!%s%n" +
                        "%s%s╚══════════════════════════════════════════════╝%s",
                BOLD, YELLOW, RESET,
                BOLD, GREEN, medal, tier, RESET,
                BOLD, YELLOW, habitName, streak, RESET,
                BOLD, YELLOW, RESET);
    }


    public List<String> getCelebratedMilestones() {
        return new ArrayList<>(celebratedMilestones);
    }
}