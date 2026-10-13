package java_sensor_reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SensorCsvReaderTest {
    private static final String HEADER =
        "sensor_name,value,temp,voltage,has_weapons\n";

    // JUnit creates this directory for each test and cleans it up afterward.
    @TempDir
    Path tempDir;

    @Test
    void validRowParsesEveryField() {
        SensorReading reading =
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,107.3,27.9,false");

        assertEquals("ENGINE_LEFT", reading.getSensorName());
        assertEquals(68.5, reading.getSensorValue(), 0.000001);
        assertEquals(107.3, reading.getTemperature(), 0.000001);
        assertEquals(27.9, reading.getVoltage(), 0.000001);
        assertFalse(reading.hasWeapons());
    }

    @Test
    void surroundingWhitespaceAndUppercaseBooleanAreAccepted() {
        SensorReading reading =
            SensorCsvReader.parseLine(" LEFT_WING , 31.7 , -4.2 , 28.1 , TRUE ");
        assertEquals("LEFT_WING", reading.getSensorName());
        assertEquals(-4.2, reading.getTemperature(), 0.000001);
        assertTrue(reading.hasWeapons());
    }

    @Test
    void missingColumnIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,107.3,27.9"));
    }

    @Test
    void extraColumnIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,107.3,27.9,false,extra"));
    }

    @Test
    void invalidTemperatureIsRejected() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,abc,27.9,false"));
        assertTrue(error.getMessage().contains("temperature"));
    }

    @Test
    void emptyTemperatureIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,,27.9,false"));
    }

    @Test
    void invalidValueIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,abc,20,27.9,false"));
    }

    @Test
    void invalidVoltageIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,20,abc,false"));
    }

    @Test
    void nonFiniteNumbersAreRejected() {
        for (String number : new String[] {"NaN", "Infinity", "-Infinity", "1e309"}) {
            assertThrows(IllegalArgumentException.class, () ->
                SensorCsvReader.parseLine("ENGINE_LEFT,68.5," + number + ",27.9,false"));
        }
    }

    @Test
    void emptySensorNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine(" ,68.5,20,27.9,false"));
    }

    @Test
    void invalidBooleanIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,20,27.9,yes"));
    }

    @Test
    void emptyLastColumnIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("ENGINE_LEFT,68.5,20,27.9,"));
    }

    @Test
    void quotedFieldsAreExplicitlyRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            SensorCsvReader.parseLine("\"ENGINE_LEFT\",68.5,20,27.9,false"));
    }

    @Test
    void badRowDoesNotPreventReadingTheNextRow() throws IOException {
        Path file = tempDir.resolve("mixed.csv");
        Files.writeString(file, HEADER
            + "ENGINE_LEFT,68.5,abc,27.9,false\n"
            + "NOSE_RADAR,54.2,43.5,27.8,false\n");

        CsvReadResult result = SensorCsvReader.readSensorFile(file.toString());

        assertEquals(1, result.getReadings().size());
        assertEquals("NOSE_RADAR", result.getReadings().get(0).getSensorName());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).startsWith("Line 2:"));
    }

    @Test
    void validFileSkipsHeaderAndPreservesRowOrder() throws IOException {
        Path file = tempDir.resolve("valid.csv");
        Files.writeString(file, HEADER
            + "LEFT_WING,31.7,-4.2,28.1,true\n"
            + "ENGINE_LEFT,68.5,107.3,27.9,false\n");

        CsvReadResult result = SensorCsvReader.readSensorFile(file.toString());

        assertEquals(2, result.getReadings().size());
        assertEquals("LEFT_WING", result.getReadings().get(0).getSensorName());
        assertEquals("ENGINE_LEFT", result.getReadings().get(1).getSensorName());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void headerOnlyFileHasNoReadingsOrErrors() throws IOException {
        Path file = tempDir.resolve("header-only.csv");
        Files.writeString(file, HEADER);
        CsvReadResult result = SensorCsvReader.readSensorFile(file.toString());
        assertTrue(result.getReadings().isEmpty());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void emptyFileReportsMissingHeader() throws IOException {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "");
        CsvReadResult result = SensorCsvReader.readSensorFile(file.toString());
        assertTrue(result.getReadings().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).startsWith("Line 1:"));
    }

    @Test
    void incorrectHeaderPreventsProcessing() throws IOException {
        Path file = tempDir.resolve("wrong-header.csv");
        Files.writeString(file, "wrong,header\nENGINE_LEFT,68.5,20,27.9,false\n");
        CsvReadResult result = SensorCsvReader.readSensorFile(file.toString());
        assertTrue(result.getReadings().isEmpty());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    void blankDataLineIsReportedAndFollowingRowIsRead() throws IOException {
        Path file = tempDir.resolve("blank-line.csv");
        Files.writeString(file, HEADER + "\nENGINE_LEFT,68.5,20,27.9,false\n");
        CsvReadResult result = SensorCsvReader.readSensorFile(file.toString());
        assertEquals(1, result.getReadings().size());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).startsWith("Line 2:"));
    }

    @Test
    void utf8BomAndWindowsLineEndingsAreAccepted() throws IOException {
        Path file = tempDir.resolve("windows.csv");
        Files.writeString(file, "\uFEFF" + HEADER.replace("\n", "\r\n")
            + "ENGINE_LEFT,68.5,20,27.9,false\r\n");
        CsvReadResult result = SensorCsvReader.readSensorFile(file.toString());
        assertEquals(1, result.getReadings().size());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void missingFileThrowsIOException() {
        Path missing = tempDir.resolve("does-not-exist.csv");
        assertThrows(IOException.class, () ->
            SensorCsvReader.readSensorFile(missing.toString()));
    }
}
