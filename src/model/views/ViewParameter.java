package model.views;

import java.sql.Timestamp;

import model.db.entities.FieldType;

/**
 * <h1>Parámetro de vista</h1>
 */
public class ViewParameter {

	/** Nombre original del campo a parametrizar en la vista. */
	private String name;

	/** Tipo de dato del parámetro. */
	private FieldType type;

	/** Valor específico del parámetro. */
	private Object value;

	/**
	 * Constructor.
	 */
	public ViewParameter() {
	}

	/**
	 * Constructor.
	 * 
	 * @param name Nombre original del campo a parametrizar en la vista.
	 * @param value Valor específico del parámetro.
	 * @param type Tipo de dato del parámetro.
	 */
	public ViewParameter(String name, Object value, FieldType type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
	}

	/**
	 * Devuelve el nombre original del campo a parametrizar 
	 * en la vista.
	 * 
	 * @return Nombre el campo.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuele el tipo de dato del campo.
	 * @return Tipo de dato.
	 */
	public FieldType getType() {
		return type;
	}

	/**
	 * Devuelve el valor del parámetro.
	 * @return Valor.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Devuelve el valor como Boolean.
	 * @return Valor como Boolean
	 */
	public Boolean getValueAsBoolean() {
		return (Boolean) value;
	}

	/**
	 * Devuelve el valor como Integer.
	 * @return Valor como Integer
	 */
	public Integer getValueAsInteger() {
		return (Integer) value;
	}
	
	/**
	 * Devuelve el valor como Float.
	 * @return Valor como Integer
	 */
	public Float getValueAsFloat() {
		return (Float) value;
	}	
	
	/**
	 * Devuelve el valor como Timestamp.
	 * @return Valor como Integer
	 */
	public Timestamp getValueAsTimestamp() {
		return (Timestamp) value;
	}

	/**
	 * Devuelve el valor como String.
	 * @return Valor como String
	 */
	public String getValueAsString() {
		return String.valueOf(value);
	}

	/**
	 * Define el nombre original del campo a parametrizar 
	 * en la vista.
	 * 
	 * @param name Nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Define el tipo de dato del campo.
	 * @param type Tipo de dato.
	 */
	public void setType(FieldType type) {
		this.type = type;
	}

	/**
	 * Define el valor adquirido para el parámetro.
	 * @param value Valor.
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
