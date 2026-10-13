# Java Sensor Reader

A small Java console application that reads simulated sensor data from a CSV file, checks each row, and displays readings and temperature warnings.

I built this project in Eclipse to practice Java, file handling, and JUnit testing. It follows the same sensor-reading idea as my earlier C++/Qt project, with the focus here on parsing, validation, and keeping the code easy to test.

## Features

- Reads sensor data from UTF-8 CSV files.
- Reports invalid rows with line numbers and continues processing valid data.
- Displays warnings for temperatures below 0 or above 100.
- Prints a summary of valid readings and errors.
- Includes JUnit tests for parsing, file handling, and temperature thresholds.

## Project Structure

Application and test classes are in `src/java_sensor_reader`.

| File | Purpose |
| --- | --- |
| `SensorReading.java` | Stores the five values from a valid row using private final fields and public getters. |
| `CsvReadResult.java` | Stores read-only lists of accepted readings and error messages. |
| `SensorCsvReader.java` | Parses individual rows and reads CSV files. |
| `Main_Reader.java` | Runs the program and prints readings, warnings, and errors. |
| `Main_ReaderTest.java` | Tests temperature warnings and boundary values. |
| `SensorCsvReaderTest.java` | Tests row validation and file-reading behavior. |

`SensorCsvReader.parseLine()` handles a single row without opening a file or printing output. `readSensorFile()` calls it for each data row and collects the results. File I/O errors are passed back to `Main_Reader` for display.

## CSV Format

Files must begin with this header, using the same column names and order:

```csv
sensor_name,value,temp,voltage,has_weapons
NOSE_RADAR,54.2,43.5,27.8,false
LEFT_WING,31.7,-4.2,28.1,true
ENGINE_LEFT,68.5,107.3,27.9,false
```

Each row contains a sensor name, three numeric values, and a boolean flag. Temperatures below 0 or above 100 produce warnings but are still accepted. Exactly 0 and 100 do not produce a warning.

## Running in Eclipse

### Requirements

- Java 17 or later
- Eclipse with Java support
- JUnit 5 (Jupiter)

### Setup

1. Download or clone the repository.
2. Create a Java project in Eclipse.
3. Import the contents of `src` into the project's `src` folder, including the supplied `module-info.java`. Copy `data` to the project root, beside `src`.
4. Under **Properties → Java Build Path → Libraries**, add **JUnit 5** to **Modulepath**.
5. Run `Main_Reader.java` using **Run As → Java Application**.

The default input is `data/sensors.csv`. Set the working directory to the project root.

To read another file, enter its path under **Run Configurations → Arguments → Program arguments**, for example:

```text
data/mixed.csv
```

Use quotes around paths that contain spaces.

## Testing

Right-click the `java_sensor_reader` package and select **Run As → JUnit Test**.

The suite contains 26 tests: 5 temperature tests and 21 parser/file-reading tests. Coverage includes valid rows, missing or extra columns, invalid numbers and booleans, empty fields, header handling, missing files, and continuing after a bad row.

File-based tests use JUnit's `@TempDir` to create temporary CSVs, so they do not depend on a local data-file path.

The included sample files have these expected results:

| File | Valid readings | Errors |
| --- | --- | --- |
| `data/sensors.csv` | 3 | 0 |
| `data/mixed.csv` | 2 | 3 |

The errors in `mixed.csv` are on lines 3, 4, and 5. The final valid row is still processed.

## Validation and Limitations

- Missing or incorrect headers stop processing. A header-only file is valid; an empty file reports a missing header.
- Empty sensor names, blank data rows, invalid boolean text, `NaN`, and infinite numeric values are rejected.
- Whitespace around data fields is trimmed. Boolean values are case-insensitive, and an optional UTF-8 BOM is accepted before the header.
- Only simple, unquoted CSV fields are supported. Quoted fields, embedded commas, and multiline fields are unsupported.
- Readings and errors are kept in memory and displayed after the file has been read. This project is intended for small CSV files.
- A file I/O failure stops the read and displays an error message.
