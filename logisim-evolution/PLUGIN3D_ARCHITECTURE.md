# Architecture du Plugin 3D pour Logisim Evolution

## 1. Vue d'Ensemble

### 1.1 Objectifs
Créer un plugin 3D modulaire et scalable, séparé du moteur 2D, permettant:
- Visualisation 3D des circuits logiques
- Navigation immersive (Overview, FPS, Focus)
- Export de modèles 3D (JSON/XML)
- Optimisation GPU (Instancing, LOD)
- Fonctionnalités innovantes (time-travel, trace de signal)

### 1.2 Contraintes
- Séparation stricte logique/vision
- Support GPU Instancing et LOD
- Activation/désactivation sans impact 2D
- API claire pour événements de simulation
- Export JSON/XML hiérarchique
- Extensibilité future

---

## 2. Structure des Paquets

```
src/main/java/com/cburch/logisim/plugin3d/
├── Plugin3D.java                 # Point d'entrée
├── api/
│   ├── SimulationEventReceiver   # Interface événements simulation
│   ├── SceneGraphAPI             # API scène 3D
│   └── ExportAPI                 # API export JSON/XML
├── core/
│   ├── SceneManager              # Gestionnaire scène principal
│   ├── CameraController          # Caméra multi-mode
│   ├── LODManager                # Gestionnaire LOD
│   └── InstanceRegistry          # Registre instances GPU
├── components/
│   ├── ComponentFactory3D       # Fabrique composants
│   ├── gates/                    # Portes logiques
│   ├── wires/                    # Câbles
│   └── memory/                   # RAM/ROM
├── simulation/
│   ├── SignalStateMapper         # Mappe états vers couleurs
│   ├── TimeTravelManager         # Time-travel
│   └── SignalTracer              # Traceur signaux
├── layout/
│   ├── AutoLayoutEngine          # Layout automatique
│   └── HierarchicalLayout        # Layout hiérarchique
├── rendering/
│   ├── RenderPipeline            # Pipeline rendu
│   ├── InstancedRenderer         # Rendu instancing
│   └── ShaderManager             # Shaders
├── export/
│   ├── JSONExporter              # Export JSON
│   ├── XMLExporter               # Export XML
│   └── ModelSerializer           # Serialisation
└── ui/
    ├── View3DWindow              # Fenêtre 3D
    └── CameraToolbar             # Barre caméra
```

---

## 3. Formats d'Export

### 3.1 Format JSON

```json
{
  "schema_version": "1.0.0",
  "export_date": "2026-02-18T21:39:24Z",
  "circuit": {
    "id": "circuit_uuid",
    "name": "main",
    "bounding_box": {
      "min": {"x": 0, "y": 0, "z": 0},
      "max": {"x": 100, "y": 50, "z": 10}
    },
    "components": [
      {
        "type": "AND_GATE",
        "id": "comp_001",
        "position": {"x": 10, "y": 5, "z": 0},
        "rotation": 0,
        "inputs": [
          {"id": "in_a", "position": {"x": 8, "y": 3, "z": 0}},
          {"id": "in_b", "position": {"x": 8, "y": 7, "z": 0}}
        ],
        "outputs": [
          {"id": "out", "position": {"x": 12, "y": 5, "z": 0}}
        ],
        "state": {
          "signal_value": 1,
          "propagation_delay": 0
        }
      }
    ],
    "wires": [
      {
        "id": "wire_001",
        "from": {"component": "comp_001", "port": "out"},
        "to": {"component": "comp_002", "port": "in_a"},
        "points": [
          {"x": 12, "y": 5, "z": 0},
          {"x": 15, "y": 5, "z": 0}
        ],
        "bit_width": 1,
        "state": {"signal_value": 1}
      }
    ]
  }
}
```

### 3.2 Format XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<logisim3d schema_version="1.0.0">
  <circuit id="circuit_uuid" name="main">
    <components>
      <component type="AND_GATE" id="comp_001">
        <position x="10" y="5" z="0"/>
        <rotation>0</rotation>
        <state signal_value="1"/>
      </component>
    </components>
  </circuit>
</logisim3d>
```

---

## 4. Algorithme de Layout Automatique

```
ALGORITHME AutoLayout(circuit):
    components = circuit.getComponents()
    subcircuits = filter(components, isSubcircuit)
    gates = filter(components, isGate)
    
    positions = {}
    
    // Placement hiérarchique
    FOR each subcircuit IN subcircuits:
        positions[subcircuit.id] = computePosition(subcircuit)
    
    // Grille pour portes
    grid = Grid(10, 10)
    FOR each gate IN gates:
        position = findBestGridPosition(gate, grid, positions)
        positions[gate.id] = position
        grid.reserve(position)
    
    // Routage cables
    FOR each wire IN wires:
        wire.path = aStarRouting(wire.start, wire.end)
    
    RETURN positions
```

---

## 5. Pipeline Simulation → Rendu 3D

```
Logisim 2D --> SimulationEvent --> SimulationEventReceiver
                                         |
                                         v
                              SignalStateMapper
                                         |
                                         v
                              InstanceRegistry
                                         |
                                         v
                              RenderPipeline (GPU)
                                         |
                                         v
                                      Écran
```

### 5.1 Interface SimulationEventReceiver

```java
package com.cburch.logisim.plugin3d.api;

