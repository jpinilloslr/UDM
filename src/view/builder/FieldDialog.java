package view.builder;

import view.I18N;
import view.utils.GUIUtils;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import model.PreferencesManager;
import model.db.entities.Entity;
import model.db.entities.Field;
import model.db.entities.ForeignKeyInfo;

import javax.swing.JComboBox;
import model.db.entities.FieldType;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;

/**
 * Diálogo de construcción de 
 * campos de una entidad.
 */
public class FieldDialog {

	/** ComboBox de entidades foráneas. */
	private JComboBox<String> cbxEntity;
	
	/** ComboBox de tipo. */
	private JComboBox<FieldType> cbxType;
	
	/** CheckBox valor autoincrementable. */
	private JCheckBox chckbxAutoinc;
	
	/** CheckBox marca de borrado lógico. */
	private JCheckBox chckbxDeletemark;
	
	/** CheckBox llave foránea. */
	private JCheckBox chckbxForeignkey;
	
	/** CheckBox llave primaria. */
	private JCheckBox chckbxPrimarykey;
	
	/** CheckBox requerido. */
	private JCheckBox chckbxRequired;
	
	/** Diálogo. */
	private JDialog dialog;
	
	/** Modo edición. */
	private boolean editMode;
	
	/** Diálogo constructor de entidades. */
	private EntityBuilderDialog entityBuilder;
	
	/** Modelo del ComboBox de entidades foráneas. */
	private DefaultComboBoxModel<String> entityModel;
	
	/** Campo. */
	private Field field;
	
	/** Label entidad. */
	private JLabel lblEntity;
	
	/** Label campo de referencia. */
	private JLabel lblReference;
	
	/** Label campo sustituto. */
	private JLabel lblSubstitute;
	
	/** Entidad modelo a la que pertenece el campo. */
	private Entity model;
	
	/** Campo de texto para el valor por defecto. */
	private JTextField tfDefaultValue;
	
	/** Campo de texto para el nombre. */
	private JTextField tfName;
	
	/** Campo de texto para el campo de referencia. */
	private JTextField tfReferenceField;

	/** Campo de texto para el campo sustituto. */
	private JTextField tfSubstituteField;
	
	
	/**
	 * Constructor. 
	 * 
	 * @param model Entidad modelo a la que pertenece el campo.
	 * @param fieldIndex Índice del campo en la entidad, si es -1 se crea
	 * 						la ventana en modo inserción, si es un índice
	 * 						válido se crea en modo edición.
	 * @param entityBuilder Diálogo constructor de entidades.
	 */
	public FieldDialog(Entity model, int fieldIndex, EntityBuilderDialog entityBuilder) {
		if(model != null && model.getFields().size() > fieldIndex && fieldIndex > -1) 
			field = model.getFields().get(fieldIndex);
		
		this.model = model;
		editMode = (fieldIndex != -1);
		
		if(!editMode) {
			field = new Field();
		}
		this.entityBuilder = entityBuilder;
		initialize(null);		
	}
	
	/**
	 * Carga el contenido del campo
	 * si está en modo edición.
	 */
	private void load() {
		if(isEditMode()) {
			chckbxAutoinc.setSelected(field.isAutoincremental());
			cbxType.setSelectedItem(field.getFieldType());
			chckbxDeletemark.setSelected(field.isLogicDeleteMarker());
			tfName.setText(field.getName());
			
			
			if(field.getFieldType() != FieldType.FT_DATETIME) {
				tfDefaultValue.setText(String.valueOf(field.getValue()));	
			} else {
				tfDefaultValue.setText("0");	
			}
			
			chckbxPrimarykey.setSelected(field.isPrimaryKey());
			chckbxRequired.setSelected(field.isRequired());
			
			if(field.getForeignKeyInfo() != null) {
				ForeignKeyInfo fkInfo = field.getForeignKeyInfo();
				
				chckbxForeignkey.setSelected(true);
				cbxEntity.setSelectedItem(fkInfo.getEntityXML());
				tfReferenceField.setText(fkInfo.getReferencedField());
				tfSubstituteField.setText(fkInfo.getSubstituteField());
				
				boolean b = true;				
				cbxEntity.setEnabled(b);
				lblEntity.setEnabled(b);
				tfReferenceField.setEnabled(b);
				lblReference.setEnabled(b);				
				tfSubstituteField.setEnabled(b);
				lblSubstitute.setEnabled(b);
			}
		}
	}
	
