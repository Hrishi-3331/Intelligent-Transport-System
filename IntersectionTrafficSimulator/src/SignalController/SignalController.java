package SignalController;

import Intersection.Intersection;
import SimulationToolbox.Simulatable;

public abstract class SignalController implements Simulatable {

    protected Intersection intersection;

    public SignalController() {

    }

    public void setIntersection(Intersection intersection){
        this.intersection = intersection;
    }

}
