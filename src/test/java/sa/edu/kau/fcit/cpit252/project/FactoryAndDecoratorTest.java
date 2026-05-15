package sa.edu.kau.fcit.cpit252.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sa.edu.kau.fcit.cpit252.project.decorator.PriorityDecorator;
import sa.edu.kau.fcit.cpit252.project.decorator.ReminderDecorator;
import sa.edu.kau.fcit.cpit252.project.factory.HabitFactory;
import sa.edu.kau.fcit.cpit252.project.model.Habit;
import sa.edu.kau.fcit.cpit252.project.model.HealthHabit;
import sa.edu.kau.fcit.cpit252.project.model.LifestyleHabit;
import sa.edu.kau.fcit.cpit252.project.model.StudyHabit;

import static org.junit.jupiter.api.Assertions.*;

class FactoryAndDecoratorTest {

    @BeforeEach
    void setUp() {
        HabitFactory.resetIdCounter();
    }

    @Test
    @DisplayName("Factory: creates HealthHabit for 'health' category")
    void createsHealthHabit() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Morning run");
        assertTrue(habit instanceof HealthHabit);
        assertEquals("Health", habit.getCategory());
    }

    @Test
    @DisplayName("Factory: creates StudyHabit for 'study' category")
    void createsStudyHabit() {
        Habit habit = HabitFactory.createHabit("study", "Read", "Read 10 pages");
        assertTrue(habit instanceof StudyHabit);
        assertEquals("Study", habit.getCategory());
    }

    @Test
    @DisplayName("Factory: creates LifestyleHabit for 'lifestyle' category")
    void createsLifestyleHabit() {
        Habit habit = HabitFactory.createHabit("lifestyle", "Meditate", "Daily calm");
        assertTrue(habit instanceof LifestyleHabit);
        assertEquals("Lifestyle", habit.getCategory());
    }

    @Test
    @DisplayName("Factory: rejects unknown categories with IllegalArgumentException")
    void rejectsUnknownCategory() {
        assertThrows(IllegalArgumentException.class,
                () -> HabitFactory.createHabit("gaming", "Play", "Game time"));
    }

    @Test
    @DisplayName("Factory: each habit gets a unique ID")
    void uniqueIds() {
        Habit h1 = HabitFactory.createHabit("health", "A", "x");
        Habit h2 = HabitFactory.createHabit("study", "B", "y");
        assertNotEquals(h1.getId(), h2.getId());
    }

    @Test
    @DisplayName("Decorator: ReminderDecorator preserves the base habit's identity")
    void reminderDecoratorPreservesIdentity() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Morning run");
        ReminderDecorator decorated = new ReminderDecorator(habit, "07:00");

        assertEquals(habit.getId(), decorated.getId());
        assertEquals(habit.getName(), decorated.getName());
        assertEquals("07:00", decorated.getReminderTime());
    }

    @Test
    @DisplayName("Decorator: PriorityDecorator stores priority correctly")
    void priorityDecoratorStoresPriority() {
        Habit habit = HabitFactory.createHabit("study", "Algo", "Algorithms");
        PriorityDecorator decorated = new PriorityDecorator(habit, PriorityDecorator.Priority.HIGH);

        assertEquals(PriorityDecorator.Priority.HIGH, decorated.getPriority());
    }

    @Test
    @DisplayName("Decorator: stacked decorators preserve all behaviors")
    void stackedDecoratorsWork() {
        Habit habit = HabitFactory.createHabit("lifestyle", "Meditate", "10 min calm");
        Habit decorated = new PriorityDecorator(habit, PriorityDecorator.Priority.HIGH);
        decorated = new ReminderDecorator(decorated, "06:00");
        assertEquals(habit.getId(), decorated.getId());
        String msg = decorated.getMotivationalMessage();
        assertTrue(msg.contains("06:00"), "Reminder time should appear in message");
        assertTrue(msg.contains("HIGH"),  "Priority should appear in message");
    }

    @Test
    @DisplayName("Decorator: completion still works through decorator chain")
    void completionWorksThroughDecorator() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        Habit decorated = new ReminderDecorator(habit, "07:00");

        decorated.markCompleted();

        assertTrue(decorated.isCompletedForToday());
        assertEquals(1, decorated.getCurrentStreak());
    }

    @Test
    @DisplayName("Decorator: needsAttention returns true for HIGH priority not yet completed")
    void needsAttentionTrueForUnfinishedHigh() {
        Habit habit = HabitFactory.createHabit("study", "Algo", "Algorithms");
        PriorityDecorator decorated = new PriorityDecorator(habit, PriorityDecorator.Priority.HIGH);

        assertTrue(decorated.needsAttention());

        decorated.markCompleted();
        assertFalse(decorated.needsAttention(),
                "After completion, the habit no longer needs attention");
    }
}
