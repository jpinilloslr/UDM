package view.editors;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import view.I18N;

import model.db.EntityCreator;
import model.db.EntityManager;
import model.db.StaticStatementsGenerator;
import model.db.auth.Role;
import model.db.auth.User;
import model.db.entities.Entity;

/**
 * <h1>Formulario editor de usuario</h1>
 */
public class UserEditorDialog extends AbstractEditorDialog {					
	
	/** ComboBox de roles. */
	private JComboBox<String> cbxRole;
	
	/** Label rol. */
	private JLabel lblRole;
	
	/** Campo de texto para la contraseña. */
	private JPasswordField pfPassword;
	
	/** Campo de texto para repetir la contraseña. */
	private JPasswordField pfRepeat;
	
	/** Campo de texto para el nombre. */
	private JTextField tfUsername;
	
	/**
	 * Constructor.
	 * @param parent Ventana padre.
	 */
	public UserEditorDialog(JFrame parent) {
		super(parent);
	}	
	
	/**
	 * Llena el ComboBox con los roles 
	 * disponibles.
	 */
	private void fillRolesCombo() {
		EntityManager manager = new EntityManager(new StaticStatementsGenerator());
		LinkedList<Entity> ents = manager.get(Role.TABLE, Role.NAME, new EntityCreator() {			
			@Override
			public Entity create() {
				return new Role();
			}
		});
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		Iterator<Entity> iter = ents.iterator();
		Role current;
		
		while(iter.hasNext()) {
			current = (Role) iter.next();			
			model.addElement(current.getName());
		}
		cbxRole.setModel(model);
	}
	
	/**
	 * Devuelve el id del rol seleccionado.
	 * @return Id del rol seleccionado.
	 */
	private int getSelectedRoleId() {
		int id = -1;
		
		EntityManager manager = new EntityManager(new StaticStatementsGenerator());
		
		ResultSet rs = manager.rawExecute("SELECT id FROM udm_role WHERE udm_role.name=\'" + 
											String.valueOf(cbxRole.getSelectedItem()) +
											"\'");
		
		try {
			if(rs.next()) {
				id = rs.getInt("id");
			}
		} catch(Exception e) {}
		
		return id;
	}
				
	@Override
	protected void createGUI() {
		super.createGUI();
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, I18N.getInstance().getString(I18N.CREDENTIALS), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 42, 357, 137);
		frmEditor.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel label = new JLabel(I18N.getInstance().getString(I18N.PASSWORD));
		label.setBounds(10, 69, 65, 14);
		panel.add(label);
		
		JLabel label_1 = new JLabel(I18N.getInstance().getString(I18N.USER));
		label_1.setBounds(10, 26, 46, 14);
		panel.add(label_1);
		
		tfUsername = new JTextField();
		tfUsername.setColumns(10);
		tfUsername.setBounds(90, 23, 253, 20);
		panel.add(tfUsername);
		
		pfPassword = new JPasswordField();
		pfPassword.setBounds(90, 66, 253, 20);
		panel.add(pfPassword);
		
		pfRepeat = new JPasswordField();
		pfRepeat.setBounds(90, 94, 253, 20);
		panel.add(pfRepeat);
		
		JLabel lblRepeat = new JLabel(I18N.getInstance().getString(I18N.REPEAT));
		lblRepeat.setHorizontalTextPosition(SwingConstants.CENTER);
		lblRepeat.setBounds(10, 97, 65, 14);
		panel.add(lblRepeat);
		
		cbxRole = new JComboBox<String>();
		cbxRole.setBounds(161, 11, 206, 20);
		frmEditor.getContentPane().add(cbxRole);
		
		lblRole = new JLabel(I18N.getInstance().getString(I18N.ROLE));
		lblRole.setBounds(136, 14, 30, 14);
		frmEditor.getContentPane().add(lblRole);
					
		btnCancel.setBounds(278, 190, 89, 23);
		btnSave.setBounds(179, 190, 89, 23);
		
		frmEditor.setBounds(100, 100, 385, 256);
		fillRolesCombo();
		loadData();				
		frmEditor.setLocationRelativeTo(null);		
	}				
	
	@Override
	protected void loadData() {		
		
		if(isInEditMode() || isInMultiEditMode()) {
			User user = (User) item;
			
			tfUsername.setText(user.getUsername());
			cbxRole.setSelectedItem(user.getRole().getName());
		}
	}
	
	@Override
	protected void save() {
		if(validateInput()) {
			EntityManager manager = new EntityManager(new StaticStatementsGenerator());
			User user = (User) item;
			boolean success = false;
			StringBuilder sb = new StringBuilder();
			sb.append(pfPassword.getPassword());
			
			if(isInEditMode()) {							
				user.setUsername(tfUsername.getText());
				user.setPassword(sb.toString());
				user.setRoleIndex(getSelectedRoleId());
				
				success = manager.update(item);
			} else {
				item = new User(0, tfUsername.getText(), sb.toString(), getSelectedRoleId());
				success = manager.insert(item, true);
				clearAll();
			}
			managerFrame.refresh();
			managerFrame.updateTable();
			
			if(success)
				frmEditor.dispose();
		}
	}
		
	@Override
	protected boolean validateInput() {
		boolean success = true;
		
		if(tfUsername.getText().length() == 0) {
			JOptionPane.showMessageDialog(getFrame(), I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);	
			success = false;
		}
		
		StringBuilder sb = new StringBuilder();
		StringBuilder sbR = new StringBuilder();
		
		sb.append(pfPassword.getPassword());
		sbR.append(pfRepeat.getPassword());
		
		if(success && !sb.toString().equals(sbR.toString())) {
			JOptionPane.showMessageDialog(getFrame(), I18N.getInstance().getString(I18N.PASSWORD_MATCH_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);	
			success = false;
		}
		
		if(success && sb.toString().length() == 0) {
			JOptionPane.showMessageDialog(getFrame(), I18N.getInstance().getString(I18N.EMPTY_PASSWORD_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);	
			success = false;
		}
		
		if(success && sb.toString().length() < 8) {
			JOptionPane.showMessageDialog(getFrame(), I18N.getInstance().getString(I18N.PASSWORD_TOO_SHORT_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);	
			success = false;
		}
		
		return success;
	}		
}
