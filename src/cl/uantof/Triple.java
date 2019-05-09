package cl.uantof;

public class Triple {

    protected double x;
    protected double y;
    protected double z;

    Triple(double x, double y, double z) {
        setXYZ(x, y, z);
    }

    Triple(double[] v) {
        setXYZ(v[1], v[2], v[3]);
    }

    Triple() {
        setXYZ(0, 0, 0);
    }

    public void setXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void scalarMult(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public float[] getFloats(float scale, double fx, double fy, double fz) {
        return new float[]{
                (float) ((x - fx) * scale),
                (float) ((y - fy) * scale),
                (float) ((z - fz) * scale)
        };
    }

    public void add(Triple a) {
        this.x += a.x;
        this.y += a.y;
        this.z += a.z;
    }

    public static Triple add(Triple a, Triple b) {
        return new Triple(
                a.x + b.x,
                a.y + b.y,
                a.z + b.z
        );
    }

    public static Triple sub(Triple a, Triple b) {
        return new Triple(
                a.x - b.x,
                a.y - b.y,
                a.z - b.z
        );
    }

    public static Triple dot(Triple a, Triple b) {
        return new Triple(
                a.x * b.x,
                a.y * b.y,
                a.z * b.z
        );
    }

    public static Triple cross(Triple a, Triple b) {
        return new Triple(
                a.y * b.z - a.z * b.y,
                a.x * b.z - a.z * b.x,
                a.x * b.y - a.y * b.x
        );
    }

    public static Triple scalarMult(double s, Triple a) {
        return new Triple(
                s * a.x,
                s * a.y,
                s * a.z
        );
    }

    public static Triple scalarDiv(Triple a, double s) {
        return new Triple(
                a.x / s,
                a.y / s,
                a.z / s
        );
    }

}
