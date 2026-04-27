package service;

import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class DeadlineMonitor extends Thread {

    // Stores task IDs whose reminder is already shown
    private Set<Integer> remindedTaskIds = new HashSet<>();

    @Override
    public void run() {

        while (true) {

            TaskDAO dao = new TaskDAO();
            List<Task> tasks = dao.getAllTasks();

            for (Task t : tasks) {

                if (t.getDeadline() != null
                        && !t.getStatus().equalsIgnoreCase("Completed")) {

                    long minutesLeft = Duration.between(
                            LocalDateTime.now(),
                            t.getDeadline()
                    ).toMinutes();

                    if (minutesLeft <= 10
                            && minutesLeft >= 0
                            && !remindedTaskIds.contains(t.getId())) {

                        JOptionPane.showMessageDialog(
                                null,
                                "Reminder: Task '" + t.getTitle()
                                        + "' deadline is near!",
                                "Deadline Alert",
                                JOptionPane.WARNING_MESSAGE
                        );

                        remindedTaskIds.add(t.getId());
                    }
                }
            }

            try {
                Thread.sleep(10000); // checks every 10 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}