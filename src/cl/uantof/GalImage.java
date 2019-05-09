package cl.uantof;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GalImage extends Canvas implements MouseListener, MouseMotionListener, Runnable {

    protected final double T_PERI = 15;
    protected final double DELTA_TIME = 0.04;
    protected double dist, vel;
    protected double rPeri = 3.0;
    protected double galMass = 4.8;
    protected double galAHalo = 0.1;
    protected double galVHalo = 1.0;
    protected double galRTHalo = 5.0;
    protected double galDiskSize = 2.5;
    protected double compMassRat = 1.0;
    protected double compMass = 4.8;
    protected double compAHalo = 0.1;
    protected double compVHalo = 1.0;
    protected double compRTHalo = 5.0;
    protected double compDiskSize = 2.5;
    protected double galTheta = 0;
    protected double galPhi = 0;
    protected double compTheta = 0;
    protected double compPhi = 0;
    boolean bigHalos = false;
    protected int nStarsRed = 125;
    protected int nStarsGreen = 125;

    protected TextField periField;
    protected TextField cMassField;
    protected JSlider galThetaSlider;
    protected JSlider galPhiSlider;
    protected JSlider compThetaSlider;
    protected JSlider compPhiSlider;
    protected GraphCanvas graphCanvas;

    protected double time = -0.1;

    protected Thread crashThread;

    protected Graphics g;
    protected Image offImage;
    protected Image title;

    protected int sizeX;
    protected int sizeY;
    protected boolean sizeHasChanged = false;

    protected boolean zoom;
    protected boolean friction;

    protected double xc, yc, zc, scale;
    protected int galsx,galsy,compsx,compsy;
    int nNewStars = 250;
    protected int[] starsX = new int[nNewStars];
    protected int[] starsY = new int[nNewStars];

    protected StarGalaxy gal = new StarGalaxy(
            galMass, galAHalo, galVHalo, galRTHalo, galDiskSize, galTheta, galPhi, nStarsGreen);

    protected StarGalaxy comp = new StarGalaxy(
            compMass, compAHalo, compVHalo, compRTHalo, compDiskSize, compTheta, compPhi, nStarsRed);

    protected Orbit crashOrbit = new Orbit(galMass, compMass, rPeri, T_PERI);

    boolean running;

    Triple comAcc = new Triple();
    TextField nNewStarsField;

    int prevX, prevY;
    float xRot, yRot, zRot;
    Matrix3D mat = new Matrix3D();
    Matrix3D transMat = new Matrix3D();

    GalImage(TextField periField, TextField cMassField, JSlider galThetaSlider,
             JSlider galPhiSlider, JSlider compThetaSlider, JSlider compPhiSlider,
             TextField nNewStarsField, GraphCanvas graphCanvas , Image title) {
        this.periField = periField;
        this.cMassField = cMassField;
        this.galThetaSlider = galThetaSlider;
        this.galPhiSlider = galPhiSlider;
        this.compThetaSlider = compThetaSlider;
        this.compPhiSlider = compPhiSlider;
        this.nNewStarsField = nNewStarsField;
        this.graphCanvas = graphCanvas;
        this.title = title;

        addMouseListener(this);
        addMouseMotionListener(this);
        crashThread = new Thread(this);

        sizeX = 800;
        sizeY = 600;

        gal.setPosVel(crashOrbit.body1Pos, crashOrbit.body1Vel);
        comp.setPosVel(crashOrbit.body2Pos, crashOrbit.body2Vel);

        periField.setText(toText(3.5 * rPeri));
        cMassField.setText(toText(compMassRat));
        galThetaSlider.setValue((int) galTheta);
        galPhiSlider.setValue((int) galPhi);
        compThetaSlider.setValue((int) compTheta);
        compPhiSlider.setValue((int) compPhi);
        nNewStarsField.setText(toText(nNewStars));

    }

    public void paint(Graphics g) {
        update(g);
    }

    public void update(Graphics h) {

        if(g == null || sizeHasChanged) {
            sizeHasChanged = false;
            offImage = createImage(sizeX, sizeY);
            g = offImage.getGraphics();
        }

        if(time < 0) {
            g.drawImage(title, 0 , 100, this);
            g.setColor(Color.WHITE);
            Font oldFont = g.getFont();
            g.setFont(new Font("default", Font.BOLD, 16));
            g.drawString("Gal Crush (Remake)",20,225);
            g.setFont(oldFont);
            g.drawString("Concept: Chris Mihos",20,265);
            g.drawString("Scientific Development: Chris Mihos, Greg Bothun",20,280);
            g.drawString("Original programming: Chris Mihos, Dave Caley, Bob Vawter",20,295);
            g.drawString("Remake programming: Rodrigo González-Castillo",20,310);
            g.drawString("Hit Start, Stop, Reset to initialize...",20,350);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, sizeX, sizeY);
            g.setColor(Color.green);
            g.fillOval(galsx - 2, galsy - 2, 5, 5);
            g.setColor(Color.red);
            g.fillOval(compsx - 2, compsy - 2, 5, 5);
            g.setColor(new Color(255, 150, 150));
            g.drawString("Galaxy Separation: " + Double.toString(round(dist)) + " kpc",10,sizeY - 20);
            g.setColor(new Color(150, 150, 255));
            g.drawString("Relative Velocity: " + Double.toString(round(vel)) + " km/s",225,sizeY - 20);
            g.setColor(Color.WHITE);
            g.drawString("Elapsed Time: " + Double.toString(round(12.0 * time)) + " Myr",10,25);
            for(int i = 0; i < nStarsGreen + nStarsRed; i++) {
                g.drawLine(starsX[i], starsY[i], starsX[i], starsY[i]);
            }
        }

        h.drawImage(offImage, 0, 0, this);

    }

    @Override
    public void run() {
        while(running) {

            gal.acc = comp.acceleration(gal.pos);
            comp.acc = gal.acceleration(comp.pos);

            dist = 3.5 * Triple.sub(gal.pos, comp.pos).mag();

            if(friction) {
                gal.acc = Triple.add(
                        gal.acc,
                        comp.dynFric(gal.interiorMass(dist / 3.5), gal.pos, gal.vel)
                );
                comp.acc = Triple.add(
                        comp.acc,
                        gal.dynFric(comp.interiorMass(dist / 3.5), comp.pos, comp.vel)
                );
            }

            double totalMass = gal.mass + comp.mass;
            comAcc.setXYZ(
                    (gal.mass * gal.acc.x + comp.mass * comp.acc.x) / totalMass,
                    (gal.mass * gal.acc.y + comp.mass * comp.acc.y) / totalMass,
                    (gal.mass * gal.acc.z + comp.mass * comp.acc.z) / totalMass
            );

            gal.acc = Triple.sub(gal.acc, comAcc);
            comp.acc = Triple.sub(comp.acc, comAcc);

            for(int i = 0; i < nStarsGreen; i++) {
                gal.stars[i].acc = Triple.add(
                        gal.acceleration(gal.stars[i].pos),
                        comp.acceleration(gal.stars[i].pos)
                );
            }
            for(int i = 0; i < nStarsRed; i++) {
                comp.stars[i].acc = Triple.add(
                        comp.acceleration(comp.stars[i].pos),
                        gal.acceleration(comp.stars[i].pos)
                );
            }

            gal.move(DELTA_TIME);
            gal.moveStars(DELTA_TIME);

            comp.move(DELTA_TIME);
            comp.moveStars(DELTA_TIME);


            dist = 3.5 * Triple.sub(gal.pos, comp.pos).mag();
            vel = 250. * Triple.sub(gal.vel, comp.vel).mag();

            // Add the points in the graph
            graphCanvas.addPoints(time * 12, dist, time * 12, vel);

            if(zoom) {
                xc = gal.pos.x;
                yc = gal.pos.y;
                zc = gal.pos.z;
            } else {
                xc = 0.;
                yc = 0.;
                zc = 0.;
            }

            // opaque part
            int holder[] = new int[3];

            mat.transform(gal.pos.getFloats((float) scale, xc, yc, zc), holder, 1);
            galsx = holder[0] + (sizeX / 2);
            galsy = (sizeY / 2) - holder[1];

            mat.transform(comp.pos.getFloats((float) scale, xc, yc, zc), holder, 1);
            compsx = holder[0] + (sizeX / 2);
            compsy = (sizeY / 2) - holder[1];

            if(starsX == null) {
                starsX = new int[nStarsGreen + nStarsRed];
                starsY = new int[nStarsGreen + nStarsRed];
            }

            for(int i = 0; i < nStarsGreen; i++) {
                mat.transform(gal.stars[i].pos.getFloats((float) scale, xc, yc, zc), holder, 1);
                starsX[i] = holder[0] + (sizeX / 2);
                starsY[i] = (sizeY / 2) - holder[1];
            }

            for(int i = 0; i < nStarsGreen; i++) {
                mat.transform(comp.stars[i].pos.getFloats((float) scale, xc, yc, zc), holder, 1);
                starsX[i + nStarsGreen] = holder[0] + (sizeX / 2);
                starsY[i + nStarsGreen] = (sizeY / 2) - holder[1];
            }

            repaint();

            time += DELTA_TIME;

            try {
                crashThread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }

        }


    }

    public void touchParams() {

        time = 0;
        running = false;

        double newPeri = Double.valueOf(periField.getText());
        double newCompMmass = Double.valueOf(cMassField.getText());

        nNewStars = Double.valueOf(nNewStarsField.getText()).intValue();
        // only even
        nNewStars = nNewStars % 2 > 0 ? nNewStars + 1 : nNewStars;

        nStarsRed = nNewStars / 2;
        nStarsGreen = nNewStars / 2;

        final double ANGLE_FACTOR = 2. * Math.PI / 360.;

        galTheta = galThetaSlider.getValue() * ANGLE_FACTOR;
        galPhi = galPhiSlider.getValue() * ANGLE_FACTOR;
        compTheta = compThetaSlider.getValue() * ANGLE_FACTOR;
        compPhi = compPhiSlider.getValue() * ANGLE_FACTOR;

        galMass = 4.8;
        
        gal = new StarGalaxy(galMass, galAHalo, galVHalo, galRTHalo, galDiskSize, galTheta,
                galPhi, nStarsGreen);
        comp = new StarGalaxy(compMass, compAHalo, compVHalo, compRTHalo, compDiskSize, compTheta,
                compPhi, nStarsRed);
        
        if(bigHalos) {
            galRTHalo = 20.;
            galMass = Math.pow(galVHalo, 2) * Math.pow(galRTHalo, 3) / 
                    Math.pow((galAHalo + galRTHalo), 2);
            gal.rtHalo = galRTHalo;
            gal.mass = galMass;
        }
        
        if(newPeri != 0.) {
            rPeri = Math.abs(newPeri) / 3.5;
        } else {
            rPeri = 0.001;
            periField.setText(Double.toString(round(rPeri * 3.5)));
        }
        
        if(compMassRat != 0.) {
            compMassRat = Math.abs(newCompMmass);
        } else {
            compMassRat = 0.001;
        }
        
        cMassField.setText(Double.toString(round(compMassRat)));

        compMass = galMass * compMassRat;
        comp.scaleMass(compMassRat);

        if(bigHalos) {
            compRTHalo = comp.rtHalo * 4.;
            compMass = Math.pow(compVHalo, 2) * Math.pow(compRTHalo, 3) /
                    Math.pow((compAHalo + compRTHalo), 2);
            comp.rtHalo = compRTHalo;
            comp.mass = compMass;
        }

        crashOrbit.update(galMass, compMass, rPeri, T_PERI);

        gal.setAngles(galTheta, galPhi);
        comp.setAngles(compTheta, compPhi);

        gal.setPosVel(crashOrbit.body1Pos, crashOrbit.body1Vel);
        gal.initStars();

        comp.setPosVel(crashOrbit.body2Pos, crashOrbit.body2Vel);
        comp.initStars();

        if (zoom) {
            xc = gal.pos.x;
            yc = gal.pos.y;
            scale = 20.;
        } else {
            xc = 0.;
            yc = 0.;
            scale = 8.;
        }

        galsx = (int) (scale * (gal.pos.x - xc) + (sizeX / 2));
        galsy = (int) ((sizeY / 2) - scale * (gal.pos.y - yc));
        compsx = (int) (scale * (comp.pos.x - xc)+(sizeX / 2));
        compsy = (int) ((sizeY / 2) - scale * (comp.pos.y - yc));

        starsX = new int[nStarsGreen + nStarsRed];
        starsY = new int[nStarsGreen + nStarsRed];

        for(int i = 0; i < nStarsGreen; i++) {
            starsX[i]= (int) (scale * (gal.stars[i].pos.x - xc) + (sizeX / 2));
            starsY[i]= (int) ((sizeY / 2) - scale * (gal.stars[i].pos.y - yc));
        }

        for(int i = 0; i < nStarsRed; i++) {
            starsX[i + nStarsGreen] = (int) (scale * (comp.stars[i].pos.x - xc) + (sizeX / 2));
            starsY[i + nStarsGreen] = (int) ((sizeY / 2) - scale * (comp.stars[i].pos.y - yc));
        }

        mat.unit();
        repaint();

    }

    @Override
    public void setSize(Dimension size) {
        super.setSize(size);
        sizeX = size.width;
        sizeY = size.height;
        sizeHasChanged = true;
    }


    public void go(boolean activate) {
        running = activate;
        if(running) {
            sizeX = getWidth();
            sizeY = getHeight();
            crashThread = new Thread(this);
            crashThread.start();
        }
    }

    public double round(double r) {
        return Math.round(r * 10) / 10;
    }

    private String toText(double v) {
        return Double.toString(round(v));
    }



    @Override
    public void mousePressed(MouseEvent event) {
        prevX = event.getX();
        prevY = event.getY();
        event.consume();
    }

    @Override
    public void mouseDragged(MouseEvent event) {

        int x = event.getX();
        int y = event.getY();

        if(!(event.isShiftDown())) {
            // We create a translation matrix and apply it to the main matrix
            transMat.unit();
            xRot = (float) ((prevY - y) * 360.f / getSize().width);
            yRot = (float) ((x - prevX) * 360.f / getSize().height);
            transMat.xrot(xRot);
            transMat.yrot(yRot);
            mat.mult(transMat);
        } else if(event.isShiftDown()) {
            transMat.unit();
            zRot = (float) ((prevX - x) * 360.f / getSize().width);
            transMat.zrot(zRot);
            mat.mult(transMat);
            float zoomDelta = (float) ((prevY - y) * 64. / getSize().height);
            scale += zoomDelta;
            if(scale < 4) {
                scale = 4;
            }
            if(scale > 40) {
                scale = 40;
            }
        }

        if(!running) {

            if(zoom) {
                xc = gal.pos.x;
                yc = gal.pos.y;
                zc = gal.pos.z;
            } else {
                xc = 0.;
                yc = 0.;
                zc = 0.;
            }

            int holder[] = new int[3];
            mat.transform(gal.pos.getFloats((float) scale, xc, yc, zc), holder, 1);
            galsx = (int)	(holder[0] + (sizeX / 2));
            galsy = (int)	((sizeY / 2) - holder[1]);

            mat.transform(comp.pos.getFloats((float) scale, xc, yc, zc), holder, 1);
            compsx = (int) (holder[0] + (sizeX / 2));
            compsy = (int) ((sizeY / 2) - holder[1]);

            for(int i = 0; i < nStarsGreen; i++) {
                mat.transform(gal.stars[i].pos.getFloats((float) scale, xc, yc, zc), holder, 1);
                starsX[i] = holder[0] + (sizeX / 2);
                starsY[i] = (sizeY / 2) - holder[1];
            }

            for(int i = 0; i < nStarsGreen; i++) {
                mat.transform(comp.stars[i].pos.getFloats((float) scale, xc, yc, zc), holder, 1);
                starsX[i + nStarsGreen] = holder[0] + (sizeX / 2);
                starsY[i + nStarsGreen] = (sizeY / 2) - holder[1];
            }
            repaint();

        }

        prevX = x;
        prevY = y;
        event.consume();

    }

    @Override
    public void mouseReleased(MouseEvent event) { }
    @Override
    public void mouseClicked(MouseEvent event) { }
    @Override
    public void mouseEntered(MouseEvent event) { }
    @Override
    public void mouseExited(MouseEvent event) { }
    @Override
    public void mouseMoved(MouseEvent event) { }

}
