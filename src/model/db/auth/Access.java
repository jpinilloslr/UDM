package model.db.auth;

import model.db.entities.Entity;
import model.db.entities.FieldType;

/**
 * <h1>Acceso</h1>
 * 
 * Representa el acceso a un elemento del sistema. Esta es una entidad especial
 * independiente de la lógica de negocio del sistema.
 */
public class Access extends Entity {
	
	/** Columna id. */
	public static final String ID = "id";

	/** Columna nombre. */
	public static final String NAME = "name";
	
	/** Nombre de la tabla. */
	public static final String TABLE = "udm_access";

	/**
	 * Constructor.
	 */
	public Access() {
		super();
		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param id Id.
	 * @param name Nombre
	 */
	public Access(int id, String name) {
		super();
		init();
		setId(id);
		setName(name);
	}

	/**
	 * Define los campos de esta entidad.
	 */
	private void init() {
		addField(ID, 0, FieldType.FT_INT, true, true);
		addField(NAME, "", FieldType.FT_STRING, false, false);
		setTableName(TABLE);
	}

	/**
	 * Devuelve el id.
	 * @return Id.
	 */
	public int getId() {
		return getAsInteger(ID);
	}

	/**
	 * Devuelve el nombre.
	 * @return Nombre.
	 */
	public String getName() {
		return getAsString(NAME);
	}

	/**
	 * Define el id.
	 * @param id Id.
	 */
	public void setId(int id) {
		setField(ID, id);
	}

	/**
	 * Define el nombre.
	 * @param name Nombre
	 */
	public void setName(String name) {
		setField(NAME, name);
	}
}
