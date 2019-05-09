package cl.uantof;

import org.w3c.dom.Text;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class GalCrash extends Applet {

    GraphCanvas graphCanvas;
    GalImage galImage;

    @Override
    public void init() {

        setLayout(new BorderLayout());

        ActionListener touchParamsListener = actionEvent -> {
            galImage.touchParams();
        };
        ActionListener touchNStarsListener = actionEvent -> {
            graphCanvas.reset();
            galImage.touchParams();
        };

        ItemListener choseListener = itemEvent -> {
            if(itemEvent.getItem().equals("Green Centered")) {
                galImage.zoom = ((Checkbox) itemEvent.getItemSelectable()).getState();
            } else if(itemEvent.getItem().equals("Friction")) {
                galImage.friction = ((Checkbox) itemEvent.getItemSelectable()).getState();
            } else if(itemEvent.getItem().equals("Big Halos")) {
                galImage.bigHalos = ((Checkbox) itemEvent.getItemSelectable()).getState();
            }
        };
        ActionListener clickListener = actionEvent -> {
            if(actionEvent.getActionCommand().equals("Start")) {
                galImage.go(true);
            } else if(actionEvent.getActionCommand().equals("Stop")) {
                galImage.go(false);
            } else if(actionEvent.getActionCommand().equals("Reset")) {
                graphCanvas.reset();
                galImage.touchParams();
            } else {
                galImage.touchParams();
            }
        };

        JSlider galThetaSlider = new JSlider(-180, 180);
        JSlider galPhiSlider = new JSlider(-180, 180);
        JSlider compThetaSlider = new JSlider(-180, 180);
        JSlider compPhiSlider = new JSlider(-180, 180);

        Label galThetaLabel = new Label("Green θ=%d°");
        galThetaLabel.setForeground(new Color(7, 150, 76));
        Label galPhiLabel = new Label("Green φ=%d°");
        galPhiLabel.setForeground(new Color(7, 150, 76));
        Label compThetaLabel = new Label("Red θ=%d°");
        compThetaLabel.setForeground(Color.RED);
        Label compPhiLabel = new Label("Red φ=%d°");
        compPhiLabel.setForeground(Color.RED);

        galThetaSlider.addChangeListener(createSliderListener(galThetaLabel));
        galPhiSlider.addChangeListener(createSliderListener(galPhiLabel));
        compThetaSlider.addChangeListener(createSliderListener(compThetaLabel));
        compPhiSlider.addChangeListener(createSliderListener(compPhiLabel));

        TextField periField = new TextField("10.5");
        TextField cMassField = new TextField("1.0");
        periField.addActionListener(touchParamsListener);
        cMassField.addActionListener(touchParamsListener);

        TextField nNewStars = new TextField("125");
        nNewStars.addActionListener(touchNStarsListener);

        Button start = new Button("Start");
        start.addActionListener(clickListener);
        Button stop = new Button("Stop");
        stop.addActionListener(clickListener);
        Button reset = new Button("Reset");
        reset.addActionListener(clickListener);

        Checkbox zoom = new Checkbox("Green Centered");
        zoom.addItemListener(choseListener);
        Checkbox friction = new Checkbox("Friction");
        friction.addItemListener(choseListener);
        Checkbox bigHalos = new Checkbox("Big Halos");
        bigHalos.addItemListener(choseListener);

        Panel controlPanel = new Panel(new GridLayout(10, 2));
        controlPanel.setPreferredSize(new Dimension(250, 500));

        controlPanel.add(galThetaLabel);
        controlPanel.add(galThetaSlider);
        controlPanel.add(galPhiLabel);
        controlPanel.add(galPhiSlider);
        controlPanel.add(compThetaLabel);
        controlPanel.add(compThetaSlider);
        controlPanel.add(compPhiLabel);
        controlPanel.add(compPhiSlider);

        controlPanel.add(new Label("Peri [kpc]"));
        controlPanel.add(periField);

        controlPanel.add(new Label("Red Galaxy Mass"));
        controlPanel.add(cMassField);

        controlPanel.add(new Label("Number of Stars"));
        controlPanel.add(nNewStars);

        controlPanel.add(zoom);
        controlPanel.add(start);
        controlPanel.add(friction);
        controlPanel.add(stop);
        controlPanel.add(bigHalos);
        controlPanel.add(reset);

        Panel topPanel = new Panel(new BorderLayout());

        topPanel.add(BorderLayout.WEST, controlPanel);

        add(BorderLayout.CENTER, topPanel);

        graphCanvas = new GraphCanvas(1000, 100);
        add(BorderLayout.SOUTH, graphCanvas);

        File pathToFile = new File("title.jpg");
        Image title = null;
        try {
            title = ImageIO.read(pathToFile);
        } catch (Exception e) {}
        galImage = new GalImage(periField, cMassField, compThetaSlider, compPhiSlider,
                galThetaSlider, galPhiSlider, nNewStars, graphCanvas, title);
        galImage.setBackground(Color.BLACK);

        Panel panel = new Panel();
        panel.setBackground(Color.YELLOW);
        panel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                galImage.setSize(panel.getSize());
            }
        });
        panel.add(galImage);
        topPanel.add(BorderLayout.CENTER, panel);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                graphCanvas.setWidth(e.getComponent().getWidth());
                galImage.setSize(panel.getSize());
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GalCrashRemake");
        Applet app = new GalCrash();
        app.init();
        frame.add(app);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        app.start();

    }

    private ChangeListener createSliderListener(Label label) {
        String template = label.getText();
        label.setText(String.format(template, 0));
        ChangeListener touchAnglesListener = changeEvent -> {
            JSlider source = (JSlider)changeEvent.getSource();
            label.setText(String.format(template, source.getValue()));
            galImage.touchParams();
        };
        return touchAnglesListener;
    }

}
