package view.editors;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import view.I18N;
import view.managers.TableManagerFrame;

import java.awt.Component;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;

import model.db.entities.Entity;

/**
 * <h1>Ventana abstracta de inserci�n/edici�n</h1>
 */
public abstract class AbstractEditorDialog {

	/** Modo edici�n. */
	private boolean editMode;
	
	/** Ventana padre. */
	private JFrame parent;
	
	/** Bot�n cancelar. */
	protected JButton btnCancel;
	
	/** Bot�n guardar. */
	protected JButton btnSave;
	
	/** Archivo XML que describe la entidad. */
	protected String entityXML;
	
	/** Di�logo. */
	protected JDialog frmEditor;		
	
	/** Elemento a insertar/editar. */
	protected Entity item;
	
	/** Lista de elementos de una edici�n m�ltiple. */
	protected ArrayList<Entity> items;	
	
	/** TableManagerFrame al que pertence. */
	protected TableManagerFrame managerFrame;
	
	/** Entidad modelo. */
	protected Entity model;
	
	/** Modo multiedici�n. */
	protected boolean multiEditMode;

	/**
	 * Constructor.
	 * @param parent Ventana padre.
	 */
	public AbstractEditorDialog(JFrame parent) {
		this.parent = parent;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param parent Ventana padre.
	 * @param model Entidad modelo.
	 * @param entityXML Archivo XML que describe a la entidad modelo.
	 */
	public AbstractEditorDialog(JFrame parent, Entity model, String entityXML) {
		this.parent = parent;
		this.entityXML = entityXML;
		this.model = model;
	}					
	
	/**
	 * Limpia todos los campos de texto. Busca din�micamente los campos de
	 * texto que se puedan haber agregado en implementaciones de las clases
	 * hijas y los limpia.
	 */
	protected void clearAll() {
		Component[] comps = getFrame().getContentPane().getComponents();
		for (Component component : comps) {
			if(component instanceof JTextField) {
				((JTextField) component).setText("");
			}
		}
	}
	
	/**
	 * Crea los componentes. 
	 */
	protected void createGUI() {
		btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.setBounds(248, 109, 89, 23);
		frmEditor.getContentPane().add(btnCancel);
		
		btnSave = new JButton(I18N.getInstance().getString(I18N.SAVE));
		btnSave.setBounds(149, 109, 89, 23);		
		frmEditor.getContentPane().add(btnSave);
		frmEditor.getRootPane().setDefaultButton(btnSave);
		
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getFrame().dispose();
			}
		});
		
		btnSave.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent ae) {
				save();
			}
		});
	}
	
	/**
	 * Inicia el contenido del frame.
	 */
	protected void initialize() {
		frmEditor = new JDialog(parent, true);		
		frmEditor.setType(Type.UTILITY);
		frmEditor.setResizable(false);
		frmEditor.setBounds(100, 100, 363, 182);		
		frmEditor.setLocationRelativeTo(null);	
		
		frmEditor.getContentPane().setLayout(null);
		
		String title  = "";
		
		if(editMode)
			title = I18N.getInstance().getString(I18N.EDIT);
		else
		if(multiEditMode)
			title = I18N.getInstance().getString(I18N.MULTI_EDIT);
		else
			title = I18N.getInstance().getString(I18N.INSERT);
		
		
		frmEditor.setTitle(title);		
		createGUI();
	}
		
	/**
	 * Carga la informaci�n del elemento a editar cuando 
	 * est� en modo de edici�n. 
	 */
	protected abstract void loadData();
	
	/**
	 * Acci�n de guardar.
	 */
	protected abstract void save();
	
	/**
	 * Verifica que los campos de texto no esten en blanco. Busca din�micamente los campos de
	 * texto que se puedan haber agregado en implementaciones de las clases
	 * hijas y verifica que no quede alguno en blanco.
	 * 
	 * @return Devuelve true si no hay campos en blanco, de lo contrario devuelve false.
	 */
	protected boolean validateEmptyFields() {
		Component[] comps = getFrame().getContentPane().getComponents();
		Component component;
		boolean success = true;
		int i = 0;
		
		while ((success) && (i < comps.length)) {
			component = comps[i];
			if(component instanceof JTextField && component.isEnabled()) {
				if(((JTextField) component).getText().length() == 0) {
					success = false;
				}
			}
			
			if(component instanceof JComboBox ) {
				String text = String.valueOf(((JComboBox<?>) component).getSelectedItem());
				if(text.length() == 0 || text.equals("null")) {
					success = false;
				}
			}
			
			i++;
		}
		
		return success;
	}
	
	/**
	 * Realiza las validaciones de los campos de datos. 
	 * @return false si ha ocurrido un error de validaci�n.
	 */
	protected boolean validateInput() {
		boolean success = validateEmptyFields();
		if(!success) {
			JOptionPane.showMessageDialog(getFrame(), I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);			
		}
		return success;
	}
	
	/**
	 * Crea el di�logo en modo inserci�n/edici�n.
	 * @param itemForEdition Elemento que se desea editar.
	 * @param managerFrame Ventana de gesti�n de tabla asociada a este di�logo.
	 */
	public void create(Entity itemForEdition, TableManagerFrame managerFrame) {
		if(itemForEdition != null) {
			item = itemForEdition;			
			editMode = true;
		} else {
			editMode = false;
		}
		
		multiEditMode = false;
		this.managerFrame = managerFrame;
		initialize();
		frmEditor.setVisible(true);
	}

	/**
	 * Crea el di�logo en modo multiedici�n.
	 * @param items Lista de elementos que se desea editar.
	 * @param managerFrame Ventana de gesti�n de tabla asociada a este di�logo.
	 */
	public void createMultiEdition(ArrayList<Entity> items, TableManagerFrame managerFrame) {
		setList(items);
		
		if(items.size() > 0)
			item = items.get(0);
		
		this.managerFrame = managerFrame;
		initialize();
		frmEditor.setVisible(true);
	}
	
	/**
	 * Devuelve el JFrame de la ventana.
	 * @return JFrame.
	 */
	public JDialog getFrame() {
		return frmEditor;
	}
	
	/**
	 * Indica si se encuentra en modo edici�n.
	 * @return Devuelve true si est� en modo edici�n.
	 */
	public boolean isInEditMode() {
		return editMode;
	}		
	
	/**
	 * Indica si se encuentra en modo multiedici�n.
	 * @return Devuelve true si est� en modo multiedici�n.
	 */
	public boolean isInMultiEditMode() {
		return multiEditMode;
	}
	
	/**
	 * Define el contenido de la lista de elementos para multiedici�n.
	 * @param items Elementos para edici�n m�ltiple.
	 */
	public void setList(ArrayList<Entity> items) {
		this.items = items;
		multiEditMode = true;
		editMode = false;
	}
}
