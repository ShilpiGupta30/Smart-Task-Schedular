package service;

import model.Task;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class TaskScheduler {

    PriorityQueue<Task> queue =
            new PriorityQueue<>(
                    (t1, t2) -> {

                        int deadlineCompare = t1.getDeadline().compareTo(t2.getDeadline());
                        if (deadlineCompare == 0) {

                            return Integer.compare( t1.getPriority(), t2.getPriority());
                        }
                        return deadlineCompare;
                    }
            );

    public void addTask(Task task) {
        queue.add(task);
    }

    public Task getNextTask() {
        return queue.peek();
    }

    // Needed for Deadline Monitor
    public List<Task> getAllTasks() {
        return new ArrayList<>(queue);
    }
    
    public int calculatePriority(LocalDateTime deadline) {

        long hoursLeft =
                java.time.Duration.between(
                        LocalDateTime.now(),
                        deadline
                ).toHours();

        if (hoursLeft <= 1) {
            return 1; // High Priority
        }

        else if (hoursLeft <= 5) {
            return 2; // Medium Priority
        }

        else {
            return 3; // Low Priority
        }
    }
}