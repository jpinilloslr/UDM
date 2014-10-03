package view.builder;

import view.I18N;
import view.utils.GUIUtils;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import model.PreferencesManager;
import model.managers.ManagerInfo;

import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;

/**
 * Diálogo de construcción de la información
 * del gestor.
 */
public class ManagerInfoDialog {
	
	/** Entidad asociada al gestor. */
	private JComboBox<String> cbxEntity;
	
	/** CheckBox eliminar. */
	private JCheckBox chckbxDelete;
	
	/** CheckBox editar. */
	private JCheckBox chckbxEdit;
	
	/** CheckBox insertar. */
	private JCheckBox chckbxInsert;
	
	/** Diálogo. */
	private JDialog dialog;
	
	/** Modo edición. */
	private boolean editMode;
	
	/** Modelo del ComboBox de entidades. */
	private DefaultComboBoxModel<String> entityModel;
	
	/** Información del gestor. */
	private ManagerInfo managerInfo;
	
	/** Lista de gestores. */
	private ArrayList<ManagerInfo> managers;
	
	/** Diálogo de construcción de la lista de gestores. */
	private ManagersListBuilderDialog parent;
	
	/** Campo de texto para el nombre. */
	private JTextField tfName;
	
	/**
	 * Constructor.
	 */
	public ManagerInfoDialog() {
		initialize(null);
	}		
	
	/**
	 * Constructor. Crea la ventana en modo inserción.
	 * 
	 * @param managers Lista de gestores.
	 * @param parent Ventana padre.
	 */
	public ManagerInfoDialog(ArrayList<ManagerInfo> managers, ManagersListBuilderDialog parent) {
		this.managers = managers;
		
		editMode = false;
		this.parent = parent;
		initialize(null);		
	}
	
	/**
	 * Constructor. Crea la ventana en modo edición.
	 * 
	 * @param managerInfo Información del gestor a editar.
	 * @param parent Ventana padre.
	 */
	public ManagerInfoDialog(ManagerInfo managerInfo, ManagersListBuilderDialog parent) {
		this.managerInfo = managerInfo;
		
		editMode = true;
		this.parent = parent;
		initialize(null);		
	}
	
	/**
	 * Carga la información del gestor
	 * si está en modo edición.
	 */
	private void load() {
		if(isEditMode()) {
			tfName.setText(managerInfo.getName());
			cbxEntity.setSelectedItem(managerInfo.getEntityXML());
			chckbxInsert.setSelected(managerInfo.showInsertOption());
			chckbxEdit.setSelected(managerInfo.showEditOption());
			chckbxDelete.setSelected(managerInfo.showDeleteOption());						
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
	 * Guarda la información insertada/editada.
	 */
	private void save() {
		if(validate()) {
			if(!isEditMode()) {
				managerInfo = new ManagerInfo();
			}
			
			managerInfo.setName(tfName.getText());
			managerInfo.setEntityXML((String) cbxEntity.getSelectedItem());
			managerInfo.setShowInsertOption(chckbxInsert.isSelected());
			managerInfo.setShowEditOption(chckbxEdit.isSelected());
			managerInfo.setShowDeleteOption(chckbxDelete.isSelected());
			
			if(!isEditMode())
				managers.add(managerInfo);
			
			parent.setModified(true);
			parent.load();
			dialog.dispose();
		}
	}

	/**
	 * Valida los componentes.
	 * @return true si la validación es satisfactoria, 
	 * 			false si no.
	 */
	private boolean validate() {
		boolean b = true;
		b &= GUIUtils.checkTextField(tfName);

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
			dialog.setTitle(I18N.getInstance().getString(I18N.EDIT) + " " +  managerInfo.getName());
		else
			dialog.setTitle(I18N.getInstance().getString(I18N.INSERT) + " " + I18N.getInstance().getString(I18N.MANAGER));
		
		dialog.setBounds(50, 50, 299, 195);
		
		dialog.setLocationRelativeTo(null);
		dialog.getContentPane().setLayout(null);
		
		JPanel panelEntity = new JPanel();
		panelEntity.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEntity.setBounds(10, 11, 272, 109);
		dialog.getContentPane().add(panelEntity);
		panelEntity.setLayout(null);
		
		JLabel lblName = new JLabel(I18N.getInstance().getString(I18N.NAME));
		lblName.setBounds(10, 11, 75, 14);
		panelEntity.add(lblName);
		
		tfName = new JTextField();
		tfName.setBounds(10, 28, 142, 20);
		panelEntity.add(tfName);
		tfName.setColumns(10);
		
		chckbxInsert = new JCheckBox(I18N.getInstance().getString(I18N.INSERT));
		chckbxInsert.setSelected(true);
		chckbxInsert.setBounds(184, 20, 82, 23);
		panelEntity.add(chckbxInsert);
		
		chckbxEdit = new JCheckBox(I18N.getInstance().getString(I18N.EDIT));
		chckbxEdit.setSelected(true);
		chckbxEdit.setBounds(184, 46, 82, 23);
		panelEntity.add(chckbxEdit);
		
		chckbxDelete = new JCheckBox(I18N.getInstance().getString(I18N.DELETE));
		chckbxDelete.setSelected(true);
		chckbxDelete.setBounds(184, 73, 82, 23);
		panelEntity.add(chckbxDelete);
		
		JLabel label = new JLabel("Entidad");
		label.setBounds(10, 59, 142, 14);
		panelEntity.add(label);
		
		cbxEntity = new JComboBox<String>();
		cbxEntity.setBounds(10, 76, 142, 20);		
		panelEntity.add(cbxEntity);
		entityModel = new DefaultComboBoxModel<String>();
		loadEntityCombo();
		cbxEntity.setModel(entityModel);
		
		JButton btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		btnCancel.setBounds(193, 132, 89, 23);
		dialog.getContentPane().add(btnCancel);
		
		JButton btnAccept = new JButton(I18N.getInstance().getString(I18N.ACCEPT));
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		btnAccept.setBounds(94, 132, 89, 23);
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
