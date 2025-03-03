package dev.joulev.archduke.io;

import dev.joulev.archduke.exceptions.ArchdukeException;
import dev.joulev.archduke.exceptions.UnknownException;
import dev.joulev.archduke.tasks.TaskStore;
import dev.joulev.archduke.tasks.Task;

/**
 * This class handles (prettified) output to the terminal.
 */
public class Output {
    /**
     * The width of the box to be printed to the terminal, inclusive of border and
     * margin.
     */
    private static final int BOX_WIDTH = 80;
    private static final String HORIZONTAL_BASE_PADDING = " ";
    private static final String LEFT_BORDER_PADDING = BoxDrawingCharacter.VERTICAL_LINE
            + HORIZONTAL_BASE_PADDING;
    private static final String RIGHT_BORDER_PADDING = HORIZONTAL_BASE_PADDING
            + BoxDrawingCharacter.VERTICAL_LINE;

    private static void printLineWithDelim(String leftDelim, String rightDelim) {
        System.out.print(leftDelim);
        for (int i = 0; i < BOX_WIDTH - leftDelim.length() - rightDelim.length(); i++) {
            System.out.print(BoxDrawingCharacter.HORIZONTAL_LINE);
        }
        System.out.println(rightDelim);
    }

    private static void printBoxTopBorder() {
        printLineWithDelim(BoxDrawingCharacter.TOP_LEFT_CORNER,
                BoxDrawingCharacter.TOP_RIGHT_CORNER);
    }

    private static void printBoxBottomBorder() {
        printLineWithDelim(BoxDrawingCharacter.BOTTOM_LEFT_CORNER,
                BoxDrawingCharacter.BOTTOM_RIGHT_CORNER);
        System.out.println();
    }

    private static void printBoxBottomBorder(boolean withBottomMargin) {
        printLineWithDelim(BoxDrawingCharacter.BOTTOM_LEFT_CORNER,
                BoxDrawingCharacter.BOTTOM_RIGHT_CORNER);
        if (withBottomMargin) {
            System.out.println();
        }
    }

    private static void printBoxRightBorder(int unusedSpace) {
        // '<=' not '<' since we also have one space as padding
        for (int i = 0; i <= unusedSpace; i++) {
            System.out.print(HORIZONTAL_BASE_PADDING);
        }
        System.out.println(BoxDrawingCharacter.VERTICAL_LINE);
    }

    /**
     * Prints the logo with padding and all.
     * 
     * @see {@link https://patorjk.com/software/taag/#p=display&f=Slant&t=archduke}
     */
    private static void printLogo() {

        String[] lines = { "                   __        __      __      ",
                "  ____ ___________/ /_  ____/ /_  __/ /_____ ",
                " / __ `/ ___/ ___/ __ \\/ __  / / / / //_/ _ \\",
                "/ /_/ / /  / /__/ / / / /_/ / /_/ / ,< /  __/",
                "\\__,_/_/   \\___/_/ /_/\\__,_/\\__,_/_/|_|\\___/ ",
                "                                             " };
        int lineLength = lines[0].length();
        int availableSpace = BOX_WIDTH - lineLength - LEFT_BORDER_PADDING.length()
                - RIGHT_BORDER_PADDING.length();
        int leftPadding = availableSpace / 2;
        int rightPadding = availableSpace - leftPadding;

        for (String line : lines) {
            System.out.print(LEFT_BORDER_PADDING);
            for (int j = 0; j < leftPadding; j++) {
                System.out.print(HORIZONTAL_BASE_PADDING);
            }
            System.out.print(line);
            for (int j = 0; j < rightPadding; j++) {
                System.out.print(HORIZONTAL_BASE_PADDING);
            }
            System.out.println(RIGHT_BORDER_PADDING);
        }
    }

    /**
     * Prints a string inside a box, with box left and right borders at the start
     * and end of the line. The line automatically wraps around if necessary. This
     * assumes the box top and bottom borders are already drawn using
     * {@link #printBoxTopBorder} and {@link #printBoxBottomBorder}.
     * 
     * The parameters of this method is similar to {@code System.out.printf}.
     * 
     * @param format The string to print, with optional format specifiers.
     * @param args   The arguments to be formatted and substituted.
     * @throws ArchdukeException If there is an error in formatting the string.
     */
    public static void printf(String format, Object... args) throws ArchdukeException {
        final String SPACE = " ";
        final String ELLIPSIS = "...";

        int maxStringWidth = BOX_WIDTH - LEFT_BORDER_PADDING.length()
                - RIGHT_BORDER_PADDING.length();

        String input;
        try {
            input = String.format(format, args);
        } catch (Exception e) {
            throw new UnknownException("Out.printf; code = formatInput");
        }
        String[] words = input.split(SPACE);
        int currentLineLength = 0;

        System.out.print(LEFT_BORDER_PADDING);
        for (String word : words) {
            if (word.length() > maxStringWidth) {
                word = word.substring(0, maxStringWidth - ELLIPSIS.length()) + ELLIPSIS;
            }
            if (currentLineLength + word.length() > maxStringWidth) {
                printBoxRightBorder(maxStringWidth - currentLineLength);
                System.out.print(LEFT_BORDER_PADDING);
                currentLineLength = 0;
            }
            System.out.print(word + SPACE);
            currentLineLength += word.length() + 1;
        }
        printBoxRightBorder(maxStringWidth - currentLineLength);
    }

