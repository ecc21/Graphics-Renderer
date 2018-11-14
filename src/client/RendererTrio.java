package client;

import line.DDALineRenderer;
import line.LineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.WireFramePolygonRenderer;

public class RendererTrio {
	LineRenderer lineRenderer = DDALineRenderer.make();
	PolygonRenderer filledRender = FilledPolygonRenderer.make();
	PolygonRenderer wireRender = WireFramePolygonRenderer.make(lineRenderer);
	
	public RendererTrio() {
		lineRenderer = DDALineRenderer.make();
		filledRender = FilledPolygonRenderer.make();
		wireRender = WireFramePolygonRenderer.make(lineRenderer);
	}

	public LineRenderer getLineRenderer() {
		return lineRenderer;
	}

	public PolygonRenderer getFilledRenderer() {
		return filledRender;
	}

	public PolygonRenderer getWireframeRenderer() {
		return wireRender;
	}

}
