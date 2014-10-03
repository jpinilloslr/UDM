package view.utils;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 *  <h1>Utilidad para GUI</h1>
 */
public class GUIUtils {
	
	/**
	 * Define un filtro de entrada de sólo números para un JTextField.
	 * @param tf JTextField.
	 * @param maxLength Longitud máxima de la entrada.
	 */
	public static void setTextFieldForNumbersOnly(final JTextField tf, final int maxLength) {
		tf.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent ke) {
				int kc = ke.getKeyChar(); 
				if(kc < '0' || kc > '9' || tf.getText().length() > maxLength) {
					ke.consume();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			
			@Override
			public void keyPressed(KeyEvent ke) {								
			}
		});
	}
	
	/**
	 * Define un filtro de entrada en un dominio para un JTextField.
	 * @param tf JTextField.
	 * @param domain Conjunto de caracteres admisibles.
	 */
	public static void setTextFieldDomain(final JTextField tf, final String domain) {
		tf.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent ke) {
				char kc = ke.getKeyChar();
				
				if(!domain.contains(String.valueOf(kc))) {
					ke.consume();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			
			@Override
			public void keyPressed(KeyEvent ke) {								
			}
		});
	}
	
	/**
	 * Chequea si un JTextField está habilitado y no 
	 * está en blanco.
	 * @param tf JTextField.
	 * @return true si está habilitado y está en blanco, false si no.
	 */
	public static boolean checkTextField(JTextField tf) {
		boolean success = true;
		if(tf.isEnabled())
			success = tf.getText().length() > 0;

		return success;
	}
	
	/**
	 * Asigna un menú contextual a un componente.
	 * @param component Componente.
	 * @param popup Menú contextual.
	 */
	public static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
		});
	}
}
