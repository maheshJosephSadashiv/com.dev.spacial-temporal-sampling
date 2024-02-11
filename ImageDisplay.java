import util.AntiAliasing;
import util.Translate;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	int width = 512;
	int height = 512;
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 600;
	private double INITIAL_ANGLE = 0;
	private double INITIAL_SCALE = 1;
	private Double inputAngle;
	private double inputScale;
	private int inputFrameRate;
	private Timer timer;
	boolean gameLoop = true;
	ExecutorService executor;
	Future<BufferedImage> future1 = null;
	Future<BufferedImage> future2 = null;
	Future<BufferedImage> future3 = null;
	int multiBuffer = 0;
	int[][] originalPixelMatrix = new int[height][width];

	class TripleBuffering implements Callable<BufferedImage>{
		double angle;
		double scale;
		public TripleBuffering(double angle, double scale){
			this.scale = scale;
			this.angle = angle;
		}
		@Override
		public BufferedImage call() throws Exception {
			return animate(this.angle, this.scale);
		}
	}
	private void readImageRGB(int width, int height, String imgPath)
	{

		try
		{
			int frameLength = width*height*3;
			File file = new File(imgPath);
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
			randomAccessFile.seek(0);
			byte[] bytes = new byte[frameLength];
			randomAccessFile.read(bytes);
			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2];
					originalPixelMatrix[x][y] = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					ind++;
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private BufferedImage animate(double angle, double scale) throws Exception {
		BufferedImage imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				double[] translated = Translate.coordinateSys(new double[]{x, y}, height, width);
				double[] rotated = util.MatrixUtil.rotationAndScale(angle + 180, translated[0], translated[1], scale);
				translated = Translate.coordinatePixel(new double[]{rotated[0], rotated[1]}, height, width);
				if (!(translated[0] >= height || translated[1] >= width
						|| translated[0] < 0 || translated[1] < 0)){
					int pix = 0;
					if(INITIAL_SCALE < 1) pix = AntiAliasing.averagingFilter(translated, originalPixelMatrix);
					else{
						int _x = (int)Math.round(translated[0]) < 512 ? (int)Math.round(translated[0]): 511;
						int _y = (int)Math.round(translated[1]) < 512 ? (int)Math.round(translated[1]): 511;
						pix = originalPixelMatrix[_x][_y];
					}
					imgOne.setRGB(x, y, pix);
				} else{
					imgOne.setRGB(x, y, Integer.MAX_VALUE);
				}
			}
		}
		return imgOne;
	}

	public void createFrame(String[] args) throws Exception {
		if(args.length != 4){
			throw new Exception("Invalid number of arguments");
		}
		String image = args[0];
		inputScale = Double.parseDouble(args[1]);
		inputAngle = Double.parseDouble(args[2]);
		inputFrameRate = Integer.parseInt(args[3]);
		readImageRGB(width, height, image);
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		lbIm1 = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		lbIm1.setHorizontalAlignment(JLabel.CENTER);
		frame.getContentPane().add(lbIm1, c);
		frame.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		frame.pack();
		frame.setVisible(true);
		executor = Executors.newFixedThreadPool(3);
		timer = new Timer(1000/inputFrameRate, e -> {
            try {
                run();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
		timer.start();
	}

	private void increment(){
		INITIAL_ANGLE += inputAngle/inputFrameRate;
		if(INITIAL_SCALE + (inputScale-1)/inputFrameRate >= 0) {
			INITIAL_SCALE += (inputScale - 1) / inputFrameRate;
		}
		else{
			INITIAL_SCALE = 0;
		}
	}

	/**
	 * @implNote The run method contains the game loop logic, to achieve higher frame rates
	 * I have created 3 threads to precalculate the transformations in advance.
	 */
	public void run() throws Exception {
		BufferedImage imgOne = null;
		switch(multiBuffer){
			case 0:
				if (future1 == null) {
					increment();
					future1 = executor.submit(new TripleBuffering(INITIAL_ANGLE, INITIAL_SCALE));
				}
				imgOne = future1.get();
				future1 = null;
				if(future2 == null) {
					increment();
					future2 = executor.submit(new TripleBuffering(INITIAL_ANGLE, INITIAL_SCALE));
				}
				if (future3 == null){
					increment();
					future3 = executor.submit(new TripleBuffering(INITIAL_ANGLE, INITIAL_SCALE));
				}
				break;
			case 1:
				imgOne = future2.get();
				future2 = null;
				if (future3 == null){
					increment();
					future3 = executor.submit(new TripleBuffering(INITIAL_ANGLE, INITIAL_SCALE));
				}
				if(future1 == null) {
					increment();
					future1 = executor.submit(new TripleBuffering(INITIAL_ANGLE, INITIAL_SCALE));
				}
				break;
			case 2:
				imgOne = future3.get();
				future3 = null;
				if(future1 == null) {
					increment();
					future1 = executor.submit(new TripleBuffering(INITIAL_ANGLE, INITIAL_SCALE));
				}
				if(future2 == null) {
					increment();
					future2 = executor.submit(new TripleBuffering(INITIAL_ANGLE, INITIAL_SCALE));
				}
				break;
		}
		lbIm1.setIcon(new ImageIcon(imgOne));
		multiBuffer = (multiBuffer + 1)%3;
	}

	public static void main(String[] args) {
		ImageDisplay imageDisplay = new ImageDisplay();
		try {
			imageDisplay.createFrame(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
