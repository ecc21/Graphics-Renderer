package windowing.drawable;

import windowing.graphics.Color;

public class ZBufferDrawable extends DrawableDecorator{

	double zBuffer[][];
	int z_depth = -200;
	int z_max = 1;
	public ZBufferDrawable(Drawable delegate,int max_z, int min_z) {
		super(delegate);
		z_depth = min_z;
		z_max = max_z;
		int width = delegate.getWidth();
		int height = delegate.getHeight();
		zBuffer = new double[height][width];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				zBuffer[y][x] = z_depth;
			}
		}
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		//System.out.println(Color.fromARGB(argbColor) + " " + multiplier);
		//System.out.println(z);
		if(x < delegate.getWidth() && x >= 0 && y < delegate.getHeight() && y > 0) {
			if(z > zBuffer[y][x] && z < z_max) {
				delegate.setPixel(x,  y, z, argbColor);	
				zBuffer[y][x] = z;
			}
		}
	}
}
