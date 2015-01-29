import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class DrawOrderVisitor implements BSPTree.Visitor {

    private List<BSPTree.Node> mDrawOrder = new ArrayList<>();
    private Point2D.Double mViewPoint;

    public DrawOrderVisitor(Point2D.Double viewPoint) {
        if (viewPoint == null) {
            throw new NullPointerException("Viewpoint cannot be null");
        }
        mViewPoint = viewPoint;
    }

    public List<BSPTree.Node> getDrawOrder() {
        return mDrawOrder;
    }

    public void reset() {
        mDrawOrder = new ArrayList<>();
    }

    @Override
    public void visit(BSPTree.Node node) {
        if (node.isLeaf()) {
            mDrawOrder.add(node);
        } else {
            if (lineInsideViewpoint(node.mLine)) {
                tryVisit(node.mFrontNode);
                tryVisit(node.mBackNode);
            } else {
                if (node.mLine.pointInFront(mViewPoint)) {
                    tryVisit(node.mBackNode);
                    mDrawOrder.add(node);
                    tryVisit(node.mFrontNode);
                } else {
                    tryVisit(node.mFrontNode);
                    mDrawOrder.add(node);
                    tryVisit(node.mBackNode);
                }
            }
        }
    }

    private void tryVisit(BSPTree.Node node) {
        if (node != null) {
            node.accept(this);
        }
    }

    private boolean lineInsideViewpoint(Line line) {
        return line.centerX == mViewPoint.x && line.centerY == mViewPoint.y;
    }

}
