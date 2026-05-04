package sa.edu.kau.fcit.cpit252.project;

import sa.edu.kau.fcit.cpit252.project.controller.HabitController;
import sa.edu.kau.fcit.cpit252.project.view.ConsoleView;

public class HabitNovaApp {
    public static void main(String[] args) {
        HabitController controller = new HabitController();
        ConsoleView view = new ConsoleView(controller);
        view.start();
    }
}