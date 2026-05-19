package sa.edu.kau.fcit.cpit252.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CoverageReflectionTest {

    private static final String BASE = "sa.edu.kau.fcit.cpit252.project.";

    @BeforeEach
    void resetFactoryCounter() {
        invokeStatic(cls("factory.HabitFactory"), "resetIdCounter", new Class<?>[]{});
    }

    @Test
    void dtoFactoryAndModelBranches() {
        Class<?> requestClass = cls("dto.HabitRequest");
        Object req = construct(requestClass, new Class<?>[]{String.class, String.class, String.class}, "health", "Run", "Jog");
        assertEquals("health", invoke(req, "getCategory", new Class<?>[]{}));
        invoke(req, "setCategory", new Class<?>[]{String.class}, "study");
        invoke(req, "setName", new Class<?>[]{String.class}, "Read");
        invoke(req, "setDescription", new Class<?>[]{String.class}, "20 pages");
        assertEquals("study", invoke(req, "getCategory", new Class<?>[]{}));
        assertEquals("Read", invoke(req, "getName", new Class<?>[]{}));
        assertEquals("20 pages", invoke(req, "getDescription", new Class<?>[]{}));

        Class<?> factory = cls("factory.HabitFactory");
        Object health = invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class}, "health", "Run", "Jog");
        Object study = invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class}, "study", "Read", "Books");
        Object lifestyle = invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class}, "lifestyle", "Meditate", "Calm");

        assertEquals("HealthHabit", health.getClass().getSimpleName());
        assertEquals("StudyHabit", study.getClass().getSimpleName());
        assertEquals("LifestyleHabit", lifestyle.getClass().getSimpleName());

        assertEquals("HABIT-0001", invoke(health, "getId", new Class<?>[]{}));
        assertEquals("HABIT-0002", invoke(study, "getId", new Class<?>[]{}));

        Object health2 = invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class, String.class, int.class}, "health", "Walk", "Outside", "unused", 45);
        Object study2 = invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class, String.class, int.class}, "study", "Algo", "Practice", "Math", 15);
        Object lifestyle2 = invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class, String.class, int.class}, "lifestyle", "Sleep", "Early", "night", 0);

        assertEquals(45, invoke(health2, "getTargetMinutes", new Class<?>[]{}));
        assertEquals("Math", invoke(study2, "getSubject", new Class<?>[]{}));
        assertEquals(15, invoke(study2, "getTargetPages", new Class<?>[]{}));
        assertEquals("night", invoke(lifestyle2, "getTimeOfDay", new Class<?>[]{}));

        invoke(health2, "setTargetMinutes", new Class<?>[]{int.class}, 60);
        invoke(study2, "setSubject", new Class<?>[]{String.class}, "Physics");
        invoke(study2, "setTargetPages", new Class<?>[]{int.class}, 25);
        invoke(lifestyle2, "setTimeOfDay", new Class<?>[]{String.class}, "evening");

        assertEquals(60, invoke(health2, "getTargetMinutes", new Class<?>[]{}));
        assertEquals("Physics", invoke(study2, "getSubject", new Class<?>[]{}));
        assertEquals(25, invoke(study2, "getTargetPages", new Class<?>[]{}));
        assertEquals("evening", invoke(lifestyle2, "getTimeOfDay", new Class<?>[]{}));

        assertThrows(IllegalArgumentException.class, () ->
                invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class}, "unknown", "x", "y"));
        assertThrows(IllegalArgumentException.class, () ->
                invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class, String.class, int.class}, "bad", "x", "y", "z", 1));

        setField(health2, "currentStreak", 0);
        assertTrue(((String) invoke(health2, "getMotivationalMessage", new Class<?>[]{})).contains("Start your health journey"));
        setField(health2, "currentStreak", 2);
        assertTrue(((String) invoke(health2, "getMotivationalMessage", new Class<?>[]{})).contains("2-day streak"));
        setField(health2, "currentStreak", 12);
        assertTrue(((String) invoke(health2, "getMotivationalMessage", new Class<?>[]{})).contains("12 days strong"));
        setField(health2, "currentStreak", 40);
        assertTrue(((String) invoke(health2, "getMotivationalMessage", new Class<?>[]{})).contains("40-day streak"));

        setField(study2, "currentStreak", 0);
        assertTrue(((String) invoke(study2, "getMotivationalMessage", new Class<?>[]{})).contains("Open that book"));
        setField(study2, "currentStreak", 3);
        assertTrue(((String) invoke(study2, "getMotivationalMessage", new Class<?>[]{})).contains("3-day study streak"));
        setField(study2, "currentStreak", 20);
        assertTrue(((String) invoke(study2, "getMotivationalMessage", new Class<?>[]{})).contains("20 days of studying"));
        setField(study2, "currentStreak", 35);
        assertTrue(((String) invoke(study2, "getMotivationalMessage", new Class<?>[]{})).contains("35-day streak"));

        setField(lifestyle2, "currentStreak", 0);
        assertTrue(((String) invoke(lifestyle2, "getMotivationalMessage", new Class<?>[]{})).contains("Small steps"));
        setField(lifestyle2, "currentStreak", 5);
        assertTrue(((String) invoke(lifestyle2, "getMotivationalMessage", new Class<?>[]{})).contains("5-day streak"));
        setField(lifestyle2, "currentStreak", 10);
        assertTrue(((String) invoke(lifestyle2, "getMotivationalMessage", new Class<?>[]{})).contains("10 days of self-improvement"));
        setField(lifestyle2, "currentStreak", 70);
        assertTrue(((String) invoke(lifestyle2, "getMotivationalMessage", new Class<?>[]{})).contains("70-day streak"));
    }

    @Test
    void habitCoreObserverAndDecoratorPaths() {
        Class<?> factory = cls("factory.HabitFactory");
        Object habit = invokeStatic(factory, "createHabit", new Class<?>[]{String.class, String.class, String.class}, "health", "Run", "Jog");

        Class<?> observerInterface = cls("observer.HabitObserver");
        List<String> events = new ArrayList<>();
        Object observerProxy = Proxy.newProxyInstance(
                observerInterface.getClassLoader(),
                new Class<?>[]{observerInterface},
                (proxy, method, args) -> {
                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    if ("toString".equals(method.getName())) {
                        return "spyObserver";
                    }
                    if ("update".equals(method.getName())) {
                        events.add(args[1].toString());
                    }
                    return null;
                });

        invoke(habit, "addObserver", new Class<?>[]{observerInterface}, (Object) null);
        invoke(habit, "addObserver", new Class<?>[]{observerInterface}, observerProxy);
        invoke(habit, "addObserver", new Class<?>[]{observerInterface}, observerProxy);
        assertEquals(1, invoke(habit, "getObserverCount", new Class<?>[]{}));

        invoke(habit, "markCompleted", new Class<?>[]{});
        assertTrue((Boolean) invoke(habit, "isCompletedForToday", new Class<?>[]{}));
        assertEquals(1, invoke(habit, "getCurrentStreak", new Class<?>[]{}));

        invoke(habit, "unmarkCompleted", new Class<?>[]{});
        assertFalse((Boolean) invoke(habit, "isCompletedForToday", new Class<?>[]{}));
        assertEquals(0, invoke(habit, "getCurrentStreak", new Class<?>[]{}));
        assertTrue(events.contains("COMPLETED"));
        assertTrue(events.contains("UNCOMPLETED"));
        assertTrue(events.contains("STREAK_BROKEN"));

        Object completionDates = getField(habit, "completionDates");
        @SuppressWarnings("unchecked")
        List<LocalDate> dates = (List<LocalDate>) completionDates;
        dates.clear();
        dates.add(LocalDate.now().minusDays(1));
        setField(habit, "currentStreak", 6);
        invoke(habit, "markCompleted", new Class<?>[]{});
        assertTrue(events.contains("MILESTONE_REACHED"));
        assertEquals(7, invoke(habit, "getCurrentStreak", new Class<?>[]{}));

        invoke(habit, "setId", new Class<?>[]{String.class}, "HX");
        invoke(habit, "setName", new Class<?>[]{String.class}, "RunFast");
        invoke(habit, "setDescription", new Class<?>[]{String.class}, "Daily run");
        invoke(habit, "setCategory", new Class<?>[]{String.class}, "Health");
        assertEquals("HX", invoke(habit, "getId", new Class<?>[]{}));
        assertEquals("RunFast", invoke(habit, "getName", new Class<?>[]{}));
        assertEquals("Daily run", invoke(habit, "getDescription", new Class<?>[]{}));
        assertEquals("Health", invoke(habit, "getCategory", new Class<?>[]{}));
        assertNotNull(invoke(habit, "getCreatedDate", new Class<?>[]{}));
        assertNotNull(invoke(habit, "toString", new Class<?>[]{}));

        setField(habit, "createdDate", null);
        assertEquals(0.0, (Double) invoke(habit, "getCompletionRate", new Class<?>[]{}), 0.0001);
        setField(habit, "createdDate", LocalDate.now().plusDays(1));
        assertEquals(0.0, (Double) invoke(habit, "getCompletionRate", new Class<?>[]{}), 0.0001);
        setField(habit, "createdDate", LocalDate.now().minusDays(2));
        @SuppressWarnings("unchecked")
        List<LocalDate> updatedDates = (List<LocalDate>) getField(habit, "completionDates");
        updatedDates.clear();
        updatedDates.add(LocalDate.now());
        double rate = (Double) invoke(habit, "getCompletionRate", new Class<?>[]{});
        assertEquals(100.0 / (ChronoUnit.DAYS.between(LocalDate.now().minusDays(2), LocalDate.now()) + 1), rate, 0.001);

        Class<?> reminderClass = cls("decorator.ReminderDecorator");
        Object reminder = construct(reminderClass, new Class<?>[]{cls("model.Habit"), String.class}, habit, "06:30");
        Class<?> priorityClass = cls("decorator.PriorityDecorator");
        Class<?> priorityEnum = cls("decorator.PriorityDecorator$Priority");
        Object high = Enum.valueOf((Class<Enum>) priorityEnum, "HIGH");
        Object medium = Enum.valueOf((Class<Enum>) priorityEnum, "MEDIUM");
        Object low = Enum.valueOf((Class<Enum>) priorityEnum, "LOW");
        Object priority = construct(priorityClass, new Class<?>[]{cls("model.Habit"), priorityEnum}, reminder, high);

        assertEquals(habit, invoke(priority, "getWrappedHabit", new Class<?>[]{}));
        assertEquals(reminder, invoke(priority, "getDirectWrapped", new Class<?>[]{}));
        assertEquals("06:30", invoke(reminder, "getReminderTime", new Class<?>[]{}));
        invoke(reminder, "setReminderTime", new Class<?>[]{String.class}, "07:00");
        assertEquals("07:00", invoke(reminder, "getReminderTime", new Class<?>[]{}));

        String message = (String) invoke(reminder, "getMotivationalMessage", new Class<?>[]{});
        assertTrue(message.contains("Reminder set for"));
        assertTrue(((String) invoke(reminder, "toString", new Class<?>[]{})).contains("Reminder"));

        String now = String.format("%02d:%02d", LocalTime.now().getHour(), LocalTime.now().getMinute());
        invoke(reminder, "setReminderTime", new Class<?>[]{String.class}, now);
        invoke(reminder, "unmarkCompleted", new Class<?>[]{});
        assertTrue((Boolean) invoke(reminder, "isReminderDue", new Class<?>[]{}));
        invoke(reminder, "markCompleted", new Class<?>[]{});
        assertFalse((Boolean) invoke(reminder, "isReminderDue", new Class<?>[]{}));

        String doneNote = (String) invoke(reminder, "getReminderNotification", new Class<?>[]{});
        assertTrue(doneNote.contains("already done"));
        invoke(reminder, "unmarkCompleted", new Class<?>[]{});
        String pendingNote = (String) invoke(reminder, "getReminderNotification", new Class<?>[]{});
        assertTrue(pendingNote.contains("REMINDER"));

        invoke(priority, "setPriority", new Class<?>[]{priorityEnum}, medium);
        assertEquals(medium, invoke(priority, "getPriority", new Class<?>[]{}));
        assertTrue(((String) invoke(priority, "getMotivationalMessage", new Class<?>[]{})).contains("MEDIUM"));

        invoke(priority, "setPriority", new Class<?>[]{priorityEnum}, low);
        assertTrue(((String) invoke(priority, "getMotivationalMessage", new Class<?>[]{})).contains("LOW"));

        invoke(priority, "setPriority", new Class<?>[]{priorityEnum}, high);
        assertTrue(((String) invoke(priority, "toString", new Class<?>[]{})).contains("Priority"));
        assertTrue((Boolean) invoke(priority, "needsAttention", new Class<?>[]{}));
        invoke(priority, "markCompleted", new Class<?>[]{});
        assertFalse((Boolean) invoke(priority, "needsAttention", new Class<?>[]{}));

        assertEquals(invoke(habit, "getCurrentStreak", new Class<?>[]{}), invoke(priority, "getCurrentStreak", new Class<?>[]{}));
        assertEquals(invoke(habit, "getBestStreak", new Class<?>[]{}), invoke(priority, "getBestStreak", new Class<?>[]{}));
        assertEquals(invoke(habit, "getCreatedDate", new Class<?>[]{}), invoke(priority, "getCreatedDate", new Class<?>[]{}));
        assertEquals(invoke(habit, "getCompletionDates", new Class<?>[]{}), invoke(priority, "getCompletionDates", new Class<?>[]{}));
        assertEquals(invoke(habit, "getCompletionRate", new Class<?>[]{}), invoke(priority, "getCompletionRate", new Class<?>[]{}));

        invoke(priority, "addObserver", new Class<?>[]{observerInterface}, observerProxy);
        assertEquals(1, invoke(priority, "getObserverCount", new Class<?>[]{}));
        invoke(priority, "removeObserver", new Class<?>[]{observerInterface}, observerProxy);
        assertEquals(0, invoke(priority, "getObserverCount", new Class<?>[]{}));
    }

    @Test
    void consoleViewIsCoveredByInteractiveFlow() {
        invokeStatic(cls("factory.HabitFactory"), "resetIdCounter", new Class<?>[]{});

        String input = String.join("\n",
                "x",
                "2",
                "3",
                "6",
                "7",
                "8",
                "9",
                "1", "unknown", "Bad", "Bad desc",
                "1", "health", "Run", "Jog",
                "2",
                "3", "HABIT-0002",
                "4", "HABIT-0002", "Run2", "Jog2",
                "6", "HABIT-0002", "07:30",
                "7", "HABIT-0002", "wrong",
                "7", "HABIT-0002", "high",
                "8",
                "9",
                "5", "HABIT-0002",
                "5", "HABIT-0002",
                "10"
        ) + "\n";

        java.io.InputStream originalIn = System.in;
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayInputStream fakeIn = new java.io.ByteArrayInputStream(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        java.io.ByteArrayOutputStream outBuffer = new java.io.ByteArrayOutputStream();
        java.io.PrintStream fakeOut = new java.io.PrintStream(outBuffer, true, java.nio.charset.StandardCharsets.UTF_8);

        try {
            System.setIn(fakeIn);
            System.setOut(fakeOut);
            Object controller = construct(cls("controller.HabitController"), new Class<?>[]{});
            Object console = construct(cls("view.ConsoleView"), new Class<?>[]{cls("controller.HabitController")}, controller);
            invoke(console, "start", new Class<?>[]{});
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = outBuffer.toString(java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(output.contains("HABITNOVA") || output.contains("H A B I T N O V A"));
        assertTrue(output.contains("Invalid option"));
        assertTrue(output.contains("No habits yet"));
        assertTrue(output.contains("Unknown habit category") || output.contains("Valid categories"));
        assertTrue(output.contains("Habit created"));
        assertTrue(output.contains("Completed"));
        assertTrue(output.contains("Habit updated"));
        assertTrue(output.contains("Reminder set") || output.contains("Set Reminder"));
        assertTrue(output.contains("Invalid priority") || output.contains("Priority set"));
        assertTrue(output.contains("Progress Dashboard"));
        assertTrue(output.contains("Activity Log"));
        assertTrue(output.contains("Habit deleted") || output.contains("not found"));
        assertTrue(output.contains("Keep building great habits") || output.contains("See you next time"));
    }
    @Test
    void serviceControllerApiWebAndObservers() {
        Class<?> serviceClass = cls("service.HabitService");
        Object service = construct(serviceClass, new Class<?>[]{});

        Object h1 = invoke(service, "addHabit", new Class<?>[]{String.class, String.class, String.class}, "health", "Run", "Jog");
        Object h2 = invoke(service, "addHabit", new Class<?>[]{String.class, String.class, String.class}, "study", "Read", "Book");

        assertEquals(2, invoke(service, "getTotalHabitCount", new Class<?>[]{}));
        assertTrue((Boolean) invoke(service, "completeHabit", new Class<?>[]{String.class}, invoke(h1, "getId", new Class<?>[]{})));
        assertTrue((Boolean) invoke(service, "editHabit", new Class<?>[]{String.class, String.class, String.class}, invoke(h2, "getId", new Class<?>[]{}), "Read2", "Book2"));
        assertFalse((Boolean) invoke(service, "editHabit", new Class<?>[]{String.class, String.class, String.class}, "missing", "x", "y"));
        assertFalse((Boolean) invoke(service, "completeHabit", new Class<?>[]{String.class}, "missing"));
        assertFalse((Boolean) invoke(service, "uncompleteHabit", new Class<?>[]{String.class}, "missing"));

        Object high = Enum.valueOf((Class<Enum>) cls("decorator.PriorityDecorator$Priority"), "HIGH");
        String id1 = (String) invoke(h1, "getId", new Class<?>[]{});
        assertTrue((Boolean) invoke(service, "setPriority", new Class<?>[]{String.class, cls("decorator.PriorityDecorator$Priority")}, id1, high));
        assertTrue((Boolean) invoke(service, "addReminder", new Class<?>[]{String.class, String.class}, id1, "08:00"));
        assertFalse((Boolean) invoke(service, "setPriority", new Class<?>[]{String.class, cls("decorator.PriorityDecorator$Priority")}, "missing", high));
        assertFalse((Boolean) invoke(service, "addReminder", new Class<?>[]{String.class, String.class}, "missing", "08:00"));

        assertEquals(1L, invoke(service, "getCompletedTodayCount", new Class<?>[]{}));
        assertTrue((Double) invoke(service, "getOverallCompletionRate", new Class<?>[]{}) >= 0.0);
        assertEquals(1, ((List<?>) invoke(service, "getHabitsByCategory", new Class<?>[]{String.class}, "study")).size());
        assertTrue(((Optional<?>) invoke(service, "findById", new Class<?>[]{String.class}, id1)).isPresent());
        assertFalse(((Optional<?>) invoke(service, "findById", new Class<?>[]{String.class}, "none")).isPresent());
        assertTrue(((List<?>) invoke(service, "getHighPriorityPending", new Class<?>[]{})).size() >= 0);
        assertTrue((Boolean) invoke(service, "deleteHabit", new Class<?>[]{String.class}, id1));
        assertFalse((Boolean) invoke(service, "deleteHabit", new Class<?>[]{String.class}, "missing"));

        Object emptyService = construct(serviceClass, new Class<?>[]{});
        assertEquals(0.0, (Double) invoke(emptyService, "getOverallCompletionRate", new Class<?>[]{}), 0.0001);

        Class<?> milestoneClass = cls("observer.MilestoneObserver");
        Class<?> streakClass = cls("observer.StreakBreakObserver");
        Class<?> logClass = cls("observer.CompletionLogObserver");
        Object milestone = construct(milestoneClass, new Class<?>[]{});
        Object streak = construct(streakClass, new Class<?>[]{});
        Object log = construct(logClass, new Class<?>[]{});

        Class<?> controllerClass = cls("controller.HabitController");
        Object controller = construct(controllerClass, new Class<?>[]{milestoneClass, streakClass, logClass}, milestone, streak, log);
        Object ch = invoke(controller, "addHabit", new Class<?>[]{String.class, String.class, String.class}, "lifestyle", "Meditate", "Calm");

        Class<?> observerInterface = cls("observer.HabitObserver");
        Object noopObserver = Proxy.newProxyInstance(
                observerInterface.getClassLoader(),
                new Class<?>[]{observerInterface},
                (proxy, method, args) -> {
                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    if ("toString".equals(method.getName())) {
                        return "noopObserver";
                    }
                    return null;
                });
        invoke(controller, "registerObserverForAllHabits", new Class<?>[]{observerInterface}, noopObserver);

        String cid = (String) invoke(ch, "getId", new Class<?>[]{});
        assertTrue((Boolean) invoke(controller, "completeHabit", new Class<?>[]{String.class}, cid));
        assertTrue((Boolean) invoke(controller, "uncompleteHabit", new Class<?>[]{String.class}, cid));
        assertTrue((Boolean) invoke(controller, "editHabit", new Class<?>[]{String.class, String.class, String.class}, cid, "New", "Desc"));
        assertFalse((Boolean) invoke(controller, "editHabit", new Class<?>[]{String.class, String.class, String.class}, "none", "A", "B"));

        assertTrue((Boolean) invoke(controller, "addReminder", new Class<?>[]{String.class, String.class}, cid, "09:00"));
        Object highPriority = Enum.valueOf((Class<Enum>) cls("decorator.PriorityDecorator$Priority"), "HIGH");
        assertTrue((Boolean) invoke(controller, "setPriority", new Class<?>[]{String.class, cls("decorator.PriorityDecorator$Priority")}, cid, highPriority));
        assertFalse((Boolean) invoke(controller, "addReminder", new Class<?>[]{String.class, String.class}, "none", "09:00"));
        assertFalse((Boolean) invoke(controller, "setPriority", new Class<?>[]{String.class, cls("decorator.PriorityDecorator$Priority")}, "none", highPriority));

        assertTrue(((Optional<?>) invoke(controller, "findById", new Class<?>[]{String.class}, cid)).isPresent());
        assertFalse(((Optional<?>) invoke(controller, "findById", new Class<?>[]{String.class}, "none")).isPresent());
        assertEquals(1, ((List<?>) invoke(controller, "getHabitsByCategory", new Class<?>[]{String.class}, "Lifestyle")).size());
        assertEquals(1, invoke(controller, "getTotalHabitCount", new Class<?>[]{}));
        assertTrue((Double) invoke(controller, "getOverallCompletionRate", new Class<?>[]{}) >= 0.0);
        assertNotNull(invoke(controller, "getCompletionLogObserver", new Class<?>[]{}));
        assertNotNull(invoke(controller, "getMilestoneObserver", new Class<?>[]{}));
        assertNotNull(invoke(controller, "getStreakBreakObserver", new Class<?>[]{}));

        assertTrue((Boolean) invoke(controller, "deleteHabit", new Class<?>[]{String.class}, cid));
        assertFalse((Boolean) invoke(controller, "deleteHabit", new Class<?>[]{String.class}, cid));

        Object m = construct(milestoneClass, new Class<?>[]{});
        Object sb = construct(streakClass, new Class<?>[]{});
        Object cl = construct(logClass, new Class<?>[]{});
        Object h = invokeStatic(cls("factory.HabitFactory"), "createHabit", new Class<?>[]{String.class, String.class, String.class}, "health", "Run", "Jog");
        Object completed = Enum.valueOf((Class<Enum>) cls("observer.HabitEvent"), "COMPLETED");
        Object uncompleted = Enum.valueOf((Class<Enum>) cls("observer.HabitEvent"), "UNCOMPLETED");
        Object milestoneReached = Enum.valueOf((Class<Enum>) cls("observer.HabitEvent"), "MILESTONE_REACHED");
        Object streakBroken = Enum.valueOf((Class<Enum>) cls("observer.HabitEvent"), "STREAK_BROKEN");

        invoke(cl, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, milestoneReached);
        invoke(cl, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, completed);
        invoke(cl, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, uncompleted);
        assertEquals(2, invoke(cl, "size", new Class<?>[]{}));
        List<String> copyLog = (List<String>) invoke(cl, "getLog", new Class<?>[]{});
        copyLog.clear();
        assertEquals(2, invoke(cl, "size", new Class<?>[]{}));
        assertEquals(1, ((List<?>) invoke(cl, "getRecent", new Class<?>[]{int.class}, 1)).size());
        invoke(cl, "clear", new Class<?>[]{});
        assertEquals(0, invoke(cl, "size", new Class<?>[]{}));

        invoke(m, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, completed);
        setField(h, "currentStreak", 7);
        invoke(m, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, milestoneReached);
        invoke(m, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, milestoneReached);
        List<String> milestones = (List<String>) invoke(m, "getCelebratedMilestones", new Class<?>[]{});
        assertEquals(1, milestones.size());
        milestones.clear();
        assertEquals(1, ((List<?>) invoke(m, "getCelebratedMilestones", new Class<?>[]{})).size());

        invoke(sb, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, completed);
        invoke(sb, "update", new Class<?>[]{cls("model.Habit"), cls("observer.HabitEvent")}, h, streakBroken);
        assertEquals(1, invoke(sb, "getBreakCount", new Class<?>[]{String.class}, invoke(h, "getId", new Class<?>[]{})));
        assertEquals(0, invoke(sb, "getBreakCount", new Class<?>[]{String.class}, "none"));

        assertEquals(4, ((Object[]) invokeStatic(cls("observer.HabitEvent"), "values", new Class<?>[]{})).length);
        assertEquals("COMPLETED", invokeStatic(cls("observer.HabitEvent"), "valueOf", new Class<?>[]{String.class}, "COMPLETED").toString());

        Object apiService = construct(serviceClass, new Class<?>[]{});
        Object apiController = construct(cls("controller.HabitApiController"), new Class<?>[]{serviceClass}, apiService);
        Object request = construct(cls("dto.HabitRequest"), new Class<?>[]{});
        invoke(request, "setCategory", new Class<?>[]{String.class}, "health");
        invoke(request, "setName", new Class<?>[]{String.class}, "Walk");
        invoke(request, "setDescription", new Class<?>[]{String.class}, "Morning");

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> createResp = (ResponseEntity<Map<String, Object>>) invoke(apiController, "create", new Class<?>[]{cls("dto.HabitRequest")}, request);
        assertEquals(HttpStatus.OK, createResp.getStatusCode());
        String createdId = (String) createResp.getBody().get("id");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> all = (List<Map<String, Object>>) invoke(apiController, "getAll", new Class<?>[]{});
        assertEquals(1, all.size());

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> completeOk = (ResponseEntity<Map<String, Object>>) invoke(apiController, "complete", new Class<?>[]{String.class}, createdId);
        assertEquals(HttpStatus.OK, completeOk.getStatusCode());
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> completeMissing = (ResponseEntity<Map<String, Object>>) invoke(apiController, "complete", new Class<?>[]{String.class}, "missing");
        assertEquals(HttpStatus.NOT_FOUND, completeMissing.getStatusCode());

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> uncompleteOk = (ResponseEntity<Map<String, Object>>) invoke(apiController, "uncomplete", new Class<?>[]{String.class}, createdId);
        assertEquals(HttpStatus.OK, uncompleteOk.getStatusCode());
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> uncompleteMissing = (ResponseEntity<Map<String, Object>>) invoke(apiController, "uncomplete", new Class<?>[]{String.class}, "missing");
        assertEquals(HttpStatus.NOT_FOUND, uncompleteMissing.getStatusCode());

        @SuppressWarnings("unchecked")
        ResponseEntity<Void> remOk = (ResponseEntity<Void>) invoke(apiController, "setReminder", new Class<?>[]{String.class, String.class}, createdId, "06:00");
        assertEquals(HttpStatus.OK, remOk.getStatusCode());
        @SuppressWarnings("unchecked")
        ResponseEntity<Void> remMissing = (ResponseEntity<Void>) invoke(apiController, "setReminder", new Class<?>[]{String.class, String.class}, "missing", "06:00");
        assertEquals(HttpStatus.NOT_FOUND, remMissing.getStatusCode());

        @SuppressWarnings("unchecked")
        ResponseEntity<Void> priOk = (ResponseEntity<Void>) invoke(apiController, "setPriority", new Class<?>[]{String.class, String.class}, createdId, "high");
        assertEquals(HttpStatus.OK, priOk.getStatusCode());
        assertThrows(IllegalArgumentException.class, () -> invoke(apiController, "setPriority", new Class<?>[]{String.class, String.class}, createdId, "bad_level"));

        List<String> logEntries = (List<String>) invoke(apiController, "getLog", new Class<?>[]{});
        assertFalse(logEntries.isEmpty());
        Map<String, Object> stats = (Map<String, Object>) invoke(apiController, "getStats", new Class<?>[]{});
        assertTrue(stats.containsKey("totalHabits"));
        assertTrue(stats.containsKey("completedToday"));
        assertTrue(stats.containsKey("overallCompletionRate"));
        assertTrue(stats.containsKey("highPriorityPending"));

        @SuppressWarnings("unchecked")
        ResponseEntity<Void> delMissing = (ResponseEntity<Void>) invoke(apiController, "delete", new Class<?>[]{String.class}, "missing");
        assertEquals(HttpStatus.NOT_FOUND, delMissing.getStatusCode());
        @SuppressWarnings("unchecked")
        ResponseEntity<Void> delOk = (ResponseEntity<Void>) invoke(apiController, "delete", new Class<?>[]{String.class}, createdId);
        assertEquals(HttpStatus.OK, delOk.getStatusCode());

        Object webService = construct(serviceClass, new Class<?>[]{});
        invoke(webService, "addHabit", new Class<?>[]{String.class, String.class, String.class}, "study", "Read", "Book");
        Object webController = construct(cls("controller.HabitWebController"), new Class<?>[]{serviceClass}, webService);
        ExtendedModelMap model = new ExtendedModelMap();
        String view = (String) invoke(webController, "dashboard", new Class<?>[]{org.springframework.ui.Model.class}, model);
        assertEquals("dashboard", view);
        assertNotNull(model.get("habits"));
        assertNotNull(model.get("totalHabits"));
        assertNotNull(model.get("completedToday"));
        assertNotNull(model.get("completionRate"));
        assertNotNull(model.get("highPriorityCount"));
        assertNotNull(model.get("log"));
    }

    private static Class<?> cls(String simpleName) {
        try {
            return Class.forName(BASE + simpleName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object construct(Class<?> type, Class<?>[] parameterTypes, Object... args) {
        try {
            Constructor<?> c = type.getDeclaredConstructor(parameterTypes);
            c.setAccessible(true);
            return c.newInstance(args);
        } catch (InvocationTargetException e) {
            throw rethrow(e.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object invoke(Object target, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method m = target.getClass().getMethod(methodName, parameterTypes);
            m.setAccessible(true);
            return m.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw rethrow(e.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object invokeStatic(Class<?> type, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method m = type.getMethod(methodName, parameterTypes);
            m.setAccessible(true);
            return m.invoke(null, args);
        } catch (InvocationTargetException e) {
            throw rethrow(e.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getField(Object target, String fieldName) {
        try {
            Field f = findField(target.getClass(), fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static RuntimeException rethrow(Throwable cause) {
        if (cause instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        if (cause instanceof Error error) {
            throw error;
        }
        return new RuntimeException(cause);
    }

    @Test
    void main() {
        HabitNovaApp.main(new String[] {});
    }
}