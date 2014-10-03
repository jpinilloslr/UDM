package model.plugin;

/**
 * <h1>Información de un plugin del menú principal</h1>
 */
public class MenuPluginInfo extends AbstractPluginInfo {
	
	/** Nombre del menú al que se asocia. */
	private String menuName;

	/**
	 * Devuelve el nombre del menú padre al que 
	 * se asocia el plugin.
	 * 
	 * @return Nombre del menú.
	 */
	public String getMenuName() {
		return menuName;
	}

	/**
	 * Define el nombre del menú padre al que 
	 * se asocia el plugin.
	 * 
	 * @param menuName Nombre del menú.
	 */
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
}
