package trukkeruae.trukkertech.com.trukkeruae.ModelClass;

public class MixUtils {

    public static String formatDist(float meters) {
        if (meters < 1000) {
            return ((int) meters) + " m";
        }
        else {
            return formatDec(meters / 1000f, 1) + " km";
        }
        /*else {
            return ((int) (meters / 1000f)) + "km";
        }*/

        /*else if (meters < 10000) {
            return formatDec(meters / 1000f, 1) + "km";
        } else {
            return ((int) (meters / 1000f)) + "km";
        }*/
    }

    static String formatDec(float val, int dec) {
        int factor = (int) Math.pow(10, dec);
        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;
        return front + "." + back;
    }
}