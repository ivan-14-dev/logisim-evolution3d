package com.cburch.logisim.plugin3d.export;

import com.cburch.logisim.plugin3d.core.SceneManager;
import com.cburch.logisim.plugin3d.core.Object3D;
import java.util.List;

/**
 * Exporteur JSON pour les modèles 3D.
 */
public class JSONExporter {
    
    private final SceneManager sceneManager;
    
    public JSONExporter(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Exporte la scène en JSON.
     */
    public String export(SceneManager scene) {
        StringBuilder json = new StringBuilder();
        
        json.append("{\n");
        json.append("  \"schema_version\": \"1.0.0\",\n");
        json.append("  \"export_date\": \"").append(java.time.Instant.now()).append("\",\n");
        json.append("  \"circuit\": {\n");
        json.append("    \"components\": [\n");
        
        List<Object3D> objects = scene.getAllObjects();
        for (int i = 0; i < objects.size(); i++) {
            Object3D obj = objects.get(i);
            json.append("      ").append(obj.toJSON());
            if (i < objects.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("    ]\n");
        json.append("  }\n");
        json.append("}\n");
        
        return json.toString();
    }
}
