package view.builder;

import view.I18N;
import view.utils.GUIUtils;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import model.db.auth.AuthenticationService;
import model.db.entities.Entity;
import model.db.entities.EntityLoader;
import model.db.entities.Field;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Diálogo constructor de entidades.
 */
public class EntityBuilderDialog extends AbstractBuilderDialog<Field> {

	/** ComboBox ordernar por. */
	private JComboBox<String> cbxOrderBy;
	
	/** CheckBox usar borrado lógico. */
	private JCheckBox chckbxLogicDelete;
	
	/** Diálogo de configuración a partir de una lista de archivos. */
	private EntityFileListDialog listDialog;
	
	/** Modelo del ComboBox ordenar por. */
	private DefaultComboBoxModel<String> orderByModel;
	
	/** Campo de texto para el nombre de la tabla. */
	private JTextField tfTableName;
	
	/**
	 * Constructor.
	 * @param entityListDialog Diálogo de configuración a partir de una lista de archivos.
	 * @param model Entidad. Si esta variable es null se crea la ventana en modo insertar,
	 * 				de lo contrario se crea en modo edición.
	 */
	public EntityBuilderDialog(EntityFileListDialog entityListDialog, Entity model) {
		super(model);
		this.listDialog = entityListDialog;
		
		if(null == model) {
			exportable = new Entity();
			editMode = false;
			title = I18N.getInstance().getString(I18N.NEW_ENTITY);
		} else { 
			exportable = new Entity(model);
			editMode = true;
			title = I18N.getInstance().getString(I18N.EDIT) + " " + ((Entity) exportable).getTableName();
		}
		
		columns = new String[]{I18N.getInstance().getString(I18N.PRIMARY_KEY), 
								I18N.getInstance().getString(I18N.AUTOINC), 
								I18N.getInstance().getString(I18N.NAME), 
								I18N.getInstance().getString(I18N.TYPE), 
								I18N.getInstance().getString(I18N.DEFAULT_VALUE), 
								I18N.getInstance().getString(I18N.FOREIGN_KEY), 
								I18N.getInstance().getString(I18N.LOGIC_DELETE_MARK),
								I18N.getInstance().getString(I18N.REQUIRED)};
		init();
	}	
	
	/**
	 * Valida el contenido de los componentes.
	 * @return true si la validación es satisfactoria,
	 * 			false si no.
	 */
	private boolean validate() {
		boolean b = true;
		b &= GUIUtils.checkTextField(tfTableName);

		if(table.getRowCount() == 0) {
			b = false;
			JOptionPane.showMessageDialog(null, I18N.getInstance().getString(I18N.NO_FIELDS_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
		
		if(!b) {
			JOptionPane.showMessageDialog(null, I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
		
		return b;
	}

	
	@Override
	protected void edit() {
		int index = table.getSelectedRow();
		new FieldDialog(((Entity) exportable), index, this);
	}
	
	@Override
	protected void insert() {
		new FieldDialog(((Entity) exportable), -1, this);
	}
		
	@Override
	protected boolean save() {
		boolean success = false;
		
		if(validate()) {
			((Entity) exportable).setXmlFile(tfTableName.getText() + ".xml");
			((Entity) exportable).setTableName(tfTableName.getText());
			((Entity) exportable).setLogicDelete(chckbxLogicDelete.isSelected());
			((Entity) exportable).setDefaultOrderField((String) cbxOrderBy.getSelectedItem());
			
			((Entity) exportable).exportToXml();
			EntityLoader.getInstance().clearCache();
			
			AuthenticationService auth = new AuthenticationService();
			auth.addAccessToLoggedUser(((Entity) exportable).getRequiredAccess());		
			
			listDialog.refreshList();
			setModified(false);
			success = true;
		}
		
		return success;
	}		
		
	@Override
	public void createTopPanel(JPanel topPanel) {
		tfTableName = new JTextField();		
		tfTableName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				setModified(true);
			}
		});
		tfTableName.setColumns(10);
		tfTableName.setBounds(10, 29, 145, 20);
		topPanel.add(tfTableName);
		
		JLabel lblTableName = new JLabel(I18N.getInstance().getString(I18N.TABLE));
		lblTableName.setBounds(10, 11, 145, 14);
		topPanel.add(lblTableName);
		
		chckbxLogicDelete = new JCheckBox(I18N.getInstance().getString(I18N.USER_LOGIC_DELETE));
		chckbxLogicDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setModified(true);
			}
		});
		chckbxLogicDelete.setEnabled(false);
		chckbxLogicDelete.setBounds(560, 28, 119, 23);
		topPanel.add(chckbxLogicDelete);
		
		JLabel label = new JLabel(I18N.getInstance().getString(I18N.ORDER_BY));
		label.setBounds(165, 11, 145, 14);
		topPanel.add(label);
		
		cbxOrderBy = new JComboBox<String>();
		cbxOrderBy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setModified(true);
			}
		});
		cbxOrderBy.setBounds(165, 29, 145, 20);
		orderByModel = new DefaultComboBoxModel<String>();
		cbxOrderBy.setModel(orderByModel);
		topPanel.add(cbxOrderBy);	
	}
	
	@Override
	public void load() {						
		if(((Entity) exportable).getTableName() != null)
			tfTableName.setText(((Entity) exportable).getTableName());
		
		chckbxLogicDelete.setSelected(((Entity) exportable).useLogicDelete());
		Iterator<Field> iter = ((Entity) exportable).getFields().iterator();
		Field current;
		
		while(tableModel.getRowCount() > 0)
			tableModel.removeRow(0);
		
		orderByModel.removeAllElements();
		
		while(iter.hasNext()) {
			current = iter.next();
			Vector<String> row = new Vector<String>();
			
			if(current.isPrimaryKey())	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));
			if(current.isAutoincremental())	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));
			row.add(current.getName());
			row.add(current.getFieldType().toString());
			row.add(String.valueOf(current.getValue()));
			if(current.getForeignKeyInfo() != null)	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));
			if(current.isLogicDeleteMarker())	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));	
			if(current.isRequired())	row.add(I18N.getInstance().getString(I18N.YES)); else row.add(I18N.getInstance().getString(I18N.NO));
			
			tableModel.addRow(row);
			orderByModel.addElement(current.getName());
		}		
		
		cbxOrderBy.setSelectedItem(((Entity) exportable).getDefaultOrderField());
		
		if(((Entity) exportable).getLogicDeleteMarkerField() == null) {
			chckbxLogicDelete.setEnabled(false);
			chckbxLogicDelete.setSelected(false);
		} else {
			chckbxLogicDelete.setEnabled(true);
		}
		
		setModified(false);
	}	
}