    /**
     * Prints a string inside a box.
     * 
     * @param format The string to print, with optional format specifiers.
     * @param args   The arguments to be formatted and substituted.
     * @throws ArchdukeException If there is an error in formatting the string.
     */
    public static void printBox(String format, Object... args) throws ArchdukeException {
        printBoxTopBorder();
        printf(format, args);
        printBoxBottomBorder();
    }

    /**
     * Prints the greeting message.
     * 
     * @throws ArchdukeException If string format fails. This should not happen, if
     *                           it happens it's a bug.
     */
    public static void greet() throws ArchdukeException {
        printBoxTopBorder();
        printLogo();
        printf("Hello! I'm Archduke. What do you want to do?");
        printBoxBottomBorder();
    }

    /**
     * Says goodbye.
     * 
     * @throws ArchdukeException If string format fails. This should not happen, if
     *                           it happens it's a bug.
     */
    public static void bye() throws ArchdukeException {
        printBoxTopBorder();
        printf("Bye. Hope to see you again soon!");
        printBoxBottomBorder(false);
    }

    /**
     * Prints an error message. Of course, inside a box.
     * 
     * @param format The string to print, with optional format specifiers.
     * @param args   The arguments to be formatted and substituted.
     * @throws ArchdukeException If there is an error in formatting the string.
     */
    public static void printError(String format, Object... args) throws ArchdukeException {
        printBox("ERROR: %s", String.format(format, args));
    }

    /**
     * Prints the tasks currently present.
     * 
     * @param taskStore The {@link TaskStore} to get the tasks from.
     * @throws ArchdukeException If string format fails. This should not happen, if
     *                           it happens it's a bug.
     */
    public static void printTasks(TaskStore taskStore) throws ArchdukeException {
        printBoxTopBorder();
        printf("Here are your tasks:");
        taskStore.listTasks();
        printBoxBottomBorder();
    }

    /**
     * Prints the tasks that match a query string.
     * 
     * @param taskStore The {@link TaskStore} to get the tasks from.
     * @param query     The query string.
     * @throws ArchdukeException If string format fails. This should not happen, if
     *                           it happens it's a bug.
     */
    public static void printQueriedTasks(TaskStore taskStore, String query)
            throws ArchdukeException {
        printBoxTopBorder();
        printf("Here are the tasks I found:");
        taskStore.queryTasks(query);
        printBoxBottomBorder();
    }

    /**
     * Prints the acknowledgement message on task addition.
     * 
     * @param task      The {@link Task} that was added.
     * @param storeSize The size of the {@link TaskStore} after the addition.
     * @throws ArchdukeException If string format fails. This should not happen, if
     *                           it happens it's a bug.
     */
    public static void printTaskAddition(Task task, int storeSize) throws ArchdukeException {
        printBoxTopBorder();
        printf("Added task:");
        printf("  %s", task.toString());
        printf("You now have %d task(s) in the list.", storeSize);
        printBoxBottomBorder();
    }

    /**
     * Prints the acknowledgement message on toggle of a task's completion status.
     * 
     * @param taskStore The {@link TaskStore} to get the tasks from.
     * @param index     The index of the task to be toggled.
     * @throws ArchdukeException If string format fails. This should not happen, if
     *                           it happens it's a bug.
     */
    public static void printTaskCompleteness(TaskStore taskStore, int index)
            throws ArchdukeException {
        Task task = taskStore.getTask(index);
        printBoxTopBorder();
        printf("The following task has been marked as %s", task.isCompleted() ? "done" : "undone");
        printf("  %s", task.toString());
        printBoxBottomBorder();
    }

    /**
     * Prints the acknowledgement message on task deletion.
     * 
     * @param task      The {@link Task} that was deleted.
     * @param storeSize The size of the {@link TaskStore} after the deletion.
     * @throws ArchdukeException If string format fails. This should not happen, if
     *                           it happens it's a bug.
     */
    public static void printTaskDeletion(Task task, int storeSize) throws ArchdukeException {
        printBoxTopBorder();
        printf("The following task has been deleted:");
        printf("  %s", task.toString());
        printf("You now have %d task(s) in the list.", storeSize);
        printBoxBottomBorder();
    }
}
