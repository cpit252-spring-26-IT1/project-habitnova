package sa.edu.kau.fcit.cpit252.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sa.edu.kau.fcit.cpit252.project.decorator.PriorityDecorator;
import sa.edu.kau.fcit.cpit252.project.decorator.ReminderDecorator;
import sa.edu.kau.fcit.cpit252.project.factory.HabitFactory;
import sa.edu.kau.fcit.cpit252.project.model.*;
import sa.edu.kau.fcit.cpit252.project.observer.*;
import sa.edu.kau.fcit.cpit252.project.service.HabitService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DesignPatternsTest {

    @BeforeEach void setUp() { HabitFactory.resetIdCounter(); }

    @Test @DisplayName("Factory: HealthHabit")
    void factoryHealth() {
        Habit h = HabitFactory.createHabit("health", "Run", "Jog");
        assertInstanceOf(HealthHabit.class, h);
        assertEquals("Health", h.getCategory());
    }

    @Test @DisplayName("Factory: StudyHabit")
    void factoryStudy() { assertInstanceOf(StudyHabit.class, HabitFactory.createHabit("study", "Read", "Books")); }

    @Test @DisplayName("Factory: LifestyleHabit")
    void factoryLifestyle() { assertInstanceOf(LifestyleHabit.class, HabitFactory.createHabit("lifestyle", "Med", "Calm")); }

    @Test @DisplayName("Factory: rejects unknown")
    void factoryRejects() { assertThrows(IllegalArgumentException.class, () -> HabitFactory.createHabit("x", "a", "b")); }

    @Test @DisplayName("Factory: unique IDs")
    void factoryIds() { assertNotEquals(HabitFactory.createHabit("health","A","x").getId(), HabitFactory.createHabit("study","B","y").getId()); }

    // === Decorator ===

    @Test @DisplayName("Reminder preserves identity")
    void reminder() {
        Habit h = HabitFactory.createHabit("health", "Run", "Jog");
        ReminderDecorator d = new ReminderDecorator(h, "07:00");
        assertEquals(h.getId(), d.getId());
        assertEquals("07:00", d.getReminderTime());
    }

    @Test @DisplayName("Priority stores level")
    void priority() {
        Habit h = HabitFactory.createHabit("study", "Algo", "DS");
        PriorityDecorator d = new PriorityDecorator(h, PriorityDecorator.Priority.HIGH);
        assertEquals(PriorityDecorator.Priority.HIGH, d.getPriority());
        assertTrue(d.needsAttention());
    }

    @Test @DisplayName("Stacking decorators")
    void stacked() {
        Habit h = HabitFactory.createHabit("lifestyle", "Med", "Calm");
        Habit d = new ReminderDecorator(new PriorityDecorator(h, PriorityDecorator.Priority.HIGH), "06:00");
        assertEquals(h.getId(), d.getId());
        assertTrue(d.getMotivationalMessage().contains("06:00"));
        assertTrue(d.getMotivationalMessage().contains("HIGH"));
    }

    @Test @DisplayName("Completion flows through decorator")
    void decoratorComplete() {
        Habit h = HabitFactory.createHabit("health", "Run", "Jog");
        Habit d = new ReminderDecorator(h, "07:00");
        d.markCompleted();
        assertTrue(d.isCompletedForToday());
        assertEquals(1, d.getCurrentStreak());
    }

    // === Observer ===

    static class SpyObserver implements HabitObserver {
        final List<HabitEvent> events = new ArrayList<>();
        @Override public void update(Habit habit, HabitEvent event) { events.add(event); }
        int countOf(HabitEvent e) { return (int) events.stream().filter(ev -> ev == e).count(); }
    }

    @Test @DisplayName("COMPLETED fires")
    void completed() {
        Habit h = HabitFactory.createHabit("health", "Run", "Jog");
        SpyObserver s = new SpyObserver(); h.addObserver(s); h.markCompleted();
        assertEquals(1, s.countOf(HabitEvent.COMPLETED));
    }

    @Test @DisplayName("STREAK_BROKEN fires on unmark")
    void streakBroken() {
        Habit h = HabitFactory.createHabit("health", "Run", "Jog");
        SpyObserver s = new SpyObserver(); h.addObserver(s);
        h.markCompleted(); h.unmarkCompleted();
        assertEquals(1, s.countOf(HabitEvent.STREAK_BROKEN));
    }

    @Test @DisplayName("Multiple observers notified")
    void multiObs() {
        Habit h = HabitFactory.createHabit("health", "Run", "Jog");
        SpyObserver a = new SpyObserver(), b = new SpyObserver();
        h.addObserver(a); h.addObserver(b); h.markCompleted();
        assertEquals(1, a.countOf(HabitEvent.COMPLETED));
        assertEquals(1, b.countOf(HabitEvent.COMPLETED));
    }

    @Test @DisplayName("CompletionLogObserver logs")
    void logObs() {
        CompletionLogObserver log = new CompletionLogObserver();
        Habit h = HabitFactory.createHabit("health", "Yoga", "Stretch");
        h.addObserver(log); h.markCompleted(); h.unmarkCompleted();
        assertEquals(2, log.size());
    }

    @Test @DisplayName("Events fire through decorators")
    void obsThroughDec() {
        Habit h = HabitFactory.createHabit("health", "Run", "Jog");
        SpyObserver s = new SpyObserver(); h.addObserver(s);
        new ReminderDecorator(h, "07:00").markCompleted();
        assertEquals(1, s.countOf(HabitEvent.COMPLETED));
    }

    // === Service ===

    @Test @DisplayName("Service attaches 3 observers")
    void svcObservers() {
        HabitService svc = new HabitService();
        assertEquals(3, svc.addHabit("health", "Run", "Jog").getObserverCount());
    }

    @Test @DisplayName("Service logs completions")
    void svcLog() {
        HabitService svc = new HabitService();
        Habit h = svc.addHabit("health", "Run", "Jog");
        svc.completeHabit(h.getId());
        assertEquals(1, svc.getCompletionLogObserver().size());
    }
}
