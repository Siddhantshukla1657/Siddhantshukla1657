import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

class Task {
    String name;
    boolean isCompleted;
    int timeInHour, timeInMinute;
    int timeOutHour, timeOutMinute;
    int hoursTaken, minutesTaken;

    Task(String name) {
        this.name = name;
        this.isCompleted = false;
        this.timeInHour = -1;  // Invalid time by default
        this.timeInMinute = -1;
        this.timeOutHour = -1;
        this.timeOutMinute = -1;
    }

    void setTimeIn(int hour, int minute) {
        this.timeInHour = hour;
        this.timeInMinute = minute;
    }

    void setTimeOut(int hour, int minute) {
        this.timeOutHour = hour;
        this.timeOutMinute = minute;
        calculateTimeTaken();
    }

    void complete() {
        isCompleted = true;
    }

    private void calculateTimeTaken() {
        if (timeInHour == -1 || timeOutHour == -1) {
            hoursTaken = 0;
            minutesTaken = 0;
        } else {
            // Convert timeIn and timeOut to minutes since midnight
            int startMinutes = timeInHour * 60 + timeInMinute;
            int endMinutes = timeOutHour * 60 + timeOutMinute;

            // If the end time is before the start time, it means the task spans into the next day
            if (endMinutes < startMinutes) {
                endMinutes += 24 * 60; // Add 24 hours worth of minutes to endMinutes
            }

            // Calculate the total time taken in minutes
            int totalMinutes = endMinutes - startMinutes;

            // Convert the total time taken into hours and minutes
            hoursTaken = totalMinutes / 60;
            minutesTaken = totalMinutes % 60;
        }
    }

    @Override
    public String toString() {
        if (isCompleted) {
            return name + " (Completed) | Time In: " + formatTime(timeInHour, timeInMinute) + 
                   " | Time Out: " + formatTime(timeOutHour, timeOutMinute) +
                   " | Duration: " + hoursTaken + " hours " + minutesTaken + " minutes";
        } else {
            return name + " (Pending)";
        }
    }

    private String formatTime(int hour, int minute) {
        boolean isPM = hour >= 12;
        int displayHour = (hour % 12 == 0) ? 12 : hour % 12;
        String amPm = isPM ? "PM" : "AM";
        return String.format("%02d:%02d %s", displayHour, minute, amPm);
    }
}

