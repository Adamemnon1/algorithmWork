package lab1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class ClosestPair  {
	public final static double INF = java.lang.Double.POSITIVE_INFINITY;
	/**
	 * The brute force, n^2, method to find the closest pair. This is used only to help expedite the
	 * overall algorithm as this method has no overhang.
	 * @param xyPointArray
	 * @return = the Result with the two points that are closest together.
	 */
	static Result findClosestPairBruteForce(XYPoint[] xyPointArray) {
		double mindist = Double.POSITIVE_INFINITY;
		XYPoint xy1 = xyPointArray[0];
		XYPoint xy2 = xyPointArray[0];
		for (int i = 0; i < xyPointArray.length-1; i++) {
			for (int c = i+1; c < xyPointArray.length; c++) {
				double dist = xyPointArray[i].dist(xyPointArray[c]);
				if (mindist > dist) {
					mindist = dist;
					xy1 = xyPointArray[i];
					xy2 = xyPointArray[c];
				}
			}
		}
		return new Result(xy1, xy2, mindist);
	}
	
	/** 
	 * Given a collection of points, find the closest pair of point and the
	 * distance between them in the form "(x1, y1) (x2, y2) distance"
	 *
	 * @param pointsByX points sorted in nondecreasing order by X coordinate
	 * @param pointsByY points sorted in nondecreasing order by Y coordinate
	 * @return Result object containing the points and distance
	 */
	static Result findClosestPair(XYPoint pointsByX[], XYPoint pointsByY[]) {

		//If there is only one point in the array, then just a Result consisting
		//of that point and a distance of 0 is returned. 
		if (pointsByX.length ==1) {
			return new Result(pointsByX[0], pointsByX[0], 0);
		}

		//For a larger array, the method closestPair is called on it.
		Result rMin = closestPair(pointsByX, pointsByY, pointsByX.length);
		return rMin;
	}

	/**
	 * This method looks for the closest pair of points over sorted arrays
	 * of XYPoints.
	 * 
	 * @param pointsByX1 = XYPoint array sorted by the x values
	 * @param pointsByY1= XYPoint array sorted by the y values
	 * @param size = int, corresponds to the size of the x and y sorted arrays.
	 * @return = Result with the pair of points that fulfill the following:
	 * 1) two points with the shortest distance between them, or
	 * 2) two points with the shortest distance and lowest x value, or
	 * 3) two points with the shortest distance and lowest x value or y value, or
	 * 4) two points with the shortest distance and lowest x vaue and y value and the indices of these
	 */
	static Result closestPair(XYPoint[] pointsByX1, XYPoint[] pointsByY1, int size) { //Result is the object that consists of a distance and two XYPoints.
		//error case: Throw an error to indicate that the inputted arrays are not the same length,
		//therefore the code cannot continue.
		if (pointsByX1.length != pointsByY1.length) {
				throw new IllegalArgumentException("Inputted arrays are not the same sizes");
		}
		//In the interest of time efficiency, I made the base case 16. 
		//Brute Forced, the time is small and has no overhang, so this helps speed
		//up the overall algorithm.
		if (size <= 16) {
			Result ans = findClosestPairBruteForce(pointsByX1);
			return ans;
		}

		//divide
		int mid = size /2; //this will determine where to split the array.
		
		while (mid < size  && pointsByX1[mid].x == pointsByX1[mid+1].x) { 
			mid++;
			//this while loop takes care of the edge case where the midpoint chosen has points with
			//identical x values to the right of it. I later split my y arrays based on the x values 
			// <= the midpoint x value. This ensures the arrays are filled.
		}
		XYPoint[] xL = Arrays.copyOfRange(pointsByX1, 0, mid +1 ); //copyOfRange only copies from the beginning inclusive, to the end exclusive. 
		XYPoint[] xR = Arrays.copyOfRange(pointsByX1, mid  +1 , size); 
		XYPoint midX = pointsByX1[mid];
		XYPoint[] yL = new XYPoint[mid +1 ];
		XYPoint[] yR = new XYPoint[pointsByY1.length - mid -1 ];
		int left = 0; //a counter for when yL is filled
		int right =0; //a counter for when yR is filled
		
		for (int i = 0; i < pointsByY1.length ; i ++ ) { //iterates to fill the yL and yR arrays
			if (pointsByY1[i].x <= midX.x) { //checks to see if the point should go into yR or yL based on
											                 //the x values of the points in the array sorted by y.
				yL[left] = pointsByY1[i];
				left++;
			}
			else {
				yR[right] = pointsByY1[i];
				right++;
			}
		}
		
		//conquer
		Result dL = closestPair(xL, yL, xL.length);
		Result dR = closestPair(xR, yR, xR.length); 
		Result dC = combine( pointsByY1, pointsByX1[mid], Math.min(dL.dist, dR.dist));
		double minDLDR = Math.min(dL.dist, dR.dist); //useful variable
		
		//edge case where the distances are all the same.
		if (dC.dist == dR.dist && dC.dist == dR.dist) { 
			Result ans = validResult3(dL, dC, dR);
			return ans;
		}
		
		//edge case where not all of the distances are the same. dC.dist == dR.dist
		else if (dC.dist == dR.dist && !(dC.dist == dL.dist) ) {
			Result ans2 = validResult2(dC, dR); //helper method to determine which result to return.
		}
		//edge case where not all of the distances are the same. dC.dist == dL.dist
		else if (dC.dist == dL.dist && !(dC.dist == dR.dist)) { 
			Result ansdCdR = validResult2(dC, dL); //helper method to determine which result to return.
			return ansdCdR;
		}
		//more edge cases, where dL.dist == dR.dist
		else if (dL.dist == dR.dist) {
			Result ansdLdR = validResult2(dL, dR);
			return ansdLdR;	
		}
		
		//other rare edge cases.
		if (dC.dist == Math.min(minDLDR, dC.dist)) {
			return dC;
		}
		else if (dR.dist == Math.min(dL.dist, dR.dist)) {
			return dR;
		}
		else {
			return dL;
		}
	}

	/**
	 * This method looks over the 'middle' of the arrays for points with the
	 * smallest distance between them, and a distance less than the distance
	 * already found in the two halves dR, dL.
	 * 
	 * @param pointsByY2 = XYPoint[] sorted by y
	 * @param pivot = the predetermined midpoint of the XYPoint[] sorted by x.
	 * @param range = the minimum distance found in either the left or right hand side.
	 * @return Result = the Result found in the combine section. 
	 */
	static Result combine( XYPoint[] pointsByY2, XYPoint pivot, double range) {
		ArrayList<XYPoint> yStripExtra = new ArrayList<XYPoint>();
		for (int i = 0; i < pointsByY2.length ; i++) {
			boolean less = pointsByY2[i].x <= pivot.x + range;
			boolean greater = pointsByY2[i].x >= pivot.x - range;
			if (   less && greater) {
				yStripExtra.add(pointsByY2[i] );
			}
		}
		double minDist = 0;
		int firstPointIndex = 0; //location of first point
		int secondPointIndex = 0; //locaiton of second point
		for (int count = 0; count < yStripExtra.size() ; count++) { 
			int k = count+1; //index of the next point to compare to.
			if ( k >= yStripExtra.size() || count >= yStripExtra.size() ) {//case where the loop must be exited.
				break;
			}
			while (k < yStripExtra.size() && yStripExtra.get(k).dist( yStripExtra.get(count)) < range ) {
				minDist = yStripExtra.get(k).dist( yStripExtra.get(count) );
				range = minDist; 
				firstPointIndex = count;
				secondPointIndex = k;
				k++;
			}
		}
		
		//edge cases to determine which point to return as the first point in the returning Result.
		if (yStripExtra.get(firstPointIndex).dist(yStripExtra.get(secondPointIndex)) == 0) {
			return new Result(yStripExtra.get(firstPointIndex), yStripExtra.get(secondPointIndex), Double.POSITIVE_INFINITY);
		}
		if (yStripExtra.get(firstPointIndex).x  < yStripExtra.get(secondPointIndex).x) {
			return new Result(yStripExtra.get(firstPointIndex), yStripExtra.get(secondPointIndex), range);
		}
		else if (yStripExtra.get(secondPointIndex).x  < yStripExtra.get(firstPointIndex).x) {
			return new Result(yStripExtra.get(secondPointIndex) ,yStripExtra.get(firstPointIndex), range );
		}
		else if (yStripExtra.get(firstPointIndex).y  < yStripExtra.get(secondPointIndex).y) {
			return new Result(yStripExtra.get(firstPointIndex), yStripExtra.get(secondPointIndex), range);
		}
		else if (yStripExtra.get(secondPointIndex).y  < yStripExtra.get(firstPointIndex).y) {
			return new Result(yStripExtra.get(secondPointIndex) ,yStripExtra.get(firstPointIndex), range );
		}
		else if (yStripExtra.get(firstPointIndex).isLeftOf(yStripExtra.get(secondPointIndex))) {
			return new Result(yStripExtra.get(firstPointIndex), yStripExtra.get(secondPointIndex), range);
		}
		else if (yStripExtra.get(secondPointIndex).isLeftOf(yStripExtra.get(firstPointIndex))) {
			return new Result(yStripExtra.get(secondPointIndex) ,yStripExtra.get(firstPointIndex), range );
		}
		else if (yStripExtra.get(firstPointIndex).isBelow(yStripExtra.get(secondPointIndex))) {
			return new Result(yStripExtra.get(firstPointIndex), yStripExtra.get(secondPointIndex), range);
		}
		else {
			return new Result(yStripExtra.get(secondPointIndex) ,yStripExtra.get(firstPointIndex), range );
		}
	}

	/**
	 * Method to find the absolute vlaue of an integer
	 * @param x = int
	 * @return = the absolute value of x
	 */
	static int abs(int x) {//given method
		if (x < 0) {
			return -x;
		} else {
			return x;
		}
	}

	/**
	 * This method looks over 2 Results and determines which one should be used
	 * that satisfies the conditions on which Result to return.
	 * @param r1 = Result
	 * @param r2 = Result
	 * @return = Result that has the lower x value or lower y value or values with
	 * lower indices than the other.
	 */
	static Result validResult2(Result r1, Result r2) {
		if (r1.p1.x < r2.p1.x ) {
			return r1;
		}
		else if (r2.p1.x < r1.p1.x) {
			return r2;
		}
		else  {
			if (r1.p1.y < r2.p1.y) {
				return r1;
			}
			else if (r2.p1.y < r1.p1.y){
				return r2;
			}
			else if (r1.p1.isLeftOf(r2.p1)) {
				return r1;
			}
			else if (r2.p1.isLeftOf(r1.p1)) {
				return r2;
			}
			else if (r1.p1.isBelow(r2.p1)) {
				return r1;
			}
			else {
				return r2;
			}
		}
	}

	/**
	 * This method determines which Result is to be used out of 3 Results.
	 * @param r1 = Result
	 * @param r2 = Result
	 * @param r3 = Result
	 * @return = Result that has the lowest x value or lowest y value or values with lowest indices.
	 */
	static Result validResult3(Result r1, Result r2, Result r3) {
		if (r1.p1.x < r2.p1.x && r1.p1.x < r3.p1.x) {
			return r1;
		}
		else if (r2.p1.x < r1.p1.x && r2.p1.x < r3.p1.x) {
			return r2;
		}
		else if (r3.p1.x < r2.p1.x && r3.p1.x < r1.p1.x) {
			return r3;
		}
		else if (r1.p1.y < r2.p1.y && r1.p1.y < r3.p1.y) {
			return r1;
		}
		else if (r2.p1.y < r1.p1.y && r2.p1.y < r3.p1.y) {
			return r2;
		}
		else if (r3.p1.y < r1.p1.y && r3.p1.y < r2.p1.y) {
			return r3;
		}
		else if (r1.p1.isLeftOf(r2.p1) && r1.p1.isLeftOf(r3.p1)) {
			return r1;
		}
		else if (r2.p1.isLeftOf(r1.p1) && r2.p1.isLeftOf(r3.p1)) {
			return r2;
		}
		else if (r3.p1.isLeftOf(r1.p1) && r3.p1.isLeftOf(r2.p1)) {
			return r3;
		}
		else if (r1.p1.isBelow(r2.p1) && r1.p1.isBelow(r3.p1)) {
			return r1;
		}
		else if (r2.p1.isBelow(r1.p1) && r2.p1.isBelow(r3.p1)) {
			return r2;
		}
		else {
			return r3;
		}
	}

}
