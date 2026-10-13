JAVA SENSOR READER - SMALL, TESTABLE REFACTOR

This source bundle is based on the Java code shared in the conversation.
It keeps package java_sensor_reader and class Main_Reader.
Use Java 17 or later and your existing Eclipse JUnit Jupiter / JUnit 5 library.

WHAT EACH CLASS DOES

SensorReading.java
    Holds the five values for ONE valid row. Private final fields and public
    getters demonstrate encapsulation. The object has no setters.

CsvReadResult.java
    Holds all accepted readings and all row-error messages for ONE file.
    Both getters return read-only lists. Error messages contain file line numbers.

SensorCsvReader.java
    parseLine(String): turns one CSV row into a SensorReading or throws
    IllegalArgumentException. No file access and no console printing.

    readSensorFile(String): opens a UTF-8 file, checks the header, calls parseLine
    for each data row, and returns a CsvReadResult. Invalid rows are collected as
    errors and skipped. Actual file I/O failures propagate as IOException.

Main_Reader.java
    Chooses the file, calls the reader, and prints readings, warnings, errors,
    and counts. The IOException catch stays here so the reader can be tested
    without capturing console output.

    getTemperatureWarning(double) is retained with the same behavior and
    location, so your existing five temperature tests remain compatible.

INSTALL INTO YOUR EXISTING ECLIPSE PROJECT

1. Keep a copy of your current Java files before replacing them.
2. Copy the four application classes from src/java_sensor_reader into your
   existing src/java_sensor_reader package. Replace Main_Reader.java.
3. Add SensorCsvReaderTest.java to the same package. Keep your existing
   Main_ReaderTest.java or replace it with the included equivalent; do not
   create two classes with the same name.
4. Keep your already-working JUnit/module setup. The supplied src/module-info.java
   is a reference for the current setup with application and tests in one source
   folder. If you use it, retain other declarations your project needs.
   JUnit must be on Modulepath for this modular setup.
5. Copy the data folder to your Eclipse project root (beside src).
6. Refresh the project. Run Main_Reader as Java Application, with the working
   directory set to the project root. With no arguments it reads data/sensors.csv.
7. For another file, use Run Configurations > Arguments > Program arguments:
   "C:/Users/gonzalezpj/Documents/java_sens_reading/sensor_data_100_rows.csv"
   Include quotes when the path contains spaces. Alternatively, edit the default
   csvFile value in Main_Reader. Tests do not use this default path.
8. Run the java_sensor_reader package as JUnit Test to run both test classes.

This is a source bundle to copy into your project, not an Eclipse project archive
with workspace metadata. No Maven or Gradle conversion is needed.

HOW TO READ THE TESTS

Start with validRowParsesEveryField. It passes a string directly to parseLine,
then checks the returned object's getters.

Next read invalidTemperatureIsRejected. assertThrows checks that an invalid row
causes the documented exception. The () -> expression supplies the code for
JUnit to execute; the exception is expected, so this is a passing test.

Then read badRowDoesNotPreventReadingTheNextRow. @TempDir supplies a temporary
folder, Files.writeString creates a tiny test CSV, and assertions check the
accepted reading, error count, and error line number. These tests use actual
files but are independent of your personal data folder.

The suite contains 5 temperature tests and 21 reader/parser tests (26 total).
These test definitions are provided for you to run in Eclipse. They were not
executed in the assistant environment: its Java launcher lacks libjli.so,
and a Java compiler and JUnit runtime are unavailable.

INTENTIONAL BEHAVIOR CHANGES

- main prints readings after the whole file has been read, then prints errors.
  Errors are no longer interleaved with the reading that caused them. stdout
  and stderr may also appear interleaved in an IDE console.
- Missing or incorrect headers produce a line-1 error and no readings.
  An empty file is a missing-header error; a header-only file is valid and empty.
- Empty sensor names, blank data rows, invalid boolean text, NaN, and infinities
  are rejected. true and false are accepted case-insensitively.
- Numeric temperatures below 0 and above 100 are still valid readings; they
  produce warnings. Exactly 0 and 100 produce no warning.
- Whitespace around data fields is trimmed. An optional UTF-8 BOM is accepted
  before the header. Header column names/order must match the documented header.
- This is deliberately a simple unquoted CSV reader. Quoted fields, embedded
  commas, and multiline fields are unsupported and quoted rows are rejected.
- A file I/O failure aborts the read and reaches main's IOException handler;
  it does not return a partial result.
- Valid readings and errors are retained in memory. This suits small practice
  CSVs, not an unlimited live data stream.

EXPECTED SAMPLE RESULTS

data/sensors.csv: 3 accepted readings, no row errors; one low-temperature warning
and one high-temperature warning.

data/mixed.csv: 2 accepted readings and 3 row errors, on lines 3, 4, and 5.
The final NOSE_RADAR reading is still accepted.

REFERENCE

JUnit Jupiter assertions and @TempDir:
https://docs.junit.org/5.13.1/user-guide/index.html
