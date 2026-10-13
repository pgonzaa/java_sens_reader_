// Matches the current learning-project setup: application and tests share src.
module java_sensor_reader {
    requires org.junit.jupiter.api;
    opens java_sensor_reader to org.junit.platform.commons;
}
