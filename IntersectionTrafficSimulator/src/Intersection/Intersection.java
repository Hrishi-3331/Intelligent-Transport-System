package Intersection;

import Animation.Animatable;
import Road.Road;
import SignalController.SignalController;
import TrafficSignal.TrafficSignal;
import res.SimulationGraphicConfig;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;

public class Intersection implements Animatable{

    private String id;
    private ArrayList<Road> roads;
    private ArrayList<TrafficSignal> signals;
    private SignalController signalController;
    private int posX;
    private int posY;

    public Intersection(String id) {
        this.id = id;
        roads = new ArrayList<Road>();
        signals = new ArrayList<TrafficSignal>();
        this.posX = SimulationGraphicConfig.VERTICAL_ROAD_X_POS;
        this.posY = SimulationGraphicConfig.HORIZONTAL_ROAD_Y_POS;
    }

    public void addRoad(Road road){
        this.roads.add(road);
    }

    public void addSignal(TrafficSignal signal){
        this.signals.add(signal);
    }

    public SignalController getSignalController() {
        return signalController;
    }

    public void setSignalController(SignalController signalController) {
        signalController.setIntersection(this);
        this.signalController = signalController;
    }

    public String getId(){
        return this.id;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    public ArrayList<TrafficSignal> getSignals() {
        return signals;
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D canvas = (Graphics2D)graphics;
        try{
            Image intersection_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.INTERSECTION));
            canvas.drawImage(intersection_image, posX, posY, SimulationGraphicConfig.ROAD_WIDTH, SimulationGraphicConfig.ROAD_WIDTH, null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
