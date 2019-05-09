package cl.uantof;

public class StarGalaxy extends Galaxy {

    double diskSize;
    double galTheta;
    double galPhi;
    int nStars;
    Star[] stars;

    StarGalaxy(double galMass, double aHalo, double vHalo, double rtHalo, double diskSize,
               double galTheta, double galPhi, int nStars) {

        super(galMass, aHalo, vHalo, rtHalo);

        this.diskSize = diskSize;
        this.galTheta = galTheta;
        this.galPhi = galPhi;
        this.nStars = nStars;

        stars = new Star[nStars];
        for(int i = 0; i < nStars; i++) {
            stars[i] = new Star();
        }

        initStars();

    }

    public void initStars() {

        double cosPhi = Math.cos(galPhi);
        double sinPhi = Math.sin(galPhi);
        double cosTheta = Math.cos(galTheta);
        double sinTetha = Math.sin(galTheta);

        double xTry, yTry, zTry, rTry;
        double vXTry, vYTry, vZTry;
        double xRot, yRot, zRot;
        double vXRot, vYRot, vZRot;
        Star star;
        double vCir;

        for(int i = 0; i < nStars; i++) {

            do {
                xTry = diskSize * (1. - 2. * Math.random());
                yTry = diskSize * (1. - 2. * Math.random());
                rTry = Math.sqrt(xTry * xTry + yTry * yTry);
            } while (rTry >= diskSize);
            zTry  = 0.;

            xRot = xTry * cosPhi + yTry * sinPhi * cosTheta + zTry * sinPhi * sinTetha;
            yRot = - xTry * sinPhi + yTry * cosPhi * cosTheta + zTry * cosPhi * sinTetha;
            zRot = - yTry * sinTetha + zTry * cosTheta;

            star = stars[i];
            star.pos.setXYZ(
                    xRot + pos.x,
                    yRot + pos.y,
                    zRot + pos.z
            );

            vCir = Math.sqrt(interiorMass(rTry) / rTry);

            vXTry = -vCir * yTry / rTry;
            vYTry = vCir * xTry / rTry;
            vZTry = 0.;

            vXRot = vXTry * cosPhi + vYTry * sinPhi * cosTheta + vZTry * sinPhi * sinTetha;
            vYRot = -vXTry * sinPhi + vYTry * cosPhi * cosTheta + vZTry * cosPhi * sinTetha;
            vZRot = - vYTry * sinTetha + vZTry * cosTheta;

            star.vel.setXYZ(
                    vXRot + vel.x,
                    vYRot + vel.y,
                    vZRot + vel.z
            );

            star.acc.setXYZ(0., 0., 0.);

        }

    }

    public void setPosVel(Triple pos, Triple vel) {
        super.setPosVel(pos, vel);
        for (int i = 0; i < nStars; i++) {
            stars[i].pos.add(pos);
            stars[i].vel.add(vel);
        }
    }

    public void moveStars(double dtime) {
        for(int i = 0; i < nStars; i++) {
            stars[i].move(dtime);
        }
    }

    public void scaleMass(double massFact) {
        diskSize = 2.5 * Math.pow(massFact, 0.5);
        super.scaleMass(massFact);
    }

    public void setAngles(double galTheta, double galPhi) {
        this.galTheta = galTheta;
        this.galPhi = galPhi;
    }

}
