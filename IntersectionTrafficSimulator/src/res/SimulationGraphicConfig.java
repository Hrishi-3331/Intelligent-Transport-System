package res;

import java.awt.*;

public final class SimulationGraphicConfig {

    private static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

    public static final String CAR_NORTH = "/res/VehicleGraphics/red_car_north.png";
    public static final String CAR_SOUTH = "/res/VehicleGraphics/red_car_south.png";
    public static final String CAR_EAST = "/res/VehicleGraphics/red_car_east.png";
    public static final String CAR_WEST = "/res/VehicleGraphics/red_car_west.png";
    public static final String ROAD_HORIZONTAL = "/res/RoadGraphics/road_horizontal.jpg";
    public static final String ROAD_VERTICAL = "/res/RoadGraphics/road_vertical.jpg";
    public static final String SIGNAL_RED = "/res/SignalGraphics/signal_red.png";
    public static final String SIGNAL_GREEN = "/res/SignalGraphics/signal_green.png";
    public static final String SIGNAL_YELLOW = "/res/SignalGraphics/signal_yellow.png";
    public static final String INTERSECTION = "/res/IntersectionGraphics/intersection.png";
    public static final int VEHICLE_LENGTH = 80;
    public static final int VEHICLE_WIDTH = 80;
    public static final int VEHICLE_HEIGHT = 60;
    public static final int ROAD_WIDTH = 160;
    public static final int ROAD_VERTICAL_LENGTH = (int) dimension.getHeight();
    public static final int ROAD_HORIZONTAL_LENGTH = (int) dimension.getWidth();
    public static final int VERTICAL_ROAD_X_POS = (int) (dimension.getWidth()/2) - (ROAD_WIDTH/2);
    public static final int HORIZONTAL_ROAD_Y_POS = (int) (dimension.getHeight()/2) - (ROAD_WIDTH/2);
    public static final int LOW_TRAFFIC_THRESHOLD_INTERVAL = 8;
    public static final int MODERATE_TRAFFIC_THRESHOLD_INTERVAL = 6;
    public static final int HIGH_TRAFFIC_THRESHOLD_INTERVAL = 4;
    public static final int VEHICLE_NORTH_POS_X = VERTICAL_ROAD_X_POS + 15;
    public static final int VEHICLE_NORTH_POS_Y = (int) dimension.getHeight();
    public static final int VEHICLE_SOUTH_POS_X = VERTICAL_ROAD_X_POS + 15;
    public static final int VEHICLE_SOUTH_POS_Y = 0;
    public static final int VEHICLE_EAST_POS_X = 0;
    public static final int VEHICLE_EAST_POS_Y = HORIZONTAL_ROAD_Y_POS + 15;
    public static final int VEHICLE_WEST_POS_X = (int) dimension.getWidth();
    public static final int VEHICLE_WEST_POS_Y = HORIZONTAL_ROAD_Y_POS + 15;
    public static final int BOUNDARY_X = (int) dimension .getWidth();
    public static final int BOUNDARY_Y = (int) dimension.getHeight();

    public static final int SIGNAL_WIDTH = 70;
    public static final int SIGNAL_HEIGHT = 120;

    public static final int SIGNAL_VERTICAL_POS_X = VERTICAL_ROAD_X_POS + ROAD_WIDTH + 15;
    public static final int SIGNAL_VERTICAL_POS_Y = HORIZONTAL_ROAD_Y_POS - SIGNAL_HEIGHT - 15;

    public static final int SIGNAL_HORIZONTAL_POS_X = VERTICAL_ROAD_X_POS - 15 - SIGNAL_WIDTH;
    public static final int SIGNAL_HORIZONTAL_POS_Y = HORIZONTAL_ROAD_Y_POS + ROAD_WIDTH + 15;

    public static final int SIGNAL_NORTH_RESTRICTOR = HORIZONTAL_ROAD_Y_POS + ROAD_WIDTH;
    public static final int SIGNAL_SOUTH_RESTRICTOR = HORIZONTAL_ROAD_Y_POS;
    public static final int SIGNAL_EAST_RESTRICTOR = VERTICAL_ROAD_X_POS;
    public static final int SIGNAL_WEST_RESTRICTOR = VERTICAL_ROAD_X_POS + ROAD_WIDTH;

    private SimulationGraphicConfig(){

    }
}
