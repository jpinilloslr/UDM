package model.db.auth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import model.db.EntityCreator;
import model.db.EntityManager;
import model.db.StaticStatementsGenerator;
import model.db.entities.Entity;
import model.db.entities.FieldType;

/**
 * <h1>Rol</h1> 
 * 
 * Representa un rol dentro del sistema. Esta es una entidad
 * especial independiente de la lógica de negocio del sistema.
 */
public class Role extends Entity {
	
	/** Columna id. */
	public static final String ID = "id";

	/** Columna nombre. */
	public static final String NAME = "name";

	/** Nombre de la tabla. */
	public static final String TABLE = "udm_role";

	/**
	 * Constructor.
	 */
	public Role() {
		super();
		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param id Id.
	 * @param name Nombre.
	 */
	public Role(int id, String name) {
		super();
		init();
		setId(id);
		setName(name);
	}

	/**
	 * Define los campos de esta entidad.
	 */
	private void init() {
		addField(ID, 0, FieldType.FT_INT, true, true);
		addField(NAME, "", FieldType.FT_STRING, false, false);
		setTableName(TABLE);
	}

	/**
	 * Devuelve la lista de nombres de accesos permitidos para este rol.
	 * @return Lista de accesos.
	 */
	public ArrayList<Access> getAccessList() {
		Permission perm = new Permission();
		perm.setRoleIndex(getId());
		perm.setSearchable(Permission.ROLE__ID);
		EntityManager manager = new EntityManager(
				new StaticStatementsGenerator());

		LinkedList<Entity> ents = manager.search(perm, new EntityCreator() {

			@Override
			public Entity create() {
				return new Permission();
			}
		});

		ArrayList<Access> accessList = new ArrayList<Access>();
		Iterator<Entity> iter = ents.iterator();

		while (iter.hasNext()) {
			Permission current = (Permission) iter.next();
			accessList.add(current.getAccess());
		}

		return accessList;
	}

	/**
	 * Devuelve el id.
	 * @return Id.
	 */
	public int getId() {
		return getAsInteger(ID);
	}

	/**
	 * Devuelve el nombre.
	 * @return Nombre.
	 */
	public String getName() {
		return getAsString(NAME);
	}

	/**
	 * Chequea si el rol tiene acceso a un recurso 
	 * especificado.
	 * 
	 * @param accessName Nombre del acceso que se requiere.
	 * @return true si el rol tiene acceso al elemento, 
	 * 			false si no.
	 */
	public boolean hasAccess(String accessName) {
		boolean acc = false;
		ArrayList<Access> accessList = getAccessList();

		if (!accessName.contains("*")) {
			if (null != accessList) {
				Iterator<Access> iter = accessList.iterator();

				while (!acc && iter.hasNext()) {
					if (iter.next().getName().equals(accessName)) {
						acc = true;
					}
				}
			}
		} else {
			accessName = accessName.substring(0, accessName.indexOf("*"));
			if (null != accessList) {
				Iterator<Access> iter = accessList.iterator();

				while (!acc && iter.hasNext()) {
					if (iter.next().getName().contains(accessName)) {
						acc = true;
					}
				}
			}
		}
		return acc;
	}

	/**
	 * Define el id.
	 * @param id Id.
	 */
	public void setId(int id) {
		setField(ID, id);
	}

	/**
	 * Define el nombre.
	 * @param name Nombre.
	 */
	public void setName(String name) {
		setField(NAME, name);
	}
}