	/** 
	 * Carga la lista de entidades menos la del modelo en el ComboBox de 
	 * entidades foráneas.
	 */
	private void loadEntityCombo() {
		entityModel.removeAllElements();
		
		File file = new File(PreferencesManager.getInstance().getEntitesPath());
		File[] files = file.listFiles();
		
		for (File currentFile : files) {
			if(null != model.getTableName()) {
				String filename = currentFile.getName();
				
				if(filename.contains("."))
					filename = filename.substring(0, filename.lastIndexOf("."));
				
				if(!filename.equalsIgnoreCase(model.getTableName()))
					entityModel.addElement(currentFile.getName());
			} else {
				entityModel.addElement(currentFile.getName());
			}
		}
	}
	
	/**
	 * Guarda el campo.
	 */
	private void save() {
		if(validate()) {
			field.setAutoincremental(chckbxAutoinc.isSelected());
			field.setFieldType((FieldType) cbxType.getSelectedItem());
			field.setLogicDeleteMarker(chckbxDeletemark.isSelected());
			field.setName(tfName.getText());
			field.setPrimaryKey(chckbxPrimarykey.isSelected());
			field.setRequired(chckbxRequired.isSelected() || chckbxPrimarykey.isSelected());
			
			switch ((FieldType) cbxType.getSelectedItem()) {
			case FT_BOOLEAN:
				if(tfDefaultValue.getText().equalsIgnoreCase("true"))
					field.setValue(true);
				else
					field.setValue(false);
				break;
			case FT_INT:
				try {
					field.setValue(Integer.valueOf(tfDefaultValue.getText()));
				} catch(Exception e) {
					field.setValue(0);
				}
				break;				
			case FT_DOUBLE:
				try {
					field.setValue(Float.valueOf(tfDefaultValue.getText()));
				} catch(Exception e) {
					field.setValue(0.0f);
				}
				break;
			case FT_DATETIME:
					field.setValue(0);
				break;
			default:
				field.setValue(tfDefaultValue.getText());
				break;
			}
			
			if(chckbxForeignkey.isSelected()) {
				ForeignKeyInfo fkInfo = new ForeignKeyInfo();
				fkInfo.setEntityXML((String) cbxEntity.getSelectedItem());
				fkInfo.setReferencedField(tfReferenceField.getText());
				fkInfo.setSubstituteField(tfSubstituteField.getText());
				field.setForeignKeyInfo(fkInfo);
			} else {
				field.setForeignKeyInfo(null);
			}
			
			if(!isEditMode()) {
				model.getFields().add(field);
			}
						
			entityBuilder.load();
			entityBuilder.setModified(true);
			dialog.dispose();
		}
	}

	/**
	 * Valida los componentes.
	 * @return true si la validación es satisfactoria, false
	 * 			si no.
	 */
	private boolean validate() {
		boolean b = true;
		b &= GUIUtils.checkTextField(tfName);
		b &= GUIUtils.checkTextField(tfReferenceField);
		b &= GUIUtils.checkTextField(tfSubstituteField);

		if(!b) {
			JOptionPane.showMessageDialog(null, I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
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
			dialog.setTitle(I18N.getInstance().getString(I18N.EDIT) + " " +  model.getTableName() + ":" + field.getName());
		else
			dialog.setTitle(I18N.getInstance().getString(I18N.INSERT) + " " + I18N.getInstance().getString(I18N.FIELD));
		
		dialog.setBounds(50, 50, 371, 368);
		
		dialog.setLocationRelativeTo(null);
		dialog.getContentPane().setLayout(null);
		
		JPanel panelEntity = new JPanel();
		panelEntity.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEntity.setBounds(10, 11, 343, 159);
		dialog.getContentPane().add(panelEntity);
		panelEntity.setLayout(null);
		
		JLabel lblName = new JLabel(I18N.getInstance().getString(I18N.NAME));
		lblName.setBounds(10, 11, 75, 14);
		panelEntity.add(lblName);
		
		tfName = new JTextField();
		tfName.setBounds(10, 28, 142, 20);
		panelEntity.add(tfName);
		tfName.setColumns(10);
		
		tfDefaultValue = new JTextField();
		tfDefaultValue.setColumns(10);
		tfDefaultValue.setBounds(10, 76, 142, 20);
		panelEntity.add(tfDefaultValue);
		
		JLabel lblDefaultValue = new JLabel(I18N.getInstance().getString(I18N.DEFAULT_VALUE));
		lblDefaultValue.setBounds(10, 59, 142, 14);
		panelEntity.add(lblDefaultValue);
		
		JLabel lblType = new JLabel(I18N.getInstance().getString(I18N.TYPE));
		lblType.setBounds(10, 107, 75, 14);
		panelEntity.add(lblType);
		
		cbxType = new JComboBox<FieldType>();
		cbxType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FieldType c = (FieldType) cbxType.getSelectedItem();
				
				if(c == FieldType.FT_BOOLEAN)
					chckbxDeletemark.setEnabled(true);
				else {
					chckbxDeletemark.setEnabled(false);
					chckbxDeletemark.setSelected(false);
				}
				
				if(c == FieldType.FT_INT) {
					chckbxForeignkey.setEnabled(true);
					chckbxAutoinc.setEnabled(true);
				} else {
					chckbxForeignkey.setEnabled(false);
					chckbxForeignkey.setSelected(false);
					
					chckbxAutoinc.setEnabled(false);
					chckbxAutoinc.setSelected(false);
				}
			}
		});
		cbxType.setModel(new DefaultComboBoxModel<FieldType>(FieldType.values()));
		cbxType.setBounds(10, 124, 142, 20);
		panelEntity.add(cbxType);
		
