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
import model.PreferencesManager;
import model.db.auth.AuthenticationService;
import model.views.ViewInfo;

import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;

/**
 * Diálogo de construcción de la información
 * de vista.
 */
public class ViewInfoDialog {

	/** ComboBox de parametrizadores. */
	private JComboBox<String> cbxParameterizer;
	
	/** Diálogo. */
	private JDialog dialog;
	
	/** Modo edición. */
	private boolean editMode;
	
	/** Modelo del ComboBox de parametrizadores. */
	private DefaultComboBoxModel<String> paramsModel;
	
	/** Diálogo de construcción de la lista de vistas. */
	private ViewsListBuilderDialog parent;
	
	/** Campo de texto para el nombre que se muestra al usuario. */
	private JTextField tfName;
	
	/** Campo de texto para el nombre de la vista. */
	private JTextField tfViewName;
	
	/** Información de la vista. */
	private ViewInfo viewInfo;
	
	/** Lista de vistas. */
	private ArrayList<ViewInfo> views;
	
	/**
	 * Constructor.
	 */
	public ViewInfoDialog() {
		initialize(null);
	}		
	
	/**
	 * Constructor. Crea la ventana en modo
	 * inserción.
	 * 
	 * @param views Lista de vistas
	 * @param parent Diálogo de construcción de la lista de vistas.
	 */
	public ViewInfoDialog(ArrayList<ViewInfo> views, ViewsListBuilderDialog parent) {
		this.views = views;
		
		editMode = false;
		this.parent = parent;
		initialize(null);		
	}
	
	/**
	 * Constructor. Crea la ventana en modo
	 * edición.
	 * 
	 * @param viewInfo Información de vista.
	 * @param parent Diálogo de construcción de la lista de vistas.
	 */
	public ViewInfoDialog(ViewInfo viewInfo, ViewsListBuilderDialog parent) {
		this.viewInfo = viewInfo;
		
		editMode = true;
		this.parent = parent;
		initialize(null);		
	}
	
	/**
	 * Carga la información de la vista
	 * si se encuentra en modo edición.
	 */
	private void load() {
		if(isEditMode()) {
			tfName.setText(viewInfo.getName());
			tfViewName.setText(viewInfo.getViewName());
			cbxParameterizer.setSelectedItem(viewInfo.getParameterizer());
		}
	}
	
	/**
	 * Carga el ComboBox de parametrizadores.
	 */
	private void loadParameterizersCombo() {
		paramsModel.removeAllElements();
		paramsModel.addElement("");
		
		File file = new File(PreferencesManager.getInstance().getParamsSelectorPath());
		File[] files = file.listFiles();
		
		for (File currentFile : files) {
			paramsModel.addElement(currentFile.getName());
		}
	}
	
	/**
	 * Guarda la información insertada/editada.
	 */
	private void save() {
		if(validate()) {
			if(!isEditMode()) {
				viewInfo = new ViewInfo();
			}
			
			viewInfo.setName(tfName.getText());
			viewInfo.setViewName(tfViewName.getText());			
			viewInfo.setParameterizer((String) cbxParameterizer.getSelectedItem());
			
			if(!isEditMode())
				views.add(viewInfo);
			
			AuthenticationService auth = new AuthenticationService();
			auth.addAccessToLoggedUser(viewInfo.getRequiredAccess());
			
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
			dialog.setTitle(I18N.getInstance().getString(I18N.EDIT) + " " +  viewInfo.getViewName());
		else
			dialog.setTitle(I18N.getInstance().getString(I18N.INSERT) + " " + I18N.getInstance().getString(I18N.LIST));
		
		dialog.setBounds(50, 50, 266, 239);
		
		dialog.setLocationRelativeTo(null);
		dialog.getContentPane().setLayout(null);
		
		JPanel panelEntity = new JPanel();
		panelEntity.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelEntity.setBounds(10, 11, 240, 157);
		dialog.getContentPane().add(panelEntity);
		panelEntity.setLayout(null);
		
		JLabel lblName = new JLabel(I18N.getInstance().getString(I18N.NAME));
		lblName.setBounds(10, 11, 75, 14);
		panelEntity.add(lblName);
		
		tfName = new JTextField();
		tfName.setBounds(10, 28, 217, 20);
		panelEntity.add(tfName);
		tfName.setColumns(10);		
		
		JLabel label = new JLabel(I18N.getInstance().getString(I18N.PARAMETERIZER));
		label.setBounds(10, 107, 142, 14);
		panelEntity.add(label);
		
		cbxParameterizer = new JComboBox<String>();
		cbxParameterizer.setBounds(10, 124, 217, 20);		
		panelEntity.add(cbxParameterizer);
		paramsModel = new DefaultComboBoxModel<String>();
		loadParameterizersCombo();
		cbxParameterizer.setModel(paramsModel);		
		
		tfViewName = new JTextField();
		tfViewName.setColumns(10);
		tfViewName.setBounds(10, 76, 217, 20);
		panelEntity.add(tfViewName);
		
		JLabel label_1 = new JLabel(I18N.getInstance().getString(I18N.VIEW));
		label_1.setBounds(10, 59, 75, 14);
		panelEntity.add(label_1);
		
		JButton btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		btnCancel.setBounds(161, 179, 89, 23);
		dialog.getContentPane().add(btnCancel);
		
		JButton btnAccept = new JButton(I18N.getInstance().getString(I18N.ACCEPT));
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		btnAccept.setBounds(62, 179, 89, 23);
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
