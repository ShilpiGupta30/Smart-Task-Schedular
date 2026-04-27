package main;

import service.TaskScheduler;
import service.DeadlineMonitor;
import service.TaskDAO;
import model.Task;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        TaskScheduler scheduler = new TaskScheduler();

        // Start Deadline Monitor
        DeadlineMonitor monitor = new DeadlineMonitor();

        monitor.start();

        int choice;

        while (true) {

            System.out.println("\n------ TASK SCHEDULER------ ");
            System.out.println("1. Add Task");
            System.out.println("2. View Next Task");
            System.out.println("3. View All Tasks");
            System.out.println("4. Update Task Status");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter Task Title: ");
                    String title = sc.nextLine();

                    

                    System.out.print("Enter Duration (hours): ");
                    int duration = sc.nextInt();

                    sc.nextLine();

                    System.out.print(
                            "Enter Deadline (yyyy-MM-dd HH:mm): "
                    );

                    LocalDateTime deadline;
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd HH:mm"
                            );

                    while (true) {

                        String deadlineStr =
                                sc.nextLine();

                        try {

                            deadline =
                                    LocalDateTime.parse(
                                            deadlineStr,
                                            formatter
                                    );

                            // Check if deadline is in past
                            if (deadline.isBefore(
                                    LocalDateTime.now()
                            )) {

                                System.out.println(
                                        " Error: Past Deadline Not Accepted"
                                );

                                continue; 
                            }

                            break; // valid deadline

                        }

                        catch (Exception e) {

                            System.out.println(
                                    " Invalid format! Use yyyy-MM-dd HH:mm"
                            );
                        }
                    }

                    // Auto Priority
                    int priority = scheduler.calculatePriority(deadline);

                    Task t = new Task();

                    t.setTitle(title);
                    t.setPriority(priority);
                    t.setDuration(duration);
                    t.setDeadline(deadline);
                    t.setStatus("Pending");
                    t.setUserId(1);   // Use existing user ID

                    scheduler.addTask(t);
                    TaskDAO dao = new TaskDAO();
                    dao.addTask(t);
                    
                    System.out.println(
                            " Task Added Successfully!"
                    );

                    break;

                case 2:

                    Task next =
                            scheduler.getNextTask();

                    if (next != null) {

                        System.out.println(
                                "Next Task: "
                                + next.getTitle()
                        );

                    } else {

                        System.out.println(
                                "No Tasks Available"
                        );
                    }

                    break;
                    
                case 3:

                    List<Task> allTasks =
                            scheduler.getAllTasks();

                    if (allTasks.isEmpty()) {

                        System.out.println("No Tasks Available");

                    } else {

                        System.out.println("\n--- ALL TASKS ---");

                        for (Task task : allTasks) {

                            System.out.println(
                                    "Title: " + task.getTitle()
                            );

                            System.out.println(
                                    "Deadline: " + task.getDeadline()
                            );

                            System.out.println(
                                    "Priority: " + task.getPriority()
                            );

                            System.out.println(
                                    "Duration: " + task.getDuration()+" hrs"
                            );
                            
                            System.out.println(
                                    "Status: " + task.getStatus()
                            );

                            System.out.println("----------------------");
                        }
                    }

                    break;
                    
                case 4:

                    List<Task> AllTask =
                            scheduler.getAllTasks();

                    if (AllTask.isEmpty()) {

                        System.out.println("No Tasks Available");

                    } else {

                        System.out.println("\n--- TASK LIST ---");

                        int index = 1;

                        for (Task task : AllTask) {

                            System.out.println(
                                    index + ". "
                                    + task.getTitle()
                                    + " | Status: "
                                    + task.getStatus()
                            );

                            index++;
                        }

                        System.out.print(
                                "Enter task number to mark Completed: "
                        );

                        int taskNum = sc.nextInt();

                        if (taskNum > 0
                                && taskNum <= AllTask.size()) {

                            Task selected =
                            		AllTask.get(taskNum - 1);

                            selected.setStatus("Completed");

                            System.out.println(
                                    " Task marked as Completed!"
                            );

                        } else {

                            System.out.println(
                                    "Invalid Task Number"
                            );
                        }
                    }

                    break;

                case 5:

                    System.out.println("Thank you...");
                    System.exit(0);

                default:

                    System.out.println("Invalid choice");
            }
        }
    }
}