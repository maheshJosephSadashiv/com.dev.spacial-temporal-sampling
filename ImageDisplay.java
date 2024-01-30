
import util.AntiAliasing;
import util.Coordinates;
import util.Translate;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.Buffer;
import javax.swing.*;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;

	// Modify the height and width values here to read and display an image with
  	// different dimensions. 
	int width = 512;
	int height = 512;
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
			animate();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private void animate() throws Exception {
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int inputAngle = 45;
		int angle = 180 + inputAngle;
		double scale = 5;
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				Coordinates translated = Translate.coordinateSys(new Coordinates(x, y), height, width);
				double[][] rotated = util.MatrixUtil.rotationAndScale(angle, translated.getxCoordinate(), translated.getyCoordinate(), scale);
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

	public void showIms(String[] args){

		// Read in the specified image

		readImageRGB(width, height, "Lena_512_512.rgb");

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);
		frame.pack();
		frame.setVisible(true);
	}
//	public void run() {
//		frame.setVisible(true);
//
//		while (true) {
//			// Display current image
//			imageLabel.setIcon(new ImageIcon(images[currentImageIndex]));
//
//			// Update current image index
//			currentImageIndex = (currentImageIndex + 1) % images.length;
//
//			// Delay to control frame rate
//			try {
//				Thread.sleep(1000); // Example: 1 second delay
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
