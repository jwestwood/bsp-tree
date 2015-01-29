import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class BSPTree {

    private List<Line> mScene;
    private Node mRoot;

    public BSPTree(List<Line> scene) {
        mScene = scene;

        int centralLineId = getMostCentralLine();
        Line rootLine = scene.remove(centralLineId);
        mRoot = new Node(rootLine, scene);
    }

    public Node getRoot() {
        return mRoot;
    }

    // choosing the most central line as the roof of the BSP tree
    private int getMostCentralLine() {
        double centerX = 0, centerY = 0;
        for (Line line : mScene) {
            centerX += line.x1 + line.x2;
            centerY += line.y1 + line.y2;
        }

        int size = mScene.size();
        centerX /= 2 * size;
        centerY /= 2 * size;
        Point2D.Double sceneCenter = new Point2D.Double(centerX, centerY);

        int nearestLine = 0;
        double nearestDist = Double.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            Line line = mScene.get(i);
            Point2D.Double lineCenter = new Point2D.Double(line.centerX, line.centerY);

            double dist = sceneCenter.distance(lineCenter);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearestLine = i;
            }
        }

        return nearestLine;
    }

    public static interface Visitor {
        void visit(Node node);
    }

    public static class Node {
        public Line mLine;
        public Node mFrontNode;
        public Node mBackNode;

        private Node(Line line, List<Line> children) {
            mLine = line;
            mFrontNode = null;
            mBackNode = null;
            partition(children);
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }

        public boolean isLeaf() {
            return mFrontNode == null && mBackNode == null;
        }

        private void partition(List<Line> children) {
            List<Line> frontChildren = new ArrayList<>();
            List<Line> backChildren = new ArrayList<>();

            // Create a plane using the partitioning line
            // All other lines will be tested against this plane
            Hyperplane plane = new Hyperplane(mLine);

            for (Line line : children) {
                Point2D.Double intersect = plane.intersects(line);

                // test if the plane does not cut the line, only passes exactly though one end of the line
                // in this case the line should not be subdivided
                boolean endOfLine = endOfLine(line, intersect);

                if (intersect != null && !endOfLine) {
                    // subdivide the segment at the point of intersection
                    Line subLine1 = new Line(line.label, line.x1, line.y1, intersect.x, intersect.y);
                    Line subLine2 = new Line(line.label + "'", intersect.x, intersect.y, line.x2, line.y2);
                    // determine which subsegment is in front of and behind the plane
                    if (subLine1.inFrontOf(mLine)) {
                        frontChildren.add(subLine1);
                        backChildren.add(subLine2);
                    } else {
                        backChildren.add(subLine1);
                        frontChildren.add(subLine2);
                    }
                } else {
                    if (new Line(mLine.centerX, mLine.centerY, line.centerX, line.centerY).inFrontOf(mLine)) {
                        frontChildren.add(line);
                    } else {
                        backChildren.add(line);
                    }
                }
            }

            if (frontChildren.size() > 0) {
                mFrontNode = new Node(frontChildren.remove(0), frontChildren);
            }

            if (backChildren.size() > 0) {
                mBackNode = new Node(backChildren.remove(0), backChildren);
            }
        }

        private static boolean endOfLine(Line line, Point2D.Double point) {
            if (point == null) {
                return false;
            }

            return (line.x1 == point.x && line.y1 == point.y) || (line.x2 == point.x && line.y2 == point.y);
        }

    }
}
