package com.cburch.logisim.plugin3d.rendering;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.app.state.AbstractAppState;

/**
 * Contrôleur de caméra orbitale pour la visualisation 3D.
 * Permet de tourner autour du circuit avec la souris.
 */
public class OrbitCameraControl extends AbstractAppState {
    
    private final Camera cam;
    private final InputManager inputManager;
    
    // Cible de la caméra
    private Vector3f target = Vector3f.ZERO.clone();
    
    // Distance et angles
    private float distance = 50f;
    private float azimuth = 0f;
    private float elevation = 30f;
    
    // Sensibilités
    private float rotationSpeed = 0.5f;
    private float zoomSpeed = 2f;
    private float minDistance = 5f;
    private float maxDistance = 200f;
    private float minElevation = -89f;
    private float maxElevation = 89f;
    
    // État
    private boolean dragging = false;
    
    // Mappings
    private static final String MAPPING_ROTATE = "OrbitRotate";
    private static final String MAPPING_ZOOM = "OrbitZoom";
    
    public OrbitCameraControl(Camera cam, InputManager inputManager) {
        this.cam = cam;
        this.inputManager = inputManager;
        registerInputs();
        updateCamera();
    }
    
    private void registerInputs() {
        inputManager.addMapping(MAPPING_ROTATE, 
            new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(MAPPING_ZOOM, 
            new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        
        inputManager.addListener(new com.jme3.input.controls.ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals(MAPPING_ROTATE)) {
                    dragging = isPressed;
                }
            }
        }, MAPPING_ROTATE);
        
        inputManager.addListener(new com.jme3.input.controls.MouseMotionListener() {
            public void onMouseMotion(int x, int y, int dx, int dy) {
                if (dragging) {
                    azimuth -= dx * rotationSpeed;
                    elevation += dy * rotationSpeed;
                    elevation = Math.max(minElevation, Math.min(maxElevation, elevation));
                    updateCamera();
                }
            }
        }, MAPPING_ZOOM);
    }
    
    private void updateCamera() {
        float azimuthRad = (float) Math.toRadians(azimuth);
        float elevationRad = (float) Math.toRadians(elevation);
        
        float x = target.x + distance * (float) (Math.cos(elevationRad) * Math.sin(azimuthRad));
        float y = target.y + distance * (float) Math.sin(elevationRad);
        float z = target.z + distance * (float) (Math.cos(elevationRad) * Math.cos(azimuthRad));
        
        cam.setLocation(new Vector3f(x, y, z));
        cam.lookAt(target, Vector3f.UNIT_Y);
    }
    
    public void zoom(float delta) {
        distance += delta * zoomSpeed;
        distance = Math.max(minDistance, Math.min(maxDistance, distance));
        updateCamera();
    }
    
    public void focusOn(Vector3f point) {
        target.set(point);
        updateCamera();
    }
    
    public void reset() {
        target.set(0, 0, 0);
        distance = 50f;
        azimuth = 0f;
        elevation = 30f;
        updateCamera();
    }
    
    @Override
    public void cleanup() {
        inputManager.deleteMapping(MAPPING_ROTATE);
        inputManager.deleteMapping(MAPPING_ZOOM);
        super.cleanup();
    }
}
