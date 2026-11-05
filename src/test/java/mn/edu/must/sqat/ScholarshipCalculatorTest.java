package mn.edu.must.sqat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScholarshipCalculatorTest {

    private static final double EPS = 1e-9;
    private final ScholarshipCalculator calc = new ScholarshipCalculator();

    // ===== Part A: happy path =====

    @Test
    void letterGrade_score95_returnsA() {
        assertEquals("A", calc.letterGrade(95));
    }

    @Test
    void letterGrade_score85_returnsB() {
        assertEquals("B", calc.letterGrade(85));
    }

    @Test
    void gpa_scoresAandB_returns3_5() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), EPS);
    }

    @Test
    void scholarship_excellentGpaAndFullLoad_returns100() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
    }

    @Test
    void scholarship_goodGpaAndEnoughCredits_returns70() {
        assertEquals(70, calc.scholarship(3.5, 13, false));
    }
}
