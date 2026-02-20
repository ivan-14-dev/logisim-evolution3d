package com.cburch.logisim.plugin3d.core;

/**
 * Classe de base pour tous les objets 3D dans la scène.
 * Représente un composant du circuit en 3D.
 */
public class Object3D {
    
    private String id;
    private String type;
    private float[] position = {0, 0, 0};
    private float[] rotation = {0, 0, 0};
    private float[] scale = {1, 1, 1};
    private float[] transformMatrix = new float[16];
    private float[] color = {0.5f, 0.5f, 0.5f, 1.0f};
    
    // État du signal
    private int signalValue = 0;
    private int bitWidth = 1;
    private boolean isHighlighted = false;
    
    // LOD
    private LODManager.LODLevel lodLevel = LODManager.LODLevel.HIGH;
    
    // Données du composant Logisim
    private Object componentData;
    
    public Object3D(String id, String type) {
        this.id = id;
        this.type = type;
        initTransformMatrix();
    }
    
    private void initTransformMatrix() {
        // Matrice identité
        transformMatrix = new float[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        };
    }
    
    // Getters et setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public float[] getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y, float z) {
        this.position[0] = x;
        this.position[1] = y;
        this.position[2] = z;
        updateTransformMatrix();
    }
    
    public float[] getRotation() {
        return rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        this.rotation[0] = x;
        this.rotation[1] = y;
        this.rotation[2] = z;
        updateTransformMatrix();
    }
    
    public float[] getScale() {
        return scale;
    }
    
    public void setScale(float x, float y, float z) {
        this.scale[0] = x;
        this.scale[1] = y;
        this.scale[2] = z;
        updateTransformMatrix();
    }
    
    public float[] getTransformMatrix() {
        return transformMatrix;
    }
    
    public void setTransformMatrix(float[] matrix) {
        if (matrix != null && matrix.length == 16) {
            this.transformMatrix = matrix;
        }
    }
    
    public float[] getColor() {
        return color;
    }
    
    public void setColor(float r, float g, float b, float a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
    }
    
    public int getSignalValue() {
        return signalValue;
    }
    
    public void setSignalValue(int signalValue) {
        this.signalValue = signalValue;
    }
    
    public int getBitWidth() {
        return bitWidth;
    }
    
    public void setBitWidth(int bitWidth) {
        this.bitWidth = bitWidth;
    }
    
    public boolean isHighlighted() {
        return isHighlighted;
    }
    
    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
    
    public LODManager.LODLevel getLODLevel() {
        return lodLevel;
    }
    
    public void setLODLevel(LODManager.LODLevel level) {
        this.lodLevel = level;
    }
    
    public Object getComponentData() {
        return componentData;
    }
    
    public void setComponentData(Object componentData) {
        this.componentData = componentData;
    }
    
    /**
     * Met à jour la matrice de transformation.
     */
    private void updateTransformMatrix() {
        // Simplification: matrice de transformation directe
        // Dans une implémentation réelle, utiliser Matrix4f de JMonkey
        transformMatrix[0] = scale[0];
        transformMatrix[5] = scale[1];
        transformMatrix[10] = scale[2];
        transformMatrix[12] = position[0];
        transformMatrix[13] = position[1];
        transformMatrix[14] = position[2];
    }
    
    /**
     * Retourne une représentation JSON de l'objet.
     */
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"").append(id).append("\",");
        sb.append("\"type\":\"").append(type).append("\",");
        sb.append("\"position\":{");
        sb.append("\"x\":").append(position[0]).append(",");
        sb.append("\"y\":").append(position[1]).append(",");
        sb.append("\"z\":").append(position[2]);
        sb.append("},");
        sb.append("\"color\":{");
        sb.append("\"r\":").append(color[0]).append(",");
        sb.append("\"g\":").append(color[1]).append(",");
        sb.append("\"b\":").append(color[2]).append(",");
        sb.append("\"a\":").append(color[3]);
        sb.append("},");
        sb.append("\"state\":{");
        sb.append("\"signal_value\":").append(signalValue).append(",");
        sb.append("\"bit_width\":").append(bitWidth);
        sb.append("}");
        sb.append("}");
        return sb.toString();
    }
}
