package java_sensor_reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class Main_ReaderTest {
    @Test
    void negativeTemperatureProducesLowWarning() {
        assertEquals("WARNING: Low temperature", Main_Reader.getTemperatureWarning(-5));
    }

    @Test
    void normalTemperatureProducesNoWarning() {
        assertEquals("", Main_Reader.getTemperatureWarning(50));
    }

    @Test
    void temperatureAbove100ProducesHighWarning() {
        assertEquals("WARNING: High temperature", Main_Reader.getTemperatureWarning(105));
    }

    @Test
    void temperatureAtZeroProducesNoWarning() {
        assertEquals("", Main_Reader.getTemperatureWarning(0));
    }

    @Test
    void temperatureAt100ProducesNoWarning() {
        assertEquals("", Main_Reader.getTemperatureWarning(100));
    }
}
