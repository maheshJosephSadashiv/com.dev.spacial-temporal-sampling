package util;

public class AntiAliasing {

    public static int averagingFilter(double[] coordinates, int[][] pixelMatrix){
        int sumR = 0;
        int sumG = 0;
        int sumB = 0;
        int count = 0;
        int xOg = (int) Math.round(coordinates[0]);
        int yOg = (int) Math.round(coordinates[1]);
        for(int i = -2 ; i < 3; i++){
            for(int j = -2; j < 3 ; j++){
                int x =  xOg + i;
                int y =  yOg + j;
                if (x >= 0 && x < pixelMatrix.length && y >=0 && y < pixelMatrix.length){
                    sumR += (pixelMatrix[x][y] >> 16) & 0xFF;
                    sumG += (pixelMatrix[x][y] >> 8) & 0xFF;
                    sumB += pixelMatrix[x][y] & 0xFF;
                    count += 1;
                }
            }
        }
        sumR = sumR/count;
        sumG = sumG/count;
        sumB = sumB/count;
        return 0xff000000 | ((sumR & 0xff) << 16) | ((sumG & 0xff) << 8) | (sumB & 0xff);
    }
}
