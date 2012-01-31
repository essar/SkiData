package uk.co.essarsoftware.ski.geo;

public class DMSCoordinate extends Coordinate
{
	private final DMSElement latitude, longitude;
	
	public DMSCoordinate(int latD, float latM, int longD, float longM) {
		this(latD, getMins(latM), getSecs(latM), longD, getMins(longM), getSecs(longM));
	}
	
	private static float getSecs(float decimalMin) {
		// Get decimal component
		float dComp = decimalMin - getMins(decimalMin);
		// Convert to seconds
		return dComp * 60.0f;
	}
	
	private static int getMins(float decimalMin) {
		return (int) Math.floor(decimalMin);
	}
	
	public DMSCoordinate(int latD, int latM, float latS, int longD, int longM, float longS) {
		// Validate latitude values
		char latX = (latD < 0 ? 'S' : 'N');
		latD = Math.abs(latD);
		if(latS >= 60) {
			latM += latS / 60;
			latS = latS % 60;
		}
		if(latM >=  60) {
			latD += latM / 60;
			latM = latM % 60;
		}
		if(latD < -90 || latD > 90) {
			throw new IllegalArgumentException(String.format("DMS latitude out of range (%d)", latD));
		}
		latitude = new DMSElement(latD, latM, latS, latX);
		
		// Validate longitude values
		char longX = (longD < 0 ? 'W' : 'E');
		longD = Math.abs(longD);
		if(longS >= 60) {
			longM += longS / 60;
			longS = longS % 60;
		}
		if(longM >=  60) {
			longD += longM / 60;
			longM = longM % 60;
		}
		if(latD < -180 || latD > 180) {
			throw new IllegalArgumentException(String.format("DMS longitude out of range (%d)", longD));
		}
		longitude = new DMSElement(longD, longM, longS, longX);
	}
	
	public int getLatitudeDegrees() {
		return latitude.degrees;
	}
	
	public char getLatitudeHemisphere() {
		return latitude.hemisphere;
	}
	
	public int getLatitudeMinutes() {
		return latitude.minutes;
	}
	
	public float getLatitudeSeconds() {
		return latitude.seconds;
	}
	
	public int getLongitudeDegrees() {
		return longitude.degrees;
	}
	
	public char getLongitudeHemisphere() {
		return longitude.hemisphere;
	}
	
	public int getLongitudeMinutes() {
		return longitude.minutes;
	}
	
	public float getLongitudeSeconds() {
		return longitude.seconds;
	}
	
	public String toString() {
		return String.format("%s,%s", latitude, longitude);
	}
	
	private static class DMSElement
	{
		private final char hemisphere;
		private final float seconds;
		private final int degrees, minutes;
		
		DMSElement(int degrees, int minutes, float seconds, char hemisphere) {
			this.degrees = degrees;
			this.minutes = minutes;
			this.seconds = seconds;
			this.hemisphere = hemisphere;
		}
		
		public String toString() {
			return String.format("%d %d\'%d\"%s", degrees, minutes, seconds, hemisphere);
		}
	}
}
