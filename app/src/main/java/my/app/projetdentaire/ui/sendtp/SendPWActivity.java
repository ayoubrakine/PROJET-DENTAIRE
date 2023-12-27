package my.app.projetdentaire.ui.sendtp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import my.app.projetdentaire.R;
import my.app.projetdentaire.api.RetrofitStudent;
import my.app.projetdentaire.api.StudentApi;
import my.app.projetdentaire.beans.CalculationResult;
import my.app.projetdentaire.beans.CalculationResultHorizontal;
import my.app.projetdentaire.beans.StudentPW;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendPWActivity extends AppCompatActivity {

    private int zoomClickCounter = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private static final int CAMERA_REQUEST_CODE = 2; // Valeur arbitraire
    private boolean isSelectingPointsLocked = false;
    private static final float DEFAULT_SCALE = 1.0f;
    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 5.0f; // Vous pouvez ajuster la valeur maximale de zoom selon vos besoins
    private boolean isProcessImageCalledvertical = false;
    private boolean isProcessImageCalledhorizontal = false;
    Bitmap contoursBitmap;


    {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Toast.makeText(SendPWActivity.this, "OpenCV initialization failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private static int calculationCounter = 0; // Compteur de calculs
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_SELECTED_POINTS = 4;
    private ImageView imageView;
    private Mat originalMat;
    private TextView step;

    private ImageView btnSelectImage, camera, gallery, reset, result, submit, step1, step2, step3;
    private List<Point> selectedPoints = new ArrayList<>();
    private Bitmap originalBitmap;
    String encodedImage;
    String encodedImage1;

    private List<CalculationResult> calculationResults = new ArrayList<>();

    private List<CalculationResultHorizontal> calculationResultshorizontal = new ArrayList<>();

    private long studentId;
    long pwdId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_pw);
        //intent
        Intent intent = getIntent();
        studentId = intent.getLongExtra("studentId", 0);
        pwdId = intent.getLongExtra("pwId", 0);
        //log
        Log.d("id student", studentId + "");
        Log.d("pwwww id", pwdId + "");

        btnSelectImage = findViewById(R.id.btnSelectImage);
        imageView = findViewById(R.id.imageview);
        step = findViewById(R.id.step);
        reset = findViewById(R.id.reset);
        result = findViewById(R.id.result);
        camera = findViewById(R.id.camera);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(SendPWActivity.this, "Impossible d'ouvrir l'appareil photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        step1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (originalBitmap != null) {
                    step.setText("Step 1 : You calculate angles with respect to the horizontal");
                    zoomClickCounter++;
                    selectedPoints.clear();
                    isProcessImageCalledvertical = false;
                    isProcessImageCalledhorizontal = false;
                    processImageHorizontal(originalBitmap);

                } else {
                    Toast.makeText(SendPWActivity.this, "Pas de photo séléctionnée", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (originalBitmap != null) {
                    // Réinitialiser la liste des points sélectionnés
                    selectedPoints.clear();
                    // Réafficher l'image initiale avec la méthode processImage
                    processImage(originalBitmap);
                } else {
                    Toast.makeText(SendPWActivity.this, "Pas de photo séléctionnée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        step2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (originalBitmap != null) {
                    step.setText("Step 2 : You calculate angles with respect to the vertical");
                    zoomClickCounter++;
                    selectedPoints.clear();

                    processImage(originalBitmap);

                } else {
                    Toast.makeText(SendPWActivity.this, "Pas de photo séléctionnée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        step3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (originalBitmap != null) {
                    step.setText("Step 3 : You calculate angles with respect to the vertical");
                    zoomClickCounter++;
                    selectedPoints.clear();
                    processImage(originalBitmap);
                } else {
                    Toast.makeText(SendPWActivity.this, "Pas de photo séléctionnée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayCalculationResultsPopup();

            }
        });


        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            // Limiter le zoom à une certaine plage
            mScaleFactor = Math.max(MIN_SCALE, Math.min(mScaleFactor, MAX_SCALE));

            // Mettre à jour la taille de l'image en fonction du facteur de zoom
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);

            // Verrouiller la sélection de points pendant le zoom
            isSelectingPointsLocked = true;

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            // Réinitialiser le verrouillage de la sélection de points après la fin du zoom
            isSelectingPointsLocked = false;
        }
    }


    // Méthode pour afficher les résultats dans un popup
    private void displayCalculationResultsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SendPWActivity.this);
        builder.setTitle("Résultats");

        // Display results for processImage
        StringBuilder resultsText = new StringBuilder();
        for (CalculationResult result : calculationResults) {
            resultsText.append("ID: ").append(result.getId()).append("\n");
            resultsText.append("Angle gauche v: ").append(result.getAngleGauche()).append(" degrés\n");
            resultsText.append("Angle droit v: ").append(result.getAngleDroit()).append(" degrés\n");
            resultsText.append("Angle d'intersection v: ").append(result.getIntersectionAngleDeg()).append(" degrés\n");
            resultsText.append("Résultat de symétrie v: ").append(result.getIssymetrical());
            resultsText.append("\n");

            if (result.getId() == 2) {
                // Add "Envoyer" button with OnClickListener
                builder.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (originalBitmap != null) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();

                            // Convertir les bytes de l'image en une chaîne Base64
                            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                        }

                        if (contoursBitmap != null) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            contoursBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();

                            // Convertir les bytes de l'image en une chaîne Base64
                            encodedImage1 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                        }

                        createPw(studentId, pwdId, new StudentPW(encodedImage, calculationResultshorizontal.get(0).getAngleGaucheHorizontal(), calculationResultshorizontal.get(0).getAngleDroitHorizontal(), calculationResults.get(0).getAngleGauche(), calculationResults.get(0).getAngleDroit(), calculationResults.get(1).getAngleGauche(), calculationResults.get(1).getAngleDroit(), calculationResults.get(0).getIntersectionAngleDeg(), 0, encodedImage1));
                        Toast.makeText(getApplicationContext(), "TP envoyé avec succes", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

        // Display results for processImageHorizontal
        for (CalculationResultHorizontal resultt : calculationResultshorizontal) {
            resultsText.append("ID: ").append(resultt.getId()).append("\n");
            resultsText.append("Angle gauche h: ").append(resultt.getAngleGaucheHorizontal()).append(" degrés\n");
            resultsText.append("Angle droit h: ").append(resultt.getAngleDroitHorizontal()).append(" degrés\n");
            resultsText.append("\n");
        }

        builder.setMessage(resultsText.toString());


        builder.setNegativeButton("OK", null); // Button OK to close the dialog
        builder.create().show(); // Show the dialog
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                Bitmap photoBitmap = (Bitmap) extras.get("data");
                if (photoBitmap != null) {
                    // Initialisez originalBitmap avec le bitmap capturé
                    originalBitmap = photoBitmap;
                    // Traitez la photo capturée de la même manière que vous le faites pour une image sélectionnée
                    processImage(originalBitmap);
                } else {
                    Toast.makeText(SendPWActivity.this, "Erreur lors de la capture de la photo", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                // Afficher l'image choisie dans l'ImageView sans traitement
                imageView.setImageBitmap(originalBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addAverageSizePoints(Mat dilatedImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilatedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            for (Point point : contour.toList()) {
                Imgproc.circle(dilatedImage, point, 3, new Scalar(255, 255, 0), -1);
            }
        }
    }

    private void selectPoints(Mat lines, Mat dilatedImage) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    double x = event.getX();
                    double y = event.getY();

                    double[] imagePoint = imageViewToImage(x, y);
                    Point touchedPoint = new Point(imagePoint[0], imagePoint[1]);

                    boolean isOnContour = isPointOnContour(touchedPoint, dilatedImage);

                    if (!isOnContour) {
                        Point closestContourPoint = findClosestContourPoint(touchedPoint, dilatedImage);

                        if (closestContourPoint != null && selectedPoints.size() < MAX_SELECTED_POINTS) {
                            selectedPoints.add(closestContourPoint);

                            if (selectedPoints.size() == MAX_SELECTED_POINTS) {
                                // Afficher le Toast une fois que 4 points ont été sélectionnés
                                Toast.makeText(SendPWActivity.this, "4 points sélectionnés", Toast.LENGTH_SHORT).show();
                            }
                            processImage(originalBitmap);
                        }
                    } else {
                        addAverageSizePoints(dilatedImage);
                        processImage(originalBitmap);
                    }
                }
                return true;
            }
        });
    }

    private void selectPointsH(Mat lines, Mat dilatedImage) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    double x = event.getX();
                    double y = event.getY();

                    double[] imagePoint = imageViewToImage(x, y);
                    Point touchedPoint = new Point(imagePoint[0], imagePoint[1]);

                    boolean isOnContour = isPointOnContour(touchedPoint, dilatedImage);

                    if (!isOnContour) {
                        Point closestContourPoint = findClosestContourPoint(touchedPoint, dilatedImage);

                        if (closestContourPoint != null && selectedPoints.size() < MAX_SELECTED_POINTS) {
                            selectedPoints.add(closestContourPoint);

                            if (selectedPoints.size() == MAX_SELECTED_POINTS) {
                                // Afficher le Toast une fois que 4 points ont été sélectionnés
                                Toast.makeText(SendPWActivity.this, "4 points sélectionnés", Toast.LENGTH_SHORT).show();
                            }
                            processImageHorizontal(originalBitmap);
                        }
                    } else {
                        addAverageSizePoints(dilatedImage);
                        processImageHorizontal(originalBitmap);
                    }
                }
                return true;
            }
        });
    }


    private boolean isPointOnContour(Point point, Mat dilatedImage) {
        int neighborhoodSize = 5;
        double[] pixelValue = dilatedImage.get((int) point.y, (int) point.x);

        for (int i = -neighborhoodSize; i <= neighborhoodSize; i++) {
            for (int j = -neighborhoodSize; j <= neighborhoodSize; j++) {
                double[] currentPixel = dilatedImage.get((int) point.y + i, (int) point.x + j);
                if (currentPixel[0] != pixelValue[0]) {
                    return true;
                }
            }
        }
        return false;
    }

    private Point findClosestContourPoint(Point touchPoint, Mat dilatedImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilatedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Point closestContourPoint = null;
        double minContourDistance = Double.MAX_VALUE;

        for (MatOfPoint contour : contours) {
            for (Point contourPoint : contour.toList()) {
                double distance = Math.sqrt(Math.pow(contourPoint.x - touchPoint.x, 2) + Math.pow(contourPoint.y - touchPoint.y, 2));

                if (distance < minContourDistance) {
                    minContourDistance = distance;
                    closestContourPoint = contourPoint;
                }
            }
        }

        return closestContourPoint;
    }

    // Méthode pour convertir les coordonnées de l'écran en coordonnées de l'image
    private double[] imageViewToImage(double x, double y) {
        double[] imagePoint = new double[2];
        if (imageView.getDrawable() != null) {
            int viewWidth = imageView.getWidth();
            int viewHeight = imageView.getHeight();
            int imageWidth = imageView.getDrawable().getIntrinsicWidth();
            int imageHeight = imageView.getDrawable().getIntrinsicHeight();

            double scaleX = (double) imageWidth / (double) viewWidth;
            double scaleY = (double) imageHeight / (double) viewHeight;

            imagePoint[0] = x * scaleX;
            imagePoint[1] = y * scaleY;
        }
        return imagePoint;
    }

    // Méthode pour dessiner les points sélectionnés en rouge
    private void drawSelectedPoints(Mat image, List<Point> points) {


        for (Point point : points) {

            Imgproc.circle(image, point, 4, new Scalar(255, 0, 0), -1);
        }
    }


    // Méthode pour trouver le point le plus proche parmi les lignes détectées
    private Point findClosestPoint(Point touchPoint, Mat lines) {
        double minDistance = Double.MAX_VALUE;
        Point closestPoint = new Point();

        for (int x = 0; x < lines.cols(); x++) {
            double[] vec = lines.get(0, x);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];

            double distance1 = Math.sqrt(Math.pow(x1 - touchPoint.x, 2) + Math.pow(y1 - touchPoint.y, 2));
            double distance2 = Math.sqrt(Math.pow(x2 - touchPoint.x, 2) + Math.pow(y2 - touchPoint.y, 2));

            if (distance1 < minDistance) {
                minDistance = distance1;
                closestPoint.set(new double[]{x1, y1});
            }

            if (distance2 < minDistance) {
                minDistance = distance2;
                closestPoint.set(new double[]{x2, y2});
            }
        }

        return closestPoint;
    }


    private void processImage(Bitmap bitmap) {


        if (isSelectingPointsLocked) {
            return; // Sortir de la méthode si la sélection de points est verrouillée (pendant le zoom)
        }
        // Convertir le Bitmap en Mat
        Mat imageMat = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);

        // Convertir l'image en niveaux de gris
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un flou gaussien
        Imgproc.GaussianBlur(imageMat, imageMat, new Size(5, 5), 0);

        // Appliquer la détection de bord de Canny
        Imgproc.Canny(imageMat, imageMat, 50, 150);

        // Appliquer une dilatation
        Mat dilatedImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(imageMat, dilatedImage, kernel);

        // Utiliser la transformation de Hough pour détecter les lignes
        Mat lines = new Mat();
        Imgproc.HoughLinesP(dilatedImage, lines, 1, Math.PI / 180, 50, 50, 10);

        // Appel correct à selectPoints() avec les deux arguments
        selectPoints(lines, dilatedImage);

        // Dessiner les contours sur une nouvelle image
        Mat contoursImage = new Mat(imageMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        Imgproc.cvtColor(imageMat, contoursImage, Imgproc.COLOR_GRAY2BGR);

        // Dessiner les points sélectionnés en rouge
        drawSelectedPoints(contoursImage, selectedPoints);

        // Vérification que nous avons sélectionné au moins 4 points
        if (selectedPoints.size() >= 4) {
            // Première droite passant par les deux premiers points (index 0 et 1)
            Point p1 = selectedPoints.get(0);
            Point p2 = selectedPoints.get(1);

            // Deuxième droite passant par les deux derniers points (index 2 et 3)
            Point p3 = selectedPoints.get(2);
            Point p4 = selectedPoints.get(3);
            double extendLength = 1000.0; // Longueur de l'extension des droites

            // Pour la première droite
            Point extensionP1 = new Point(p1.x - extendLength, p1.y - extendLength * ((p2.y - p1.y) / (p2.x - p1.x)));
            Point extensionP2 = new Point(p2.x + extendLength, p2.y + extendLength * ((p2.y - p1.y) / (p2.x - p1.x)));

            // Pour la deuxième droite
            Point extensionP3 = new Point(p3.x - extendLength, p3.y - extendLength * ((p4.y - p3.y) / (p4.x - p3.x)));
            Point extensionP4 = new Point(p4.x + extendLength, p4.y + extendLength * ((p4.y - p3.y) / (p4.x - p3.x)));

            // Tri des points par coordonnées x
            Collections.sort(selectedPoints, new Comparator<Point>() {
                @Override
                public int compare(Point p1, Point p2) {
                    return Double.compare(p1.x, p2.x);
                }
            });


            // Recherche du point le plus à gauche et du point le plus à droite parmi les quatre points sélectionnés
            Point leftMostPoint = selectedPoints.get(0);
            Point rightMostPoint = selectedPoints.get(0);

            for (Point point : selectedPoints) {
                if (point.x < leftMostPoint.x) {
                    leftMostPoint = point;
                }
                if (point.x > rightMostPoint.x) {
                    rightMostPoint = point;
                }
            }

            // Dessiner les lignes verticales passant par les points les plus à gauche et à droite
            Imgproc.line(contoursImage, new Point(leftMostPoint.x, 0), new Point(leftMostPoint.x, contoursImage.rows()), new Scalar(255, 0, 0), 2);
            Imgproc.line(contoursImage, new Point(rightMostPoint.x, 0), new Point(rightMostPoint.x, contoursImage.rows()), new Scalar(255, 0, 0), 2);


            // Calcul de l'intersection des lignes définies par extensionP1-extensionP2 et extensionP3-extensionP4
            Point intersectionPoint = computeIntersection(extensionP1, extensionP2, extensionP3, extensionP4);

            // Dessiner les segments limités par leftMostPoint, rightMostPoint, et l'intersection
            Imgproc.line(contoursImage, leftMostPoint, intersectionPoint, new Scalar(0, 255, 0), 2);
            Imgproc.line(contoursImage, rightMostPoint, intersectionPoint, new Scalar(0, 255, 0), 2);


            // Calcul des angles gauche et droit (angle de dépouille)
            double deltaX = Math.abs(selectedPoints.get(0).x - selectedPoints.get(1).x);
            double deltaY = Math.abs(selectedPoints.get(0).y - selectedPoints.get(1).y);
            double taperAngleRad = Math.atan(deltaY / deltaX);
            double taperAngleDeg = Math.toDegrees(taperAngleRad);
            double angleGauche = 90 - taperAngleDeg;

            double deltaX2 = Math.abs(selectedPoints.get(2).x - selectedPoints.get(3).x);
            double deltaY2 = Math.abs(selectedPoints.get(2).y - selectedPoints.get(3).y);
            double taperAngleRad2 = Math.atan(deltaY2 / deltaX2);
            double taperAngleDeg2 = Math.toDegrees(taperAngleRad2);
            double angleDroit = 90 - taperAngleDeg2;


            // Calcul de l'angle formé par l'intersection des deux droites vertes
            // Équation des droites : y = mx + c, où m est la pente et c est l'ordonnée à l'origine

            // Calcul de la pente (m) des deux droites vertes
            double m1 = (extensionP2.y - extensionP1.y) / (extensionP2.x - extensionP1.x);
            double m2 = (extensionP4.y - extensionP3.y) / (extensionP4.x - extensionP3.x);

            // Calcul de l'angle entre les deux droites (en radians)
            double intersectionAngleRad = Math.atan(Math.abs((m2 - m1) / (1 + m1 * m2)));

            // Conversion de l'angle en degrés
            double intersectionAngleDeg = Math.toDegrees(intersectionAngleRad);

            // Affichage de l'angle d'intersection dans le TextView
            //anglesTextView.append("Angle d'intersection : " + intersectionAngleDeg + " degrés\n");


            // Appeler la méthode pour évaluer la symétrie
            // evaluateSymmetry(angleGauche, angleDroit);

            isProcessImageCalledvertical = true;
            calculationResults.add(new CalculationResult(angleGauche, angleDroit, intersectionAngleDeg, evaluateSymmetry(angleGauche, angleDroit)));
            //isProcessImageCalledvertical = false;

        }
        // Convertir le Mat avec les contours en Bitmap
        contoursBitmap = Bitmap.createBitmap(contoursImage.cols(), contoursImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(contoursImage, contoursBitmap);

        // Afficher l'image avec les contours dans l'ImageView
        imageView.setImageBitmap(contoursBitmap);

        // Réinitialiser le facteur de zoom à la valeur par défaut après le chargement de la nouvelle image
        mScaleFactor = DEFAULT_SCALE;
        imageView.setScaleX(mScaleFactor);
        imageView.setScaleY(mScaleFactor);

    }

    private Point calculateIntersectionHorizontal(Point line1Start, Point line1End, Point line2Start, Point line2End) {
        double x1 = line1Start.x;
        double y1 = line1Start.y;
        double x2 = line1End.x;
        double y2 = line1End.y;
        double x3 = line2Start.x;
        double y3 = line2Start.y;
        double x4 = line2End.x;
        double y4 = line2End.y;
        double determinant = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (determinant == 0) {
            // Les lignes sont parallèles ou confondues, pas de point d'intersection
            return null;
        } else {
            double intersectionX = (((x1 * y2) - (y1 * x2)) * (x3 - x4) - (x1 - x2) * ((x3 * y4) - (y3 * x4))) / determinant;
            double intersectionY = (((x1 * y2) - (y1 * x2)) * (y3 - y4) - (y1 - y2) * ((x3 * y4) - (y3 * x4))) / determinant;

            return new Point(intersectionX, intersectionY);
        }
    }

    private void processImageHorizontal(Bitmap bitmap) {


        if (isSelectingPointsLocked) {
            return; // Sortir de la méthode si la sélection de points est verrouillée (pendant le zoom)
        }
        // Convertir le Bitmap en Mat
        Mat imageMat = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);

        // Convertir l'image en niveaux de gris
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un flou gaussien
        Imgproc.GaussianBlur(imageMat, imageMat, new Size(5, 5), 0);

        // Appliquer la détection de bord de Canny
        Imgproc.Canny(imageMat, imageMat, 50, 150);

        // Appliquer une dilatation
        Mat dilatedImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(imageMat, dilatedImage, kernel);

        // Utiliser la transformation de Hough pour détecter les lignes
        Mat lines = new Mat();
        Imgproc.HoughLinesP(dilatedImage, lines, 1, Math.PI / 180, 50, 50, 10);

        // Appel correct à selectPoints() avec les deux arguments
        selectPointsH(lines, dilatedImage);

        // Dessiner les contours sur une nouvelle image
        Mat contoursImage = new Mat(imageMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        Imgproc.cvtColor(imageMat, contoursImage, Imgproc.COLOR_GRAY2BGR);


        // Dessiner les points sélectionnés en rouge
        drawSelectedPoints(contoursImage, selectedPoints);

        // Vérification que nous avons sélectionné au moins 4 points
        if (selectedPoints.size() >= 4) {
            // Première droite passant par les deux premiers points (index 0 et 1)
            Point p1 = selectedPoints.get(0);
            Point p2 = selectedPoints.get(1);

            // Deuxième droite passant par les deux derniers points (index 2 et 3)
            Point p3 = selectedPoints.get(2);
            Point p4 = selectedPoints.get(3);
            double extendLength = 1000.0; // Longueur de l'extension des droites

            // Pour la première droite
            Point extensionP1 = new Point(p1.x - extendLength, p1.y - extendLength * ((p2.y - p1.y) / (p2.x - p1.x)));
            Point extensionP2 = new Point(p2.x + extendLength, p2.y + extendLength * ((p2.y - p1.y) / (p2.x - p1.x)));

            // Pour la deuxième droite
            Point extensionP3 = new Point(p3.x - extendLength, p3.y - extendLength * ((p4.y - p3.y) / (p4.x - p3.x)));
            Point extensionP4 = new Point(p4.x + extendLength, p4.y + extendLength * ((p4.y - p3.y) / (p4.x - p3.x)));

            // Tri des points par coordonnées x
            Collections.sort(selectedPoints, new Comparator<Point>() {
                @Override
                public int compare(Point p1, Point p2) {
                    return Double.compare(p1.x, p2.x);
                }
            });


            // Recherche du point le plus à gauche et du point le plus à droite parmi les quatre points sélectionnés
            Point leftMostPoint = selectedPoints.get(0);
            Point rightMostPoint = selectedPoints.get(0);

            for (Point point : selectedPoints) {
                if (point.x < leftMostPoint.x) {
                    leftMostPoint = point;
                }
                if (point.x > rightMostPoint.x) {
                    rightMostPoint = point;
                }
            }

            Imgproc.line(contoursImage, leftMostPoint, rightMostPoint, new Scalar(0, 0, 255), 2);
            Point intersectionPoint = calculateIntersectionHorizontal(extensionP1, extensionP2, extensionP3, extensionP4);
            if (intersectionPoint != null) {
                // Dessiner des segments limités par leftMostPoint, rightMostPoint et le point d'intersection
                Imgproc.line(contoursImage, leftMostPoint, intersectionPoint, new Scalar(255, 0, 0), 2);
                Imgproc.line(contoursImage, rightMostPoint, intersectionPoint, new Scalar(255, 0, 0), 2);
            }
            // Calcul des angles avec la ligne horizontale
            double angleGaucheAvecLigne = calculateAngleWithHorizontalLine(leftMostPoint, rightMostPoint, selectedPoints.get(0), selectedPoints.get(1));
            double angleDroitAvecLigne = calculateAngleWithHorizontalLine(leftMostPoint, rightMostPoint, selectedPoints.get(2), selectedPoints.get(3));

            Log.d("angle1", angleGaucheAvecLigne + "");
            Log.d("angle2", angleDroitAvecLigne + "");

            isProcessImageCalledhorizontal = true;
            calculationResultshorizontal.add(new CalculationResultHorizontal(angleGaucheAvecLigne, angleDroitAvecLigne));
            //calculationResults.add(new CalculationResult(angleGaucheAvecLigne, angleDroitAvecLigne));
            //isProcessImageCalledhorizontal = false;

        }


        // Convertir le Mat avec les contours en Bitmap
        contoursBitmap = Bitmap.createBitmap(contoursImage.cols(), contoursImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(contoursImage, contoursBitmap);

        // Afficher l'image avec les contours dans l'ImageView
        imageView.setImageBitmap(contoursBitmap);

        // Réinitialiser le facteur de zoom à la valeur par défaut après le chargement de la nouvelle image
        mScaleFactor = DEFAULT_SCALE;
        imageView.setScaleX(mScaleFactor);
        imageView.setScaleY(mScaleFactor);

    }

    private double calculateAngleWithHorizontalLine(Point lineStart, Point lineEnd, Point point1, Point point2) {
        double mLine = (lineEnd.y - lineStart.y) / (lineEnd.x - lineStart.x);
        double mSegment = (point2.y - point1.y) / (point2.x - point1.x);

        // Calcul de l'angle entre la ligne horizontale et le segment
        double angleRad = Math.atan(Math.abs((mSegment - mLine) / (1 + mLine * mSegment)));

        // Conversion de l'angle en degrés
        return Math.toDegrees(angleRad);
    }

    private String evaluateSymmetry(double angleGauche, double angleDroit) {
        // Seuil prédéfini pour la différence d'angles pour détecter l'asymétrie
        final double seuilDifferenceAngles = 5.0; // Vous pouvez ajuster ce seuil selon votre besoin

        String r;

        // Calcul de la différence entre les angles gauche et droit
        double differenceAngles = Math.abs(angleGauche - angleDroit);

        // Vérification de la différence par rapport au seuil prédéfini
        if (differenceAngles > seuilDifferenceAngles) {
            // Indiquer une possible asymétrie
            r = "NOT Symmetrical";
        } else {
            // Les angles sont symétriques ou la différence est faible
            r = "Symmetrical";
        }
        return r;
    }

    // Méthode pour calculer l'intersection de deux lignes
    private Point computeIntersection(Point p1, Point p2, Point p3, Point p4) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        double x3 = p3.x, y3 = p3.y;
        double x4 = p4.x, y4 = p4.y;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d == 0) {
            return new Point(-1, -1); // Aucune intersection
        } else {
            double x = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            double y = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
            return new Point(x, y);
        }
    }

    public void createPw(long s, long d, StudentPW studentPW) {

        StudentApi angleRetrofit = RetrofitStudent.getClient().create(StudentApi.class);

        Call<StudentPW> call = angleRetrofit.create(s, d, s, d, studentPW);
        call.enqueue(new Callback<StudentPW>() {
            @Override
            public void onResponse(Call<StudentPW> call, Response<StudentPW> response) {
                Log.d("response", response.toString());
            }

            @Override
            public void onFailure(Call<StudentPW> call, Throwable t) {
                Log.d("err", t.toString());

            }
        });

    }

    // Override onDestroy to release OpenCV resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (originalMat != null) {
            originalMat.release();
        }
    }
}