package my.app.projetdentaire.beans;

import android.graphics.Bitmap;

public class CalculationResult {

    private static int idCounter = 1;
    private int id;
    private double angleGauche;
    private double angleDroit;
    private double convergence;
    private String isSymetrical;
    private double angleGaucheHorizontal;
    private double angleDroitHorizontal;
    private Bitmap images;


    public CalculationResult(double angleGauche, double angleDroit, double intersectionAngleDeg,String issymetrical) {
        this.id = idCounter++;
        this.angleGauche = angleGauche;
        this.angleDroit = angleDroit;
        this.convergence = intersectionAngleDeg;
        this.isSymetrical = issymetrical;
    }
    public CalculationResult(double angleGauche, double angleDroit, double intersectionAngleDeg,String issymetrical,Bitmap images) {
        this.id = idCounter++;
        this.angleGauche = angleGauche;
        this.angleDroit = angleDroit;
        this.convergence = intersectionAngleDeg;
        this.isSymetrical = issymetrical;
        this.images=images;
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
        return convergence;
    }

    // Getter pour l'ID
    public int getId() {
        return id;
    }

    public String getIssymetrical() {
        return isSymetrical;
    }

    public double getAngleGaucheHorizontal() {
        return angleGaucheHorizontal;
    }

    public double getAngleDroitHorizontal() {
        return angleDroitHorizontal;
    }

    public Bitmap getImages() {
        return images;
    }

}
