package main.java.utilities;

public class AreaChecker {
    public static boolean isHit(Double x, Double y, Double r){
        r = Math.abs(r);
        if(y <= r && x >= -r && x <= 0 && y>=0)
            return true;
        if(y*y + x*x <= r*r/4 && y >= 0 && x >= 0)
            return true;
        if(y >= -x - r && y <= 0 && x <= 0)
            return true;
        return false;
    }
}
