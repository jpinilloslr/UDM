package model.db.auth;

import model.db.EntityManager;
import model.db.StaticStatementsGenerator;
import model.db.entities.Entity;
import model.db.entities.FieldType;

/**
 * <h1>Usuario</h1> 
 * 
 * Representa un usuario del sistema. Esta es una entidad
 * especial independiente de la lógica de negocio del sistema.
 */
public class User extends Entity {

	/** Columna id. */
	public static final String ID = "id";

	/** Columna contraseña. */
	public static final String PASSWORD = "password";

	/** Columna rol. */
	public static final String ROLE = "udm_role__id";

	/** Nombre de la tabla. */
	public static final String TABLE = "udm_user";

	/** Columna nombre de usuario. */
	public static final String USERNAME = "username";

	/**
	 * Constructor.
	 */
	public User() {
		super();
		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param id Id.
	 * @param username Nombre de usuario.
	 * @param password Contraseña.
	 * @param role Id del rol.
	 */
	public User(int id, String username, String password, int role) {
		super();
		init();
		setId(id);
		setUsername(username);
		setPassword(password);
		setRoleIndex(role);
	}

	/**
	 * Constructor.
	 * 
	 * @param id Id.
	 * @param username Nombre de usuario.
	 * @param password Contraseña.
	 * @param role Rol.
	 */
	public User(int id, String username, String password, Role role) {
		super();
		init();
		setId(id);
		setUsername(username);
		setPassword(password);
		setRole(role);
	}

	/**
	 * Define los campos de esta entidad.
	 */
	private void init() {
		addField(ID, 0, FieldType.FT_INT, true, true);
		addField(USERNAME, "", FieldType.FT_STRING, false, false);
		addField(PASSWORD, "", FieldType.FT_STRING, false, false);
		addField(ROLE, "", FieldType.FT_INT, false, false);
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
	 * Devuelve la contraseña.
	 * @return Contraseña.
	 */
	public String getPassword() {
		return getAsString(PASSWORD);
	}

	/**
	 * Devuelve el rol.
	 * @return Rol
	 */
	public Role getRole() {
		Role role = new Role();
		role.setId(getAsInteger(ROLE));

		EntityManager em = new EntityManager(new StaticStatementsGenerator());
		role = (Role) em.get(role);
		return role;
	}

	/**
	 * Devuelve el id del rol.
	 * @return Id del rol.
	 */
	public int getRoleIndex() {
		return getAsInteger(ROLE);
	}

	/**
	 * Devuelve el nombre de usuario.
	 * @return Nombre de usuario.
	 */
	public String getUsername() {
		return getAsString(USERNAME);
	}

	/**
	 * Define el id.
	 * @param id Id.
	 */
	public void setId(int id) {
		setField(ID, id);
	}

	/**
	 * Define la contraseña.
	 * @param password Contraseña.
	 */
	public void setPassword(String password) {
		setField(PASSWORD, Encription.getSHA512(password));
	}

	/**
	 * Define el rol.
	 * @param role Rol.
	 */
	public void setRole(Role role) {
		setField(ROLE, role.getId());
	}

	/**
	 * Define el id del rol.
	 * @param role Id del rol.
	 */
	public void setRoleIndex(int role) {
		setField(ROLE, role);
	}

	/**
	 * Define el nombre de usuario.
	 * @param username Nombre de usuario.
	 */
	public void setUsername(String username) {
		setField(USERNAME, username);
	}

	/**
	 * Verifica si la contraseña dada coincide con la 
	 * almacenada para este usuario.
	 * 
	 * @param password Contraseña.
	 * @return true si la contraseña es válida, false si no.
	 */
	public boolean validatePassword(String password) {
		String test = Encription.getSHA512(password);
		String pass = getPassword();
		return pass.equals(test);
	}
}
