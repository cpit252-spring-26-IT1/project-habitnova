package sa.edu.kau.fcit.cpit252.project.observer;

import sa.edu.kau.fcit.cpit252.project.model.Habit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CompletionLogObserver implements HabitObserver {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<String> logEntries = new ArrayList<>();

    @Override
    public void update(Habit habit, HabitEvent event) {
        if (event != HabitEvent.COMPLETED && event != HabitEvent.UNCOMPLETED) {
            return;
        }

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String action = (event == HabitEvent.COMPLETED) ? "COMPLETED" : "UNCOMPLETED";

        String entry = String.format("[%s] %s — %s (streak: %d)",
                timestamp, action, habit.getName(), habit.getCurrentStreak());

        logEntries.add(entry);
    }

    public List<String> getLog() {
        return new ArrayList<>(logEntries);
    }

    public List<String> getRecent(int n) {
        int size = logEntries.size();
        int from = Math.max(0, size - n);
        return new ArrayList<>(logEntries.subList(from, size));
    }

    public int size() {
        return logEntries.size();
    }

    public void clear() {
        logEntries.clear();
    }
}
