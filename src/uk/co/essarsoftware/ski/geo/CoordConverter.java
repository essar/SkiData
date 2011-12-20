package uk.co.essarsoftware.ski.geo;

public class CoordConverter
{
	private static final double sm_a = 6378137.0;
	private static final double sm_b = 6356752.314;

    private CoordConverter() {
    }
    
    /**
     * Computes the ellipsoidal distance from the equator to a point at a given latitude.
     * Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
     * GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
     * Globals:
     *     sm_a - Ellipsoid model major axis.
     *     sm_b - Ellipsoid model minor axis.
     *     
     * @param phi Latitude of the point, in radians.
     * @return The ellipsoidal distance of the point from the equator, in metres.
     */
    private static double arcLengthOfMeridian(double phi) {
        /* Calculate n */
        double n = (sm_a - sm_b) / (sm_a + sm_b);

        /* Calculate alpha */
        double alpha = ((sm_a + sm_b) / 2.0) * (1.0 + (Math.pow(n, 2) / 4.0) + (Math.pow(n, 4) / 64.0));

        /* Calculate beta */
        double beta = (-3.0 * n / 2.0) + (9.0 * Math.pow(n, 3) / 16.0) + (-3.0 * Math.pow(n, 5) / 32.0);

        /* Calculate gamma */
        double gamma = (15.0 * Math.pow(n, 2) / 16.0) + (-15.0 * Math.pow (n, 4) / 32.0);
    
        /* Calculate delta */
        double delta = (-35.0 * Math.pow(n, 3) / 48.0) + (105.0 * Math.pow(n, 5) / 256.0);
    
        /* Calculate epsilon */
        double epsilon = 315.0 * Math.pow(n, 4) / 512.0;
    
        /* Now calculate the sum of the series and return */
        double result = alpha * (phi + (beta * Math.sin(2.0 * phi)) 
        		+ (gamma * Math.sin(4.0 * phi))
        		+ (delta * Math.sin(6.0 * phi))
        		+ (epsilon * Math.sin(8.0 * phi)));

        return result;
    }
    
    /**
     * Computes the footpoint latitude for use in converting transverse
     * Mercator coordinates to ellipsoidal coordinates.
     *
     * Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
     *   GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
     *
     * @param y The UTM northing coordinate, in meters.
     * @return the footpoint latitude, in radians.
     */
    private static double footpointLatitude(double y) {
        /* Calculate n (Eq. 10.18) */
        double n = (sm_a - sm_b) / (sm_a + sm_b);
        	
        /* Calculate alpha_ (Eq. 10.22) */
        /* (Same as alpha in Eq. 10.17) */
        double alpha_ = ((sm_a + sm_b) / 2.0) * (1 + (Math.pow(n, 2) / 4) + (Math.pow(n, 4) / 64));
        
        /* Calculate y_ (Eq. 10.23) */
        double y_ = y / alpha_;
        
        /* Calculate beta_ (Eq. 10.22) */
        double beta_ = (3.0 * n / 2.0) + (-27.0 * Math.pow(n, 3) / 32.0) + (269.0 * Math.pow(n, 5) / 512.0);
        
        /* Calculate gamma_ (Eq. 10.22) */
        double gamma_ = (21.0 * Math.pow(n, 2) / 16.0) + (-55.0 * Math.pow(n, 4) / 32.0);
        	
        /* Calculate delta_ (Eq. 10.22) */
        double delta_ = (151.0 * Math.pow(n, 3) / 96.0) + (-417.0 * Math.pow(n, 5) / 128.0);
        	
        /* Calculate epsilon_ (Eq. 10.22) */
        double epsilon_ = (1097.0 * Math.pow(n, 4) / 512.0);
        	
        /* Now calculate the sum of the series (Eq. 10.21) */
        double result = y_ + (beta_ * Math.sin (2.0 * y_))
        		+ (gamma_ * Math.sin(4.0 * y_))
        		+ (delta_ * Math.sin(6.0 * y_))
        		+ (epsilon_ * Math.sin(8.0 * y_));
        
        return result;
    }
    
    public static WGSCoordinate DMS2WGS(DMSCoordinate dms) {
        // Build latitude value
        double lat = dms.getLatitudeDegrees()
                + (dms.getLatitudeMinutes() / 60.0)
                + (dms.getLatitudeSeconds() / 3600.0);
        // Check E/W - West is negative
        lat *= (dms.getLatitudeHemisphere() == 'W' ? -1 : 1);

        // Build longitude value
        double lon = dms.getLongitudeDegrees()
                + (dms.getLongitudeMinutes() / 60.0)
                + (dms.getLongitudeSeconds() / 3600.0);
        // Check N/S - South is negative
        lon *= (dms.getLongitudeHemisphere() == 'S' ? -1 : 1);

        return new WGSCoordinate((float) lat, (float) lon, WGSCoordinate.COORD_MODE_DEG);
    }

