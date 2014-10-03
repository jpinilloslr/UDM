package model.views;

import model.db.entities.FieldType;

/**
 * <h1>Parámetro</h1>
 * 
 * Describe un parámetro del diálogo de parametrización.
 */
public class Parameter {
	
	/** Nombre original del campo del que depende este. */
	private String dependency;

	/** Nombre original del campo al que se asocia el parámetro. */
	private String name;

	/** Consulta que genera el dominio de valores posibles para este parámetro. */
	private String query;

	/** Tipo de dato del campo. */
	private FieldType type;

	/**
	 * Constructor.
	 */
	public Parameter() {
	}

	/**
	 * Constructor.
	 * 
	 * @param name Nombre original del campo al que se 
	 * 				aplica el parámetro.
	 * @param dependency Nombre original del campo del 
	 * 				que depende este, puede quedar
	 *				en blanco si no depende de ninguno.
	 * @param type Tipo de dato del campo.
	 * @param query Consulta que genera el dominio de 
	 * 				valores posibles para este parámetro.
	 */
	public Parameter(String name, String dependency, FieldType type,
			String query) {
		super();
		this.name = name;
		this.dependency = dependency;
		this.type = type;
		this.setQuery(query);
	}

	/**
	 * Devuelve el nombre original del campo del que 
	 * depende este, puede estar en blanco si no depende 
	 * de ninguno.
	 * 
	 * @return Nombre del campo dependencia.
	 */
	public String getDependency() {
		return dependency;
	}

	/**
	 * Devuelve el nombre original del campo al que 
	 * se asocia el parámetro.
	 * 
	 * @return Nombre original del campo.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuelve la consulta que genera el dominio de 
	 * valores posibles para este parámetro.
	 * 
	 * @return Consulta.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Devuelve el tipo de dato del campo.
	 * @return Tipo de dato del campo.
	 */
	public FieldType getType() {
		return type;
	}

	/**
	 * Define el nombre original del campo del que depende 
	 * este, puede estar en blanco si no depende de ninguno.
	 * 
	 * @param dependency Nombre original del campo dependencia.
	 */
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	/**
	 * Define el nombre original del campo al que se aplica 
	 * el parámetro.
	 * 
	 * @param name Nombre original del campo.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Define la consulta que genera el dominio de valores 
	 * posibles para este parámetro.
	 * 
	 * @param query Consulta.
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Define el tipo de dato del campo.
	 * @param type Tipo de dato.
	 */
	public void setType(FieldType type) {
		this.type = type;
	}
}
