package my.app.projetdentaire.beans;

public class StudentPW {

    private String imageFront;
    private double af1;
    private double af2;
    private double bf1;
    private double bf2;
    private double cf1;
    private double cf2;
    private double cvf2;
    private double note;
    private String imageSide;

    private String date;
    private String time;

    public StudentPW() {
    }

    public StudentPW(String imageFront, double af1, double af2, double bf1, double bf2, double cf1, double cf2, double cvf2, double note, String imageSide) {
        this.imageFront = imageFront;
        this.af1 = af1;
        this.af2 = af2;
        this.bf1 = bf1;
        this.bf2 = bf2;
        this.cf1 = cf1;
        this.cf2 = cf2;
        this.cvf2 = cvf2;
        this.note = note;
        this.imageSide = imageSide;

    }


    public String getImageFront() {
        return imageFront;
    }

    public void setImageFront(String imageFront) {
        this.imageFront = imageFront;
    }

    public double getAf1() {
        return af1;
    }

    public void setAf1(double af1) {
        this.af1 = af1;
    }

    public double getAf2() {
        return af2;
    }

    public void setAf2(double af2) {
        this.af2 = af2;
    }

    public double getBf1() {
        return bf1;
    }

    public void setBf1(double bf1) {
        this.bf1 = bf1;
    }

    public double getBf2() {
        return bf2;
    }

    public void setBf2(double bf2) {
        this.bf2 = bf2;
    }

    public double getCf1() {
        return cf1;
    }

    public void setCf1(double cf1) {
        this.cf1 = cf1;
    }

    public double getCf2() {
        return cf2;
    }

    public void setCf2(double cf2) {
        this.cf2 = cf2;
    }

    public double getCvf2() {
        return cvf2;
    }

    public void setCvf2(double cvf2) {
        this.cvf2 = cvf2;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public String getImageSide() {
        return imageSide;
    }

    public void setImageSide(String imageSide) {
        this.imageSide = imageSide;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}