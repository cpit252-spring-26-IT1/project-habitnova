package sa.edu.kau.fcit.cpit252.project.controller;

import sa.edu.kau.fcit.cpit252.project.decorator.HabitDecorator;
import sa.edu.kau.fcit.cpit252.project.decorator.PriorityDecorator;
import sa.edu.kau.fcit.cpit252.project.decorator.ReminderDecorator;
import sa.edu.kau.fcit.cpit252.project.factory.HabitFactory;
import sa.edu.kau.fcit.cpit252.project.model.Habit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HabitController {
    private final List<Habit> habits;

    public HabitController() {
        this.habits = new ArrayList<>();
    }

    public Habit addHabit(String category, String name, String description) {
        Habit habit = HabitFactory.createHabit(category, name, description);
        habits.add(habit);
        return habit;
    }

    public boolean addReminder(String id, String reminderTime) {
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId().equals(id)) {
                Habit original = habits.get(i);
                Habit decorated = new ReminderDecorator(original, reminderTime);
                habits.set(i, decorated);
                return true;
            }
        }
        return false;
    }

    public boolean setPriority(String id, PriorityDecorator.Priority priority) {
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId().equals(id)) {
                Habit original = habits.get(i);
                Habit decorated = new PriorityDecorator(original, priority);
                habits.set(i, decorated);
                return true;
            }
        }
        return false;
    }

    public boolean deleteHabit(String id) {
        return habits.removeIf(h -> h.getId().equals(id));
    }

    public boolean editHabit(String id, String newName, String newDescription) {
        Optional<Habit> found = findById(id);
        if (found.isPresent()) {
            found.get().setName(newName);
            found.get().setDescription(newDescription);
            return true;
        }
        return false;
    }

    public boolean completeHabit(String id) {
        Optional<Habit> found = findById(id);
        if (found.isPresent()) {
            found.get().markCompleted();
            return true;
        }
        return false;
    }

    public boolean uncompleteHabit(String id) {
        Optional<Habit> found = findById(id);
        if (found.isPresent()) {
            found.get().unmarkCompleted();
            return true;
        }
        return false;
    }

    public List<Habit> getAllHabits() {
        return new ArrayList<>(habits);
    }

    public List<Habit> getHabitsByCategory(String category) {
        return habits.stream()
                .filter(h -> h.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Habit> getHighPriorityPending() {
        return habits.stream()
                .filter(h -> h instanceof PriorityDecorator)
                .filter(h -> ((PriorityDecorator) h).needsAttention())
                .collect(Collectors.toList());
    }

    public Optional<Habit> findById(String id) {
        return habits.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst();
    }

    public long getCompletedTodayCount() {
        return habits.stream()
                .filter(Habit::isCompletedForToday)
                .count();
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
}
