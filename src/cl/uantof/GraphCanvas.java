package cl.uantof;

import java.awt.*;

public class GraphCanvas extends Canvas {

    int maxMeasurements = 10000;
    int height;
    int width;
    int xMax;
    int yMax;
    int pointIndex = 0;
    double xScale;
    double yScale;
    double yScale2;
    double[] xPosPoints;
    double[] yPosPoints;
    double[] xVelPoints;
    double[] yVelPoints;
    Image offImage;
    Graphics g;

    GraphCanvas(int width, int height) {
        this.width = width;
        this.height = height;
        xPosPoints = new double[maxMeasurements];
        yPosPoints = new double[maxMeasurements];
        xVelPoints = new double[maxMeasurements];
        yVelPoints = new double[maxMeasurements];
        xMax = width;
        yMax = height;
        setMax(1000, 1000);
        setBackground(Color.BLACK);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void paint(Graphics g) {
        update(g);
    }

    public void update(Graphics h) {

        offImage = createImage(getSize().width, getSize().height);
        g = offImage.getGraphics();
        g.setColor(Color.darkGray);

        for(int i = 0; i < width; i += 80) {
            g.drawLine(i, 0, i, height);
        }
        for(int i = 0; i < height; i += 20) {
            g.drawLine(0, i, width, i);
        }

        g.setColor(Color.darkGray);
        g.drawRect(0, 0, width - 1, height - 1);
        g.setColor(new Color(255, 150, 150));

        for(int i = 1; i < pointIndex; i++) {
            g.drawLine((int)(xPosPoints[i] * xScale),
                    (int)(height - yPosPoints[i] * yScale2),
                    (int)(xPosPoints[i-1] * xScale),
                    (int)(height - yPosPoints[i -1] * yScale2));
        }
        g.setColor(new Color(150, 150, 255));
        for(int i = 1; i < pointIndex; i++) {
            g.drawLine((int)(xVelPoints[i] * xScale),
                    (int)(height - yVelPoints[i] * yScale),
                    (int)(xVelPoints[i-1] * xScale),
                    (int)(height - yVelPoints[i -1] * yScale));
        }

        g.setColor(Color.WHITE);
        for(int i = 80; i < width; i += 80) {
            g.drawString((int)(i / ((double) width / xMax)) + " Myrs", i - 20, height - 5);
        }

        g.setColor(new Color(150, 150, 255));
        g.drawString("V (km/s)", width - 50, height - 85);

        for(int i = 20; i < height; i += 20) {
            g.drawString((i / (double) height / yMax) + "", width - 40, height - i + 11);
        }

        g.setColor(new Color(255, 150, 150));
        g.drawString("R (kpc)", 5, height - 85);
        for(int i = 20; i < height; i += 20) {
            g.drawString((i/ ((double) height / 150)) + "", 5, height - i + 11);
        }

        h.drawImage(offImage, 0, 0, this);

    }

    public void reset() {
        xPosPoints = new double[maxMeasurements];
        yPosPoints = new double[maxMeasurements];
        xVelPoints = new double[maxMeasurements];
        yVelPoints = new double[maxMeasurements];
        pointIndex = 0;
        repaint();
    }

    public void setMax(int xMax, int yMax) {
        this.xMax = xMax;
        this.yMax = yMax;
        xScale = (double) width / xMax;
        yScale = (double) height / yMax;
        yScale2 = yScale * (200 / 30);
        reset();
        repaint();
    }

    public void addPoints(double xPos, double yPos, double xVel, double yVel) {
        if(pointIndex < maxMeasurements) {
            xPosPoints[pointIndex] = xPos;
            yPosPoints[pointIndex] = yPos;
            xVelPoints[pointIndex] = xVel;
            yVelPoints[pointIndex] = yVel;
            pointIndex++;
            repaint();
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

}
