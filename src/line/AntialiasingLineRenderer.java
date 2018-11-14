package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class AntialiasingLineRenderer implements LineRenderer {
	// use the static factory make() instead of constructor.
	private AntialiasingLineRenderer() {}

	
	/*
	 * (non-Javadoc)
	 * @see client.LineRenderer#drawLine(client.Vertex2D, client.Vertex2D, windowing.Drawable)
	 * 
	 * @pre: p2.x >= p1.x && p2.y >= p1.y
	 */
	
	private int opaqued_color(int red,int green, int blue,double fraction) {
		int r = ((int)(Math.round(fraction*red)));
		int g = ((int)(Math.round(fraction*green)));
		int b = (int)(Math.round(fraction*blue));
		return (0xff<<24) + (r<<16) + (g<<8) + b;
		
		//return (0xff<<24) +(int)(Math.round((fraction)*red) << 16)+ (int)(Math.round((fraction)*green) << 8) + (int)(Math.round((fraction)*blue));
	}
	
	private int alpha_color_proto(int opaque_color, int old_color, double opacity) {
		int old_red = 0xff & old_color >> 16;
		int old_green = 0xff & old_color >> 8;
		int old_blue = 0xff & old_color;
		int opaque_red = 0xff & opaque_color >> 16;
		int opaque_green = 0xff & opaque_color >> 8;
		int opaque_blue = 0xff & opaque_color;		
		int r = (int)Math.round((opaque_red + (1-opacity)*old_red));
		int g = (int)Math.round((opaque_green + (1-opacity)*old_green));
		int b = (int)Math.round((opaque_blue + (1-opacity)*old_blue));
		return (0xff<<24) + (r<<16) + (g<<8) + b;
		
	}
	
	private int alpha_color(int new_color, int old_color, double opacity) {
		int old_red = 0xff & old_color >> 16;
		int old_green = 0xff & old_color >> 8;
		int old_blue = 0xff & old_color;
		double opaque_red = opacity*(0xff & new_color >> 16);
		double opaque_green = opacity*(0xff & new_color >> 8);
		double opaque_blue = opacity*(0xff & new_color);		
		int r = (int)Math.round((opaque_red + (1-opacity)*old_red));
		int g = (int)Math.round((opaque_green + (1-opacity)*old_green));
		int b = (int)Math.round((opaque_blue + (1-opacity)*old_blue));
		return (0xff<<24) + (r<<16) + (g<<8) + b;
	}
	
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		
		double slope = deltaY / deltaX;
		
		int argbColor = p1.getColor().asARGB();

		//int red = 0xff & argbColor >> 16;
		//int green = 0xff & argbColor >> 8;
		//int blue = 0xff & argbColor;
		//System.out.print(red);
		//System.out.print(green);
		//System.out.print(blue);
		//System.out.println(argbColor);
		double x = p1.getIntX();
		double y = p1.getIntY();

		if(slope < 1) {
			for(x = p1.getIntX(); x <= p2.getIntX(); x++) {
				//System.out.println(y-Math.floor(y));
				//drawable.setPixel((int)x, (int)Math.floor(y), 0.0, (0xff<<24) +(int)(Math.round((y-Math.floor(y))*red) << 16)+ (int)(Math.round((y-Math.floor(y))*green) << 8) + (int)(Math.round((y-Math.floor(y))*blue)));
				//drawable.setPixel((int)x, (int)Math.floor(y-1), 0.0, (0xff<<24) +(int)(Math.round((1-(y-Math.floor(y)))*red) << 16)+ (int)(Math.round((1-(y-Math.floor(y)))*green) << 8) + (int)(Math.round((1-(y-Math.floor(y)))*blue)));
				//drawable.setPixel((int)x, (int)Math.floor(y), 0.0, opaqued_color(red,green,blue,y-Math.floor(y)));
				//drawable.setPixel((int)x, (int)Math.floor(y-1), 0.0, opaqued_color(red,green,blue,1-(y-Math.floor(y))));
				//drawable.setPixel((int)x, (int)y, 0.0, alpha_color(opaqued_color(red,green,blue,y-Math.floor(y)), drawable.getPixel((int)x,(int)y),y-Math.floor(y)));
				//drawable.setPixel((int)x, (int)y-1, 0.0, alpha_color(opaqued_color(red,green,blue,1-(y-Math.floor(y))), drawable.getPixel((int)x,(int)y-1),1-(y-Math.floor(y))));
				drawable.setPixel((int)x, (int)y, 0.0, alpha_color(argbColor, drawable.getPixel((int)x,(int)y),y-Math.floor(y)));
				drawable.setPixel((int)x, (int)y-1, 0.0, alpha_color(argbColor, drawable.getPixel((int)x,(int)y-1),1-(y-Math.floor(y))));
				y = y + slope;

			}
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new AntialiasingLineRenderer());
	}
}
