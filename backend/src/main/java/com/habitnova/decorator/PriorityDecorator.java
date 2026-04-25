package com.habitnova.decorator;

import com.habitnova.model.Habit;

public class PriorityDecorator extends HabitDecorator {
    public enum Priority {
        HIGH("🔴 HIGH"),
        MEDIUM("🟡 MEDIUM"),
        LOW("🟢 LOW");

        private final String label;

        Priority(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
    private Priority priority;

    public PriorityDecorator(Habit wrappedHabit, Priority priority) {
        super(wrappedHabit);
        this.priority = priority;
    }

    @Override
    public String getMotivationalMessage() {
        String original = wrappedHabit.getMotivationalMessage();
        switch (priority) {
            case HIGH:
                return original + "\n    🔴 This is a HIGH priority habit — make it happen!";
            case MEDIUM:
                return original + "\n    🟡 MEDIUM priority — stay on track.";
            case LOW:
                return original + "\n    🟢 LOW priority — but still worth doing!";
            default:
                return original;
        }
    }

    @Override
    public String toString() {
        return wrappedHabit.toString() + " | Priority: " + priority.getLabel();
    }

    public boolean needsAttention() {
        return priority == Priority.HIGH && !wrappedHabit.isCompletedForToday();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}