package view.managers;

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import view.AbstractListFrame;
import view.I18N;
import view.ThemeManager;
import view.editors.AbstractEditorDialog;
import view.editors.DynamicEditorDialog;
import model.PreferencesManager;
import model.db.DBLink;
import model.db.EntityManager;
import model.db.FuncStatementsGenerator;
import model.db.StaticStatementsGenerator;
import model.db.entities.Entity;
import model.db.entities.EntityLoader;
import model.db.entities.Field;
import model.db.entities.FieldType;
import model.db.entities.ForeignKeyInfo;
import model.managers.ManagerInfo;
import model.plugin.ContextPluginInfo;
import model.plugin.Plugin;
import model.plugin.PluginManager;

/**
 * <h1>Ventana de gestión de tablas</h1>
 * 
 * Hereda todas las características de GenericListFrame. Introduce
 * la posibilidad de vincularse a una tabla de la base de datos 
 * adpotando su estructura de manera dinámica a partir de los metadatos. 
 * Expone los controles necesarios para insertar, actualizar y eliminar.
 */
public class TableManagerFrame extends AbstractListFrame {

	/** Botón insertar. */
	protected JButton btnInsert;

	/** Caché de referencias a llaves foráneas. */
	protected HashMap<String, String> cache;
	
	/** Ventana de edición asociada a este gestor. */
	protected AbstractEditorDialog editorDialog;
	
	/** Lista de entidades. Es la fuente de datos del gestor. */
	protected LinkedList<Entity> entities;
	
	/** Información del gestor. */
	protected ManagerInfo managerInfo;
	
	/** Menú datos. */
	protected JMenu mnManager;
	
	/** Menú eliminar. */
	protected JMenuItem mntmDelete;
	
	/** Menú editar. */
	protected JMenuItem mntmEdit;	
	
	/** Menú insertar. */
	protected JMenuItem mntmInsert;
	
	/** Menú restaurar. */
	protected JMenuItem mntmRestore;
	
	/** Entidad modelo para este gestor */
	protected Entity model;
	
