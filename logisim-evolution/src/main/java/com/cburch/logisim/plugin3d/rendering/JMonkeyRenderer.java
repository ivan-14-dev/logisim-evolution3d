package com.cburch.logisim.plugin3d.rendering;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.light.DirectionalLight;
import com.jme3.light.AmbientLight;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.app.state.AppState;
import com.jme3.renderer.Camera;

/**
 * Application JMonkeyEngine pour la visualisation 3D des circuits.
 * Demo standalone - peut être intégrée au plugin principal.
 */
public class JMonkeyRenderer extends SimpleApplication {
    
    // Couleurs des états de signaux
    private static final ColorRGBA COLOR_HIGH = new ColorRGBA(0, 1, 0, 1);      // Vert
    private static final ColorRGBA COLOR_LOW = new ColorRGBA(0.3f, 0.3f, 0.3f, 1);   // Gris
    private static final ColorRGBA COLOR_FLOAT = new ColorRGBA(1, 0.5f, 0, 1);   // Orange
    private static final ColorRGBA COLOR_ERROR = new ColorRGBA(1, 0, 0, 1);      // Rouge
    private static final ColorRGBA COLOR_PCB = new ColorRGBA(0.23f, 0.23f, 0.23f, 1);  // PCB gris
    
    // Géométries des portes
    private Geometry andGateGeometry;
    private Geometry orGateGeometry;
    private Geometry notGateGeometry;
    private Geometry wireGeometry;
    
    // Contrôleur de caméra orbitale
    private OrbitCameraControl orbitCamera;
    
    public static void main(String[] args) {
        JMonkeyRenderer app = new JMonkeyRenderer();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Configuration caméra
        cam.setLocation(new Vector3f(0, 50, 100));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cam.setFrustumNear(0.1f);
        cam.setFrustumFar(1000f);
        
        // Lumières
        setupLighting();
        
        // Matériaux
        setupMaterials();
        
        // Créer les prototypes de portes
        createGatePrototypes();
        
        // Ajouter des portes de démonstration
        createDemoCircuit();
        
        // Contrôles caméra
        setupCameraControls();
        
        // Fond
        viewPort.setBackgroundColor(new ColorRGBA(0.1f, 0.1f, 0.15f, 1));
    }
    
    private void setupLighting() {
        // Lumière directionnelle (soleil)
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalize());
        sun.setColor(ColorRGBA.White);
        sun.setIntensity(0.8f);
        rootNode.addLight(sun);
        
