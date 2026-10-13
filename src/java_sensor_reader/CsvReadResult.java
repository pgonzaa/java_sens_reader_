package java_sensor_reader;

import java.util.List;

// A file can contain both valid readings and invalid rows.
public final class CsvReadResult {
    private final List<SensorReading> readings;
    private final List<String> errors;

    CsvReadResult(List<SensorReading> readings, List<String> errors) {
        // Copy the lists so callers cannot accidentally change the result.
        this.readings = List.copyOf(readings);
        this.errors = List.copyOf(errors);
    }

    public List<SensorReading> getReadings() { return readings; }
    public List<String> getErrors() { return errors; }
}
