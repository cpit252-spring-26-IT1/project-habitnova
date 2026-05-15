package sa.edu.kau.fcit.cpit252.project.observer;

import sa.edu.kau.fcit.cpit252.project.model.Habit;

public interface HabitObserver {


    void update(Habit habit, HabitEvent event);
}
