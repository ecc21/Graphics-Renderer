package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineRenderer implements LineRenderer{

	
	private DDALineRenderer() {}
	
	
	
	private double calc_color_slope(double x1, double x2, double color1, double color2) {
		double deltaX = x2 - x1;
		double deltaColor = color2 - color1;
		if(deltaColor == 0) {
			return 0;
		}
		return deltaColor/deltaX;
	}
	/*
	 * (non-Javadoc)
	 * @see client.LineRenderer#drawLine(client.Vertex2D, client.Vertex2D, windowing.Drawable)
	 * 
	 * @pre: p2.x >= p1.x && p2.y >= p1.y
	 */
	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		double slope = deltaY / deltaX;
		double intercept = p2.getIntY() - slope * p2.getIntX();
	
		double x = p1.getIntX();
		double y = p1.getIntY();
		
		
		int argbColor1 = p1.getColor().asARGB();
		int argbColor2 = p2.getColor().asARGB();
		Color color1 = Color.fromARGB(argbColor1);
		Color color2 = Color.fromARGB(argbColor2);
		int color1R = color1.getIntR();
		int color1G = color1.getIntG();
		int color1B = color1.getIntB();

		int color2R = color2.getIntR();
		int color2G = color2.getIntG();
		int color2B = color2.getIntB();
		
		double rSlope = calc_color_slope(p1.getIntX(),p2.getIntX(), color1R,color2R);
		double gSlope = calc_color_slope(p1.getIntX(),p2.getIntX(), color1G,color2G);
		double bSlope = calc_color_slope(p1.getIntX(),p2.getIntX(), color1B,color2B);
		
		//System.out.println(color1R);
		//System.out.println(color2R);
		
		/*System.out.println(rSlope);
		System.out.println(gSlope);
		System.out.println(bSlope);*/
		if(slope <= 1) {
			for(x = p1.getIntX(); x <= p2.getIntX(); x++) {
				drawable.setPixel((int)x, (int)Math.round(y),p1.getZ(), argbColor1);
				//System.out.print(x);
				//System.out.println((int)Math.round(y));
				//System.out.println(color1R);
				//System.out.println(color1G);
				//System.out.println(color1B);
				y = y + slope;
				color1R += rSlope;
				color1G += gSlope;
				color1B += bSlope;
				
				argbColor1 = Color.makeARGB(Math.round(color1R), Math.round(color1G), Math.round(color1B));
			}
		}
		else {
			//System.out.print(" why am i here");
			//for(y = p1.getIntY(); y <= p2.getIntY(); y++) {
			//	drawable.setPixel((int)Math.round(x), (int)y, 0.0, argbColor);
			//	x = x + 1/slope;
			//}
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new DDALineRenderer());
	}

}
