package com.habitnova.decorator;
import com.habitnova.model.Habit;

public class ReminderDecorator extends HabitDecorator {
    private String reminderTime;

    public ReminderDecorator(Habit wrappedHabit, String reminderTime) {
        super(wrappedHabit);
        this.reminderTime = reminderTime;
    }

    @Override
    public String getMotivationalMessage() {
        String original = wrappedHabit.getMotivationalMessage();
        return original + "\n    ⏰ Reminder set for " + reminderTime;
    }

    @Override
    public String toString() {
        return wrappedHabit.toString() + " | ⏰ Reminder: " + reminderTime;
    }

    public boolean isReminderDue() {
        java.time.LocalTime now = java.time.LocalTime.now();
        String currentTime = String.format("%02d:%02d", now.getHour(), now.getMinute());
        return currentTime.equals(reminderTime) && !wrappedHabit.isCompletedForToday();
    }

    public String getReminderNotification() {
        if (!wrappedHabit.isCompletedForToday()) {
            return String.format("⏰ REMINDER: Don't forget to \"%s\" — %s",
                    wrappedHabit.getName(), wrappedHabit.getDescription());
        }
        return String.format("✅ \"%s\" is already done for today. Great job!",
                wrappedHabit.getName());
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}