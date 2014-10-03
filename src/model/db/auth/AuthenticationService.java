package model.db.auth;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import model.db.DBLink;
import model.db.EntityCreator;
import model.db.EntityManager;
import model.db.StaticStatementsGenerator;
import model.db.entities.Entity;
import model.managers.ManagerLoader;
import model.views.ViewLoader;
import view.Resources.RequiredAccess;

/**
 * <h1>Servicio de autentificación</h1>
 */
public class AuthenticationService {
	
	/** Usuario actual. */
	private static User loggedUser;
	
	/** Manipulador de entidades. */
	private EntityManager manager;

	/**
	 * Constructor.
	 */
	public AuthenticationService() {
		manager = new EntityManager(new StaticStatementsGenerator());
	}

	/**
	 * Devuelve al usuario logueado en el sistema.
	 * @return Usuario logueado.
	 */
	public static User getLoggedUser() {
		return loggedUser;
	}

	/**
	 * Define al usuario actualmente logueado.
	 * @param user Usuario.
	 */
	public static void setLoggedUser(User user) {
		loggedUser = user;
	}

	/**
	 * Verifica la integridad de la lista de nombres de accesos 
	 * del sistema, crea los que no existan.
	 */
	private void createAccessList() {
		createAccessIfNotExist(RequiredAccess.DEVELOPER_DEBUG);
		createAccessIfNotExist(RequiredAccess.DEVELOPER_BUILD);
		createAccessIfNotExist(RequiredAccess.ADMIN_USER);
		createAccessIfNotExist(RequiredAccess.ADMIN_ROLE);

		/* Es necesario cargar las tablas y vistas disponibles para generar los
		 accesos necesarios. */
		ManagerLoader mloader = new ManagerLoader();
		mloader.getAvailableManagers();
		ViewLoader vloader = new ViewLoader();
		vloader.getAvailableViews();
	}

	/**
	 * Verifica que exista el usuario y rol por defecto, de no existir crea un
	 * rol llamado 'superuser' con todos los accesos asignados y un usuario
	 * perteneciente a ese rol con nombre 'su' y contraseña vacía.
	 */
	private void createDefaultUser() {
		EntityManager manager = new EntityManager(
				new StaticStatementsGenerator());
		Role role = new Role(0, "superuser");
		role.setSearchable(Role.NAME);

		if (null == manager.searchLast(role))
			role = (Role) manager.insertAndRetrieve(role);

		User user = new User(0, "su", "", role);
		user.setSearchable(User.USERNAME);

		if (null == manager.searchLast(user))
			user = (User) manager.insertAndRetrieve(user);

		LinkedList<Entity> ents = manager.get(Access.TABLE, "",
				new EntityCreator() {
					@Override
					public Entity create() {
						return new Access();
					}
				});

		Iterator<Entity> iter = ents.iterator();
		Access access;

		while (iter.hasNext()) {
			access = (Access) iter.next();

			if (!access.getName().startsWith("developer.")) {
				Permission perm = new Permission(role, access);
				perm.setSearchable(Permission.ACCESS__ID);
				perm.setSearchable(Permission.ROLE__ID);

				LinkedList<Entity> coinc = manager.search(perm,
						new EntityCreator() {
							@Override
							public Entity create() {
								return new Permission();
							}
						});

				if (coinc.size() == 0) {
					manager.insert(perm, true);
				}
			}
		}
	}

	/**
	 * Crea la tabla udm_access.
	 */
	private void createTableAccess() {
		try {
			DBLink.getInstance().getConnection().createStatement()
					.executeUpdate("DROP TABLE \"udm_access\"");
		} catch (SQLException e) {
		}

		try {
			DBLink.getInstance()
					.getConnection()
					.createStatement()
					.executeUpdate(
							"CREATE TABLE udm_access"
									+ "("
									+ "   id serial NOT NULL,"
									+ "  name character varying NOT NULL,"
									+ "  CONSTRAINT udm_access_pkey PRIMARY KEY (id )"
									+ ")" + "WITH (" + "  OIDS=FALSE" + ");"
									+ "ALTER TABLE udm_access"
									+ "  OWNER TO postgres;");
		} catch (SQLException e) {
		}
	}

	/**
	 * Crea la tabla udm_permission.
	 */
	private void createTablePermission() {
		try {
			DBLink.getInstance().getConnection().createStatement()
					.executeUpdate("DROP TABLE \"udm_permission\"");
		} catch (SQLException e) {
		}

		try {
			DBLink.getInstance()
					.getConnection()
					.createStatement()
					.executeUpdate(
							"CREATE TABLE udm_permission"
									+ "("
									+ "  udm_role__id integer NOT NULL,"
									+ "  udm_access_id integer NOT NULL,"
									+ "  CONSTRAINT udm_permission_pkey PRIMARY KEY (udm_role__id , udm_access_id ),"
									+ "  CONSTRAINT udm_permission_udm_access_id_fkey FOREIGN KEY (udm_access_id)"
									+ "      REFERENCES udm_access (id) MATCH SIMPLE"
									+ "      ON UPDATE NO ACTION ON DELETE NO ACTION,"
									+ "  CONSTRAINT udm_permission_udm_role__id_fkey FOREIGN KEY (udm_role__id)"
									+ "      REFERENCES udm_role (id) MATCH SIMPLE"
									+ "      ON UPDATE NO ACTION ON DELETE NO ACTION"
									+ ")" + "WITH (" + "  OIDS=FALSE" + ");"
									+ "ALTER TABLE udm_permission"
									+ "  OWNER TO postgres;");
		} catch (SQLException e) {
		}
	}

