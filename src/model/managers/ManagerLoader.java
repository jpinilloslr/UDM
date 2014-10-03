package model.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.ExportableConfig;
import model.PreferencesManager;
import model.db.auth.AuthenticationService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import view.I18N;

/**
 * <h1>Cargador de gestores</h1>
 */
public class ManagerLoader implements ExportableConfig<ManagerInfo> {
	
	/** Archivo que almacena la lista de gestores. */
	public static final String FILE_NAME = "managers.xml";

	/** Lista de gestores. */
	private ArrayList<ManagerInfo> managers;

	/**
	 * Constructor.
	 */
	public ManagerLoader() {
	}

	/**
	 * Exporta la información de un gestor.
	 * 
	 * @param data Contenido XML.
	 * @param managerInfo Información del gestor.
	 * @return Contenido XML con el nuevo nodo agregado.
	 */
	private String exportManagerInfo(String data, ManagerInfo managerInfo) {
		data += "\t<manager name=\"" + managerInfo.getName() + "\" "
				+ "entity=\"" + managerInfo.getEntityXML() + "\" "
				+ "showInsertOption=\""
				+ String.valueOf(managerInfo.showInsertOption()) + "\" "
				+ "showEditOption=\""
				+ String.valueOf(managerInfo.showEditOption()) + "\" "
				+ "showDeleteOption=\""
				+ String.valueOf(managerInfo.showDeleteOption())
				+ "\" ></manager>\r\n";
		return data;
	}

	@Override
	public void export() {
		exportToXml();
	}

	/**
	 * Exporta la lista de gestores a un archivo en formato XML.
	 */
	public void exportToXml() {
		String data = " <managersList>\r\n";

		Iterator<ManagerInfo> iter = getAvailableManagers().iterator();
		ManagerInfo current;

		while (iter.hasNext()) {
			current = iter.next();
			data = exportManagerInfo(data, current);
		}

		data += " </managersList>";

		try {
			File file = new File(PreferencesManager.getInstance().getDataPath()
					+ FILE_NAME);

			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter pw = new PrintWriter(file, "UTF-8");
			pw.print(data);
			pw.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Devuelve una lista de gestores disponibles.
	 * @return Gestores disponibles.
	 */
	public ArrayList<ManagerInfo> getAvailableManagers() {
		ArrayList<ManagerInfo> list = new ArrayList<ManagerInfo>();

		if (null != managers) {
			list = managers;
		} else {
			AuthenticationService authServ = new AuthenticationService();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document dom = null;
			String attrib;

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				dom = db.parse(new FileInputStream(PreferencesManager
						.getInstance().getDataPath() + FILE_NAME));

				if (null != dom) {
					Element root = dom.getDocumentElement();

					NodeList nl = root.getElementsByTagName("manager");

					if ((nl != null) && (nl.getLength() > 0)) {
						for (int i = 0; i < nl.getLength(); i++) {
							Element el = (org.w3c.dom.Element) nl.item(i);

							ManagerInfo mi = new ManagerInfo();

							attrib = el.getAttribute("name");
							mi.setName(attrib);

							attrib = el.getAttribute("entity");
							mi.setEntityXML(attrib);

							attrib = el.getAttribute("showInsertOption");
							if ((attrib != null)
									&& attrib.equalsIgnoreCase("false")) {
								mi.setShowInsertOption(false);
							}

							attrib = el.getAttribute("showEditOption");
							if ((attrib != null)
									&& attrib.equalsIgnoreCase("false")) {
								mi.setShowEditOption(false);
							}

							attrib = el.getAttribute("showDeleteOption");
							if ((attrib != null)
									&& attrib.equalsIgnoreCase("false")) {
								mi.setShowDeleteOption(false);
							}

							if (null != mi.getRequiredAccess()) {
								authServ.createAccessIfNotExist(mi
										.getRequiredAccess());
							}

							list.add(mi);
						}
					}
					managers = list;
				}
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(null, "ManagerLoader Error: "
						+ exc.getMessage(),
						I18N.getInstance().getString(I18N.ERROR),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return list;
	}

	@Override
	public List<ManagerInfo> getExportableElements() {
		return getAvailableManagers();
	}

	/**
	 * Obtiene la información del gestor por el texto que 
	 * lo identifica.
	 * 
	 * @param name Texto del gestor que se muestra al usuario.
	 * @return Información del gestor o null si no se encuentra.
	 */
	public ManagerInfo getManagerInfoByName(String name) {
		ManagerInfo mi = null;
		ArrayList<ManagerInfo> list = getAvailableManagers();
		Iterator<ManagerInfo> iter = list.iterator();
		ManagerInfo current;

		while ((null == mi) && iter.hasNext()) {
			current = iter.next();

			if (I18N.getInstance().getString(current.getName())
					.equalsIgnoreCase(name)) {
				mi = current;
			}
		}

		return mi;
	}
}
