package sa.edu.kau.fcit.cpit252.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for HabitNova's REST API and web controller.
 * Uses Spring Boot MockMvc — no real server needed.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HabitApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / returns the Thymeleaf dashboard")
    void dashboardLoads() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @Test
    @DisplayName("GET /api/habits returns JSON array")
    void listHabits() throws Exception {
        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/habits creates a HealthHabit via Factory Method")
    void createHealthHabit() throws Exception {
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category\":\"health\",\"name\":\"Run\",\"description\":\"Morning jog\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Run"))
                .andExpect(jsonPath("$.category").value("Health"));
    }

    @Test
    @DisplayName("POST /api/habits creates a StudyHabit via Factory Method")
    void createStudyHabit() throws Exception {
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category\":\"study\",\"name\":\"Algorithms\",\"description\":\"LeetCode daily\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Study"));
    }

    @Test
    @DisplayName("POST /api/habits rejects blank fields (Bean Validation)")
    void rejectsBlankFields() throws Exception {
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category\":\"\",\"name\":\"\",\"description\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/habits/stats returns dashboard statistics")
    void getStats() throws Exception {
        mockMvc.perform(get("/api/habits/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHabits").isNumber())
                .andExpect(jsonPath("$.completedToday").isNumber());
    }

    @Test
    @DisplayName("GET /api/habits/log returns activity log (Observer)")
    void getActivityLog() throws Exception {
        mockMvc.perform(get("/api/habits/log"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("DELETE /api/habits/{id} returns 404 for unknown id")
    void deleteNonExistent() throws Exception {
        mockMvc.perform(delete("/api/habits/DOES-NOT-EXIST"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/habits/{id}/complete returns 404 for unknown id")
    void completeNonExistent() throws Exception {
        mockMvc.perform(post("/api/habits/FAKE/complete"))
                .andExpect(status().isNotFound());
    }
}
