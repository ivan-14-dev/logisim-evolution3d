package com.cburch.logisim.plugin3d.core;

/**
 * Contrôleur de caméra multi-mode pour la navigation 3D.
 * Supporte les modes: Overview, FPS, Focus, Orbit, Top-Down
 */
public class CameraController {
    
    /**
     * Modes de caméra disponibles.
     */
    public enum CameraMode {
        OVERVIEW,   // Vue d'ensemble du circuit
        FPS,       // Navigation première personne (WASD)
        FOCUS,     // Zoom sur un composant
        ORBIT,     // Rotation autour d'un point
        TOP_DOWN   // Vue de dessus (2D traditionnelle)
    }
    
    // Mode actuel
    private CameraMode currentMode = CameraMode.OVERVIEW;
    
    // Position et cible de la caméra
    private float[] position = {0, 100, 100};
    private float[] target = {0, 0, 0};
    private float[] up = {0, 1, 0};
    
    // Paramètres
    private float fov = 60f;           // Field of view
    private float near = 0.1f;         // Near plane
    private float far = 1000f;         // Far plane
    
    // Pour mode orbit
    private float orbitDistance = 100f;
    private float orbitAngleX = 0f;
    private float orbitAngleY = 45f;
    
    // Pour mode FPS
    private float moveSpeed = 10f;
    private float lookSpeed = 0.1f;
    private float yaw = 0f;
    private float pitch = 0f;
    
    // Pour Smooth
    private boolean smoothTransition = true;
    private float[] targetPosition;
    private float[] targetLookAt;
    
    public CameraController() {
        targetPosition = position.clone();
        targetLookAt = target.clone();
    }
    
    /**
     * Change le mode de caméra.
     */
    public void setMode(String mode) {
        switch (mode.toUpperCase()) {
            case "OVERVIEW":
                setMode(CameraMode.OVERVIEW);
                break;
            case "FPS":
                setMode(CameraMode.FPS);
                break;
            case "FOCUS":
                setMode(CameraMode.FOCUS);
                break;
            case "ORBIT":
                setMode(CameraMode.ORBIT);
                break;
            case "TOP_DOWN":
                setMode(CameraMode.TOP_DOWN);
                break;
        }
    }
    
    public void setMode(CameraMode mode) {
        this.currentMode = mode;
        
        switch (mode) {
            case OVERVIEW:
                position = new float[] {0, 100, 100};
                target = new float[] {0, 0, 0};
                break;
            case TOP_DOWN:
                position = new float[] {0, 200, 0};
                target = new float[] {0, 0, 0};
                break;
            case FPS:
                position = new float[] {0, 5, 0};
                target = new float[] {0, 0, -1};
                yaw = 0;
                pitch = 0;
                break;
            case ORBIT:
                updateOrbitPosition();
                break;
        }
    }
    
    /**
     * Focus sur un composant spécifique.
     */
    public void focusOn(Object3D component) {
        if (component != null) {
            float[] compPos = component.getPosition();
            target = compPos.clone();
            orbitDistance = 20f;
            currentMode = CameraMode.ORBIT;
            updateOrbitPosition();
        }
    }
    
    /**
     * Met à jour la caméra (appelé à chaque frame).
     */
    public void update(float deltaTime) {
        if (smoothTransition) {
            // Interpolation Smooth
            position = lerp(position, targetPosition, deltaTime * 5f);
            target = lerp(target, targetLookAt, deltaTime * 5f);
        }
        
        switch (currentMode) {
            case ORBIT:
                updateOrbitPosition();
                break;
            case FPS:
                // Les entrées clavier gèrent le mouvement
                break;
        }
    }
    
    /**
     * Met à jour la position orbitale.
     */
    private void updateOrbitPosition() {
        float radX = (float) Math.toRadians(orbitAngleX);
        float radY = (float) Math.toRadians(orbitAngleY);
        
        position[0] = target[0] + orbitDistance * (float) Math.sin(radX) * (float) Math.cos(radY);
        position[1] = target[1] + orbitDistance * (float) Math.sin(radY);
        position[2] = target[2] + orbitDistance * (float) Math.cos(radX) * (float) Math.cos(radY);
    }
    
    /**
     * Rotation orbitale.
     */
    public void orbit(float deltaX, float deltaY) {
        orbitAngleX += deltaX * 0.5f;
        orbitAngleY = Math.max(-89f, Math.min(89f, orbitAngleY + deltaY * 0.5f));
        updateOrbitPosition();
    }
    
    /**
     * Zoom.
     */
    public void zoom(float delta) {
        orbitDistance = Math.max(5f, Math.min(500f, orbitDistance - delta));
        updateOrbitPosition();
    }
    
    /**
     * Déplacement FPS.
     */
    public void moveForward(float amount) {
        float[] forward = getForwardVector();
        targetPosition[0] += forward[0] * amount * moveSpeed;
        targetPosition[1] += forward[1] * amount * moveSpeed;
        targetPosition[2] += forward[2] * amount * moveSpeed;
    }
    
    public void moveRight(float amount) {
        float[] right = getRightVector();
        targetPosition[0] += right[0] * amount * moveSpeed;
        targetPosition[1] += right[1] * amount * moveSpeed;
        targetPosition[2] += right[2] * amount * moveSpeed;
    }
    
    public void moveUp(float amount) {
        targetPosition[1] += amount * moveSpeed;
    }
    
    /**
     * Rotation FPS.
     */
    public void look(float deltaX, float deltaY) {
        yaw -= deltaX * lookSpeed;
        pitch -= deltaY * lookSpeed;
        pitch = Math.max(-89f, Math.min(89f, pitch));
        
        // Calculer la cible basée sur yaw et pitch
        float[] forward = getForwardVector();
        targetLookAt[0] = targetPosition[0] + forward[0];
        targetLookAt[1] = targetPosition[1] + forward[1];
        targetLookAt[2] = targetPosition[2] + forward[2];
    }
    
    /**
     * Retourne la distance de la caméra à un point.
     */
    public float getDistanceTo(float[] point) {
        float dx = position[0] - point[0];
        float dy = position[1] - point[1];
        float dz = position[2] - point[2];
        return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * Retourne le vecteur avant de la caméra.
     */
    private float[] getForwardVector() {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);
        
        return new float[] {
            (float) (Math.sin(yawRad) * Math.cos(pitchRad)),
            (float) Math.sin(pitchRad),
            (float) (-Math.cos(yawRad) * Math.cos(pitchRad))
        };
    }
    
    /**
     * Retourne le vecteur droit de la caméra.
     */
    private float[] getRightVector() {
        float yawRad = (float) Math.toRadians(yaw);
        
        return new float[] {
            (float) Math.cos(yawRad),
            0,
            (float) Math.sin(yawRad)
        };
    }
    
    /**
     * Interpolation linéaire.
     */
    private float[] lerp(float[] a, float[] b, float t) {
        float[] result = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + (b[i] - a[i]) * Math.min(1f, t);
        }
        return result;
    }
    
    // Getters
    
    public CameraMode getCurrentMode() {
        return currentMode;
    }
    
    public float[] getPosition() {
        return position;
    }
    
    public float[] getTarget() {
        return target;
    }
    
    public float getFov() {
        return fov;
    }
    
    public float getNear() {
        return near;
    }
    
    public float getFar() {
        return far;
    }
}
