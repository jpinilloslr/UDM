package view;

import java.awt.SystemColor;

import javax.swing.JInternalFrame;

/**
 * <h1>Ventana MDI abstracta</h1>
 * 
 * Todas las ventanas que hereden de esta tendrán
 * características MDI.
 */
public abstract class MDIFrame {
	
	/** Frame interno. */
	protected JInternalFrame frmManager;
	
	/**
	 * Constructor.
	 */
	public MDIFrame() {		
		initialize();
	}
	
	/**
	 * Inicia el contenido del frame.
	 */
	protected void initialize() {
		frmManager = new JInternalFrame("", true, true, true, false);
		frmManager.getContentPane().setBackground(SystemColor.control);
		frmManager.setResizable(true);
		frmManager.setBounds(100, 100, 756, 550);		
	}
	
	/**
	 * Devuelve el frame interno.
	 * @return Frame interno.
	 */
	public JInternalFrame getFrame() {
		return frmManager;
	}
}
