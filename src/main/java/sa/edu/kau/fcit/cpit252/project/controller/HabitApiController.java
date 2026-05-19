package sa.edu.kau.fcit.cpit252.project.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sa.edu.kau.fcit.cpit252.project.decorator.PriorityDecorator;
import sa.edu.kau.fcit.cpit252.project.dto.HabitRequest;
import sa.edu.kau.fcit.cpit252.project.model.Habit;
import sa.edu.kau.fcit.cpit252.project.service.HabitService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
public class HabitApiController {

    private final HabitService habitService;

    public HabitApiController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return habitService.getAllHabits().stream().map(this::toMap).toList();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody HabitRequest req) {
        Habit habit = habitService.addHabit(req.getCategory(), req.getName(), req.getDescription());
        return ResponseEntity.ok(toMap(habit));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> complete(@PathVariable String id) {
        if (habitService.completeHabit(id))
            return ResponseEntity.ok(toMap(habitService.findById(id).get()));
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/uncomplete")
    public ResponseEntity<Map<String, Object>> uncomplete(@PathVariable String id) {
        if (habitService.uncompleteHabit(id))
            return ResponseEntity.ok(toMap(habitService.findById(id).get()));
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return habitService.deleteHabit(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/reminder")
    public ResponseEntity<Void> setReminder(@PathVariable String id, @RequestParam String time) {
        return habitService.addReminder(id, time) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/priority")
    public ResponseEntity<Void> setPriority(@PathVariable String id, @RequestParam String level) {
        PriorityDecorator.Priority p = PriorityDecorator.Priority.valueOf(level.toUpperCase());
        return habitService.setPriority(id, p) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/log")
    public List<String> getLog() {
        return habitService.getCompletionLogObserver().getLog();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("totalHabits", habitService.getTotalHabitCount());
        s.put("completedToday", habitService.getCompletedTodayCount());
        s.put("overallCompletionRate", Math.round(habitService.getOverallCompletionRate() * 10.0) / 10.0);
        s.put("highPriorityPending", habitService.getHighPriorityPending().size());
        return s;
    }

    private Map<String, Object> toMap(Habit h) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", h.getId());
        m.put("name", h.getName());
        m.put("description", h.getDescription());
        m.put("category", h.getCategory());
        m.put("completedToday", h.isCompletedForToday());
        m.put("currentStreak", h.getCurrentStreak());
        m.put("bestStreak", h.getBestStreak());
        m.put("completionRate", Math.round(h.getCompletionRate() * 10.0) / 10.0);
        m.put("motivationalMessage", h.getMotivationalMessage());
        m.put("type", h.getClass().getSimpleName());
        return m;
    }
}
