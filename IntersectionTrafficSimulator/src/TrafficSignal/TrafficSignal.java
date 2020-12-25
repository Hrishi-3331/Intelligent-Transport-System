package TrafficSignal;

import Animation.Animatable;
import Road.Road;
import SignalController.SignalController;
import res.SimulationGraphicConfig;
import javax.imageio.ImageIO;
import java.awt.*;

public class TrafficSignal implements Animatable {

    public static final int STATE_GREEN = 0;
    public static final int STATE_RED = 1;
    public static final int STATE_YELLOW = 2;

    private int stateNeedle;
    private final int[] states = {STATE_RED, STATE_YELLOW, STATE_GREEN, STATE_YELLOW};
    private int state;
    private Road road;
    private int posX;
    private int posY;
    private int restrictorPos;
    private SignalController controller;

    public TrafficSignal() {
        this.state = STATE_RED;
        stateNeedle = 0;
    }

    public TrafficSignal(Road road){
        this();
        this.setRoad(road);
    }

    public void setController(SignalController controller){
        this.controller = controller;
    }

    public int getSignalState(){
        return this.state;
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D canvas = (Graphics2D) graphics;
        Image signal_image;
        try{
            switch (this.getSignalState()){
                case TrafficSignal.STATE_GREEN:
                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_GREEN));
                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
                    break;

                case TrafficSignal.STATE_RED:
                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_RED));
                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
                    break;

                case TrafficSignal.STATE_YELLOW:
                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_YELLOW));
                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
                    break;
            }
        }
        catch (Exception e){
            canvas.fillOval(this.posX, this.posY, 20, 20);
        }
    }

    public void setRoad(Road road){
        this.road = road;
        road.setTrafficSignal(this);
        switch (road.getDirection()){
            case Road.DIRECTION_NORTH:
                this.posX = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_X;
                this.posY = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_Y;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_NORTH_RESTRICTOR;
                break;

            case Road.DIRECTION_SOUTH:
                this.posX = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_X;
                this.posY = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_Y;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_SOUTH_RESTRICTOR;
                break;

            case Road.DIRECTION_EAST:
                this.posX = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_X;
                this.posY = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_Y;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_EAST_RESTRICTOR;
                break;

            case Road.DIRECTION_WEST:
                this.posX = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_X;
                this.posY = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_Y;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_WEST_RESTRICTOR;
                break;
        }
    }

    public Road getRoad(){
        return this.road;
    }

    public int getRestrictorPos() {
        return restrictorPos;
    }

    public SignalController getController() {
        return controller;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setRestrictorPos(int restrictorPos) {
        this.restrictorPos = restrictorPos;
    }

    public void toggleState(){
        this.stateNeedle = (stateNeedle + 1)%4;
        this.state = states[stateNeedle];
    }

    public void setGreen(){
        this.state = TrafficSignal.STATE_GREEN;
        this.stateNeedle = 2;
    }

    public void setRed(){
        this.state = TrafficSignal.STATE_RED;
        this.stateNeedle = 0;
    }
}
