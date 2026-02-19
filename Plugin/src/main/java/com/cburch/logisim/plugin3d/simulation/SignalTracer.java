package com.cburch.logisim.plugin3d.simulation;

import java.util.*;

/**
 * Traceur de signaux pour visualiser le flux de données.
 */
public class SignalTracer {
    
    // Historique des signaux par composant
    private final Map<String, List<SignalEvent>> signalHistory = new HashMap<>();
    
    // Signaux actuellement tracés
    private final Set<String> tracedSignals = new HashSet<>();
    
    // Configuration
    private boolean enabled = true;
    private int maxEventsPerSignal = 1000;
    
    /**
     * Démarre le tracing d'un composant.
     */
    public void startTrace(String componentId) {
        tracedSignals.add(componentId);
        signalHistory.put(componentId, new ArrayList<>());
    }
    
    /**
     * Arrête le tracing d'un composant.
     */
    public void stopTrace(String componentId) {
        tracedSignals.remove(componentId);
    }
    
    /**
     * Enregistre un changement de signal.
     */
    public void recordSignalChange(String componentId, int value, 
                                   int bitWidth, long timestamp) {
        if (!enabled) return;
        
        if (!tracedSignals.contains(componentId)) return;
        
        List<SignalEvent> events = signalHistory.computeIfAbsent(
            componentId, k -> new ArrayList<>());
        
        int oldValue = events.isEmpty() ? 0 : events.get(events.size() - 1).newValue;
        
        SignalEvent event = new SignalEvent(oldValue, value, timestamp);
        events.add(event);
        
        while (events.size() > maxEventsPerSignal) {
            events.remove(0);
        }
    }
    
    /**
     * Retourne l'historique des signaux d'un composant.
     */
    public List<SignalEvent> getTrace(String componentId) {
        return signalHistory.getOrDefault(componentId, Collections.emptyList());
    }
    
    /**
     * Retourne tous les signaux tracés.
     */
    public Set<String> getTracedSignals() {
        return new HashSet<>(tracedSignals);
    }
    
    /**
     * Efface le tracing d'un composant.
     */
    public void clearTrace(String componentId) {
        signalHistory.remove(componentId);
    }
    
    /**
     * Efface tout le tracing.
     */
    public void clearAll() {
        signalHistory.clear();
        tracedSignals.clear();
    }
    
    /**
     * Active/désactive le tracing.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Événement de changement de signal.
     */
    public static class SignalEvent {
        public final int oldValue;
        public final int newValue;
        public final long timestamp;
        
        public SignalEvent(int oldValue, int newValue, long timestamp) {
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.timestamp = timestamp;
        }
        
        public boolean hasChanged() {
            return oldValue != newValue;
        }
    }
}
