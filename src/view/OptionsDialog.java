package view;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;

import model.PreferencesManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;

/**
 * <h1>Diálogo de opciones</h1>
 */
public class OptionsDialog {

	/** ComboBox idioma. */
	private JComboBox<String> cbxLang;
	
	/** ComboBox tema. */
	private JComboBox<String> cbxTheme;
	
	/** CheckBox ocultar campos autoincrementables. */
	private JCheckBox chbxHideAutoinc;
	
	/** CheckBox sustituir llaves foráneas. */
	private JCheckBox chbxReplaceFkRef;
	
	/** Diálogo. */
	private JDialog dialog;
	
	/** Modelo del ComboBox de idiomas. */
	private DefaultComboBoxModel<String> langModel;
	
	/** Panel interfáz. */
	private JPanel panelInterface;
	
	/** Formato de fecha. */
	private JTextField tfDateFormat;
	
	/** Formato de fecha y hora. */
	private JTextField tfDateTimeFormat;
	
	/** Formato de hora. */
	private JTextField tfTimeFormat;

	/**
	 * Constructor.
	 * 
	 * @param parent Ventana padre.
	 */
	public OptionsDialog(JFrame parent) {
		initialize(parent);
	}
	
	/**
	 * Inicia el contenido del frame.
	 * @param parent Ventana padre.
	 */
	private void initialize(JFrame parent) {
		dialog = new JDialog(parent, true);
		dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(OptionsDialog.class.getResource(
									ThemeManager.getInstance().getImage(ThemeManager.OPTIONS_ICON))));
		dialog.setBounds(100, 100, 353, 377);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, I18N.getInstance().getString(I18N.ENTITIES), 
											TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 103, 317, 76);
		dialog.getContentPane().add(panel);
		panel.setLayout(null);
		
		chbxHideAutoinc = new JCheckBox(I18N.getInstance().getString(I18N.HIDE_AUTOINC_FIELDS));
		chbxHideAutoinc.setBounds(6, 18, 228, 23);
		panel.add(chbxHideAutoinc);
		
		chbxReplaceFkRef = new JCheckBox(I18N.getInstance().getString(I18N.REPLACE_FOREIGN_KEY_REFERENCE));
		chbxReplaceFkRef.setBounds(6, 44, 255, 23);
		panel.add(chbxReplaceFkRef);
		
		JButton btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		btnCancel.setBounds(238, 306, 89, 23);
		dialog.getContentPane().add(btnCancel);
		
		JButton btnAccept = new JButton(I18N.getInstance().getString(I18N.ACCEPT));
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveConfig();
			}
		});
		btnAccept.setBounds(139, 306, 89, 23);
		dialog.getContentPane().add(btnAccept);
		dialog.getRootPane().setDefaultButton(btnAccept);
		
		panelInterface = new JPanel();
		panelInterface.setBorder(new TitledBorder(null, I18N.getInstance().getString(I18N.INTERFACE), 
													TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelInterface.setLayout(null);
		panelInterface.setBounds(10, 11, 317, 81);
		dialog.getContentPane().add(panelInterface);
		
		cbxTheme = new JComboBox<String>();
		cbxTheme.setModel(new DefaultComboBoxModel<String>(new String[] {ThemeManager.DEFAULT_THEME, "crystal", "gnome", "olivia"}));
		cbxTheme.setBounds(87, 18, 206, 20);
		panelInterface.add(cbxTheme);
		
		JLabel lblTheme = new JLabel(I18N.getInstance().getString(I18N.THEME));
		lblTheme.setBounds(10, 21, 46, 14);
		panelInterface.add(lblTheme);
		
		JLabel label = new JLabel(I18N.getInstance().getString(I18N.LANGUAGE));
		label.setBounds(10, 52, 67, 14);
		panelInterface.add(label);
		
		cbxLang = new JComboBox<String>();
		cbxLang.setBounds(87, 49, 206, 20);
		langModel = new DefaultComboBoxModel<String>();
		cbxLang.setModel(langModel);
		panelInterface.add(cbxLang);
		
		JPanel panelDateAndTimeFormat = new JPanel();
		panelDateAndTimeFormat.setBorder(new TitledBorder(null, I18N.getInstance().getString(I18N.DATE_AND_TIME_FORMATS), 
															TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelDateAndTimeFormat.setBounds(10, 190, 317, 105);
		dialog.getContentPane().add(panelDateAndTimeFormat);
		panelDateAndTimeFormat.setLayout(null);
		
		JLabel lblTimeFormat = new JLabel(I18N.getInstance().getString(I18N.TIME_FORMAT));
		lblTimeFormat.setBounds(10, 23, 86, 14);
		panelDateAndTimeFormat.add(lblTimeFormat);
		
		tfTimeFormat = new JTextField();
		tfTimeFormat.setBounds(113, 20, 194, 20);
		panelDateAndTimeFormat.add(tfTimeFormat);
		tfTimeFormat.setColumns(10);
		
		JLabel lblDateFormat = new JLabel(I18N.getInstance().getString(I18N.DATE_FORMAT));
		lblDateFormat.setBounds(10, 49, 86, 14);
		panelDateAndTimeFormat.add(lblDateFormat);
		
		tfDateFormat = new JTextField();
		tfDateFormat.setColumns(10);
		tfDateFormat.setBounds(113, 46, 194, 20);
		panelDateAndTimeFormat.add(tfDateFormat);
		
		tfDateTimeFormat = new JTextField();
		tfDateTimeFormat.setColumns(10);
		tfDateTimeFormat.setBounds(113, 72, 194, 20);
		panelDateAndTimeFormat.add(tfDateTimeFormat);
		
		JLabel lblDateTimeFormat = new JLabel(I18N.getInstance().getString(I18N.DATETIME_FORMAT));
		lblDateTimeFormat.setBounds(10, 75, 86, 14);
		panelDateAndTimeFormat.add(lblDateTimeFormat);
		dialog.setTitle(I18N.getInstance().getString(I18N.OPTIONS));
		
		dialog.setLocationRelativeTo(null);
		loadAvailableLangs();
		loadConfig();
		dialog.setVisible(true);
	}
	
	/**
	 * Carga los archivos de idiomas disponibles.
	 */
	private void loadAvailableLangs() {
		File file = new File(PreferencesManager.getInstance().getLangPath());
		File[] files = file.listFiles();
		langModel.removeAllElements();
		
		for (File currentFile : files) {
			String lang = currentFile.getName();
			if(lang.contains("."))
				lang = lang.substring(0, lang.lastIndexOf("."));
			
			langModel.addElement(lang);
		}
	}
	
	/**
	 * Carga configuración.
	 */
	private void loadConfig() {
		PreferencesManager pm = PreferencesManager.getInstance();
		
		chbxHideAutoinc.setSelected(pm.hideAutoincrementalFields());
		chbxReplaceFkRef.setSelected(pm.replaceForeignKeyValues());
		cbxTheme.setSelectedItem(pm.getThemeName());
		cbxLang.setSelectedItem(pm.getLanguage());
		
		tfDateFormat.setText(pm.getDateFormat());
		tfTimeFormat.setText(pm.getTimeFormat());
		tfDateTimeFormat.setText(pm.getDateTimeFormat());
	}

	/**
	 * Guarda configuración.
	 */
	private void saveConfig() {
		PreferencesManager pm = PreferencesManager.getInstance();
		
		pm.setHideAutoincrementalFields(chbxHideAutoinc.isSelected());
		pm.setReplaceForeignKeyValues(chbxReplaceFkRef.isSelected());
		pm.setTimeFormat(tfTimeFormat.getText());
		pm.setDateFormat(tfDateFormat.getText());
		pm.setDateTimeFormat(tfDateTimeFormat.getText());
		
		if(!pm.getThemeName().equals((String) cbxTheme.getSelectedItem())) {
			pm.setThemeName((String) cbxTheme.getSelectedItem());	
			JOptionPane.showMessageDialog(dialog, I18N.getInstance().getString(I18N.SHOULD_RESTART_MESSAGE), I18N.getInstance().getString(I18N.OPTIONS), JOptionPane.INFORMATION_MESSAGE);
		}
		
		if(!pm.getLanguage().equals((String) cbxLang.getSelectedItem())) {
			pm.setLanguage((String) cbxLang.getSelectedItem());	
			JOptionPane.showMessageDialog(dialog, I18N.getInstance().getString(I18N.SHOULD_RESTART_MESSAGE), I18N.getInstance().getString(I18N.OPTIONS), JOptionPane.INFORMATION_MESSAGE);
		}
	
		pm.savePreferences();
		dialog.dispose();
	}
}
