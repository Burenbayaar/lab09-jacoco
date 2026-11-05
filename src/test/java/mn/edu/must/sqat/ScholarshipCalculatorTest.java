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

    // ===== Part B: cover every branch =====

    @Test
    void letterGrade_score75_returnsC() {
        assertEquals("C", calc.letterGrade(75));
    }

    @Test
    void letterGrade_score65_returnsD() {
        assertEquals("D", calc.letterGrade(65));
    }

    @Test
    void letterGrade_score50_returnsF() {
        assertEquals("F", calc.letterGrade(50));
    }

    @Test
    void letterGrade_negativeScore_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
    }

    @Test
    void letterGrade_scoreAbove100_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    @Test
    void gpa_allFiveGrades_returns2() {
        // A=4, B=3, C=2, D=1, F=0 (switch default) -> 10 / 5 = 2.0
        assertEquals(2.0, calc.gpa(new double[]{95, 85, 75, 65, 50}), EPS);
    }

    @Test
    void gpa_nullScores_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
    }

    @Test
    void gpa_emptyScores_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    @Test
    void scholarship_negativeGpa_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.1, 15, false));
    }

    @Test
    void scholarship_gpaAbove4_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 15, false));
    }

    @Test
    void scholarship_negativeCredits_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.5, -1, false));
    }

    @Test
    void scholarship_withViolation_returns0() {
        assertEquals(0, calc.scholarship(3.9, 16, true));
    }

    @Test
    void scholarship_excellentGpaButFewCredits_returns70() {
        assertEquals(70, calc.scholarship(3.9, 13, false));
    }

    @Test
    void scholarship_goodGpaButFewCredits_returns50() {
        assertEquals(50, calc.scholarship(3.5, 10, false));
    }

    @Test
    void scholarship_lowGpa_returns0() {
        assertEquals(0, calc.scholarship(2.5, 20, false));
    }

    // ===== Part C: loop boundaries in gpa() (0 / 1 / 2+ iterations) =====

    @Test
    void gpa_loopZeroIterations_nullAndEmpty_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
    }

    @Test
    void gpa_loopOneIteration_singleA_returns4() {
        assertEquals(4.0, calc.gpa(new double[]{95}), EPS);
    }

    @Test
    void gpa_loopManyIterations_fourScores_returns2_5() {
        assertEquals(2.5, calc.gpa(new double[]{95, 85, 75, 65}), EPS);
    }
}
