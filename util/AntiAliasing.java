package util;

import java.awt.image.BufferedImage;

public class AntiAliasing {

    private static final int[] neighbours = {-1, 0, 1};

    public static void antialiasingForRotation(Coordinates coordinates, int pix, BufferedImage img, int width, int height){
        int xCiel = (int) Math.ceil(coordinates.getxCoordinate());
        int yCiel = (int) Math.ceil(coordinates.getyCoordinate());
        for (int i: neighbours) {
            for(int j : neighbours){
                if ( xCiel + i < width && yCiel + j < height && xCiel + i > -1 && yCiel + j > -1){
                    img.setRGB(xCiel + i, yCiel + j, pix);
                }
            }
        }
    }
}
