package view.builder;

import view.I18N;
import view.utils.GUIUtils;
import view.views.ParameterizeDialogConfig;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import model.PreferencesManager;
import javax.swing.JComboBox;
import model.db.entities.FieldType;
import model.views.Parameter;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

/**
 * Diálogo de construcción de parámetro.
 */
public class ParameterDialog {

	/** ComboBox para el tipo de dato. */
	private JComboBox<FieldType> cbxType;
	
	/** Diálogo. */
	private JDialog dialog;
	
	/** Modo edición. */
	private boolean editMode;
	
	/** Modelo del ComboBox de entidades. */
	private DefaultComboBoxModel<String> entityModel;
	
	/** Parámetro. */
	private Parameter parameter;
	
	/** Configuración de parametrizador. */
	private ParameterizeDialogConfig pdc;
	
	/** Diálogo de contrucción de parametrizadores. */
	private PDCBuilderDialog pdcBuilder;
	
	/** Campo de texto para la dependencia. */
	private JTextField tfDependency;
	
	/** Campo de texto para el nombre. */
	private JTextField tfName;
	
	/** Panel de texto para la consulta. */
	private JTextPane tpQuery;
	
	/**
	 * Constructor.
	 * 
	 * @param pdc Configuración del parametrizador.
	 * @param fieldIndex Índice del parámetro en el parametrizador, en caso de ser -1
	 * 						se crea la ventana en modo inserción.
	 * @param pdcBuilder Diálogo de contrucción de parametrizadores.
	 */
	public ParameterDialog(ParameterizeDialogConfig pdc, int fieldIndex, PDCBuilderDialog pdcBuilder) {
		if(pdc != null && pdc.getParams().size() > fieldIndex && fieldIndex > -1) 
			parameter = pdc.getParams().get(fieldIndex);
		
		this.pdc = pdc;
		editMode = (fieldIndex != -1);
		
		if(!editMode) {
			parameter = new Parameter();
		}
		this.pdcBuilder = pdcBuilder;
		initialize(null);		
	}
	
	/**
	 * Carga los datos del parámetro
	 * si se encuentra en modo edición.
	 */
	private void load() {
		if(isEditMode()) {
			tfName.setText(parameter.getName());
			tfDependency.setText(parameter.getDependency());
			cbxType.setSelectedItem(parameter.getType());
			tpQuery.setText(parameter.getQuery());
		}
	}
	
	/**
	 * Carga el ComboBox de entidades.
	 */
	private void loadEntityCombo() {
		entityModel.removeAllElements();
		
		File file = new File(PreferencesManager.getInstance().getEntitesPath());
		File[] files = file.listFiles();
		
		for (File currentFile : files) {
			entityModel.addElement(currentFile.getName());
		}
	}
	
	/**
	 * Guarda los datos insertados/editados.
	 */
	private void save() {
		if(validate()) {
			parameter.setName(tfName.getText());
			parameter.setDependency(tfDependency.getText());
			parameter.setQuery(tpQuery.getText());
			parameter.setType((FieldType) cbxType.getSelectedItem());
								
			if(!isEditMode()) {
				pdc.getParams().add(parameter);
			}
						
			pdcBuilder.load();
			pdcBuilder.setModified(true);
			dialog.dispose();
		}
	}

	/**
	 * Valida los componentes.
	 * @return true si la validación fue satisfactoria, 
	 * 			false si no.
	 */
	private boolean validate() {
		boolean b = true;
		b &= GUIUtils.checkTextField(tfName);

		if(!b) {
			JOptionPane.showMessageDialog(null, I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), 
					I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
		
		return b;
	}
	
	/**
	 * Inicia el contenido del frame.
	 * @param parent Ventana padre.
	 */
	protected void initialize(JFrame parent) {

		dialog = new JDialog(parent, true);
		dialog.setResizable(false);
		
		if(isEditMode())
			dialog.setTitle(I18N.getInstance().getString(I18N.EDIT) + " " +  pdc.getFilename() + ":" + parameter.getName());
		else
			dialog.setTitle(I18N.getInstance().getString(I18N.INSERT) + " " + I18N.getInstance().getString(I18N.PARAMETER));
		
		dialog.setBounds(50, 50, 371, 368);
		
		dialog.setLocationRelativeTo(null);
		dialog.getContentPane().setLayout(null);
		
		JPanel panelData = new JPanel();
		panelData.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelData.setBounds(10, 11, 343, 103);
		dialog.getContentPane().add(panelData);
		panelData.setLayout(null);
		
		JLabel lblName = new JLabel(I18N.getInstance().getString(I18N.FIELD_NAME));
		lblName.setBounds(10, 11, 142, 14);
		panelData.add(lblName);
		
		tfName = new JTextField();
		tfName.setBounds(10, 28, 142, 20);
		panelData.add(tfName);
		tfName.setColumns(10);
		
		tfDependency = new JTextField();
		tfDependency.setColumns(10);
		tfDependency.setBounds(191, 71, 142, 20);
		panelData.add(tfDependency);
		
		JLabel lblDefaultValue = new JLabel(I18N.getInstance().getString(I18N.DEPENDENCY));
		lblDefaultValue.setBounds(191, 54, 142, 14);
		panelData.add(lblDefaultValue);
		
		JLabel lblType = new JLabel(I18N.getInstance().getString(I18N.TYPE));
		lblType.setBounds(10, 54, 75, 14);
		panelData.add(lblType);
		
		cbxType = new JComboBox<FieldType>();		
		cbxType.setModel(new DefaultComboBoxModel<FieldType>(FieldType.values()));
		cbxType.setBounds(10, 71, 142, 20);
		panelData.add(cbxType);
		
		JPanel panelQuery = new JPanel();
		panelQuery.setBorder(new TitledBorder(null, I18N.getInstance().getString(I18N.QUERY), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelQuery.setLayout(null);
		panelQuery.setBounds(10, 125, 343, 169);
		dialog.getContentPane().add(panelQuery);
		
		tpQuery = new JTextPane();
		tpQuery.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tpQuery.setBounds(21, 21, 300, 126);
		panelQuery.add(tpQuery);
		entityModel = new DefaultComboBoxModel<String>();
		loadEntityCombo();
		
		JButton btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		btnCancel.setBounds(264, 305, 89, 23);
		dialog.getContentPane().add(btnCancel);
		
		JButton btnAccept = new JButton(I18N.getInstance().getString(I18N.ACCEPT));
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		btnAccept.setBounds(165, 305, 89, 23);
		dialog.getContentPane().add(btnAccept);
		dialog.getRootPane().setDefaultButton(btnAccept);
		
		load();
		dialog.setVisible(true);
	}

	/**
	 * Indica si se encuentra en modo edición.
	 * @return true si se encuentra en modo edición,
	 * 			false si se encuentra en modo inserción.
	 */
	public boolean isEditMode() {
		return editMode;
	}
}
