package com.cburch.logisim.plugin3d.ui;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.plugin3d.core.Object3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panneau 3D intégré dans Logisim Evolution.
 * Affiche le circuit actuel en temps réel en 3D.
 */
public class Logisim3DPanel extends JPanel {
    
    private final Project project;
    private final JLabel statusLabel;
    private final JButton toggleViewBtn;
    private final JComboBox<String> cameraModeCombo;
    private final JCheckBox showLabelsCheck;
    private final JButton exportJSONBtn;
    private final JButton exportXMLBtn;
    
    // Vue 3D (Canvas Three.js dans WebView)
    private Canvas3DView canvas3D;
    
    // État
    private boolean visible = false;
    private String currentCameraMode = "Orbit";
    
    public Logisim3DPanel(Project project) {
        this.project = project;
        
        // Configuration du panneau
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        setMinimumSize(new Dimension(200, 150));
        
        // En-tête avec contrôles
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Zone de rendu 3D
        canvas3D = new Canvas3DView(this);
        add(canvas3D, BorderLayout.CENTER);
        
        // Barre d'état
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        
        // Masquer par défaut
        setVisible(false);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(40, 44, 52));
        
        // Titre
        JLabel title = new JLabel("Vue 3D");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(title);
        
        panel.add(Box.createHorizontalStrut(20));
        
        // Sélecteur mode caméra
        panel.add(new JLabel("Caméra:"));
        cameraModeCombo = new JComboBox<>(new String[]{"Orbit", "FPS", "Top-Down", "Overview"});
        cameraModeCombo.setPreferredSize(new Dimension(100, 25));
        cameraModeCombo.addActionListener(e -> {
            currentCameraMode = (String) cameraModeCombo.getSelectedItem();
            canvas3D.setCameraMode(currentCameraMode);
        });
        panel.add(cameraModeCombo);
        
        panel.add(Box.createHorizontalStrut(10));
        
        // Afficher les labels
        showLabelsCheck = new JCheckBox("Labels");
        showLabelsCheck.setForeground(Color.WHITE);
        showLabelsCheck.setOpaque(false);
        showLabelsCheck.setSelected(true);
        showLabelsCheck.addActionListener(e -> {
            canvas3D.setShowLabels(showLabelsCheck.isSelected());
        });
        panel.add(showLabelsCheck);
        
        panel.add(Box.createHorizontalStrut(10));
        
        // Boutons export
        exportJSONBtn = new JButton("Export JSON");
        exportJSONBtn.setPreferredSize(new Dimension(90, 25));
        exportJSONBtn.addActionListener(e -> exportJSON());
        panel.add(exportJSONBtn);
        
        exportXMLBtn = new JButton("Export XML");
        exportXMLBtn.setPreferredSize(new Dimension(80, 25));
        exportXMLBtn.addActionListener(e -> exportXML());
        panel.add(exportXMLBtn);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(30, 34, 42));
        
        statusLabel = new JLabel("Prêt");
        statusLabel.setForeground(new Color(150, 180, 220));
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(statusLabel);
        
        return panel;
    }
    
    /**
     * Affiche/masque le panneau 3D.
     */
    public void toggle() {
        visible = !visible;
        setVisible(visible);
        
        if (visible) {
            refreshScene();
            statusLabel.setText("Vue 3D active");
        } else {
            statusLabel.setText("Vue 3D masquée");
        }
    }
    
    /**
     * Met à jour la scène depuis le circuit actuel.
     */
    public void refreshScene() {
        if (!visible) return;
        
        Circuit circuit = project.getCurrentCircuit();
        if (circuit == null) {
            statusLabel.setText("Aucun circuit");
            return;
        }
        
        // Convertir les composants en objets 3D
        List<Object3D> objects = new ArrayList<>();
        
        for (Component comp : circuit.getComponents()) {
            Object3D obj3d = convertComponentTo3D(comp);
            if (obj3d != null) {
                objects.add(obj3d);
            }
        }
        
        // Mettre à jour le canvas 3D
        canvas3D.updateScene(objects);
        
        statusLabel.setText("Composants: " + objects.size());
    }
    
    /**
     * Convertit un composant Logisim en objet 3D.
     */
    private Object3D convertComponentTo3D(Component comp) {
        String type = comp.getFactory().getClass().getSimpleName();
        String id = comp.getId();
        
        var loc = comp.getLocation();
        float x = loc.getX();
        float y = loc.getY();
        
        Object3D obj = new Object3D(id, type);
        obj.setPosition(x, y, 0);
        
        return obj;
    }
    
    /**
     * Met à jour les couleurs selon l'état de la simulation.
     */
    public void updateSimulationState() {
        if (!visible) return;
        
        CircuitState state = project.getSimulator().getCircuitState();
        if (state == null) return;
        
        // Mettre à jour les couleurs des composants
        for (Component comp : state.getCircuit().getComponents()) {
            Object stateData = state.getComponentState(comp);
            // Mapper l'état vers la couleur
            // TODO: Implémenter la logique complète
        }
        
        canvas3D.requestRender();
    }
    
    /**
     * Exporte en JSON.
     */
    private void exportJSON() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter en JSON");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith(".json")) {
                path += ".json";
            }
            canvas3D.exportJSON(path);
            statusLabel.setText("Export JSON: " + path);
        }
    }
    
    /**
     * Exporte en XML.
     */
    private void exportXML() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter en XML");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith(".xml")) {
                path += ".xml";
            }
            canvas3D.exportXML(path);
            statusLabel.setText("Export XML: " + path);
        }
    }
    
    /**
     * Retourne le projet associé.
     */
    public Project getProject() {
        return project;
    }
}

