package com.cburch.logisim.plugin3d.simulation;

import java.util.*;

/**
 * Gestionnaire Time-Travel pour la simulation.
 * Permet d'annuler/retablir les états de simulation.
 */
public class TimeTravelManager {
    
    private static final int MAX_HISTORY = 10000;
    
    // Historique des états
    private final Deque<SimulationSnapshot> history = new ArrayDeque<>();
    private final Deque<SimulationSnapshot> future = new ArrayDeque<>();
    
    // Pas actuel
    private int currentStep = 0;
    private boolean isRecording = true;
    
    /**
     * Enregistre un nouvel état de simulation.
     */
    public void recordState(Map<String, Object> componentStates, 
                           Map<String, Object> wireStates) {
        if (!isRecording) return;
        
        SimulationSnapshot snapshot = new SimulationSnapshot(
            new HashMap<>(componentStates),
            new HashMap<>(wireStates),
            System.nanoTime(),
            currentStep++
        );
        
        history.push(snapshot);
        future.clear(); // Effacer le futur
        
        // Limiter la taille
        while (history.size() > MAX_HISTORY) {
            history.removeLast();
        }
    }
    
    /**
     * Annule le dernier changement.
     */
    public boolean undo() {
        if (history.size() <= 1) return false;
        
        SimulationSnapshot current = history.pop();
        future.push(current);
        
        SimulationSnapshot previous = history.peek();
        currentStep = previous.getStep();
        
        return true;
    }
    
    /**
     * Rétablit un changement annulé.
     */
    public boolean redo() {
        if (future.isEmpty()) return false;
        
        SimulationSnapshot next = future.pop();
        history.push(next);
        
        currentStep = next.getStep();
        
        return true;
    }
    
    /**
     * Saute à un pas spécifique.
     */
    public void jumpTo(int step) {
        if (step < 0 || step >= history.size()) return;
        
        // Recréer l'historique jusqu'au pas souhaité
        while (history.size() > step + 1) {
            SimulationSnapshot s = history.pop();
            future.push(s);
        }
        
        currentStep = step;
    }
    
    /**
     * Retourne le nombre de pas dans l'historique.
     */
    public int getHistorySize() {
        return history.size();
    }
    
    /**
     * Retourne le pas actuel.
     */
    public int getCurrentStep() {
        return currentStep;
    }
    
    /**
     * Active/désactive l'enregistrement.
     */
    public void setRecording(boolean recording) {
        this.isRecording = recording;
    }
    
    /**
     * Retourne l'état actuel.
     */
    public SimulationSnapshot getCurrentState() {
        return history.peek();
    }
    
    /**
     * Snapshot d'un état de simulation.
     */
    public static class SimulationSnapshot {
        private final Map<String, Object> componentStates;
        private final Map<String, Object> wireStates;
        private final long timestamp;
        private final int step;
        
        public SimulationSnapshot(Map<String, Object> compStates, 
                                  Map<String, Object> wireStates,
                                  long timestamp, int step) {
            this.componentStates = compStates;
            this.wireStates = wireStates;
            this.timestamp = timestamp;
            this.step = step;
        }
        
        public Map<String, Object> getComponentStates() {
            return componentStates;
        }
        
        public Map<String, Object> getWireStates() {
            return wireStates;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public int getStep() {
            return step;
        }
    }
}
