import util.Coordinates;
import util.Translate;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
//	BufferedImage imgOne;
	int width = 512;
	int height = 512;
	private long inputAngle = 0;
	private double scale = 1;
	int[][] originalPixelMatrix = new int[height][width];

	class DoubleBuffering implements Callable<BufferedImage>{

		@Override
		public BufferedImage call() throws Exception {
			return animate();
		}
	}
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

	private BufferedImage animate() throws Exception {
		BufferedImage imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		System.out.println(inputAngle +" "+ scale);
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
		return imgOne;
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
		boolean isDouble = false;

		while (true) {
			if(!isDouble){

			}
			// Display current image
			Instant starts = Instant.now();
			inputAngle += 45;
			scale *= 1.01;
			// Creating a thread using the MyCallable instance
			ExecutorService executor = Executors.newFixedThreadPool(2);
			Future<BufferedImage> future = executor.submit(new DoubleBuffering());
			BufferedImage imgOne = future.get();
			inputAngle += 45;
			scale *= 1.01;
			Future<BufferedImage> future2 = executor.submit(new DoubleBuffering());
			executor.shutdown();
			Instant ends = Instant.now();
			System.out.println(TimeUnit.NANOSECONDS.toMillis(Duration.between(starts, ends).getNano()));
			lbIm1.setIcon(new ImageIcon(imgOne));
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BufferedImage imgOne2 = future2.get();
			lbIm1.setIcon(new ImageIcon(imgOne2));
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

	//TODO : antialiasing while zooming out, check with input as checker board pattern!!

}
