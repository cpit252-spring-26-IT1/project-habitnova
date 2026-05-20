package sa.edu.kau.fcit.cpit252.project.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
public class HabitEntity {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false)
    private LocalDate createdDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "completion_dates", joinColumns = @JoinColumn(name = "habit_id"))
    @Column(name = "completed_on")
    private List<LocalDate> completionDates = new ArrayList<>();

    private int bestStreak;

    public HabitEntity() {}

    public HabitEntity(String id, String name, String description,
                       String category, LocalDate createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdDate = createdDate;
        this.bestStreak = 0;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<LocalDate> getCompletionDates() {
        return completionDates;
    }
    public void setCompletionDates(List<LocalDate> completionDates) {
        this.completionDates = completionDates;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }
}
