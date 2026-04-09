package com.habitnova.model;

import java.time.LocalDate;

public class HealthHabit extends Habit {
    private int targetMinutes;

    public HealthHabit(String id, String name, String description) {
        super(id, name, description, "Health");
        this.targetMinutes = 30;
    }

    public HealthHabit(String id, String name, String description, int targetMinutes) {
        super(id, name, description, "Health");
        this.targetMinutes = targetMinutes;
    }

    @Override
    public boolean isCompletedForToday() {
        return getCompletionDates().contains(LocalDate.now());
    }

    @Override
    public String getMotivationalMessage() {
        int streak = getCurrentStreak();
        if (streak == 0) {
            return "💪 Start your health journey today! Every step counts.";
        } else if (streak < 7) {
            return String.format("🔥 %d-day streak! Your body is thanking you.", streak);
        } else if (streak < 30) {
            return String.format("⭐ %d days strong! You're building a healthier you.", streak);
        } else {
            return String.format("🏆 %d-day streak! You're a health champion!", streak);
        }
    }

    public int getTargetMinutes() { return targetMinutes; }
    public void setTargetMinutes(int targetMinutes) { this.targetMinutes = targetMinutes; }
}
