import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.*;

class Task {
    String name;
    Calendar startTime;
    Calendar endTime;
    boolean isCompleted;

    Task(String name, Calendar startTime, Calendar endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = false;
    }
}

public class ToDoListApp {
    private ArrayList<Task> tasks = new ArrayList<>();
    
    public static void main(String[] args) {
        ToDoListApp app = new ToDoListApp();
        app.run();
    }

    public void run() {
        // Start the timer for alarms
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAlarms();
            }
        }, 0, 1000); // Check every second

        showMenu();
    }

    private void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nMenu:");
            System.out.println("1. Add Task");
            System.out.println("2. Complete Task");
            System.out.println("3. View Pending Tasks");
            System.out.println("4. View Completed Tasks");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTask(scanner);
                    break;
                case 2:
                    completeTask(scanner);
                    break;
                case 3:
                    viewTasks(false); // View pending tasks
                    break;
                case 4:
                    viewTasks(true); // View completed tasks
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        } while (choice != 5);
        
        scanner.close();
    }

    private void addTask(Scanner scanner) {
        System.out.print("Enter task name: ");
        String name = scanner.nextLine();

        System.out.print("Enter start time (HH:mm): ");
        String[] startParts = scanner.nextLine().split(":");

        // Set end time to be 0 seconds after the start time
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startParts[0]));
        startTime.set(Calendar.MINUTE, Integer.parseInt(startParts[1]));
        
        Calendar endTime = (Calendar) startTime.clone(); // Clone start time
        endTime.set(Calendar.SECOND, 0); // Set seconds to zero

        tasks.add(new Task(name, startTime, endTime));
        System.out.println("Added: " + name);
    }

    private void completeTask(Scanner scanner) {
        viewTasks(false); // Show pending tasks first

        if (tasks.isEmpty()) {
            System.out.println("No tasks available to complete.");
            return;
        }

        System.out.print("Enter the index of the task to complete: ");
        int index = scanner.nextInt() - 1; // Adjust for zero-based index

        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).isCompleted = true; // Mark as completed
            System.out.println("Completed: " + tasks.get(index).name);
            tasks.remove(index); // Remove from pending tasks
        } else {
            System.out.println("Invalid index.");
        }
    }

    private void viewTasks(boolean completed) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (task.isCompleted == completed) {
                filteredTasks.add(task);
            }
        }

        if (filteredTasks.isEmpty()) {
            System.out.println(completed ? "No completed tasks." : "No pending tasks.");
            return;
        }

        // Sort by start time
        Collections.sort(filteredTasks, Comparator.comparing(task -> task.startTime));

        for (int i = 0; i < filteredTasks.size(); i++) {
            Task task = filteredTasks.get(i);
            String status = task.isCompleted ? " (Completed)" : "";
            System.out.println((i + 1) + ". " + task.name + " (Start: " + task.startTime.getTime() + ", End: " + task.endTime.getTime() + ")" + status);
        }
    }

    private void checkAlarms() {
        Calendar now = Calendar.getInstance();

        for (Task task : tasks) {
            if (now.after(task.startTime) && !task.isCompleted) {
                playSound("start.wav"); // Play sound at start time
                playSound("reminder.wav"); // Play reminder sound during the task duration
                break; // Only play once per minute
            } else if (now.after(task.endTime) && !task.isCompleted) {
                playSound("end.wav"); // Play sound at end time
                task.isCompleted = true; // Mark as completed
                break; // Only play once per minute
            }
        }
    }

    private void playSound(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            Thread.sleep(clip.getMicrosecondLength() / 1000); // Wait for sound to finish

            clip.close();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
