package com.cburch.logisim.plugin3d.ui;

import com.cburch.logisim.plugin3d.core.CameraController;

/**
 * Barre d'outils pour les contrôles de caméra.
 */
public class CameraToolbar {
    
    private final CameraController cameraController;
    
    public CameraToolbar(CameraController cameraController) {
        this.cameraController = cameraController;
    }
    
    /**
     * Bascule vers le mode Overview.
     */
    public void setOverviewMode() {
        cameraController.setMode(CameraController.CameraMode.OVERVIEW);
        System.out.println("[CameraToolbar] Mode: Overview");
    }
    
    /**
     * Bascule vers le mode FPS.
     */
    public void setFPSMode() {
        cameraController.setMode(CameraController.CameraMode.FPS);
        System.out.println("[CameraToolbar] Mode: FPS");
    }
    
    /**
     * Bascule vers le mode Orbit.
     */
    public void setOrbitMode() {
        cameraController.setMode(CameraController.CameraMode.ORBIT);
        System.out.println("[CameraToolbar] Mode: Orbit");
    }
    
    /**
     * Bascule vers le mode Top-Down.
     */
    public void setTopDownMode() {
        cameraController.setMode(CameraController.CameraMode.TOP_DOWN);
        System.out.println("[CameraToolbar] Mode: Top-Down");
    }
    
    /**
     * Effectue un zoom avant.
     */
    public void zoomIn() {
        cameraController.zoom(5f);
    }
    
    /**
     * Effectue un zoom arrière.
     */
    public void zoomOut() {
        cameraController.zoom(-5f);
    }
    
    /**
     * Reset la vue.
     */
    public void resetView() {
        cameraController.setMode(CameraController.CameraMode.OVERVIEW);
    }
}
