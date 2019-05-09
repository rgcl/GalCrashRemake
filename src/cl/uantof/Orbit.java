package cl.uantof;

public class Orbit {

    protected double energy;
    protected double rPeri;
    protected double tPeri;
    protected double eccentricity;
    protected double mass1, mass2;
    protected Triple body1Pos, body2Pos;
    protected Triple body1Vel, body2Vel;


    Orbit(double mass1, double mass2, double rPeri, double tPeri) {
        energy = 0.0;
        eccentricity = 1.0;
        this.mass1 = mass1;
        this.mass2 = mass2;
        this.rPeri = rPeri;
        this.tPeri = tPeri;

        body1Pos = new Triple();
        body1Vel = new Triple();
        body2Pos = new Triple();
        body2Vel = new Triple();

        init(this.mass1, this.mass2, this.rPeri, this.tPeri);

    }

    public void update(double mass1, double mass2, double rPeri, double tPeri) {
        init(mass1, mass2, rPeri, tPeri);
    }


    public void init(double mass1, double mass2, double rPeri, double tPeri) {

        // Parabolic Orbit
        double mu = mass1 + mass2;
        double p = 2 * rPeri;
        double nhat = Math.sqrt(mu / p / p / p);
        double cotS = 3.0 * nhat * tPeri;
        double s = Math.atan(1.0 / cotS);
        double cotTheta = Math.pow((1. / Math.tan(s / 2.)), 0.3333);
        double theta = Math.atan(1. / cotTheta);
        double tanFon2 = 2. / Math.tan(2. * theta);
        double r = (p / 2.) * (1 + tanFon2 * tanFon2);
        double vel = Math.sqrt(2. * mu / r);
        double sinSqPhi = p / (2. * r);
        double phi = Math.asin(Math.sqrt(sinSqPhi));
        double f = 2. * Math.atan(tanFon2);
        double xc = -r * Math.cos(f);
        double yc = r * Math.sin(f);
        double vxc = vel * Math.cos(f + phi);
        double vyc = -vel * Math.sin(f + phi);
        double xcom = mass2 * xc / (mass1 + mass2);
        double ycom = mass2 * yc / (mass1 + mass2);
        double vxcom = mass2 * vxc / (mass1 + mass2);
        double vycom = mass2 * vyc / (mass1 + mass2);

        body1Pos.setXYZ(-xcom, -ycom, 0.);
        body1Vel.setXYZ(-vxcom, -vycom, 0.);

        body2Pos.setXYZ(xc - xcom, yc - ycom, 0.);
        body2Vel.setXYZ(vxc - vxcom, vyc - vycom, 0.);

    }

}

