# Smart Task Scheduler

Smart Task Scheduler is a Java desktop application for creating, scheduling, prioritizing, tracking, and deleting tasks. The project uses Java Swing for the graphical interface, MySQL for persistent storage, and a priority-based scheduler to decide which task should be handled next.

## Features

- Add tasks with title, duration, deadline, status, and user ID
- Automatically calculate task priority based on deadline urgency
- View all tasks sorted by deadline
- Mark selected tasks as completed
- Delete selected tasks
- Show task summary counts for total, pending, and completed tasks
- Display reminder popups when a deadline is within 10 minutes
- Store task and user data in a MySQL database
- Includes both a console-based entry point and a Swing GUI

## Tech Stack

- Java 21
- Java Swing
- JDBC
- MySQL
- MySQL Connector/J 9.6.0
- Eclipse IDE project structure

## Project Structure

```text
SmartTaskScheduler/
|-- src/
|   |-- database/
|   |   `-- DBConnection.java
|   |-- main/
|   |   `-- Main.java
|   |-- model/
|   |   |-- Task.java
|   |   `-- User.java
|   |-- service/
|   |   |-- DeadlineMonitor.java
|   |   |-- TaskDAO.java
|   |   |-- TaskScheduler.java
|   |   `-- UserDAO.java
|   `-- ui/
|       `-- TaskSchedulerGUI.java
|-- bin/
|-- .classpath
|-- .project
|-- .gitignore
`-- README.md
```

## Main Components

| Component | Description |
| --- | --- |
| `TaskSchedulerGUI` | Main Swing-based desktop interface |
| `Main` | Console-based task scheduler interface |
| `TaskScheduler` | Handles in-memory task priority ordering |
| `TaskDAO` | Performs task database operations |
| `UserDAO` | Handles user login and registration database operations |
| `DeadlineMonitor` | Runs in the background and shows deadline reminder popups |
| `DBConnection` | Creates the MySQL database connection |
| `Task` | Task model class |
| `User` | User model class |

## Database Setup

Create a MySQL database named `task_scheduler`.

```sql
CREATE DATABASE task_scheduler;
USE task_scheduler;
```

Create the `users` table:

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL
);
```

Create the `tasks` table:

```sql
CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    priority INT NOT NULL,
    deadline DATETIME NOT NULL,
    duration INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

Insert a default user because new tasks currently use `user_id = 1`:

```sql
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'admin');
```

## Configuration

Update the database credentials in:

```text
src/database/DBConnection.java
```

The project currently connects to:

```text
jdbc:mysql://localhost:3306/task_scheduler
```

Make sure the MySQL username, password, and database name match your local MySQL setup.

## How to Run

### Run in Eclipse

1. Open Eclipse.
2. Select `File > Import > Existing Projects into Workspace`.
3. Choose the `SmartTaskScheduler` folder.
4. Make sure Java 21 is configured.
5. Add the MySQL Connector/J `.jar` file to the build path if Eclipse cannot find it.
6. Run one of these classes:
   - `ui.TaskSchedulerGUI` for the desktop GUI
   - `main.Main` for the console version

### Run from Command Line

Compile the project with the MySQL Connector/J jar in the classpath:

```bash
javac -cp "path/to/mysql-connector-j-9.6.0.jar" -d bin src/database/*.java src/model/*.java src/service/*.java src/ui/*.java src/main/*.java
```

Run the GUI version:

```bash
java -cp "bin;path/to/mysql-connector-j-9.6.0.jar" ui.TaskSchedulerGUI
```

Run the console version:

```bash
java -cp "bin;path/to/mysql-connector-j-9.6.0.jar" main.Main
```

On macOS or Linux, replace the semicolon `;` in the classpath with a colon `:`.

## Priority Logic

Task priority is calculated from the time remaining before the deadline:

| Time Left | Priority |
| --- | --- |
| 1 hour or less | 1, High |
| 5 hours or less | 2, Medium |
| More than 5 hours | 3, Low |

Tasks are ordered by earliest deadline first. If two tasks have the same deadline, the task with the lower priority number comes first.

## Reminder Logic

`DeadlineMonitor` checks the database every 10 seconds. If a task is not completed and its deadline is within the next 10 minutes, the application shows a Swing popup reminder.

## Usage

1. Start MySQL and make sure the `task_scheduler` database exists.
2. Run `ui.TaskSchedulerGUI`.
3. Click `Add New Task`.
4. Enter task title, duration, deadline, and status.
5. Use `Refresh Tasks` to reload the table.
6. Select a task row to mark it completed or delete it.

Deadline format:

```text
yyyy-MM-dd HH:mm
```

Example:

```text
2026-04-27 18:30
```

## Notes

- The `bin/` folder contains compiled `.class` files and is ignored by Git.
- The project is currently configured as an Eclipse Java project.
- The GUI stores tasks directly in MySQL through `TaskDAO`.
- The console version keeps tasks in memory through `TaskScheduler` and also inserts new tasks into the database.
- New tasks currently use `user_id = 1`, so keep a default user in the `users` table.