	/**
	 * Crea la tabla udm_role.
	 */
	private void createTableRole() {
		try {
			DBLink.getInstance().getConnection().createStatement()
					.executeUpdate("DROP TABLE \"udm_role\"");
		} catch (SQLException e) {
		}

		try {
			DBLink.getInstance()
					.getConnection()
					.createStatement()
					.executeUpdate(
							"CREATE TABLE udm_role"
									+ "("
									+ "  id serial NOT NULL,"
									+ "  name character varying NOT NULL,"
									+ "  CONSTRAINT udm_role_pkey PRIMARY KEY (id )"
									+ ")" + "WITH (" + "  OIDS=FALSE" + ");"
									+ "ALTER TABLE udm_role"
									+ "  OWNER TO postgres;");
		} catch (SQLException e) {
		}
	}

	/**
	 * Crea la tbla udm_user.
	 */
	private void createTableUser() {
		try {
			DBLink.getInstance().getConnection().createStatement()
					.executeUpdate("DROP TABLE \"udm_user\"");
		} catch (SQLException e) {
		}

		try {
			DBLink.getInstance()
					.getConnection()
					.createStatement()
					.executeUpdate(
							"CREATE TABLE udm_user"
									+ "("
									+ "  id serial NOT NULL,"
									+ "  username character varying NOT NULL,"
									+ "  password character varying NOT NULL,"
									+ "  udm_role__id integer NOT NULL,"
									+ "  CONSTRAINT udm_user_pkey PRIMARY KEY (id ),"
									+ "  CONSTRAINT udm_user_udm_role__id_fkey FOREIGN KEY (udm_role__id)"
									+ "      REFERENCES udm_role (id) MATCH SIMPLE"
									+ "      ON UPDATE NO ACTION ON DELETE NO ACTION"
									+ ")" + "WITH (" + "  OIDS=FALSE" + ");"
									+ "ALTER TABLE udm_user"
									+ "  OWNER TO postgres;");
		} catch (SQLException e) {
		}
	}

	/**
	 * Agrega un nombre de acceso al rol del usuario actual.
	 * @param accessName Nombre de acceso a agregar.
	 */
	public void addAccessToLoggedUser(String accessName) {
		Role role = AuthenticationService.getLoggedUser().getRole();
		createAccessIfNotExist(accessName);
		
		Permission perm = new Permission(role, getAccessByName(accessName));
		perm.setSearchable(Permission.ACCESS__ID);
		perm.setSearchable(Permission.ROLE__ID);

		LinkedList<Entity> coinc = manager.search(perm, new EntityCreator() {
			@Override
			public Entity create() {
				return new Permission();
			}
		});

		if (coinc.size() == 0) {
			manager.insert(perm, true);
		}
	}

	/**
	 * Chequea la integridad de las tablas asociadas con el sistema de
	 * autentificación, de no existir las crea.
	 */
	public void checkAuthTablesIntegrity() {
		try {
			DBLink.getInstance()
					.getConnection()
					.createStatement()
					.executeQuery("SELECT id, name FROM \"udm_access\" LIMIT 1");
		} catch (SQLException e) {
			createTableAccess();
		}

		try {
			DBLink.getInstance().getConnection().createStatement()
					.executeQuery("SELECT id, name FROM \"udm_role\" LIMIT 1");
		} catch (SQLException e) {
			createTableRole();
		}

		try {
			DBLink.getInstance()
					.getConnection()
					.createStatement()
					.executeQuery(
							"SELECT udm_role__id, udm_access_id FROM \"udm_permission\" LIMIT 1");
		} catch (SQLException e) {
			createTablePermission();
		}

		try {
			DBLink.getInstance()
					.getConnection()
					.createStatement()
					.executeQuery(
							"SELECT id, username, password, udm_role__id FROM \"udm_user\" LIMIT 1");
		} catch (SQLException e) {
			createTableUser();
		}
	}

	/**
	 * Verifica la integridad del sistema de autentificación. Chequea las tablas
	 * relacionadas con el servicio, la lista de acceso y la presencia del super
	 * usuario por defecto del sistema. Esta función debe llamarse durante el
	 * arranque, antes de la autentificación.
	 */
	public void checkIntegrity() {
		checkAuthTablesIntegrity();
		createAccessList();
		createDefaultUser();
	}

	/**
	 * Crea un nombre de acceso si no existe.
	 * @param name Nombre del acceso.
	 */
	public void createAccessIfNotExist(String name) {
		Access acc = new Access();
		acc.setName(name);
		acc.setSearchable(Access.NAME);
		
		if (manager.searchLast(acc) == null) {
			manager.insert(acc, true);
		}
	}

	/**
	 * Obtiene un Access a partir de su nombre.
	 * 
	 * @param accessName Nombre del acceso.
	 * @return Access.
	 */
	public Access getAccessByName(String accessName) {
		Access acc = new Access();
		acc.setName(accessName);
		acc.setSearchable(Access.NAME);

		acc = (Access) manager.searchLast(acc);
		return acc;
	}

	/**
	 * Devuelve un usuario a partir de su nombre.
	 * 
	 * @param name Nombre de usuario.
	 * @return El usuario si existe, null si no.
	 */
	public User getUserByName(String name) {
		User user = new User();
		user.setUsername(name);
		user.setSearchable(User.USERNAME);

		user = (User) manager.searchLast(user);
		return user;
	}
}
