package com.cburch.logisim.plugin3d.components;

import com.cburch.logisim.plugin3d.core.Object3D;
import com.cburch.logisim.plugin3d.core.SceneManager;
import com.cburch.logisim.comp.Component;
import java.util.Map;
import java.util.HashMap;

/**
 * Fabrique pour créer les objets 3D depuis les composants Logisim.
 */
public class ComponentFactory3D {
    
    private final SceneManager sceneManager;
    private final Map<String, Object3D> prototypes;
    
    public ComponentFactory3D(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.prototypes = new HashMap<>();
        loadPrototypes();
    }
    
    /**
     * Charge les prototypes de composants.
     */
    private void loadPrototypes() {
        // Créer des prototypes de base pour chaque type
        prototypes.put("AND_GATE", new Object3D("prototype_and", "AND_GATE"));
        prototypes.put("OR_GATE", new Object3D("prototype_or", "OR_GATE"));
        prototypes.put("NOT_GATE", new Object3D("prototype_not", "NOT_GATE"));
        prototypes.put("XOR_GATE", new Object3D("prototype_xor", "XOR_GATE"));
        prototypes.put("NAND_GATE", new Object3D("prototype_nand", "NAND_GATE"));
        prototypes.put("NOR_GATE", new Object3D("prototype_nor", "NOR_GATE"));
        prototypes.put("BUFFER", new Object3D("prototype_buffer", "BUFFER"));
        
        prototypes.put("FLIP_FLOP_D", new Object3D("prototype_dff", "FLIP_FLOP_D"));
        prototypes.put("FLIP_FLOP_JK", new Object3D("prototype_jkff", "FLIP_FLOP_JK"));
        prototypes.put("FLIP_FLOP_T", new Object3D("prototype_tff", "FLIP_FLOP_T"));
        
        prototypes.put("RAM", new Object3D("prototype_ram", "RAM"));
        prototypes.put("ROM", new Object3D("prototype_rom", "ROM"));
        
        prototypes.put("PULL_RESISTOR", new Object3D("prototype_pull", "PULL_RESISTOR"));
        prototypes.put("CLOCK", new Object3D("prototype_clock", "CLOCK"));
        prototypes.put("PIN", new Object3D("prototype_pin", "PIN"));
    }
    
    /**
     * Crée un objet 3D depuis un composant Logisim.
     */
    public Object3D createFromComponent(Component component) {
        if (component == null) return null;
        
        String type = getComponentType(component);
        Object3D prototype = prototypes.get(type);
        
        if (prototype == null) {
            // Type non reconnu, utiliser un prototype générique
            prototype = new Object3D("prototype_unknown", "UNKNOWN");
        }
        
        // Cloner le prototype
        Object3D obj = new Object3D(
            component.getId(),
            type
        );
        
        // Configurer la position depuis le composant Logisim
        var loc = component.getLocation();
        obj.setPosition(loc.getX(), loc.getY(), 0);
        
        // Configurer les données du composant
        obj.setComponentData(component);
        
        return obj;
    }
    
    /**
     * Retourne le type de composant Logisim.
     */
    private String getComponentType(Component component) {
        // Extraire le type depuis la factory du composant
        String factoryName = component.getFactory().getClass().getSimpleName();
        
        // Mapper vers les types 3D
        switch (factoryName) {
            case "AndGate":
                return "AND_GATE";
            case "OrGate":
                return "OR_GATE";
            case "NotGate":
                return "NOT_GATE";
            case "XorGate":
                return "XOR_GATE";
            case "NandGate":
                return "NAND_GATE";
            case "NorGate":
                return "NOR_GATE";
            case "Buffer":
                return "BUFFER";
            case "DFlipFlop":
                return "FLIP_FLOP_D";
            case "JKFlipFlop":
                return "FLIP_FLOP_JK";
            case "TFlipFlop":
                return "FLIP_FLOP_T";
            case "Ram":
                return "RAM";
            case "Rom":
                return "ROM";
            case "PullResistor":
                return "PULL_RESISTOR";
            case "Clock":
                return "CLOCK";
            case "Pin":
                return "PIN";
            default:
                return "UNKNOWN";
        }
    }
    
    /**
     * Crée un objet 3D pour un cable.
     */
    public Object3D createWire(String wireId, float[] start, float[] end) {
        Object3D wire = new Object3D(wireId, "WIRE");
        wire.setPosition(start[0], start[1], start[2]);
        
        // Calculer la direction
        float[] mid = new float[] {
            (start[0] + end[0]) / 2,
            (start[1] + end[1]) / 2,
            (start[2] + end[2]) / 2
        };
        
        float dx = end[0] - start[0];
        float dy = end[1] - start[1];
        float dz = end[2] - start[2];
        float length = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        
        wire.setScale(length, 0.1f, 0.1f);
        
        return wire;
    }
    
    /**
     * Enregistre un nouveau type de prototype.
     */
    public void registerPrototype(String type, Object3D prototype) {
        prototypes.put(type, prototype);
    }
}
