package sa.edu.kau.fcit.cpit252.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sa.edu.kau.fcit.cpit252.project.controller.HabitController;
import sa.edu.kau.fcit.cpit252.project.factory.HabitFactory;
import sa.edu.kau.fcit.cpit252.project.model.Habit;
import sa.edu.kau.fcit.cpit252.project.observer.CompletionLogObserver;
import sa.edu.kau.fcit.cpit252.project.observer.HabitEvent;
import sa.edu.kau.fcit.cpit252.project.observer.HabitObserver;
import sa.edu.kau.fcit.cpit252.project.observer.MilestoneObserver;
import sa.edu.kau.fcit.cpit252.project.observer.StreakBreakObserver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ObserverPatternTest {

    private HabitController controller;
    private TestObserver spy;

    static class TestObserver implements HabitObserver {
        final List<HabitEvent> received = new ArrayList<>();
        final List<String> habitNames = new ArrayList<>();

        @Override
        public void update(Habit habit, HabitEvent event) {
            received.add(event);
            habitNames.add(habit.getName());
        }

        int countOf(HabitEvent event) {
            return (int) received.stream().filter(e -> e == event).count();
        }
    }

    @BeforeEach
    void setUp() {
        HabitFactory.resetIdCounter();
        controller = new HabitController();
        spy = new TestObserver();
    }

    @Test
    @DisplayName("Subject: addObserver registers an observer")
    void addObserverRegisters() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        assertEquals(0, habit.getObserverCount());

        habit.addObserver(spy);
        assertEquals(1, habit.getObserverCount());
    }

    @Test
    @DisplayName("Subject: addObserver is idempotent — same observer not added twice")
    void addObserverIsIdempotent() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        habit.addObserver(spy);
        habit.addObserver(spy);
        assertEquals(1, habit.getObserverCount(),
                "Adding the same observer twice should not register it twice");
    }

    @Test
    @DisplayName("Subject: addObserver(null) is safely ignored")
    void addNullObserverIgnored() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        habit.addObserver(null);
        assertEquals(0, habit.getObserverCount());
    }

    @Test
    @DisplayName("Subject: removeObserver unregisters an observer")
    void removeObserverUnregisters() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        habit.addObserver(spy);
        habit.removeObserver(spy);
        assertEquals(0, habit.getObserverCount());
    }

    @Test
    @DisplayName("Subject: multiple observers can be attached and all are notified")
    void multipleObserversNotified() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        TestObserver spy2 = new TestObserver();

        habit.addObserver(spy);
        habit.addObserver(spy2);

        habit.markCompleted();

        assertEquals(1, spy.countOf(HabitEvent.COMPLETED));
        assertEquals(1, spy2.countOf(HabitEvent.COMPLETED));
    }

    @Test
    @DisplayName("Event: COMPLETED fires when habit is marked complete")
    void completedEventFires() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        habit.addObserver(spy);

        habit.markCompleted();
        assertEquals(1, spy.countOf(HabitEvent.COMPLETED));
    }

    @Test
    @DisplayName("Event: COMPLETED does NOT fire twice for the same day")
    void completedNotFiredTwice() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        habit.addObserver(spy);

        habit.markCompleted();
        habit.markCompleted();

        assertEquals(1, spy.countOf(HabitEvent.COMPLETED),
                "Marking the same day twice must fire only once");
    }

    @Test
    @DisplayName("Event: UNCOMPLETED fires when completion is undone")
    void uncompletedEventFires() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        habit.addObserver(spy);

        habit.markCompleted();
        habit.unmarkCompleted();

        assertEquals(1, spy.countOf(HabitEvent.UNCOMPLETED));
    }

    @Test
    @DisplayName("Event: UNCOMPLETED does not fire when nothing is completed")
    void uncompletedNoopFiresNothing() {
        Habit habit = HabitFactory.createHabit("health", "Run", "Daily run");
        habit.addObserver(spy);

        habit.unmarkCompleted();
        assertEquals(0, spy.countOf(HabitEvent.UNCOMPLETED));
    }

    @Test
    @DisplayName("Event: STREAK_BROKEN fires when a streak goes from >0 to 0")
    void streakBrokenEventFires() {
        Habit habit = HabitFactory.createHabit("study", "Read", "Read daily");
        habit.addObserver(spy);

        habit.markCompleted();
        habit.unmarkCompleted();

        assertEquals(1, spy.countOf(HabitEvent.STREAK_BROKEN));
    }

    @Test
    @DisplayName("CompletionLogObserver: logs both COMPLETED and UNCOMPLETED events")
    void completionLogObserverLogs() {
        CompletionLogObserver log = new CompletionLogObserver();
        Habit habit = HabitFactory.createHabit("health", "Yoga", "Morning yoga");
        habit.addObserver(log);

        habit.markCompleted();
        habit.unmarkCompleted();

        assertEquals(2, log.size());
        assertTrue(log.getLog().get(0).contains("COMPLETED"));
        assertTrue(log.getLog().get(1).contains("UNCOMPLETED"));
    }

    @Test
    @DisplayName("CompletionLogObserver: ignores milestone events")
    void completionLogObserverIgnoresMilestones() {
        CompletionLogObserver log = new CompletionLogObserver();
        Habit habit = HabitFactory.createHabit("health", "Yoga", "Morning yoga");
        habit.addObserver(log);
        TestObserver other = new TestObserver();
        habit.addObserver(other);
        habit.markCompleted();
        assertEquals(1, log.size(),
                "Log observer should only record one entry (COMPLETED), not milestones");
    }

    @Test
    @DisplayName("StreakBreakObserver: increments break count on STREAK_BROKEN")
    void streakBreakObserverCounts() {
        StreakBreakObserver breakObs = new StreakBreakObserver();
        Habit habit = HabitFactory.createHabit("study", "Algo", "Practice algorithms");
        habit.addObserver(breakObs);

        habit.markCompleted();
        habit.unmarkCompleted();

        assertEquals(1, breakObs.getBreakCount(habit.getId()));
    }

    @Test
    @DisplayName("MilestoneObserver: starts with no milestones celebrated")
    void milestoneObserverStartsEmpty() {
        MilestoneObserver obs = new MilestoneObserver();
        assertTrue(obs.getCelebratedMilestones().isEmpty());
    }

    @Test
    @DisplayName("MilestoneObserver: ignores non-milestone events")
    void milestoneObserverIgnoresOtherEvents() {
        MilestoneObserver obs = new MilestoneObserver();
        Habit habit = HabitFactory.createHabit("study", "Read", "Read daily");
        habit.addObserver(obs);

        habit.markCompleted();
        habit.unmarkCompleted();

        assertTrue(obs.getCelebratedMilestones().isEmpty(),
                "Milestone observer should ignore non-milestone events");
    }

    @Test
    @DisplayName("Controller: auto-attaches all three observers to every new habit")
    void controllerAttachesObservers() {
        Habit habit = controller.addHabit("health", "Run", "Morning run");
        assertEquals(3, habit.getObserverCount());
    }

    @Test
    @DisplayName("Controller: completion is logged in the shared CompletionLogObserver")
    void controllerCompletionLogged() {
        Habit habit = controller.addHabit("health", "Run", "Morning run");
        controller.completeHabit(habit.getId());

        CompletionLogObserver log = controller.getCompletionLogObserver();
        assertEquals(1, log.size());
        assertTrue(log.getLog().get(0).contains("Run"));
    }

    @Test
    @DisplayName("Controller: logs from multiple habits accumulate in one observer")
    void controllerLogsAcrossHabits() {
        Habit h1 = controller.addHabit("health", "Run", "Run daily");
        Habit h2 = controller.addHabit("study", "Read", "Read daily");

        controller.completeHabit(h1.getId());
        controller.completeHabit(h2.getId());

        assertEquals(2, controller.getCompletionLogObserver().size(),
                "Single observer instance should aggregate events from all habits");
    }

    @Test
    @DisplayName("Decorator integration: events still fire when habit is decorated")
    void eventsFireThroughDecorators() {
        Habit habit = controller.addHabit("health", "Run", "Daily run");
        controller.addReminder(habit.getId(), "07:00");
        controller.setPriority(habit.getId(), sa.edu.kau.fcit.cpit252.project
                .decorator.PriorityDecorator.Priority.HIGH);

        controller.completeHabit(habit.getId());

        assertEquals(1, controller.getCompletionLogObserver().size(),
                "Events must still flow to observers even through decorator layers");
    }

    @Test
    @DisplayName("Decorator integration: observer count is shared via wrapped habit")
    void decoratorForwardsObserverCount() {
        Habit habit = controller.addHabit("health", "Run", "Daily run");
        controller.addReminder(habit.getId(), "07:00");

        Habit decorated = controller.findById(habit.getId()).get();
        assertEquals(3, decorated.getObserverCount(),
                "Decorator should forward observer count to wrapped habit");
    }
}
