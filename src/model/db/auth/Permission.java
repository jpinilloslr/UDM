package model.db.auth;

import model.db.EntityManager;
import model.db.StaticStatementsGenerator;
import model.db.entities.Entity;
import model.db.entities.FieldType;

/**
 * <h1>Permiso</h1> 
 * 
 * Representa la asignación de un acceso a un rol. Esta es una
 * entidad especial independiente de la lógica de negocio del sistema.
 */
public class Permission extends Entity {
	
	/** Columna acceso. */
	public static final String ACCESS__ID = "udm_access_id";

	/** Columna rol. */
	public static final String ROLE__ID = "udm_role__id";

	/** Nombre de la tabla. */
	public static final String TABLE = "udm_permission";

	/**
	 * Constructor.
	 */
	public Permission() {
		super();
		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param roleId Id del rol.
	 * @param accessId Id del acceso.
	 */
	public Permission(int roleId, int accessId) {
		super();
		init();
		setRoleIndex(roleId);
		setAccessIndex(accessId);
	}

	/**
	 * Constructor.
	 * 
	 * @param role Rol.
	 * @param access Acceso.
	 */
	public Permission(Role role, Access access) {
		super();
		init();
		setRole(role);
		setAccess(access);
	}

	/**
	 * Define los campos de esta entidad.
	 */
	private void init() {
		addField(ROLE__ID, 0, FieldType.FT_INT, true, false);
		addField(ACCESS__ID, 0, FieldType.FT_INT, true, false);
		setTableName(TABLE);
	}

	/**
	 * Devuelve el acceso.
	 * @return Acceso.
	 */
	public Access getAccess() {
		Access ent = new Access();
		ent.setId(getAsInteger(ACCESS__ID));
		EntityManager em = new EntityManager(new StaticStatementsGenerator());
		ent = (Access) em.get(ent);
		return ent;
	}

	/**
	 * Devuelve el id del acceso.
	 * @return Id del acceso.
	 */
	public int getAccessIndex() {
		return getAsInteger(ACCESS__ID);
	}

	/**
	 * Devuelve el rol.
	 * @return Rol.
	 */
	public Role getRole() {
		Role ent = new Role();
		ent.setId(getAsInteger(ROLE__ID));
		EntityManager em = new EntityManager(new StaticStatementsGenerator());
		ent = (Role) em.get(ent);
		return ent;
	}

	/**
	 * Devuelve el id del rol.
	 * @return Id del rol.
	 */
	public int getRoleIndex() {
		return getAsInteger(ROLE__ID);
	}

	/**
	 * Define el acceso.
	 * @param access Acceso.
	 */
	public void setAccess(Access access) {
		setField(ACCESS__ID, access.getId());
	}

	/**
	 * Define el acceso a partir de su id.
	 * @param access Id del acceso.
	 */
	public void setAccessIndex(int access) {
		setField(ACCESS__ID, access);
	}

	/**
	 * Define el rol.
	 * @param role Rol.
	 */
	public void setRole(Role role) {
		setField(ROLE__ID, role.getId());
	}

	/**
	 * Define el rol a partir de su id.
	 * @param role Id del rol.
	 */
	public void setRoleIndex(int role) {
		setField(ROLE__ID, role);
	}
}
