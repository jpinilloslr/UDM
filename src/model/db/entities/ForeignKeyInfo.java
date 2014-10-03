package model.db.entities;

/**
 * <h1>Información de llave foránea</h1>
 * 
 * Describe los metadatos que definen a un campo como llave foránea.
 * Mediante esta información es posible saber a qué entidad se refiere
 * un campo descrito como llave foránea, el campo de referencia  y un campo 
 * sustituto en la entidad foránea, en caso que se desee sustituir el campo
 * de referencia por otro más descriptivo para el usuario. 
 */
public class ForeignKeyInfo {
	
	/** Archivo XML que describe a la entidad foránea. */
	private String entityXML;

	/** Nombre del campo de referencia. */
	private String referencedField;

	/** Nombre del campo sustituto. */
	private String substituteField;

	/**
	 * Constructor.
	 */
	public ForeignKeyInfo() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param referencedField Campo de referencia.
	 * @param substituteField Campo sustituto.
	 */
	public ForeignKeyInfo(String referencedField, String substituteField) {
		super();
		this.referencedField = referencedField;
		this.substituteField = substituteField;
	}

	/**
	 * Devuelve el archivo XML que describe a la entidad foránea.
	 * @return Nombre de archivo.
	 */
	public String getEntityXML() {
		return entityXML;
	}

	/**
	 * Devuelve el nombre del campo de referencia.
	 * @return Nombre del campo de referencia.
	 */
	public String getReferencedField() {
		return referencedField;
	}

	/**
	 * Devuelve el nombre del campo sustituto.
	 * @return Nombre del campo sustituto.
	 */
	public String getSubstituteField() {
		return substituteField;
	}

	/**
	 * Define el archivo XML que describe la entidad foránea.
	 * @param entityXML Nombre de archivo XML.
	 */
	public void setEntityXML(String entityXML) {
		this.entityXML = entityXML;
	}

	/**
	 * Define el nombre del campo de referencia.
	 * @param referencedField Nombre del campo de referencia.
	 */
	public void setReferencedField(String referencedField) {
		this.referencedField = referencedField;
	}

	/**
	 * Define el nombre del campo sustituto.
	 * @param substituteField Nombre del campo sustituto.
	 */
	public void setSubstituteField(String substituteField) {
		this.substituteField = substituteField;
	}
}
