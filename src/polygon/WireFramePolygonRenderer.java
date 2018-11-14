package polygon;

import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import windowing.drawable.Drawable;

public class WireFramePolygonRenderer implements PolygonRenderer {
	LineRenderer lineRenderer = DDALineRenderer.make();
	public WireFramePolygonRenderer(LineRenderer line_renderer) {
		this.lineRenderer = line_renderer;
	}
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		
		Vertex3D v1 = polygon.get(0);
		Vertex3D v2 = polygon.get(1);
		Vertex3D v3 = polygon.get(2);
		Vertex3D v4;
		Vertex3D v5;
		if(polygon.length() == 3) {
			lineRenderer.drawLine(v1, v2, drawable);
			lineRenderer.drawLine(v1, v3, drawable);
			lineRenderer.drawLine(v2, v3, drawable);
		}
		if(polygon.length() == 4) {
			v4 = polygon.get(3);
			lineRenderer.drawLine(v1, v2, drawable);
			lineRenderer.drawLine(v2, v3, drawable);
			lineRenderer.drawLine(v3, v4, drawable);
			lineRenderer.drawLine(v4, v1, drawable);
		}
		if(polygon.length() == 5) {
			v4 = polygon.get(3);
			v5 = polygon.get(4);
			lineRenderer.drawLine(v1, v2, drawable);
			lineRenderer.drawLine(v2, v3, drawable);
			lineRenderer.drawLine(v3, v4, drawable);
			lineRenderer.drawLine(v4, v5, drawable);
			lineRenderer.drawLine(v5, v1, drawable);
		}

	}
	public static PolygonRenderer make( LineRenderer line_renderer) {
		// TODO Auto-generated method stub
		//lineRenderer = line_renderer;
		return new WireFramePolygonRenderer(line_renderer);
	}

}
