package sa.edu.kau.fcit.cpit252.project.model;

import sa.edu.kau.fcit.cpit252.project.observer.HabitEvent;
import sa.edu.kau.fcit.cpit252.project.observer.HabitObserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Habit {
    private static final int[] MILESTONES = {7, 30, 100};

    private String id;
    private String name;
    private String description;
    private String category;
    private LocalDate createdDate;
    private List<LocalDate> completionDates;
    private int currentStreak;
    private int bestStreak;
    private final transient List<HabitObserver> observers = new ArrayList<>();

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

    public void addObserver(HabitObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(HabitObserver observer) {
        observers.remove(observer);
    }

    public int getObserverCount() {
        return observers.size();
    }

    protected void notifyObservers(HabitEvent event) {
        for (HabitObserver observer : new ArrayList<>(observers)) {
            observer.update(this, event);
        }
    }


    public void markCompleted() {
        LocalDate today = LocalDate.now();
        if (!completionDates.contains(today)) {
            int previousStreak = currentStreak;
            completionDates.add(today);
            updateStreak();
            notifyObservers(HabitEvent.COMPLETED);

            for (int milestone : MILESTONES) {
                if (currentStreak >= milestone && previousStreak < milestone) {
                    notifyObservers(HabitEvent.MILESTONE_REACHED);
                    break;
                }
            }
        }
    }

    public void unmarkCompleted() {
        LocalDate today = LocalDate.now();
        if (completionDates.contains(today)) {
            int previousStreak = currentStreak;
            completionDates.remove(today);
            recalculateStreak();

            notifyObservers(HabitEvent.UNCOMPLETED);

            if (previousStreak > 0 && currentStreak == 0) {
                notifyObservers(HabitEvent.STREAK_BROKEN);
            }
        }
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

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<LocalDate> getCompletionDates() {
        return new ArrayList<>(completionDates);
    }

    public int getCurrentStreak() {
        return currentStreak;
    }
    public int getBestStreak() {
        return bestStreak;
    }

    public void restoreState(List<LocalDate> storedDates, int storedBestStreak, LocalDate storedCreatedDate) {
        this.completionDates = new ArrayList<>(storedDates);
        this.createdDate = storedCreatedDate;
        this.bestStreak = storedBestStreak;
        recalculateStreak();
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s — %s | Streak: %d | Best: %d | Rate: %.1f%%",
                category, name, description, currentStreak, bestStreak, getCompletionRate());
    }
}
