# Prompts IA et Recommandations pour Plugin 3D

## Prompts pour IA Générative - Création de Modèles 3D

### Prompt 1: Porte AND Gate 3D

```
Tâche: Créer un modèle 3D pour une porte AND dans le plugin Logisim Evolution

Contexte technique:
- Type: Porte logique AND à 2-8 entrées configurables
- Style visuel: Électronique/PCB avec look moderne
- Format sortie: Mesh optimisé pour instancing GPU (OBJ/GLTF)
- Niveaux LOD requis: 4 (High/Medium/Low/Ultra-Low)
- Ports de connexion: Entrées sur les côtés, Sortie à l'extrémité

Spécifications détaillées:
- Géométrie: Boîtier rectangulaire avec chanfreins et encoches pour les ports
- Dimensions normalisées: 2x1x0.5 unités (Lxlxh)
- Materials:
  * Corps principal: Gris foncé satiné (#3A3A3A), rugosité 0.6
  * Inscription: Blanc (#FFFFFF), police technique
  * Ports de connexion: Cuivre metallisé (#B87333), metalness 0.9
  * LED d'état: Sphère intégrёe, émissive dynamique
  
États visuels:
| État    | Couleur LED  | Effet visuel              |
|---------|--------------|---------------------------|
| HIGH    | #00FF00      | Glow intensif (intensité 1.0) |
| LOW     | #FF0000      |LED êteint ou très faible |
| FLOAT   | #FFA500      | Pulse周期ique (1Hz)      |
| ERROR   | #FF0000      | Clignotement rapide      |

Animation requise:
- Transition entre états: 100ms ease-in-out
- Propagation du signal: Effet "ripple" le long du chemin
- Clock: Pulse lumineux toutes les X ms

 LOD Details:
- HIGH (0-20u): Mesh complet avec détails des broches, gravures
- MEDIUM (20-50u): Mesh simplifié, sans gravures fines
- LOW (50-100u): Box avec couleur, ports simplifiés
- ULTRA_LOW (100u+): Point sprite avec code couleur
```

### Prompt 2: Câble/Wire 3D

```
Tâche: Créer un modèle 3D pour un câble électrique dans le plugin Logisim Evolution

Contexte:
- Type: Câble logique (1-bit à multi-bit)
- Style: Câble PCB avec gaine isolante
- Animation: Flux de données visuel

Spécifications:
- Géométrie: Tube flexible avec segmentation
- Rayon: 0.05 unités (1-bit), variable (multi-bit)
- Material: Isolation PVC bleu/vert/jaune selon usage
- Conducteur interne: Cuivre (#B87333) visible aux extrémités

États visuels:
| Bit Width | Couleur base | Animation flux    |
|-----------|--------------|------------------|
| 1         | #3366CC     | Particules       |
| 4         | #CC33CC     | Bandes mobiles   |
| 8+        | Gradient    | Vague continue   |

Comportement:
- Changement d'état: Propagation instantanée
- Multi-bit: Codage couleur par valeur numérique
- Bundles: Regroupement visuel pour fils parallèles
```

### Prompt 3: RAM/ROM 3D

```
Tâche: Créer un modèle 3D pour un composant mémoire (RAM/ROM) dans Logisim Evolution

Spécifications:
- Type: RAM ou ROM paramétrable (512B à 64KB)
- Style: Boîtier DIP/FPGA avec pins et afficheur
- Dimensions: 4x3x0.5 unités base, extensible

Éléments visuels:
1. Boîtier principal: Noir mat avec inscriptions techniques
2. Afficheur hexadécimal: 7-segments intégré (lecture)
3. LEDs d'état: Lecture (vert), Écriture (rouge), Accès (jaune)
4. Pins: Barrette de connexions latérales

États:
- Idle: LED jaune clignotante lente
- Read: LED verte allumée, données sur afficheur
- Write: LED rouge pulsée pendant écriture
- Error: LED rouge clignotante rapide
```

---

## Recommandations d'Implémentation

### Architecture Générale

```
┌─────────────────────────────────────────────────────────────────┐
│                    LOGISIM EVOLUTION 2D                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │   Circuit   │  │  Simulator  │  │  Components │            │
│  │    State    │  │   Engine    │  │   (Gates,  │            │
│  │             │  │             │  │   Memory)  │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
└──────────────────────────┬──────────────────────────────────────┘
                           │ Interface Events
┌──────────────────────────▼──────────────────────────────────────┐
│                    PLUGIN 3D (Séparé)                           │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              API Bridge (SimulationEventReceiver)       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                          │                                      │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐              │
│  │   Scene    │  │   Layout   │  │  Export    │              │
│  │  Manager   │  │  Engine    │  │  (JSON/XML)│              │
│  └────────────┘  └────────────┘  └────────────┘              │
│         │                                                   │
│  ┌──────▼──────────────────────────────────────────┐          │
│  │           Rendering Pipeline (GPU)              │          │
│  │  ┌──────────┐  ┌──────────┐  ┌─────────────┐  │          │
│  │  │ Instancing│  │   LOD    │  │   Shaders   │  │          │
│  │  └──────────┘  └──────────┘  └─────────────┘  │          │
│  └──────────────────────────────────────────────────┘          │
└────────────────────────────────────────────────────────────────┘
```

### Points Clés de Conception

1. **Séparation stricte**
   - Plugin dans un module Gradle séparé
   - Communication uniquement via interfaces
   - Pas de dépendance directe au code 2D

2. **Performance**
   - Instancing GPU pour les portes identiques
   - LOD dynamique selon distance caméra
   - Batch update des états de signaux

3. **Extensibilité**
   - Fabrique de composants 3D extensible
   - Points d'extension pour nouveaux types
   - Support plugins additionnels

