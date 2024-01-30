import util.Coordinates;
import util.Translate;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 512;
	int height = 512;
	private long inputAngle = 0;
	private double scale = 1;
	int[][] originalPixelMatrix = new int[height][width];
	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath)
	{

		try
		{
			int frameLength = width*height*3;
			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);
			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2];
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					originalPixelMatrix[x][y] = pix;
					ind++;
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private void animate() throws Exception {
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				Coordinates translated = Translate.coordinateSys(new Coordinates(x, y), height, width);
				double[][] rotated = util.MatrixUtil.rotationAndScale(inputAngle + 180, translated.getxCoordinate(), translated.getyCoordinate(), scale);
				translated = Translate.coordinatePixel(new Coordinates(rotated[0][0], rotated[1][0]), height, width);
				if (!(translated.getxCoordinate() >= height || translated.getyCoordinate() >= width
						|| translated.getxCoordinate() < 0 || translated.getyCoordinate() < 0)){
					int pix = originalPixelMatrix[(int) translated.getxCoordinate()][(int) translated.getyCoordinate()];
					imgOne.setRGB(x, y, pix);
				} else{
					imgOne.setRGB(x, y, Integer.MAX_VALUE);
				}
			}
		}
	}

	public void showIms(String[] args) throws Exception {

		// Read in the specified image
		readImageRGB(width, height, "Lena_512_512.rgb");

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		run();
	}
	public void run() throws Exception {

		lbIm1 = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);
		frame.setPreferredSize(new Dimension(width, height));
		frame.pack();
		frame.setVisible(true);

		while (true) {
			// Display current image
			inputAngle = 0;
			scale = 5;
			animate();
			lbIm1.setIcon(new ImageIcon(imgOne));
			System.out.println(inputAngle);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		try {
			ren.showIms(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
