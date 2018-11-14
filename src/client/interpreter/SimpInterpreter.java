package client.interpreter;

import java.util.List;
import java.util.Stack;

import client.interpreter.LineBasedReader;
import geometry.Point3DH;
import geometry.Rectangle;
import geometry.Vertex3D;
import line.LineRenderer;
import client.interpreter.Clipper;
import windowing.drawable.DepthCueingDrawable;
import client.RendererTrio;
import geometry.Transformation;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.Shader;
import shading.FlatShader;
import shading.GouraudShader;
import shading.PhongShader;
import windowing.drawable.Drawable;
import windowing.drawable.ZBufferDrawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private static RenderStyle renderStyle;
	
	private Transformation CTM;
	private Transformation worldToScreen;
	
	private static int WORLD_LOW_X = -100;
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;
	
	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	
	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;
	private Color depthColor = Color.BLACK;
	private double specular_k = 0.3;
	private double specular_p = 8;
	
	
	private Drawable drawable;
	private static Drawable depthCueingDrawable;
	
	private LineRenderer lineRenderer;
	private static PolygonRenderer filledRenderer;
	private static PolygonRenderer wireframeRenderer;
	private Transformation cameraToScreen; //perspective matrix * inv camera
	private Clipper clipper;
	
	private Transformation cameraToWorld; //location of camera in world
	private Transformation view; //inverse of camera location in world
	private Transformation projection; // perspective projection
	private Transformation worldToCamera; //visible space
	private Transformation cameraToView; //visible space
	private Transformation worldToView; //visible space
	private Stack<Transformation> transformationStack;
	
	private Lighting lighting;
	
	private static ShadingStyle shadingStyle;
	
	private static FlatShader flatShader;
	private static GouraudShader gouraudShader;
	private static PhongShader phongShader;
	
	private Dimensions worldDimensions;
	
	private double near = -200;
	private double far = -200;
	private boolean depth_coloring = false;


	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	
	public enum ShadingStyle {
		FLAT,
		GOURAUD,
		PHONG;
	}
	public SimpInterpreter(String filename, 
			Drawable drawable,
			RendererTrio renderers) {
		drawable.clear();
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.depthCueingDrawable = 	new ZBufferDrawable(drawable,0,-2000000);
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.defaultColor = Color.WHITE;
		makeWorldToScreenTransform(drawable.getDimensions());
		worldDimensions = drawable.getDimensions();
		
		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		transformationStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
		CTM = Transformation.identity();
		//CTM.print_matrix();
		
		clipper = new Clipper(WORLD_LOW_X, WORLD_LOW_Y, WORLD_HIGH_X, WORLD_HIGH_Y, -1, -200);
		
		lighting = new Lighting(ambientLight);
		shadingStyle = ShadingStyle.PHONG;
		
		flatShader = new FlatShader();
		flatShader.set_lighting(lighting);
		gouraudShader = new GouraudShader();
		gouraudShader.set_lighting(lighting);
		phongShader = new PhongShader();
		phongShader.set_lighting(lighting);
		
		
	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		// TODO: fill this in
		double x_scale = (double)dimensions.getWidth()/(double)(WORLD_HIGH_X - WORLD_LOW_X);
		double y_scale = (double)dimensions.getHeight()/(double)(WORLD_HIGH_Y - WORLD_LOW_Y);
		double z_scale = 1;

		Transformation s_xform = Transformation.scale(x_scale,y_scale,z_scale);
		worldToScreen = Transformation.multiply(s_xform, Transformation.identity());
		//CTM * Transformation
		double center_x = dimensions.getWidth()/2;
		double center_y = dimensions.getHeight()/2;
		//origin 0, 0 -> center_x, centery
		//translate(center_x, center_y, 0,0)
		Transformation t_xform = Transformation.translate(center_x,center_y, 0.0);
		//s_xform.print_matrix();
		//t_xform.print_matrix();
		worldToScreen = Transformation.multiply(t_xform, worldToScreen);
		
			
	}
	
	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			//CTM.print_matrix();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	public void interpretLine(String line) {
		//System.out.println(line);
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	private void interpretCommand(String[] tokens) {
		//System.out.println(tokens[0]);
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;
		
		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;
		case "light" :		interpretLight(tokens);		break;
		case "flat" :		flat();						break;
		case "gouraud" :	gouraud();					break;
		case "phong" :		phong();					break;
		
		default :
			System.err.println("bad input line: " + tokens[0]);
			break;
		}
	}

	private void phong() {
		// TODO Auto-generated method stub
		shadingStyle = ShadingStyle.PHONG;
	}

	private void gouraud() {
		// TODO Auto-generated method stub
		shadingStyle = ShadingStyle.GOURAUD;
	}

	private void flat() {
		// TODO Auto-generated method stub
		shadingStyle = ShadingStyle.FLAT;
	}

	private void push() {
		transformationStack.push(this.CTM);
	}
	private void pop() {
		CTM = transformationStack.pop();
	}
	private void wire() {
		renderStyle = RenderStyle.WIREFRAME;
	}
	private void filled() {
		renderStyle = RenderStyle.FILLED;
	}
	
	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}	

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		// TODO: finish this method
		Transformation s_cform = Transformation.scale(sx, sy, sz);
		CTM = Transformation.multiply(CTM,s_cform);
	}
	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		// TODO: finish this method
		Transformation t_xform = Transformation.translate(tx, ty, tz);
		CTM = Transformation.multiply( CTM,t_xform);
	}
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);
		// TODO: finish this method
		//System.out.println(axisString);
		Transformation r_xform = Transformation.identity();
		if(axisString.equals("X")) {
			r_xform = Transformation.rotateX(Math.toRadians(angleInDegrees));
		}
		else if(axisString.equals("Y")) {
			r_xform = Transformation.rotateY(Math.toRadians(angleInDegrees));
		}
		else if(axisString.equals("Z")) {
			r_xform = Transformation.rotateZ(Math.toRadians(angleInDegrees));
		}
		else {
			System.out.println("Something went wrong in interpretRotate");
		}
		CTM = Transformation.multiply(CTM, r_xform);
	}
	private static double cleanNumber(String string) {
		return Double.parseDouble(string);
	}
	
	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);
		
		private int numTokensPerVertex;
		
		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	private void interpretLine(String[] tokens) {			
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);
		// TODO: finish this method
		line(vertices[0],vertices[1]);
	}	
	private void interpretPolygon(String[] tokens) {			
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
		// TODO: finish this method
		polygon(vertices[0],vertices[1],vertices[2]);
	}
	
	
	
	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);	
		//System.out.println(vertexColors);
		Vertex3D vertices[] = new Vertex3D[numVertices];
		
		for(int index = 0; index < numVertices; index++) {
			
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		//System.out.println(tokens.length);
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	public int numTokensForCommandWithNVertices(int numVertices) {
		//System.out.println(numVertices);
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}
	private Color depthLerp(Vertex3D vertex) {
		double z = vertex.getZ();
		Color color = vertex.getColor();
		Color lighting_calculation_color = color.multiply(ambientLight);

		if(z >= near) {
			//System.out.println("its near joe");
			color = color.multiply(ambientLight);
			//System.out.println(ambientLight);
		} 
		else if(z <= far) {
			//System.out.println("its far bob");
			color = color.multiply(depthColor);
		}
		else {
			//.out.println("juuuust right");
			//System.out.println(z);
			double dz = far-near;
			double dr = depthColor.getR() - lighting_calculation_color.getR();
			double dg = depthColor.getG() - lighting_calculation_color.getG();
			double db = depthColor.getB() - lighting_calculation_color.getB();
			double deltaR = dr/dz;
			double deltaG = dg/dz;
			double deltaB = db/dz;
			double r = lighting_calculation_color.getR() + z*deltaR;
			double g = lighting_calculation_color.getG() + z*deltaG;
			double b = lighting_calculation_color.getB() + z*deltaB;
			
			Color lerp = new Color(r,g,b);
			//System.out.println("ambient is: " + ambientLight + " depth is: " + depthColor + " z is: " + z + " Lerped is: "+ lerp);
			//System.out.println("color is: " + color);
			//color = color.multiply(lerp);
			//System.out.println("lerped color is: " + color);
			color = lerp;
		}
		//System.out.println("returning color:" + color);
		return color;
	}
	private Polygon depthLerp(Polygon polygon) {

		Vertex3D v0 = polygon.get(0);
		Vertex3D v1 = polygon.get(1).subtractCamera(polygon.get(0));
		Vertex3D v2 = polygon.get(2).subtractCamera(polygon.get(0));
		double sx = (v1.getCameraY() * v2.getCameraZ()) - (v2.getCameraY() * v1.getCameraZ());
		double sy = (v1.getCameraZ() * v2.getCameraX()) - (v2.getCameraZ() * v1.getCameraX());
		double sz = (v1.getCameraX() * v2.getCameraY()) - (v2.getCameraX() * v1.getCameraY());
		
		v1 = polygon.get(1);
		v2 = polygon.get(2);
		
		Vertex3D normal = new Vertex3D(sx,sy,sz,v0.getColor());
		normal = normal.normalize();

		Color lighting_calculation_color_0 = this.lighting.calculate_light(polygon, normal, v0);
		Color lighting_calculation_color_1 = this.lighting.calculate_light(polygon, normal, v1);
		Color lighting_calculation_color_2 = this.lighting.calculate_light(polygon, normal, v2);
		
		Color color0 = v0.getColor();
		Color color1 = v1.getColor();
		Color color2 = v2.getColor();
		
		double z0 = v0.getZ();
		double z1 = v1.getZ();
		double z2 = v2.getZ();
		
		/*v0 = polygon.get(0).replaceColor(lighting_calculation_color_0);
		v1 = polygon.get(1).replaceColor(lighting_calculation_color_1);
		v2 = polygon.get(2).replaceColor(lighting_calculation_color_2);
		Polygon new_polygon = Polygon.make(v0,v1,v2);
		return new_polygon;*/
		if(z0 >= near) {
			color0 = color0.multiply(lighting_calculation_color_0);
			//System.out.println(ambientLight);
		} 
		else if(z0 <= far) {
			color0 = color0.multiply(depthColor);
		}
		else {
			double dz = far-near;
			double dr = depthColor.getR() - lighting_calculation_color_0.getR();
			double dg = depthColor.getG() - lighting_calculation_color_0.getG();
			double db = depthColor.getB() - lighting_calculation_color_0.getB();
			double deltaR = dr/dz;
			double deltaG = dg/dz;
			double deltaB = db/dz;
			double r = lighting_calculation_color_0.getR() + z0*deltaR;
			double g = lighting_calculation_color_0.getG() + z0*deltaG;
			double b = lighting_calculation_color_0.getB() + z0*deltaB;
			
			Color lerp = new Color(r,g,b);
			color0 = lerp;
		}
		if(z1 >= near) {
			color1 = color1.multiply(lighting_calculation_color_1);
		} 
		else if(z1 <= far) {
			color1 = color1.multiply(depthColor);
		}
		else {
			double dz = far-near;
			double dr = depthColor.getR() - lighting_calculation_color_1.getR();
			double dg = depthColor.getG() - lighting_calculation_color_1.getG();
			double db = depthColor.getB() - lighting_calculation_color_1.getB();
			double deltaR = dr/dz;
			double deltaG = dg/dz;
			double deltaB = db/dz;
			double r = lighting_calculation_color_1.getR() + z1*deltaR;
			double g = lighting_calculation_color_1.getG() + z1*deltaG;
			double b = lighting_calculation_color_1.getB() + z1*deltaB;
			
			Color lerp = new Color(r,g,b);
			color1 = lerp;
		}
		if(z2 >= near) {
			color2 = color2.multiply(lighting_calculation_color_2);
		} 
		else if(z2 <= far) {
			color2 = color2.multiply(depthColor);
		}
		else {

			double dz = far-near;
			double dr = depthColor.getR() - lighting_calculation_color_2.getR();
			double dg = depthColor.getG() - lighting_calculation_color_2.getG();
			double db = depthColor.getB() - lighting_calculation_color_2.getB();
			double deltaR = dr/dz;
			double deltaG = dg/dz;
			double deltaB = db/dz;
			double r = lighting_calculation_color_2.getR() + z2*deltaR;
			double g = lighting_calculation_color_2.getG() + z2*deltaG;
			double b = lighting_calculation_color_2.getB() + z2*deltaB;
			
			Color lerp = new Color(r,g,b);
			color2 = lerp;
		}
		
		v0 = polygon.get(0).replaceColor(color0);
		v1 = polygon.get(1).replaceColor(color1);
		v2 = polygon.get(2).replaceColor(color2);
		Polygon new_polygon = Polygon.make(v0,v1,v2);
		return new_polygon;
		
	}

	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		
		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);		
		}

		// TODO: finish this method
		Vertex3D p = new Vertex3D(point.getX()/point.getW(), point.getY()/point.getW(), point.getZ()/point.getW(), color);
		return p;

	}
	public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		// TODO: finish this method
		Point3DH point = new Point3DH(x,y,z);
		return point;

	}
	public static Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);
		// TODO: finish this method
		Color color = new Color(r,g,b);
		return color;

	}
	
	
	private void line(Vertex3D p1, Vertex3D p2) {

		Vertex3D cameraP1 = transformToCamera(p1);
		Vertex3D cameraP2 = transformToCamera(p2);
		
		if(depth_coloring == true) {
			Color color = depthLerp(cameraP1);
			p1 = p1.replaceColor(color);
			color = depthLerp(cameraP2);
			p2 = p2.replaceColor(color);
		}
		
		Vertex3D screenP1 = transformToView(p1);
		Vertex3D screenP2 = transformToView(p2);
		
		
		// TODO: finish this method
		//Vertex3D v1 = Transformation.multiply(CTM, screenP1);
		//Vertex3D v2 = Transformation.multiply(CTM, screenP2);
		//lineRenderer.drawLine(v1, v2, depthCueingDrawable);
		lineRenderer.drawLine(screenP1, screenP2, depthCueingDrawable);
	}
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		/*System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);*/
		Vertex3D cameraP1 = transformToCamera(p1);
		Vertex3D cameraP2 = transformToCamera(p2);
		Vertex3D cameraP3 = transformToCamera(p3);
		
		cameraP1 = cameraP1.replaceCameraPoint(cameraP1.getPoint3D());
		cameraP2 = cameraP2.replaceCameraPoint(cameraP2.getPoint3D());
		cameraP3 = cameraP3.replaceCameraPoint(cameraP3.getPoint3D());
		/*System.out.println("camera space:");
		System.out.println(cameraP1);
		System.out.println(cameraP2);
		System.out.println(cameraP3);*/
		Polygon cameraPolygon = Polygon.make(cameraP1,cameraP2,cameraP3);
		//System.out.println("polygon is :" + cameraPolygon);
		cameraPolygon = clipper.clip_hither(cameraPolygon);
		if(cameraPolygon == null) {
			//System.out.println("CULLED: near");
			return;
		}
		cameraPolygon = clipper.clip_yon(cameraPolygon);
		if(cameraPolygon == null) {
			//System.out.println("CULLED: far");
			return;
		}
		//System.out.println("new gon is :" + cameraPolygon);
		cameraP1 = cameraPolygon.get(0);
		cameraP2 = cameraPolygon.get(1);
		cameraP3 = cameraPolygon.get(2);
		
		if(cameraPolygon.length() == 4) {
			Vertex3D cameraP4 = cameraPolygon.get(3);
			//System.out.println("polygon length is 4 at camera");
			single_polygon(cameraP1, cameraP3,cameraP4,true);
		}
		/*System.out.println("new camera space:");
		System.out.println(cameraP1);
		System.out.println(cameraP2);
		System.out.println(cameraP3);*/
		if(depth_coloring == true) {
			//System.out.println("old color is: "+cameraP1.getColor());
			Polygon polygon = Polygon.make(cameraP1, cameraP2, cameraP3);
			polygon = depthLerp(polygon);
			/*Color color = depthLerp(cameraP1);
			cameraP1 = cameraP1.replaceColor(color);
			//System.out.println("new color is: "+p1.getColor());
			color = depthLerp(cameraP2);
			cameraP2 = cameraP2.replaceColor(color);
			color = depthLerp(cameraP3);
			cameraP3 = cameraP3.replaceColor(color);*/
			cameraP1 = polygon.get(0);
			cameraP2 = polygon.get(1);
			cameraP3 = polygon.get(2);
		}
		
		Vertex3D viewP1 = cameraToView(cameraP1);
		Vertex3D viewP2 = cameraToView(cameraP2);
		Vertex3D viewP3 = cameraToView(cameraP3);
		/*System.out.println("view space:");
		System.out.println(viewP1);
		System.out.println(viewP2);
		System.out.println(viewP3);*/
		Polygon viewPolygon = Polygon.make(viewP1,viewP2,viewP3);
		//System.out.println("view gon is :" + viewPolygon);
		viewPolygon = clipper.clip_left(viewPolygon);
		if(viewPolygon == null) {
			//System.out.println("CULLED: left");
			return;
		}
		viewPolygon = clipper.clip_right(viewPolygon);
		if(viewPolygon == null) {
			//System.out.println("CULLED: right");
			return;
		}
		viewPolygon = clipper.clip_top(viewPolygon);
		if(viewPolygon == null) {
			//System.out.println("CULLED: top");
			return;
		}
		viewPolygon = clipper.clip_bottom(viewPolygon);
		if(viewPolygon == null) {
			//System.out.println("CULLED: bottom");
			return;
		}
		
		//System.out.println("new view gon is :" + viewPolygon);
		//System.out.println("new gon length is :" + viewPolygon.length());
		
		viewP1 = viewPolygon.get(0);
		viewP2 = viewPolygon.get(1);
		viewP3 = viewPolygon.get(2);
		if(viewPolygon.length() == 4) {
			Vertex3D viewP4 = viewPolygon.get(3);
			single_polygon(viewP1, viewP3,viewP4,false);
		}
		if(viewPolygon.length() == 5) {
			Vertex3D viewP4 = viewPolygon.get(3);
			Vertex3D viewP5 = viewPolygon.get(4);
			single_polygon(viewP1, viewP3,viewP4,false);
			single_polygon(viewP1, viewP4,viewP5,false);
		}
		/*System.out.println("new view space:");
		System.out.println(viewP1);
		System.out.println(viewP2);
		System.out.println(viewP3);*/
		Vertex3D screenP1 = viewToScreen(viewP1);
		Vertex3D screenP2 = viewToScreen(viewP2);
		Vertex3D screenP3 = viewToScreen(viewP3);
		
		/*cameraP1 = transformToCamera(p1);
		cameraP2 = transformToCamera(p2);
		cameraP3 = transformToCamera(p3);
		Vertex3D screenP1 = cameraToScreen(cameraP1);
		Vertex3D screenP2 = cameraToScreen(cameraP2);
		Vertex3D screenP3 = cameraToScreen(cameraP3);*/
		
		/*System.out.println("screen space:");
		System.out.println(screenP1);
		System.out.println(screenP2);
		System.out.println(screenP3);*/
		assert(cameraP1.getPoint3D() == screenP1.getCameraPoint() );
		//System.out.println("cameraP1 is:" + cameraP1.getPoint3D() + ". screenP1 camera is: " + screenP1.getCameraPoint());
		Polygon polygon = Polygon.make(screenP1,screenP2,screenP3);
		polygon.setSpecularScalar(specular_k);
		polygon.setSpecularExonent(specular_p);
		//Polygon polygon = Polygon.make(cameraP1,cameraP2,cameraP3);
		//System.out.println(polygon);
		switch (renderStyle) {
		case FILLED:
			switch (shadingStyle) {
			case FLAT:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,flatShader);
				break;
			case GOURAUD:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,gouraudShader);
				break;
			case PHONG:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,phongShader);
				break;
			}

			break;
		case WIREFRAME:
			wireframeRenderer.drawPolygon(polygon, depthCueingDrawable,flatShader);
		}
	}
	//assumes p1, p2 ,p3 are already clipped -> no changes in polygon size
	private void single_polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3, boolean camera_space) {

		/*System.out.println("new camera space:");
		System.out.println(cameraP1);
		System.out.println(cameraP2);
		System.out.println(cameraP3);*/
		Vertex3D viewP1 = p1;
		Vertex3D viewP2 = p2;
		Vertex3D viewP3 = p3;
		Polygon viewPolygon = Polygon.make(viewP1,viewP2,viewP3);	
		//if(!camera_space)
		//	System.out.println("view polygon is " + viewPolygon);
		
		
		if(camera_space == true) {
			if(depth_coloring == true) {
				//System.out.println("ambient is : "+ambientLight);
				//System.out.println("depth is : "+depthColor);
				//System.out.println("old color1 is: "+p1.getColor());
				Polygon polygon = Polygon.make(p1,p2,p3);
				polygon = depthLerp(polygon);
				/*Color color = depthLerp(p1);
				p1 = p1.replaceColor(color);
				//System.out.println("new color1 is: "+p1.getColor());
				//System.out.println("old color2 is: "+p2.getColor());
				color = depthLerp(p2);
				p2 = p2.replaceColor(color);
				//System.out.println("new color2 is: "+p2.getColor());
				//System.out.println("old color3 is: "+p3.getColor());
				color = depthLerp(p3);
				p3 = p3.replaceColor(color);
				//System.out.println("new color2 is: "+p3.getColor());*/
				p1 = polygon.get(0);
				p2 = polygon.get(1);
				p3 = polygon.get(2);
			}
			viewP1 = cameraToView(p1);
			viewP2 = cameraToView(p2);
			viewP3 = cameraToView(p3);	
			viewPolygon = Polygon.make(viewP1,viewP2,viewP3);	
			//System.out.println("view polygon is " + viewPolygon);
			
			viewPolygon = clipper.clip_left(viewPolygon);
			if(viewPolygon == null) {
				//System.out.println("CULLED: left");
				return;
			}
			viewPolygon = clipper.clip_right(viewPolygon);
			if(viewPolygon == null) {
				//System.out.println("CULLED: right");
				return;
			}
			viewPolygon = clipper.clip_top(viewPolygon);
			if(viewPolygon == null) {
				//System.out.println("CULLED: top");
				return;
			}
			viewPolygon = clipper.clip_bottom(viewPolygon);
			if(viewPolygon == null) {
				//System.out.println("CULLED: bottom");
				return;
			}
			//System.out.println("new view polygon is " + viewPolygon);
			
			viewP1 = viewPolygon.get(0);
			viewP2 = viewPolygon.get(1);
			viewP3 = viewPolygon.get(2);
			if(viewPolygon.length() == 4) {
				Vertex3D viewP4 = viewPolygon.get(3);
				single_polygon(viewP1, viewP3,viewP4,false);
			}
			if(viewPolygon.length() == 5) {
				Vertex3D viewP4 = viewPolygon.get(3);
				Vertex3D viewP5 = viewPolygon.get(4);
				single_polygon(viewP1, viewP3,viewP4,false);
				single_polygon(viewP1, viewP4,viewP5,false);
			}
			
		}
		/*System.out.println("view space:");
		System.out.println(viewP1);
		System.out.println(viewP2);
		System.out.println(viewP3);*/

		//System.out.println("new gon length is :" + viewPolygon.length());

		/*System.out.println("new view space:");
		System.out.println(viewP1);
		System.out.println(viewP2);
		System.out.println(viewP3);*/
		Vertex3D screenP1 = viewToScreen(viewP1);
		Vertex3D screenP2 = viewToScreen(viewP2);
		Vertex3D screenP3 = viewToScreen(viewP3);
		
		/*System.out.println("screen space:");
		System.out.println(screenP1);
		System.out.println(screenP2);
		System.out.println(screenP3);*/

		Polygon polygon = Polygon.make(screenP1,screenP2,screenP3);
		polygon.setSpecularScalar(specular_k);
		polygon.setSpecularExonent(specular_p);
		//Polygon polygon = Polygon.make(cameraP1,cameraP2,cameraP3);
		//System.out.println(polygon);
		switch (renderStyle) {
		case FILLED:
			switch (shadingStyle) {
			case FLAT:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,flatShader);
				break;
			case GOURAUD:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,gouraudShader);
				break;
			case PHONG:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,phongShader);
				break;
			}
			
			break;
		case WIREFRAME:
			wireframeRenderer.drawPolygon(polygon, depthCueingDrawable,flatShader);
		}
	}
	public static void renderPolygon(Polygon polygon) {
		switch (renderStyle) {
		case FILLED:
			switch (shadingStyle) {
			case FLAT:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,flatShader);
				break;
			case GOURAUD:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,gouraudShader);
				break;
			case PHONG:
				filledRenderer.drawPolygon(polygon, depthCueingDrawable,phongShader);
				break;
			}
			break;
		case WIREFRAME:
			wireframeRenderer.drawPolygon(polygon, depthCueingDrawable,flatShader);
		}
	}
	private void interpretCamera(String[] tokens) {			
		double xlow = cleanNumber(tokens[1]);
		double ylow = cleanNumber(tokens[2]);
		double xhigh = cleanNumber(tokens[3]);
		double yhigh = cleanNumber(tokens[4]);
		double hither = cleanNumber(tokens[5]);
		double yon = cleanNumber(tokens[6]);
		
		clipper.setPlane(xlow, ylow, xhigh, yhigh, hither, yon);
		/*System.out.println("left clipping plane:" + xlow);
		System.out.println("bottom clipping plane:" + ylow);
		System.out.println("right clipping plane:" + xhigh);
		System.out.println("top clipping plane:" + yhigh);
		System.out.println("near (hither) clipping plane:" + hither);
		System.out.println("far (yon) clipping plane:" + yon);*/
		
		//CTM.print_matrix();
		
		cameraToWorld = CTM;
	
		Transformation tForm = Transformation.identity();
		//inverse transformation = negate transformation
		tForm.set_val(0, 3, -CTM.get_val(0, 3));
		tForm.set_val(1, 3, -CTM.get_val(1, 3));
		tForm.set_val(2, 3, -CTM.get_val(2, 3));
		//inverse rotation = transpose rotation
		Transformation rForm = CTM;
		rForm = Transformation.transpose(rForm);
		//transformation values from CTM set to 0
		rForm.set_val(3, 0, 0);
		rForm.set_val(3, 1, 0);
		rForm.set_val(3, 2, 0);
		
		//tForm.print_matrix();
		//rForm.print_matrix();
		
		view = Transformation.multiply(rForm,tForm);
		
		//view.print_matrix();
		//have where camera is pointing, need to turn camera to perspective view
		projection = Transformation.identity();
		double viewing_plane = -1;
		double d = viewing_plane;
		projection.set_val(3, 3, 0);
		projection.set_val(2, 2, 1);
		projection.set_val(3, 2, 1/d);
		
		//projection.print_matrix();
		
		Transformation viewProjection = Transformation.multiply(projection,view);
		worldToCamera = view;
		cameraToView = projection;
		worldToView = viewProjection;
		
		//worldToCamera = view;\
		
		//wide rectangle: scale by y
		double x_scale = (double)worldDimensions.getWidth()/(double)(xhigh-xlow);
		double y_scale = (double)worldDimensions.getHeight()/(double)(yhigh-ylow);
		double z_scale = 1;
		if((xhigh-xlow) < (yhigh-ylow)) {
			x_scale = (double)worldDimensions.getWidth()/(double)(xhigh-xlow);
			y_scale = (double)worldDimensions.getHeight()/(double)(yhigh-ylow);
			x_scale = y_scale;
			z_scale = 1;
		}
		//long rectangle, scale by x
		else if((xhigh-xlow) > (yhigh-ylow)) {
			x_scale = (double)worldDimensions.getWidth()/(double)(xhigh-xlow);
			y_scale = (double)worldDimensions.getHeight()/(double)(yhigh-ylow);
			y_scale = x_scale;
			z_scale = 1;
		}

		Transformation s_xform = Transformation.scale(x_scale,y_scale,z_scale);
		cameraToScreen = Transformation.multiply(s_xform, Transformation.identity());
		//CTM * Transformation
		double center_x = (worldDimensions.getWidth())/(xhigh-xlow);
		double center_y = (worldDimensions.getHeight())/(yhigh-ylow);
		//origin 0, 0 -> center_x, centery
		//translate(center_x, center_y, 0,0)
		Transformation t_xform = Transformation.translate(center_x,center_y, 0.0);
		//s_xform.print_matrix();
		//t_xform.print_matrix();
		cameraToScreen = Transformation.multiply(t_xform, cameraToScreen);
		//cameraToScreen.print_matrix();
		
	}	


	private Vertex3D transformToCamera(Vertex3D vertex) {
		// TODO: finish this method
		//Transformation xform = Transformation.multiply(worldToScreen,CTM);
		Transformation camera_space = Transformation.multiply(worldToCamera, CTM);
		//Transformation view_space = Transformation.multiply(cameraToView, camera_space);
		//Transformation xform = Transformation.multiply(cameraToScreen, view_space);
		//xform.print_matrix();
		return Transformation.multiply(camera_space, vertex);
	}
	private Vertex3D transformToView(Vertex3D vertex) {
		// TODO: finish this method
		//Transformation xform = Transformation.multiply(worldToScreen,CTM);
		Transformation camera_space = Transformation.multiply(worldToCamera, CTM);
		Transformation view_space = Transformation.multiply(cameraToView, camera_space);
		//xform.print_matrix();
		return Transformation.multiply(view_space, vertex);
	}	
	private Vertex3D cameraToView(Vertex3D vertex) {
		// TODO: finish this method
		//Transformation xform = Transformation.multiply(worldToScreen,CTM);
		double z = vertex.getZ();
		Vertex3D view_space = Transformation.multiply(cameraToView, vertex);
		Point3DH p = view_space.getPoint3D();
		//z = view_space.getZ();
		double x = view_space.getX();
		double y = view_space.getY();
		double w = p.getW();
		Point3DH newp = new Point3DH(x,y,z,1);
		view_space = view_space.replacePoint(newp);
		//xform.print_matrix();
		return view_space;
	}	
	private Vertex3D viewToScreen(Vertex3D vertex) {
		// TODO: finish this method
		//Transformation xform = Transformation.multiply(worldToScreen,CTM);
		//Transformation camera_space = Transformation.multiply(worldToCamera, CTM);
		Vertex3D xform = Transformation.multiply(cameraToScreen, vertex);
		//xform.print_matrix();
		//return Transformation.multiply(xform, vertex);
		return xform;
	}

	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		Point3DH point = new Point3DH(x, y, z, w);
		return point;
	}
		
	private void interpretSurface(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		Color color = new Color(r,g,b);
		if(tokens.length >3) {
			specular_k = cleanNumber(tokens[4]);
			specular_p = cleanNumber(tokens[5]);
		}
		defaultColor = color;
	}
	private void interpretAmbient(String[] tokens) {
		// TODO Auto-generated method stub
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		Color color = new Color(r,g,b);
		ambientLight = color;
		lighting.set_ambient(ambientLight);
		
	}
	private void interpretDepth(String[] tokens) {
		// TODO Auto-generated method stub
		double n = cleanNumber(tokens[1]);	
		double f = cleanNumber(tokens[2]);				
		double r = cleanNumber(tokens[3]);
		double g = cleanNumber(tokens[4]);
		double b = cleanNumber(tokens[5]);
		Color color = new Color(r,g,b);
		depthColor = color;
		near = n;
		far = f;

		depth_coloring = true;
	}
	private void interpretObj(String[] tokens) {

		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		objFile(filename + ".obj");
		
	}
	private void objFile(String filename) {
		ObjReader objReader = new ObjReader(filename, defaultColor);
		objReader.read();
		objReader.render(CTM, worldToCamera, cameraToView, cameraToScreen, clipper, depth_coloring,ambientLight, depthColor, near, far, lighting);
	}
	private void interpretLight(String[] tokens) {			
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		Color lightColor = new Color(r,g,b);
		double A = cleanNumber(tokens[4]);
		double B = cleanNumber(tokens[5]);	
		
		Vertex3D light_location = new Vertex3D(0,0,0, lightColor);
		Vertex3D light_camera_location = transformToCamera(light_location);
		//System.out.println("light camera location: " + light_camera_location);
		Vertex3D light_view_location = cameraToView(light_camera_location);
		Vertex3D light_screen_location = viewToScreen(light_view_location);
		//System.out.println("light screen location: " + light_screen_location);
		//I dont know why, but screen location gives better results? look into it.
		light_location = light_camera_location;
		Light light = new Light(A,B, light_location);
		//lightList.add(light);
		lighting.add_light(light);
		
		flatShader.set_lighting(lighting);		
		gouraudShader.set_lighting(lighting);
		phongShader.set_lighting(lighting);
		
	}	
	

}
