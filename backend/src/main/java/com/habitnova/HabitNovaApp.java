package com.habitnova;

import com.habitnova.controller.HabitController;
import com.habitnova.view.ConsoleView;

public class HabitNovaApp {

    public static void main(String[] args) {
        HabitController controller = new HabitController();
        ConsoleView view = new ConsoleView(controller);
        view.start();
    }
}
