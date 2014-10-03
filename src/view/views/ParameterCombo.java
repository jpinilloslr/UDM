package view.views;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import view.I18N;

import model.db.DBLink;
import model.db.entities.FieldType;

/**
 * <h1>ComboBox asociado a un parámetro</h1>
 */
public class ParameterCombo {
	
	/** ComboBox asociado. */
	private JComboBox<Object> comboBox;
	
	/** Lista de índices asociados a los elementos cargados en el ComboBox. */
	private LinkedList<Integer> comboIndexList;
	
	/** Label asociado. */
	private JLabel label;
	
	/** Nombre del componente. */
	private String name;
	
	/** Tipo de dato del parámetro. */
	private FieldType type;
	
	/**
	 * Constructor.
	 */
	public ParameterCombo() {
		label = new JLabel();
		comboBox = new JComboBox<Object>();
	}
	
	/**
	 * Devuelve el id asociado al elemento seleccionado en
	 * el ComboBox.
	 * @return Id asociado.
	 */
	private int getSelectedValueId() {
		int index = -1;
		if(comboIndexList != null) {
			int i = comboBox.getSelectedIndex();
			index = comboIndexList.get(i);
		}
		return index;
	}
	
	/**
	 * Crea relación de dependencia con otro ParameterCombo.
	 * 
	 * @param comp ParameterCombo dependiente.
	 * @param query Consulta que define el contenido del componente
	 * 				dependiente.
	 */
	public void createDependency(final ParameterCombo comp, final String query) {
		
		comboBox.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String sql = query.replaceAll("dependency", String.valueOf(getSelectedValueId()));
				comp.loadComboWithQuery(sql);
			}
		});
	}

	/**
	 * Devuelve el ComboBox.
	 * @return ComboBox.
	 */
	public JComboBox<Object> getComboBox() {
		return comboBox;
	}

	/**
	 * Devuelve el Label.
	 * @return Label.
	 */
	public JLabel getLabel() {
		return label;
	}
	
	/**
	 * Devuelve el texto del Label.
	 * @return Texto.
	 */
	public String getLabelText() {
		return label.getText();
	}
	
	/**
	 * Devuelve el nombre.
	 * @return Nombre.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Devuelve el valor seleccionado.
	 * @return Valor seleccionado.
	 */
	public Object getSelectedValue() {
		Object obj = comboBox.getSelectedItem();
		
		if(type == FieldType.FT_INT && !String.valueOf(obj).equals(I18N.getInstance().getString(I18N.ALL)))
			obj = Integer.valueOf(String.valueOf(obj));
		
		if(type == FieldType.FT_STRING && !String.valueOf(obj).equals(I18N.getInstance().getString(I18N.ALL)))
			obj = String.valueOf(obj).trim();
		
		return obj;
	}		
	
	/**
	 * Devuelve el tipo.
	 * @return Tipo.
	 */
	public FieldType getType() {
		return type;
	}		
	
	/**
	 * Carga el contenido del ComboBox a partir
	 * de una consulta.
	 * @param query Consulta.
	 */
	public void loadComboWithQuery(String query) {
		comboIndexList = new LinkedList<Integer>();
		DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>();
		String value = "";
		
		try {
			PreparedStatement ps = DBLink.getInstance().getConnection().prepareStatement(query);			
			ResultSet rs = ps.executeQuery();
			model.addElement(I18N.getInstance().getString(I18N.ALL));
			comboIndexList.add(-1);
			
			while(rs.next()) {
				comboIndexList.add(Integer.valueOf(String.valueOf(rs.getObject(1))));
				
				value = String.valueOf(rs.getObject(2));
				while(model.getIndexOf(value) > -1)
					value += " ";
				
				model.addElement(value);
			}
			
			comboBox.setModel(model);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
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
		container.add(label);
		label.setBounds(x, y, 190, 20);
		label.setVisible(true);
				
		container.add(comboBox);
		comboBox.setVisible(true);	
		comboBox.setBounds(x, y+22, 190, 20);
	}

	/**
	 * Define el texto del Label.
	 * @param text Texto.
	 */
	public void setLabelText(String text) {
		label.setText(text);
	}

	/**
	 * Define el nombre.
	 * @param name Nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Define el tipo.
	 * @param type Tipo.
	 */
	public void setType(FieldType type) {
		this.type = type;
	}
}