    public static DMSCoordinate WGS2DMS(WGSCoordinate wgs) {
        // Break down latitude degrees into D M S components
        int latDeg = (int) wgs.getLatitudeDegrees();
        double latMS = (wgs.getLatitudeDegrees() - latDeg) * 60.0 * 60.0;
        int latM = (int) (latMS / 60);
        int latS = (int) (latMS % 60);

        // Break down longitude degrees into D M S components
        int longDeg = (int) wgs.getLongitudeDegrees();
        double longMS = (wgs.getLongitudeDegrees() - longDeg) * 60.0 * 60.0;
        int longM = (int) (longMS / 60);
        int longS = (int) (longMS % 60);

        // Build coordinate object
        return new DMSCoordinate(latDeg, latM, latS, longDeg, longM, longS);
    }

    /**
     * Converts x and y coordinates in the Transverse Mercator projection to
     * a latitude/longitude pair.  Note that Transverse Mercator is not
     * the same as UTM; a scale factor is required to convert between them.
     *
     * Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
     *   GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
     *
     * @param utm the input coordinate in UTM format.
     * 
     * @return the same coordinate in WGS84 format. 
     */
    public static WGSCoordinate UTM2WGS(UTMCoordinate utm) {
    	/*
    	 * Remarks:
    	 *   The local variables Nf, nuf2, tf, and tf2 serve the same purpose as
    	 *   N, nu2, t, and t2 in WGS2UTM, but they are computed with respect
    	 *   to the footpoint latitude phif.
    	 *
    	 *   x1frac, x2frac, x2poly, x3poly, etc. are to enhance readability and
    	 *   to optimize computations.
    	 */
    	double x = (double) utm.getX();
    	double y = (double) utm.getY();
    	
    	/* Adjust easing and northing for UTM system. */
    	x -= 500000.0;
        x /= UTMCoordinate.scaleFactor;
        	
        /* If in southern hemisphere, adjust y accordingly. */
        y -= (utm.getBand() == 'S' ? 10000000.0 : 0.0);
        y /= UTMCoordinate.scaleFactor;
    	
    	/* Calculate lambda0 */
    	double lambda0 = utm.getCentralMeridian();
    	
        /* Get the value of phif, the footpoint latitude. */
        double phif = footpointLatitude(y);
        	
        /* Calculate ep2 */
        double ep2 = (Math.pow(sm_a, 2) - Math.pow(sm_b, 2)) / Math.pow(sm_b, 2);
        	
        /* Calculate cos(phif) */
        double cf = Math.cos(phif);
        	
        /* Calculate nuf2 */
        double nuf2 = ep2 * Math.pow(cf, 2);
        	
        /* Calculate nf and initialize nfpow */
        double nf = Math.pow(sm_a, 2) / (sm_b * Math.sqrt(1.0 + nuf2));
        double nfpow = nf;
        	
        /* Calculate tf */
        double tf = Math.tan(phif);
        double tf2 = tf * tf;
        double tf4 = tf2 * tf2;
        
        /* Calculate fractional coefficients for x**n in the equations
           below to simplify the expressions for latitude and longitude. */
        double x1frac = 1.0 / (nfpow * cf);
        
        nfpow *= nf;   // now equals Nf^2
        double x2frac = tf / (2.0 * nfpow);
        
        nfpow *= nf;   // now equals Nf^3
        double x3frac = 1.0 / (6.0 * nfpow * cf);
        
        nfpow *= nf;   // now equals Nf^4
        double x4frac = tf / (24.0 * nfpow);
        
        nfpow *= nf;   // now equals Nf^5
        double x5frac = 1.0 / (120.0 * nfpow * cf);
        
        nfpow *= nf;   // now equals Nf^6
        double x6frac = tf / (720.0 * nfpow);
        
        nfpow *= nf;   // now equals Nf^7
        double x7frac = 1.0 / (5040.0 * nfpow * cf);
        
        nfpow *= nf;   // now equals Nf^8
        double x8frac = tf / (40320.0 * nfpow);
        
        /* Calculate polynomial coefficients for x^n.
           -- x^1 does not have a polynomial coefficient. */
        double x2poly = -1.0 - nuf2;
        double x3poly = -1.0 - 2 * tf2 - nuf2;
        double x4poly = 5.0 + 3.0 * tf2 + 6.0 * nuf2 - 6.0 * tf2 * nuf2 - 3.0 * (nuf2 *nuf2) - 9.0 * tf2 * (nuf2 * nuf2);
        double x5poly = 5.0 + 28.0 * tf2 + 24.0 * tf4 + 6.0 * nuf2 + 8.0 * tf2 * nuf2;
        double x6poly = -61.0 - 90.0 * tf2 - 45.0 * tf4 - 107.0 * nuf2 + 162.0 * tf2 * nuf2;
        double x7poly = -61.0 - 662.0 * tf2 - 1320.0 * tf4 - 720.0 * (tf4 * tf2);
        double x8poly = 1385.0 + 3633.0 * tf2 + 4095.0 * tf4 + 1575 * (tf4 * tf2);
        	
        /* Calculate latitude */
        double lat = phif+ x2frac * x2poly * (x * x)
        		+ x4frac * x4poly * Math.pow (x, 4.0)
        		+ x6frac * x6poly * Math.pow (x, 6.0)
        		+ x8frac * x8poly * Math.pow (x, 8.0);
        	
        /* Calculate longitude */
        double lon = lambda0 + x1frac * x
        		+ x3frac * x3poly * Math.pow (x, 3.0)
        		+ x5frac * x5poly * Math.pow (x, 5.0)
        		+ x7frac * x7poly * Math.pow (x, 7.0);
        
        /* Build coordinate */
        return new WGSCoordinate((float) lat, (float) lon, WGSCoordinate.COORD_MODE_RAD);
    }

