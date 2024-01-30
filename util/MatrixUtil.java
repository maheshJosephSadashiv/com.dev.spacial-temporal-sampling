package util;

public class MatrixUtil {
    public static double[][] rotationAndScale(long angle, double x, double y, double size) throws Exception {
        double angleInRadians = Math.toRadians(-angle);
        double cos = roundAvoid(Math.cos(angleInRadians), 5);
        double sin = roundAvoid(Math.sin(angleInRadians), 5);
        return  new double[][]{{(x*cos - y*sin)/size},{(x*sin + y*cos)/size}};
    }

    public static double[][] multiply(double[][] firstMatrix, double[][] secondMatrix) throws Exception {
        if (firstMatrix[0].length != secondMatrix.length){
            throw new Exception("Sad stuff mate, you cannot multiply these matrices");
        }
        double[][] result = new double[firstMatrix.length][secondMatrix[0].length];
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyCell(firstMatrix, secondMatrix, row, col);
            }
        }
        return result;
    }

    private static double multiplyCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
