package windowing.drawable;

import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator{

	double zBuffer[][];
	int z_depth = -200;
	int z_max = 0;
	int argb_color;
	public DepthCueingDrawable(Drawable delegate,int max_z, int min_z, Color color) {
		super(delegate);
		z_depth = min_z;
		z_max = max_z;
		argb_color = color.asARGB();
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
		double multiplier = (z_depth - z)/z_depth;
		/*Color color = Color.fromARGB(argbColor);
		color = color.scale(multiplier);
		int argb = color.asARGB();*/

		Color color = Color.fromARGB(this.argb_color);
		color = color.scale(multiplier);
		int argb = color.asARGB();
		if(x < delegate.getWidth() && x >= 0 && y < delegate.getHeight() && y > 0) {
			if(z > z_max) {
				//do nothing
			}
			else if(z > zBuffer[y][x] && z < z_max) {
				if(z_depth < z) {
					delegate.setPixel(x,  y, z, argb);	
					zBuffer[y][x] = z;
				}
				
			}
		}
		
	}
	

}
