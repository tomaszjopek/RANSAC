package algorithms;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

/**
 * Created by Tomek on 2017-05-27.
 */
public class Neighbourhood {

    private List<Pair> basicList;
    private List<Pair> filteredPairs;
    private int divide = 1;

    public Neighbourhood(List<Pair> basicList) {
        this.basicList = basicList;
        filteredPairs = new ArrayList<>();
    }

    public Neighbourhood(List<Pair> basicList, int divide) {
        this.basicList = basicList;
        this.divide = divide;
    }

    public List<Pair> getConsistanceNeighbours(int number) {

        return basicList
                .stream()
                .filter(pair -> checkNeighbours(pair.getFirstPoint(), pair.getSecondPoint(), number))
                .collect(Collectors.toList());
    }

    private boolean checkNeighbours(Point point1, Point point2, int number)
    {
        Comparator<Point> comparator = (o1, o2) -> {
          if(o1.getDistance() > o2.getDistance())
              return 1;
          else if(o1.getDistance() == o2.getDistance())
              return 0;
          else
              return -1;
        };

        basicList.forEach(pair -> pair.getFirstPoint().setDistance(euclideanDistance(point1, pair.getFirstPoint())));
        List<Point> p1 = basicList
                .stream()
                .filter(pair -> pair.getFirstPoint().getDistance() != 0)
                .map(Pair::getFirstPoint)
                .sorted(comparator)
                .collect(Collectors.toList());

        basicList.forEach(pair -> pair.getSecondPoint().setDistance(euclideanDistance(point2, pair.getSecondPoint())));
        List<Point> p2 = basicList
                .stream()
                .filter(pair -> pair.getSecondPoint().getDistance() != 0)
                .map(Pair::getSecondPoint)
                .sorted(comparator)
                .collect(Collectors.toList());


        Cohesion cohesion = new Cohesion();

        p1.stream().limit(number).forEach(point -> {
            int key = point.getNeighbourIndex();
            Point tmpPoint = basicList.get(key).getSecondPoint();

            p2.stream().limit(number).forEach(point3 -> {
                if(point3 == tmpPoint)
                    cohesion.increment();
            });
        });

        return cohesion.getCohesion() > number/divide;
    }

    private double euclideanDistance(Point p1, Point p2) {

        double value = pow(p1.getxCoordinate() - p2.getxCoordinate(), 2) + pow(p1.getyCoordinate() - p2.getyCoordinate(), 2);

        return sqrt(value);
    }

    public List<Pair> getBasicList() {
        return basicList;
    }

    public void setBasicList(List<Pair> basicList) {
        this.basicList = basicList;
    }

    public List<Pair> getFilteredPairs() {
        return filteredPairs;
    }

    public void setFilteredPairs(List<Pair> filteredPairs) {
        this.filteredPairs = filteredPairs;
    }

    private class Cohesion {
        int cohesion;

        Cohesion() {
            cohesion = 0;
        }

        public Cohesion(int cohesion) {
            this.cohesion = cohesion;
        }

        void increment() {
            cohesion++;
        }

        int getCohesion() {
            return cohesion;
        }

        void setCohesion(int cohesion) {
            this.cohesion = cohesion;
        }
    }

}
