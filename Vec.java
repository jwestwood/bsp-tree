
public class Vec {

    public final double x, y, z, a;
    private double length = Double.NaN;

    public Vec(double x, double y, double z, double a) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.a = a;
    }

    public Vec(double x, double y, double z) {
        this(x, y, z, 0);
    }

    public Vec(double x, double y) {
        this(x, y, 0, 0);
    }

    public Vec(Vec vec) {
        this(vec.x, vec.y, vec.z, vec.a);
    }

    public Vec normalise() {
        double len = length();
        if (len == 0) {
            len = 1;
        }
        return new Vec(x / len, y / len, z / len, a);
    }

    public double length() {
        if (length != length) {
            length = Math.sqrt(x * x + y * y + z * z);
        }
        return length;
    }

    public double dot(Vec v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec cross(Vec v) {
        return new Vec(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x );
    }
}
