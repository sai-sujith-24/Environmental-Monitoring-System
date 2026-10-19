import java.util.Random;

/*
 * Concurrent Environmental Monitoring System Using Multithreading
 * Three threads run at the same time:
 *   TemperatureMonitor : 1 reading every 1 second
 *   HumidityMonitor    : 1 reading every 2 seconds
 *   AirQualityMonitor  : 1 reading every 3 seconds
 * Each thread keeps its OWN counters, so there is no shared data.
 * The main thread waits for all threads to finish and then prints the report.
 */

// ---------------------------------------------------------------
// Thread 1: Temperature (decimal value, 1 second interval)
// ---------------------------------------------------------------
class TemperatureMonitor extends Thread {
    private static final int TOTAL_READINGS = 10;

    private final Random random = new Random();
    private int totalCount = 0;
    private int normalCount = 0;
    private int warningCount = 0;
    private int criticalCount = 0;   // always 0 for temperature

    public int getTotalCount()    { return totalCount; }
    public int getNormalCount()   { return normalCount; }
    public int getWarningCount()  { return warningCount; }
    public int getCriticalCount() { return criticalCount; }

    @Override
    public void run() {
        for (int i = 1; i <= TOTAL_READINGS; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Temperature Thread interrupted. Stopping early.");
                Thread.currentThread().interrupt();
                return;
            }

            // random value between 10.0 and 45.0, rounded to 1 decimal place
            double temp = 10.0 + random.nextDouble() * 35.0;
            temp = Math.round(temp * 10) / 10.0;

            totalCount++;
            String line = "Temperature Thread - Reading " + i + ": " + temp + "\u00B0C - ";

            if (temp >= 18.0 && temp <= 35.0) {
                normalCount++;
                line += "NORMAL";
            } else {
                warningCount++;
                line += "WARNING";
                if (temp > 35.0) {
                    line += "\nALERT: Temperature exceeds the permitted threshold!";
                } else {
                    line += "\nALERT: Temperature is below the permitted threshold!";
                }
            }
            System.out.println(line);   // one println keeps the lines together
        }
    }
}

// ---------------------------------------------------------------
// Thread 2: Humidity (integer value, 2 second interval)
// ---------------------------------------------------------------
class HumidityMonitor extends Thread {
    private static final int TOTAL_READINGS = 10;

    private final Random random = new Random();
    private int totalCount = 0;
    private int normalCount = 0;
    private int warningCount = 0;
    private int criticalCount = 0;   // always 0 for humidity

    public int getTotalCount()    { return totalCount; }
    public int getNormalCount()   { return normalCount; }
    public int getWarningCount()  { return warningCount; }
    public int getCriticalCount() { return criticalCount; }

    @Override
    public void run() {
        for (int i = 1; i <= TOTAL_READINGS; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Humidity Thread interrupted. Stopping early.");
                Thread.currentThread().interrupt();
                return;
            }

            // random integer between 15 and 90
            int humidity = 15 + random.nextInt(76);

            totalCount++;
            String line = "Humidity Thread - Reading " + i + ": " + humidity + "% - ";

            if (humidity >= 30 && humidity <= 70) {
                normalCount++;
                line += "NORMAL";
            } else {
                warningCount++;
                line += "WARNING";
                if (humidity > 70) {
                    line += "\nALERT: Humidity exceeds the permitted threshold!";
                } else {
                    line += "\nALERT: Humidity is below the permitted threshold!";
                }
            }
            System.out.println(line);
        }
    }
}

// ---------------------------------------------------------------
// Thread 3: Air Quality Index (integer value, 3 second interval)
// ---------------------------------------------------------------
class AirQualityMonitor extends Thread {
    private static final int TOTAL_READINGS = 10;

    private final Random random = new Random();
    private int totalCount = 0;
    private int normalCount = 0;
    private int warningCount = 0;
    private int criticalCount = 0;

    public int getTotalCount()    { return totalCount; }
    public int getNormalCount()   { return normalCount; }
    public int getWarningCount()  { return warningCount; }
    public int getCriticalCount() { return criticalCount; }

