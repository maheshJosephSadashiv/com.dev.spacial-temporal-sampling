
import util.Coordinates;
import util.Translate;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;

	// Modify the height and width values here to read and display an image with
  	// different dimensions. 
	int width = 512;
	int height = 512;

	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
	{
		int inputAngle = 45;
		int angle = 180 + inputAngle;
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
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);

					Coordinates translated = Translate.coordinateSys(new Coordinates(x, y), height, width);
					double[][] rotated = util.MatrixUtil.rotation(angle, translated.getxCoordinate(), translated.getyCoordinate());
					translated = Translate.coordinatePixel(new Coordinates(rotated[0][0], rotated[1][0]), height, width);

					if (!(translated.getxCoordinate() >= height || translated.getyCoordinate() >= width
							|| translated.getxCoordinate() < 0 || translated.getyCoordinate() < 0)){
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						antialiasing(translated, pix, img);
					}
					ind++;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void showIms(String[] args){

		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, "Lena_512_512.rgb", imgOne);

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);
		frame.pack();
		frame.setVisible(true);
	}


	private void antialiasing(Coordinates coordinates, int pix, BufferedImage img){
		int xCiel = (int) Math.ceil(coordinates.getxCoordinate());
		int yCiel = (int) Math.ceil(coordinates.getyCoordinate());
		if(xCiel < width && yCiel < height){
			img.setRGB(xCiel, yCiel, pix);
		}
		int xFloor = (int) Math.floor(coordinates.getxCoordinate());
		int yFloor = (int) Math.floor(coordinates.getyCoordinate());
		if(xFloor >= 0 && yFloor >= 0){
			img.setRGB(xFloor, yFloor, pix);
		}
	}
	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
