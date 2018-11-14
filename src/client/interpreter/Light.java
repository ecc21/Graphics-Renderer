package client.interpreter;

import geometry.Vertex3D;
import windowing.graphics.Color;

public class Light {
	private double A = 0;
	private double B = 0;
	private Vertex3D light;
	
	public Light(double A,double B,Vertex3D light) {
		this.A = A;
		this.B = B;
		this.light = light;
		
	}
	public double getA() {
		return A;
	}
	public double getB() {
		return B;
	}
	public Vertex3D getLight() {
		return light;
	}

}
