package my.app.projetdentaire.beans;

public class CalculationResult {

    private static int idCounter = 1;
    private int id;
    private double angleGauche;
    private double angleDroit;
    private double intersectionAngleDeg;
    private String issymetrical;

    private double angleGaucheHorizontal;
    private double angleDroitHorizontal;
    private double intersectionAngleDegHorizontal;

    public CalculationResult(double angleGauche, double angleDroit, double intersectionAngleDeg,String issymetrical) {
        this.id = idCounter++;
        this.angleGauche = angleGauche;
        this.angleDroit = angleDroit;
        this.intersectionAngleDeg = intersectionAngleDeg;
        this.issymetrical = issymetrical;
    }

    public CalculationResult(double angleGaucheHorizontal, double angleDroitHorizontal) {
        this.id = idCounter++;
        this.angleGaucheHorizontal = angleGaucheHorizontal;
        this.angleDroitHorizontal = angleDroitHorizontal;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    // Getters
    public double getAngleGauche() {
        return angleGauche;
    }

    public double getAngleDroit() {
        return angleDroit;
    }

    public double getIntersectionAngleDeg() {
        return intersectionAngleDeg;
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
