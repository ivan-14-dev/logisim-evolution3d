package com.cburch.logisim.plugin3d.export;

import com.cburch.logisim.plugin3d.core.SceneManager;
import com.cburch.logisim.plugin3d.core.Object3D;
import java.util.List;

/**
 * Exporteur XML pour les modèles 3D.
 */
public class XMLExporter {
    
    private final SceneManager sceneManager;
    
    public XMLExporter(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Exporte la scène en XML.
     */
    public String export(SceneManager scene) {
        StringBuilder xml = new StringBuilder();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<logisim3d schema_version=\"1.0.0\">\n");
        xml.append("  <circuit>\n");
        
        List<Object3D> objects = scene.getAllObjects();
        for (Object3D obj : objects) {
            xml.append("    <component id=\"").append(obj.getId()).append("\"");
            xml.append(" type=\"").append(obj.getType()).append("\">\n");
            
            float[] pos = obj.getPosition();
            xml.append("      <position x=\"").append(pos[0]);
            xml.append("\" y=\"").append(pos[1]);
            xml.append("\" z=\"").append(pos[2]).append("\"/>\n");
            
            float[] col = obj.getColor();
            xml.append("      <color r=\"").append(col[0]);
            xml.append("\" g=\"").append(col[1]);
            xml.append("\" b=\"").append(col[2]);
            xml.append("\" a=\"").append(col[3]).append("\"/>\n");
            
            xml.append("      <state signal_value=\"").append(obj.getSignalValue());
            xml.append("\" bit_width=\"").append(obj.getBitWidth()).append("\"/>\n");
            
            xml.append("    </component>\n");
        }
        
        xml.append("  </circuit>\n");
        xml.append("</logisim3d>\n");
        
        return xml.toString();
    }
}
