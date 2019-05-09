package cl.uantof;

public class Galaxy {

    public static final double G = 1.0;

    protected double mass;
    protected double aHalo;
    protected double vHalo;
    protected double rtHalo;

    protected Triple pos;
    protected Triple vel;
    protected Triple acc;

    Galaxy(double mass, double aHalo, double vHalo, double rtHalo) {

        this.mass = mass;
        this.aHalo = aHalo;
        this.vHalo = vHalo;
        this.rtHalo = rtHalo;

        pos = new Triple();
        vel = new Triple();
        acc = new Triple();

    }

    public Galaxy() {
        throw new IllegalArgumentException("Use the other constructor");
    }

    public void setPosVel(Triple pos, Triple vel) {
        this.pos = pos;
        this.vel = vel;
    }

    public void scaleMass(double massFactor) {

        mass = 4.8 * massFactor;
        vHalo = Math.pow(massFactor, 0.25);
        aHalo = 0.1 * Math.pow(massFactor, 0.5);

        double a2 = -mass / vHalo / vHalo;
        double a1 = -2 * aHalo * mass / vHalo / vHalo;
        double a0 = -mass * aHalo * aHalo / vHalo / vHalo;
        double q = a1 / 3. - a2 * a2 / 9.;
        double r = (a1 * a2 - 3. * a0) / 6. - a2 * a2 * a2 / 27.;

        double sqrQ3R = Math.sqrt(q * q * q + r * r);
        double s1 = Math.pow(r + sqrQ3R, 0.333);
        double s2 = Math.pow(r - sqrQ3R, 0.333);

        rtHalo = (s1 + s2) - a2 / 3;

    }

    public void move(double dtime) {

        double dtime2 = dtime * dtime;

        // r(t) = r0 + v0*t + (1/2)*a*t^2
        pos.setXYZ(
                pos.x + vel.x * dtime + 0.5f * acc.x * dtime2,
                pos.y + vel.y * dtime + 0.5f * acc.y * dtime2,
                pos.z + vel.z * dtime + 0.5f * acc.z * dtime2
        );

        // v(t) = v0 + a*t
        vel.setXYZ(
                vel.x + acc.x * dtime,
                vel.y + acc.y * dtime,
                vel.z + acc.z * dtime
        );

    }

    /**
     * Calculate the acceleration to the given position
     * @param pos
     * @return the calculated acceleration
     */
    public Triple acceleration(Triple pos) {

        Triple triple = Triple.sub(pos, this.pos);
        double r = triple.mag();
        // G*m/r^2
        double accMag = -G * interiorMass(r) / (r * r);
        triple.scalarMult(accMag / r);
        return triple;

    }

    public double potential(Triple pos) {

        double r = Triple.sub(pos, this.pos).mag();
        return G * interiorMass(r) / r;

    }

    public double density(double r) {
        // The following comment was in the original code, I copy it exactly as it is:
        // "a stupid way" (sic)
        double rInner = r * 0.99;
        double rOuter = r * 1.01;

        double mInner = interiorMass(rInner);
        double mOuter = interiorMass(rOuter);

        double deltaM = mOuter - mInner;
        double vol = (4./3.) * Math.PI * (Math.pow(rOuter, 3) - Math.pow(rInner, 3));

        return deltaM / vol;

    }


    public double interiorMass(double r) {
        return r < rtHalo ?
                // vHalo^2 * r^3 / (aHalo + 2)^2
                vHalo * vHalo * r * r * r / (( aHalo + r) * (aHalo + r))
                : mass;
    }


    /**
     * dynFric: Sort of mimics a Chandrasekhar-type expression
     * @param pMass
     * @param pPos
     * @param pVel
     * @return
     */
    public Triple dynFric(double pMass, Triple pPos, Triple pVel) {

        double lnGamma = 3.0;

        Triple dVel = Triple.sub(pVel, vel);
        double v = dVel.mag();
        double r = Triple.sub(pPos, pos).mag();

        double galRhol = density(r);
        double fricMag = 4. * Math.PI * G * lnGamma * pMass * galRhol * v / Math.pow((v + 1), 3);

        double factor = fricMag / v;
        return new Triple(
                - dVel.x * factor,
                - dVel.y * factor,
                - dVel.z * factor
        );
    }

    public void consolePrint() {
        System.out.println("Mass: " + mass + " rtHalo: " + rtHalo);
        System.out.println(String.format("Pos: %d, %d, %d", pos.x, pos.y, pos.z));
        System.out.println(String.format("Vel: %d, %d, %d", vel.x, vel.y, vel.z));
        System.out.println(String.format("Acc: %d, %d, %d", acc.x, acc.y, acc.z));
    }

}
