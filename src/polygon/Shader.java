package polygon;

import geometry.Vertex3D;
import windowing.graphics.Color;

//@FunctionalInterface
public interface Shader {
	public Color shade_pixel(Polygon polygon, Vertex3D current);

	public Polygon shade_vertex(Polygon polygon);
	
}
