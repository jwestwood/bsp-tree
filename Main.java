import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main {
    //private static final int LINE_COUNT = 2000;
    //private static final int LINE_COUNT = 100;
    private static final int LINE_COUNT = 10;

    private static final int X_LIMIT = 500;
    private static final int Y_LIMIT = 500;

    private List<Line> mLines;
    private BSPTree mTree;
    private Point2D.Double mViewPoint;
    private Random mRNG = new Random();

    private Main() {

        mTree = generateBSPTree(LINE_COUNT);
        mViewPoint = new Point2D.Double(mRNG.nextInt(X_LIMIT), mRNG.nextInt(Y_LIMIT));
        //printDrawOrder();

        BSPTreeViewer viewer = new BSPTreeViewer(mTree, mViewPoint);
        viewer.setVisible(true);
        viewer.addListener(new BSPTreeViewer.Listener() {
            @Override
            public void viewpointChanged(Point2D.Double position, Vec lookVector) {
                mViewPoint = position;
                //printDrawOrder();
            }

            @Override
            public BSPTree generateNewTree(int numLines) {
                mTree = generateBSPTree(numLines);
                return mTree;
            }
        });
    }

    private BSPTree generateBSPTree(int numLines) {
        mLines = new ArrayList<>();
        for (int i = 0; i < numLines; i++) {
            char c = (char) (i + 65);
            mLines.add(new Line(""+c, mRNG.nextInt(X_LIMIT), mRNG.nextInt(Y_LIMIT), mRNG.nextInt(X_LIMIT), mRNG.nextInt(Y_LIMIT)));
        }

        return new BSPTree(mLines);
    }

    private void printDrawOrder() {
        DrawOrderVisitor visitor = new DrawOrderVisitor(mViewPoint);
        mTree.getRoot().accept(visitor);
        List<BSPTree.Node> drawOrder = visitor.getDrawOrder();

        System.out.println("Draw Order:");
        for (BSPTree.Node node : drawOrder) {
            System.out.println(node.mLine.label);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
