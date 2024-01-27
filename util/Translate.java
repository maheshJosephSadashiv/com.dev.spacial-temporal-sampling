package util;

public class Translate {

    public static Coordinates coordinateSys(Coordinates coordinates, int height, int width){
        double Sx = width/2 - coordinates.getxCoordinate();
        double Sy = height/2 - coordinates.getyCoordinate();
        return new Coordinates(Sx, Sy);
    }
    public static Coordinates coordinatePixel(Coordinates coordinates, int height, int width){
        double Sx = coordinates.getxCoordinate() +  width/2 ;
        double Sy = height/2 + coordinates.getyCoordinate();
        return new Coordinates(Sx, Sy);
    }

}
