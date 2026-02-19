package com.cburch.logisim.plugin3d;

import com.cburch.logisim.plugin3d.api.SimulationEventReceiver;
import com.cburch.logisim.plugin3d.core.SceneManager;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.comp.Component;

/**
 * Implémentation du récepteur d'événements de simulation.
 */
public class SimulationEventReceiverImpl implements SimulationEventReceiver {
    
    private final SceneManager sceneManager;
    private Simulator simulator;
    private CircuitState currentState;
    
    public SimulationEventReceiverImpl(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Configure le simulateur source.
     */
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }
    
    @Override
    public void onSignalChange(String componentId, int value, int bitWidth) {
        // Mettre à jour l'état du composant 3D
        sceneManager.updateComponentState(componentId, value, bitWidth);
    }
    
    @Override
    public void onClockTick(long timestamp) {
        // Propagation des signaux d'horloge
        System.out.println("[SimulationEvent] Clock tick: " + timestamp);
    }
    
    @Override
    public void onCircuitChange(CircuitEvent event) {
        // Synchroniser la scène avec les changements du circuit
        if (event.getCircuit() != null) {
            sceneManager.syncWithCircuit(event.getCircuit());
        }
    }
    
    @Override
    public void onPropagationComplete() {
        // Demander un rendu
        sceneManager.requestRender();
    }
    
    @Override
    public CircuitState getSimulationState() {
        if (simulator != null) {
            return simulator.getCircuitState();
        }
        return currentState;
    }
    
    @Override
    public void cleanup() {
        // Nettoyer les ressources
        simulator = null;
        currentState = null;
    }
    
    /**
     * Retourne le gestionnaire de scène.
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
