package shading;

import client.interpreter.Lighting;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.Shader;
import windowing.graphics.Color;

public class FlatShader implements Shader{

	Lighting lighting;
	public void set_lighting(Lighting lighting) {
		this.lighting = lighting;
	}
	@Override
	public Polygon shade_vertex(Polygon polygon) {
		//System.out.println("coming into shade_vertex");
		//System.out.println(polygon.get(0).hasNormal());
		//v2-v1 X v3-v1

		if(polygon.length() > 3) {
			System.out.println("we have a problem, houston");
		}
		Vertex3D v0 = polygon.get(0);
		Vertex3D v1 = polygon.get(1).subtractCamera(polygon.get(0));
		Vertex3D v2 = polygon.get(2).subtractCamera(polygon.get(0));
		
		/*Vertex3D v1 = polygon.get(1).subtract(polygon.get(0));
		Vertex3D v2 = polygon.get(2).subtract(polygon.get(0));		*/
		
		//double sx = v1y*v2z - v2y*v1z
		//double sy = v1z*v2x - v2z*v1x
		//double sz = v1x*v2y - v2x*v1y
		
		/*double sx = (v1.getY() * v2.getZ()) - (v2.getY() * v1.getZ());
		double sy = (v1.getZ() * v2.getX()) - (v2.getZ() * v1.getX());
		double sz = (v1.getX() * v2.getY()) - (v2.getX() * v1.getY());*/
		
		//normal vector
		double sx = (v1.getCameraY() * v2.getCameraZ()) - (v2.getCameraY() * v1.getCameraZ());
		double sy = (v1.getCameraZ() * v2.getCameraX()) - (v2.getCameraZ() * v1.getCameraX());
		double sz = (v1.getCameraX() * v2.getCameraY()) - (v2.getCameraX() * v1.getCameraY());
		
		v1 = polygon.get(1);
		v2 = polygon.get(2);
		
		Vertex3D normal = new Vertex3D(sx,sy,sz,v0.getColor());
		normal = normal.normalize();
		//System.out.println("face normal is: " + normal);
		/*if(v0.hasNormal() && v1.hasNormal() && v2.hasNormal()) {
			//System.out.println("has normal");
			//System.out.println("n_v0: "+ v0.getNormal()+"n_v1: "+ v1.getNormal()+"n_v2: "+ v2.getNormal());
			Vertex3D normal0 = v0.getNormal();
			Vertex3D normal1 = v1.getNormal();
			Vertex3D normal2 = v2.getNormal();
			sx = (normal0.getX() + normal1.getX() + normal2.getX())/3;
			sy = (normal0.getY() + normal1.getY() + normal2.getY())/3;
			sz = (normal0.getZ() + normal1.getZ() + normal2.getZ())/3;
			normal = new Vertex3D(sx,sy,sz,v0.getColor()).normalize();
		}*/
		//System.out.println("normal is: " + normal);
		//center of triangle
		/*double centerX = ((polygon.get(0).getX() + polygon.get(1).getX() + polygon.get(2).getX())/3);
		double centerY = ((polygon.get(0).getY() + polygon.get(1).getY() + polygon.get(2).getY())/3);
		double centerZ = ((polygon.get(0).getZ() + polygon.get(1).getZ() + polygon.get(2).getZ())/3);*/
		
		double centerX = ((polygon.get(0).getCameraX() + polygon.get(1).getCameraX() + polygon.get(2).getCameraX())/3);
		double centerY = ((polygon.get(0).getCameraY() + polygon.get(1).getCameraY() + polygon.get(2).getCameraY())/3);
		double centerZ = ((polygon.get(0).getCameraZ() + polygon.get(1).getCameraZ() + polygon.get(2).getCameraZ())/3);
		
		Vertex3D center_point = new Vertex3D(centerX,centerY,centerZ, v0.getColor());
		
		Vertex3D normalized_normal = normal.normalize();

		Color color = lighting.calculate_light(polygon, normalized_normal, center_point);
		//System.out.println("polygon color is: "+ color);
		v0 = polygon.get(0).replaceColor(color);
		v1 = polygon.get(1).replaceColor(color);
		v2 = polygon.get(2).replaceColor(color);
		Polygon new_polygon = Polygon.make(v0,v1,v2);
		return new_polygon;
	}
	@Override
	public Color shade_pixel(Polygon polygon, Vertex3D current) {
		
		//v2-v1 X v3-v1
		Color color = polygon.get(0).getColor();
		//System.out.println("printing pixel color: "+color);
		return color;
	}

}
