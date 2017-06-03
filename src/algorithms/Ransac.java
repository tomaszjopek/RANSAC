package algorithms;

import org.ejml.simple.SimpleMatrix;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by Tomek on 2017-06-02.
 */
public class Ransac {

    private static final int SAMPLES_COUNT_AFFINE_TRANSFORMATION = 3;
    private static final int SAMPLES_COUNT_PERSPECTIVE_TRANSFORMATION = 4;

    private List<Pair> basicPairs;

    public Ransac(List<Pair> basicPairs) {
        this.basicPairs = basicPairs;
    }

    public List<Pair> getfilteredPairs(String method, int iterations, double maxError) {
        if(method.toLowerCase().trim().equals("affine")) {
            SimpleMatrix bestModel = getBestModelAffine(iterations, maxError);

            return basicPairs
                    .stream()
                    .filter(pair -> countError(bestModel, pair.getFirstPoint(), pair.getSecondPoint()) < maxError)
                    .collect(Collectors.toList());
        }
        else {
            SimpleMatrix bestModel = getBestModelPerspective(iterations, maxError);

            return basicPairs
                    .stream()
                    .filter(pair -> countError(bestModel, pair.getFirstPoint(), pair.getSecondPoint()) < maxError)
                    .collect(Collectors.toList());
        }
    }

    private SimpleMatrix getBestModelAffine(int iterations, double maxError) {
        final SimpleMatrix[] bestModel = new SimpleMatrix[1];
        final int[] bestScore = {0};

        IntStream.range(0, iterations).forEach(value -> {
            SimpleMatrix model = null;

            while(model == null) {
                Collections.shuffle(basicPairs);
                List<Pair> selectedSubSet = basicPairs.subList(0, SAMPLES_COUNT_AFFINE_TRANSFORMATION);
                model = calculateModelAffineTransform(selectedSubSet);
            }

            Scores score = new Scores();
            SimpleMatrix tmp = model;

            basicPairs.forEach(pair -> {
                double error = countError(tmp, pair.getFirstPoint(), pair.getSecondPoint());
                pair.getSecondPoint().setDistance(error);
                if(error < maxError) {
                    score.increment();
                }
            });

            if(score.score > bestScore[0]) {
                bestScore[0] = score.score;
                bestModel[0] = model;
            }

        });

        return bestModel[0];
    }

    private SimpleMatrix getBestModelPerspective(int iterations, double maxError) {
        final SimpleMatrix[] bestModel = new SimpleMatrix[1];
        final int[] bestScore = {0};

        IntStream.range(0, iterations).forEach(value -> {
            SimpleMatrix model = null;

            while(model == null) {
                Collections.shuffle(basicPairs);
                List<Pair> selectedSubSet = basicPairs.subList(0, SAMPLES_COUNT_PERSPECTIVE_TRANSFORMATION);
                model = calculateModelPerspectiveTransformation(selectedSubSet);
            }

            Scores score = new Scores();
            SimpleMatrix tmp = model;

            basicPairs.forEach(pair -> {
                double error = countError(tmp, pair.getFirstPoint(), pair.getSecondPoint());
                pair.getSecondPoint().setDistance(error);
                if(error < maxError) {
                    score.increment();
                }
            });

            if(score.score > bestScore[0]) {
                bestScore[0] = score.score;
                bestModel[0] = model;
            }

        });

        return bestModel[0];
    }

    private double countError(SimpleMatrix tmp, Point firstPoint, Point secondPoint) {
        SimpleMatrix pointVector = new SimpleMatrix(new double[][]{{firstPoint.getxCoordinate()}, {firstPoint.getyCoordinate()}, {0}});

        SimpleMatrix transformation = tmp.mult(pointVector);

        double value = pow(transformation.get(1, 0) - secondPoint.getxCoordinate() + transformation.get(2, 0) - secondPoint.getyCoordinate() , 2);

        return sqrt(value);
    }