    /**
     * Converts a latitude/longitude pair to x and y coordinates in the
     * Transverse Mercator projection.  Note that Transverse Mercator is not
     * the same as UTM; a scale factor is required to convert between them.
     *
     * Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
     * GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
     *
     * @param wgs the input coordinate, in WGS84 format.
     * 
     * @return the same coordinate in UTM format.
     */
    public static UTMCoordinate WGS2UTM(WGSCoordinate wgs) {
    	double phi = wgs.getLatitude();
    	double lambda = wgs.getLongitude();
    	
    	/* Calculate UTM zone */
        int zone = (int) ((wgs.getLongitudeDegrees() + 180.0) / 6.0) + 1;
        
        /* Calculate ep2 */
        double ep2 = (Math.pow(sm_a, 2) - Math.pow(sm_b, 2)) / Math.pow(sm_b, 2);
        
        /* Calculate nu2 */
        double nu2 = ep2 * Math.pow(Math.cos(phi), 2);
    
        /* Calculate n */
        double n = Math.pow(sm_a, 2) / (sm_b * Math.sqrt(1.0 + nu2));
    
        /* Calculate t */
        double t = Math.tan(phi);
        double t2 = t * t;
        
        /* Calculate l */
        double lambda0 = UTMCoordinate.calcCentralMeridian(zone);
        double l = lambda - lambda0;
    
        /* Calculate coefficients for l^n in the equations below so a normal human being
         * can read the expressions for easting and northing
         * l^1 and l^2 have coefficients of 1.0 */
        double l3coef = 1.0 - t2 + nu2;
        double l4coef = 5.0 - t2 + 9.0 * nu2 + 4.0 * (nu2 * nu2);
        double l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2 - 58.0 * t2 * nu2;
        double l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2 - 330.0 * t2 * nu2;
        double l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2);
        double l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2);
    
        /* Calculate easing (x) */
        double x = n * Math.cos(phi) * l
        	+ (n / 6.0 * Math.pow(Math.cos(phi), 3) * l3coef * Math.pow(l, 3))
            + (n / 120.0 * Math.pow(Math.cos(phi), 5) * l5coef * Math.pow(l, 5))
            + (n / 5040.0 * Math.pow(Math.cos(phi), 7) * l7coef * Math.pow(l, 7));
    
        /* Calculate northing (y) */
        double y = arcLengthOfMeridian(phi)
            + (t / 2.0 * n * Math.pow(Math.cos(phi), 2) * Math.pow(l, 2))
            + (t / 24.0 * n * Math.pow(Math.cos(phi), 4) * l4coef * Math.pow(l, 4))
            + (t / 720.0 * n * Math.pow(Math.cos(phi), 6) * l6coef * Math.pow(l, 6))
            + (t / 40320.0 * n * Math.pow(Math.cos(phi), 8) * l8coef * Math.pow(l, 8));
    
        /* Adjust easing and northing for UTM system. */
        int x0 = (int) Math.round(x * UTMCoordinate.scaleFactor + 500000.0);
        int y0 = (int) Math.round(y * UTMCoordinate.scaleFactor);
        y0 += (y0 < 0 ? 10000000 : 0);
    	
        /* Create coordinate */
        return new UTMCoordinate(x0, y0, zone, (lambda < 0 ? 'S' : 'N'));
    }
    
    public static void main(String[] args) {
    	float lat = Float.parseFloat(args[0]);
    	float lon = Float.parseFloat(args[1]);
    	WGSCoordinate wgs = new WGSCoordinate(lat, lon, WGSCoordinate.COORD_MODE_DEG);

    	DMSCoordinate dms = CoordConverter.WGS2DMS(wgs);
    	System.out.println("DMS: " + dms);
    	
    	UTMCoordinate utm = CoordConverter.WGS2UTM(wgs);
    	System.out.println("UTM: " + utm);
    }
}
