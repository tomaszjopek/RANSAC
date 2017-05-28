package algorithms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Tomek on 2017-05-27.
 */
public class Reader {

    public static List<Point> readImage(String path) {
        List<Point> list = new ArrayList<>();

        try(Stream<String> stream = Files.lines(Paths.get(path))) {
            stream.forEach(line ->  {
                String array[] = line.split(" ");
                double xCor = Double.parseDouble(array[0]);
                double yCor = Double.parseDouble(array[1]);

                int[] tmpArr = new int[128];

                for(int i = 5; i < array.length; i++) {
                    tmpArr[i-5] = Integer.parseInt(array[i]);
                }

                Point tmpPoint = new Point(xCor, yCor, tmpArr);
                list.add(tmpPoint);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
