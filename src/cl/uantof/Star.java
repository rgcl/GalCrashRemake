package cl.uantof;

public class Star {

    protected double mass, pot, ebind;
    protected Triple pos;
    protected Triple vel;
    protected Triple acc;

    Star(double mass, double x, double y, double z, double vx, double vy, double vz) {
        this.mass = mass;
        pos = new Triple(x, y, z);
        vel = new Triple(vx, vy, vz);
        acc = new Triple();
    }

    Star() {
        mass = 1.;
        pos = new Triple();
        vel = new Triple();
        acc = new Triple();
    }


    Star(double mass, Triple pos, Triple vel) {
        this.mass = mass;
        this.pos = pos;
        this.vel = vel;
        acc = new Triple();
    }

    public void move(double dtime) {
        double dtime2 = dtime * dtime;
        pos.setXYZ(
                pos.x + vel.x * dtime + 0.5f * acc.x * dtime2,
                pos.y + vel.y * dtime + 0.5f * acc.y * dtime2,
                pos.z + vel.z * dtime + 0.5f * acc.z * dtime2
        );
        vel.setXYZ(
                vel.x + acc.x * dtime,
                vel.y + acc.y * dtime,
                vel.z + acc.z * dtime
        );


    }

    public void print() {

        System.out.println("Mass: " + mass);
        System.out.println("Pos : " + pos.x + "," + pos.y + "," + pos.z);
        System.out.println("Vel : " + vel.x + "," + vel.y + "," + vel.z);
        System.out.println("Acc : " + acc.x + "," + acc.y + "," + acc.z);

    }

}
