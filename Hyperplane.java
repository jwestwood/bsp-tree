import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Hyperplane {

    public final double y;
    public final double x;
    public final double slope;

    public Hyperplane(Line line) {
        slope = Double.isInfinite(line.slope) ? Double.POSITIVE_INFINITY : line.slope;

        if (slope == Double.POSITIVE_INFINITY) {
            x = line.x1;
            y = Double.NaN;
        }  else if (slope == 0) {
            x = Double.NaN;
            y = line.y1;
        } else {
            x = line.x1 - (line.y1 / slope);
            y = line.y1 - (slope * line.x1);
        }
    }

    public boolean isVertical() {
        return slope == Double.POSITIVE_INFINITY;
    }

    public boolean isHorizontal() {
        return slope == 0;
    }

    /**
     * Calculates the point of intersection between this plane and the given plane.
     * @param plane another hyperplane.
     * @return the 2D point of intersection, or null if the planes are parallel
     */
    public Point2D.Double intersects(Hyperplane plane) {
        if (plane.slope == slope) {
            return null;
        }

        double xIntercept = (plane.y - y) / (slope - plane.slope);
        double yIntercept = plane.slope * xIntercept + plane.y;
        if (Double.isNaN(xIntercept)) {
            xIntercept = plane.x;
            yIntercept = slope * xIntercept + y;
        }
        if (Double.isNaN(yIntercept)) {
            xIntercept = x;
            yIntercept = plane.slope * x + plane.y;
        }
        return new Point2D.Double(xIntercept, yIntercept);
    }

    /**
     * Calculates the point of intersection between this plane and the given line segment.
     * @param line a line segment to test against this plane
     * @return the 2D point of intersection, or null if the line segment does not intersect this plane.
     */
    public Point2D.Double intersects(Line line) {
        Point2D.Double intercept = intersects(new Hyperplane(line));
        if (intercept != null) {
            double dist = Line2D.ptSegDist(line.x1, line.y1, line.x2, line.y2, intercept.x, intercept.y);
            // rounding errors often cause almost zero distance for a point exactly on the line, so long as the distance
            // is below this tollerance then the lines intercept.
            if (Math.abs(dist) < 0.0001) {
                return intercept;
            }
        }
        return null;
    }

}
