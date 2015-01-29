import java.awt.geom.Point2D;

public class Line {
    public final double x1, y1, x2, y2;
    public final double centerX, centerY;
    public final double slope;
    public final Vec face;
    public final String label;

    public Line(String label, double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        slope = (y2 - y1) / (x2 - x1);
        centerX = (x1 + x2) / 2;
        centerY = (y1 + y2) / 2;
        face = new Vec(y2 - y1, x1 - x2);
        this.label = label;
    }

    public Line(double x1, double y1, double x2, double y2) {
        this("", x1, y1, x2, y2);
    }

    public Line(String label, Point2D p1, Point2D p2) {
        this(label, p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public Line(Point2D p1, Point2D p2) {
        this("", p1, p2);
    }

    /**
     * @param point A point to test
     * @return true if the given point is in front of this line, otherwise false (point is behind this line)
     */
    public boolean pointInFront(Point2D.Double point) {
        Vec offset = new Vec(point.x - centerX, point.y - centerY).normalise();
        return face.normalise().dot(offset) > 0;
    }

    /**
     * @param plane the reference plane
     * @return true if this line is in front of the reference plane, false if this line is behind the reference plane
     */
    public boolean inFrontOf(Line plane) {
        Vec offset = new Vec(centerX - plane.centerX, centerY - plane.centerY).normalise();
        Vec normal = plane.face.normalise();
        return normal.dot(offset) > 0;
    }

    public Point2D.Double intersects(Line line) {
        return linesIntersect(x1, y1, x2, y2, line.x1, line.y1, line.x2, line.y2);
    }

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
