package util;

public class Translate {

    public static double[] coordinateSys(double[] coordinates, int height, int width){
        double Sx = width/2 - coordinates[0];
        double Sy = height/2 - coordinates[1];
        return new double[]{Sx, Sy};
    }
    public static double[] coordinatePixel(double[] coordinates, int height, int width){
        double Sx = coordinates[0] +  width/2 ;
        double Sy = height/2 + coordinates[1];
        return new double[]{Sx, Sy};
    }

}
