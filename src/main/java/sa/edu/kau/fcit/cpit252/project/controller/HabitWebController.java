package sa.edu.kau.fcit.cpit252.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sa.edu.kau.fcit.cpit252.project.service.HabitService;

@Controller
public class HabitWebController {

    private final HabitService habitService;

    public HabitWebController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("habits", habitService.getAllHabits());
        model.addAttribute("totalHabits", habitService.getTotalHabitCount());
        model.addAttribute("completedToday", habitService.getCompletedTodayCount());
        model.addAttribute("completionRate", Math.round(habitService.getOverallCompletionRate() * 10.0) / 10.0);
        model.addAttribute("highPriorityCount", habitService.getHighPriorityPending().size());
        model.addAttribute("log", habitService.getCompletionLogObserver().getRecent(20));
        return "dashboard";
    }
}
