package java_sensor_reader;

// One parsed row. Private final fields keep the reading unchanged after creation.
public final class SensorReading {
    private final String sensorName;
    private final double sensorValue;
    private final double temperature;
    private final double voltage;
    private final boolean hasWeapons;

    SensorReading(String sensorName, double sensorValue, double temperature,
                  double voltage, boolean hasWeapons) {
        this.sensorName = sensorName;
        this.sensorValue = sensorValue;
        this.temperature = temperature;
        this.voltage = voltage;
        this.hasWeapons = hasWeapons;
    }

    public String getSensorName() { return sensorName; }
    public double getSensorValue() { return sensorValue; }
    public double getTemperature() { return temperature; }
    public double getVoltage() { return voltage; }
    public boolean hasWeapons() { return hasWeapons; }
}
