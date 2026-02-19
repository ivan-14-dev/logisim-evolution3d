package com.cburch.logisim.plugin3d.api;

import com.cburch.logisim.plugin3d.core.Object3D;
import java.util.List;

/**
 * API publique pour la gestion de la scène 3D.
 * Permet d'interagir avec les objets 3D de la scène.
 */
public interface SceneGraphAPI {
    
    /**
     * Ajoute un objet à la scène.
     */
    void addObject(Object3D object);
    
    /**
     * Supprime un objet de la scène.
     */
    void removeObject(Object3D object);
    
    /**
     * Met à jour un objet existant.
     */
    void updateObject(String id, Object3D object);
    
    /**
     * Récupère un objet par son ID.
     */
    Object3D getObject(String id);
    
    /**
     * Récupère tous les objets de la scène.
     */
    List<Object3D> getAllObjects();
    
    /**
     * Efface tous les objets de la scène.
     */
    void clear();
    
    /**
     * Met à jour la transformation d'un objet.
     */
    void updateTransform(String id, float[] matrix);
    
    /**
     * Met à jour la couleur d'un objet (pour les signaux).
     */
    void updateColor(String id, float r, float g, float b, float a);
}
