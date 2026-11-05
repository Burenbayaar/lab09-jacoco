package mn.edu.must.sqat;

/**
 * Тэтгэлэг тооцоологч — Лаб 9-ийн бүтцийн тестийн бай.
 * Спецификаци (oracle):
 *   letterGrade: 90-100 -> "A", 80-89.99 -> "B", 70-79.99 -> "C",
 *                60-69.99 -> "D", 0-59.99 -> "F";
 *                0-иос бага, 100-аас их бол IllegalArgumentException.
 *   gpa:         дүнгүүдийн голч (A=4, B=3, C=2, D=1, F=0);
 *                null эсвэл хоосон массивт IllegalArgumentException.
 *   scholarship: сахилгын зөрчилтэй бол 0;
 *                голч >= 3.8 ба кредит >= 15 бол 100;
 *                голч >= 3.4 ба кредит >= 12 бол 70;
 *                голч >= 3.0 бол 50; бусад тохиолдолд 0.
 *                Голч 0-4, кредит 0+ хязгаараас гадуур бол IllegalArgumentException.
 */
public class ScholarshipCalculator {

    public String letterGrade(double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Оноо 0-100 хооронд байх ёстой: " + score);
        }
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    public double gpa(double[] scores) {
        if (scores == null || scores.length == 0) {
            throw new IllegalArgumentException("Дүнгүй голч бодох боломжгүй");
        }
        double total = 0;
        for (double s : scores) {
            switch (letterGrade(s)) {
                case "A": total += 4; break;
                case "B": total += 3; break;
                case "C": total += 2; break;
                case "D": total += 1; break;
                default:  break;
            }
        }
        return total / scores.length;
    }

    public int scholarship(double gpa, int credits, boolean hasViolation) {
        if (gpa < 0 || gpa > 4 || credits < 0) {
            throw new IllegalArgumentException("Буруу оролт");
        }
        if (hasViolation) return 0;
        if (gpa >= 3.8 && credits >= 15) return 100;
        if (gpa >= 3.4 && credits >= 12) return 70;
        if (gpa >= 3.0) return 50;
        return 0;
    }
}
