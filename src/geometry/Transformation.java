package geometry;

public class Transformation {
	private final static int width = 4;
	private final static int height = 4;
	private double xform_matrix [][];
	
	public Transformation() {
		xform_matrix = new double[height][width];
		for(int y = 0; y <height; y++) {
			for(int x = 0; x < width; x++) {
				xform_matrix[y][x] = 0;
			}
		}
	}
	public int get_height() {
		return height;
	}
	public int get_width() {
		return width;
	}
	
	public void set_val(int y,int x,double val) {
		xform_matrix[y][x] = val;
	}
	public double get_val(int y,int x) {
		return xform_matrix[y][x];
	}
	//returns identity matrix

	public static Transformation identity() {
		// TODO Auto-generated method stub
		Transformation xform = new Transformation();
		xform.set_val(0,0,1);
		xform.set_val(1,1,1);
		xform.set_val(2,2,1);
		xform.set_val(3,3,1);
		return xform;
		
	}

	public static Transformation scale(double x_scale, double y_scale, double z_scale) {
		
		Transformation xform = identity();
		xform.set_val(0, 0, x_scale);
		xform.set_val(1, 1, y_scale);
		xform.set_val(2, 2, z_scale);
		xform.set_val(3, 3, 1);
		//xform.print_matrix();
		return xform;
	}
	public static Transformation translate(double x, double y, double z) {
		// TODO Auto-generated method stub
		Transformation xform = identity();
		xform.set_val(0, 3, x);
		xform.set_val(1, 3, y);
		xform.set_val(2, 3, z);
		xform.set_val(3, 3, 1);
		return xform;
	}
	//assume theta is in radians
	public static Transformation rotateX(double theta) {
		Transformation xform = identity();
		xform.set_val(1,1,Math.cos(theta));
		xform.set_val(1,2,-Math.sin(theta));
		xform.set_val(2,1,Math.sin(theta));
		xform.set_val(2,2,Math.cos(theta));
		//xform.print_matrix();
		return xform;
	}
	public static Transformation rotateY(double theta) {
		Transformation xform = identity();
		xform.set_val(0,0,Math.cos(theta));
		xform.set_val(0,2,Math.sin(theta));
		xform.set_val(2,0,-Math.sin(theta));
		xform.set_val(2,2,Math.cos(theta));
		//xform.print_matrix();
		return xform;
	}
	public static Transformation rotateZ(double theta) {
		Transformation xform = identity();
		xform.set_val(0,0,Math.cos(theta));
		xform.set_val(0,1,-Math.sin(theta));
		xform.set_val(1,0,Math.sin(theta));
		xform.set_val(1,1,Math.cos(theta));
		
		return xform;
	}

	public static Transformation transpose(Transformation xform) {
		Transformation trans = identity();
		for(int j = 0; j < xform.get_height(); j++) {
			for(int i = 0; i < xform.get_width(); i++) {
				double val = xform.get_val(i,j);
				trans.set_val(j, i, val);
			}
		}
		return trans;
	}
	public static Transformation multiply(Transformation left, Transformation right) {
		Transformation xform = new Transformation();
		//left.print_matrix();
		//right.print_matrix();
		for(int j = 0; j < height; j++) {
			for(int i = 0; i < width; i++) {
				double sum = 0;
				for(int k = 0; k < width; k++){
					sum = sum + left.get_val(j, k)*right.get_val(k, i);
				}
				xform.set_val(j, i, sum);
			}
		}
		//xform.print_matrix();
		return xform;
	}
	public static Vertex3D multiply(Transformation left, Vertex3D point) {
		double[] vector = new double[4];
		double[] new_point = new double[4];
		vector[0] = point.getX();
		vector[1] = point.getY();
		vector[2] = point.getZ();
		vector[3] = 1;
		new_point = multiply(left, vector);
		/*System.out.println(new_point[0]);
		System.out.println(new_point[1]);
		System.out.println(new_point[2]);*/
		Vertex3D vertex = new Vertex3D(new_point[0]/new_point[3],new_point[1]/new_point[3],new_point[2]/new_point[3], point.getColor());
		vertex = vertex.replaceCameraPoint(point.getCameraPoint());
		return vertex;
		
		
	}
	public static double[] multiply(Transformation left, double[] vector) {
		/*left.print_matrix();
		System.out.println(vector[0]);
		System.out.println(vector[1]);
		System.out.println(vector[2]);
		System.out.println(vector[3]);*/
		double[] points = new double[4];
		for(int j = 0; j < left.get_height(); j++) {
			for(int i = 0; i < 1; i++) {
				double sum = 0;
				for(int k = 0; k < left.get_width(); k++){
					sum = sum + left.get_val(j, k)*vector[k];
				}
				points[j] = sum;
				//System.out.println(points[j]);
				
			}
		}
		/*System.out.println(points[0]);
		System.out.println(points[1]);
		System.out.println(points[2]);
		System.out.println(points[3]);*/
		return points;
	}
	public void print_matrix() {
		
		System.out.println(xform_matrix[0][0] + " " + xform_matrix[0][1]+ " " +xform_matrix[0][2]+ " " +xform_matrix[0][3]);
		System.out.println(xform_matrix[1][0]+ " " +xform_matrix[1][1]+ " " +xform_matrix[1][2]+ " " +xform_matrix[1][3]);
		System.out.println(xform_matrix[2][0]+ " " +xform_matrix[2][1]+ " " +xform_matrix[2][2]+ " " +xform_matrix[2][3]);
		System.out.println(xform_matrix[3][0]+ " " +xform_matrix[3][1]+ " " +xform_matrix[3][2]+ " " +xform_matrix[3][3]);
	}
	


