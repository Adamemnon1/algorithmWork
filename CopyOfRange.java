package lab1;

import java.util.Arrays;

public class CopyOfRange {

	public static void main(String[] args) {
		XYPoint[] array = new XYPoint[10];
		for (int i = 0; i < 10; i ++ ) {
			XYPoint xy = new XYPoint(i,i);
			array[i] = xy;
			
		}
		int mid = array.length / 2;
		System.out.println("Mid is: " +mid);
		XYPoint[] xr = Arrays.copyOfRange(array, 0, mid);
		for (int i = 0; i < xr.length; i++) {
		System.out.println("xr is: " + xr[i]);
		}
		XYPoint[] xl = Arrays.copyOfRange(array, mid+1, array.length);
		for (int i = 0; i < xl.length; i++) {
			System.out.println("xl is: " + xl[i]);
		}

	}

	public final static double INF = java.lang.Double.POSITIVE_INFINITY;

	/** 
	 * Given a collection of points, find the closest pair of point and the
	 * distance between them in the form "(x1, y1) (x2, y2) distance"
	 *
	 * @param pointsByX points sorted in nondecreasing order by X coordinate
	 * @param pointsByY points sorted in nondecreasing order by Y coordinate
	 * @return Result object containing the points and distance
	 */
	static Result findClosestPair(XYPoint pointsByX[], XYPoint pointsByY[]) {

		/* 
		 * Two sorted arrays by x and y. Must use XYPoint.x or .y to get the x and y value
		 * associated with the value in each array, this is not a big deal but note it.
		 * 
		 * Divide: divide the byX array into halves. I must then get the YL and YR which are 
		 * the y points associated with the x points in the XL and XR sections. 
		 * How do the YL and YR?
		 * 
		 * Conquer: Need closest distance of XL and YL. Also need closest distance of XR and YR.
		 * Return the result of the Combine.
		 * 
		 * Combine: takes in the array sorted by y and the midpoint in x and the min distance
		 * so far. 
		 */
		
		Result rMin = closestPair(pointsByX, pointsByY, pointsByX.length);
		return rMin;
	}

	static Result closestPair(XYPoint pointsByX1[], XYPoint pointsByY1[], int size) {
		
		//base case
		if (size == 1) {
			return new Result(pointsByX1[0], pointsByY1[0], Double.POSITIVE_INFINITY);
		}
		if (size == 2 ) { //changeed from size == 2
			System.out.println("pointsByX1.length in error catching is: " + pointsByX1.length);//size is 1...
			return new Result( pointsByX1[0], pointsByX1[1], pointsByX1[0].dist(pointsByX1[1]));
		}
		
		//divide
		
		int mid = (int)( ((double)size) /2);
		System.out.println("Mid is: " + mid);
		
		
		XYPoint[] xL = Arrays.copyOfRange(pointsByX1, 0, mid-1);//changed from 0 to mid
		
		System.out.println("xL.length is: " + xL.length);
		
		for (int i = 0; i < xL.length; i++) {
			System.out.println("xL i is: " + i);
			System.out.println("xL is: " + xL[i]);
		}
		
		XYPoint[] xR = Arrays.copyOfRange(pointsByX1, mid, size-1); //changed from mid+1 to size
		
		
		System.out.println("xR.length is: " + xR.length);
		for (int i = 0; i < xR.length; i++) {
			System.out.println("xR i is: " +i);
			System.out.println("xR is: " + xR[i]);
			
		}
		
		XYPoint[] yLByY = Arrays.copyOfRange(xL, 0, mid-1); 
		
		System.out.println("pointsByX size is: " + pointsByX1.length);

		XYPoint[] yRByY = Arrays.copyOfRange(xR, mid, size-1); 
		
		//conquer
		Result dL = closestPair(xL, yLByY, mid); 
		Result dR = closestPair(xR, yRByY, pointsByX1.length - mid); 
		return combine( pointsByY1, pointsByX1[mid], Math.min(dL.dist, dR.dist));
		
	}
	
	static Result combine( XYPoint[] pointsByY2, XYPoint pivot, double range) {
		XYPoint[] yStripExtra = new XYPoint[pointsByY2.length];
		for (int i = 0; i < pointsByY2.length; i++) {
			if (pointsByY2[i].x <= pivot.x + range && pointsByY2[i].x >= pivot.x - range) {
				yStripExtra[i] = pointsByY2[i];
			}
		}
		
		double minDist = 0;
		XYPoint y1 = new XYPoint(0,0);
		XYPoint y2 = new XYPoint(1,1);
		
		for (int count = 0; count < yStripExtra.length; count++) {
			if (yStripExtra[count] == null) {
				break;
			}
			System.out.println("yStripExtra length is: " + yStripExtra.length);
			int k = count+1;
			System.out.println("int count is: " + count);
			System.out.println("int k is: " + k);
			while (yStripExtra[k].dist( yStripExtra[count]) <= range ) {
				minDist = yStripExtra[k].dist( yStripExtra[count] );
				range = Math.min(minDist, range);
				y1 = yStripExtra[count];
				y2 = yStripExtra[k];
				
				k++;
			}
		}
		return new Result(y1, y2, range);
	}

	static int abs(int x) {
		if (x < 0) {
			return -x;
		} else {
			return x;
		}
	}

	static double absDouble(double x) {
		if (x<0) {
			return -x;
		}
		else {
			return -x;
		}
	}
	
}





