package Animation;

import SimulationToolbox.ScenarioHandler;

import javax.swing.*;

public class AnimationWindow extends JFrame {

    private AnimationPanel panel;

    public AnimationWindow(ScenarioHandler handler){
        panel = new AnimationPanel(handler);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
    }

    public void start(){
        panel.start();
        this.setVisible(true);
    }
}
