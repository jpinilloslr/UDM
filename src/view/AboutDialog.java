package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * <h1>Diálogo Acerca de</h1>
 */
public class AboutDialog {

	/** Diálogo. */
	private JDialog dialog;

	/**
	 * Constructor.
	 * @param parent Padre.
	 */
	public AboutDialog(JFrame parent) {
		initialize(parent);
	}

	/**
	 * Inicia el contenido de la ventana.
	 * @param parent Ventana padre.
	 */
	private void initialize(JFrame parent) {
		dialog = new JDialog(parent, true);
		dialog.setResizable(false);
		dialog.setBounds(100, 100, 343, 199);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.getContentPane().setLayout(null);
		dialog.setTitle(I18N.getInstance().getString(I18N.ABOUT));

		JButton btnClose = new JButton(I18N.getInstance()
				.getString(I18N.ACCEPT));
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		btnClose.setBounds(230, 137, 89, 23);
		dialog.getContentPane().add(btnClose);

		JLabel lblNewLabel = new JLabel(I18N.getInstance().getString(
				I18N.getInstance().getString(I18N.UDM)));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(new Color(0, 191, 255));
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 25));
		lblNewLabel.setBounds(10, 11, 317, 32);
		dialog.getContentPane().add(lblNewLabel);

		JLabel lblDayana = new JLabel(
				"Dayana Pedroso Alfonso           G24  #20");
		lblDayana.setBounds(111, 69, 208, 14);
		dialog.getContentPane().add(lblDayana);

		JLabel lblJoaqunPinillosLa = new JLabel(
				"Joaqu\u00EDn Pinillos La Rosa             G24  #22");
		lblJoaqunPinillosLa.setBounds(111, 84, 208, 14);
		dialog.getContentPane().add(lblJoaqunPinillosLa);

		JLabel lblOmarTorresDomnguez = new JLabel(
				"Omar Torres Dom\u00EDnguez            G24  #31");
		lblOmarTorresDomnguez.setBounds(111, 99, 208, 14);
		dialog.getContentPane().add(lblOmarTorresDomnguez);

		JLabel icon = new JLabel("");
		icon.setIcon(new ImageIcon(AboutDialog.class.getResource(ThemeManager
				.getInstance().getImage(ThemeManager.INFO))));
		icon.setBounds(20, 63, 64, 57);
		dialog.getContentPane().add(icon);
		dialog.getRootPane().setDefaultButton(btnClose);

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.controlHighlight);
		panel.setBounds(10, 54, 317, 1);
		dialog.getContentPane().add(panel);

		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
}
