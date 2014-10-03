package view;

import javax.swing.ImageIcon;

import model.PreferencesManager;

/**
 * <h1>Gestor de temas</h1>
 */
public class ThemeManager {
	
	/** Variable singleton. */
	private static ThemeManager singleton = null;
	
	public static final int ABOUT = 27;
	public static final int ADMIN = 9;
	public static final int AUTHENTICATION_BANNER = 20;
	public static final int CASCADE = 14;
	public static final int CASCADE_DISABLED = 15;
	public static final int DATABASE = 13;
	public static final int DATABASE_BANNER = 21;
	public static final int DATABASE_PARAMS = 12;
	public static final int DEBUG = 25;	
	public static final int ENTITY = 6;
	public static final int EXPORT = 2;
	public static final int FILTER = 5;
	public static final int HOR_LAYOUT = 16;
	public static final int HOR_LAYOUT_DISABLED = 17;
	public static final int ICON = 0;
	public static final int INFO = 11;
	public static final int NEW = 1;
	public static final int OPTIONS = 23;
	public static final int OPTIONS_ICON = 24;
	public static final int REFRESH = 3;
	public static final int REPORT = 26;
	public static final int ROLES = 22;
	public static final int SEARCH = 4;
	public static final int TOOLS = 8;
	public static final int USERS = 10;
	public static final int VER_LAYOUT = 18;
	public static final int VER_LAYOUT_DISABLED = 19;
	public static final int VIEW = 7;
	
	/** Nombre el tema por defecto. */
	public static final String DEFAULT_THEME = "system";
	
	/** Nombre del tema actual. */
	private String currentTheme;
	
	/**
	 * Constructor.
	 */
	private ThemeManager() {
		currentTheme = PreferencesManager.getInstance().getThemeName();
	}
	
	/**
	 * Inicia la instancia de la variable singleton.
	 * @return Gestor de temas.
	 */
	public static ThemeManager getInstance() {
		if (singleton == null) {
			synchronized (ThemeManager.class) {
				if (singleton == null) {
					singleton = new ThemeManager();						
				}
			}
		}
		return singleton;
	}
	
	/**
	 * Obtiene la URI de una imagen a partir
	 * de su identificador.
	 * 
	 * @param themeName Nombre del tema.
	 * @param image Identificador de la imagen.
	 * @return URI de la imagen.
	 */
	private String getImage(String themeName, int image) {
		String file = "themes/" + themeName + "/";
		
		switch (image) {
		case ICON:
			file += "app_icon.png";
			break;
		case NEW:
			file += "tbb_new.png";
			break;		
		case EXPORT:
			file += "tbb_export.png";
			break;	
		case REFRESH:
			file += "tbb_refresh.png";
			break;	
		case SEARCH:
			file += "tbb_search.png";
			break;	
		case FILTER:
			file += "tbb_filter.png";
			break;	
		case ENTITY:
			file += "entity.png";
			break;	
		case VIEW:
			file += "view.png";
			break;	
		case TOOLS:
			file += "tools.png";
			break;	
		case ADMIN:
			file += "admin.png";
			break;	
		case USERS:
			file += "users.png";
			break;	
		case INFO:
			file += "info.png";
			break;	
		case DATABASE_PARAMS:
			file += "database_params.png";
			break;	
		case CASCADE:
			file += "cascade.png";
			break;	
		case DATABASE:
			file += "database.png";
			break;
		case CASCADE_DISABLED:
			file += "cascade_disabled.png";
			break;
		case HOR_LAYOUT:
			file += "hor_layout.png";
			break;
		case HOR_LAYOUT_DISABLED:
			file += "hor_layout_disabled.png";
			break;
		case VER_LAYOUT:
			file += "ver_layout.png";
			break;
		case VER_LAYOUT_DISABLED:
			file += "ver_layout_disabled.png";
			break;
		case AUTHENTICATION_BANNER:
			file += "auth_banner.png";
			break;
		case DATABASE_BANNER:
			file += "database_banner.png";
			break;
		case ROLES:
			file += "role.png";
			break;
		case OPTIONS:
			file += "options.png";
			break;
		case OPTIONS_ICON:
			file += "options_icon.png";
			break;			
		case DEBUG:
			file += "debug.png";
			break;
		case REPORT:
			file += "report.png";
			break;
		case ABOUT:
			file += "about.png";
			break;			

		default:
			file += "icon.png";
			break;
		}
		
		return file;
	}
	
	/**
	 * Obtiene la URI de una imagen a partir
	 * de su identificador.
	 * 
	 * @param image Identificador de la imagen.
	 * @return URI de la imagen.
	 */
	public String getImage(int image) {
		String img = getImage(currentTheme, image);
		
		try {
			new ImageIcon(MainWindow.class.getResource(img));
		} catch(Exception e) {
			img = getImage(DEFAULT_THEME, image);
		}
		
		return img;
	}
	
	
}
