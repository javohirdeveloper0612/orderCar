package com.example.util;

import com.example.entity.LocationClient;

public class KmUtil {
    public static Double calculateDistance(LocationClient A, LocationClient B) {


        double lat1 = Math.toRadians(A.getLatitude());
        double lon1 = Math.toRadians(A.getLongitude());
        double lat2 = Math.toRadians(B.getLatitude());
        double lon2 = Math.toRadians(B.getLongitude());

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Ma'lumotlarni kilometerga o'tkazish
        double earthRadiusKm = 6371;
        double distanceInKm = earthRadiusKm * c;
        double roundedDistance = Math.round(distanceInKm * 100) / 100.0;

        return  roundedDistance;
    }

    public static long calculateSum(double distanceInKm){

            long sum = 0;
            if (distanceInKm < 50) {
                sum = 1250000*100;
            } else if (distanceInKm >= 50 && distanceInKm <= 130) {
                sum = (long) (distanceInKm * 25000*100);
            } else if (distanceInKm > 130 && distanceInKm <= 200) {
                sum = (long) (distanceInKm * 20000*100);
            } else if (distanceInKm > 200) {
                sum = (long) (distanceInKm * 15000*100);
            }
            return sum;

    }
}
