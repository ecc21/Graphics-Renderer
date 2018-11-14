package client;

import java.util.List;

import client.interpreter.SimpInterpreter;

import geometry.Point2D;
import line.AlternatingLineRenderer;
import line.AntialiasingLineRenderer;
import line.BresenhamLineRenderer;
import line.DDALineRenderer;
import line.ExpensiveLineRenderer;
import line.LineRenderer;
import polygon.FilledPolygonRenderer;
/*import notProvided.client.ColoredDrawable;
import notProvided.client.testpages.MeshPolygonTest;
import notProvided.client.testpages.ParallelogramTest;
import notProvided.client.testpages.RandomLineTest;
import notProvided.client.testpages.RandomPolygonTest;
import notProvided.client.testpages.StarburstPolygonTest;
import notProvided.line.AntialiasingLineRenderer;
import notProvided.line.BresenhamLineRenderer;
import notProvided.line.DDALineRenderer;
import notProvided.polygon.FilledPolygonRenderer;
import notProvided.polygon.WireframePolygonRenderer;*/
import polygon.PolygonRenderer;
import polygon.WireFramePolygonRenderer;
import windowing.PageTurner;
import windowing.drawable.Drawable;
import windowing.drawable.GhostWritingDrawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.drawable.ZBufferDrawable;
import windowing.drawable.DepthCueingDrawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;
	
	private static final int NUM_PAGES = 16;
	protected static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 1;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);
	private static final Point2D[] lowCornersOfPanels = {
			/*new Point2D( 50, 400),
			new Point2D(400, 400),
			new Point2D( 50,  50),
			new Point2D(400,  50),*/
			new Point2D(50,  50)
	};
	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable[] panels;
	private Drawable[] ghostPanels;					// use transparency and write only white
	private Drawable largePanel;
	
	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer[];
	private SimpInterpreter interpreter;
	private RendererTrio renderers;
	
	String filename;
	
	private boolean hasArgument = false;
	 List<String> raw;
	public Client(Drawable drawable, boolean hasArguments, List<String> raw) {

		this.drawable = drawable;
		this.hasArgument = hasArguments;
		this.raw = raw;
		createDrawables();
		createRenderers();
	}

	public void createDrawables() {
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
		//image = new ColoredDrawable(image, ARGB_WHITE);
		
		largePanel = new TranslatingDrawable(image, point(  50, 50),  dimensions(650, 650));
		
		createPanels();
		createGhostPanels();

	}

	public void createPanels() {
		panels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
			panels[index] = new TranslatingDrawable(image, lowCornersOfPanels[index], PANEL_SIZE);
		}
	}

	private void createGhostPanels() {
		ghostPanels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
			Drawable drawable = panels[index];
			ghostPanels[index] = new GhostWritingDrawable(drawable, GHOST_COVERAGE);
		}
	}

	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	private void createRenderers() {
		
		lineRenderers = new LineRenderer[1];
		polygonRenderer = new PolygonRenderer[2];
		/*lineRenderers[0] = BresenhamLineRenderer.make();
		lineRenderers[1] = DDALineRenderer.make();
		lineRenderers[2] = AlternatingLineRenderer.make();
		lineRenderers[3] = AntialiasingLineRenderer.make();*/
		lineRenderers[0] = DDALineRenderer.make();
		polygonRenderer[0] = WireFramePolygonRenderer.make(DDALineRenderer.make());
		polygonRenderer[1] = FilledPolygonRenderer.make();
		renderers = new RendererTrio();
	}
	
	@Override
	public void nextPage() { 	

		if(hasArgument) {
			argumentNextPage();
		}
		else {
			noArgumentNextPage();
		}

	}
	
	private void argumentNextPage() {
		image.clear();
		largePanel.clear();
		
		String filename = raw.get(0);
		
		interpreter = new SimpInterpreter(filename + ".simp", largePanel, renderers);
		interpreter.interpret();
	}
	
	public void noArgumentNextPage() {
		
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;
		
		image.clear();
		largePanel.clear();
		String filename;

		switch(pageNumber) {
		case 1:  filename = "page-a1";	 break;
		case 2:  filename = "page-a2";	 break;
		case 3:	 filename = "page-a3";	 break;
		case 4:  filename = "page-b1";	 break;
		case 5:  filename = "page-b2";	 break;
		case 6:  filename = "page-b3";	 break;
		case 7:  filename = "page-c1";	 break;
		case 8:  filename = "page-c2";	 break;
		case 9:  filename = "page-c3";	 break;
		case 10:  filename = "page-d";	 break;
		case 11:  filename = "page-e";	 break;
		case 12:  filename = "page-f1";	 break;
		case 13:  filename = "page-f2";	 break;
		case 14:  filename = "page-g";	 break;
		case 15:  filename = "page-h";	 break;
		case 0:  filename = "page-i";	 break;
		

		default: defaultPage();
				 return;
		}				 
		interpreter = new SimpInterpreter(filename + ".simp", largePanel, renderers);
		interpreter.interpret();
	}

	private void defaultPage() {
		image.clear();
		largePanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}
}