    private SimpleMatrix calculateModelAffineTransform(List<Pair> selectedSubSet) {
        double[][] firstMatrix = new double[][]
                {
                        {selectedSubSet.get(0).getFirstPoint().getxCoordinate(),selectedSubSet.get(0).getFirstPoint().getyCoordinate(),1,0,0,0},
                        {selectedSubSet.get(1).getFirstPoint().getxCoordinate(),selectedSubSet.get(1).getFirstPoint().getyCoordinate(),1,0,0,0},
                        {selectedSubSet.get(2).getFirstPoint().getxCoordinate(),selectedSubSet.get(2).getFirstPoint().getyCoordinate(),1,0,0,0},
                        {0,0,0,selectedSubSet.get(0).getFirstPoint().getxCoordinate(),selectedSubSet.get(0).getFirstPoint().getyCoordinate(),1},
                        {0,0,0,selectedSubSet.get(1).getFirstPoint().getxCoordinate(),selectedSubSet.get(1).getFirstPoint().getyCoordinate(),1},
                        {0,0,0,selectedSubSet.get(2).getFirstPoint().getxCoordinate(),selectedSubSet.get(2).getFirstPoint().getyCoordinate(),1},
                };

        SimpleMatrix fMatrix = new SimpleMatrix(firstMatrix);

        fMatrix = fMatrix.invert();

        Point tmp = selectedSubSet.get(0).getSecondPoint();
        Point tmp1 = selectedSubSet.get(1).getSecondPoint();
        Point tmp2 = selectedSubSet.get(2).getSecondPoint();

        double[][] secondMatrix = new double[][]
                {
                        {tmp.getxCoordinate()},
                        {tmp1.getxCoordinate()},
                        {tmp2.getxCoordinate()},
                        {tmp.getyCoordinate()},
                        {tmp1.getyCoordinate()},
                        {tmp2.getyCoordinate()}
                };

        SimpleMatrix sMatrix = new SimpleMatrix(secondMatrix);

        SimpleMatrix resultMatrix = fMatrix.mult(sMatrix);

        double[][] affineTransform = new double[][]
                {
                        {resultMatrix.get(0,0), resultMatrix.get(1,0), resultMatrix.get(2,0)},
                        {resultMatrix.get(3,0), resultMatrix.get(4,0), resultMatrix.get(5,0)},
                        {0, 0, 1}
                };

        return new SimpleMatrix(affineTransform);
    }

