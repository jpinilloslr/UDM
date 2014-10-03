package model.plugin;

/**
 * <h1>Informaci�n de un plugin del men� principal</h1>
 */
public class MenuPluginInfo extends AbstractPluginInfo {
	
	/** Nombre del men� al que se asocia. */
	private String menuName;

	/**
	 * Devuelve el nombre del men� padre al que 
	 * se asocia el plugin.
	 * 
	 * @return Nombre del men�.
	 */
	public String getMenuName() {
		return menuName;
	}

	/**
	 * Define el nombre del men� padre al que 
	 * se asocia el plugin.
	 * 
	 * @param menuName Nombre del men�.
	 */
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
}
