package algorithms;

/**
 * Created by Tomek on 2017-05-27.
 */
public class Point {
    private double xCoordinate;
    private double yCoordinate;
    private int[] features;
    private int neighbourIndex;
    private double distance;

    public Point(double xCoordinate, double yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.features = new int[128];
    }

    public Point(double xCoordinate, double yCoordinate, int[] features) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.features = features;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int[] getFeatures() {
        return features;
    }

    public void setFeatures(int[] features) {
        this.features = features;
    }

    public int getNeighbourIndex() {
        return neighbourIndex;
    }

    public void setNeighbourIndex(int neighbourIndex) {
        this.neighbourIndex = neighbourIndex;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
