import java.awt.geom.Point2D;

public class Intersect {

//    public static void main(String[] arg) {
//        test(1, 1, 4, 1,  2, 4, 2, -1); // plus sign
//        test(1, 1, 4, 1,  1, 2, 4, 2); // parallel horizontal
//        test(2, 4, 2, -1, 3, 4, 3, -1); // parallel vertical
//        test(1, 1, 3, 3,  1, 3, 3, 1); // cross sign
//        test(1, 1, 4, 1,  8, 4, 8, -1); // non-intersecting plus
//        test(1, 1, 3, 3,  5, 3, 7, 1); // non-intersecting cross
//        test(1, 1, 1, 4,  1, 2, 3, 2); // touching end on edge
//        test(1, 1, 1, 4,  1, 1, 4, 1); // touching end on end
//    }
//
//    private static void test(double px, double py, double px2, double py2, double qx, double qy, double qx2, double qy2) {
//        String str = String.format("[%f, %f, %f, %f] and [%f, %f, %f, %f]", px, py, px2, py2, qx, qy, qx2, qy2);
//        Point2D.Double point = linesIntersect(px, py, px2, py2, qx, qy, qx2, qy2);
//
//        if (point != null) {
//            str += " intersect at (" + point.x + ", " + point.y + ")";
//        } else {
//            str += " do not intersect";
//        }
//        System.out.println(str);
//    }

    /**
     * Tests whether the lines (px,py) -> (px2,py2) and (qx,qy) -> (qx2,qy2) intersect each other. Returns the point
     * of intersection, or null if the lines do not intersect.
     * @param px 1st x coordinate of line 1
     * @param py 1st y coordinate of line 1
     * @param px2 2nd x coordinate of line 1
     * @param py2 2nd y coordinate of line 1
     * @param qx 1st x coordinate of line 2
     * @param qy 1st y coordinate of line 2
     * @param qx2 2nd x coordinate of line 2
     * @param qy2 2nd y coordinate of line 2
     * @return The point of intersection, or null if the lines do not intersect.
     */
    public static Point2D.Double linesIntersect(double px, double py, double px2, double py2, double qx, double qy, double qx2, double qy2) {
        // p - the start of the first line
        // q - the start of the second line
        // r - vector from p to the end of the first line
        double rx = px2 - px;
        double ry = py2 - py;
        // s - vector from q to the end of the second line
        double sx = qx2 - qx;
        double sy = qy2 - qy;

        // test that the lines are not parallel
        if (cross(rx, ry, sx, sy) == 0.0) {
            return null; // no intersection
        }

        // intermediate calculation for t...
        double qMinusPCrossS = cross(qx-px, qy-py, sx, sy);
        double rCrossS = cross(rx, ry, sx, sy);
        // t - coefficient of r that determins where on Line A a point lies
        double t = qMinusPCrossS / rCrossS;

        // intermediate calculation for u...
        double qMinusPCrossR = cross(qx-px, qy-py, rx, ry);
        // u - coefficient of s that determins where on Line B a point lies
        double u = qMinusPCrossR / rCrossS;


        // if:  0 <= t <= 1  AND  0 <= u <= 1  then the lines intersect
        // in this case t and u tell us the point of intersection
        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            return new Point2D.Double(px + rx * t, py + ry * t);
        } else {
            return null;
        }
    }

    /**
     * Scalar cross product of two vectors [ux, uy] and [vx, vy].
     */
    private static double cross(double ux, double uy, double vx, double vy) {
        return ux * vy - vx * uy;
    }
}
