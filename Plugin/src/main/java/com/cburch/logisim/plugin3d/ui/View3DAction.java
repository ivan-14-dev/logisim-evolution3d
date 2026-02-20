package com.cburch.logisim.plugin3d.ui;

import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.AbstractTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action pour afficher/masquer la vue 3D.
 * Cette action peut être ajoutée au menu de Logisim Evolution.
 */
public class View3DAction extends AbstractTool {
    
    private static final String NAME = "Vue 3D";
    private static final String TOOLTIP = "Afficher la vue 3D du circuit";
    
    private Project project;
    private Logisim3DPanel panel3D;
    
    public View3DAction(Project project) {
        this.project = project;
    }
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String getToolTip() {
        return TOOLTIP;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        toggle3DView();
    }
    
    /**
     * Bascule l'affichage du panneau 3D.
     */
    public void toggle3DView() {
        // Chercher la fenêtre principale
        Frame frame = JOptionPane.getFrameForComponent(null);
        
        // Si le panneau n'existe pas, le créer
        if (panel3D == null) {
            panel3D = new Logisim3DPanel(project);
        }
        
        // Chercher un onglet ou split pane existant
        // Pour simplifier, on ouvre dans une nouvelle fenêtre
        showInNewWindow();
    }
    
    /**
     * Affiche la vue 3D dans une nouvelle fenêtre.
     */
    private void showInNewWindow() {
        JFrame frame = new JFrame("Logisim Evolution - Vue 3D");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Créer le panneau 3D
        if (panel3D == null) {
            panel3D = new Logisim3DPanel(project);
        }
        
        frame.add(panel3D);
        
        // Centrer sur l'écran
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
            (screen.width - frame.getWidth()) / 2,
            (screen.height - frame.getHeight()) / 2
        );
        
        frame.setVisible(true);
        
        // Rafraîchir la scène
        panel3D.refreshScene();
    }
    
    /**
     * Retourne le panneau 3D.
     */
    public Logisim3DPanel getPanel3D() {
        return panel3D;
    }
    
    /**
     * Met à jour l'état de la simulation dans la vue 3D.
     */
    public void updateSimulationState() {
        if (panel3D != null) {
            panel3D.updateSimulationState();
        }
    }
}

/**
 * Classe d'action simple pour le menu.
 */
class View3DMenuAction extends AbstractAction {
    
    private Project project;
    
    public View3DMenuAction(Project project) {
        super("Vue 3D du Circuit");
        this.project = project;
        
        // Icône optionnelle
        putValue(Action.SHORT_DESCRIPTION, "Afficher le circuit en 3D");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
            KeyEvent.VK_3, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        open3DView();
    }
    
    private void open3DView() {
        // Créer et afficher la fenêtre 3D
        JFrame frame = new JFrame("Logisim Evolution - Vue 3D");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1024, 768);
        
        Logisim3DPanel panel = new Logisim3DPanel(project);
        frame.add(panel);
        
        // Centrer
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
            (screen.width - frame.getWidth()) / 2,
            (screen.height - frame.getHeight()) / 2
        );
        
        frame.setVisible(true);
        
        // Initialiser la scène
        panel.refreshScene();
    }
}

/**
 * Bouton de la barre d'outils pour la vue 3D.
 */
class View3DToolbarButton extends JButton {
    
    private static final ImageIcon ICON_3D = create3DIcon();
    
    public View3DToolbarButton(Action action) {
        super(action);
        setIcon(ICON_3D);
        setToolTipText((String) action.getValue(Action.SHORT_DESCRIPTION));
        setFocusable(false);
    }
    
    private static ImageIcon create3DIcon() {
        // Créer une icône simple 3D
        String iconText = "3D";
        return new ImageIcon();
    }
}
