# HabitNova: Smart Habit Tracker


## Description
A console-based application designed to help students build and maintain consistent daily habits.
Most existing trackers are either too simple (plain checklists) or too complex (full productivity suites). HabitNova
sits in the middle — a focused, intelligent habit tracker that lets users define personal habits across categories,
track daily completion, monitor streaks and progress, and receive motivational feedback that encourages consistency.

## Features

### ✅ Implemented Features (Completed)
* **Project & Repository Setup:** Core project structure initialized with Maven build configuration, package layout (`model`, `view`, `controller`, `factory`, `decorator`), and GitHub repository.
* **Core Architecture (MVC):** Clean separation of concerns with `Habit` model classes, `HabitController` orchestration layer, and `ConsoleView` for user interaction.
* **Factory Method Design Pattern (Creational):** `HabitFactory` centralizes the creation of `HealthHabit`, `StudyHabit`, and `LifestyleHabit` subclasses, decoupling client code from concrete classes and supporting easy extension to new categories.
* **Decorator Design Pattern (Structural):** `ReminderDecorator` and `PriorityDecorator` dynamically add reminder times and priority levels to any habit at runtime — stackable on a single habit without subclass explosion.
* **Streak Engine:** Tracks current streak and all-time best streak per habit, with proper recalculation when completions are undone.
* **Interactive Console UI:** ANSI-colored menu system with progress dashboard, visual progress bars, high-priority alerts, and context-aware motivational messages.
* **Comprehensive Test Suite:** 47 unit tests covering Factory Method, Decorator behavior, stacked decorators, and core controller operations — all passing.

### ⏳ Planned Features (Backlog)
* **Behavioral Pattern (Observer):** Implement Observer pattern for streak alerts and milestone notifications (Stage 3).
* **Data Persistence:** Save habits and decorator state to a local JSON file so habits survive between application runs.
* **Habit History View:** Display the last 7 days per habit as a completion grid to help users identify patterns.
* **Weekly Progress Export (CSV):** Export habit performance summaries for external tracking or sharing with accountability partners.
* **Additional Habit Categories:** Extend beyond Health/Study/Lifestyle to include Finance, Social, and Creative categories.
* **Strategy Pattern for Streak Rules:** Allow users to choose strict, flexible, or weekday-only streak calculation modes.
* **Graphical Frontend:** Replace the console view with a JavaFX or web-based UI/UX layer.

### 🛑 Deferred Features (Won't Implement / Future)
* **Cloud Sync & Multi-Device Support:** Cross-device synchronization has been deferred. The current scope focuses on a single-user local application; cloud features fall outside the design-patterns curriculum and will be revisited in a future release.
* **Push Notifications:** Real-time OS-level reminder notifications are deferred. Reminders are currently displayed in-app only, since native notification systems are platform-specific and outside the scope of this stage.

## 🛠️ Prerequisites
To run this project, you must have the following installed:
* **Java 17 or 21** (JDK)
* **Apache Maven**

## Usage

To build and run the app, use:

- **Option 1: Run directly via Maven**
  - Open the terminal and run the following command:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.habitnova.HabitNovaApp"
```

- **Option 2: Build and run the executable JAR**
  - Open the terminal and run the following command:
```bash
mvn clean package
```

```bash
cd target
java -jar course-project-1.0-SNAPSHOT.jar
```

- **Option 3: Run the test suite**
```bash
mvn test
```

## Design Patterns

| Stage | Type | Pattern | Status |
|:------|:-----|:--------|:------:|
| Stage 1 | Creational | Factory Method | ✅ |
| Stage 2 | Structural | Decorator | ✅ |
| Stage 3 | Behavioral | Observer | 🔜 |

## Screenshots
![HabitNova Main Menu](assets/ui-screenshot.png)
![HabitNova Progress Dashboard](assets/ui-screenshot2.png)

## Team
| Name | Student ID |
|:-----|:----------:|
| Fahad Alshehri | 2338900 |
| Alwaleed Alrefaei | 2345441 |

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