	/*public static double determinant(Transformation xform) {
		double m1 = xform.get_val(0, 0);
		double m2 = xform.get_val(0, 1);
		double m3 = xform.get_val(0, 2);
		double m4 = xform.get_val(0, 3);
		double[][] minor1 = new double[3][3];
		double[][] minor2 = new double[3][3];
		double[][] minor3 = new double[3][3];
		double[][] minor4 = new double[3][3];
		
		//get minors of matrix
		for(int j = 0; j <3; j++) {
			for(int i = 0; i < 3; i++) {
				minor1[j][i] = xform.get_val(j+1, i+1);
				minor4[j][i] = xform.get_val(j,i+1);
			}
		}
		for(int i = 0; i < 3; i ++) {
			minor2[0][i] = xform.get_val(0, i+1);
			minor3[2][i] = xform.get_val(3, i+1);
		}
		for(int j = 0; j < 2; j ++) {
			for(int i = 0; i < 3; i++) {
				minor2[j+1][i] = xform.get_val(j+2, i+1);
				minor3[j][i] = xform.get_val(j, i+1);
			}
		}
		
		//add up determinant of minors
		for(int j = 0; j < 2; j++) {
			for(int i = 0; i <3; i++) {
				
			}
		}
	}*/
	public static Transformation inverse(Transformation xform) {
		// TODO Auto-generated method stub
		double b00 = xform.get_val(1, 1)*xform.get_val(2,2)*xform.get_val(3,3) +
					 xform.get_val(1, 2)*xform.get_val(2,3)*xform.get_val(3,1) +
					 xform.get_val(1, 3)*xform.get_val(2,1)*xform.get_val(3,2) -
					 xform.get_val(1, 1)*xform.get_val(2,3)*xform.get_val(3,2) -
					 xform.get_val(1, 2)*xform.get_val(2,1)*xform.get_val(3,3) -
					 xform.get_val(1, 3)*xform.get_val(2,2)*xform.get_val(3,1);
		
		double b01 = xform.get_val(0, 1)*xform.get_val(2,3)*xform.get_val(3,2) +
					 xform.get_val(0, 2)*xform.get_val(2,1)*xform.get_val(3,3) +
					 xform.get_val(0, 3)*xform.get_val(2,2)*xform.get_val(3,1) -
					 xform.get_val(0, 1)*xform.get_val(2,2)*xform.get_val(3,3) -
					 xform.get_val(0, 2)*xform.get_val(2,3)*xform.get_val(3,1) -
					 xform.get_val(0, 3)*xform.get_val(2,1)*xform.get_val(3,2);
		
		double b02 = xform.get_val(0, 1)*xform.get_val(1,2)*xform.get_val(3,3) +
					 xform.get_val(0, 2)*xform.get_val(1,3)*xform.get_val(3,1) +
					 xform.get_val(0, 3)*xform.get_val(1,1)*xform.get_val(3,2) -
					 xform.get_val(0, 1)*xform.get_val(1,3)*xform.get_val(3,2) -
					 xform.get_val(0, 2)*xform.get_val(1,1)*xform.get_val(3,3) -
					 xform.get_val(0, 3)*xform.get_val(1,2)*xform.get_val(3,1);

		double b03 = xform.get_val(0, 1)*xform.get_val(1,3)*xform.get_val(2,2) +
					 xform.get_val(0, 2)*xform.get_val(1,1)*xform.get_val(2,3) +
					 xform.get_val(0, 3)*xform.get_val(1,2)*xform.get_val(2,1) -
					 xform.get_val(0, 1)*xform.get_val(1,2)*xform.get_val(2,3) -
					 xform.get_val(0, 2)*xform.get_val(1,3)*xform.get_val(2,1) -
					 xform.get_val(0, 3)*xform.get_val(1,1)*xform.get_val(2,2);

		double b10 = xform.get_val(1, 0)*xform.get_val(2,3)*xform.get_val(3,2) +
					 xform.get_val(1, 2)*xform.get_val(2,0)*xform.get_val(3,3) +
					 xform.get_val(1, 3)*xform.get_val(2,2)*xform.get_val(3,0) -
					 xform.get_val(1, 0)*xform.get_val(2,2)*xform.get_val(3,3) -
					 xform.get_val(1, 2)*xform.get_val(2,3)*xform.get_val(3,0) -
					 xform.get_val(1, 3)*xform.get_val(2,0)*xform.get_val(3,2);
		
		double b11 = xform.get_val(0, 0)*xform.get_val(2,2)*xform.get_val(3,3) +
					 xform.get_val(0, 2)*xform.get_val(2,3)*xform.get_val(3,0) +
					 xform.get_val(0, 3)*xform.get_val(2,0)*xform.get_val(3,2) -
					 xform.get_val(0, 0)*xform.get_val(2,3)*xform.get_val(3,2) -
					 xform.get_val(0, 2)*xform.get_val(2,0)*xform.get_val(3,3) -
					 xform.get_val(0, 3)*xform.get_val(2,2)*xform.get_val(3,0);
		
		double b12 = xform.get_val(0, 0)*xform.get_val(1,3)*xform.get_val(3,2) +
					 xform.get_val(0, 2)*xform.get_val(1,0)*xform.get_val(3,3) +
					 xform.get_val(0, 3)*xform.get_val(1,2)*xform.get_val(3,0) -
					 xform.get_val(0, 0)*xform.get_val(1,2)*xform.get_val(3,3) -
					 xform.get_val(0, 2)*xform.get_val(1,3)*xform.get_val(3,0) -
					 xform.get_val(0, 3)*xform.get_val(1,0)*xform.get_val(3,2);	
		
		double b13 = xform.get_val(0, 0)*xform.get_val(1,2)*xform.get_val(2,3) +
					 xform.get_val(0, 2)*xform.get_val(1,3)*xform.get_val(2,0) +
					 xform.get_val(0, 3)*xform.get_val(1,0)*xform.get_val(2,2) -
					 xform.get_val(0, 0)*xform.get_val(1,3)*xform.get_val(2,2) -
					 xform.get_val(0, 2)*xform.get_val(1,0)*xform.get_val(2,3) -
					 xform.get_val(0, 3)*xform.get_val(1,2)*xform.get_val(2,0);	
		
		double b20 = xform.get_val(1, 0)*xform.get_val(2,1)*xform.get_val(3,3) +
					 xform.get_val(1, 1)*xform.get_val(2,3)*xform.get_val(3,0) +
					 xform.get_val(1, 3)*xform.get_val(2,0)*xform.get_val(3,1) -
					 xform.get_val(1, 0)*xform.get_val(2,3)*xform.get_val(3,1) -
					 xform.get_val(1, 1)*xform.get_val(2,0)*xform.get_val(3,3) -
					 xform.get_val(1, 3)*xform.get_val(2,1)*xform.get_val(3,0);	
		
		double b21 = xform.get_val(0, 0)*xform.get_val(2,3)*xform.get_val(3,1) +
					 xform.get_val(0, 1)*xform.get_val(2,0)*xform.get_val(3,3) +
					 xform.get_val(0, 3)*xform.get_val(2,1)*xform.get_val(3,0) -
					 xform.get_val(0, 0)*xform.get_val(2,1)*xform.get_val(3,3) -
					 xform.get_val(0, 1)*xform.get_val(2,3)*xform.get_val(3,0) -
					 xform.get_val(0, 3)*xform.get_val(2,0)*xform.get_val(3,1);	
		
		double b22 = xform.get_val(0, 0)*xform.get_val(1,1)*xform.get_val(3,3) +
					 xform.get_val(0, 1)*xform.get_val(1,3)*xform.get_val(3,0) +
					 xform.get_val(0, 3)*xform.get_val(1,0)*xform.get_val(3,1) -
					 xform.get_val(0, 0)*xform.get_val(1,3)*xform.get_val(3,1) -
					 xform.get_val(0, 1)*xform.get_val(1,0)*xform.get_val(3,3) -
					 xform.get_val(0, 3)*xform.get_val(1,1)*xform.get_val(3,0);	
		
		double b23 = xform.get_val(0, 0)*xform.get_val(1,3)*xform.get_val(2,1) +
					 xform.get_val(0, 1)*xform.get_val(1,0)*xform.get_val(2,3) +
					 xform.get_val(0, 3)*xform.get_val(1,1)*xform.get_val(2,0) -
					 xform.get_val(0, 0)*xform.get_val(1,1)*xform.get_val(2,3) -
					 xform.get_val(0, 1)*xform.get_val(1,3)*xform.get_val(2,0) -
					 xform.get_val(0, 3)*xform.get_val(1,0)*xform.get_val(2,1);		
		
		double b30 = xform.get_val(1, 0)*xform.get_val(2,2)*xform.get_val(3,1) +
					 xform.get_val(1, 1)*xform.get_val(2,0)*xform.get_val(3,2) +
					 xform.get_val(1, 2)*xform.get_val(2,1)*xform.get_val(3,0) -
					 xform.get_val(1, 0)*xform.get_val(2,1)*xform.get_val(3,2) -
					 xform.get_val(1, 1)*xform.get_val(2,2)*xform.get_val(3,0) -
					 xform.get_val(1, 2)*xform.get_val(2,0)*xform.get_val(3,1);	
		
		double b31 = xform.get_val(0, 0)*xform.get_val(2,1)*xform.get_val(3,2) +
					 xform.get_val(0, 1)*xform.get_val(2,2)*xform.get_val(3,0) +
					 xform.get_val(0, 2)*xform.get_val(2,0)*xform.get_val(3,1) -
					 xform.get_val(0, 0)*xform.get_val(2,2)*xform.get_val(3,1) -
					 xform.get_val(0, 1)*xform.get_val(2,0)*xform.get_val(3,2) -
					 xform.get_val(0, 2)*xform.get_val(2,1)*xform.get_val(3,0);	
		
		double b32 = xform.get_val(0, 0)*xform.get_val(1,2)*xform.get_val(3,1) +
					 xform.get_val(0, 1)*xform.get_val(1,0)*xform.get_val(3,2) +
					 xform.get_val(0, 2)*xform.get_val(1,1)*xform.get_val(3,0) -
					 xform.get_val(0, 0)*xform.get_val(1,1)*xform.get_val(3,2) -
					 xform.get_val(0, 1)*xform.get_val(1,2)*xform.get_val(3,0) -
					 xform.get_val(0, 2)*xform.get_val(1,0)*xform.get_val(3,1);	
		
		double b33 = xform.get_val(0, 0)*xform.get_val(1,1)*xform.get_val(2,2) +
					 xform.get_val(0, 1)*xform.get_val(1,2)*xform.get_val(2,0) +
					 xform.get_val(0, 2)*xform.get_val(1,0)*xform.get_val(2,1) -
					 xform.get_val(0, 0)*xform.get_val(1,2)*xform.get_val(2,1) -
					 xform.get_val(0, 1)*xform.get_val(1,0)*xform.get_val(2,2) -
					 xform.get_val(0, 2)*xform.get_val(1,1)*xform.get_val(2,0);	
		
		Transformation B = identity();
		B.set_val(0, 0, b00);
		B.set_val(0, 1, b01);
		B.set_val(0, 2, b02);
		B.set_val(0, 3, b03);
		B.set_val(1, 0, b10);
		B.set_val(1, 1, b11);
		B.set_val(1, 2, b12);
		B.set_val(1, 3, b13);
		B.set_val(2, 0, b20);
		B.set_val(2, 1, b21);
		B.set_val(2, 2, b22);
		B.set_val(2, 3, b23);
		B.set_val(3, 0, b30);
		B.set_val(3, 1, b31);
		B.set_val(3, 2, b32);
		B.set_val(3, 3, b33);
		
		return B;
	}

}
