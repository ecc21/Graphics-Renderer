package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3DH;
import geometry.Transformation;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	private class ObjVertex {
		// TODO: fill this class in.  Store indices for a vertex, a texture, and a normal.  Have getters for them.
		int vertexIndex;
		int textureIndex;
		int normalIndex;
		
		public ObjVertex(int vi,int ti,int ni) {
			this.vertexIndex = vi;
			this.textureIndex = ti;
			this.normalIndex = ni;
		}

		public int getVertexIndex() {
			return vertexIndex;
			
		}
		public int getTextureIndex() {
			return textureIndex;
		}
		public int getNormalIndex() {
			return normalIndex;
		}
		
	}
	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;
	}	
	private LineBasedReader reader;
	
	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;
	
	ObjReader(String filename, Color defaultColor) {
		// TODO: Initialize an instance of this class.
		this.defaultColor = defaultColor;
		this.reader = new LineBasedReader(filename);
		this.objVertices =  new ArrayList<Vertex3D>(); 
		this.transformedVertices =  new ArrayList<Vertex3D>(); 
		this.objNormals =  new ArrayList<Point3DH>(); 
		this.objFaces =  new ArrayList<ObjFace>(); 
	}
	private Color depthLerp(Vertex3D vertex, Color ambientLight, Color depthColor,double near, double far) {
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
	private Polygon depthLerp(Polygon polygon, Lighting lighting, Color depthColor,double near, double far) {

		Vertex3D v0 = new Vertex3D(polygon.get(0).getCameraPoint(), Color.WHITE);
		Vertex3D v1 = polygon.get(1).subtractCamera(polygon.get(0));
		Vertex3D v2 = polygon.get(2).subtractCamera(polygon.get(0));
		double sx = (v1.getCameraY() * v2.getCameraZ()) - (v2.getCameraY() * v1.getCameraZ());
		double sy = (v1.getCameraZ() * v2.getCameraX()) - (v2.getCameraZ() * v1.getCameraX());
		double sz = (v1.getCameraX() * v2.getCameraY()) - (v2.getCameraX() * v1.getCameraY());
		
		v1 = new Vertex3D(polygon.get(1).getCameraPoint(), Color.WHITE);
		v2 = new Vertex3D(polygon.get(2).getCameraPoint(), Color.WHITE);
		
		Vertex3D normal = new Vertex3D(sx,sy,sz,v0.getColor());
		normal = normal.normalize();

		Color lighting_calculation_color_0 = lighting.calculate_light(polygon, normal, v0);
		Color lighting_calculation_color_1 = lighting.calculate_light(polygon, normal, v1);
		Color lighting_calculation_color_2 = lighting.calculate_light(polygon, normal, v2);
		
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

	public void render(Transformation CTM, 
						Transformation worldToCamera, 
						Transformation cameraToView, 
						Transformation viewToScreen,
						Clipper clipper,
						Boolean depthColoring,
						Color ambientLight,
						Color depthColor,
						double near,
						double far,
						Lighting lighting) {
		// TODO: Implement.  All of the vertices, normals, and faces have been defined.
		// First, transform all of the vertices.		
		// Then, go through each face, break into triangles if necessary, and send each triangle to the renderer.
		// You may need to add arguments to this function, and/or change the visibility of functions in SimpInterpreter.
		Transformation camera_space = Transformation.multiply(worldToCamera, CTM);
		
		for(int i = 0; i < objVertices.size(); i++) {
			Vertex3D vertex = objVertices.get(i);
		
			//to camera space
			vertex = Transformation.multiply(camera_space, vertex);
			vertex = vertex.replaceCameraPoint(vertex.getPoint3D());

			if(depthColoring == true) {
				////Color color = depthLerp(vertex, ambientLight, depthColor, near, far);
				//vertex = vertex.replaceColor(color);
			}
			
			//to view space
			double z = vertex.getZ();
			vertex = Transformation.multiply(cameraToView, vertex);
			Point3DH p = vertex.getPoint3D();
			double x = vertex.getX();
			double y = vertex.getY();
			double w = p.getW();
			Point3DH newp = new Point3DH(x,y,z,w);
			vertex = vertex.replacePoint(newp);
			//vertex = vertex.replaceCameraPoint(newp);
			//to screen space
			vertex = Transformation.multiply(viewToScreen, vertex);
			
			/*if( i < objNormals.size() ) {
				Vertex3D normal = new Vertex3D(objNormals.get(i), Color.WHITE);
				Transformation view_space = Transformation.multiply(cameraToView, camera_space);
				Transformation screen_space = Transformation.multiply(viewToScreen, view_space);
				Transformation inverse_camera_space = Transformation.transpose(Transformation.inverse(camera_space));
				normal = Transformation.multiply(inverse_camera_space, normal).normalize();
				
				//normal = Transformation.multiply(viewToScreen, normal);
				
				vertex.setNormal(normal);
			}*/
			transformedVertices.add(vertex);
			//vertex = SimpInterpreter.transformToCamera(vertex);

		}
		ObjFace face;
		for(int i = 0; i <objFaces.size(); i++) {
			
			face = objFaces.get(i);
			ObjVertex v1 = face.get(0);
			ObjVertex v2 = face.get(1);
			ObjVertex v3 = face.get(2);
			Vertex3D[] vertexArray = new Vertex3D[3];
			vertexArray[0] = transformedVertices.get(v1.getVertexIndex());
			vertexArray[1] = transformedVertices.get(v2.getVertexIndex());
			vertexArray[2] = transformedVertices.get(v3.getVertexIndex());
			if(objNormals.size() > 0) {
				Vertex3D normal1 = new Vertex3D(objNormals.get(v1.getNormalIndex()%objNormals.size()), Color.WHITE);
				Vertex3D normal2 = new Vertex3D(objNormals.get(v2.getNormalIndex()%objNormals.size()), Color.WHITE);
				Vertex3D normal3 = new Vertex3D(objNormals.get(v3.getNormalIndex()%objNormals.size()), Color.WHITE);
				Transformation inverse_camera_space = Transformation.transpose(Transformation.inverse(camera_space));
				//Transformation.multiply(inverse_camera_space, camera_space).print_matrix();
				normal1 = Transformation.multiply(inverse_camera_space, normal1).normalize();
				normal2 = Transformation.multiply(inverse_camera_space, normal2).normalize();
				normal3 = Transformation.multiply(inverse_camera_space, normal3).normalize();
				
				vertexArray[0].setNormal(normal1);
				vertexArray[1].setNormal(normal2);
				vertexArray[2].setNormal(normal3);
			}
			//System.out.println(vertexArray[0].hasNormal());
			Polygon p = Polygon.make(vertexArray);
			if(depthColoring == true) {
				//p = depthLerp(p, lighting, depthColor, near, far);
				//vertex = vertex.replaceColor(color);
			}
			//want to use SimpInterpreter.polygon here....
			SimpInterpreter.renderPolygon(p);
			
		}
	}
	
	//private Polygon polygonForFace(ObjFace face) {
		// TODO: This function might be used in render() above.  Implement it if you find it handy.

	//}
	public Vertex3D[] returnFace(ObjFace face) {
		Vertex3D[] vertexArray = new Vertex3D[3];
		ObjVertex v1 = face.get(0);
		ObjVertex v2 = face.get(1);
		ObjVertex v3 = face.get(2);
		vertexArray[0] = objVertices.get(v1.getVertexIndex());
		vertexArray[1] = objVertices.get(v2.getVertexIndex());
		vertexArray[2] = objVertices.get(v3.getVertexIndex());
		return vertexArray;
	
	}
	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		
		for(int i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());
			
			// TODO: fill in action to take here.
			ObjVertex objV = new ObjVertex(vertexIndex, textureIndex,normalIndex);
			face.add(objV);
		}
		// TODO: fill in action to take here.
		//objFaces.add(face);
		ObjVertex v0 = face.get(0);
		for(int i = 0; i < face.size()-2; i++) {
			ObjFace triangle = new ObjFace();
			ObjVertex v1 = face.get(i+1);
			ObjVertex v2 = face.get(i+2);
			triangle.add(v0);
			triangle.add(v1);
			triangle.add(v2);
			objFaces.add(triangle);
		}
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		// TODO: write this.  subtokens[tokenIndex], if it exists, holds a string for an index.
		// use Integer.parseInt() to get the integer value of the index.
		// Be sure to handle both positive and negative indices.
		if(subtokens == null) {
			return -1;
		}
		int index = Integer.parseInt(subtokens[0]);
		
		if(index >= 0) {
			return tokenIndex + index -1;
		}
		else if(index < 0) {
			return baseForNegativeIndices - index-1;
		}
		return -1;
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);				
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		// TODO: fill in action to take here.
		objNormals.add(normal);
	}
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);		
		// TODO: fill in action to take here.
		Vertex3D vertex = new Vertex3D(point, color);
		objVertices.add(vertex);
		
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}