package java_sensor_reader;

import java.io.IOException;

public class Main_Reader {
    public static void main(String[] args) {
        // Eclipse: Run Configurations > Arguments can supply a different path.
        String csvFile = args.length > 0 ? args[0] : "data/sensors.csv";

        try {
            CsvReadResult result = SensorCsvReader.readSensorFile(csvFile);

            for (SensorReading reading : result.getReadings()) {
                printReading(reading);
            }

            for (String error : result.getErrors()) {
                System.err.println(error);
            }

            System.out.println("Valid readings: " + result.getReadings().size());
            System.out.println("Errors: " + result.getErrors().size());
        } catch (IOException e) {
            System.err.println("Unable to open or read the CSV file: " + csvFile);
            System.err.println("Details: " + e.getMessage());
        }
    }

    private static void printReading(SensorReading reading) {
        System.out.println("Sensor: " + reading.getSensorName());
        System.out.println("Value: " + reading.getSensorValue());
        System.out.println("Temperature: " + reading.getTemperature());

        String warning = getTemperatureWarning(reading.getTemperature());
        if (!warning.isEmpty()) {
            System.out.println(warning);
        }

        System.out.println("Voltage: " + reading.getVoltage());
        System.out.println("Has weapons: " + reading.hasWeapons());
        System.out.println("-------------------");
    }

    // Kept here so your existing temperature tests still work.
    public static String getTemperatureWarning(double temperature) {
        if (temperature < 0) {
            return "WARNING: Low temperature";
        } else if (temperature > 100) {
            return "WARNING: High temperature";
        }
        return "";
    }
}
