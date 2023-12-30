package my.app.projetdentaire.beans;

public class CalculationResultHorizontal {
    private static int idCounter = 1;
    private int id;
    private double angleGaucheHorizontal;
    private double angleDroitHorizontal;

    public CalculationResultHorizontal(double angleGaucheHorizontal, double angleDroitHorizontal) {
        this.id = idCounter++;
        this.angleGaucheHorizontal = angleGaucheHorizontal;
        this.angleDroitHorizontal = angleDroitHorizontal;
    }
    // Getter pour l'ID
    public int getId() {
        return id;
    }
    public double getAngleGaucheHorizontal() {
        return angleGaucheHorizontal;
    }
    public double getAngleDroitHorizontal() {
        return angleDroitHorizontal;
    }
    public static int getIdCounter() {
        return idCounter;
    }

}
