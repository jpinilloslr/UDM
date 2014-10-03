package view.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import view.AbstractListFrame;
import view.I18N;
import view.ThemeManager;
import view.editors.UserEditorDialog;
import model.db.EntityCreator;
import model.db.EntityManager;
import model.db.FuncStatementsGenerator;
import model.db.StaticStatementsGenerator;
import model.db.auth.Role;
import model.db.auth.User;
import model.db.entities.Entity;
import model.db.entities.Field;
import model.db.entities.FieldType;

/**
 * <h1>Ventana de gestión de usuarios del sistema</h1>
 */
public class UserManagerFrame extends TableManagerFrame {
		
	/**
	 * Constructor.
	 */
	public UserManagerFrame() {
		super(I18N.getInstance().getString(I18N.USERS));
		frmManager.setFrameIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.USERS))));
		setEditorDialog(new UserEditorDialog((JFrame) frmManager.getParent()));
	}
	
	/**
	 * Dvuelve el nombre de una columna de
	 * esta tabla.
	 * 
	 * @param key Nombre.
	 * @return Nombre sustituto.
	 */
	private String getColumnName(String key) {
		String value = key;
		
		if(key.equals(User.USERNAME))
			value = I18N.getInstance().getString(I18N.USERNAME);
		else
		if(key.equals(User.PASSWORD))
			value = I18N.getInstance().getString(I18N.PASSWORD);
		else
		if(key.equals(User.ROLE))
			value = I18N.getInstance().getString(I18N.ROLE);
		
		return value;
	}
	
	@Override
	public String getRequiredAccess() {
		User user = new User();
		String access = "admin." + user.getTableName(); 		
		return access;
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
	
					if(field.getFieldType() == FieldType.FT_BOOLEAN) {
						if(value.equals("true")) 
							value = I18N.getInstance().getString(I18N.YES);
						else
							if(value.equals("false")) 
								value = I18N.getInstance().getString(I18N.NO);
					}
					
					if(field.getName().equals(User.ROLE)) {
						Role role = new Role();
						role.setId((Integer) field.getValue());
						EntityManager manager = new EntityManager(new StaticStatementsGenerator());
						role = (Role) manager.get(role);
						value = role.getName(); 
					}
					
					if(field.getName().equals("password")) {
						value = "********"; 
					}
	
					entry.add(value);
				}
			}

			tableModel.addRow(entry);
		}	

		printStatus(String.valueOf(tableModel.getRowCount()) + " " + I18N.getInstance().getString(I18N.RECORDS));
	}		
	
	@Override
	protected void createGUI() {
		ArrayList<String> fieldsNames = new ArrayList<String>();
		Entity ent = new User();

		for(int i=0; i<ent.getFields().size(); i++) {
			if(!ent.getFields().get(i).isAutoincremental())
				fieldsNames.add(getColumnName(ent.getFields().get(i).getName()));
		}

		createTable(fieldsNames.toArray(new String[0]));				
	}
	
	@Override
	public void refresh() {
		EntityManager manager = new EntityManager(new FuncStatementsGenerator());		
		entities = manager.get(User.TABLE, User.USERNAME, new EntityCreator() {			
			@Override
			public Entity create() {
				return new User();
			}
		});
	}
}
