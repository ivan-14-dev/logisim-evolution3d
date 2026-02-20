package com.cburch.logisim.plugin3d.ui;

import com.cburch.logisim.plugin3d.core.SceneManager;
import com.cburch.logisim.plugin3d.core.CameraController;

/**
 * Fenêtre de vue 3D pour le plugin.
 * Note: Dans une implémentation réelle, étendrait JFrame ou Panel JMonkey
 */
public class View3DWindow {
    
    private final SceneManager sceneManager;
    private boolean visible = false;
    private boolean initialized = false;
    
    // Panneau de contrôles
    private CameraToolbar toolbar;
    
    public View3DWindow(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Affiche la fenêtre.
     */
    public void show() {
        if (!initialized) {
            initialize();
        }
        visible = true;
        System.out.println("[View3D] Fenêtre affichée");
    }
    
    /**
     * Cache la fenêtre.
     */
    public void hide() {
        visible = false;
        System.out.println("[View3D] Fenêtre cachée");
    }
    
    /**
     * Ferme la fenêtre.
     */
    public void close() {
        visible = false;
        initialized = false;
        System.out.println("[View3D] Fenêtre fermée");
    }
    
    /**
     * Initialise la fenêtre et le rendu 3D.
     */
    private void initialize() {
        // Dans une implémentation réelle:
        // - Créer la fenêtre JFrame
        // - Initialiser JMonkeyEngine
        // - Configurer la caméra
        // - Créer le panneau de rendu
        
        toolbar = new CameraToolbar(sceneManager.getCameraController());
        
        initialized = true;
        System.out.println("[View3D] Initialisation terminée");
    }
    
    /**
     * Met à jour le rendu.
     */
    public void update() {
        if (visible && initialized) {
            // Appeler le rendu JMonkey
            sceneManager.update(0.016); // ~60 FPS
        }
    }
    
    /**
     * Retourne si la fenêtre est visible.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Retourne le panneau de caméra.
     */
    public CameraToolbar getToolbar() {
        return toolbar;
    }
}
