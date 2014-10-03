package model.views;

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
 * <h1>Cargador de vistas</h1>
 */
public class ViewLoader implements ExportableConfig<ViewInfo> {
	
	/** Archivo contenedor de las vistas. */
	public static final String FILENAME = "views.xml";
	
	/** Lista de vistas. */
	private ArrayList<ViewInfo> views;

	/**
	 * Constrctor.
	 */
	public ViewLoader() {
	}

	/**
	 * Exporta la información de una vista.
	 * 
	 * @param data Contenido XML.
	 * @param viewInfo Información de vista.
	 * @return Contenido XML con el nuevo nodo agregado.
	 */
	private String exportViewInfo(String data, ViewInfo viewInfo) {
		data += "\t<view viewName=\"" + viewInfo.getViewName() + "\" "
				+ "name=\"" + viewInfo.getName() + "\" "
				+ "parameterSelector=\"" + viewInfo.getParameterizer()
				+ "\"></view>\r\n";
		return data;
	}

	@Override
	public void export() {
		exportToXml();
	}

	/**
	 * Exporta la lista de vistas a un archivo en formato XML.
	 */
	public void exportToXml() {
		String data = " <viewsList>\r\n";

		Iterator<ViewInfo> iter = getAvailableViews().iterator();
		ViewInfo current;

		while (iter.hasNext()) {
			current = iter.next();
			data = exportViewInfo(data, current);
		}

		data += " </viewsList>";

		try {
			File file = new File(PreferencesManager.getInstance().getDataPath()
					+ FILENAME);

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
	 * Devuelve una lista de vistas disponibles.
	 * @return Vistas disponibles.
	 */
	public ArrayList<ViewInfo> getAvailableViews() {
		ArrayList<ViewInfo> list = new ArrayList<ViewInfo>();

		if (null != views) {
			list = views;
		} else {
			AuthenticationService authServ = new AuthenticationService();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document dom = null;

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				dom = db.parse(new FileInputStream(PreferencesManager
						.getInstance().getDataPath() + FILENAME));

				if (null != dom) {
					Element root = dom.getDocumentElement();

					NodeList nl = root.getElementsByTagName("view");

					if ((nl != null) && (nl.getLength() > 0)) {
						for (int i = 0; i < nl.getLength(); i++) {
							Element el = (org.w3c.dom.Element) nl.item(i);

							ViewInfo mi = new ViewInfo(
									el.getAttribute("viewName"),
									el.getAttribute("name"),
									el.getAttribute("parameterSelector"));
							authServ.createAccessIfNotExist(mi
									.getRequiredAccess());
							list.add(mi);
						}
					}
					views = list;
				}
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(null,
						"ViewLoader Error: " + exc.getMessage(), I18N
								.getInstance().getString(I18N.ERROR),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return list;
	}

	@Override
	public List<ViewInfo> getExportableElements() {
		return getAvailableViews();
	}

	/**
	 * Obtiene la información de la vista por el texto que 
	 * la identifica.
	 * 
	 * @param text Texto de la vista que se muestra al usuario.
	 * @return Información de la vista o null si no se encuentra.
	 */
	public ViewInfo getViewInfoByText(String text) {
		ViewInfo mi = null;
		ArrayList<ViewInfo> list = getAvailableViews();
		Iterator<ViewInfo> iter = list.iterator();
		ViewInfo current;

		while ((null == mi) && iter.hasNext()) {
			current = iter.next();

			if (I18N.getInstance().getString(current.getName())
					.equalsIgnoreCase(text)) {
				mi = current;
			}
		}

		return mi;
	}
}
