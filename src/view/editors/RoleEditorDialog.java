package view.editors;

import java.awt.Component;
import java.awt.SystemColor;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import view.I18N;

import model.db.EntityCreator;
import model.db.EntityManager;
import model.db.StaticStatementsGenerator;
import model.db.auth.Access;
import model.db.auth.Permission;
import model.db.auth.Role;
import model.db.entities.Entity;

/**
 *  <h1>Formulario editor de rol</h1>
 */
public class RoleEditorDialog extends AbstractEditorDialog {					
	
	/** Panel con los CheckBox de nombres de accesos. */
	private JPanel list;
	
	/** Campo de texto para el nombre. */
	private JTextField tfName;
	
	/**
	 * Constructor.
	 * @param parent Ventana padre.
	 */
	public RoleEditorDialog(JFrame parent) {
		super(parent);
	}
	
	/**
	 * Activa un CheckBox identificado por su texto.	
	 * @param text Texto.
	 */
	private void activateCheckBox(String text) {
		boolean found = false;
		Component[] childs = list.getComponents();
		JCheckBox current;
		int i = 0;
		
		while(!found && i<childs.length) {
			current = (JCheckBox) childs[i];
			
			if(current.getText().equals(text)) {
				current.setSelected(true);
				found = true;
			}
			i++;
		}
	}
	
	/**
	 * Devuelve un objeto Access a partir de su nombre.
	 * @param name Nombre.
	 * @return Access.
	 */
	private Access getAccessByName(String name) {
		Access access = new Access();
		access.setName(name);
		access.setSearchable(Access.NAME);
		EntityManager manager = new EntityManager(new StaticStatementsGenerator());
		access = (Access) manager.searchLast(access);
		return access;
	}
				
	/**
	 * Crea la lista de CheckBox referidos a los accesos.
	 */
	private void loadAccessList() {
		EntityManager manager = new EntityManager(new StaticStatementsGenerator());
		LinkedList<Entity> ents = manager.get(Access.TABLE, Access.NAME, new EntityCreator() {			
			@Override
			public model.db.entities.Entity create() {
				return new Access();
			}
		});
		
		Iterator<Entity> iter = ents.iterator();
		Access current;
		int ypos = 5;
		int xpos = 5;
		int count = 0;
		int maxY = 0;
		
		while(iter.hasNext()) {
			current = (Access) iter.next();
			
			if(count%13 == 0 && count > 0) {
				xpos += 200;
				ypos = 5;
			}
			
			JCheckBox item = new JCheckBox();
			item.setVisible(true);
			item.setBounds(xpos, ypos, 180, 23);
			item.setText(current.getName());
			list.add(item);
			list.setComponentZOrder(item, 0);
			
			ypos += 23;
			count++;
			
			if(ypos > maxY)
				maxY = ypos;
		}
		list.setSize(xpos + 190, maxY+5);
		frmEditor.setBounds(0, 0, list.getX() + list.getWidth()+15, list.getY() + list.getHeight()+75);
	}
	
	@Override
	protected void createGUI() {
		super.createGUI();		
		
		JLabel lblNombre = new JLabel(I18N.getInstance().getString(I18N.NAME));
		lblNombre.setBounds(10, 11, 46, 14);
		frmEditor.getContentPane().add(lblNombre);
		
		tfName = new JTextField();
		tfName.setBounds(10, 30, 200, 20);
		frmEditor.getContentPane().add(tfName);
		tfName.setColumns(10);		
		
		list = new JPanel();
		list.setLayout(null);
		list.setBounds(10, 75, 587, 175);
		list.setBorder(new LineBorder(SystemColor.controlHighlight));
		frmEditor.getContentPane().add(list);
						
		JLabel lblPermisos = new JLabel(I18N.getInstance().getString(I18N.PERMISSION));
		lblPermisos.setBounds(10, 61, 150, 14);
		frmEditor.getContentPane().add(lblPermisos);		
		
		loadAccessList();
		frmEditor.setLocationRelativeTo(null);	
		
		btnCancel.setBounds(list.getX() + list.getWidth()-89, list.getY()+list.getHeight()+10, 89, 23);				
		btnSave.setBounds(list.getX() + list.getWidth()-89-89-10, list.getY()+list.getHeight()+10, 89, 23);
		
		loadData();						
	}
		
	@Override
	protected void loadData() {		
		if(isInEditMode() || isInMultiEditMode()) {
			Role role = (Role) item;
			
			tfName.setText(role.getName());
			
			Iterator<Access> iter = role.getAccessList().iterator();
			Access current;
			
			while(iter.hasNext()) {
				current = iter.next();
				activateCheckBox(current.getName());				
			}
		}
	}	
	
	@Override
	protected void save() {
		if(validateInput()) {
			EntityManager manager = new EntityManager(new StaticStatementsGenerator());
			Role role = (Role) item;			
			boolean success = false;
			
			if(isInEditMode()) {
				role.setName(tfName.getText());				
				success = manager.update(item);
			} else {
				item = new Role(0, tfName.getText());	
				role = (Role) item;
				item = manager.insertAndRetrieve(item);
				
				if(null != item)
					success = true;				
			}
			
			
			Component[] childs = list.getComponents();
			JCheckBox current;
			
			for (Component component : childs) {
				current = (JCheckBox) component;
				Access access = getAccessByName(current.getText());
				
				Permission perm = new Permission(role, access);			
				
				if(current.isSelected()) {
					if(null == manager.get(perm)) {
						manager.insert(perm, true);
					}
				} else {
					if(null != manager.get(perm)) {
						manager.delete(perm);
					}
				}
			}
			
			managerFrame.refresh();
			managerFrame.updateTable();
			
			if(success)
				frmEditor.dispose();
		}
	}
}
