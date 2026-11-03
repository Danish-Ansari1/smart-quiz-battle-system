# Smart Quiz Battle System

The **Smart Quiz Battle System** is a Java-based desktop application designed to make learning engaging through timed multiple-choice questions, automatic score calculation, result reviews, and a persistent database-backed leaderboard[span_0](start_span)[span_0](end_span)[span_1](start_span)[span_1](end_span)[span_2](start_span)[span_2](end_span).

---

## 🚀 Features

* **Player Setup:** Enter a player name and choose from multiple categories before starting the quiz[span_3](start_span)[span_3](end_span).
* **Multiple-Choice Quiz:** Displays one question at a time with four distinct answer options[span_4](start_span)[span_4](end_span).
* **Countdown Timer:** A built-in timer counts down for each question; automatically moves forward when time expires[span_5](start_span)[span_5](end_span).
* **Automatic Scoring:** Instantly checks answers, awards points, and calculates final percentages[span_6](start_span)[span_6](end_span).
* **Result Review & Persistence:** Shows total questions, correct/incorrect answers, performance metrics, and saves history into MySQL[span_7](start_span)[span_7](end_span).
* **Leaderboard:** Ranks top players in descending order based on their scores[span_8](start_span)[span_8](end_span).

---

## 🛠️ Technology Stack

* **Language:** Java[span_9](start_span)[span_9](end_span)
* **GUI Framework:** Java Swing (Windows, Labels, Buttons, Tables)[span_10](start_span)[span_10](end_span)
* **Database:** MySQL[span_11](start_span)[span_11](end_span)
* **Connectivity:** JDBC (Java Database Connectivity)[span_12](start_span)[span_12](end_span)
* **Development Environment:** VS Code / IntelliJ IDEA / Eclipse (`JDK 8+`)[span_13](start_span)[span_13](end_span)

---

## 📁 Project Directory Structure

```text
SmartQuizBattleSystem/
├── lib/
│   └── mysql-connector-j-x.x.x.jar   <-- MySQL JDBC Driver
├── src/
│   └── com/
│       └── quiz/
│           ├── db/
│           │   └── DatabaseConnection.java
│           ├── model/
│           │   ├── Player.java
│           │   ├── Question.java
│           │   └── QuizResult.java
│           ├── dao/
│           │   ├── QuestionDAO.java
│           │   └── QuizResultDAO.java
│           ├── ui/
│           │   ├── WelcomeFrame.java
│           │   ├── QuizFrame.java
│           │   ├── ResultFrame.java
│           │   └── LeaderboardFrame.java
│           └── Main.java
└── README.md