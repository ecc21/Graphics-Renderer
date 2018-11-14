package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

public class Lighting {

	private List<Light> lighting;
	private Color Ia;
	
	public Lighting(Color ambientLight) {
		lighting = new ArrayList<Light>(); 
		this.Ia = ambientLight;
	}
	public void add_light(Light light) {
		lighting.add(light);
	}
	public void set_ambient(Color color) {
		this.Ia = color;
	}
	public double getPointLength(Light light, Vertex3D point) {
		Vertex3D light_data = light.getLight();
		double di = Math.sqrt(
				Math.pow((light_data.getX() - point.getX()),2) + 
				Math.pow((light_data.getY() - point.getY()),2) +
				Math.pow((light_data.getZ() - point.getZ()),2)
				);
		return di;
	}
	//L is vector from light to surface
	public Vertex3D getL(Light light, Vertex3D point) {
		Vertex3D light_data = light.getLight();

		/*double magnitude = getPointLength(light, point);
		double nx = ((point.getX() - light_data.getX())/magnitude);
		double ny = ((point.getY() - light_data.getY())/magnitude);
		double nz = ((point.getZ() - light_data.getZ())/magnitude);*/
		
		/*double nx = ((light_data.getX() - point.getX())/magnitude);
		double ny = ((light_data.getY() - point.getY())/magnitude);
		double nz = ((light_data.getZ() - point.getZ())/magnitude);*/
		
		/*double nx = ((point.getX() - light_data.getX()));
		double ny = ((point.getY() - light_data.getY()));
		double nz = ((point.getZ() - light_data.getZ()));*/
		double nx = ((light_data.getX() - point.getX()));
		double ny = ((light_data.getY() - point.getY()));
		double nz = ((light_data.getZ() - point.getZ()));
		Vertex3D L = new Vertex3D(nx,ny,nz, point.getColor());
		Vertex3D normal_L = L.normalize();
		
		return normal_L;
	}
	public Vertex3D getR(Vertex3D L, Vertex3D normal) {
		Vertex3D normal_L = L.normalize();
		Vertex3D normal_normal = normal.normalize();
		double scale = 2*normal_L.dot(normal_normal);
		Vertex3D rhs = normal_normal.scale(scale);
		rhs = rhs.subtract(normal_L);
		return rhs.normalize();
	}
		
	//normal MUST BE NORMALZIED.
	public Color calculate_light(Polygon polygon, Vertex3D normal,  Vertex3D point) {
		//need: Ia, kd
		Color Kd = polygon.get(0).getColor();
		//System.out.println("Ia is: " + Ia);
		Color I = Kd;
		I = Kd.multiply(Ia);
		for(int i = 0; i < lighting.size(); i++) {
			Light light_i = lighting.get(i);
			Vertex3D light_data = light_i.getLight();
			Color light_color = light_data.getColor();
			//di = sqrt((x1-x0)^2 + (y1-y0)^2 + (z1-z0)^2)
			double di = getPointLength(light_i, point);
			double fatti = 1/(light_i.getA() + light_i.getB()*di);
			Color Iifatti = light_color.scale(fatti);
			
			Vertex3D L = getL(light_i, point);
			
			
			double NL = normal.getX()*L.getX() + normal.getY()*L.getY() + normal.getZ()*L.getZ();
			/*if(NL < 0) {
				NL = 0;
			}*/
			Color KdNL = Kd.scale(NL);
			//System.out.println("KdNL is KD: " + Kd + " * NL: " + NL);
			
			//ks
			Vertex3D R = getR(L,normal);
			R = R.normalize();
			//System.out.println("R is: " + R);
			Vertex3D V = new Vertex3D(-point.getX(),-point.getY(),-point.getZ(), Color.WHITE);
			V = V.normalize();
			double RdotV = V.dot(R);
			/*if(RdotV < 0) {
				RdotV = 0;
			}*/
			//System.out.println("RdotV is: " + RdotV);
			double Ks = polygon.getSpecularScalar();
			double p = polygon.getSpecularExonent();
			RdotV = Math.pow(RdotV, p);
			double KsRdotV = Ks*RdotV;
			//System.out.println("KsRdotV is: " + KsRdotV);
			
			KdNL = KdNL.add(new Color(KsRdotV,KsRdotV,KsRdotV));
			Color sum = Iifatti.multiply(KdNL);
			//System.out.println(sum);
			I = I.add(sum);
			
			}
		//System.out.println("I is: " + I);
		//I = I.scale(-1);
		return I;
	}
}
