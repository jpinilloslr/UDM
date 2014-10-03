package view.editors;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import org.jdesktop.swingx.JXDatePicker;

import model.PreferencesManager;
import model.db.DBLink;
import model.db.entities.Entity;
import model.db.entities.EntityLoader;
import model.db.entities.Field;
import model.db.entities.FieldType;
import model.db.entities.ForeignKeyInfo;
import view.I18N;

/**
 * <h1>Componente de edición asociado a un campo.</h1>
 * 
 * Esta clase asocia un campo de una entidad con un componente de edición.
 * Garantiza la sincronización entre el campo y el componente. Durante
 * la construcción de una instancia se define en tiempo de ejecución el 
 * tipo de componente físico que se usará dependiendo del tipo de dato
 * del campo.
 */
public class AssociatedComponent {
	
	/** Lista de índices de los nomencladores. */
	private LinkedList<Integer> comboIndexList;
	
	/** Componente de edición. */
	private JComponent component;
	
	/** Contenedor. */
	private Container container;
	
	/** Campo. */
	private Field field;
	
	/** Fuerza al componente a crearse como un ComboBox. */
	private boolean forcedAsCombo;
	
	/** Label */
	private JLabel label;
	
	/**
	 * Constructor.
	 * @param field Campo asociado.
	 */
	public AssociatedComponent(Field field) {
		this.field = field;
		forcedAsCombo = false;
	}
	
	/**
	 * Crea los componentes basados en el tipo de 
	 * dato del campo asociado.
	 */
	private void createComponents() {
		
		label = new JLabel(I18N.getInstance().getString(field.getName()));
		
		switch(field.getFieldType()) {
		case FT_BOOLEAN:
			component = new JCheckBox();
			((JCheckBox)component).setText(I18N.getInstance().getString(field.getName()));
			((JCheckBox)component).setSelected((Boolean) field.getValue());
			break;
		case FT_DOUBLE:
			component = new JSpinner();
			JSpinner spinnerFloat = (JSpinner) component;
			SpinnerNumberModel modelDouble = new SpinnerNumberModel();
			modelDouble.setValue(0.0d);
			modelDouble.setStepSize(0.1d);
			spinnerFloat.setModel(modelDouble);
			spinnerFloat.setValue(field.getValue());
			break;
		case FT_INT:
			if(field.getForeignKeyInfo() != null) {
				component = new JComboBox<String>();				
				loadCombo();
				
				((JComboBox<?>)component).setSelectedItem(getReferedValue(field));
			} else {
				component = new JSpinner();
				JSpinner spinnerInt = (JSpinner) component;
				SpinnerNumberModel modelInt = new SpinnerNumberModel();
				spinnerInt.setModel(modelInt);
				spinnerInt.setValue(field.getValue());
			}
			break;
		case FT_DATE:
			component = new JXDatePicker();
			JXDatePicker datePicker = (JXDatePicker) component;
			datePicker.setFormats(new String[] {PreferencesManager.getInstance().getDateFormat()});			
			Date date = (Date) field.getValue();
			if(null != date)
				datePicker.setDate(date);
			break;
		case FT_TIME:
			component = new JSpinner();
			JSpinner spinnerTime = (JSpinner) component;
			SpinnerDateModel modelTime = new SpinnerDateModel();
			spinnerTime.setModel(modelTime);
			DateEditor editorTime = new JSpinner.DateEditor(spinnerTime, PreferencesManager.getInstance().getTimeFormat());
			spinnerTime.setEditor(editorTime);
			
			Time time = (Time) field.getValue();
			if(null != time)
				spinnerTime.setValue(time);
			break;
		case FT_DATETIME:
			component = new JSpinner();
			JSpinner spinnerDateTime = (JSpinner) component;
			SpinnerDateModel modelDateTime = new SpinnerDateModel();
			spinnerDateTime.setModel(modelDateTime);
			DateEditor editorDateTime = new JSpinner.DateEditor(spinnerDateTime, PreferencesManager.getInstance().getDateTimeFormat());
			spinnerDateTime.setEditor(editorDateTime);
			
			Timestamp timestamp = (Timestamp) field.getValue();
			
			if(null != timestamp)
				spinnerDateTime.setValue(timestamp);
			break;
		default:
			component = new JTextField();
			((JTextField)component).setText(String.valueOf(field.getValue()));
			break;
		}
	}
	
