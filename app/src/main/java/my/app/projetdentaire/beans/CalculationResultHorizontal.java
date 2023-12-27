package my.app.projetdentaire.beans;

public class CalculationResultHorizontal {
    private static int idCounter = 1;
    private int id;
    private String issymetrical;
    private double angleGaucheHorizontal;
    private double angleDroitHorizontal;
    private double intersectionAngleDegHorizontal;


    public CalculationResultHorizontal (double angleGaucheHorizontal, double angleDroitHorizontal) {
        this.id = idCounter++;
        this.angleGaucheHorizontal = angleGaucheHorizontal;
        this.angleDroitHorizontal = angleDroitHorizontal;
    }

    // Getter pour l'ID
    public int getId() {
        return id;
    }

    public String getIssymetrical() {
        return issymetrical;
    }

    public double getAngleGaucheHorizontal() {
        return angleGaucheHorizontal;
    }

    public double getAngleDroitHorizontal() {
        return angleDroitHorizontal;
    }

    public double getIntersectionAngleDegHorizontal() {
        return intersectionAngleDegHorizontal;
    }
}
