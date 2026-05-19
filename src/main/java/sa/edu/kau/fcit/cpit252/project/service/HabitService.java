package sa.edu.kau.fcit.cpit252.project.service;

import org.springframework.stereotype.Service;
import sa.edu.kau.fcit.cpit252.project.decorator.PriorityDecorator;
import sa.edu.kau.fcit.cpit252.project.decorator.ReminderDecorator;
import sa.edu.kau.fcit.cpit252.project.factory.HabitFactory;
import sa.edu.kau.fcit.cpit252.project.model.Habit;
import sa.edu.kau.fcit.cpit252.project.observer.CompletionLogObserver;
import sa.edu.kau.fcit.cpit252.project.observer.MilestoneObserver;
import sa.edu.kau.fcit.cpit252.project.observer.StreakBreakObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabitService {

    private final List<Habit> habits = new ArrayList<>();

    private final MilestoneObserver milestoneObserver = new MilestoneObserver();
    private final StreakBreakObserver streakBreakObserver = new StreakBreakObserver();
    private final CompletionLogObserver completionLogObserver = new CompletionLogObserver();


    public Habit addHabit(String category, String name, String description) {
        Habit habit = HabitFactory.createHabit(category, name, description);
        attachObservers(habit);
        habits.add(habit);
        return habit;
    }

    private void attachObservers(Habit habit) {
        habit.addObserver(milestoneObserver);
        habit.addObserver(streakBreakObserver);
        habit.addObserver(completionLogObserver);
    }

    public boolean addReminder(String id, String reminderTime) {
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId().equals(id)) {
                habits.set(i, new ReminderDecorator(habits.get(i), reminderTime));
                return true;
            }
        }
        return false;
    }

    public boolean setPriority(String id, PriorityDecorator.Priority priority) {
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId().equals(id)) {
                habits.set(i, new PriorityDecorator(habits.get(i), priority));
                return true;
            }
        }
        return false;
    }

    public boolean deleteHabit(String id) {
        return habits.removeIf(h -> h.getId().equals(id));
    }

    public boolean editHabit(String id, String newName, String newDescription) {
        return findById(id).map(h -> {
            h.setName(newName);
            h.setDescription(newDescription);
            return true;
        }).orElse(false);
    }

    public boolean completeHabit(String id) {
        return findById(id).map(h -> {
            h.markCompleted();
            return true;
        }).orElse(false);
    }

    public boolean uncompleteHabit(String id) {
        return findById(id).map(h -> {
            h.unmarkCompleted();
            return true;
        }).orElse(false);
    }

    public List<Habit> getAllHabits() {
        return new ArrayList<>(habits);
    }

    public List<Habit> getHabitsByCategory(String category) {
        return habits.stream()
                .filter(h -> h.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public Optional<Habit> findById(String id) {
        return habits.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst();
    }

    public long getCompletedTodayCount() {
        return habits.stream().filter(Habit::isCompletedForToday).count();
    }

    public int getTotalHabitCount() {
        return habits.size();
    }

    public double getOverallCompletionRate() {
        if (habits.isEmpty()) return 0.0;
        return habits.stream()
                .mapToDouble(Habit::getCompletionRate)
                .average()
                .orElse(0.0);
    }

    public List<Habit> getHighPriorityPending() {
        return habits.stream()
                .filter(h -> h instanceof PriorityDecorator
                        && ((PriorityDecorator) h).needsAttention())
                .collect(Collectors.toList());
    }

    public CompletionLogObserver getCompletionLogObserver() {
        return completionLogObserver;
    }

    public MilestoneObserver getMilestoneObserver() {
        return milestoneObserver;
    }

    public StreakBreakObserver getStreakBreakObserver() {
        return streakBreakObserver;
    }
}
