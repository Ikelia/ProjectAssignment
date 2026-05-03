package com.bank.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple application-level logger.
 *
 * Writes timestamped log entries to:
 *   data/bank.log
 *
 * Also keeps an in-memory list so the UI can display recent activity.
 *
 * Log levels: INFO, SUCCESS, WARNING, ERROR
 */
public class BankLogger {

    public enum Level { INFO, SUCCESS, WARNING, ERROR }

    private static final String LOG_FILE = "data/bank.log";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** In-memory log for the UI activity feed (most-recent first). */
    private static final List<String> inMemoryLog = new ArrayList<>();

    private BankLogger() {}

    public static void log(Level level, String message) {
        String entry = String.format("[%s] [%s] %s",
                LocalDateTime.now().format(FMT), level, message);

        // Keep in-memory (cap at 200 entries)
        inMemoryLog.add(0, entry);
        if (inMemoryLog.size() > 200) inMemoryLog.remove(inMemoryLog.size() - 1);

        // Append to file
        ensureLogFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[LOGGER ERROR] " + e.getMessage());
        }
    }

    public static void info(String message)    { log(Level.INFO,    message); }
    public static void success(String message) { log(Level.SUCCESS, message); }
    public static void warning(String message) { log(Level.WARNING, message); }
    public static void error(String message)   { log(Level.ERROR,   message); }

    /** Returns an unmodifiable view of the in-memory log (most-recent first). */
    public static List<String> getRecentLogs() {
        return Collections.unmodifiableList(inMemoryLog);
    }

    private static void ensureLogFile() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
    }
}
