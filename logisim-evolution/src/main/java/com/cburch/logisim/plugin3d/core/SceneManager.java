package com.cburch.logisim.plugin3d.core;

import com.cburch.logisim.plugin3d.api.SceneGraphAPI;
import com.cburch.logisim.plugin3d.api.ExportAPI;
import com.cburch.logisim.plugin3d.components.ComponentFactory3D;
import com.cburch.logisim.plugin3d.simulation.TimeTravelManager;
import com.cburch.logisim.plugin3d.simulation.SignalTracer;
import com.cburch.logisim.plugin3d.export.JSONExporter;
import com.cburch.logisim.plugin3d.export.XMLExporter;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.circuit.Circuit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire principal de la scène 3D.
 * Gère tous les objets 3D et coordonne le rendu.
 */
public class SceneManager implements SceneGraphAPI, ExportAPI {
    
    // Projet Logisim associé
    private final Project project;
    
    // Gestionnaire de caméra
    private CameraController cameraController;
    
    // Gestionnaire LOD
    private LODManager lodManager;
    
    // Fabrique de composants
    private ComponentFactory3D componentFactory;
    
    // Objets de la scène
    private final Map<String, Object3D> objects = new ConcurrentHashMap<>();
    
    // Time Travel
    private TimeTravelManager timeTravelManager;
    
    // Traceur de signaux
    private SignalTracer signalTracer;
    
    // Exporteurs
    private JSONExporter jsonExporter;
    private XMLExporter xmlExporter;
    
    // Paramètres d'affichage
    private boolean showLabels = true;
    private String cameraMode = "OVERVIEW";
    private boolean needsRender = false;
    
    public SceneManager(Project project) {
        this.project = project;
        this.cameraController = new CameraController();
        this.lodManager = new LODManager();
        this.componentFactory = new ComponentFactory3D(this);
        this.timeTravelManager = new TimeTravelManager();
        this.signalTracer = new SignalTracer();
        this.jsonExporter = new JSONExporter(this);
        this.xmlExporter = new XMLExporter(this);
    }
    
    /**
     * Met à jour la scène (appelé à chaque frame).
     */
    public void update(double deltaTime) {
        // Mettre à jour la caméra
        cameraController.update((float) deltaTime);
        
        // Mettre à jour le LOD pour chaque objet
        for (Object3D obj : objects.values()) {
            float distance = cameraController.getDistanceTo(obj.getPosition());
            LODManager.LODLevel level = lodManager.getLODLevel(distance);
            obj.setLODLevel(level);
        }
        
        // Réinitialiser le flag de rendu
        needsRender = false;
    }
    
    /**
     * Demande un rendu de la scène.
     */
    public void requestRender() {
        this.needsRender = true;
    }
    
    // ========== Implémentation SceneGraphAPI ==========
    
    @Override
    public void addObject(Object3D object) {
        if (object != null && object.getId() != null) {
            objects.put(object.getId(), object);
            needsRender = true;
        }
    }
    
    @Override
    public void removeObject(Object3D object) {
        if (object != null && object.getId() != null) {
            objects.remove(object.getId());
            needsRender = true;
        }
    }
    
    @Override
    public void updateObject(String id, Object3D object) {
        if (id != null && object != null) {
            objects.put(id, object);
            needsRender = true;
        }
    }
    
    @Override
    public Object3D getObject(String id) {
        return objects.get(id);
    }
    
    @Override
    public List<Object3D> getAllObjects() {
        return new ArrayList<>(objects.values());
    }
    
    @Override
    public void clear() {
        objects.clear();
        needsRender = true;
    }
    
    @Override
    public void updateTransform(String id, float[] matrix) {
        Object3D obj = objects.get(id);
        if (obj != null) {
            obj.setTransformMatrix(matrix);
            needsRender = true;
        }
    }
    
    @Override
    public void updateColor(String id, float r, float g, float b, float a) {
        Object3D obj = objects.get(id);
        if (obj != null) {
            obj.setColor(r, g, b, a);
            needsRender = true;
        }
    }
    
    // ========== Implémentation ExportAPI ==========
    
    @Override
    public String exportToJSON() {
        return jsonExporter.export(this);
    }
    
    @Override
    public String exportToXML() {
        return xmlExporter.export(this);
    }
    
    @Override
    public boolean saveToFile(String path, String format) {
        try {
            String content;
            if ("json".equalsIgnoreCase(format)) {
                content = exportToJSON();
            } else if ("xml".equalsIgnoreCase(format)) {
                content = exportToXML();
            } else {
                return false;
            }
            
            java.nio.file.Files.write(
                java.nio.file.Paths.get(path), 
                content.getBytes()
            );
            return true;
        } catch (Exception e) {
            System.err.println("[SceneManager] Erreur export: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadFromFile(String path) {
        // TODO: Implémenter l'import
        return false;
    }
    
    // ========== Méthodes supplémentaires ==========
    
    /**
     * Met à jour l'état d'un composant depuis la simulation.
     */
    public void updateComponentState(String componentId, int signalValue, int bitWidth) {
        Object3D obj = objects.get(componentId);
        if (obj != null) {
            obj.setSignalValue(signalValue);
            obj.setBitWidth(bitWidth);
            needsRender = true;
        }
        
        // Enregistrer dans le traceur
        signalTracer.recordSignalChange(componentId, signalValue, bitWidth, 
            System.nanoTime());
    }
    
    /**
     * Synchronise la scène avec un circuit.
     */
    public void syncWithCircuit(Circuit circuit) {
        if (circuit == null) return;
        
        // Ajouter les nouveaux composants
        for (var comp : circuit.getComponents()) {
            String id = comp.getId();
            if (!objects.containsKey(id)) {
                Object3D obj3d = componentFactory.createFromComponent(comp);
                if (obj3d != null) {
                    addObject(obj3d);
                }
            }
        }
        
        // Supprimer les composants supprimés
        Set<String> currentIds = new HashSet<>();
        for (var comp : circuit.getComponents()) {
            currentIds.add(comp.getId());
        }
        objects.keySet().removeIf(id -> !currentIds.contains(id));
    }
    
    // Getters et setters
    
    public Project getProject() {
        return project;
    }
    
    public CameraController getCameraController() {
        return cameraController;
    }
    
    public LODManager getLODManager() {
        return lodManager;
    }
    
    public ComponentFactory3D getComponentFactory() {
        return componentFactory;
    }
    
    public TimeTravelManager getTimeTravelManager() {
        return timeTravelManager;
    }
    
    public SignalTracer getSignalTracer() {
        return signalTracer;
    }
    
    public boolean isShowLabels() {
        return showLabels;
    }
    
    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }
    
    public String getCameraMode() {
        return cameraMode;
    }
    
    public void setCameraMode(String mode) {
        this.cameraMode = mode;
        cameraController.setMode(mode);
    }
    
    public boolean needsRender() {
        return needsRender;
    }
}
