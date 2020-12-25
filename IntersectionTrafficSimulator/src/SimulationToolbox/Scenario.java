package SimulationToolbox;

import Animation.Animatable;
import Intersection.Intersection;

import java.util.ArrayList;

public abstract class Scenario {

    private ArrayList<Simulatable> simulatables;
    private ArrayList<Animatable> animatables;
    private ArrayList<Object> components;
    private Intersection intersection;

    public Scenario() {
        simulatables = new ArrayList<>();
        animatables = new ArrayList<>();
        components = new ArrayList<>();
    }

    public abstract void buildScenario();

    public void addComponent(Object o){
        if (o == null) return;
        components.add(o);
        if (o instanceof Simulatable){
            simulatables.add((Simulatable)o);
        }
        if (o instanceof Animatable){
            animatables.add((Animatable)o);
        }
        if (o instanceof Intersection){
            this.intersection = (Intersection)o;
        }
    }

    public ArrayList<Object> getComponents() {
        return components;
    }

    ArrayList<Simulatable> getSimulatables() {
        return simulatables;
    }

    ArrayList<Animatable> getAnimatables() {
        return animatables;
    }

    public Intersection getIntersection() {
        return intersection;
    }
}
