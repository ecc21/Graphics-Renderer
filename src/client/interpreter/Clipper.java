package client.interpreter;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Clipper {
	private double xlow = 0;
	private double ylow = 0;
	private double xhigh = 0;
	private double yhigh = 0;
	private double hither = -1;
	private double yon = -200;
	
	
	public Clipper(double xlow,double ylow,double xhigh,double yhigh,double hither,double yon) {
		
		this.xlow = xlow;
		this.ylow = ylow;
		this.xhigh = xhigh;
		this.yhigh = yhigh;
		this.hither = hither;
		this.yon = yon;
	}
	public void setPlane( double xlow,double ylow,double xhigh,double yhigh,double hither,double yon) {
		this.xlow = xlow;
		this.ylow = ylow;
		this.xhigh = xhigh;
		this.yhigh = yhigh;
		this.hither = hither;
		this.yon = yon;
	}
	
	public Polygon polygonClip(Polygon p) {
		
		//clip hither
		
		//clip yon
		//clip left: xlow ylow, xlow yhigh inside > xlow, outside <xlow
		//clip bottom: xlow ylow, xhigh ylow
		//clip top: xlow yhigh, xhigh yhigh
		//clip right: xhigh ylow xhigh y high
	
		return p;
		
	}
	public Polygon clip_top(Polygon polygon) {
		int n = polygon.length();
		Vertex3D v0 = polygon.get(0);
		int k = -1;
		for(int i = 0; i < n; i ++) {
			Vertex3D vi = polygon.get(i);
			Vertex3D vi_next= polygon.get(i+1);
			//vi is outside of hither plane and vi+1 is inside hither plane
			if(vi.getY() > this.yhigh && vi_next.getY() < this.yhigh) {
				k = i;
			}
		}
		//if no k found
		if(k<0) {
			//System.out.println("no k found");
			//if v0 is outside, all are outside.
			if(v0.getY() > this.xhigh) {
				return null;
			}
			else {
				return polygon;
			}
		}
		//for a triangle polygon, a clip on 1 plane will return at most 4 vertices 
		List<Vertex3D> vertexList = new ArrayList<Vertex3D>();
		if(k>=0) {
			//System.out.println(k);
			Vertex3D vi = polygon.get(k);
			Vertex3D vi_next= polygon.get(k+1);
			Vertex3D q = line_intersect_y_plane(vi, vi_next,this.yhigh);
			//System.out.println("vi is: " + vi + " vi+1 is: " + vi_next + " intersection is: " + q);
			vertexList.add(q);
			//System.out.println(vertexList);
			for(int i = k+1; i < k+n+1; i++) {
				vi = polygon.get(i);
				//if vi inside
				if(vi.getY() <= this.yhigh) {
					vertexList.add(vi);
					//System.out.println(vertexList);
				}
				//if vi outside
				else if(vi.getY() > this.yhigh) {
					Vertex3D vi_prev = polygon.get(i-1);
					Vertex3D r = line_intersect_y_plane(vi, vi_prev,this.yhigh);
					vertexList.add(r);
					Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
 					Polygon p = Polygon.make(vertices);
					return p;
				}
			}
			Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
			Polygon p = Polygon.make(vertices);
			return p;	
		}
		else {
			System.out.println("what IS k: " + k);
		}
		System.out.println("shouldn't be here: clipper top");
		return polygon;
	}
	public Polygon clip_bottom(Polygon polygon) {
		int n = polygon.length();
		Vertex3D v0 = polygon.get(0);
		int k = -1;
		for(int i = 0; i < n; i ++) {
			Vertex3D vi = polygon.get(i);
			Vertex3D vi_next= polygon.get(i+1);
			//vi is outside of bottm plane and vi+1 is inside bottom plane
			if(vi.getY() < this.ylow && vi_next.getY() > this.ylow) {
				k = i;
			}
		}
		//if no k found
		if(k<0) {
			//if v0 is outside, all are outside.
			if(v0.getY() < this.ylow) {
				return null;
			}
			else {
				return polygon;
			}
		}
		//for a triangle polygon, a clip on 1 plane will return at most 4 vertices 
		List<Vertex3D> vertexList = new ArrayList<Vertex3D>();
		if(k>=0) {
			//System.out.println(k);
			Vertex3D vi = polygon.get(k);
			Vertex3D vi_next= polygon.get(k+1);
			Vertex3D q = line_intersect_y_plane(vi, vi_next,this.ylow);
			//System.out.println("vi is: " + vi + " vi+1 is: " + vi_next + " intersection is: " + q);
			vertexList.add(q);
			//System.out.println(vertexList);
			for(int i = k+1; i < k+n+1; i++) {
				vi = polygon.get(i);
				//if vi inside
				if(vi.getY() >= this.ylow) {
					vertexList.add(vi);
					//System.out.println("vertex list is: " +vertexList);
				}
				//if vi outside
				else if(vi.getY() < this.ylow) {
					Vertex3D vi_prev = polygon.get(i-1);
					Vertex3D r = line_intersect_y_plane(vi, vi_prev,this.ylow);
					vertexList.add(r);
					//System.out.println("vertex list is: " +vertexList);
					Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
 					Polygon p = Polygon.make(vertices);
					return p;
				}
			}
			Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
			Polygon p = Polygon.make(vertices);
			return p;	
		}
		else {
			System.out.println("what IS k: " + k);
		}
		System.out.println("shouldn't be here: clipper bottom");
		return polygon;
	}
	public Polygon clip_left(Polygon polygon) {
		int n = polygon.length();
		//System.out.println(n);
		Vertex3D v0 = polygon.get(0);
		//Vertex3D v3 = polygon.get(3);
		//System.out.println(v0 + " " + v3);
		int k = -1;
		for(int i = 0; i < n; i ++) {
			Vertex3D vi = polygon.get(i);
			Vertex3D vi_next= polygon.get(i+1);
			//vi is outside of hither plane and vi+1 is inside hither plane
			if(vi.getX() < this.xlow && vi_next.getX() > this.xlow) {
				k = i;
			}
		}
		//if no k found
		if(k<0) {
			//if v0 is outside, all are outside.
			if(v0.getX() < this.xlow) {
				return null;
			}
			else {
				return polygon;
			}
		}
		List<Vertex3D> vertexList = new ArrayList<Vertex3D>();
		if(k>=0) {
			//System.out.println(k);
			Vertex3D vi = polygon.get(k);
			Vertex3D vi_next= polygon.get(k+1);
			Vertex3D q = line_intersect_x_plane(vi, vi_next,this.xlow);
			//System.out.println("vi is: " + vi + " vi+1 is: " + vi_next + " intersection is: " + q);
			vertexList.add(q);
			//System.out.println("vertex list is: " + vertexList);
			for(int i = k+1; i < k+n+1; i++) {
				vi = polygon.get(i);
				//if vi inside
				if(vi.getX() >= this.xlow) {
					vertexList.add(vi);
					//System.out.println("vertex list is: " + vertexList);
				}
				//if vi outside
				else if(vi.getX() < this.xlow) {
					Vertex3D vi_prev = polygon.get(i-1);
					Vertex3D r = line_intersect_x_plane(vi, vi_prev,this.xlow);
					//System.out.println("vi is: " + vi + " vi-1 is: " + vi_prev + " intersection is: " + r);
					vertexList.add(r);
					//System.out.println("vertex list is: " + vertexList);
					Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
 					Polygon p = Polygon.make(vertices);
					return p;
				}
			}
			//System.out.println("shouldnt be here...");
			Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
			Polygon p = Polygon.make(vertices);
			return p;	
		}
		else {
			System.out.println("what IS k: " + k);
		}
		System.out.println("shouldn't be here: clipper left");
		return polygon;
	}
	public Polygon clip_right(Polygon polygon) {
		int n = polygon.length();
		Vertex3D v0 = polygon.get(0);
		int k = -1;
		for(int i = 0; i < n; i ++) {
			Vertex3D vi = polygon.get(i);
			Vertex3D vi_next= polygon.get(i+1);
			//vi is outside of hither plane and vi+1 is inside hither plane
			if(vi.getX() > this.xhigh && vi_next.getX() < this.xhigh) {
				k = i;
			}
		}
		//if no k found
		if(k<0) {
			//System.out.println("no k found");
			//if v0 is outside, all are outside.
			if(v0.getX() > this.xhigh) {
				return null;
			}
			else {
				return polygon;
			}
		}
		//for a triangle polygon, a clip on 1 plane will return at most 4 vertices 
		List<Vertex3D> vertexList = new ArrayList<Vertex3D>();
		if(k>=0) {
			//System.out.println(k);
			Vertex3D vi = polygon.get(k);
			Vertex3D vi_next= polygon.get(k+1);
			Vertex3D q = line_intersect_x_plane(vi, vi_next,this.xhigh);
			//System.out.println("vi is: " + vi + " vi+1 is: " + vi_next + " intersection is: " + q);
			vertexList.add(q);
			//System.out.println(vertexList);
			for(int i = k+1; i < k+n+1; i++) {
				vi = polygon.get(i);
				//if vi inside
				if(vi.getX() <= this.xhigh) {
					vertexList.add(vi);
					//System.out.println(vertexList);
				}
				//if vi outside
				else if(vi.getX() > this.xhigh) {
					Vertex3D vi_prev = polygon.get(i-1);
					//System.out.println("vi is " + vi + " and vi_prev is "+ vi_prev);
					Vertex3D r = line_intersect_x_plane(vi, vi_prev,this.xhigh);
					if(r != null) {
						vertexList.add(r);
					}
					
					//System.out.println(vertexList);
					Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
 					Polygon p = Polygon.make(vertices);
					return p;
				}
			}
			Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
			Polygon p = Polygon.make(vertices);
			return p;	
		}
		else {
			System.out.println("what IS k: " + k);
		}
		System.out.println("shouldn't be here: clipper right");
		return polygon;
	}
	public Polygon clip_hither(Polygon polygon) {
		int n = polygon.length();
		Vertex3D v0 = polygon.get(0);
		int k = -1;
		for(int i = 0; i < n; i ++) {
			Vertex3D vi = polygon.get(i);
			Vertex3D vi_next= polygon.get(i+1);
			//vi is outside of hither plane and vi+1 is inside hither plane
			if(vi.getZ() > this.hither && vi_next.getZ() < this.hither) {
				k = i;
			}
		}
		//if no k found
		if(k<0) {
			//if v0 is outside, all are outside.
			if(v0.getZ() > this.hither) {
				return null;
			}
			else {
				return polygon;
			}
		}
		//for a triangle polygon, a clip on 1 plane will return at most 4 vertices 
		List<Vertex3D> vertexList = new ArrayList<Vertex3D>();
		if(k>=0) {
			//System.out.println(k);
			Vertex3D vi = polygon.get(k);
			Vertex3D vi_next= polygon.get(k+1);
			Vertex3D q = line_intersect_z_plane(vi, vi_next,this.hither);
			//System.out.println("vi is " + vi+ "vi_next is"+ vi_next);
			//System.out.println("vi is: " + vi + " vi+1 is: " + vi_next + " intersection is: " + q);
			vertexList.add(q);
			//System.out.println(vertexList);
			for(int i = k+1; i < k+n+1; i++) {
				vi = polygon.get(i);
				//if vi inside
				if(vi.getZ() <= this.hither) {
					vertexList.add(vi);
					//System.out.println(vertexList);
				}
				//if vi outside
				else if(vi.getZ() > this.hither) {
					Vertex3D vi_prev = polygon.get(i-1);
					Vertex3D r = line_intersect_z_plane(vi, vi_prev,this.hither);
					vertexList.add(r);
					Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
					
 					Polygon p = Polygon.make(vertices);
					return p;
				}
			}
			Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
			Polygon p = Polygon.make(vertices);
			return p;	
		}
		else {
			System.out.println("what IS k: " + k);
		}
		System.out.println("shouldn't be here: clipper hither");
		return polygon;
	}
	public Polygon clip_yon(Polygon polygon) {
		int n = polygon.length();
		Vertex3D v0 = polygon.get(0);
		//System.out.println(v0);
		int k = -1;
		for(int i = 0; i < n; i ++) {
			Vertex3D vi = polygon.get(i);
			Vertex3D vi_next= polygon.get(i+1);
			//vi is outside of yon plane and vi+1 is inside yon plane
			if(vi.getZ() < this.yon && vi_next.getZ() > this.yon) {
				k = i;
			}
		}
		//if no k found
		if(k<0) {
			//if v0 is outside, all are outside.
			if(v0.getZ() < yon) {
				return null;
			}
			else {
				return polygon;
			}
		}

		//for a triangle polygon, a clip on 1 plane will return at most 4 vertices 
		//Vertex3D[] vertexList = new Vertex3D[4];
		List<Vertex3D> vertexList = new ArrayList<Vertex3D>();
		if(k>=0) {
			//System.out.println(k);
			Vertex3D vi = polygon.get(k);
			Vertex3D vi_next= polygon.get(k+1);
			Vertex3D q = line_intersect_z_plane(vi, vi_next,this.yon);
			//System.out.println("vi is: " + vi + " vi+1 is: " + vi_next + " intersection is: " + q);
			vertexList.add(q);
			//System.out.println(vertexList);
			for(int i = k+1; i < k+n+1; i++) {
				vi = polygon.get(i);
				//if vi inside
				if(vi.getZ() >= this.yon) {
					vertexList.add(vi);
					//System.out.println(vertexList);
				}
				//if vi outside
				else if(vi.getZ() < this.yon) {
					Vertex3D vi_prev = polygon.get(i-1);
					Vertex3D r = line_intersect_z_plane(vi, vi_prev,this.yon);
					vertexList.add(r);
					Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
 					Polygon p = Polygon.make(vertices);
					return p;
				}
			}
			Vertex3D[] vertices = vertexList.toArray(new Vertex3D[vertexList.size()]);
			Polygon p = Polygon.make(vertices);
			return p;	
		}
		else {
			System.out.println("what IS k: " + k);
		}
		System.out.println("shouldn't be here: clipper yon");
		return polygon;
	}
	
	//entering function assumes p1, p2 intersect hither plane in the middle somewhere
	public Vertex3D line_intersect_z_plane(Vertex3D p1, Vertex3D p2, double z) {
		double max_z;
		double min_z;
		Color color;
		if(p1.getZ() > p2.getZ()) {
			max_z = p1.getZ();
			min_z = p2.getZ();
			color = p2.getColor();
		}
		else {
			max_z = p2.getZ();
			min_z = p1.getZ();
			color = p1.getColor();
		}
		if(min_z < z && max_z > z) {
			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();
			//r(t) = <p1.x p1.y p1.z> + t<dx, dy, dz> 
			//z = p1.z + t*dz;
			double t = (z-p1.getZ())/dz;
			/*System.out.println("dz is: " + dz);
			System.out.println("t is: " + t);
			System.out.println("x1 is: " +  p1.getX()+ "and x2 is: "+ p2.getX());
			System.out.println("dx is: " + dx);*/
			double x = p1.getX() + t*dx;
			double y = p1.getY() + t*dy;
			/*double x = p1.getX();
			double y = p1.getY();*/
			//point z == z, since its on clipping plane z
			
			Vertex3D p = new Vertex3D(x,y,z,color);
			
			return p;
		}
		return null;
	}
	public Vertex3D line_intersect_x_plane(Vertex3D p1, Vertex3D p2, double x) {
		double max_x;
		double min_x;
		Color color;
		if(p1.getX() > p2.getX()) {
			max_x = p1.getX();
			min_x = p2.getX();
			color = p2.getColor();
		}
		else {
			max_x = p2.getX();
			min_x = p1.getX();
			color = p1.getColor();
		}
		if(min_x < x && max_x > x) {
			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();
			//r(t) = <p1.x p1.y p1.z> + t<dx, dy, dz> 
			//x = p1.x + t*dx;
			double t = (x-p1.getX())/dx;
			double z = p1.getZ() + t*dz;
			double y = p1.getY() + t*dy;
			/*double z = p1.getZ();
			double y = p1.getY();*/
			Vertex3D p = new Vertex3D(x,y,z,color);
			
			return p;
		}
		return null;
	}
	public Vertex3D line_intersect_y_plane(Vertex3D p1, Vertex3D p2, double y) {
		double max_y;
		double min_y;
		Color color;
		if(p1.getY() > p2.getY()) {
			max_y = p1.getY();
			min_y = p2.getY();
			color = p2.getColor();
		}
		else {
			max_y = p2.getY();
			min_y = p1.getY();
			color = p1.getColor();
		}
		if(min_y < y && max_y > y) {
			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();
			//r(t) = <p1.x p1.y p1.z> + t<dx, dy, dz> 
			//y = p1.y + t*dy;
			double t = (y-p1.getY())/dy;
			double x = p1.getX() + t*dx;
			double z = p1.getZ() + t*dz;
			/*double x = p1.getX();
			double z = p1.getZ();*/
			//point y == yz, since its on clipping plane y
			Vertex3D p = new Vertex3D(x,y,z,color);
			
			return p;
		}
		return null;
	}
}
