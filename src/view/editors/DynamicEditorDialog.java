package view.editors;

import java.awt.SystemColor;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import view.I18N;
import view.utils.GUIUtils;

import model.PreferencesManager;
import model.db.EntityManager;
import model.db.StaticStatementsGenerator;
import model.db.entities.Entity;
import model.db.entities.EntityLoader;
import model.db.entities.Field;
import model.db.entities.FieldType;

/**
 * Formulario Insertar/Editar dinámico. La interface
 * de este formulario se genera dinámicamente a
 * partir de los metadatos de la entidad que se está 
 * insertando/editando. Cada componente se crea según
 * el tipo de dato de su campo asociado.
 */
public class DynamicEditorDialog extends AbstractEditorDialog {					

	/** Lista de componentes asociados a los campos de la entidad. */
	private ArrayList<AssociatedComponent> components;
	
	/** Espaciado entre columnas de componentes. */
	private static final int X_SPACE = 200;

	/**
	 * Constructor.
	 * 
	 * @param parent Ventana padre.
	 * @param model Entidad modelo.
	 * @param entityXML Archivo de descripción de la entidad modelo.
	 */
	public DynamicEditorDialog(JFrame parent, Entity model, String entityXML) {
		super(parent, model, entityXML);
	}	
	
	/**
	 * Carga una configuración opcional para 
	 * esta ventana de edición. Esta configuración define
	 * algunos parámetros de visualización de los componente de
	 * la ventana.
	 */
	private void loadEditorConfig() {
		String filename = PreferencesManager.getInstance().getEditorsPath() + model.getTableName() + ".xml";
		File file = new File(filename);

		if(file.exists()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();	
			Document dom = null;

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				dom = db.parse(new FileInputStream(filename));		

				if(null != dom) {
					Element root = dom.getDocumentElement();					
					
					applyComboValues(root);
					applyForceAsCombo(root);
					applyTextFieldsDomains(root);
				}
			}catch(Exception exc) {
				JOptionPane.showMessageDialog(null, "DynamicInserEditFrame Error: " + exc.getMessage(), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Aplica valores a los ComboBox.
	 * @param root Nodo XML.
	 */
	private void applyComboValues(Element root) {
		NodeList nl = root.getElementsByTagName("comboValues");

		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (org.w3c.dom.Element)nl.item(i);
				String compName = el.getAttribute("name");
				String depCompName = el.getAttribute("dependency");
				String query = el.getTextContent().trim();

				AssociatedComponent component = getComponentByFieldName(compName);
				AssociatedComponent dependencyComp = getComponentByFieldName(depCompName);

				if(component != null) {
					if(null == dependencyComp)
						component.loadComboWithQuery(query);
					else {
						dependencyComp.createDependency(component, query);
						dependencyComp.loadData();
					}
					component.loadData();
				}
			}
		}
	}
	
	/**
	 * Fuerza a un componente a mostrarse como un ComboBox.
	 * @param root Nodo XML.
	 */
	private void applyForceAsCombo(Element root) {
		NodeList nl = root.getElementsByTagName("forceAsCombo");

		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (org.w3c.dom.Element)nl.item(i);
				String compName = el.getAttribute("name");
				String values = el.getTextContent().trim();
				AssociatedComponent component = getComponentByFieldName(compName);

				if(component != null) {
					String[] modelContent = values.split(",");
					component.forceAsComboBox(modelContent);
					component.loadData();
				}
			}
		}
	}
	
	/**
	 * Restringe el dominio de valores posibles para un TextField.
	 * @param root Nodo XML.
	 */
	private void applyTextFieldsDomains(Element root) {
		NodeList nl = root.getElementsByTagName("textfieldDomain");

		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (org.w3c.dom.Element)nl.item(i);
				String compName = el.getAttribute("name");
				String domain = el.getTextContent().trim();
				AssociatedComponent component = getComponentByFieldName(compName);

				if(component != null) {
					GUIUtils.setTextFieldDomain((JTextField) component.getComponent(), domain);
				}
			}
		}
	}
	
	/**
	 * Devuelve un componente asociado a partir de su nombre.
	 * @param name Nombre.
	 * @return Componente asociado si se encuentra, null si no.
	 */
	private AssociatedComponent getComponentByFieldName(String name) {
		AssociatedComponent comp = null;
		Iterator<AssociatedComponent> iter = components.iterator();
		AssociatedComponent current;

		while(comp == null && iter.hasNext()) {
			current = iter.next();
			if(current.getField().getName().equals(name)) {
				comp = current;
			}
		}

		return comp;
	}

	/**
	 * Si se está realizando una edición múltiple habilita
	 * únicamente los componentes que coinciden con los campos
	 * con contenido común en los elementos a editar.
	 */
	private void checkCommonFields() {
		String val1, val2, fieldName;
		
		if(items.size() > 0) {
			Entity first = items.get(0);
			item = first;
			Entity current;

			for(int i=1; i<items.size(); i++) {
				current = items.get(i);

				for(int j=0; j<first.getFields().size(); j++) {
					val1 = String.valueOf(first.getFields().get(j).getValue());
					val2 = String.valueOf(current.getFields().get(j).getValue());

					if(!val1.equals(val2)) {
						fieldName = first.getFields().get(j).getName();
						AssociatedComponent comp = getComponentByFieldName(fieldName);

						if(null != comp) {
							comp.clear();
							comp.setEnable(false);
						}
					} 
				}
			}		
			
			Iterator<AssociatedComponent> iterComps = components.iterator();
			boolean enable = false;
			
			while(!enable && iterComps.hasNext()) {
				if(iterComps.next().isEnabled()) {
					enable = true;
				}
			}
			
			if(!enable) {
				JOptionPane.showMessageDialog(frmEditor, I18N.getInstance().getString(I18N.NO_ENABLE_COMPONENTS), 
						frmEditor.getTitle(), JOptionPane.INFORMATION_MESSAGE);
				btnSave.setEnabled(false);
			}
		}
	}

	@Override
	protected void save() {
		if(validateInput()) {
			EntityManager manager = new EntityManager(new StaticStatementsGenerator());
			boolean success = false;

			for(int i=0; i<components.size(); i++) {
				if(components.get(i).isEnabled())
					components.get(i).save();
			}

			if(isInEditMode()) {												
				success = manager.update(model);
			} 
			else 
				if (isInMultiEditMode()) {
					success = true;
					
					for(int i=0; i<items.size(); i++) {
						Iterator<AssociatedComponent> iterComps = components.iterator();
						AssociatedComponent component;
						
						while(iterComps.hasNext()) {
							component = iterComps.next();
							
							if(component.isEnabled()) {
								String fieldName = component.getField().getName();
								Object fieldValue = component.getField().getValue();
								
								items.get(i).setField(fieldName, fieldValue);
							}
						}
						success &= manager.update(items.get(i));
					}
				} else {
					success = manager.insert(model, true);				
				}
			managerFrame.refresh();
			managerFrame.updateTable();

			if(success)
				frmEditor.dispose();
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void createGUI() {
		super.createGUI();
		components = new ArrayList<AssociatedComponent>();	
		model = EntityLoader.getInstance().load(entityXML);

		if(isInEditMode()) {
			model = item;
		}

		if(isInMultiEditMode())
			model = items.get(0);

		Iterator<Field> iter = model.getFields().iterator();
		Field current;

		int x = 10;
		int y = 10;
		int maxY = 10;
		int count = 0;		
		
		int visibleColumns = 0;

		while(iter.hasNext()) {
			current = iter.next();
			if((!current.isPrimaryKey() && !current.isLogicDeleteMarker()) || current.getForeignKeyInfo() != null) {
				visibleColumns++;
			}
		}
		
		int elementsPerColumn = visibleColumns/2;
		
		if(visibleColumns%2 != 0)
			elementsPerColumn++;
		
		if(visibleColumns < 4)
			elementsPerColumn = 4;
		
		iter = model.getFields().iterator();
		JComponent last = null;
		
		while(iter.hasNext()) {
			current = iter.next();

			if((!current.isPrimaryKey() && !current.isLogicDeleteMarker()) || current.getForeignKeyInfo() != null) {
				
				if(count%elementsPerColumn == 0 && count > 0) {
					x += X_SPACE;
					y = 10;
				}
				
				AssociatedComponent comp = new AssociatedComponent(current);
				comp.locate(frmEditor.getContentPane(), x, y);
				components.add(comp);
				y += 50;
				count++;
				
				if(last != null) {
					last.setNextFocusableComponent(comp.getComponent());					
					last = comp.getComponent();
				} else
					last = comp.getComponent();

				if(y > maxY)
					maxY = y;
				
				if(current.isPrimaryKey() && current.getForeignKeyInfo() != null && (isInEditMode() || isInMultiEditMode()))
					comp.setEnable(false);
			}				
			
		}		

		x += X_SPACE;

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.controlHighlight);
		panel.setBounds(10, maxY, x-20, 1);
		frmEditor.getContentPane().add(panel);

		btnCancel.setBounds(x-89-10, maxY+7, 89, 23);
		btnSave.setBounds(x-89-89-15, maxY+7, 89, 23);


		frmEditor.setBounds(100, 100, x+10, maxY+70);			
		loadData();			
		if(isInMultiEditMode())
			checkCommonFields();
		frmEditor.setLocationRelativeTo(null);		

		loadEditorConfig();
	}

	@Override
	protected void loadData() {
		/* Esta función no es necesaria utilizando AssociatedComponent ya que 
		 * el componente se encarga de la sincronización automática con el campo. 
		 */
	}		
	
	@Override
	protected boolean validateInput() {
		Iterator<AssociatedComponent> iter = components.iterator();
		AssociatedComponent current;
		boolean success = true;
		
		while(success && iter.hasNext()) {
			current = iter.next();
			
			if(current.isEnabled()) {
			
				if(current.getField().getFieldType() == FieldType.FT_STRING && 
						current.getField().isRequired() && 
						current.getComponent() instanceof JTextField) {
					String value =  ((JTextField) current.getComponent()).getText();
					
					if(null == value || value.length() == 0) {
						success = false;
						JOptionPane.showMessageDialog(getFrame(), 
								I18N.getInstance().getString(current.getField().getName()) + ": " +
								I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), 
								I18N.getInstance().getString(I18N.ERROR), 
								JOptionPane.ERROR_MESSAGE);
					}
				} else			
				if(current.getComponent() instanceof JComboBox<?>) {
					if(((JComboBox<?>) current.getComponent()).getSelectedIndex() == -1) {
						success = false;
						JOptionPane.showMessageDialog(getFrame(), 
								I18N.getInstance().getString(current.getField().getName()) + ": " +
								I18N.getInstance().getString(I18N.EMPTY_FIELDS_ERROR), 
								I18N.getInstance().getString(I18N.ERROR), 
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
		return success;
	}
}