    @Override
    public void run() {
        for (int i = 1; i <= TOTAL_READINGS; i++) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Air Quality Thread interrupted. Stopping early.");
                Thread.currentThread().interrupt();
                return;
            }

            // random integer between 20 and 300
            int aqi = 20 + random.nextInt(281);

            totalCount++;
            String line = "Air Quality Thread - Reading " + i + ": AQI " + aqi + " - ";

            if (aqi <= 100) {
                normalCount++;
                line += "NORMAL";
            } else if (aqi <= 200) {
                warningCount++;
                line += "WARNING";
                line += "\nALERT: Air quality is above the permitted threshold!";
            } else {
                criticalCount++;
                line += "CRITICAL";
                line += "\nCRITICAL ALERT: Hazardous air quality detected!";
            }
            System.out.println(line);
        }
    }
}

// ---------------------------------------------------------------
// Main class: starts the threads, waits for them, prints the report
// ---------------------------------------------------------------
public class EnvironmentalMonitoringSystemV2 {

    public static void main(String[] args) {
        System.out.println("===== ENVIRONMENTAL MONITORING SYSTEM =====");

        TemperatureMonitor tempThread = new TemperatureMonitor();
        HumidityMonitor humidityThread = new HumidityMonitor();
        AirQualityMonitor aqiThread = new AirQualityMonitor();

        // start() makes all three threads run at the same time
        tempThread.start();
        humidityThread.start();
        aqiThread.start();

        // Wait for every thread to finish. If main itself gets interrupted
        // while waiting, remember it and keep waiting, so we never read the
        // counters while a thread is still running.
        boolean interrupted = false;
        Thread[] threads = { tempThread, humidityThread, aqiThread };

        for (Thread thread : threads) {
            while (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();   // restore the flag for later
        }

        // Combine each thread's own statistics (safe: all threads are finished)
        int totalReadings = tempThread.getTotalCount()
                + humidityThread.getTotalCount()
                + aqiThread.getTotalCount();

        int totalNormal = tempThread.getNormalCount()
                + humidityThread.getNormalCount()
                + aqiThread.getNormalCount();

        int totalWarning = tempThread.getWarningCount()
                + humidityThread.getWarningCount()
                + aqiThread.getWarningCount();

        int totalCritical = tempThread.getCriticalCount()
                + humidityThread.getCriticalCount()
                + aqiThread.getCriticalCount();

        int totalAlerts = totalWarning + totalCritical;

        // Overall status = most severe reading
        String overallStatus;
        if (totalCritical > 0) {
            overallStatus = "CRITICAL";
        } else if (totalWarning > 0) {
            overallStatus = "WARNING";
        } else {
            overallStatus = "NORMAL";
        }

        System.out.println();
        System.out.println("===== CONSOLIDATED MONITORING REPORT =====");
        System.out.println("Temperature readings generated : " + tempThread.getTotalCount());
        System.out.println("Humidity readings generated    : " + humidityThread.getTotalCount());
        System.out.println("Air quality readings generated : " + aqiThread.getTotalCount());
        System.out.println("Total readings                 : " + totalReadings);
        System.out.println();
        System.out.println("Parameter      NORMAL  WARNING  CRITICAL");
        System.out.println("-----------------------------------------");
        printRow("Temperature", tempThread.getNormalCount(),
                tempThread.getWarningCount(), tempThread.getCriticalCount());
        printRow("Humidity", humidityThread.getNormalCount(),
                humidityThread.getWarningCount(), humidityThread.getCriticalCount());
        printRow("Air Quality", aqiThread.getNormalCount(),
                aqiThread.getWarningCount(), aqiThread.getCriticalCount());
        printRow("TOTAL", totalNormal, totalWarning, totalCritical);
        System.out.println();
        System.out.println("Total environmental alerts     : " + totalAlerts);
        System.out.println("Overall Environmental Status   : " + overallStatus);
        System.out.println();
        System.out.println("All monitoring threads have completed execution.");
    }

    // Prints one row of the summary table
    private static void printRow(String name, int normal, int warning, int critical) {
        System.out.printf("%-14s %6d  %7d  %8d%n", name, normal, warning, critical);
    }
}