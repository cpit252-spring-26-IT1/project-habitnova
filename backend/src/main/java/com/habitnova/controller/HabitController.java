package com.habitnova.controller;

import com.habitnova.factory.HabitFactory;
import com.habitnova.model.Habit;

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