public interface SimulationEventReceiver {
    void onSignalChange(String componentId, int value, int bitWidth);
    void onClockTick(long timestamp);
    void onCircuitChange(Object event);
    void onPropagationComplete();
}
```

---

## 6. Modes de Caméra

| Mode | Description |
|------|-------------|
| **Overview** | Vue d'ensemble du circuit |
| **FPS** | Navigation première personne (WASD) |
| **Focus** | Zoom sur un composant |
| **Orbit** | Rotation autour d'un point |
| **Top-Down** | Vue de dessus |

### 6.1 CameraController

```java
public class CameraController {
    public enum CameraMode { OVERVIEW, FPS, FOCUS, ORBIT, TOP_DOWN }
    
    private CameraMode currentMode;
    
    public void setMode(CameraMode mode) {
        this.currentMode = mode;
        // Configuration caméra selon mode
    }
    
    public void focusOnComponent(Object3D component) {
        targetPosition = component.getWorldTranslation();
        setMode(CameraMode.ORBIT);
    }
}
```

---

## 7. Optimisation GPU

### 7.1 Instancing

```java
public class InstancedRenderer {
    private static final int MAX_INSTANCES = 1024;
    
    public void renderGates(List<Gate3D> gates) {
        Map<GateType, List<Gate3D>> grouped = groupByType(gates);
        
        for (var entry : grouped.entrySet()) {
            prepareInstanceData(entry.getValue());
            renderInstanced(entry.getKey().getMesh(), 
                           entry.getValue().size());
        }
    }
}
```

### 7.2 LOD

```java
public class LODManager {
    private static final float LOD_HIGH = 20.0f;
    private static final float LOD_MEDIUM = 50.0f;
    private static final float LOD_LOW = 100.0f;
    
    public LODLevel getLODLevel(float distance) {
        if (distance < LOD_HIGH) return LODLevel.HIGH;
        if (distance < LOD_MEDIUM) return LODLevel.MEDIUM;
        if (distance < LOD_LOW) return LODLevel.LOW;
        return LODLevel.ULTRA_LOW;
    }
}
```

---

## 8. Fonctionnalités Innovantes

### 8.1 Time-Travel

```java
public class TimeTravelManager {
    private Deque<SimulationSnapshot> history = new ArrayDeque<>();
    private static final int MAX_HISTORY = 10000;
    
    public void recordState(CircuitState state) {
        history.push(new SimulationSnapshot(state));
        if (history.size() > MAX_HISTORY) history.removeLast();
    }
    
    public void undo() {
        if (history.size() <= 1) return;
        history.pop(); // Remove current
        applyState(history.peek()); // Apply previous
    }
}
```

### 8.2 Trace de Signal

```java
public class SignalTracer {
    private Map<String, List<SignalEvent>> signalHistory = new HashMap<>();
    
    public void recordSignalChange(String componentId, int oldVal, 
                                   int newVal, long time) {
        signalHistory.computeIfAbsent(componentId, k -> new ArrayList<>())
            .add(new SignalEvent(oldVal, newVal, time));
    }
}
```

---

## 9. Prompts IA pour Modèles 3D

### 9.1 Création porte AND

```
Créer modèle 3D porte AND pour Logisim Evolution:
- Type: Porte AND 2-8 entrées
- Style: PCB électronique
- LOD: 4 niveaux
- Ports: Entrées côtés, Sortie bout
- Couleurs: Corps gris (#3A3A3A), Ports cuivre (#B87333)
- LED: Vert si actif, Rouge si inactif
- Animation: Pulse lumineux sur changement état
```

### 9.2 Modification selon état

```
Modifier apparence 3D selon état simulation:
| État    | Couleur | LED    |
|---------|---------|--------|
| HIGH    | Vert    | Glow   |
| LOW     | Gris    | None   |
| FLOAT   | Orange  | Pulse  |
| ERROR   | Rouge   | Blink  |
```

---

## 10. Intégration Plugin

```java
public class Plugin3D implements Plugin {
    private SceneManager sceneManager;
    private View3DWindow view3DWindow;
    private boolean enabled = false;
    
    @Override
    public void load(Project project) {
        sceneManager = new SceneManager(project);
        view3DWindow = new View3DWindow(sceneManager);
        project.getSimulator().addListener(
            new SimulationEventReceiver(sceneManager));
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) view3DWindow.show();
        else view3DWindow.hide();
    }
}
```

---

## 11. Checklist Implémentation

- [ ] 1. Structure des paquets base
- [ ] 2. Interface SimulationEventReceiver
- [ ] 3. SceneManager
- [ ] 4. Fabrique composants 3D
- [ ] 5. Caméra multi-mode
- [ ] 6. Layout automatique
- [ ] 7. Rendu instancing GPU
- [ ] 8. Système LOD
- [ ] 9. Exporteurs JSON/XML
- [ ] 10. Time-Travel
- [ ] 11. Trace signaux
- [ ] 12. Visualisation énergétique
- [ ] 13. Interface utilisateur 3D
- [ ] 14. Tests intégration

---

## 12. Technologies Recommandées

| Composant | Technologie |
|-----------|-------------|
| Moteur 3D | JMonkeyEngine 3.x |
| Rendu | OpenGL 4.x |
| Shaders | GLSL |
| Serialisation | Jackson (JSON) + JAXB |
| UI | JavaFX/Swing (intégré) |

---

*Document généré - Architecture Plugin 3D Logisim Evolution*
*Version: 1.0.0 - Date: 2026-02-18*
