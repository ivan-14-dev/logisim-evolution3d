package com.cburch.logisim.plugin3d;

import com.cburch.logisim.plugin3d.api.SimulationEventReceiver;
import com.cburch.logisim.plugin3d.core.SceneManager;
import com.cburch.logisim.plugin3d.ui.View3DWindow;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.proj.Project;

/**
 * Point d'entrée principal du Plugin 3D pour Logisim Evolution.
 * Ce plugin ajoute une visualisation 3D des circuits logiques.
 */
public class Plugin3D implements Plugin {
    
    private SceneManager sceneManager;
    private View3DWindow view3DWindow;
    private SimulationEventReceiver eventReceiver;
    private boolean enabled = false;
    private Project currentProject;
    
    public Plugin3D() {
        // Constructeur par défaut
    }
    
    @Override
    public String getName() {
        return "Logisim 3D View";
    }
    
    @Override
    public void load(Project project) {
        this.currentProject = project;
        
        // Initialiser le gestionnaire de scène
        this.sceneManager = new SceneManager(project);
        
        // Créer le récepteur d'événements de simulation
        this.eventReceiver = new SimulationEventReceiver(sceneManager);
        
        // S'abonner aux événements du simulateur
        project.getSimulator().addListener(eventReceiver);
        
        // Créer la fenêtre 3D
        this.view3DWindow = new View3DWindow(sceneManager);
        
        System.out.println("[Plugin3D] Chargé avec succès");
    }
    
    @Override
    public void unload() {
        if (view3DWindow != null) {
            view3DWindow.close();
        }
        
        if (eventReceiver != null && currentProject != null) {
            currentProject.getSimulator().removeListener(eventReceiver);
            eventReceiver.cleanup();
        }
        
        enabled = false;
        System.out.println("[Plugin3D] Déchargé");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        if (enabled && view3DWindow != null) {
            view3DWindow.show();
        } else if (view3DWindow != null) {
            view3DWindow.hide();
        }
    }
    
    /**
     * Active ou désactive le rendu des noms de composants
     */
    public void setShowLabels(boolean show) {
        if (sceneManager != null) {
            sceneManager.setShowLabels(show);
        }
    }
    
    /**
     * Change le mode de caméra
     */
    public void setCameraMode(String mode) {
        if (sceneManager != null) {
            sceneManager.setCameraMode(mode);
        }
    }
    
    /**
     * Exporte la scène actuelle en JSON
     */
    public String exportJSON() {
        if (sceneManager != null) {
            return sceneManager.exportToJSON();
        }
        return null;
    }
    
    /**
     * Exporte la scène actuelle en XML
     */
    public String exportXML() {
        if (sceneManager != null) {
            return sceneManager.exportToXML();
        }
        return null;
    }
}