		chckbxPrimarykey = new JCheckBox(I18N.getInstance().getString(I18N.PRIMARY_KEY));
		chckbxPrimarykey.setBounds(183, 27, 154, 23);
		panelEntity.add(chckbxPrimarykey);
		
		chckbxAutoinc = new JCheckBox(I18N.getInstance().getString(I18N.AUTOINC));
		chckbxAutoinc.setEnabled(false);
		chckbxAutoinc.setBounds(183, 53, 154, 23);
		panelEntity.add(chckbxAutoinc);
		
		chckbxDeletemark = new JCheckBox(I18N.getInstance().getString(I18N.LOGIC_DELETE_MARK));		
		chckbxDeletemark.setEnabled(false);
		chckbxDeletemark.setBounds(183, 106, 154, 23);
		panelEntity.add(chckbxDeletemark);
		
		chckbxRequired = new JCheckBox(I18N.getInstance().getString(I18N.REQUIRED));
		chckbxRequired.setBounds(183, 80, 154, 23);
		panelEntity.add(chckbxRequired);
		
		JPanel panelForeignKey = new JPanel();
		panelForeignKey.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelForeignKey.setLayout(null);
		panelForeignKey.setBounds(10, 181, 343, 109);
		dialog.getContentPane().add(panelForeignKey);
		
		lblSubstitute = new JLabel(I18N.getInstance().getString(I18N.SUBSTITUTE_FIELD));
		lblSubstitute.setEnabled(false);
		lblSubstitute.setBounds(191, 59, 142, 14);
		panelForeignKey.add(lblSubstitute);
		
		tfSubstituteField = new JTextField();
		tfSubstituteField.setEnabled(false);
		tfSubstituteField.setColumns(10);
		tfSubstituteField.setBounds(191, 76, 142, 20);
		panelForeignKey.add(tfSubstituteField);
		
		lblEntity = new JLabel(I18N.getInstance().getString(I18N.ENTITY));
		lblEntity.setEnabled(false);
		lblEntity.setBounds(10, 59, 142, 14);
		panelForeignKey.add(lblEntity);
		
		lblReference = new JLabel(I18N.getInstance().getString(I18N.REFERENCE_FIELD));
		lblReference.setEnabled(false);
		lblReference.setBounds(191, 11, 142, 14);
		panelForeignKey.add(lblReference);
		
		tfReferenceField = new JTextField();
		tfReferenceField.setEnabled(false);
		tfReferenceField.setColumns(10);
		tfReferenceField.setBounds(191, 28, 142, 20);
		panelForeignKey.add(tfReferenceField);
		
		chckbxForeignkey = new JCheckBox(I18N.getInstance().getString(I18N.FOREIGN_KEY));
		chckbxForeignkey.setEnabled(false);
		chckbxForeignkey.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				boolean b = chckbxForeignkey.isSelected();
					
				cbxEntity.setEnabled(b);
				lblEntity.setEnabled(b);
				tfReferenceField.setEnabled(b);
				lblReference.setEnabled(b);				
				tfSubstituteField.setEnabled(b);
				lblSubstitute.setEnabled(b);
			}
		});
		chckbxForeignkey.setBounds(10, 27, 97, 23);
		panelForeignKey.add(chckbxForeignkey);
		
		cbxEntity = new JComboBox<String>();
		cbxEntity.setEnabled(false);
		entityModel = new DefaultComboBoxModel<String>();
		cbxEntity.setModel(entityModel);
		cbxEntity.setBounds(10, 76, 142, 20);
		loadEntityCombo();
		panelForeignKey.add(cbxEntity);
		
		JButton btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		btnCancel.setBounds(266, 301, 89, 23);
		dialog.getContentPane().add(btnCancel);
		
		JButton btnAccept = new JButton(I18N.getInstance().getString(I18N.ACCEPT));
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		btnAccept.setBounds(167, 301, 89, 23);
		dialog.getContentPane().add(btnAccept);
		dialog.getRootPane().setDefaultButton(btnAccept);
		
		load();
		dialog.setVisible(true);
	}

	/**
	 * Indica si se encuentra en modo edición.
	 * @return true si se encuentra en modo edición,	
	 * 			false si no.
	 */
	public boolean isEditMode() {
		return editMode;
	}
}