	/**
	 * Devuelve el valor al que hace referencia un campo 
	 * descrito como llave foránea.
	 * 
	 * @param field Campo.
	 * @return Valor de referencia.
	 */
	private String getReferedValue(Field field) {
		String refValue = "";
		ForeignKeyInfo fkInfo = field.getForeignKeyInfo();
		Entity ref = EntityLoader.getInstance().load(fkInfo.getEntityXML());
		
		try {
			PreparedStatement ps = DBLink.getInstance().getConnection().prepareStatement("SELECT \"" + fkInfo.getSubstituteField() + "\" FROM \"" + ref.getTableName() +
					"\" WHERE " + fkInfo.getReferencedField() + "=?");

			ps.setInt(1, (Integer) field.getValue());
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				refValue = String.valueOf(rs.getObject(1));
			}
				
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
		
		return refValue;
	}
	
	/**
	 * Devuelve el id asociado al elemento seleccionado en
	 * el ComboBox.
	 * @return Id asociado.
	 */
	private int getSelectedValueId() {
		int index = -1;
		if(comboIndexList != null) {
			int i = ((JComboBox<?>)component).getSelectedIndex();
			index = comboIndexList.get(i);
		}
		return index;
	}
	
	/**
	 * Carga el combobox que describe a una llave
	 * foránea con los valores de la tabla a la que
	 * hace referencia.
	 */
	@SuppressWarnings("unchecked")
	private void loadCombo() {
		comboIndexList = new LinkedList<Integer>();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		ForeignKeyInfo fkInfo = field.getForeignKeyInfo();
		Entity ref = EntityLoader.getInstance().load(fkInfo.getEntityXML());
		String value = "";
		
		try {
			PreparedStatement ps = DBLink.getInstance().getConnection().prepareStatement("SELECT \"" + fkInfo.getSubstituteField() + "\", \"" + fkInfo.getReferencedField() + "\" FROM \"" + ref.getTableName() + "\"");			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				comboIndexList.add(Integer.valueOf(String.valueOf(rs.getObject(2))));
				
				value = String.valueOf(rs.getObject(1));
				while(model.getIndexOf(value) > -1) {
					value += " ";
				}
				
				model.addElement(value);
			}
			
			((JComboBox<String>)component).setModel(model);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Vacía el componente.
	 */
	public void clear() {
		switch(field.getFieldType()) {
		case FT_BOOLEAN:
			((JCheckBox)component).setText("");
			break;
		case FT_DATE:
			((JXDatePicker)component).setDate(new Date(System.currentTimeMillis()));
			break;
		case FT_INT:
			if(field.getForeignKeyInfo() != null) {
				
			} else {
				((JSpinner)component).setValue(0);
			}
			break;
		default:
			if(component instanceof JTextField) {
				((JTextField)component).setText("");
			}
			break;
		}
	}		
	
	/**
	 * Crea una dependencia entre dos ComboBox.
	 * @param comp Componente que contiene el ComboBox dependencia.
	 * @param query Consulta que carga el ComboBox dependiente.
	 */
	public void createDependency(final AssociatedComponent comp, final String query) {
		JComboBox<?> comboBox = (JComboBox<?>) component;
		
		comboBox.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String sql = query.replaceAll("dependency", String.valueOf(getSelectedValueId()));
				comp.loadComboWithQuery(sql);
			}
		});
	}
	
	/**
	 * Fuerza al componente a crearse como
	 * un ComboBox.
	 * 
	 * @param values Valores del ComboBox.
	 */
	@SuppressWarnings("unchecked")
	public void forceAsComboBox(String[] values) {
		int x = component.getX();
		int y = component.getY();
		boolean enable = component.isEnabled();
		
		if(null != container) {
			container.remove(component);
		}
		
		component = new JComboBox<String>();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(values);
		((JComboBox<String>) component).setModel(model);
		component.setEnabled(enable);
		
		if(null != container) {
			container.add(component);
			component.setBounds(x, y, 190, 20);
		}
		forcedAsCombo = true;
	}
	
	/**
	 * Devuelve el componente.
	 * @return Componente.
	 */
	public JComponent getComponent() {
		return component;
	}
	
	/**
	 * Devuelve el campo.
	 * @return Campo.
	 */
	public Field getField() {
		return field;
	}
	
	/**
	 * Permite saber si los controles asociados a este
	 * campo están habilitados.
	 * 
	 * @return true si están habilitados, false si no.
	 */
	public boolean isEnabled() {
		return component.isEnabled();
	}
	
	/**
	 * Carga el ComboBox que describe a un campo descrito
	 * como llave foránea por los valores obtenido de una 
	 * consulta. 
	 * 
	 * @param query Consulta. Debe devolver dos campos, uno con el id del
	 * elemento y otro con el valor asociado que se desea mostrar.
	 */
	@SuppressWarnings("unchecked")
	public void loadComboWithQuery(String query) {
		comboIndexList = new LinkedList<Integer>();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		String value = "";
		
		try {
			PreparedStatement ps = DBLink.getInstance().getConnection().prepareStatement(query);			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				comboIndexList.add(Integer.valueOf(String.valueOf(rs.getObject(1))));
				
				value = String.valueOf(rs.getObject(2));
				while(model.getIndexOf(value) > -1) {
					value += " ";
				}
				
				model.addElement(value);
			}
			
			((JComboBox<String>)component).setModel(model);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
		}
	}
			
	/**
	 * Carga los datos del campo 
	 * en el componente asociado.
	 */
	public void loadData() {
		
		switch(field.getFieldType()) {
		case FT_BOOLEAN:
			((JCheckBox)component).setSelected((Boolean) field.getValue());
			break;
		case FT_DOUBLE:
			((JTextField)component).setText(String.valueOf(field.getValue()));
			break;
		case FT_INT:
			if(field.getForeignKeyInfo() != null) {
				((JComboBox<?>)component).setSelectedItem(getReferedValue(field));
				JComboBox<?> combo = (JComboBox<?>) component;
				if(combo.getActionListeners().length > 0) {
					for (ActionListener al : combo.getActionListeners()) {
						al.actionPerformed(null);
					}
				}
				
			} else {
				if(!forcedAsCombo) {
					((JTextField)component).setText(String.valueOf(field.getValue()));
				} else {
					((JComboBox<?>)component).setSelectedItem(String.valueOf(field.getValue()));
				}
			}						
			
			break;
		case FT_DATETIME:
			((JSpinner)component).setValue(field.getValue());
			break;
		default:
			if(!forcedAsCombo) {
				((JTextField)component).setText(String.valueOf(field.getValue()));
			} else {
				((JComboBox<?>)component).setSelectedItem(String.valueOf(field.getValue()));
			}
			break;
		}
		
		
	}
	
	/**
	 * Crea los componentes asociados a este campo y los
	 * posiciona en un contenedor.
	 * 
	 * @param container Contenedor.
	 * @param x Coordenada X.
	 * @param y Coordenada Y.
	 */
	public void locate(Container container, int x, int y) {
		this.container = container;
		createComponents();
		
		if(field.getFieldType() != FieldType.FT_BOOLEAN) {
			container.add(label);
			label.setBounds(x, y, 190, 20);
			label.setVisible(true);
		}
				
		container.add(component);
		component.setVisible(true);	
		component.setBounds(x, y+22, 190, 20);
	}
	
	/**
	 * Guarda el contenido del componente en el
	 * campo asociado.
	 */
	public void save() {
		switch(field.getFieldType()) {
		case FT_BOOLEAN:
			field.setValue(((JCheckBox) component).isSelected());			
			break;
		case FT_DOUBLE:
			field.setValue((((SpinnerNumberModel) ((JSpinner)component).getModel()).getNumber()).doubleValue());
			break;
		case FT_INT:
			if(field.getForeignKeyInfo() != null) {
				field.setValue(getSelectedValueId());
			} else {
				if(!forcedAsCombo) {
					field.setValue((((SpinnerNumberModel) ((JSpinner)component).getModel()).getNumber()).intValue());
				} else {
					field.setValue(Integer.valueOf(String.valueOf(((JComboBox<?>)component).getSelectedItem())));
				}
			}
			break;
		case FT_DATE:
			long timed = ((java.util.Date) ((JXDatePicker) component).getDate()).getTime(); 
			field.setValue(new Date(timed));
			break;
		case FT_TIME:
			long timet = ((java.util.Date) ((JSpinner) component).getValue()).getTime(); 
			field.setValue(new Time(timet));
			break;
		case FT_DATETIME:
			long time = ((java.util.Date) ((JSpinner) component).getValue()).getTime(); 
			field.setValue(new Timestamp(time));
			break;
		default:
			if(!forcedAsCombo) {
				field.setValue(((JTextField)component).getText());
			} else {
				field.setValue(String.valueOf(((JComboBox<?>)component).getSelectedItem()));
			}
			break;
		}
	}

	/**
	 * Habilita o deshabilita los controles asociados
	 * a este campo.
	 * @param enable Habilitado.
	 */
	public void setEnable(boolean enable) {
		if(label != null) {
			label.setEnabled(enable);
		}
		component.setEnabled(enable);
	}
}
