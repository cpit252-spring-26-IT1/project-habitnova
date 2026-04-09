package com.habitnova.model;

import java.time.LocalDate;

public class StudyHabit extends Habit {
    private String subject;
    private int targetPages;

    public StudyHabit(String id, String name, String description) {
        super(id, name, description, "Study");
        this.subject = "General";
        this.targetPages = 10;
    }

    public StudyHabit(String id, String name, String description, String subject, int targetPages) {
        super(id, name, description, "Study");
        this.subject = subject;
        this.targetPages = targetPages;
    }

    @Override
    public boolean isCompletedForToday() {
        return getCompletionDates().contains(LocalDate.now());
    }

    @Override
    public String getMotivationalMessage() {
        int streak = getCurrentStreak();
        if (streak == 0) {
            return "📚 Open that book! Knowledge awaits.";
        } else if (streak < 7) {
            return String.format("📖 %d-day study streak! Your brain is growing.", streak);
        } else if (streak < 30) {
            return String.format("🎓 %d days of studying! You're ahead of the curve.", streak);
        } else {
            return String.format("🧠 %d-day streak! Scholar-level dedication!", streak);
        }
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getTargetPages() { return targetPages; }
    public void setTargetPages(int targetPages) { this.targetPages = targetPages; }
}
