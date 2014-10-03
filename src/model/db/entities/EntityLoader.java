package model.db.entities;

import java.util.HashMap;

import model.PreferencesManager;

/**
 * <h1>Cargador de entidades</h1> 
 * 
 * Utiliza el patr�n Singleton para garantizar la
 * presencia de una sola instancia de esta clase. Implementa una cach� que hace
 * la carga de las entidades mucho m�s eficiente. La carga de las entidades debe
 * hacerse mediante esta clase y no directamente.
 */
public class EntityLoader {

	/** Variable singleton. */
	private static EntityLoader singleton = null;

	/** Cach� de entidades. */
	private HashMap<String, Entity> cache;

	/**
	 * Constructor.
	 */
	private EntityLoader() {
		cache = new HashMap<String, Entity>();
	}

	/**
	 * Inicia la instancia de la variable singleton.
	 * @return EntityLoader.
	 */
	public static EntityLoader getInstance() {
		if (singleton == null) {
			synchronized (EntityLoader.class) {
				if (singleton == null) {
					singleton = new EntityLoader();
				}
			}
		}
		return singleton;
	}

	/**
	 * Vac�a la cach�.
	 */
	public void clearCache() {
		cache.clear();
	}

	/**
	 * Carga los metadatos de una entidad. Esta clase utiliza una cach� para
	 * cargar las entidades, deber�a preferirse a cargarlas individualmente.
	 * 
	 * @param xmlFile Archivo XML que describe a la entidad. S�lo el nombre, 
	 * 					el path lo obtiene del gestor de preferencias.
	 * @return Entidad.
	 */
	public Entity load(String xmlFile) {
		Entity ent = null;
		String filename = PreferencesManager.getInstance().getEntitesPath()
				+ xmlFile;
		Entity model = cache.get(filename);

		if (model == null) {
			model = new Entity();
			if (model.loadFromXML(filename)) {
				cache.put(filename, model);
			}
		}

		if (null != model) {
			ent = new Entity();
			ent.clone(model);
		}

		return ent;
	}
}