	/**
	 * Este contstructor solo debe llamarse desde 
	 * clases hijas.
	 * 
	 * @param title Título de la ventana.
	 */
	protected TableManagerFrame(String title) {
		super();
		filteredIndexes = new ArrayList<Integer>();		
		frmManager.setVisible(true);
		frmManager.setTitle(title);
		frmManager.setFrameIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.ENTITY))));
		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param title Título de la ventana.
	 * @param managerInfo Información referente al gestor de tablas.
	 */
	public TableManagerFrame(String title, ManagerInfo managerInfo) {
		super();
		filteredIndexes = new ArrayList<Integer>();		
		frmManager.setVisible(true);
		frmManager.setTitle(title);
		frmManager.setFrameIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.ENTITY))));

		this.managerInfo = managerInfo;
		model = EntityLoader.getInstance().load(managerInfo.getEntityXML());
		setEditorDialog(new DynamicEditorDialog(null, model, managerInfo.getEntityXML()));
		init();
	}
	
	/**
	 * Formatea el valor de un campo según su tipo.
	 * @param fieldValue Valor del campo.
	 * @param type Tipo del campo.
	 * @return Valor en formato texto.
	 */
	private String formatFieldValue(Object fieldValue, FieldType type) {
		String value = String.valueOf(fieldValue);

		if(type == FieldType.FT_BOOLEAN) {
			if(value.equals("true")) 
				value = I18N.getInstance().getString(I18N.YES);
			else
				if(value.equals("false")) 
					value = I18N.getInstance().getString(I18N.NO);
		} else				
		if(type == FieldType.FT_DATETIME) {
			Timestamp timestamp = (Timestamp) fieldValue;
			SimpleDateFormat sdf = new SimpleDateFormat(PreferencesManager.getInstance().getDateTimeFormat());
			
			if(null != timestamp)
				value = sdf.format(timestamp);
		} else				
		if(type == FieldType.FT_DATE) {
			Date date = (Date) fieldValue;
			SimpleDateFormat sdf = new SimpleDateFormat(PreferencesManager.getInstance().getDateFormat());
			
			if(null != date)
				value = sdf.format(date);
		} else				
		if(type == FieldType.FT_TIME) {
			Time time = (Time) fieldValue;
			SimpleDateFormat sdf = new SimpleDateFormat(PreferencesManager.getInstance().getTimeFormat());
				
			if(null != time)
				value = sdf.format(time);
		}
		
		return value;
	}			
	
	/**
	 * Dado un campo descrito como llave foránea, devuelve el valor
	 * al que hace referencia en la entidad externa.
	 * 
	 * @param field Campo descrito como llave foránea.
	 * @return Valor al que hace referencia la llave foránea.
	 */
	private String getForeignKeyReference(Field field) {
		String ref = cache.get(field.getName() + String.valueOf(field.getValue()));
		
		if(ref == null || ref.length() == 0) {
			ForeignKeyInfo fkInfo = field.getForeignKeyInfo(); 
			
			Entity refEnt = EntityLoader.getInstance().load(fkInfo.getEntityXML());
			FieldType type = FieldType.FT_STRING;
			Field substituteField = refEnt.getByName(fkInfo.getSubstituteField());
			
			if(substituteField != null)
				type = substituteField.getFieldType();
					
			String sql = "SELECT \"" + fkInfo.getSubstituteField() + "\" FROM \"" + refEnt.getTableName() +  
						"\" WHERE " + fkInfo.getReferencedField() + "=?";
			try {
				PreparedStatement ps = DBLink.getInstance().getConnection().prepareStatement(sql);
				ps.setInt(1, (Integer) field.getValue());
				
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					ref = formatFieldValue(rs.getObject(1), type);
					cache.put(field.getName() + String.valueOf(field.getValue()), ref);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return ref;
	}	
	
	/**
	 * Inicia.
	 */
	private void init() {
		cache = new HashMap<String, String>();
		PreferencesManager.getInstance().loadPreferences();
		load();
		
		if(managerInfo != null) {
			if(!managerInfo.showInsertOption()) {
				btnInsert.setVisible(false);
				mntmInsert.setVisible(false);
			}
			
			if(!managerInfo.showEditOption()) 
				mntmEdit.setVisible(false);
			
			if(!managerInfo.showDeleteOption()) {
				mntmDelete.setVisible(false);
				mntmRestore.setVisible(false);
			}
			
			if(mnManager.getMenuComponentCount() == 0)
				mnManager.setVisible(false);
		}
	}

	@Override
	protected void checkMenuItemAccesibility() {
		super.checkMenuItemAccesibility();

		int selectedElements = table.getSelectedRowCount();

		mntmEdit.setEnabled(false);
		mntmDelete.setEnabled(false);
		mntmRestore.setVisible(false);		
		
		if(selectedElements > 0) {
			mntmDelete.setEnabled(true);
			mntmEdit.setEnabled(true);
		}

		if(entities.size() > 0 && entities.get(0).useLogicDelete()) {
			boolean canRestore = true;
			int i = 0; 
			int[] selected = table.getSelectedRows();
			while (i<selected.length && canRestore) {
				Entity current = entities.get(getListIndexFromTableIndex(table.convertRowIndexToModel(selected[i])));
				
				Boolean active = (Boolean) current.getLogicDeleteMarkerField().getValue();
				if(active)
					canRestore = false;
				i++;
			}

			if(canRestore)
				mntmRestore.setVisible(true);
		}
	}

	@Override
	protected void createGUI() {
		ArrayList<String> fieldsNames = new ArrayList<String>();
		Entity ent = model;
		
		for(int i=0; i<ent.getFields().size(); i++) {
			if(ent.getFields().get(i).isAutoincremental()) {
				if(!PreferencesManager.getInstance().hideAutoincrementalFields())
					fieldsNames.add(I18N.getInstance().getString(ent.getFields().get(i).getName()));
			} else {
				fieldsNames.add(I18N.getInstance().getString(ent.getFields().get(i).getName()));
			}
		}

		createTable(fieldsNames.toArray(new String[0]));				
	}

	@Override
	protected void createMainMenu() {		
		mnManager = new JMenu(I18N.getInstance().getString(I18N.MANAGE));
		menuBar.add(mnManager);

		mnManager.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent arg0) {
			}
			public void menuDeselected(MenuEvent arg0) {
			}
			public void menuSelected(MenuEvent arg0) {
				checkMenuItemAccesibility();
			}
		});

		mntmInsert = new JMenuItem(I18N.getInstance().getString(I18N.INSERT));
		mnManager.add(mntmInsert);	
		mntmInsert.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		super.createMainMenu();
	}

	@Override
	protected void createPopupMenu() {
		super.createPopupMenu();

		mntmEdit = new JMenuItem(I18N.getInstance().getString(I18N.EDIT));
		mntmDelete = new JMenuItem(I18N.getInstance().getString(I18N.DELETE));
		mntmRestore = new JMenuItem(I18N.getInstance().getString(I18N.RESTORE));
		popupMenu.add(mntmEdit);
		popupMenu.add(mntmDelete);
		popupMenu.add(mntmRestore);	

		mntmDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteSelecteds();
				refresh();
			}
		});	

		mntmRestore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				restoreSelecteds();
				refresh();
			}
		});					
	}

	@Override
	protected void createToolBarElements() {
		btnInsert = new JButton("");
		btnInsert.setIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.NEW))));
		btnInsert.setFocusable(false);
		btnInsert.setToolTipText(I18N.getInstance().getString(I18N.INSERT));
		toolBar.add(btnInsert);

		btnInsert.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				insertPopupMenu();
			}
		});
		super.createToolBarElements();
	}
	
	/**
	 * Elimina los elementos seleccionados de la lista. Permite selección múltiple
	 * y muestra un cuadro de diálogo de confirmación antes de proceder.
	 */
	protected void deleteSelecteds() {
		int[] selected = table.getSelectedRows();
		LinkedList<Entity> deleteBatch = new LinkedList<Entity>();


		String title = I18N.getInstance().getString(I18N.DELETE_PROMPT_TITLE);
		String msg = (selected.length > 1)? I18N.getInstance().getString(I18N.DELETE_MULTIPLE_PROMPT) : I18N.getInstance().getString(I18N.DELETE_SINGLE_PROMPT);


		if(JOptionPane.showConfirmDialog(frmManager, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			EntityManager manager = new EntityManager(new StaticStatementsGenerator());
			for (int i : selected) {
				deleteBatch.add(entities.get(getListIndexFromTableIndex(table.convertRowIndexToModel(i))));
			}
			Iterator<Entity> iter = deleteBatch.iterator();
			Entity current;
			while(iter.hasNext()) {
				current = iter.next();
				Field fAct = current.getLogicDeleteMarkerField();

				if(null != fAct) {
					boolean active = (Boolean) fAct.getValue();

					if(!active)
						current.setLogicDelete(false);
				}

				manager.delete(current);
			}

			refresh();
			updateTable();
		}
	}			
	
	/**
	 * Acción editar.
	 */
	protected void editPopupMenu() {
		if(null != editorDialog) {

			if(table.getSelectedRowCount() == 1) {
				Entity item = entities.get(getRealSelectedItemIndex());
				editorDialog.create(item, this);
			} else {
				int[] selected = table.getSelectedRows();
				ArrayList<Entity> batch = new ArrayList<Entity>();
				for (int i : selected) {
					batch.add(entities.get(getListIndexFromTableIndex(i)));
				}
				editorDialog.createMultiEdition(batch, this);
			}
		}
	}	

	/**
	 * Inicia el contenido del frame.
	 */
	protected void initialize() {
		super.initialize();

		mntmInsert.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				insertPopupMenu();
			}
		});

		mntmEdit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editPopupMenu();
			}
		});
	}

	/**
	 * Acción insertar.
	 */
	protected void insertPopupMenu() {
		if(null != editorDialog) {
			editorDialog.create(null, this);
		}
	}
	
	/**
	 * Restaura los elementos seleccionados de la lista. Permite selección múltiple
	 * y muestra un cuadro de diálogo de confirmación antes de proceder.
	 */
	protected void restoreSelecteds() {
		int[] selected = table.getSelectedRows();
		LinkedList<Entity> restoreBatch = new LinkedList<Entity>();


		String title = I18N.getInstance().getString(I18N.RESTORE_PROMPT_TITLE);
		String msg = (selected.length > 1)? I18N.getInstance().getString(I18N.RESTORE_MULTIPLE_PROMPT) : I18N.getInstance().getString(I18N.RESTORE_SINGLE_PROMPT);


		if(JOptionPane.showConfirmDialog(frmManager, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			EntityManager manager = new EntityManager(new StaticStatementsGenerator());
			for (int i : selected) {
				restoreBatch.add(entities.get(getListIndexFromTableIndex(table.convertRowIndexToModel(i))));
			}
			Iterator<Entity> iter = restoreBatch.iterator();
			Entity current;

			while(iter.hasNext()) {
				current = iter.next();
				current.getLogicDeleteMarkerField().setValue(true);
				manager.update(current);
			}

			refresh();
			updateTable();
		}
	}
	
	@Override
	public String getRequiredAccess() {
		String access = ""; 	
		access = model.getRequiredAccess();

		return access;
	}
	
	@Override
	public void loadPlugins() {
		ArrayList<Plugin> plugins = PluginManager.getInstance().getPluginsForWindow(getRequiredAccess());
		Iterator<Plugin> iter = plugins.iterator();
		
		if(iter.hasNext()) {
			JSeparator sep = new JSeparator();
			sep.setForeground(SystemColor.controlHighlight);
			sep.setBackground(SystemColor.menu);
			popupMenu.add(sep);
		}
		
		while(iter.hasNext()) {
			final Plugin current = iter.next();
			
			JMenuItem mntmPlugin = new JMenuItem(current.getText());
			popupMenu.add(mntmPlugin);
			
			mntmPlugin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(table.getSelectedRowCount() >= 1) {
						int[] selected = table.getSelectedRows();
						ArrayList<Entity> batch = new ArrayList<Entity>();
						for (int i : selected) {
							batch.add(entities.get(getListIndexFromTableIndex(i)));
						}

						if(current.setEntities(batch))
							current.load();							
					}			
				}
			});	
			
			if(((ContextPluginInfo) current.getPluginInfo()).isMultiSelect())
				pluginsMultiSelect.add(mntmPlugin);
			else
				pluginsSingleSelect.add(mntmPlugin);
		}
	}

	@Override
	public void refresh() {
		cache.clear();
		EntityManager manager = new EntityManager(new FuncStatementsGenerator());		
		entities = manager.get(model.getTableName(), model.getDefaultOrderField(), managerInfo.getEntityXML());
	}	
	
	/**
	 * Define la ventana de inserción/edición correspondiente 
	 * a este gestor de tabla.
	 * 
	 * @param editorDialog Ventana de inserción/edición.
	 */
	public void setEditorDialog(AbstractEditorDialog editorDialog) {
		this.editorDialog = editorDialog;
	}
	
	@Override
	public void updateTable() {		
		
		while(tableModel.getRowCount() > 0)
			tableModel.removeRow(0);

		Iterator<Entity> iter = entities.iterator();
		Entity current;		

		while(iter.hasNext()) {
			current = iter.next();
			Field field;
			Vector<String> entry = new Vector<String>();

			for(int i=0; i<current.getFields().size(); i++) {
				field = current.getFields().get(i);				
				String value = formatFieldValue(field.getValue(), field.getFieldType());
				
				if(field.getForeignKeyInfo() != null && PreferencesManager.getInstance().replaceForeignKeyValues()) {
					value = getForeignKeyReference(field);
				}

				if(field.isAutoincremental()) {
					if(!PreferencesManager.getInstance().hideAutoincrementalFields())
						entry.add(value);
				} else
					entry.add(value);
			}

			tableModel.addRow(entry);
		}	

		printStatus(String.valueOf(tableModel.getRowCount()) + " " + I18N.getInstance().getString(I18N.RECORDS));
	}	
}
