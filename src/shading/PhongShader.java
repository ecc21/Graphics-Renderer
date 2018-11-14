package shading;

import client.interpreter.Lighting;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.Shader;
import windowing.graphics.Color;

public class PhongShader implements Shader {

	Lighting lighting;
	public void set_lighting(Lighting lighting) {
		this.lighting = lighting;
	}
	@Override
	public Polygon shade_vertex(Polygon polygon) {
		//apply lighting model at each vertex
		
		//v2-v1 X v3-v1
		Vertex3D v0 = polygon.get(0);
		Vertex3D v1 = polygon.get(1).subtractCamera(polygon.get(0));
		Vertex3D v2 = polygon.get(2).subtractCamera(polygon.get(0));
		
		/*Vertex3D v1 = polygon.get(1).subtract(polygon.get(0));
		Vertex3D v2 = polygon.get(2).subtract(polygon.get(0));*/
		
		
		//double sx = v1y*v2z - v2y*v1z
		//double sy = v1z*v2x - v2z*v1x
		//double sz = v1x*v2y - v2x*v1y
		
		//normal vector
		/*double sx = (v1.getY() * v2.getZ()) - (v2.getY() * v1.getZ());
		double sy = (v1.getZ() * v2.getX()) - (v2.getZ() * v1.getX());
		double sz = (v1.getX() * v2.getY()) - (v2.getX() * v1.getY());*/
		
		double sx = (v1.getCameraY() * v2.getCameraZ()) - (v2.getCameraY() * v1.getCameraZ());
		double sy = (v1.getCameraZ() * v2.getCameraX()) - (v2.getCameraZ() * v1.getCameraX());
		double sz = (v1.getCameraX() * v2.getCameraY()) - (v2.getCameraX() * v1.getCameraY());
		
		Vertex3D normal = new Vertex3D(sx,sy,sz,v0.getColor());
		normal = normal.normalize();
		Vertex3D normalized_normal0 = normal.normalize();
		Vertex3D normalized_normal1 = normal.normalize();
		Vertex3D normalized_normal2 = normal.normalize();
		//System.out.println("face normal is: " + normal);
		/*if(v0.hasNormal()) {
			Vertex3D normal0 = v0.getNormal();
			normalized_normal0 = normal0.normalize();
			
		}
		if(v1.hasNormal()) {
			Vertex3D normal1 = v1.getNormal();
			normalized_normal1 = normal1.normalize();
			
		}
		if(v2.hasNormal()) {
			Vertex3D normal2 = v2.getNormal();
			normalized_normal2 = normal2.normalize();
			
		}*/
		
		double x0 = (polygon.get(0).getCameraX());
		double y0 = (polygon.get(0).getCameraY());
		double z0 = (polygon.get(0).getCameraZ());
		Vertex3D camera_p0 = new Vertex3D(x0,y0,z0, v0.getColor());
		
		double x1 = (polygon.get(1).getCameraX());
		double y1 = (polygon.get(1).getCameraY());
		double z1 = (polygon.get(1).getCameraZ());
		Vertex3D camera_p1 = new Vertex3D(x1,y1,z1, v0.getColor());
		
		double x2 = (polygon.get(2).getCameraX());
		double y2 = (polygon.get(2).getCameraY());
		double z2 = (polygon.get(2).getCameraZ());
		Vertex3D camera_p2 = new Vertex3D(x2,y2,z2, v0.getColor());

		Color color_v0 = lighting.calculate_light(polygon, normalized_normal0, camera_p0);
		Color color_v1 = lighting.calculate_light(polygon, normalized_normal1, camera_p1);
		Color color_v2 = lighting.calculate_light(polygon, normalized_normal2, camera_p2);
		
		//System.out.println(v1.getColor());
		v0 = polygon.get(0).replaceColor(color_v0);
		v1 = polygon.get(1).replaceColor(color_v1);
		v2 = polygon.get(2).replaceColor(color_v2);
		v0.setNormal(normalized_normal0);
		v1.setNormal(normalized_normal1);
		v2.setNormal(normalized_normal2);
		Polygon new_polygon = Polygon.make(v0,v1,v2);
		return new_polygon;
	}
	@Override
	public Color shade_pixel(Polygon polygon, Vertex3D current) {
	

		//lerp between 3 vectors
		/*Vertex3D v0 = polygon.get(0);
		Vertex3D v1 = polygon.get(1);	
		Vertex3D v2 = polygon.get(2);
		Vertex3D f0 = v0.subtract(current);
		Vertex3D f1 = v1.subtract(current);
		Vertex3D f2 = v2.subtract(current);
		
		double area = Vertex3D.cross(v0.subtract(v1), v0.subtract(v2)).distance();
		
		double a0 = Vertex3D.cross(f1, f2).distance()/area;
		double a1 = Vertex3D.cross(f2, f0).distance()/area;
		double a2 = Vertex3D.cross(f0, f1).distance()/area;
		
		double x = v0.getNormal().getX()*a0 + v1.getNormal().getX()*a1 + v2.getNormal().getX()*a2;
		double y = v0.getNormal().getY()*a0 + v1.getNormal().getY()*a1 + v2.getNormal().getY()*a2;
		double z = v0.getNormal().getZ()*a0 + v1.getNormal().getZ()*a1 + v2.getNormal().getZ()*a2;
		
		Vertex3D normal = new Vertex3D(x,y,z, Color.WHITE);
		
		double x0 = (polygon.get(0).getCameraX());
		double y0 = (polygon.get(0).getCameraY());
		double z0 = (polygon.get(0).getCameraZ());
		Vertex3D camera_p0 = new Vertex3D(x0,y0,z0, v0.getColor());
		
		double x1 = (polygon.get(1).getCameraX());
		double y1 = (polygon.get(1).getCameraY());
		double z1 = (polygon.get(1).getCameraZ());
		Vertex3D camera_p1 = new Vertex3D(x1,y1,z1, v0.getColor());
		
		double x2 = (polygon.get(2).getCameraX());
		double y2 = (polygon.get(2).getCameraY());
		double z2 = (polygon.get(2).getCameraZ());
		Vertex3D camera_p2 = new Vertex3D(x2,y2,z2, v0.getColor());
		
		x = x0*a0 + x1*a1 + x2*a2;
		y = y0*a0 + y1*a1 + y2*a2;
		z = z0*a0 + z1*a1 + z2*a2;
		
		current = new Vertex3D(x,y,z, current.getColor());
		
		Color color = lighting.calculate_light(polygon, normal, current);
		
		
		return color;*/
		//lerp between 3 vectors
		Vertex3D v0 = polygon.get(0);
		Vertex3D v1 = polygon.get(1);	
		Vertex3D v2 = polygon.get(2);
		Vertex3D f0 = v0.subtract(current);
		Vertex3D f1 = v1.subtract(current);
		Vertex3D f2 = v2.subtract(current);
		
		double area = Vertex3D.cross(v0.subtract(v1), v0.subtract(v2)).distance();
		
		double a0 = Vertex3D.cross(f1, f2).distance()/area;
		double a1 = Vertex3D.cross(f2, f0).distance()/area;
		double a2 = Vertex3D.cross(f0, f1).distance()/area;
		
		double r = v0.getColor().getR()*a0 + v1.getColor().getR()*a1 + v2.getColor().getR()*a2;
		double g = v0.getColor().getG()*a0 + v1.getColor().getG()*a1 + v2.getColor().getG()*a2;
		double b = v0.getColor().getB()*a0 + v1.getColor().getB()*a1 + v2.getColor().getB()*a2;
		
		Color color = new Color(r,g,b);
		
		return color;
	}

}
