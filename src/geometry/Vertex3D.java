package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {
	protected Point3DH point;
	protected Color color;
	protected Point3DH cameraPoint;
	protected Vertex3D normal;
	private boolean has_normal = false;
	
	public Vertex3D(Point3DH point, Color color) {
		super();
		this.point = point;
		this.color = color;
		this.cameraPoint = point;
		this.has_normal = false;
	}
	public Vertex3D(Point3DH point, Color color, Point3DH cameraPoint) {
		super();
		this.point = point;
		this.color = color;
		this.cameraPoint = cameraPoint;
		this.has_normal = false;
	}
	public Vertex3D(Point3DH point, Color color, Point3DH cameraPoint, Vertex3D normal) {
		super();
		this.point = point;
		this.color = color;
		this.cameraPoint = cameraPoint;
		this.normal = normal;
		if(normal != null) {
			this.has_normal = true;
		}

	}
	public Vertex3D(double x, double y, double z, Color color) {
		this(new Point3DH(x, y, z), color);
	}

	public Vertex3D() {
	}
	public double getX() {
		return point.getX();
	}
	public double getY() {
		return point.getY();
	}
	public double getZ() {
		return point.getZ();
	}
	public double getCameraSpaceZ() {
		return getZ();
	}
	public Point getPoint() {
		return point;
	}
	public Point3DH getPoint3D() {
		return point;
	}
	public Point3DH getCameraPoint() {
		return cameraPoint;
	}
	public int getIntX() {
		return (int) Math.round(getX());
	}
	public int getIntY() {
		return (int) Math.round(getY());
	}
	public int getIntZ() {
		return (int) Math.round(getZ());
	}
	
	public Color getColor() {
		return color;
	}
	
	public Vertex3D rounded() {
		return new Vertex3D(point.round(), color);
	}
	public Vertex3D add(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.add(other3D.getPoint()),
				            color.add(other3D.getColor()),
				            cameraPoint);
	}
	public Vertex3D subtract(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()),
				            cameraPoint);
	}
	public Vertex3D subtractCamera(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point,
				            color,
				            cameraPoint.subtract(other3D.getCameraPoint()));
	}
	public Vertex3D scale(double scalar) {
		return new Vertex3D(point.scale(scalar),
				            color.scale(scalar),
				            cameraPoint);
	}
	public Vertex3D replacePoint(Point3DH newPoint) {
		return new Vertex3D(newPoint, color,cameraPoint);
	}
	public Vertex3D replaceColor(Color newColor) {
		return new Vertex3D(point, newColor,cameraPoint, normal);
	}
	public Vertex3D replaceCameraPoint(Point3DH newCameraPoint) {
		return new Vertex3D(point, color,newCameraPoint);
	}
	public Vertex3D euclidean() {
		Point3DH euclidean = getPoint3D().euclidean();
		return replacePoint(euclidean);
	}
	//distance from origin
	public double distance() {
		double di = Math.sqrt(
				Math.pow(this.getX(),2) + 
				Math.pow(this.getY(),2) +
				Math.pow(this.getZ(),2)
				);
		return di;
	}
	//assuming vertex is the end point, current vertex is the start point
	public Vertex3D normalize() {
		double di = this.distance();
		if(di == 0) {
			return this;
		}
		/*double nx = ((this.getX() - vertex.getX())/di);
		double ny = ((this.getY() - vertex.getY())/di);
		double nz = ((this.getZ() - vertex.getZ())/di);*/
		
		double nx = ((this.getX())/di);
		double ny = ((this.getY())/di);
		double nz = ((this.getZ())/di);
		Vertex3D normal = new Vertex3D(nx,ny,nz,this.getColor());
		normal = normal.replaceCameraPoint(this.getCameraPoint());
		return normal;
	}
	public static Vertex3D cross(Vertex3D v1, Vertex3D v2) {
		double sx = (v1.getY() * v2.getZ()) - (v2.getY() * v1.getZ());
		double sy = (v1.getZ() * v2.getX()) - (v2.getZ() * v1.getX());
		double sz = (v1.getX() * v2.getY()) - (v2.getX() * v1.getY());
		Vertex3D vertex = new Vertex3D(sx,sy,sz,v1.getColor());
		return vertex;
	}
	public double dot(Vertex3D v) {
		return this.getX()*v.getX() + this.getY()*v.getY() + this.getZ()*v.getZ();
	}
	
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	public String toIntString() {
		return "(" + getIntX() + ", " + getIntY() + getIntZ() + ", " + ", " + getColor().toIntString() + ")";
	}
	public double getCameraX() {
		return cameraPoint.getX();
	}
	public double getCameraY() {
		return cameraPoint.getY();
	}
	public double getCameraZ() {
		return cameraPoint.getZ();
	}
	public boolean hasNormal() {
		return this.has_normal;
	}
	
	public void setNormal(double x, double y, double z) {
		
		this.normal = new Vertex3D(x,y,z,this.getColor());
		this.has_normal = true;
	}
	public void setNormal(Vertex3D normal) {
		//System.out.println("setting normal");
		this.normal = normal;
		this.has_normal = true;
	}
	public Vertex3D getNormal() {
		return this.normal;
	}
}
