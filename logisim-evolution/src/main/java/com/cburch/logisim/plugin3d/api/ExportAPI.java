package com.cburch.logisim.plugin3d.api;

import com.cburch.logisim.plugin3d.core.SceneManager;

/**
 * Interface pour l'export des modèles 3D.
 * Supporte les formats JSON et XML.
 */
public interface ExportAPI {
    
    /**
     * Exporte la scène actuelle en format JSON.
     * 
     * @return Chaîne JSON représentant la scène
     */
    String exportToJSON();
    
    /**
     * Exporte la scène actuelle en format XML.
     * 
     * @return Chaîne XML représentant la scène
     */
    String exportToXML();
    
    /**
     * Sauvegarde l'export dans un fichier.
     * 
     * @param path Chemin du fichier
     * @param format Format ("json" ou "xml")
     * @return true si succès
     */
    boolean saveToFile(String path, String format);
    
    /**
     * Importe une scène depuis un fichier.
     * 
     * @param path Chemin du fichier
     * @return true si succès
     */
    boolean loadFromFile(String path);
}
