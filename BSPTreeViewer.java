import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class BSPTreeViewer extends JFrame {
    private static final long serialVersionUID = -2414813615705018370L;
    private Listener mListener;

    public BSPTreeViewer(BSPTree tree, Point2D.Double viewPoint) {
        Container content = getContentPane();
        content.add(new BSPTreeViewPanel(tree, viewPoint));
        setTitle("BSP Tree Viewer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    public void addListener(Listener listener) {
        this.mListener = listener;
    }

    public static interface Listener {
        void viewpointChanged(Point2D.Double position, Vec lookVector);
        BSPTree generateNewTree(int numLines);
    }

    private class BSPTreeViewPanel extends JPanel {
        private static final long serialVersionUID = 2898398150709435188L;
        private BSPTree mBSPTree;
        private Point2D.Double mViewPoint;
        private List<BSPTree.Node> mDrawOrder;
        private Gradient mGradient;

        private int mLineCountSeed = 3;
        private int mLineCount = 8;

        public BSPTreeViewPanel(BSPTree bspTree, Point2D.Double viewPoint) {
            // Rainbows :3
            mGradient = new Gradient(new Color(255, 0, 0), new Color(255, 0, 128));
            mGradient.addStop(1/11d, new Color(255, 128, 0));
            mGradient.addStop(2/11d, new Color(255, 255, 0));
            mGradient.addStop(3/11d, new Color(128, 255, 0));
            mGradient.addStop(4/11d, new Color(0, 255, 0));
            mGradient.addStop(5/11d, new Color(0, 255, 128));
            mGradient.addStop(6/11d, new Color(0, 255, 255));
            mGradient.addStop(7/11d, new Color(0, 128, 255));
            mGradient.addStop(8/11d, new Color(0, 0, 255));
            mGradient.addStop(9/11d, new Color(128, 0, 255));
            mGradient.addStop(10/11d, new Color(255, 0, 255));

            setBSPTree(bspTree, viewPoint);
        }

        public void setBSPTree(BSPTree tree, Point2D.Double viewPoint) {
            mBSPTree = tree;
            mViewPoint = viewPoint;
            mDrawOrder = getDrawOrder();
            setPreferredSize(new Dimension(500, 520));
            repaint();

            addMouseWheelListener(new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int rot = e.getWheelRotation();
                    if (rot != 0) {
                        mLineCountSeed = Math.min(11, Math.max(1, mLineCountSeed - rot));
                        mLineCount = getLineCount(mLineCountSeed);
                        mBSPTree = mListener.generateNewTree(mLineCount);
                        mDrawOrder = getDrawOrder();
                        repaint();
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    System.out.println(e.getButton());
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON3:
                            mBSPTree = mListener.generateNewTree(mLineCount);
                            break;
                        default:
                            mViewPoint = new Point2D.Double(e.getX(), e.getY());
                            mListener.viewpointChanged(mViewPoint, new Vec(1, 0));
                            break;
                    }
                    mDrawOrder = getDrawOrder();
                    repaint();
                }
            });
        }

        private int getLineCount(int seed) {
            return (int) (Math.pow(2, seed));
        }

        private List<BSPTree.Node> getDrawOrder() {
            DrawOrderVisitor visitor = new DrawOrderVisitor(mViewPoint);
            mBSPTree.getRoot().accept(visitor);
            return visitor.getDrawOrder();
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.black);
            g2.fillRect(0,  0, getWidth(), getHeight());

            for (int i = 0; i < mDrawOrder.size(); i++) {
                BSPTree.Node node = mDrawOrder.get(i);
                Line line = node.mLine;
                Vec normal = line.face.normalise();

                // draw the line segment normal
//                g2.setColor(Color.black);
//                g2.setStroke(new BasicStroke(1));
//                g2.drawLine((int) line.centerX,
//                        (int) line.centerY,
//                        (int) (line.centerX + 10 * normal.x),
//                        (int) (line.centerY + 10 * normal.y));

                g2.setStroke(new BasicStroke(2));
                g2.setColor(mGradient.getColor(i / (mDrawOrder.size() - 1d)));

                // draw the line segment
                g2.drawLine((int) node.mLine.x1, (int) node.mLine.y1, (int) node.mLine.x2, (int) node.mLine.y2);

                // draw the label
//                g2.drawString(node.mLine.label + "(" + i + ")",
//                        (int) (line.centerX + 25 * normal.x),
//                        (int) (line.centerY + 25 * normal.y));
            }

            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect((int) mViewPoint.getX() - 2, (int) mViewPoint.getY() - 2, 4, 4);
            g2.drawString("V", (int) mViewPoint.getX() + 5, (int) mViewPoint.getY() + 13);

            g2.drawString(mLineCount + " lines, " + mDrawOrder.size() + " segments.", 5, getHeight() - 5);
        }
    }

    private static class Gradient {
        private List<Stop> mStops;

        public Gradient(Color start, Color end) {
            mStops = new LinkedList<>();
            mStops.add(new Stop(start, 0));
            mStops.add(new Stop(end, 1));
        }

        public void addStop(double factor, Color color) {
            if (factor < 0 || factor > 1) {
                throw new IllegalArgumentException("factor must be >= 0 and <= 1");
            }
            for (int i = 0; i< mStops.size(); i++ ) {
                Stop s = mStops.get(i);
                if (factor > s.p) {
                    continue;
                } else if (factor == s.p) {
                     s.c = color;
                     return;
                } else {
                    mStops.add(i, new Stop(color, factor));
                    return;
                }
            }
        }

        public Color getColor(double position) {
            if (position <= 0) {
                return mStops.get(0).c;
            } else if (position >= 1) {
                return mStops.get(mStops.size() - 1).c;
            }

            Stop prevStop = mStops.get(0);
            Stop nextStop = prevStop;
            for (int i = 0; i < mStops.size(); i++) {
                Stop s = mStops.get(i);
                if (position <= s.p) {
                    nextStop = s;
                    break;
                } else {
                    prevStop = s;
                }
            }

            double range = nextStop.p - prevStop.p;
            double lerp = (position - prevStop.p) / range;

            double red = prevStop.c.getRed() + (nextStop.c.getRed() - prevStop.c.getRed()) * lerp;
            double green = prevStop.c.getGreen() + (nextStop.c.getGreen() - prevStop.c.getGreen()) * lerp;
            double blue = prevStop.c.getBlue() + (nextStop.c.getBlue() - prevStop.c.getBlue()) * lerp;
            double alpha = prevStop.c.getAlpha() + (nextStop.c.getAlpha() - prevStop.c.getAlpha()) * lerp;
            return new Color((int) red, (int) green, (int) blue, (int) alpha);
        }

        private class Stop {
            Color c;
            double p;
            Stop(Color color, double pos) {
                c = color;
                p = pos;
            }
        }

    }
}
