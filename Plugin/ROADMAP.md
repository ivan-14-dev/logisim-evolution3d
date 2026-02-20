# Roadmap - Plugin 3D Logisim Evolution

## État Actuel du Projet

### Structure Actuelle
```
Plugin/
├── src/main/java/com/cburch/logisim/plugin3d/
│   ├── api/                    ✅ Interfaces de base
│   ├── core/                   ✅ Classes principales  
│   ├── components/             ⚠️ Partiel
│   ├── simulation/              ✅ Time-travel & trace
│   ├── rendering/               ⚠️ JMonkeyEngine
│   ├── export/                  ⚠️ JSON/XML basique
│   └── ui/                     ⚠️ Panneau Swing
└── web/
    └── visualization.html       ✅ Demo fonctionnelle
```

---

## Ce Qu'il Faut Faire Pour Être Compétitif

### 1. Architecture & Design Patterns

| Aspect | Actuel | Compétitif | Priorité |
|--------|--------|------------|----------|
| **Architecture** | Simple | Modulaire (OSGi) | HAUTE |
| **API Events** | Interface | EventBus/Observer | HAUTE |
| **Threads** | Blocking | Async/Non-blocking | MOYENNE |
| **Configuration** | Hardcoded | DI (Guice/Dagger) | MOYENNE |

### 2. Rendu 3D (Points Critiques)

**Actuel:**
- JMonkeyEngine basique
- Géométries primitives (Box, Sphere)
- Couleurs statiques

**Compétitif - À implémenter:**
- [ ] **GPU Instancing** - Pour 1000+ composants
- [ ] **LOD Dynamique** - 4+ niveaux avec transition smooth
- [ ] **Shaders Personalisés** - Glow, emission, transparency
- [ ] **Post-Processing** - Bloom, SSAO, anti-aliasing
- [ ] **Ombres temps réel** - Shadow mapping
- [ ] **PBR Materials** - Metallic/roughness

### 3. Synchronisation Simulation → 3D

| Fonction | Statut | Détails |
|----------|--------|---------|
| Signal change | ⚠️ Partiel | Via interface |
| Clock tick | ❌ Manquant | À implémenter |
| Component add/remove | ⚠️ Partiel | Via CircuitListener |
| Wire state | ❌ Manquant | À implémenter |

### 4. Modèles 3D de Composants

**À créer:**
- [ ] Portes logiques détaillées (AND, OR, NOT, XOR, NAND, NOR, XNOR)
- [ ] Bascules (D, JK, T, SR)
- [ ] Mémoires (RAM, ROM) avec afficheur hexadécimal
- [ ] Entrées/Sorties (Pin, Clock, Button, LED)
- [ ] Composants avancés (Multiplexeur, Démultiplexeur, Encodeur, Décodeur)
- [ ] CPU complet avec visualiseur interne

### 5. Fonctionnalités Avancées

| Fonctionnalité | Priorité | Complexité |
|----------------|----------|------------|
| Time-travel | HAUTE | Moyenne |
| Signal tracing | HAUTE | Faible |
| Visualisation énergie | MOYENNE | Haute |
| Analyse timing | MOYENNE | Haute |

### 6. Interaction Utilisateur

| Fonction | Statut | À faire |
|----------|--------|---------|
| Caméra orbit | ✅ | Optimiser |
| Caméra FPS | ❌ | Implémenter |
| Sélection composants | ❌ | Raycasting |
| Déplacement composants | ❌ | Drag & drop 3D |
| Zoom focus | ❌ | Animer |

### 7. Export/Import

| Format | Statut | Qualité |
|--------|--------|---------|
| JSON | ⚠️ Basique | Étendre |
| XML | ⚠️ Basique | Étendre |
| GLTF/OBJ | ❌ | Créer |
| Scene3D | ❌ | Créer |

---

## Plan d'Action Détaillé

### Phase 1: Fondations (2-3 semaines)

1. **Architecture OSGi** - Faire du plugin un module OSGi réel
2. **Event Bus** - Implémenter EventBus pour événements
3. **API propre** - Nettoyer et documenter les interfaces

### Phase 2: Rendu 3D (3-4 semaines)

1. **GPU Instancing** - Renderer optimisé
2. **Shaders** - Glow, emission, PBR
3. **LOD** - Système complet
4. **Post-processing** - Bloom

### Phase 3: Composants (4-6 semaines)

1. **Modèles 3D** - Créer meshes détaillés
2. **Animations** - LED, clock, transition
3. **Audio** - Son des commutations

### Phase 4: Intégration (2-3 semaines)

1. **Synchronisation** - Temps réel
2. **Menu** - Intégration UI Logisim
3. **Raccourcis** - Clavier

### Phase 5: Tests & Optimisation (2-3 semaines)

1. **Tests** - Unitaires + Intégration
2. **Benchmark** - Performance
3. **Documentation** - Wiki

---

## Comparatif avec Solutions Existantes

| Solution | Points Forts | Faiblesses |
|----------|--------------|------------|
| Logisim original | Simple | Pas de 3D |
| Digital | 3D basique | Fermé |
| **Notre Plugin** | Open source, extensible | En développement |

---

## Technologies Recommandées

| Composant | Technologie | Justification |
|-----------|-------------|---------------|
| **3D Engine** | JMonkeyEngine 3.8+ | Mature, Java natif |
| **DI** | Google Guice | Léger, compatible OSGi |
| **Serialization** | Jackson + JAXB | Standard |
| **Shaders** | GLSL 3.30+ | Standard GPU |
| **Tests** | JUnit 5 + Mockito | Standard Java |
| **Build** | Gradle (Kotlin) | Moteur existant |

---

## Métriques de Performance Cibles

| Métrique | Cible | Méthode |
|----------|-------|---------|
| FPS (100 comp) | 60+ | Benchmark |
| FPS (1000 comp) | 30+ | Instancing |
| Latence signal→3D | <16ms | Profiler |
| Mémoire (1000 comp) | <200MB | JVisualVM |
| Temps de chargement | <2s | Stopwatch |

---

## Prochaines Étapes Immédiates

1. ⬜ **Nettoyer le code** - Supprimer les packages incomplets
2. ⬜ **Documentation** - README avec installation
3. ⬜ **Build setup** - gradle.properties correct
4. ⬜ **Premier test** - Compilation avec Logisim

---

*Document généré le 2026-02-19*