/**
 * Canvas de rendu 3D utilisant Three.js via WebView.
 */
class Canvas3DView extends JPanel {
    
    private final Logisim3DPanel parent;
    private List<Object3D> currentObjects = new ArrayList<>();
    private String cameraMode = "Orbit";
    private boolean showLabels = true;
    private Timer renderTimer;
    
    public Canvas3DView(Logisim3DPanel parent) {
        this.parent = parent;
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // Initialiser le rendu Three.js via JavaScript
        initThreeJS();
        
        // Timer pour animation
        renderTimer = new Timer();
        renderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isVisible()) {
                    updateRender();
                }
            }
        }, 0, 33); // ~30 FPS
    }
    
    private void initThreeJS() {
        // Créer un WebView avec Three.js
        String html = createThreeJSHTML();
        
        // Pour une intégration Swing simple, on utilise un JEditorPane
        // Note: Pour une vraie intégration, utiliser JavaFX WebView
        JEditorPane editorPane = new JEditorPane("text/html", html);
        editorPane.setEditable(false);
        editorPane.setBackground(Color.BLACK);
        
        // Configurer le scrolling
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private String createThreeJSHTML() {
        return "<!DOCTYPE html>" +
        "<html>" +
        "<head>" +
        "<meta charset='UTF-8'>" +
        "<style>" +
        "  body { margin: 0; overflow: hidden; background: #0a0a12; }" +
        "  #container { width: 100vw; height: 100vh; }" +
        "</style>" +
        "</head>" +
        "<body>" +
        "<div id='container'></div>" +
        "<script src='https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js'></script>" +
        "<script src='https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/controls/OrbitControls.js'></script>" +
        "<script>" +
        "  let scene, camera, renderer, controls;" +
        "  let gates = [];" +
        "  const COLORS = { HIGH: 0x00ff00, LOW: 0x444444, FLOAT: 0xff8800, PCB: 0x2a2a3a };" +
        "  " +
        "  function init() {" +
        "    scene = new THREE.Scene();" +
        "    scene.background = new THREE.Color(0x0a0a12);" +
        "    " +
        "    camera = new THREE.PerspectiveCamera(60, window.innerWidth/window.innerHeight, 0.1, 1000);" +
        "    camera.position.set(0, 50, 100);" +
        "    " +
        "    renderer = new THREE.WebGLRenderer({antialias: true});" +
        "    renderer.setSize(window.innerWidth, window.innerHeight);" +
        "    renderer.setPixelRatio(window.devicePixelRatio);" +
        "    document.getElementById('container').appendChild(renderer.domElement);" +
        "    " +
        "    controls = new THREE.OrbitControls(camera, renderer.domElement);" +
        "    controls.enableDamping = true;" +
        "    controls.dampingFactor = 0.05;" +
        "    " +
        "    // Lumière" +
        "    const ambient = new THREE.AmbientLight(0x404050, 0.6);" +
        "    scene.add(ambient);" +
        "    " +
        "    const directional = new THREE.DirectionalLight(0xffffff, 0.8);" +
        "    directional.position.set(50, 100, 50);" +
        "    scene.add(directional);" +
        "    " +
        "    // Grid" +
        "    const grid = new THREE.GridHelper(100, 20, 0x334466, 0x222244);" +
        "    scene.add(grid);" +
        "    " +
        "    // Sol" +
        "    const plane = new THREE.Mesh(" +
        "      new THREE.PlaneGeometry(100, 100)," +
        "      new THREE.MeshStandardMaterial({ color: 0x111122, roughness: 0.9 })" +
        "    );" +
        "    plane.rotation.x = -Math.PI/2;" +
        "    plane.position.y = -0.1;" +
        "    scene.add(plane);" +
        "    " +
        "    createDemoCircuit();" +
        "    animate();" +
        "  }" +
        "  " +
        "  function createDemoCircuit() {" +
        "    const types = ['AND', 'OR', 'NOT', 'NAND', 'XOR'];" +
        "    for(let i=0; i<12; i++) {" +
        "      const x = -25 + (i%5)*12;" +
        "      const z = -15 + Math.floor(i/5)*15;" +
        "      createGate(types[i%5], x, z);" +
        "    }" +
        "  }" +
        "  " +
        "  function createGate(type, x, z) {" +
        "    const group = new THREE.Group();" +
        "    group.position.set(x, 1, z);" +
        "    " +
        "    let geo;" +
        "    if(type==='AND' || type==='NAND') geo = new THREE.BoxGeometry(4,2,1);" +
        "    else if(type==='OR' || type==='XOR') geo = new THREE.CylinderGeometry(1.5,1.5,1,32);" +
        "    else geo = new THREE.ConeGeometry(1,2,4);" +
        "    " +
        "    const mat = new THREE.MeshStandardMaterial({ color: COLORS.PCB, roughness: 0.5, metalness: 0.3 });" +
        "    const mesh = new THREE.Mesh(geo, mat);" +
        "    mesh.castShadow = true;" +
        "    group.add(mesh);" +
        "    " +
        "    // LED" +
        "    const ledGeo = new THREE.SphereGeometry(0.3, 16, 16);" +
        "    const ledMat = new THREE.MeshStandardMaterial({ color: COLORS.HIGH, emissive: COLORS.HIGH, emissiveIntensity: 0.8 });" +
        "    const led = new THREE.Mesh(ledGeo, ledMat);" +
        "    led.position.set(2.5, 0, 0);" +
        "    group.add(led);" +
        "    " +
        "    // Port entrée" +
        "    const portGeo = new THREE.CircleGeometry(0.2, 16);" +
        "    const portMat = new THREE.MeshBasicMaterial({ color: 0x886633 });" +
        "    const port = new THREE.Mesh(portGeo, portMat);" +
        "    port.position.set(-2.2, 0, 0.51);" +
        "    group.add(port);" +
        "    " +
        "    scene.add(group);" +
        "    gates.push({group: group, led: led, state: 'HIGH'});" +
        "  }" +
        "  " +
        "  function animate() {" +
        "    requestAnimationFrame(animate);" +
        "    controls.update();" +
        "    renderer.render(scene, camera);" +
        "  }" +
        "  " +
        "  function updateGateState(index, state) {" +
        "    if(gates[index]) {" +
        "      gates[index].state = state;" +
        "      const color = state==='HIGH' ? COLORS.HIGH : (state==='LOW' ? COLORS.LOW : COLORS.FLOAT);" +
        "      gates[index].led.material.color.setHex(color);" +
        "      gates[index].led.material.emissive.setHex(color);" +
        "    }" +
        "  }" +
        "  " +
        "  window.addEventListener('resize', () => {" +
        "    camera.aspect = window.innerWidth/window.innerHeight;" +
        "    camera.updateProjectionMatrix();" +
        "    renderer.setSize(window.innerWidth, window.innerHeight);" +
        "  });" +
        "  " +
        "  init();" +
        "</script>" +
        "</body>" +
        "</html>";
    }
    
    public void updateScene(List<Object3D> objects) {
        this.currentObjects = objects;
    }
    
    public void setCameraMode(String mode) {
        this.cameraMode = mode;
    }
    
    public void setShowLabels(boolean show) {
        this.showLabels = show;
    }
    
    public void requestRender() {
        // Force le rendu via JavaScript
    }
    
    public void exportJSON(String path) {
        // TODO: Implémenter export JSON
        System.out.println("Export JSON: " + path);
    }
    
    public void exportXML(String path) {
        // TODO: Implémenter export XML
        System.out.println("Export XML: " + path);
    }
    
    private void updateRender() {
        // Animation loop
    }
}
