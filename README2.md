# Community Food Donation and Request System — Setup Guide

Data is stored locally in CSV files under a `data/` folder — no
database, no internet connection, no signup needed. Everyone on the
team follows these same steps so the project runs identically on every
laptop.

## 1. Everyone: one-time VS Code setup

1. Install VS Code, then install the **Extension Pack for Java**
   (by Microsoft) from the Extensions panel — it includes Maven
   support, which this project uses to keep the build consistent
   across everyone's machine, even though there are no external
   library downloads needed.
2. Install a JDK if you don't have one (Java 17 or newer). VS Code
   will prompt you to download one if it can't find one.

## 2. Get the code running

```bash
git clone <your-repo-url>
cd <project-folder>
```

Open that folder directly in VS Code (the one containing `pom.xml`,
not a parent folder wrapping it). Wait for the bottom status bar to
say "Java: Ready" and for a **Maven Dependencies** entry to appear in
the Explorer sidebar — that confirms VS Code has recognized the
project correctly.

Right-click `Main.java` (under `src/main/java/com/foodsystem`) and
choose **Run**.

## 3. Where your data goes

The first time you register a user, donate food, or submit a request,
the app automatically creates a `data/` folder next to the project
with three files:

```
data/
├── users.csv
├── donations.csv
└── requests.csv
```

These are plain text files — open them in VS Code any time to see
exactly what's been saved, which is handy for debugging.

**Important for your final submission:** decide as a team whether
`data/` should be committed to GitHub (so your teacher sees sample
data immediately) or left out (a fresh, empty system on first run).
Either is fine — just agree on it together, and mention your choice
in the report.

## Project structure

```
src/main/java/com/foodsystem/
├── model/       User, Donation, Request, Status, Urgency — plain data classes
├── exception/   Custom exceptions for validation and lookup failures
├── util/        Validator — shared input-checking methods
├── storage/     UserFileManager, DonationFileManager, RequestFileManager
│                — all CSV file reading/writing lives here, nowhere else
└── view/        LoginFrame, RegisterFrame, DashboardFrame, and each
                 module's screen — GUI code only, never touches a file directly
```
