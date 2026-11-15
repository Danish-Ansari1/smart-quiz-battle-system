# 🚀 Smart Quiz Battle System 

The **Smart Quiz Battle System** is a modern, production-level Java desktop application designed for interactive assessments, real-time quizzes, and comprehensive student performance tracking. It features an enterprise-grade UI built entirely with Java Swing and a robust MySQL backend.

---

## ✨ Key Features

### 1. Enterprise Welcome Portal (`WelcomeFrame`)
* **Interactive Landing Page:** A clean SaaS-style entry portal welcoming users with system highlights and system metrics before entering the authentication gate.
* **Seamless Navigation:** Smooth transition into the portal with hover effects and modern button animations.

### 2. Secure Role-Based Authentication (`Login/Signup`)
* **Dual Roles:** Secure registration and login supporting both **Students** and **Teacher Admins**.
* **Input Validation & Security:** Built-in validation, password masking with a "Show Password" toggle, and strict error handling for unauthorized attempts.

### 3. Fullscreen Interactive Quiz Engine (`QuizEngineFrame`)
* **Immersive Fullscreen Mode:** Opens automatically in maximized mode with custom responsive option cards.
* **Live Countdown Timer:** Real-time visual countdown for each question that triggers color alerts when time is running low.
* **Dynamic Progress Tracking:** Clear progress bars and question counters (`Question X of Y`).
* **Instant Performance Report:** Popup summary showing final scores, percentages, correct/wrong counts, and a scrollable question-by-question review sheet.

### 4. Teacher & Admin Oversight Dashboard (`AdminFrame`)
* **Executive KPI Cards:** Live metric cards displaying Total Attempts, Top Performer, and Class Average Accuracy.
* **Live Leaderboard:** Ranks students automatically based on scores and percentages with Gold, Silver, and Bronze highlighting.
* **Student Answer Sheet Audit:** Dedicated audit logs showing exact student selections, correct answers, and status (`CORRECT`, `WRONG`, `SKIPPED`) with color-coded badges.
* **Drilldown Inspection:** Double-click any student row to instantly inspect their complete individual answer sheet breakdown.
* **Question Bank Manager:** Built-in form allowing teachers to add new questions dynamically to any category (Java Programming, Computer Science, etc.).

---

## 🛠️ Technology Stack

* **Language:** Java (`JDK 8+`)
* **GUI Framework:** Java Swing (Custom Card Layouts, GridBagLayout, Styled Components)
* **Database:** MySQL
* **Connectivity:** JDBC (Java Database Connectivity)
* **Development Environment:** IntelliJ IDEA / Eclipse / VS Code

---

## 📁 Project Directory Structure

```text
SmartQuizBattleSystem/
├── lib/
│   └── mysql-connector-j-x.x.x.jar   <-- MySQL JDBC Driver
├── src/
│   ├── Main.java                     <-- Application Entry Point (Welcome Portal)
│   ├── Player.java                   <-- Player Session Model
│   ├── Question.java                 <-- Question Entity Model
│   ├── DBConnection.java             <-- MySQL Database Connection Handler
│   ├── DatabaseDAO.java              <-- Database Data Access Object (Queries & Logs)
│   ├── WelcomeFrame.java             <-- Enterprise Landing Page
│   ├── LoginSignUpFrame.java         <-- Secure Authentication UI
│   ├── DashboardFrame.java           <-- Student Category & Dashboard Portal
│   ├── QuizEngineFrame.java          <-- Fullscreen Quiz Runner & Review Sheet
│   └── AdminFrame.java               <-- Teacher Control Center & Audit Panel
└── README.me
* 👥 Project Contributors
* Amit Kumar
* Adnan Ahsan
* Ashish Kumar
* Danish Ansari
