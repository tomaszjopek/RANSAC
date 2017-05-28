package algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

/**
 * Created by Tomek on 2017-05-27.
 */
public class MyImage {
    private List<Point> points;

    public MyImage() {
        points = new ArrayList<>();
    }

    public MyImage(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Pair> getPairs(MyImage image) {
        Point[] image1 = points.toArray(new Point[points.size()]);
        Point[] image2 = image.getPoints().toArray(new Point[image.getPoints().size()]);

        double bestDistance, currentDistance;
        int neighbourIndex;

        for (Point anImage1 : image1) {
            bestDistance = Double.MAX_VALUE;
            neighbourIndex = 0;

            for (int j = 0; j < image2.length; j++) {
                currentDistance = euclideanDistance(anImage1, image2[j]);
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                    neighbourIndex = j;
                }
            }

            anImage1.setNeighbourIndex(neighbourIndex);
        }

        for (Point anImage2 : image2) {
            bestDistance = Double.MAX_VALUE;
            neighbourIndex = 0;

            for (int j = 0; j < image1.length; j++) {
                currentDistance = euclideanDistance(anImage2, image1[j]);
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                    neighbourIndex = j;
                }
            }

            anImage2.setNeighbourIndex(neighbourIndex);
        }

        List<Pair> pairs = new LinkedList<>();

        int length = image1.length > image2.length ? image2.length : image1.length;

        for(int i = 0; i < length; i++) {
            int index = image1[i].getNeighbourIndex();
            if(i == image2[index].getNeighbourIndex()) {
                Pair tmpPair = new Pair(image1[i], image2[index]);
                pairs.add(0, tmpPair);
            }
        }

        return pairs;
    }

    private double euclideanDistance(Point fPoint, Point sPoint) {
        double value = 0.0;

        for(int i = 0; i < 128; i++) {
            value += pow(fPoint.getFeatures()[i] - sPoint.getFeatures()[i], 2);
        }

        return sqrt(value);
    }
}
