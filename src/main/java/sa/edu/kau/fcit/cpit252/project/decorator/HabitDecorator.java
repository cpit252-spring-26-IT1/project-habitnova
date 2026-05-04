package sa.edu.kau.fcit.cpit252.project.decorator;

import sa.edu.kau.fcit.cpit252.project.model.Habit;

import java.time.LocalDate;
import java.util.List;

public abstract class HabitDecorator extends Habit{
    protected final Habit wrappedHabit;

    public HabitDecorator(Habit wrappedHabit) {
        super(wrappedHabit.getId(), wrappedHabit.getName(),
                wrappedHabit.getDescription(), wrappedHabit.getCategory());
        this.wrappedHabit = wrappedHabit;
    }

    @Override
    public boolean isCompletedForToday() {
        return wrappedHabit.isCompletedForToday();
    }

    @Override
    public String getMotivationalMessage() {
        return wrappedHabit.getMotivationalMessage();
    }

    @Override
    public void markCompleted() {
        wrappedHabit.markCompleted();
    }

    @Override
    public void unmarkCompleted() {
        wrappedHabit.unmarkCompleted();
    }

    @Override
    public double getCompletionRate() {
        return wrappedHabit.getCompletionRate();
    }

    @Override
    public int getCurrentStreak() {
        return wrappedHabit.getCurrentStreak();
    }

    @Override
    public int getBestStreak() {
        return wrappedHabit.getBestStreak();
    }

    @Override
    public List<LocalDate> getCompletionDates() {
        return wrappedHabit.getCompletionDates();
    }

    @Override
    public LocalDate getCreatedDate() {
        return wrappedHabit.getCreatedDate();
    }

    /**
     * Returns the original unwrapped habit at the bottom of the decorator chain.
     */
    public Habit getWrappedHabit() {
        if (wrappedHabit instanceof HabitDecorator) {
            return ((HabitDecorator) wrappedHabit).getWrappedHabit();
        }
        return wrappedHabit;
    }

    /**
     * Returns the directly wrapped habit (one level up).
     */
    public Habit getDirectWrapped() {
        return wrappedHabit;
    }
}