---

## Configuration Gradle Recommandée

```kotlin
// build.gradle.kts du module plugin3d
plugins {
    id("java-library")
    id("application")
}

dependencies {
    // Logisim Evolution core
    implementation(project(":logisim-evolution"))
    
    // JMonkeyEngine pour 3D
    implementation("org.jmonkeyengine:jme3-core:3.6.0")
    implementation("org.jmonkeyengine:jme3-desktop:3.6.0")
    implementation("org.jmonkeyengine:jme3-lwjgl:3.6.0")
    
    // JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

application {
    mainClass.set("com.cburch.logisim.plugin3d.Plugin3D")
}
```

---

## Structure des Fichiers à Créer

```
src/main/java/com/cburch/logisim/plugin3d/
├── Plugin3D.java
├── api/
│   ├── SimulationEventReceiver.java
│   ├── SceneGraphAPI.java
│   └── ExportAPI.java
├── core/
│   ├── SceneManager.java
│   ├── CameraController.java
│   ├── LODManager.java
│   ├── InstanceRegistry.java
│   └── Object3D.java
├── components/
│   ├── ComponentFactory3D.java
│   ├── Gate3D.java
│   ├── Wire3D.java
│   └── Memory3D.java
├── simulation/
│   ├── SignalStateMapper.java
│   ├── TimeTravelManager.java
│   └── SignalTracer.java
├── layout/
│   ├── AutoLayoutEngine.java
│   └── HierarchicalLayout.java
├── rendering/
│   ├── RenderPipeline.java
│   ├── InstancedRenderer.java
│   └── ShaderManager.java
├── export/
│   ├── JSONExporter.java
│   ├── XMLExporter.java
│   └── ModelSerializer.java
└── ui/
    ├── View3DWindow.java
    └── CameraToolbar.java
```

---

## Checklist d'Implémentation

### Phase 1: Fondations
- [ ] 1.1 Création module Gradle plugin3d
- [ ] 1.2 Configuration dépendances JMonkeyEngine
- [ ] 1.3 Interface SimulationEventReceiver
- [ ] 1.4 Point d'entrée Plugin3D

### Phase 2: Core Scene
- [ ] 2.1 SceneManager basique
- [ ] 2.2 CameraController (Overview, Orbit)
- [ ] 2.3 LODManager (4 niveaux)
- [ ] 2.4 Fenêtre View3DWindow

### Phase 3: Composants
- [ ] 3.1 ComponentFactory3D
- [ ] 3.2 Gate3D (AND, OR, NOT, XOR, NAND)
- [ ] 3.3 Wire3D
- [ ] 3.4 Memory3D (RAM, ROM)

### Phase 4: Rendu
- [ ] 4.1 InstancedRenderer
- [ ] 4.2 ShaderManager (couleurs états)
- [ ] 4.3 SignalStateMapper
- [ ] 4.4 Animation transitions

### Phase 5: Layout
- [ ] 5.1 AutoLayoutEngine
- [ ] 5.2 Algorithme A* pour fils
- [ ] 5.3 Placement grille hiérarchique

### Phase 6: Features
- [ ] 6.1 TimeTravelManager
- [ ] 6.2 SignalTracer
- [ ] 6.3 Visualisation énergétique
- [ ] 6.4 Export JSON/XML

### Phase 7: Caméra
- [ ] 7.1 Mode FPS (WASD)
- [ ] 7.2 Mode Focus (clic composant)
- [ ] 7.3 Mode Top-Down

### Phase 8: Tests
- [ ] 8.1 Tests unitaires
- [ ] 8.2 Tests intégration Logisim
- [ ] 8.3 Performance (1000+ composants)

---

## Exemples de Code - Intégration

### Intégration avec le Simulateur

```java
// Dans SimulationEventReceiver
public class PluginSimulationListener implements SimulatorListener {
    
    private final SceneManager sceneManager;
    
    public PluginSimulationListener(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    @Override
    public void tick(CircuitState circuitState) {
        // Mettre à jour les états des composants
        for (Component comp : circuitState.getCircuit().getComponents()) {
            Object state = circuitState.getComponentState(comp);
            sceneManager.updateComponentState(comp.getId(), state);
        }
    }
    
    @Override
    public void propagationComplete() {
        // Déclencher le rendu
        sceneManager.requestRender();
    }
}
```

### Synchronisation 2D → 3D

```java
public class SceneSynchronizer {
    
    private final Map<String, Object3D> objectMap = new HashMap<>();
    private final SceneManager sceneManager;
    
    public void syncWithCircuit(Circuit circuit) {
        // Ajouter les nouveaux composants
        for (Component comp : circuit.getComponents()) {
            if (!objectMap.containsKey(comp.getId())) {
                Object3D obj3d = createFromComponent(comp);
                objectMap.put(comp.getId(), obj3d);
                sceneManager.addObject(obj3d);
            }
        }
        
        // Supprimer les composants supprimés
        Set<String> currentIds = getComponentIds(circuit);
        objectMap.keySet().removeIf(id -> !currentIds.contains(id));
    }
}
```

---

## Métriques de Performance Cibles

| Métrique | Cible | Méthode mesure |
|----------|-------|----------------|
| FPS (100 composants) | 60+ | Benchmark intégré |
| FPS (1000 composants) | 30+ | Benchmark intégré |
| Latence signal→affichage | <16ms | Time measurement |
| Mémoire (1000 instances) | <100MB | Profiler |
| Temps de chargement | <2s | Stopwatch |

---

*Document généré - Prompts et Recommandations Plugin 3D*
*Version: 1.0.0 - Date: 2026-02-18*
