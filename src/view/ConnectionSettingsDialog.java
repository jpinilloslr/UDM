package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.db.DBLink;
import model.db.auth.AuthenticationService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import view.utils.GUIUtils;

/**
 * <h1>Diálogo de configuración de conexión a la base de datos</h1>
 */
public class ConnectionSettingsDialog {

	/** Botón guardar. */
	private JButton btnSave;
	
	/** Botón test. */
	private JButton btnTest;	
	
	/** Diálogo. */
	private JDialog dialog;
	
	/** Modo imperativo. */
	private boolean imperativeMode;
	
	/** Campo de texto para la contraseña. */
	private JPasswordField passwordField;
	
	/** Campo de texto para el nombre de la base de datos. */
	private JTextField tfDatabase;
	
	/** Campo de texto para el host. */
	private JTextField tfHost;
	
	/** Campo de texto para el puerto. */
	private JTextField tfPort;
	
	/** Campo de texto para en nombre de usuario. */
	private JTextField tfUsername;

	/**
	 * Constructor.
	 * @param parent Ventana padre.
	 */
	public ConnectionSettingsDialog(JFrame parent) {
		imperativeMode = false;
		initialize(parent);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent Ventana padre.
	 * @param imperativeMode Modo imperativo. Si está activado no permite 
	 * 							acceder a la ventana padre a menos que se 
	 * 							consiga una conexión satisfactoria. 
	 */
	public ConnectionSettingsDialog(JFrame parent, boolean imperativeMode) {
		this.imperativeMode = imperativeMode;
		initialize(parent);
	}

	/**
	 * Si se encuentra en modo imperativo y no se ha podido 
	 * establecer conexión satisfactoria con la base de datos 
	 * cierra la ventana padre.
	 * 
	 * @param parent Ventana padre.
	 */
	private void checkImperativeMode(JFrame parent) {
		Connection c = DBLink.getInstance().getConnection();

		if (imperativeMode && (null == c)) {
			parent.dispose();
		}
	}
	
	/**
	 * Deshabilita el botón guardar al 
	 * cambiar el texto del TextField.
	 * 
	 * @param textField TextField.
	 */
	private void disableSaveOnChangeEvent(JTextField textField) {
		textField.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent arg0) {
				btnSave.setEnabled(false);
				dialog.getRootPane().setDefaultButton(btnTest);
			}
			
			public void insertUpdate(DocumentEvent arg0) {
				btnSave.setEnabled(false);
				dialog.getRootPane().setDefaultButton(btnTest);
			}
			
			public void changedUpdate(DocumentEvent arg0) {
				btnSave.setEnabled(false);
				dialog.getRootPane().setDefaultButton(btnTest);
			}
		});
	}

	/**
	 * Inicia el contenido del frame.
	 * @param parent Ventana padre.
	 */
	private void initialize(final JFrame parent) {
		dialog = new JDialog(parent, true);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				checkImperativeMode(parent);
			}
		});
		dialog.setResizable(false);
		dialog.setBounds(100, 100, 346, 353);
		dialog.setTitle(I18N.getInstance().getString(I18N.CONNECTION_SETTINGS));
		dialog.getContentPane().setLayout(null);

		tfUsername = new JTextField();
		tfUsername.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				btnSave.setEnabled(false);
			}
		});
		tfUsername.setBounds(90, 209, 240, 20);
		dialog.getContentPane().add(tfUsername);
		tfUsername.setColumns(10);

		JLabel lblUsername = new JLabel(I18N.getInstance().getString(
				I18N.USERNAME));
		lblUsername.setBounds(10, 212, 46, 14);
		dialog.getContentPane().add(lblUsername);

		JLabel lblPassword = new JLabel(I18N.getInstance().getString(
				I18N.PASSWORD));
		lblPassword.setBounds(10, 243, 65, 14);
		dialog.getContentPane().add(lblPassword);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 354, 67);
		dialog.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblTitle = new JLabel("");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTitle.setIcon(new ImageIcon(ConnectionSettingsDialog.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.DATABASE_BANNER))));
		lblTitle.setBounds(0, 0, 344, 67);
		panel.add(lblTitle);

		JPanel sep1 = new JPanel();
		sep1.setBackground(SystemColor.controlShadow);
		sep1.setBounds(0, 67, 400, 1);
		dialog.getContentPane().add(sep1);

		JPanel sep2 = new JPanel();
		sep2.setBackground(SystemColor.controlShadow);
		sep2.setBounds(0, 287, 400, 1);
		dialog.getContentPane().add(sep2);

		JButton btnCancel = new JButton(I18N.getInstance().getString(
				I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DBLink.getInstance().loadConfig();
				DBLink.getInstance().initialize();
				checkImperativeMode(parent);
				dialog.dispose();
			}
		});
		btnCancel.setBounds(241, 296, 89, 23);
		dialog.getContentPane().add(btnCancel);

		btnSave = new JButton(I18N.getInstance().getString(I18N.SAVE));
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveConnectionParams();
				DBLink.getInstance().loadConfig();
				DBLink.getInstance().initialize();

				AuthenticationService auth = new AuthenticationService();
				auth.checkIntegrity();
				dialog.dispose();
			}
		});
		btnSave.setEnabled(false);
		btnSave.setBounds(142, 296, 89, 23);
		dialog.getContentPane().add(btnSave);

		btnTest = new JButton(I18N.getInstance().getString(I18N.TEST));
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				test();
			}
		});
		btnTest.setBounds(43, 296, 89, 23);
		dialog.getContentPane().add(btnTest);

		passwordField = new JPasswordField();
		disableSaveOnChangeEvent(passwordField);
		
		passwordField.setBounds(90, 240, 240, 20);
		dialog.getContentPane().add(passwordField);

		JPanel panelHost = new JPanel();
		panelHost.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panelHost.setBounds(10, 78, 320, 67);
		dialog.getContentPane().add(panelHost);
		panelHost.setLayout(null);

		tfHost = new JTextField();
		disableSaveOnChangeEvent(tfHost);
		tfHost.setColumns(10);
		tfHost.setBounds(10, 25, 217, 20);
		panelHost.add(tfHost);

		JLabel lblHost = new JLabel(I18N.getInstance().getString(I18N.HOST));
		lblHost.setBounds(10, 11, 36, 14);
		panelHost.add(lblHost);

		tfPort = new JTextField("5432");
		disableSaveOnChangeEvent(tfPort);
		tfPort.setColumns(10);
		tfPort.setBounds(237, 25, 73, 20);
		GUIUtils.setTextFieldForNumbersOnly(tfPort, 5);
		panelHost.add(tfPort);

		JLabel lblPort = new JLabel(I18N.getInstance().getString(I18N.PORT));
		lblPort.setBounds(237, 11, 36, 14);
		panelHost.add(lblPort);

		JLabel label = new JLabel(I18N.getInstance().getString(I18N.DATABASE));
		label.setBounds(10, 159, 89, 14);
		dialog.getContentPane().add(label);

		tfDatabase = new JTextField();
		disableSaveOnChangeEvent(tfDatabase);
		tfDatabase.setColumns(10);
		tfDatabase.setBounds(90, 156, 240, 20);
		dialog.getContentPane().add(tfDatabase);
		dialog.getRootPane().setDefaultButton(btnTest);

		loadConfig();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	/**
	 * Carga la configuración de conexión a la base de datos.
	 */
	private void loadConfig() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new FileInputStream(DBLink.CONFIG_FILENAME));
		} catch (Exception exc) {
		}

		if (null != dom) {
			Element root = dom.getDocumentElement();

			String url = root.getAttribute("url").trim();
			String database = "";
			String host = "";
			String port = "";
			if (url.contains("/")) {
				database = url.substring(url.lastIndexOf("/") + 1);
			}

			if (url.contains("//")) {
				host = url.substring(url.indexOf("//") + 2);
				host = host.substring(0, host.lastIndexOf("/"));

				if (host.contains(":")) {
					port = host.substring(host.indexOf(":") + 1);
					host = host.substring(0, host.indexOf(":"));
				}
			}

			try {
				Integer.valueOf(port);
				tfPort.setText(port);
			} catch (Exception e) {
				port = "5432";
			}
			tfPort.setText(port);
			tfHost.setText(host);
			tfDatabase.setText(database);
			tfUsername.setText(root.getAttribute("username").trim());
			passwordField.setText(root.getAttribute("password").trim());
		}
	}

	/**
	 * Guarda la configuración de conexión a la base de datos.
	 */
	@SuppressWarnings("deprecation")
	private void saveConnectionParams() {
		String params = "";
		params = "<connection driver=\"org.postgresql.Driver\"\r\n"
				+ "url=\"jdbc:postgresql://" + tfHost.getText() + ":"
				+ tfPort.getText() + "/" + tfDatabase.getText() + "\"\r\n"
				+ "username=\"" + tfUsername.getText() + "\"\r\n"
				+ "password=\"" + passwordField.getText() + "\">\r\n"
				+ "</connection>";

		try {
			PrintWriter pw = new PrintWriter(DBLink.CONFIG_FILENAME, "UTF-8");
			pw.print(params);
			pw.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Realiza un test de conexión con los parámetros actuales, de realizarse
	 * satisfactoriamente habilita el botón de guardar.
	 */
	@SuppressWarnings("deprecation")
	private void test() {
		String url = "jdbc:postgresql://" + tfHost.getText() + ":"
				+ tfPort.getText() + "/" + tfDatabase.getText();
		DBLink link = DBLink.getInstance();
		link.setDriver("org.postgresql.Driver");
		link.setUrl(url);
		link.setUsername(tfUsername.getText());
		link.setPassword(passwordField.getText());

		link.initialize();
		
		if (null != link.getConnection()) {
			btnSave.setEnabled(true);
			dialog.getRootPane().setDefaultButton(btnSave);
			
			JOptionPane.showMessageDialog(null,
					I18N.getInstance().getString(I18N.CONNECTION_SUCCESS), I18N.getInstance().getString(I18N.UDM),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			btnSave.setEnabled(false);
			
			JOptionPane.showMessageDialog(null,
					I18N.getInstance().getString(I18N.CONNECTIVITY_ERROR), 
					I18N.getInstance().getString(I18N.ERROR), 
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
