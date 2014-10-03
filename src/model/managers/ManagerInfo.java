package model.managers;

import model.db.entities.Entity;
import model.db.entities.EntityLoader;

/**
 * <h1>Informaci�n del gestor</h1>
 */
public class ManagerInfo {

	/** Archivo XML con la descripci�n de la entidad vinculada al gestor. */
	private String entityXML;

	/** Nombre. */
	private String name;

	/** Permite operaciones de eliminaci�n. */
	private boolean showDeleteOption;

	/** Permite operaciones de edici�n. */
	private boolean showEditOption;

	/** Permite operaciones de inserci�n. */
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
	 * @param entityXML Archivo XML con la descripci�n de la entidad vinculada al
	 *             gestor.
	 * @param showInsertOption Permite operaciones de inserci�n.
	 * @param showEditOption Permite operaciones de edici�n.
	 * @param showDeleteOption Permite operaciones de eliminaci�n.
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
	 * Define si permitir� operaciones de eliminaci�n.
	 * 
	 * @param showDeleteOption Permite operaciones de 
	 * 							eliminaci�n.
	 */
	public void setShowDeleteOption(boolean showDeleteOption) {
		this.showDeleteOption = showDeleteOption;
	}

	/**
	 * Define si permitir� operaciones de edici�n.
	 * 
	 * @param showEditOption Permite operaciones de 
	 * 							edici�n.
	 */
	public void setShowEditOption(boolean showEditOption) {
		this.showEditOption = showEditOption;
	}

	/**
	 * Define si permitir� operaciones de inserci�n.
	 * 
	 * @param showInsertOption Permite operaciones de 
	 * 							inserci�n.
	 */
	public void setShowInsertOption(boolean showInsertOption) {
		this.showInsertOption = showInsertOption;
	}

	/**
	 * Indica si permite operaciones de eliminaci�n.
	 * @return Permite operaciones de eliminaci�n.
	 */
	public boolean showDeleteOption() {
		return showDeleteOption;
	}

	/**
	 * Indica si permite operaciones de edici�n.
	 * @return Permite operaciones de edici�n.
	 */
	public boolean showEditOption() {
		return showEditOption;
	}

	/**
	 * Indica si permite operaciones de inserci�n.
	 * @return Permite operaciones de inserci�n.
	 */
	public boolean showInsertOption() {
		return showInsertOption;
	}
}