    private SimpleMatrix calculateModelPerspectiveTransformation(List<Pair> selectedSubSet) {
        double[][] firstMatrix = new double[][]
                {
                        {selectedSubSet.get(0).getFirstPoint().getxCoordinate(),selectedSubSet.get(0).getFirstPoint().getyCoordinate(),1,0,0,0,(-1) * selectedSubSet.get(0).getSecondPoint().getxCoordinate() * selectedSubSet.get(0).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(0).getSecondPoint().getxCoordinate() * selectedSubSet.get(0).getFirstPoint().getyCoordinate()},
                        {selectedSubSet.get(1).getFirstPoint().getxCoordinate(),selectedSubSet.get(1).getFirstPoint().getyCoordinate(),1,0,0,0,(-1) * selectedSubSet.get(1).getSecondPoint().getxCoordinate() * selectedSubSet.get(1).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(1).getSecondPoint().getxCoordinate() * selectedSubSet.get(1).getFirstPoint().getyCoordinate()},
                        {selectedSubSet.get(2).getFirstPoint().getxCoordinate(),selectedSubSet.get(2).getFirstPoint().getyCoordinate(),1,0,0,0,(-1) * selectedSubSet.get(2).getSecondPoint().getxCoordinate() * selectedSubSet.get(2).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(2).getSecondPoint().getxCoordinate() * selectedSubSet.get(2).getFirstPoint().getyCoordinate()},
                        {selectedSubSet.get(3).getFirstPoint().getxCoordinate(),selectedSubSet.get(3).getFirstPoint().getyCoordinate(),1,0,0,0,(-1) * selectedSubSet.get(3).getSecondPoint().getxCoordinate() * selectedSubSet.get(3).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(3).getSecondPoint().getxCoordinate() * selectedSubSet.get(3).getFirstPoint().getyCoordinate()},
                        {0,0,0,selectedSubSet.get(0).getFirstPoint().getxCoordinate(),selectedSubSet.get(0).getFirstPoint().getyCoordinate(),1,(-1) * selectedSubSet.get(0).getSecondPoint().getyCoordinate() * selectedSubSet.get(0).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(0).getSecondPoint().getyCoordinate() * selectedSubSet.get(0).getFirstPoint().getyCoordinate()},
                        {0,0,0,selectedSubSet.get(1).getFirstPoint().getxCoordinate(),selectedSubSet.get(1).getFirstPoint().getyCoordinate(),1,(-1) * selectedSubSet.get(1).getSecondPoint().getyCoordinate() * selectedSubSet.get(1).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(1).getSecondPoint().getyCoordinate() * selectedSubSet.get(1).getFirstPoint().getyCoordinate()},
                        {0,0,0,selectedSubSet.get(2).getFirstPoint().getxCoordinate(),selectedSubSet.get(2).getFirstPoint().getyCoordinate(),1,(-1) * selectedSubSet.get(2).getSecondPoint().getyCoordinate() * selectedSubSet.get(2).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(2).getSecondPoint().getyCoordinate() * selectedSubSet.get(2).getFirstPoint().getyCoordinate()},
                        {0,0,0,selectedSubSet.get(3).getFirstPoint().getxCoordinate(),selectedSubSet.get(3).getFirstPoint().getyCoordinate(),1,(-1) * selectedSubSet.get(3).getSecondPoint().getyCoordinate() * selectedSubSet.get(3).getFirstPoint().getxCoordinate(),(-1) * selectedSubSet.get(3).getSecondPoint().getyCoordinate() * selectedSubSet.get(3).getFirstPoint().getyCoordinate()}
                };

        SimpleMatrix fMatrix = new SimpleMatrix(firstMatrix);
        fMatrix = fMatrix.invert();

        Point tmp = selectedSubSet.get(0).getSecondPoint();
        Point tmp1 = selectedSubSet.get(1).getSecondPoint();
        Point tmp2 = selectedSubSet.get(2).getSecondPoint();
        Point tmp3 = selectedSubSet.get(3).getSecondPoint();

        double[][] secondMatrix = new double[][]
                {
                        {tmp.getxCoordinate()},
                        {tmp1.getxCoordinate()},
                        {tmp2.getxCoordinate()},
                        {tmp3.getxCoordinate()},
                        {tmp.getyCoordinate()},
                        {tmp1.getyCoordinate()},
                        {tmp2.getyCoordinate()},
                        {tmp3.getyCoordinate()}
                };

        SimpleMatrix sMatrix = new SimpleMatrix(secondMatrix);

        SimpleMatrix resultMatrix = fMatrix.mult(sMatrix);

        double[][] perspectiveTransform = new double[][]
                {
                        {resultMatrix.get(0,0), resultMatrix.get(1,0), resultMatrix.get(2,0)},
                        {resultMatrix.get(3,0), resultMatrix.get(4,0), resultMatrix.get(5,0)},
                        {resultMatrix.get(6,0), resultMatrix.get(7,0), 1}
                };

        return new SimpleMatrix(perspectiveTransform);
    }


    public List<Pair> getBasicPairs() {
        return basicPairs;
    }

    public void setBasicPairs(List<Pair> basicPairs) {
        this.basicPairs = basicPairs;
    }

    private class Scores{
        int score;

        Scores() {
            score = 0;
        }

        void increment() {
            score++;
        }

        int getScore() {
            return score;
        }

        void setScore(int score) {
            this.score = score;
        }
    }
}
