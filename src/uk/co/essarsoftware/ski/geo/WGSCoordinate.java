package uk.co.essarsoftware.ski.geo;

public class WGSCoordinate extends Coordinate
{
	private final float latitude, longitude;

    public static final int COORD_MODE_DEG = 0x01;
    public static final int COORD_MODE_RAD = 0x02;

    public WGSCoordinate(float latitude, float longitude, int coordMode) {
        switch(coordMode) {
            case COORD_MODE_DEG:
                if(latitude < -90.0f || latitude > 90.0f) {
                    throw new IllegalArgumentException(String.format("Latitude degree input out of range (%f)", latitude));
                }
                this.latitude = (float) degToRad(latitude);

                if(longitude < -180.0f || longitude > 180.0f) {
                    throw new IllegalArgumentException(String.format("Longitude degree input out of range (%f)", longitude));
                }
                this.longitude = (float) degToRad(longitude);

                break;
            case COORD_MODE_RAD:
                if(latitude < ((-1.0f * PI) / 2.0f) || latitude > (PI / 2.0f)) {
                    throw new IllegalArgumentException(String.format("Latitude radian input out of range (%f)", latitude));
                }
                this.latitude = latitude;

                if(longitude < (-1.0f * PI) || longitude > PI) {
                    throw new IllegalArgumentException(String.format("Longitude radian input out of range (%f)", longitude));
                }
                this.longitude = longitude;

                break;
		    default:
		    	throw new IllegalArgumentException(String.format("Invalid coordinate mode (%d)", coordMode));
        }
    }

    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o instanceof WGSCoordinate) {
            WGSCoordinate c = (WGSCoordinate) o;
            return (latitude == c.latitude && longitude == c.longitude);
        }
        return false;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLatitudeDegrees() {
        return (float) radToDeg(latitude);
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLongitudeDegrees() {
        return (float) radToDeg(longitude);
    }
    
    public String toString() {
    	return String.format("%3.5f, %3.5f", getLatitudeDegrees(), getLongitudeDegrees());
    }
}
