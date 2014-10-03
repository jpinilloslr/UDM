package model.managers;

import model.db.entities.Entity;
import model.db.entities.EntityLoader;

/**
 * <h1>Información del gestor</h1>
 */
public class ManagerInfo {

	/** Archivo XML con la descripción de la entidad vinculada al gestor. */
	private String entityXML;

	/** Nombre. */
	private String name;

	/** Permite operaciones de eliminación. */
	private boolean showDeleteOption;

	/** Permite operaciones de edición. */
	private boolean showEditOption;

	/** Permite operaciones de inserción. */
	private boolean showInsertOption;

	/**
	 * Constructor.
	 */
	public ManagerInfo() {
		showInsertOption = true;
		showEditOption = true;
		showDeleteOption = true;
	}

	/**
	 * Constructor.
	 * 
	 * @param name Nombre. Es el texto que se muestra al usuario o se envia al
	 *             motor de i18n.
	 * @param entityXML Archivo XML con la descripción de la entidad vinculada al
	 *             gestor.
	 * @param showInsertOption Permite operaciones de inserción.
	 * @param showEditOption Permite operaciones de edición.
	 * @param showDeleteOption Permite operaciones de eliminación.
	 */
	public ManagerInfo(String name, String entityXML, boolean showInsertOption,
			boolean showEditOption, boolean showDeleteOption) {
		this.name = name;
		this.entityXML = entityXML;
		this.showInsertOption = showInsertOption;
		this.showEditOption = showEditOption;
		this.showDeleteOption = showDeleteOption;
	}

	/**
	 * Devuelve el archivo XML que describe a la 
	 * entidad asociada al gestor.
	 * 
	 * @return Nombre de arcihvo XML.
	 */
	public String getEntityXML() {
		return entityXML;
	}

	/**
	 * Devuelve el nombre.
	 * @return Nombre.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuelve el nombre de acceso requerido para el gestor.
	 * @return Nombre de acceso requerido.
	 */
	public String getRequiredAccess() {
		Entity ent = EntityLoader.getInstance().load(entityXML);
		return ent.getRequiredAccess();
	}

	/**
	 * Define el nombre de archivo XML que describe a la 
	 * entidad vinculada al gestor.
	 * 
	 * @param entityXML Nombre de archivo XML.
	 */
	public void setEntityXML(String entityXML) {
		this.entityXML = entityXML;
	}

	/**
	 * Define el nombre del gestor.
	 * @param name Nombre del gestor.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Define si permitirá operaciones de eliminación.
	 * 
	 * @param showDeleteOption Permite operaciones de 
	 * 							eliminación.
	 */
	public void setShowDeleteOption(boolean showDeleteOption) {
		this.showDeleteOption = showDeleteOption;
	}

	/**
	 * Define si permitirá operaciones de edición.
	 * 
	 * @param showEditOption Permite operaciones de 
	 * 							edición.
	 */
	public void setShowEditOption(boolean showEditOption) {
		this.showEditOption = showEditOption;
	}

	/**
	 * Define si permitirá operaciones de inserción.
	 * 
	 * @param showInsertOption Permite operaciones de 
	 * 							inserción.
	 */
	public void setShowInsertOption(boolean showInsertOption) {
		this.showInsertOption = showInsertOption;
	}

	/**
	 * Indica si permite operaciones de eliminación.
	 * @return Permite operaciones de eliminación.
	 */
	public boolean showDeleteOption() {
		return showDeleteOption;
	}

	/**
	 * Indica si permite operaciones de edición.
	 * @return Permite operaciones de edición.
	 */
	public boolean showEditOption() {
		return showEditOption;
	}

	/**
	 * Indica si permite operaciones de inserción.
	 * @return Permite operaciones de inserción.
	 */
	public boolean showInsertOption() {
		return showInsertOption;
	}
}
