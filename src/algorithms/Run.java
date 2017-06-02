package algorithms;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * Created by Tomek on 2017-05-26.
 */
public class Run {
    private static final String path1 = "E:\\workspace\\SI\\RANSAC\\res\\images\\chobieniaone.png.haraff.sift";
    private static final String path2 = "E:\\workspace\\SI\\RANSAC\\res\\images\\chobieniasec.png.haraff.sift";

    public static void main(String[] args) {
        List<Point> listOne = Reader.readImage(path1);
        List<Point> listTwo = Reader.readImage(path2);

        MyImage fImage = new MyImage(listOne);
        MyImage sImage = new MyImage(listTwo);

        List<Pair> pairs = fImage.getPairs(sImage);
        Neighbourhood neighbourhood = new Neighbourhood(pairs, 5);
        List<Pair> consistancePairs = neighbourhood.getConsistanceNeighbours(200);

        assert consistancePairs.size() != 0 : "Pusty zbior par spojnych";

        ImageIcon iconOne = new ImageIcon("E:\\workspace\\SI\\RANSAC\\res\\images\\chobieniaone.png");
        ImageIcon iconTwo = new ImageIcon("E:\\workspace\\SI\\RANSAC\\res\\images\\chobieniasec.png");
        Image image1 = iconOne.getImage();
        Image image2 = iconTwo.getImage();

        int w = image1.getWidth(iconOne.getImageObserver()) + image2.getWidth(iconTwo.getImageObserver());
        int h = Math.max(iconOne.getIconHeight(), iconTwo.getIconHeight());
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(image1, 0, 0, null);
        graphics2D.drawImage(image2, image1.getWidth(null), 0, null);

        Random rand = new Random();
        Color color;

        for(Pair pair : consistancePairs) {
            Shape shape = new Line2D.Double(
                    pair.getFirstPoint().getxCoordinate(),
                    pair.getFirstPoint().getyCoordinate(),
                    pair.getSecondPoint().getxCoordinate() + image1.getWidth(null),
                    pair.getSecondPoint().getyCoordinate()
            );

            color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256),rand.nextInt(256));
            graphics2D.setColor(color);
            graphics2D.draw(shape);
        }

        graphics2D.dispose();
        ImageIcon myIcon = new ImageIcon(image);

        JFrame frame = new JFrame();
        JLabel label = new JLabel(myIcon);
        frame.add(label);
        frame.setTitle("Pairs of key points");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