        // Lumière ambiante
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1));
        rootNode.addLight(ambient);
    }
    
    private void setupMaterials() {
        // Matériau PCB
        Material pcbMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pcbMat.setColor("Color", COLOR_PCB);
        pcbMat.setFloat("Alpha", 1.0f);
        
        // Matériau signal HIGH
        Material highMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        highMat.setColor("Color", COLOR_HIGH);
        highMat.setBoolean("UseMaterialColors", true);
        
        // Matériau signal LOW
        Material lowMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lowMat.setColor("Color", COLOR_LOW);
        
        // Matériau avec emission (pour LEDs)
        Material ledHighMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ledHighMat.setBoolean("UseMaterialColors", true);
        ledHighMat.setColor("Diffuse", COLOR_HIGH);
        ledHighMat.setColor("Emissive", COLOR_HIGH);
        
        Material ledLowMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ledLowMat.setBoolean("UseMaterialColors", true);
        ledLowMat.setColor("Diffuse", COLOR_LOW);
    }
    
    private void createGatePrototypes() {
        // AND Gate - forme rectangulaire avec encoches
        Box andBox = new Box(2f, 1f, 0.5f);
        andGateGeometry = new Geometry("AND_GATE", andBox);
        
        Material andMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        andMat.setColor("Color", COLOR_PCB);
        andGateGeometry.setMaterial(andMat);
        
        // OR Gate - forme ovale
        Sphere orSphere = new Sphere(16, 16, 1.2f);
        orGateGeometry = new Geometry("OR_GATE", orSphere);
        
        Material orMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        orMat.setColor("Color", COLOR_PCB);
        orGateGeometry.setMaterial(orMat);
        
        // NOT Gate - triangle
        Box notBox = new Box(1f, 1f, 0.3f);
        notGateGeometry = new Geometry("NOT_GATE", notBox);
        
        Material notMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        notMat.setColor("Color", COLOR_PCB);
        notGateGeometry.setMaterial(notMat);
        
        // Wire - cylinder
        Box wireBox = new Box(1f, 0.1f, 0.1f);
        wireGeometry = new Geometry("WIRE", wireBox);
        
        Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", COLOR_HIGH);
        wireGeometry.setMaterial(wireMat);
    }
    
    /**
     * Crée un circuit de démonstration.
     */
    private void createDemoCircuit() {
        // AND Gate à (0, 0, 0)
        Geometry and1 = andGateGeometry.clone();
        and1.setLocalTranslation(0, 0, 0);
        and1.setName("AND_1");
        rootNode.attachChild(and1);
        
        // Ajouter LED à la sortie
        Geometry led1 = createLED(COLOR_HIGH);
        led1.setLocalTranslation(2.5f, 0, 0);
        led1.setName("LED_AND_1");
        rootNode.attachChild(led1);
        
        // OR Gate à (10, 0, 0)
        Geometry or1 = orGateGeometry.clone();
        or1.setLocalTranslation(10, 0, 0);
        or1.setName("OR_1");
        rootNode.attachChild(or1);
        
        // LED OR
        Geometry led2 = createLED(COLOR_LOW);
        led2.setLocalTranslation(12.5f, 0, 0);
        led2.setName("LED_OR_1");
        rootNode.attachChild(led2);
        
        // NOT Gate à (0, 10, 0)
        Geometry not1 = notGateGeometry.clone();
        not1.setLocalTranslation(0, 10, 0);
        not1.setName("NOT_1");
        rootNode.attachChild(not1);
        
        // LED NOT
        Geometry led3 = createLED(COLOR_HIGH);
        led3.setLocalTranslation(2f, 10, 0);
        led3.setName("LED_NOT_1");
        rootNode.attachChild(led3);
        
        // Câble entre AND et OR
        createWire(2.5f, 0, 0, 7.5f, 0, 0);
        
        // Plus de portes pour démo
        for (int i = 0; i < 5; i++) {
            Geometry andDemo = andGateGeometry.clone();
            andDemo.setLocalTranslation(-10 + i * 5, -10 + (i % 3) * 5, 0);
            andDemo.setName("AND_DEMO_" + i);
            rootNode.attachChild(andDemo);
            
            Geometry ledDemo = createLED(i % 2 == 0 ? COLOR_HIGH : COLOR_LOW);
            ledDemo.setLocalTranslation(-8 + i * 5, -10 + (i % 3) * 5, 0);
            ledDemo.setName("LED_DEMO_" + i);
            rootNode.attachChild(ledDemo);
        }
    }
    
    /**
     * Crée une LED.
     */
    private Geometry createLED(ColorRGBA color) {
        Sphere sphere = new Sphere(8, 8, 0.3f);
        Geometry led = new Geometry("LED", sphere);
        
        Material ledMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ledMat.setBoolean("UseMaterialColors", true);
        ledMat.setColor("Diffuse", color);
        ledMat.setColor("Emissive", color.mult(0.5f));
        led.setMaterial(ledMat);
        
        return led;
    }
    
    /**
     * Crée un cable.
     */
    private void createWire(float x1, float y1, float z1, float x2, float y2, float z2) {
        float length = (float) Math.sqrt(
            (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
        
        Box wireBox = new Box(length/2, 0.1f, 0.1f);
        Geometry wire = new Geometry("WIRE", wireBox);
        
        float mx = (x1 + x2) / 2;
        float my = (y1 + y2) / 2;
        float mz = (z1 + z2) / 2;
        
        wire.setLocalTranslation(mx, my, mz);
        
        Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", COLOR_HIGH);
        wire.setMaterial(wireMat);
        
        rootNode.attachChild(wire);
    }
    
    private void setupCameraControls() {
        orbitCamera = new OrbitCameraControl(cam, inputManager);
        stateManager.attach(orbitCamera);
    }
    
    /**
     * Met à jour l'état d'un composant.
     */
    public void updateComponentState(String componentId, int value) {
        Geometry geom = (Geometry) rootNode.getChild("LED_" + componentId);
        if (geom != null) {
            Material mat = geom.getMaterial();
            ColorRGBA color = (value == 1) ? COLOR_HIGH : COLOR_LOW;
            mat.setColor("Emissive", color.mult(0.5f));
            mat.setColor("Diffuse", color);
        }
    }
}