public class ToDoListApp {
    private ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        ToDoListApp app = new ToDoListApp();
        app.run();
    }

    public void run() {
        showMenu();
    }

    private void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nMenu:");
            System.out.println("1. Add Task");
            System.out.println("2. Start Task");
            System.out.println("3. Complete Task");
            System.out.println("4. View All Tasks");
            System.out.println("5. View Pending Tasks");
            System.out.println("6. View Completed Tasks");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTask(scanner);
                    break;
                case 2:
                    startTask(scanner);
                    break;
                case 3:
                    completeTask(scanner);
                    break;
                case 4:
                    viewTasks(); // View all tasks
                    break;
                case 5:
                    viewPendingTasks(); // View pending tasks
                    break;
                case 6:
                    viewCompletedTasks(); // View completed tasks
                    break;
                case 7:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        } while (choice != 7);
        
        scanner.close();
    }

    private void addTask(Scanner scanner) {
        System.out.print("Enter task name: ");
        String name = scanner.nextLine();
        tasks.add(new Task(name));
        System.out.println("Added: " + name);
    }

    private void startTask(Scanner scanner) {
        ArrayList<Integer> pendingTaskIndices = viewPendingTasks();

        if (pendingTaskIndices.isEmpty()) {
            System.out.println("No tasks available to start.");
            return;
        }

        System.out.print("Enter the index of the task to start: ");
        int index = scanner.nextInt() - 1; // Adjust for zero-based index
        scanner.nextLine();

        if (index >= 0 && index < pendingTaskIndices.size()) {
            int taskIndex = pendingTaskIndices.get(index);
            int[] time = getTimeInput(scanner, "Enter time in (HH:MM format): ");
            tasks.get(taskIndex).setTimeIn(time[0], time[1]);
            System.out.println("Started: " + tasks.get(taskIndex).name + " at " + format12Hour(time[0], time[1], time[2]));
        } else {
            System.out.println("Invalid index.");
        }
    }

    private void completeTask(Scanner scanner) {
        ArrayList<Integer> pendingTaskIndices = viewPendingTasks();

        if (pendingTaskIndices.isEmpty()) {
            System.out.println("No tasks available to complete.");
            return;
        }

        System.out.print("Enter the index of the task to complete: ");
        int index = scanner.nextInt() - 1; // Adjust for zero-based index
        scanner.nextLine();

        if (index >= 0 && index < pendingTaskIndices.size()) {
            int taskIndex = pendingTaskIndices.get(index);
            int[] time = getTimeInput(scanner, "Enter time out (HH:MM format): ");
            tasks.get(taskIndex).setTimeOut(time[0], time[1]);
            tasks.get(taskIndex).complete();
            System.out.println("Completed: " + tasks.get(taskIndex).name);
        } else {
            System.out.println("Invalid index.");
        }
    }

    // Helper function to handle time input with exception handling and AM/PM selection
    private int[] getTimeInput(Scanner scanner, String prompt) {
        int hour = -1;
        int minute = -1;
        int amPm = -1; // 0 for AM, 1 for PM
        boolean validInput = false;

        while (!validInput) {
            System.out.print(prompt);
            try {
                String[] timeInput = scanner.nextLine().split(":");
                hour = Integer.parseInt(timeInput[0]);
                minute = Integer.parseInt(timeInput[1]);

                if (hour >= 1 && hour <= 12 && minute >= 0 && minute <= 59) {
                    System.out.print("Is it AM or PM? Enter 0 for AM and 1 for PM: ");
                    amPm = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (amPm == 0 || amPm == 1) {
                        hour = convertTo24Hour(hour, amPm);
                        validInput = true;
                    } else {
                        System.out.println("Invalid AM/PM selection. Please enter 0 for AM and 1 for PM.");
                    }
                } else {
                    System.out.println("Invalid time range. Please enter valid hours (1-12) and minutes (0-59).");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter time in HH:MM format.");
            }
        }
        return new int[]{hour, minute, amPm};
    }

    // Convert 12-hour format to 24-hour format based on AM/PM
    private int convertTo24Hour(int hour, int amPm) {
        if (amPm == 1 && hour != 12) {
            return hour + 12; // Convert PM hours (except 12 PM) to 24-hour format
        } else if (amPm == 0 && hour == 12) {
            return 0; // 12 AM is 00:00 in 24-hour format
        }
        return hour;
    }

    private String format12Hour(int hour, int minute, int amPm) {
        String amPmString = (amPm == 0) ? "AM" : "PM";
        int displayHour = (hour % 12 == 0) ? 12 : hour % 12;
        return String.format("%02d:%02d %s", displayHour, minute, amPmString);
    }

    private ArrayList<Integer> viewPendingTasks() {
        ArrayList<Integer> pendingTaskIndices = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            if (!tasks.get(i).isCompleted) {
                pendingTaskIndices.add(i);
                System.out.println((pendingTaskIndices.size()) + ". " + tasks.get(i));
            }
        }

        if (pendingTaskIndices.isEmpty()) {
            System.out.println("No pending tasks.");
        }

        return pendingTaskIndices;
    }

    private void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        Collections.sort(tasks, Comparator.comparing(task -> task.name)); // Sort by task name

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    private void viewCompletedTasks() {
        ArrayList<Task> completedTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isCompleted) {
                completedTasks.add(task);
            }
        }

        if (completedTasks.isEmpty()) {
            System.out.println("No completed tasks.");
            return;
        }

        Collections.sort(completedTasks, Comparator.comparing(task -> task.name)); // Sort by task name

        for (int i = 0; i < completedTasks.size(); i++) {
            System.out.println((i + 1) + ". " + completedTasks.get(i));
        }
    }
}
