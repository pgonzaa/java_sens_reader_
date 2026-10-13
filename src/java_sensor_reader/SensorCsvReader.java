package java_sensor_reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SensorCsvReader {
    private static final String HEADER =
        "sensor_name,value,temp,voltage,has_weapons";

    // Parse one row. No file access or console output is needed to test this.
    public static SensorReading parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("empty sensor row");
        }

        // This small reader supports simple, unquoted CSV fields only.
        if (line.contains("\"")) {
            throw new IllegalArgumentException("quoted CSV fields are not supported");
        }

        String[] values = line.split(",", -1);
        if (values.length != 5) {
            throw new IllegalArgumentException(
                "expected 5 columns, found " + values.length);
        }

        String sensorName = values[0].trim();
        if (sensorName.isEmpty()) {
            throw new IllegalArgumentException("sensor name is empty");
        }

        double sensorValue = parseNumber(values[1], "value");
        double temperature = parseNumber(values[2], "temperature");
        double voltage = parseNumber(values[3], "voltage");

        String weaponsText = values[4].trim();
        if (!weaponsText.equalsIgnoreCase("true")
                && !weaponsText.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("has_weapons must be true or false");
        }

        return new SensorReading(sensorName, sensorValue, temperature, voltage,
                                 Boolean.parseBoolean(weaponsText));
    }

    private static double parseNumber(String text, String fieldName) {
        double number;
        try {
            number = Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                fieldName + " must be a number: '" + text + "'", e);
        }

        if (!Double.isFinite(number)) {
            throw new IllegalArgumentException(fieldName + " must be finite");
        }
        return number;
    }

    // File failures propagate as IOException. Invalid rows become error entries.
    public static CsvReadResult readSensorFile(String csvFile) throws IOException {
        List<SensorReading> readings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Files.newBufferedReader uses UTF-8 by default.
        try (BufferedReader br = Files.newBufferedReader(Paths.get(csvFile))) {
            String header = br.readLine();
            // Accept the optional UTF-8 byte order mark used by some editors.
            if (header != null && header.startsWith("\uFEFF")) {
                header = header.substring(1);
            }
            if (header == null || !HEADER.equals(header.trim())) {
                errors.add("Line 1: missing or incorrect CSV header. Expected: " + HEADER);
                return new CsvReadResult(readings, errors);
            }

            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    readings.add(parseLine(line));
                } catch (IllegalArgumentException e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage()
                               + ". Skipping.");
                }
            }
        }

        return new CsvReadResult(readings, errors);
    }
}
