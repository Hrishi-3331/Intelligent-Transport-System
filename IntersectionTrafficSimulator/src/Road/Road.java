package Road;

import Animation.Animatable;
import SimulationToolbox.Scenario;
import TrafficSignal.TrafficSignal;
import Vehicle.Vehicle;
import res.SimulationGraphicConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public abstract class Road implements Animatable {

    public static final int DIRECTION_NORTH = 0;
    public static final int DIRECTION_SOUTH = 1;
    public static final int DIRECTION_EAST = 2;
    public static final int DIRECTION_WEST = 3;
    public static final int INTENSITY_LOW = 0;
    public static final int INTENSITY_MODERATE = 1;
    public static final int INTENSITY_HIGH = 2;

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    protected String id;
    protected int orientation;
    protected int direction;
    protected int trafficIntensity;
    protected int posX;
    protected int posY;
    protected boolean lane;
    protected TrafficSignal trafficSignal;

    protected int[][] roadGrid;

    protected ArrayList<Vehicle> vehicles;
    protected HashMap<Integer, Integer> road_data;

    public Road(String id, int direction) {
        this.direction = direction;
        this.id = id;
        if (direction == Road.DIRECTION_NORTH | direction == Road.DIRECTION_SOUTH) {
            orientation = Road.ORIENTATION_VERTICAL;
            posY = 0;
            posX = SimulationGraphicConfig.VERTICAL_ROAD_X_POS;
        } else if (direction == Road.DIRECTION_EAST | direction == Road.DIRECTION_WEST) {
            orientation = Road.ORIENTATION_HORIZONTAL;
            posX = 0;
            posY = SimulationGraphicConfig.HORIZONTAL_ROAD_Y_POS;
        }
        roadGrid = new int[SimulationGraphicConfig.BOUNDARY_X*2][SimulationGraphicConfig.BOUNDARY_Y*2];
        this.trafficIntensity = Road.INTENSITY_MODERATE;
        vehicles = new ArrayList<>();
        lane = false;
        road_data = new HashMap<>();
    }

    public String getId(){
        return this.id;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getDirection() {
        return direction;
    }

    public void addVehicle(Vehicle vehicle){
        this.vehicles.add(vehicle);
    }

    public int getTrafficIntensity() {
        return trafficIntensity;
    }

    public void setTrafficIntensity(int trafficIntensity) {
        this.trafficIntensity = trafficIntensity;
    }

    public void check(Vehicle vehicle){
        if (this.trafficSignal != null){
            TrafficSignal signal = this.trafficSignal;
            if (vehicle.getMobilePos() == signal.getRestrictorPos()){
                if (signal.getSignalState() != TrafficSignal.STATE_GREEN && vehicle.getRunState() == Vehicle.STATE_RUNNING){
                    vehicle.waitUntilGreen();
                    roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 1;
                    return;
                }
                else if (signal.getSignalState() == TrafficSignal.STATE_GREEN && vehicle.getRunState() == Vehicle.STATE_WAITING){
                    vehicle.goUntilRed();
                    roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 0;
                    return;
                }
                return;
            }

            if (vehicle.getPosX() < 0 || vehicle.getPosY() < 0) return;
            if (roadGrid[vehicle.getPosX()][vehicle.getPosY()] == 1){
                vehicle.waitUntilGreen();
                roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 1;
            }
            else {
                vehicle.goUntilRed();
                roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 0;
            }
        }
    }

    public void setTrafficSignal(TrafficSignal trafficSignal) {
        this.trafficSignal = trafficSignal;
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D canvas = (Graphics2D) graphics;
        Image roadImage = null;
        try{
            switch (this.getOrientation()){
                case Road.ORIENTATION_HORIZONTAL:
                    roadImage = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.ROAD_HORIZONTAL));
                    canvas.drawImage(roadImage, posX, posY, SimulationGraphicConfig.ROAD_HORIZONTAL_LENGTH, SimulationGraphicConfig.ROAD_WIDTH, null);
                    break;

                case Road.ORIENTATION_VERTICAL:
                    roadImage = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.ROAD_VERTICAL));
                    canvas.drawImage(roadImage, posX, posY, SimulationGraphicConfig.ROAD_WIDTH, SimulationGraphicConfig.ROAD_VERTICAL_LENGTH, null);
                    break;
            }
        }
        catch (Exception e){
            switch (this.getOrientation()){
                case Road.ORIENTATION_HORIZONTAL:
                    canvas.drawRect(posX, posY, SimulationGraphicConfig.ROAD_HORIZONTAL_LENGTH, SimulationGraphicConfig.ROAD_WIDTH);
                    break;

                case Road.ORIENTATION_VERTICAL:
                    canvas.drawRect(posX, posY, SimulationGraphicConfig.ROAD_WIDTH, SimulationGraphicConfig.ROAD_VERTICAL_LENGTH);
                    break;
            }
        }
    }

    public int getWaitingCount(){
        int count = 0;
        for (Vehicle vehicle : vehicles){
            if (vehicle.getRunState() == Vehicle.STATE_WAITING) count++;
        }
        return count;
    }

    public boolean newVehicle(int flag, Scenario scenario) {
        if (flag > getTrafficThreshold()){
            boolean add = new Random().nextBoolean();
            if (add){
                Vehicle vehicle = new Vehicle(UUID.randomUUID().toString(), this);
                addVehicle(vehicle);
                switch (vehicle.getFacing()){
                    case Road.DIRECTION_EAST:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_EAST_POS_X);
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_EAST_POS_Y + getLaneOffset());
                        break;

                    case Road.DIRECTION_WEST:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_WEST_POS_X);
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_WEST_POS_Y + getLaneOffset());
                        break;

                    case Road.DIRECTION_SOUTH:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_SOUTH_POS_X + getLaneOffset());
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_SOUTH_POS_Y);
                        break;

                    case Road.DIRECTION_NORTH:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_NORTH_POS_X + getLaneOffset());
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_NORTH_POS_Y);
                        break;
                }
                scenario.addComponent(vehicle);
                this.addVehicle(vehicle);
            }
            return add;
        }
        else return false;
    }

    protected int getLaneOffset(){
        lane = !lane;
        if (lane){
            return SimulationGraphicConfig.VEHICLE_HEIGHT + 15;
        }
        else return 0;
    }

    public TrafficSignal getTrafficSignal() {
        return trafficSignal;
    }

    private int getTrafficThreshold(){
        switch (this.getTrafficIntensity()){
            case Road.INTENSITY_LOW:
                return SimulationGraphicConfig.LOW_TRAFFIC_THRESHOLD_INTERVAL;

            case Road.INTENSITY_MODERATE:
                return SimulationGraphicConfig.MODERATE_TRAFFIC_THRESHOLD_INTERVAL;

            case Road.INTENSITY_HIGH:
                return SimulationGraphicConfig.HIGH_TRAFFIC_THRESHOLD_INTERVAL;
        }
        return 3;
    }
}
