package view.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import model.db.DBLink;
import model.db.JasperManager;
import model.views.ViewInfo;
import model.views.ViewParameter;

import view.AbstractListFrame;
import view.I18N;
import view.ThemeManager;

/**
 * <h1>Ventana de listado</h1>
 * 
 * Hereda todas las características de GenericListFrame. Introduce
 * la posibilidad de mostrar una vista de la base de datos y vincularse
 * con una ventana de parametrización.
 */
public class ViewFrame extends AbstractListFrame {
		
	/** Botón reporte. */
	private JButton btnShowReport;
	
	/** Información de la vista */
	private ViewInfo viewInfo;
	
	/** Botón parametrizador. */
	protected JButton btnParameterizer;
	
	/** Menú parametrizador. */
	protected JMenuItem mntmParameterizer;
	
	/** Lista de parámetros que permiten establecer un filtrado a priori del contenido de la ventana. */
	protected ArrayList<ViewParameter> params;
	
	/** Ventana padre. */
	protected JFrame parent;
	
	/**
	 * ResultSet obtenido al ejecutar la vista.
	 * A partir de sus metadatos se genera la estructura de
	 * la tabla para esta ventana y de sus datos el contenido.
	 */
	protected ResultSet rs;		

	/**
	 * Constructor.
	 * 
	 * @param parent Ventana padre.
	 * @param viewInfo Información de la vista.
	 */
	public ViewFrame(JFrame parent, ViewInfo viewInfo) {
		super();
		filteredIndexes = new ArrayList<Integer>();	
		frmManager.setFrameIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.VIEW))));
		this.parent = parent;
		this.viewInfo = viewInfo;
		frmManager.setTitle(I18N.getInstance().getString(viewInfo.getName()));
		
		if(viewInfo.getParameterizer() == null || viewInfo.getParameterizer().length() == 0)
			btnParameterizer.setVisible(false);
		
		File report = new File("reports/" + viewInfo.getViewName() + ".jasper");		
		
		if(!report.exists()) {
			btnShowReport.setVisible(false);
		}
	}
	
	/**
	 * Convierte los parámetros iniciales de filtrado
	 * en parámetros entendibles por la API de iReport.
	 * 
	 * @return Mapa de parámetros para iReport.
	 */
	private HashMap<String, Object> getParamsForReport() {
		HashMap<String, Object> repParams = new HashMap<String, Object>();
		
		if(params != null) {			
			Iterator<ViewParameter> iter = params.iterator();
			ViewParameter current;			
			
			while(iter.hasNext()) {
				current = iter.next();
				repParams.put(current.getName(), current.getValue());
			}
		}
		
		return repParams;
	}
	
	/**
	 * Convierte los parámetros iniciales de filtrado
	 * en condiciones SQL.
	 * 
	 * @return Cláusula de condiciones SQL.
	 */
	private String getWhereFromParams() {
		String where = "";
		
		if(params != null) {			
			Iterator<ViewParameter> iter = params.iterator();
			ViewParameter current;			
			
			while(iter.hasNext()) {
				current = iter.next();
				where += "\"" + current.getName() + "\"=";
				
				switch (current.getType()) {
				case FT_BOOLEAN:
					if(current.getValueAsBoolean())
						where += "true";
					else
						where += "false";
					break;
				case FT_INT:
					where += String.valueOf(current.getValueAsInteger());
					break;
				case FT_DOUBLE:
					where += String.valueOf(current.getValueAsInteger());
					break;					
				case FT_STRING:
					where += "\'" + current.getValueAsString() + "\'";
					break;
				}
				
				if(iter.hasNext())
					where += " AND ";
			}
		}
		
		if(where.length() > 0) 
			where = " WHERE " + where;
		return where;
	}
	
	@Override
	protected void createGUI() {		
		ArrayList<String> fieldsNames = new ArrayList<String>();
		String name;
		
		if(rs != null) {
			try {
				for(int i=0; i<rs.getMetaData().getColumnCount(); i++) {
					name = rs.getMetaData().getColumnName(i+1);					
					fieldsNames.add(I18N.getInstance().getString(name));
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
				
		createTable(fieldsNames.toArray(new String[0]));
	}			

	@Override
	protected void createMainMenu() {
		super.createMainMenu();
		mntmParameterizer = new JMenuItem(I18N.getInstance().getString(I18N.FILTER));
		mntmParameterizer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		mnData.add(mntmParameterizer);	
		
		mntmParameterizer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				new ParameterizeDialog(parent, ViewFrame.this, params);
			}
		});
	}

	@Override
	protected void createToolBarElements() {
		super.createToolBarElements();
		
		btnShowReport = new JButton("");
		btnShowReport.setIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.REPORT))));
		btnShowReport.setFocusable(false);
		btnShowReport.setToolTipText(I18N.getInstance().getString(I18N.REPORT));
		toolBar.add(btnShowReport);
	
		btnShowReport.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JasperManager.showReport(getParamsForReport(), "reports/" + viewInfo.getViewName() + ".jasper", DBLink.getInstance().getConnection());
			}
		});		
		
		btnParameterizer = new JButton("");
		btnParameterizer.setIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.FILTER))));
		btnParameterizer.setFocusable(false);
		btnParameterizer.setToolTipText(I18N.getInstance().getString(I18N.FILTER));
		toolBar.add(btnParameterizer);
	
		btnParameterizer.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ParameterizeDialog(parent, ViewFrame.this, params);
			}
		});						
	}
	
	/**
	 * Devuelve los parámetros iniciales de filtrado del listado.
	 * @return Parámetros iniciales.
	 */
	public ArrayList<ViewParameter> getParams() {
		return params;
	}
	
	@Override
	public String getRequiredAccess() {
		return viewInfo.getRequiredAccess();
	}	
	
	/**
	 * Devuelve la información de la vista.
	 * @return Información de la vista.
	 */
	public ViewInfo getViewInfo() {
		return viewInfo;
	}	
	
	/**
	 * Devuelve el nombre de la vista.
	 * @return Nombre de la vista.
	 */
	public String getViewName() {
		return viewInfo.getViewName();
	}	
	
	@Override
	public void loadPlugins() {
	}	
	
	@Override
	public void refresh() {			
		try {
			Connection con =  DBLink.getInstance().getConnection();
			
			if(null != con) {
				Statement stmt = con.createStatement(
	                    ResultSet.TYPE_SCROLL_INSENSITIVE,
	                    ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery("SELECT * FROM " + viewInfo.getViewName() + getWhereFromParams());
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), I18N.getInstance().getString(I18N.ERROR), JOptionPane.ERROR_MESSAGE);
			printStatus(I18N.getInstance().getString(I18N.ERROR));
		}
	}
	
	/**
	 * Asigna los parámetros iniciales de filtrado del listado.
	 * @param params Parámetros iniciales.
	 */
	public void setParams(ArrayList<ViewParameter> params) {
		this.params = params;		
		btnShowReport.setVisible(true);
	}		
	
	/**
	 * Hace visible la ventana.	
	 */
	public void show() {
		frmManager.setVisible(true);
		load();
	}

	@Override
	public void updateTable() {
		
		while(tableModel.getRowCount() > 0)
			tableModel.removeRow(0);	
		
		if(null != rs) {
			try {
				rs.absolute(0);
				int columns = rs.getMetaData().getColumnCount();
				
				while(rs.next()) {
					Vector<String> entry = new Vector<String>();
					
					for(int i=0; i<columns; i++) {
						entry.add(String.valueOf(rs.getObject(i+1)));
					}
					
					tableModel.addRow(entry);
				}	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		printStatus(String.valueOf(tableModel.getRowCount()) + " " + I18N.getInstance().getString(I18N.RECORDS));
	}
}
