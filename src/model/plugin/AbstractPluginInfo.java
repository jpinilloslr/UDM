package model.plugin;

/**
 * <h1>Información del plugin abstracto</h1>
 */
public abstract class AbstractPluginInfo {
	
	/** Nombre. */
	private String name;
	
	/**
	 * Devuelve el nombre. Debe coincidir con el nombre del 
	 * archivo jar en la carpeta de plugins.
	 * 
	 * @return Nombre.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Define el nombre. Debe coincidir con el nombre del 
	 * archivo jar en la carpeta de plugins.
	 * 
	 * @param name Nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
