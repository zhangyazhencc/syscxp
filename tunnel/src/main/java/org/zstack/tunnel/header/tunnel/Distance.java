package org.zstack.tunnel.header.tunnel;

/**
 * Created by DCY on 2017-09-19
 */
public class Distance {

    public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
        double PI = 3.141592653589793231462643383279169399375;
        double latR = 6378140, lonR = 6356755;
        double j = ((lon2 - lon1) * PI * lonR * Math.cos(Math.toRadians(((lat1 + lat2) / 2) * PI / 180)) / 180) * Math.cos(Math.toRadians((lat1 + lat2) / 2));
        double w = (lat2 - lat1) * PI * latR / 180;
        return Math.hypot(j, w);
    }


    public static void main(String[] args){

        double d = getDistance(30.600295, 104.070372, 30.61826, 104.158765);
        System.out.println("Distance2 is:"+d);
    }
}
