package com.habitnova.model;

import java.time.LocalDate;

public class LifestyleHabit extends Habit {

    private String timeOfDay;

    public LifestyleHabit(String id, String name, String description) {
        super(id, name, description, "Lifestyle");
        this.timeOfDay = "morning";
    }

    public LifestyleHabit(String id, String name, String description, String timeOfDay) {
        super(id, name, description, "Lifestyle");
        this.timeOfDay = timeOfDay;
    }

    @Override
    public boolean isCompletedForToday() {
        return getCompletionDates().contains(LocalDate.now());
    }

    @Override
    public String getMotivationalMessage() {
        int streak = getCurrentStreak();
        if (streak == 0) {
            return "🌱 Small steps lead to big changes. Start today!";
        } else if (streak < 7) {
            return String.format("✨ %d-day streak! You're cultivating good vibes.", streak);
        } else if (streak < 30) {
            return String.format("🌟 %d days of self-improvement! Keep shining.", streak);
        } else {
            return String.format("🎯 %d-day streak! Lifestyle master!", streak);
        }
    }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
}
