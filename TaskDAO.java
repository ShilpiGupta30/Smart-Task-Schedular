package service;

import java.sql.*;
import java.util.*;

import database.DBConnection;
import model.Task;

public class TaskDAO {

    // INSERT TASK
    public void addTask(Task task) {

        try {
            Connection con =
                    DBConnection.getConnection();

            String query =
                    "INSERT INTO tasks (title, priority, deadline, duration, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setString(1, task.getTitle());
            ps.setInt(2, task.getPriority());

            // Store deadline properly
            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            task.getDeadline()
                    )
            );

            ps.setInt(4, task.getDuration());
            ps.setString(5, task.getStatus());
            ps.setInt(6, task.getUserId());

            ps.executeUpdate();

            System.out.println(
                    "Task stored in database!"
            );

        }

        catch (Exception e) {

            e.printStackTrace();

        }
    }

    // GET ALL TASKS FROM DATABASE
    public List<Task> getAllTasks() {

        List<Task> tasks =
                new ArrayList<>();

        try {

            Connection con =
                    DBConnection.getConnection();

            String query =
                    "SELECT * FROM tasks ORDER BY deadline ASC, id ASC";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Task t =
                        new Task();

                t.setId(
                        rs.getInt("id")
                );

                t.setTitle(
                        rs.getString("title")
                );

                t.setPriority(
                        rs.getInt("priority")
                );

                t.setDeadline(
                        rs.getTimestamp("deadline")
                                .toLocalDateTime()
                );

                t.setDuration(
                        rs.getInt("duration")
                );

                t.setStatus(
                        rs.getString("status")
                );

                t.setUserId(
                        rs.getInt("user_id")
                );

                tasks.add(t);
            }

        }

        catch (Exception e) {

            e.printStackTrace();

        }

        return tasks;
    }

    // UPDATE TASK STATUS
    public void updateTaskStatus(
            int taskId,
            String status
    ) {

        try {

            Connection con =
                    DBConnection.getConnection();

            String query =
                    "UPDATE tasks SET status=? WHERE id=?";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setString(1, status);
            ps.setInt(2, taskId);

            ps.executeUpdate();

        }

        catch (Exception e) {

            e.printStackTrace();

        }
    }
    // DELETE TASK
    public void deleteTask(int id) {

        try {

            Connection con =
                    DBConnection.getConnection();

            String query =
                    "DELETE FROM tasks WHERE id=?";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setInt(1, id);

            ps.executeUpdate();

            System.out.println("Task Deleted!");

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }
}
