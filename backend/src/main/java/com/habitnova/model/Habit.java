package com.habitnova.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Habit {
    private String id;
    private String name;
    private String description;
    private String category;
    private LocalDate createdDate;
    private List<LocalDate> completionDates;
    private int currentStreak;
    private int bestStreak;

    public Habit(String id, String name, String description, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdDate = LocalDate.now();
        this.completionDates = new ArrayList<>();
        this.currentStreak = 0;
        this.bestStreak = 0;
    }

    public abstract boolean isCompletedForToday();
    public abstract String getMotivationalMessage();

    public void markCompleted() {
        LocalDate today = LocalDate.now();
        if (!completionDates.contains(today)) {
            completionDates.add(today);
            updateStreak();
        }
    }

    public void unmarkCompleted() {
        LocalDate today = LocalDate.now();
        completionDates.remove(today);
        recalculateStreak();
    }

    private void updateStreak() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (completionDates.contains(yesterday) || currentStreak == 0) {
            currentStreak++;
        } else {
            currentStreak = 1;
        }

        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
    }

    private void recalculateStreak() {
        currentStreak = 0;
        LocalDate date = LocalDate.now();
        while (completionDates.contains(date)) {
            currentStreak++;
            date = date.minusDays(1);
        }
        
    }

    public double getCompletionRate() {
        if (createdDate == null) return 0.0;
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(createdDate, LocalDate.now()) + 1;
        if (totalDays <= 0) return 0.0;
        return (completionDates.size() * 100.0) / totalDays;
    }

    // ── Getters & Setters ──

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getCreatedDate() { return createdDate; }

    public List<LocalDate> getCompletionDates() { return new ArrayList<>(completionDates); }

    public int getCurrentStreak() { return currentStreak; }
    public int getBestStreak() { return bestStreak; }

    @Override
    public String toString() {
        return String.format("[%s] %s — %s | Streak: %d | Best: %d | Rate: %.1f%%",
                category, name, description, currentStreak, bestStreak, getCompletionRate());
    }
}
