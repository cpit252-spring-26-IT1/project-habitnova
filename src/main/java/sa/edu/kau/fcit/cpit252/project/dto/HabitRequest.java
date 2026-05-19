package sa.edu.kau.fcit.cpit252.project.dto;

import jakarta.validation.constraints.NotBlank;

public class HabitRequest {
    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    public HabitRequest() {}
    public HabitRequest(String category, String name, String description) {
        this.category = category; this.name = name; this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
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
}
