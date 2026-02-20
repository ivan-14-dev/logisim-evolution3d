package com.cburch.logisim.plugin3d.api;

import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitState;

/**
 * Interface pour recevoir les événements de simulation du moteur 2D.
 * Cette interface permet au plugin 3D de synchroniser l'état 
 * de la visualisation avec la simulation logique.
 */
public interface SimulationEventReceiver {
    
    /**
     * Called when a signal changes on a component output.
     * 
     * @param componentId Unique identifier of the component
     * @param value Signal value (0, 1, or -1 for floating)
     * @param bitWidth Bit width of the signal
     */
    void onSignalChange(String componentId, int value, int bitWidth);
    
    /**
     * Called on each clock tick.
     * 
     * @param timestamp Current simulation timestamp in nanoseconds
     */
    void onClockTick(long timestamp);
    
    /**
     * Called when a circuit changes (components added/removed/modified).
     * 
     * @param event Circuit event containing change information
     */
    void onCircuitChange(CircuitEvent event);
    
    /**
     * Called when propagation is complete for this simulation step.
     * This is the optimal time to trigger a render update.
     */
    void onPropagationComplete();
    
    /**
     * Gets the current simulation state.
     * 
     * @return Current circuit state from the simulator
     */
    CircuitState getSimulationState();
    
    /**
     * Cleanup method called when the plugin is unloaded.
     */
    void cleanup();
}
