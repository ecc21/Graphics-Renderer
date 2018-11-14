package polygon;



import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class FilledPolygonRenderer implements PolygonRenderer {

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader shader) {
		// TODO Auto-generated method stub
		//draw polygon
		//fill it in
		
		/*Vertex3D[] vertexList = new Vertex3D[polygon.length()];
		for(int i = 0; i < polygon.length(); i++) {
			vertexList[i] = polygon.get(i);
		}*/
		polygon = shader.shade_vertex(polygon);
		//System.out.println("Coming out of shade vertex: filled polygon");
		Vertex3D v1 = polygon.get(0);
		Vertex3D v2 = polygon.get(1);
		Vertex3D v3 = polygon.get(2);
		Vertex3D v4;
		Vertex3D v5;
		int ymin = Math.min(v1.getIntY(), Math.min(v2.getIntY(), v3.getIntY()));
		if(polygon.length() == 3) {
			if(ymin == v1.getIntY()) {
				//System.out.println("v1 smallest y");
				triangle_renderer(v1,v2,v3,drawable, shader);
			}
			else if(ymin == v2.getIntY()) {
				//System.out.println("v2 smallest y");
				triangle_renderer(v2,v1,v3,drawable, shader);
			}
			else if(ymin == v3.getIntY()) {
				//System.out.println("v3 smallest y");
				triangle_renderer(v3,v2,v1,drawable, shader);
			}
		}
		if(polygon.length() == 4 ) {
			v4 = polygon.get(3);
			ymin = Math.min(v1.getIntY(), Math.min(v2.getIntY(), Math.min(v3.getIntY(),v4.getIntY())));
			if(ymin == v1.getIntY()) {
				//System.out.println("v1 smallest y");
				triangle_renderer(v1,v2,v3,drawable, shader);
				triangle_renderer(v1,v3,v4,drawable, shader);
			}
			else if(ymin == v2.getIntY()) {
				//System.out.println("v2 smallest y");
				triangle_renderer(v2,v1,v3,drawable, shader);
				triangle_renderer(v2,v3,v4,drawable, shader);
				
			}
			else if(ymin == v3.getIntY()) {
				//System.out.println("v3 smallest y");
				triangle_renderer(v3,v1,v2,drawable, shader);
				triangle_renderer(v2,v2,v4,drawable, shader);
			}
			else if(ymin == v4.getIntY()) {
				//System.out.println("v3 smallest y");
				triangle_renderer(v4,v1,v2,drawable, shader);
				triangle_renderer(v4,v2,v3,drawable, shader);
			}
			
		}
		if(polygon.length() == 5 ) {
			v4 = polygon.get(3);
			v5 = polygon.get(4);
			ymin = Math.min(v1.getIntY(), Math.min(v2.getIntY(), Math.min(v3.getIntY(), Math.min(v4.getIntY(),v5.getIntY()))));
			if(ymin == v1.getIntY()) {
				//System.out.println("v1 smallest y");
				triangle_renderer(v1,v2,v3,drawable, shader);
				triangle_renderer(v1,v3,v4,drawable, shader);
				triangle_renderer(v1,v4,v5,drawable, shader);
			}
			else if(ymin == v2.getIntY()) {
				//System.out.println("v2 smallest y");
				triangle_renderer(v2,v1,v3,drawable, shader);
				triangle_renderer(v2,v3,v4,drawable, shader);
				triangle_renderer(v2,v4,v5,drawable, shader);
			}
			else if(ymin == v3.getIntY()) {
				//System.out.println("v3 smallest y");
				triangle_renderer(v3,v1,v2,drawable, shader);
				triangle_renderer(v3,v2,v4,drawable, shader);
				triangle_renderer(v3,v4,v5,drawable, shader);
			}
			else if(ymin == v4.getIntY()) {
				//System.out.println("v3 smallest y");
				triangle_renderer(v4,v1,v2,drawable, shader);
				triangle_renderer(v4,v2,v3,drawable, shader);
				triangle_renderer(v4,v3,v5,drawable, shader);
			}
			else if(ymin == v5.getIntY()) {
				//System.out.println("v3 smallest y");
				triangle_renderer(v5,v1,v2,drawable, shader);
				triangle_renderer(v5,v2,v3,drawable, shader);
				triangle_renderer(v5,v3,v4,drawable, shader);
			}
		}
		
		
		

		/*int num_vertices = polygon.length();
		Vertex3D vertex_array[] = new Vertex3D[num_vertices];
		for(int i = 0 ; i < num_vertices; i++) {
			vertex_array[] = 
		}		
		
		int min_count = 0;
		int minlocation[] = new int[num_vertices];
		//get min
		for(int i = 0 ; i < num_vertices; i++) {
			int testmin = polygon.get(i).getIntY();
			if(ymin >= testmin) {
				ymin = testmin;
			}
		}
		//check for min location(s)
		int min_counter = 0;
		for(int i = 0 ; i < num_vertices; i++) {
			int testmin = polygon.get(i).getIntY();
			if(ymin == testmin) {
				minlocation[min_counter] = i;	
				min_counter++;
			}
		}
		
		//get mins
		v1 = polygon.get(minlocation[0]);*/
		
		
		//System.out.println(ymin);


	}
	
	
	private double calc_color_slope(double x1, double x2, double color1, double color2) {
		double deltaX = x2 - x1;
		double deltaColor = color2 - color1;
		if(deltaColor == 0) {
			return 0;
		}
		return deltaColor/deltaX;
	}
	
	/* assume v1 is top vector */
	public void triangle_renderer(Vertex3D v1,Vertex3D v2,Vertex3D v3, Drawable drawable, Shader shader) {
		
		if(v1.getIntY() == v2.getIntY()) {
			straight_triangle_renderer(v3,v1,v2,drawable,shader);
			
		}
		else if(v1.getIntY() == v3.getIntY()) {
			straight_triangle_renderer(v2,v1,v3,drawable,shader);
		}

		
		else {
			int min_x = Math.min(v2.getIntX(), v3.getIntX());	
			Vertex3D left_vertex;
			Vertex3D right_vertex;
			double dx = v2.getIntX() - v3.getIntX();
			double dy = v2.getIntY() - v3.getIntY();
			double slope;
			if(dx == 0) {
				slope = 0;
			}
			else {
				slope = dy/dx;
			}
			double intercept = v2.getIntY() - slope*v2.getIntX();
			double boundary_point_Y = slope*v1.getIntX() + intercept;
			double boundary_point_X = (v1.getIntY() - intercept)/slope;
			if(v1.getIntY()> boundary_point_Y ) {
				if(min_x == v2.getIntX()) {
					left_vertex = v3;
					right_vertex = v2;
				}
				else {
					left_vertex = v2;
					right_vertex = v3;
				}
			}	
			else {
				if(min_x == v2.getIntX()) {
					left_vertex = v2;
					right_vertex = v3;
				}
				else {
					left_vertex = v3;
					right_vertex = v2;
				}
			}
			if(dx == 0) {
				if(v1.getX() > v2.getX()) {
					if(v2.getY()>v3.getY()) {
						left_vertex = v3;
						right_vertex = v2;
					}
					else {
						left_vertex = v2;
						right_vertex = v3;
					}
					
				}
				else {
					if(v2.getY()>v3.getY()) {
						left_vertex = v2;
						right_vertex = v3;
					}
					else {
						left_vertex = v3;
						right_vertex = v2;
					}
				}
			}
			double leftdeltaX,leftdeltaY,rightdeltaX,rightdeltaY, leftslope = 0,rightslope = 0;
			double leftX = v1.getIntX();
			double rightX = v1.getIntX();
			double y = v1.getIntY();
			
			leftdeltaX = left_vertex.getIntX() - v1.getIntX();
			leftdeltaY = left_vertex.getIntY() - v1.getIntY();
			leftslope = leftdeltaY/leftdeltaX;
			double inverse_leftslope;
			if(leftdeltaX == 0) {
				inverse_leftslope = 0;
			}
			else {
				inverse_leftslope = 1/leftslope;
			}
			int leftargbColor1 = v1.getColor().asARGB();
			int argbColor2 = left_vertex.getColor().asARGB();
			Color color1 = Color.fromARGB(leftargbColor1);
			Color color2 = Color.fromARGB(argbColor2);
			double leftColor1R = color1.getIntR();
			double leftColor1G = color1.getIntG();
			double leftColor1B = color1.getIntB();
			double leftColor2R = color2.getIntR();
			double leftColor2G = color2.getIntG();
			double leftColor2B = color2.getIntB();
			double leftRSlope = calc_color_slope(v1.getIntY(),left_vertex.getIntY(), leftColor1R,leftColor2R);
			double leftGSlope = calc_color_slope(v1.getIntY(),left_vertex.getIntY(), leftColor1G,leftColor2G);
			double leftBSlope = calc_color_slope(v1.getIntY(),left_vertex.getIntY(), leftColor1B,leftColor2B);

	
			rightdeltaX = right_vertex.getIntX() - v1.getIntX();
			rightdeltaY = right_vertex.getIntY() - v1.getIntY();
			rightslope = rightdeltaY/rightdeltaX;
			double inverse_rightslope;
			if(rightdeltaX == 0) {
				inverse_rightslope = 0;
			}
			else {
				inverse_rightslope = 1/rightslope;
			}
			int rightargbColor1 = v1.getColor().asARGB();
			argbColor2 = right_vertex.getColor().asARGB();
			color1 = Color.fromARGB(rightargbColor1);
			color2 = Color.fromARGB(argbColor2);
			double rightColor1R = color1.getIntR();
			double rightColor1G = color1.getIntG();
			double rightColor1B = color1.getIntB();
			double rightColor2R = color2.getIntR();
			double rightColor2G = color2.getIntG();
			double rightColor2B = color2.getIntB();
			double rightRSlope = calc_color_slope(v1.getIntY(),right_vertex.getIntY(), rightColor1R,rightColor2R);
			double rightGSlope = calc_color_slope(v1.getIntY(),right_vertex.getIntY(), rightColor1G,rightColor2G);
			double rightBSlope = calc_color_slope(v1.getIntY(),right_vertex.getIntY(), rightColor1B,rightColor2B);
			
			double leftZ = v1.getZ();
			double rightZ = v1.getZ();
			double leftDeltaZ = left_vertex.getZ() - v1.getZ(); 
			double rightDeltaZ = right_vertex.getZ() - v1.getZ();
			double leftZSlope = leftDeltaZ/leftdeltaY;
			double rightZSlope = rightDeltaZ/rightdeltaY;
			
			int min_y = Math.min(left_vertex.getIntY(), right_vertex.getIntY());

			//draw first half
			while(y < min_y) {
				int leftcolorX = leftargbColor1;
				int rightcolorX = rightargbColor1;
				color1 = Color.fromARGB(leftcolorX);
				color2 = Color.fromARGB(rightcolorX);	
				double color1R = color1.getIntR();
				double color1G = color1.getIntG();
				double color1B = color1.getIntB();
				double color2R = color2.getIntR();
				double color2G = color2.getIntG();
				double color2B = color2.getIntB();	
				double rSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1R,color2R);
				double gSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1G,color2G);
				double bSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1B,color2B);
				
				double deltaZ = rightZ - leftZ;
				double zSlope;
				if(rightX - leftX == 0) {
					zSlope = 0;
				}
				else {
					zSlope = deltaZ/(rightX - leftX);
				}
				double z = leftZ;
				// System.out.println(leftX + " " + rightX);
				for(int x = (int) Math.round(leftX); x <(int)Math.round(rightX);x++) {
					//System.out.println("something wrong here maybe?");
					Polygon polygon = Polygon.make(v1,left_vertex,right_vertex);
					Vertex3D current = new Vertex3D(x,y,z, Color.fromARGB(leftcolorX));
					Color color = shader.shade_pixel(polygon,current);
					int color_argb = color.asARGB();
					drawable.setPixel(x, (int)y, z, color_argb);
					//drawable.setPixel(x, (int)y, z, leftcolorX);
					z += zSlope;
					
					color1R += rSlope;
					color1G += gSlope;
					color1B += bSlope;
					leftcolorX = Color.makeARGB((int)Math.round(color1R), (int)Math.round(color1G), (int)Math.round(color1B));
				}
				
				leftX += inverse_leftslope;
				rightX += inverse_rightslope;
				
				leftZ += leftZSlope;
				rightZ += rightZSlope;
				
				leftColor1R += leftRSlope;
				leftColor1G += leftGSlope;
				leftColor1B += leftBSlope;
				
				leftargbColor1 = Color.makeARGB((int)Math.round(leftColor1R), (int)Math.round(leftColor1G),(int) Math.round(leftColor1B));
				
				rightColor1R += rightRSlope;
				rightColor1G += rightGSlope;
				rightColor1B += rightBSlope;
				
				rightargbColor1 = Color.makeARGB((int)Math.round(rightColor1R),(int) Math.round(rightColor1G), (int)Math.round(rightColor1B));
	
				y++;
			}
			assert(y == min_y);
			//System.out.println("got to here");

			int max_y = Math.max(left_vertex.getIntY(), right_vertex.getIntY());
			Vertex3D midPoint1= new Vertex3D(leftX,y,leftZ,Color.fromARGB(leftargbColor1));
			Vertex3D midPoint2= new Vertex3D(rightX,y,rightZ,Color.fromARGB(rightargbColor1));
			
			if(max_y == left_vertex.getIntY()) {
				straight_triangle_renderer(left_vertex,midPoint1,midPoint2, drawable,shader);
			}
			else if(max_y == right_vertex.getIntY()) {
				straight_triangle_renderer(right_vertex,midPoint1,midPoint2, drawable,shader);
			}
			else {
				//straight_triangle_renderer(left_vertex,midPoint1,midPoint2, drawable);
			}
		}
	}
	//assume v2==v3 and v2,v3 < v1 or v2,v3 > v1
	public void straight_triangle_renderer(Vertex3D v1,Vertex3D v2,Vertex3D v3, Drawable drawable, Shader shader) {
		

		double min_x = Math.min(v2.getX(), v3.getX());	
		Vertex3D left_vertex;
		Vertex3D right_vertex;
		
		if(min_x == v2.getX()) {
			left_vertex = v2;
			right_vertex = v3;
		}
		else if (min_x == v3.getX()){
			left_vertex = v3;
			right_vertex = v2;
		}
		else {
			System.out.println("something went wrong in straight triangle renderer");
			left_vertex = v3;
			right_vertex = v2;
		}
		
		double leftdeltaX,leftdeltaY,rightdeltaX,rightdeltaY, leftslope = 0,rightslope = 0;
		double leftX = v1.getX();
		double rightX = v1.getX();
		double y = v1.getIntY();
		
		leftdeltaX = left_vertex.getX() - v1.getX();
		leftdeltaY = left_vertex.getIntY() - v1.getIntY();
		leftslope = leftdeltaY/leftdeltaX;
		double inverse_leftslope;
		if(leftdeltaX == 0) {
			inverse_leftslope = 0;
		}
		else {
			inverse_leftslope = 1/leftslope;
		}
		int leftargbColor1 = v1.getColor().asARGB();
		int argbColor2 = left_vertex.getColor().asARGB();
		Color color1 = Color.fromARGB(leftargbColor1);
		Color color2 = Color.fromARGB(argbColor2);
		double leftColor1R = color1.getIntR();
		double leftColor1G = color1.getIntG();
		double leftColor1B = color1.getIntB();
		double leftColor2R = color2.getIntR();
		double leftColor2G = color2.getIntG();
		double leftColor2B = color2.getIntB();
		double leftRSlope = calc_color_slope(v1.getIntY(),left_vertex.getIntY(), leftColor1R,leftColor2R);
		double leftGSlope = calc_color_slope(v1.getIntY(),left_vertex.getIntY(), leftColor1G,leftColor2G);
		double leftBSlope = calc_color_slope(v1.getIntY(),left_vertex.getIntY(), leftColor1B,leftColor2B);
		
		
		rightdeltaX = right_vertex.getX() - v1.getX();
		rightdeltaY = right_vertex.getIntY() - v1.getIntY();
		rightslope = rightdeltaY/rightdeltaX;
		
		double inverse_rightslope;
		if(rightdeltaX == 0) {
			inverse_rightslope = 0;
		}
		else {
			inverse_rightslope = 1/rightslope;
		}
		
		int rightargbColor1 = v1.getColor().asARGB();
		argbColor2 = right_vertex.getColor().asARGB();
		color1 = Color.fromARGB(rightargbColor1);
		color2 = Color.fromARGB(argbColor2);
		double rightColor1R = color1.getIntR();
		double rightColor1G = color1.getIntG();
		double rightColor1B = color1.getIntB();
		double rightColor2R = color2.getIntR();
		double rightColor2G = color2.getIntG();
		double rightColor2B = color2.getIntB();
		double rightRSlope = calc_color_slope(v1.getIntY(),right_vertex.getIntY(), rightColor1R,rightColor2R);
		double rightGSlope = calc_color_slope(v1.getIntY(),right_vertex.getIntY(), rightColor1G,rightColor2G);
		double rightBSlope = calc_color_slope(v1.getIntY(),right_vertex.getIntY(), rightColor1B,rightColor2B);
		
		double leftZ = v1.getZ();
		double rightZ = v1.getZ();
		double leftDeltaZ = left_vertex.getZ() - v1.getZ(); 
		double rightDeltaZ = right_vertex.getZ() - v1.getZ();
		double leftZSlope = leftDeltaZ/leftdeltaY;
		double rightZSlope = rightDeltaZ/rightdeltaY;

		//draw triangle
		if(v1.getIntY() > v2.getIntY()) {
			//System.out.println("testing1");
			for(y = v1.getIntY(); y >=v2.getIntY(); y--) {
				int leftcolorX = leftargbColor1;
				int rightcolorX = rightargbColor1;
				color1 = Color.fromARGB(leftcolorX);
				color2 = Color.fromARGB(rightcolorX);	
				double color1R = color1.getIntR();
				double color1G = color1.getIntG();
				double color1B = color1.getIntB();
				double color2R = color2.getIntR();
				double color2G = color2.getIntG();
				double color2B = color2.getIntB();	
				double rSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1R,color2R);
				double gSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1G,color2G);
				double bSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1B,color2B);
								
				double deltaZ = rightZ - leftZ;
				double zSlope;
				if(rightX - leftX == 0) {
					zSlope = 0;
				}
				else {
					zSlope = deltaZ/(rightX - leftX);
				}
				
				double z = leftZ;
				for(int x = (int) Math.round(leftX); x <(int)Math.round(rightX);x++) {
					Polygon polygon = Polygon.make(v1,v2,v3);
					Vertex3D current = new Vertex3D(x,y,z, Color.fromARGB(leftcolorX));
					Color color = shader.shade_pixel(polygon,current);
					int color_argb = color.asARGB();
					drawable.setPixel(x, (int)y, z, color_argb);
					//drawable.setPixel(x, (int)y, z, leftcolorX);
					z += zSlope;
					color1R += rSlope;
					color1G += gSlope;
					color1B += bSlope;
					leftcolorX = Color.makeARGB((int)Math.round(color1R), (int)Math.round(color1G), (int)Math.round(color1B));
				}
				//drawable.setPixel((int) Math.round(leftX), (int)y,0.0,leftargbColor1);
				//drawable.setPixel((int) Math.round(rightX), (int)y,0.0,rightargbColor1);
				leftX -= inverse_leftslope;
				rightX -= inverse_rightslope;
				
				leftZ -= leftZSlope;
				rightZ -= rightZSlope;
				
				leftColor1R -= leftRSlope;
				leftColor1G -= leftGSlope;
				leftColor1B -= leftBSlope;
				
				leftargbColor1 = Color.makeARGB((int)Math.round(leftColor1R),(int) Math.round(leftColor1G),(int) Math.round(leftColor1B));
				
				rightColor1R -= rightRSlope;
				rightColor1G -= rightGSlope;
				rightColor1B -= rightBSlope;
				
				rightargbColor1 = Color.makeARGB((int)Math.round(rightColor1R), (int)Math.round(rightColor1G),(int) Math.round(rightColor1B));
				
			}
		}
		/*else if(v1.getY() < v2.getY()) {
			//System.out.println("testing2");
			//leftX = left_vertex.getX();
			//rightX = right_vertex.getX();
			int leftcolorX = leftargbColor1;
			int rightcolorX = rightargbColor1;
			color1 = Color.fromARGB(leftcolorX);
			color2 = Color.fromARGB(rightcolorX);	
			double color1R = color1.getIntR();
			double color1G = color1.getIntG();
			double color1B = color1.getIntB();
			double color2R = color2.getIntR();
			double color2G = color2.getIntG();
			double color2B = color2.getIntB();	
			double rSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1R,color2R);
			double gSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1G,color2G);
			double bSlope = calc_color_slope((int)Math.round(leftX),(int)Math.round(rightX), color1B,color2B);
			
			
			for(y = v1.getIntY(); y <=v2.getIntY(); y++) {
				for(int x = (int) Math.round(leftX); x <(int)Math.round(rightX);x++) {
					drawable.setPixel(x, (int)y,v1.getZ(), leftcolorX);
					color1R += rSlope;
					color1G += gSlope;
					color1B += bSlope;
					leftcolorX = Color.makeARGB((int)Math.round(color1R), (int)Math.round(color1G), (int)Math.round(color1B));
				}
				//drawable.setPixel((int) Math.round(leftX), (int)y,0.0,leftargbColor1);
				//drawable.setPixel((int) Math.round(rightX), (int)y,0.0,rightargbColor1);
				leftX += inverse_leftslope;
				rightX += inverse_rightslope;
				
				
				leftColor1R += leftRSlope;
				leftColor1G += leftGSlope;
				leftColor1B += leftBSlope;
				
				leftargbColor1 = Color.makeARGB((int)Math.round(leftColor1R),(int) Math.round(leftColor1G),(int) Math.round(leftColor1B));
				
				rightColor1R += rightRSlope;
				rightColor1G += rightGSlope;
				rightColor1B += rightBSlope;
				
				rightargbColor1 = Color.makeARGB((int)Math.round(rightColor1R),(int) Math.round(rightColor1G),(int) Math.round(rightColor1B));
			}
		}*/
		else if (v1.getIntY() == v3.getIntY()) {
			//System.out.println("testing");
		}

	
	}
	public static PolygonRenderer make() {
		// TODO Auto-generated method stub
		return new FilledPolygonRenderer();
	}


}
