package uk.co.essarsoftware.ski.geo;

abstract class Coordinate
{
	public static final double PI = 3.141592653589793;
	
	protected static double degToRad(double deg) {
        return deg * (PI / 180.0);
    }
	
	protected static double degToRad(int deg) {
		return degToRad((double) deg);
	}

    protected static double radToDeg(double rad) {
    	return rad * (180.0 / PI);
    }
}
