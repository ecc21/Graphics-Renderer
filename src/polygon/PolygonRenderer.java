package polygon;

import polygon.Shader;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	// assumes polygon is ccw.
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader);

	/*default public void drawPolygon(Polygon polygon, Drawable panel) {
		drawPolygon(polygon, panel,  c -> c);
	};*/
	public static PolygonRenderer make() {
		// TODO Auto-generated method stub
		return new FilledPolygonRenderer();
	}
}
