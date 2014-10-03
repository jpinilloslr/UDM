package model.db.entities;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * <h1>Campo</h1>
 * 
 * Representa un campo de una entidad. Describe todos los metadatos necesarios
 * para la generaci�n din�mica de consultas.
 */
public class Field {

	/** Autoincrementable. */
	private boolean autoinc;

	/** Tipo. */
	private FieldType fieldType;

	/** Informaci�n de llave for�nea. */
	private ForeignKeyInfo foreignKeyInfo;

	/** Marcador de borrado l�gico. */
	private boolean logicDeleteMarker;

	/** Nombre. */
	private String name;

	/** Llave primaria. */
	private boolean primaryKey;

	/** El campo no puede ser null. */
	private boolean required;

	/** Referencia de b�squeda. */
	private boolean searchable;
	
	/** Valor. */
	private Object value;

	/**
	 * Constructor.
	 */
	public Field() {
		this.required = false;
		this.searchable = false;
		this.logicDeleteMarker = false;
		this.foreignKeyInfo = null;
	}

	/**
	 * Crea un campo.
	 * 
	 * @param name Nombre.
	 * @param value Valor inicial.
	 * @param fieldType Tipo.
	 * @param primaryKey Es llave primaria.
	 * @param autoinc Es autoincrementable.
	 */
	public Field(String name, Object value, FieldType fieldType,
			boolean primaryKey, boolean autoinc) {
		this.name = name;
		this.value = value;
		this.fieldType = fieldType;
		this.primaryKey = primaryKey;
		this.searchable = false;
		this.autoinc = autoinc;
		this.logicDeleteMarker = false;
		this.required = false;
		this.foreignKeyInfo = null;
	}

	/**
	 * Crea un campo.
	 * 
	 * @param name Nombre.
	 * @param value Valor inicial.
	 * @param fieldType Tipo.
	 * @param primaryKey Es llave primaria.
	 * @param autoinc Es autoincrementable.
	 * @param logicDeleteMarker Marcador de borrado l�gico.
	 */
	public Field(String name, Object value, FieldType fieldType,
			boolean primaryKey, boolean autoinc, boolean logicDeleteMarker) {
		this.name = name;
		this.value = value;
		this.fieldType = fieldType;
		this.primaryKey = primaryKey;
		this.searchable = false;
		this.autoinc = autoinc;
		this.logicDeleteMarker = logicDeleteMarker;
		this.required = false;
		this.foreignKeyInfo = null;
	}

	/**
	 * Crea un campo.
	 * 
	 * @param name Nombre.
	 * @param value Valor inicial.
	 * @param fieldType Tipo.
	 * @param primaryKey Es llave primaria.
	 * @param autoinc Es autoincrementable.
	 * @param foreignKeyInfo Informaci�n de llave for�nea.
	 */
	public Field(String name, Object value, FieldType fieldType,
			boolean primaryKey, boolean autoinc, ForeignKeyInfo foreignKeyInfo) {
		this.name = name;
		this.value = value;
		this.fieldType = fieldType;
		this.primaryKey = primaryKey;
		this.searchable = false;
		this.autoinc = autoinc;
		this.logicDeleteMarker = false;
		this.required = false;
		this.foreignKeyInfo = foreignKeyInfo;
	}

	/**
	 * Clonar.
	 * @param field Campo a clonar.
	 */
	public void clone(Field field) {
		this.autoinc = field.isAutoincremental();
		this.fieldType = field.getFieldType();
		this.foreignKeyInfo = field.getForeignKeyInfo();
		this.logicDeleteMarker = field.isLogicDeleteMarker();
		this.name = field.getName();
		this.primaryKey = field.isPrimaryKey();
		this.searchable = field.isSearchable();
		this.value = field.getValue();
		this.required = field.isRequired();
	}

	/**
	 * Devuelve el tipo.
	 * @return Tipo.
	 */
	public FieldType getFieldType() {
		return fieldType;
	}

	/**
	 * Devuelve la informaci�n de llave for�nea.
	 * @return Informaci�n de llave for�nea.
	 */
	public ForeignKeyInfo getForeignKeyInfo() {
		return foreignKeyInfo;
	}

	/**
	 * Devuelve el nombre.
	 * @return Nombre.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuelve el valor.
	 * @return Valor.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Indica si es autoincrementable.
	 * @return true si es autoincrementable.
	 */
	public boolean isAutoincremental() {
		return autoinc;
	}

	/**
	 * Indica si es marcador de borrado l�gico.
	 * @return true si es marcador de borrado l�gico, 
	 * 			false si no.
	 */
	public boolean isLogicDeleteMarker() {
		return logicDeleteMarker;
	}

	/**
	 * Es llave primaria.
	 * @return Devuelve true si es llave primaria.
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Es requerido. No puede ser nulo.
	 * @return Requerido.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Indica si es una referencia de b�squeda.
	 * @return true si es una referencia de b�squeda, 
	 * 			false si no.
	 */
	public boolean isSearchable() {
		return searchable;
	}

	/**
	 * Redefine el campo a su valor predeterminado seg�n el tipo.
	 */
	public void reset() {
		switch (fieldType) {
		case FT_BOOLEAN:
			setValue(false);
			break;
		case FT_INT:
			setValue(0);
			break;
		case FT_DOUBLE:
			setValue(0.0f);
			break;
		case FT_DATE:
			setValue(new Date(0));
			break;			
		case FT_DATETIME:
			setValue(new Timestamp(0));
			break;
		default:
			setValue("");
			break;
		}
	}

	/**
	 * Define si es autoincrementable.
	 * @param autoincremental Autoincrementable.
	 */
	public void setAutoincremental(boolean autoincremental) {
		this.autoinc = autoincremental;
	}

	/**
	 * Define el tipo.
	 * @param fieldType tipo.
	 */
	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * Define la informaci�n de llave for�nea.
	 * @param foreignKeyInfo Informaci�n de llave for�nea.
	 */
	public void setForeignKeyInfo(ForeignKeyInfo foreignKeyInfo) {
		this.foreignKeyInfo = foreignKeyInfo;
	}

	/**
	 * Define si es marcador de borrado l�gico.
	 * @param logicDeleteMarker Marcador de borrado l�gico.
	 */
	public void setLogicDeleteMarker(boolean logicDeleteMarker) {
		this.logicDeleteMarker = logicDeleteMarker;
	}

	/**
	 * Define el nombre.
	 * @param name Nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Define si es llave primaria.
	 * @param primaryKey Llave primaria.
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Define si es requerido.
	 * @param required Requerido
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Define si es una referencia de b�squeda.
	 * @param searchable Referencia de b�squeda.
	 */
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	/**
	 * Define el valor.
	 * @param value Valor.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getName() + "=" + String.valueOf(getValue());
	}
}
