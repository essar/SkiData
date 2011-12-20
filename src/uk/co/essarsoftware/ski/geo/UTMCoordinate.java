package uk.co.essarsoftware.ski.geo;

public class UTMCoordinate extends Coordinate
{
	private char band;
    private int x, y, zone;
    
    static final double scaleFactor = 0.9996f;
    
    /**
     * Determines the central meridian for the given UTM zone.
     * @param zone An integer value designating the UTM zone, range [1,60].
     * @return The central meridian for the given UTM zone, in radians, or zero if
     * the UTM zone parameter is outside the range [1,60].
     * Range of the central meridian is the radian equivalent of [-177,+177].
     */
    static double calcCentralMeridian(int zone) {
    	if(zone < 1 || zone > 60) {
    		return 0.0;
    	}
        return degToRad(-183 + (zone * 6));
    }

    public UTMCoordinate(int x, int y, int zone, char band) {
    	if(x < 0) {
    		throw new IllegalArgumentException(String.format("UTM easing coordinate out of range (%d)", x));
    	}
        this.x = x;
        if(y < 0) {
    		throw new IllegalArgumentException(String.format("UTM northing coordinate out of range (%d)", x));
    	}
        this.y = y;
        if(zone < 1 || zone > 60) {
            throw new IllegalArgumentException(String.format("UTM zone input out of range (%d)", zone));
        }
        this.zone = zone;
        if(band != 'N' && band != 'S') {
            throw new IllegalArgumentException(String.format("UTM band input out of range (%s)", band));
        }
        this.band = band;
    }

    public char getBand() {
        return band;
    }
    
    public double getCentralMeridian() {
    	return calcCentralMeridian(zone);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZone() {
        return zone;
    }

    public String toString() {
        return String.format("%d%s %d %d", zone, band, x, y);
    }
}
