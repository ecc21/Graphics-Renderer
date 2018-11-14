package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class BresenhamLineRenderer implements LineRenderer{

	private BresenhamLineRenderer() {}
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
		
		double slope = (deltaY / deltaX);
		if(deltaX == 0) {
			slope = 1.0;
		}
		double intercept = p2.getIntY() - slope * p2.getIntX();
		
		int y = p1.getIntY();
		int argbColor = p1.getColor().asARGB();
		
		double delta_error = Math.abs(deltaY / deltaX);
		double error = 0.0;
		
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			drawable.setPixel(x, y, 0.0, argbColor);
			error += delta_error;
			if(error >= 0.5) {
				y++;
				error--;
			}
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new BresenhamLineRenderer());
	}
}
