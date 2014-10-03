package view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import model.ConfigFilesIntegrityChecker;
import model.PreferencesManager;
import model.db.DBLink;
import model.db.auth.AuthenticationService;
import model.db.auth.User;

/**
 * <h1>Ventana de autentificación</h1>
 */
public class AuthenticationFrame {

	/** Botón aceptar. */
	private JButton btnAccept;

	/** Botón cancelar. */
	private JButton btnCancel;

	/** Frame. */
	private JFrame frame;

	/** Campo de texto para la contraseña. */
	private JPasswordField passwordField;

	/** Campo de texto para el nombre de usuario. */
	private JTextField tfUsername;

	/** Cantidad de intentos fallidos de autentificación. */
	private int tryCount;

	/**
	 * Constructor.
	 */
	public AuthenticationFrame() {
		initialize();
		tryCount = 0;
	}

	/**
	 * Punto de entrada al programa.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
				}

				try {
					final AuthenticationFrame window = new AuthenticationFrame();

					window.btnAccept.setEnabled(false);
					window.btnCancel.setEnabled(false);
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							window.checkConfigFilesIntegrity();
							window.checkDatabaseConnectivity();

							window.btnAccept.setEnabled(true);
							window.btnCancel.setEnabled(true);
						}
					});

					thread.start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Chequea la presencia de todos los archivos de configuración requeridos,
	 * de no existir los crea con valores por defecto.
	 */
	private void checkConfigFilesIntegrity() {
		ConfigFilesIntegrityChecker cfic = new ConfigFilesIntegrityChecker();
		cfic.checkAll();
	}

	/**
	 * Chequea la disponibilidad de conexión con la base de datos. De no poderse
	 * establecer satisfactoriamente una conexión se muestra la ventana de
	 * configuración de conexión en modo imperativo para que el usuario
	 * introduzca los parámetros correctamente.
	 */
	private void checkDatabaseConnectivity() {

		Connection c = DBLink.getInstance().getConnection();
		if (null == c) {
			JOptionPane.showMessageDialog(frame,
					I18N.getInstance().getString(I18N.CONNECTIVITY_ERROR), I18N
							.getInstance().getString(I18N.ERROR),
					JOptionPane.ERROR_MESSAGE);

			new ConnectionSettingsDialog(frame, true);
		} else {
			AuthenticationService auth = new AuthenticationService();
			auth.checkIntegrity();
		}
	}

	/**
	 * Inicia el contenido del frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setType(Type.POPUP);
		frame.setResizable(false);
		frame.setBounds(100, 100, 359, 238);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle(I18N.getInstance().getString(I18N.AUTHENTICATION));
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource(ThemeManager.getInstance()
						.getImage(ThemeManager.ICON))));

		tfUsername = new JTextField();
		tfUsername.setColumns(10);
		tfUsername.setBounds(90, 89, 253, 20);
		tfUsername.setText(PreferencesManager.getInstance()
				.getLastLoggedUsername());
		frame.getContentPane().add(tfUsername);

		JLabel label = new JLabel(I18N.getInstance().getString(I18N.USERNAME));
		label.setBounds(10, 92, 46, 14);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel(I18N.getInstance().getString(I18N.PASSWORD));
		label_1.setBounds(10, 123, 65, 14);
		frame.getContentPane().add(label_1);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 354, 67);
		frame.getContentPane().add(panel);

		JLabel label_2 = new JLabel("");
		label_2.setIcon(new ImageIcon(AuthenticationFrame.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.AUTHENTICATION_BANNER))));
		label_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		label_2.setBounds(0, 0, 354, 67);
		panel.add(label_2);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.controlShadow);
		panel_1.setBounds(0, 67, 400, 1);
		frame.getContentPane().add(panel_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.controlShadow);
		panel_2.setBounds(0, 167, 400, 1);
		frame.getContentPane().add(panel_2);

		btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		btnCancel.setBounds(241, 176, 89, 23);
		frame.getContentPane().add(btnCancel);

		btnAccept = new JButton(I18N.getInstance().getString(I18N.ACCEPT));
		btnAccept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				login();
			}
		});
		btnAccept.setBounds(142, 176, 89, 23);
		frame.getRootPane().setDefaultButton(btnAccept);
		frame.getContentPane().add(btnAccept);

		passwordField = new JPasswordField();
		passwordField.setBounds(90, 120, 253, 20);
		frame.getContentPane().add(passwordField);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Efectua el proceso de autentificación.
	 * Si el proceso falla más de tres veces la ventana
	 * se cierra.
	 */
	@SuppressWarnings("deprecation")
	private void login() {
		String username = tfUsername.getText();
		String password = passwordField.getText();

		AuthenticationService auth = new AuthenticationService();
		User user = auth.getUserByName(username);

		if ((null != user) && user.validatePassword(password)) {
			frame.dispose();
			AuthenticationService.setLoggedUser(user);
			PreferencesManager.getInstance().setLastLoggedUsername(
					user.getUsername());
			PreferencesManager.getInstance().savePreferences();
			new MainWindow();
		} else {
			tryCount++;
			JOptionPane.showMessageDialog(null,
					I18N.getInstance().getString(I18N.ACCESS_DENIED), I18N
							.getInstance().getString(I18N.ERROR),
					JOptionPane.ERROR_MESSAGE);

			if (tryCount >= 3) {
				frame.dispose();
			}
		}
	}
}
