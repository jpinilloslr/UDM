package view.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import view.AbstractListFrame;
import view.I18N;
import view.ThemeManager;
import view.editors.RoleEditorDialog;
import model.db.EntityCreator;
import model.db.EntityManager;
import model.db.FuncStatementsGenerator;
import model.db.StaticStatementsGenerator;
import model.db.auth.Access;
import model.db.auth.Permission;
import model.db.auth.Role;
import model.db.auth.User;
import model.db.entities.Entity;
import model.db.entities.Field;

/**
 * <h1>Ventana de gestión de roles del sistema</h1>
 */
public class RoleManagerFrame extends TableManagerFrame {		

	/**
	 * Constructor.
	 */
	public RoleManagerFrame() {
		super(I18N.getInstance().getString(I18N.ROLES));	
		frmManager.setFrameIcon(new ImageIcon(AbstractListFrame.class.getResource(
				ThemeManager.getInstance().getImage(ThemeManager.ROLES))));
		setEditorDialog(new RoleEditorDialog((JFrame) frmManager.getParent()));
	}
	
	/**
	 * Devuelve la lista de accesos permitidos a un rol 
	 * separados por coma.
	 * 
	 * @param roleId Id del rol.
	 * @return Lista de accesos permitidos.
	 */
	private String getAccessListForRole(int roleId) {
		String access = "";
		EntityManager manager = new EntityManager(new StaticStatementsGenerator());
		Role role = new Role();
		role.setId(roleId);
		role = (Role) manager.get(role);		
		ArrayList<Access> accessList = role.getAccessList();
		Iterator<Access> iter = accessList.iterator();
		
		while(iter.hasNext()) {
			access += iter.next().getName();
			
			if(iter.hasNext())
				access += ", ";
		}
		return access;
	}
	
	@Override
	protected void createGUI() {
		ArrayList<String> fieldsNames = new ArrayList<String>();
		Entity ent = new Role();

		for(int i=0; i<ent.getFields().size(); i++) {
			if(!ent.getFields().get(i).isAutoincremental())
				fieldsNames.add(I18N.getInstance().getString(ent.getFields().get(i).getName()));
		}
		
		fieldsNames.add(I18N.getInstance().getString(I18N.ACCESS));
		createTable(fieldsNames.toArray(new String[0]));				
	}

	@Override
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
			Role current;
			
			while(iter.hasNext()) {
				current = (Role) iter.next();
				manager.rawUpdate("DELETE FROM " + Permission.TABLE + " WHERE " + Permission.ROLE__ID + "=" + String.valueOf(current.getId()));
				manager.delete(current);
			}

			refresh();
			updateTable();
		}
	}	
	
	@Override
	public String getRequiredAccess() {
		User user = new User();
		String access = "admin." + user.getTableName(); 		
		return access;
	}
	
	@Override
	public void refresh() {
		EntityManager manager = new EntityManager(new FuncStatementsGenerator());		
		entities = manager.get(Role.TABLE, Role.NAME, new EntityCreator() {			
			@Override
			public Entity create() {
				return new Role();
			}
		});
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

				if(!field.isAutoincremental()) {
					String value = String.valueOf(field.getValue());										
					entry.add(value);
				}
			}
			entry.add(getAccessListForRole((Integer) current.getByName("id").getValue()));

			tableModel.addRow(entry);
		}	

		printStatus(String.valueOf(tableModel.getRowCount()) + " " + I18N.getInstance().getString(I18N.RECORDS));
	}
}
