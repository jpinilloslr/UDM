package view;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import model.db.auth.AuthenticationService;
import model.db.auth.User;
import view.Resources.Style;

/**
 * Ventana de lista abstracta. Todas las ventanas que hereden de esta 
 * tendrán las siguientes características:
 * 
 * <ul>
 * 		<li>Funcionalidad MDI.</li>
 * 		<li>Una tabla.</li>
 * 		<li>Un menú principal y una barra de herramientas con opciones de edición
 * 			y exportación de los datos de la tabla.</li>
 * 		<li>Un menú contextual con opciones de filtrado y eliminación de los registros 
 * 			de la fuente de datos de la tabla  independientemente de su estructura y 
 * 			contenido.</li>
 * 		<li>Un componente de búsqueda sobre la tabla independientemente de su
 * 			estructura y contenido.</li>
 * </ul>
 */
public abstract class AbstractListFrame extends MDIFrame {

	/** Botón exportar. */
	protected JButton btnExport;

	/** Botón refrescar. */
	protected JButton btnRefresh;

	/** Botón buscar. */
	protected JButton btnSearch;

	/**
	 * Listado de índices. Este listado sirve para obtener el índice real de un
	 * elemento en la fuente de datos que no tiene por qué coincidir con el
	 * índice en la tabla una vez que haya sido filtrada por el componente de
	 * búsqueda.
	 */
	protected ArrayList<Integer> filteredIndexes;

	/** Label de la barra de estado de la ventana. */
	protected JLabel lblStatus;

	/** Menú principal. */
	protected JMenuBar menuBar;

	/** Menú datos. */
	protected JMenu mnData;
	
	/** Menú filtro. */
	protected JMenu mnFilter;
	
	/** Menú exportar. */
	protected JMenuItem mntmExport;
	
	/** Menú refrescar. */
	protected JMenuItem mntmRefresh;
	
	/** Menú buscar */
	protected JMenuItem mntmSearch;
	
	/** Panel de contenido. */
	protected JPanel panelRoot;
	
	/** Panel de búsqueda en el menú principal. */
	protected JPanel panelSearch;
	
	/** Lista de menús a plugins contextuales que permiten selección múltiple. */
	protected ArrayList<JMenuItem> pluginsMultiSelect;

	/** Lista de menús a plugins contextuales que no permiten selección múltiple. */
	protected ArrayList<JMenuItem> pluginsSingleSelect;
	
	/** Menú contextual. */
	protected JPopupMenu popupMenu;
	
	/** Tabla. */
	protected JTable table;
	
	/** Modelo de la tabla. */
	protected DefaultTableModel tableModel;

	/** Componente de búsqueda sobre la tabla. */
	protected JTextField tfSearch;
	
	/** Barra de herramientas. */
	protected JToolBar toolBar;

	/**
	 * Constructor.
	 */
	public AbstractListFrame() {
		initialize();
	}

	/**
	 * Chequea que el usuario actual tenga el permiso apropiado para acceder
	 * este recurso. De no ser así la ventana se cierra.
	 */
	private void checkRequiredAccess() {
		User user = AuthenticationService.getLoggedUser();	
		boolean success = false;
		
		if(user != null) {
			if(user.getRole() != null) {
				success = user.getRole().hasAccess(getRequiredAccess());
			}
		}

		if (!success) {
			frmManager.dispose();
		}
	}

	/**
	 * Crea el menú contextual asociado al filtrado por columna.
	 */
	private void createDynamicFilterByColumnMenu() {
		int count = tableModel.getColumnCount();

		for (int i = 0; i < count; i++) {
			final Integer column = i;
			JMenuItem item = new JMenuItem(tableModel.getColumnName(i));

			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String value = String.valueOf(tableModel.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), column));
					panelSearch.setVisible(true);
					tfSearch.setText("[" + tableModel.getColumnName(column) + "=\"" + value + "\"]");
					AbstractListFrame.this.filter();
				}
			});

			mnFilter.add(item);
		}
	}

	/**
	 * Define la disponibilidad de todos los plugins contextuales asociados a
	 * esta ventana que permitan selección múltiple.
	 * 
	 * @param enable Habilitado.
	 */
	private void enablePluginsMenuMultiSelect(boolean enable) {
		Iterator<JMenuItem> iter = pluginsMultiSelect.iterator();

		while (iter.hasNext()) {
			iter.next().setEnabled(enable);
		}
	}

	/**
	 * Define la disponibilidad de todos los plugins contextuales asociados a
	 * esta ventana que no permitan selección múltiple.
	 * 
	 * @param enable Habilitado.
	 */
	private void enablePluginsMenuSingleSelect(boolean enable) {
		Iterator<JMenuItem> iter = pluginsSingleSelect.iterator();

		while (iter.hasNext()) {
			iter.next().setEnabled(enable);
		}
	}

	/**
	 * Acción de refrescamiento del menú.
	 */
	private void refreshMenuAction() {
		printStatus(I18N.getInstance().getString(I18N.LOADING));
		tfSearch.setEnabled(false);
		mnData.setEnabled(false);

		refresh();
		updateTable();

		tfSearch.setEnabled(true);
		mnData.setEnabled(true);
	}

	/**
	 * Busca coincidencias en la tabla.
	 */
	public void filter() {
		String filter = tfSearch.getText();

		String[] parts = filter.split(",");
		updateTable();

		filteredIndexes.clear();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			filteredIndexes.add(i);
		}

		for (String part : parts) {
			part = part.trim();
			filterTable(part);
		}
	}

	/**
	 * Devuelve el JFrame de la ventana.
	 * @return JFrame
	 */
	@Override
	public JInternalFrame getFrame() {
		return frmManager;
	}

	/**
	 * Devuelve el nombre de acceso requerido para 
	 * esta ventana.
	 * 
	 * @return Nombre de acceso requerido.
	 */
	public abstract String getRequiredAccess();

	/**
	 * Carga los datos, crea la estructura de la tabla 
	 * dinámicamente a partir de los metadatos y muestra la 
	 * información. El trabajo pesado de carga se realiza
	 * en un hilo de ejecución secundario.
	 */
	public void load() {
		printStatus(I18N.getInstance().getString(I18N.LOADING));
		tfSearch.setEnabled(false);
		mnData.setEnabled(false);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				refresh();
				createGUI();
				updateTable();
				createDynamicFilterByColumnMenu();
				checkRequiredAccess();
				loadPlugins();

				tfSearch.setEnabled(true);
				mnData.setEnabled(true);
			}
		});

		thread.start();
	}

	/**
	 * Carga la lista de plugins contextuales 
	 * asociados a esta ventana.
	 */
	public abstract void loadPlugins();

	/**
	 * Refresca la fuente de datos.
	 */
	public abstract void refresh();

	/**
	 * Actualiza la tabla.
	 */
	public abstract void updateTable();

	/**
	 * Chequea la disponibilidad de los elementos del menú 
	 * contextual según la cantidad de elementos seleccionados 
	 * en la tabla.
	 */
	protected void checkMenuItemAccesibility() {
		int selectedElements = table.getSelectedRowCount();
		mnFilter.setEnabled(false);

		enablePluginsMenuMultiSelect(false);
		enablePluginsMenuSingleSelect(false);

		if (selectedElements == 1) {
			mnFilter.setEnabled(true);
			enablePluginsMenuSingleSelect(true);
		}

		if (selectedElements >= 1) {
			enablePluginsMenuMultiSelect(true);
		}
	}

	/**
	 * Limpia el campo de búsqueda.
	 */
	protected void clearSearchBox() {
		tfSearch.setText("");
	}

	/**
	 * Crea la GUI.
	 */
	protected abstract void createGUI();

	/**
	 * Crea el menú principal. 
	 */ 
	protected void createMainMenu() {

		mnData = new JMenu(I18N.getInstance().getString(I18N.DATA));
		mntmExport = new JMenuItem(I18N.getInstance().getString(I18N.EXPORT));
		mntmExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		mnData.add(mntmExport);
		menuBar.add(mnData);

		mntmExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportData();
			}
		});

		mntmRefresh = new JMenuItem(I18N.getInstance().getString(I18N.REFRESH));
		mntmRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_MASK));
		mnData.add(mntmRefresh);

		mntmRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshMenuAction();
			}
		});

		mntmSearch = new JMenuItem(I18N.getInstance().getString(I18N.SEARCH));
		mntmSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnData.add(mntmSearch);

		mntmSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panelSearch.setVisible(true);
				tfSearch.grabFocus();
			}
		});

		panelSearch = new JPanel();
		panelSearch.setOpaque(false);
		panelSearch.setBackground(SystemColor.menu);
		menuBar.add(panelSearch);
		panelSearch.setLayout(new BorderLayout(0, 0));
		panelSearch.setVisible(false);

		tfSearch = new JTextField();
		panelSearch.add(tfSearch, BorderLayout.EAST);
		tfSearch.setToolTipText(I18N.getInstance().getString(I18N.SEARCH));
		tfSearch.setColumns(30);

		tfSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					tfSearch.setText("");
					filter();
					panelSearch.setVisible(false);
				} else if ((e.getKeyCode() == KeyEvent.VK_ENTER)
						|| (tfSearch.getText().length() == 0)) {
					printStatus(I18N.getInstance().getString(I18N.LOADING));
					filter();
				}
			}
		});
	}

	/**
	 * Crea el menú contextual.
	 */
	protected void createPopupMenu() {
		popupMenu = new JPopupMenu();

		mnFilter = new JMenu(I18N.getInstance().getString(I18N.FILTER));
		popupMenu.add(mnFilter);

		popupMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				checkMenuItemAccesibility();
			}
		});
	}

	/**
	 * Crea la tabla de la ventana.
	 * 
	 * @param columns Un arreglo de String con los nombres de 
	 * 					las columnas de la tabla.
	 */
	protected void createTable(String[] columns) {
		JScrollPane scrollPane = new JScrollPane();
		panelRoot.add(scrollPane, BorderLayout.CENTER);

		// reescribimos isCellEditable para que ninguna celda pueda editarse
		table = new JTable() {
			private static final long serialVersionUID = -8826733137099010616L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		tableModel = new DefaultTableModel(new Object[][] {}, columns);

		table.setModel(tableModel);
		scrollPane.setViewportView(table);

		// table.setAutoCreateRowSorter(true);
		table.setGridColor(Style.TABLE_GRID);
		table.setSelectionBackground(Style.SELECTION_BACKGROUND);
		table.setSelectionForeground(Style.SELECTION_FOREGROUND);
		table.setComponentPopupMenu(popupMenu);
	}

	/**
	 * Crea la barra de herramientas.
	 */
	protected void createToolBarElements() {
		ThemeManager theme = ThemeManager.getInstance();
		btnExport = new JButton("");
		btnExport.setIcon(new ImageIcon(AbstractListFrame.class
				.getResource(theme.getImage(ThemeManager.EXPORT))));
		btnExport.setFocusable(false);
		btnExport.setToolTipText(I18N.getInstance().getString(I18N.EXPORT));
		toolBar.add(btnExport);

		btnRefresh = new JButton("");
		btnRefresh.setIcon(new ImageIcon(AbstractListFrame.class
				.getResource(theme.getImage(ThemeManager.REFRESH))));
		btnRefresh.setFocusable(false);
		btnRefresh.setToolTipText(I18N.getInstance().getString(I18N.REFRESH));
		toolBar.add(btnRefresh);

		btnSearch = new JButton("");
		btnSearch.setIcon(new ImageIcon(AbstractListFrame.class
				.getResource(theme.getImage(ThemeManager.SEARCH))));
		btnSearch.setFocusable(false);
		btnSearch.setToolTipText(I18N.getInstance().getString(I18N.SEARCH));
		toolBar.add(btnSearch);

		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportData();
			}
		});

		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshMenuAction();
			}
		});

		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panelSearch.setVisible(true);
				tfSearch.grabFocus();
			}
		});
	}

	/**
	 * Exporta los datos de la tabla.
	 */
	protected void exportData() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Web (htm, html)", "htm", "html");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(getFrame());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String fileName = chooser.getSelectedFile().getAbsolutePath();

			if (!fileName.endsWith(".htm") && !fileName.endsWith(".html")) {
				fileName += ".html";
			}

			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}

			try {
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				String data = generateExportData(tableModel);
				raf.writeBytes(data);
				raf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Filtra el contenido de la tabla a partir de 
	 * un filtro. El filtro puede contener:
	 * 
	 * <ul>
	 * 		<li>Un texto (ej. john): Como resultado se buscarán todas las coincidencias
	 * 						del texto contenido en cualquiera de las celdas 
	 * 						de cada una de las filas de la tabla.</li>
	 * 		<li>Un texto entre comillas (ej. "john"): Como resultado se buscarán todas las coincidencias
	 * 						exactas del texto en cualquiera de las celdas 
	 * 						de cada una de las filas de la tabla.</li>
	 * 		<li>[nombre o número de la columna:texto] (ej. [First Name:john]): Como resultado se buscarán 
	 * 						todas las coincidencias	del texto contenido en la columna especificada.</li>
	 * </ul>
	 * 
	 * @param filter El filtro de búsqueda.
	 */
	protected void filterTable(String filter) {
		int rowCount = tableModel.getRowCount();
		int colCount = tableModel.getColumnCount();
		int row, col;
		String value;
		boolean coinc = false;
		boolean strict = false;
		int referedColumn = -1;

		if (filter.startsWith("[") && filter.endsWith("]")) {
			filter = filter.substring(1);
			filter = filter.substring(0, filter.length() - 1);
			String[] parts = filter.split("=");

			try {
				if (parts.length == 2) {
					referedColumn = tableModel.findColumn(parts[0]);

					if (-1 == referedColumn) {
						referedColumn = Integer.valueOf(parts[0]);
					}

					filter = parts[1];
				}
			} catch (Exception e) {
				referedColumn = -1;
			}
		}

		if (filter.startsWith("\"") && filter.endsWith("\"")) {
			filter = filter.substring(1);
			filter = filter.substring(0, filter.length() - 1);
			strict = true;
		}

		row = rowCount - 1;

		while (row >= 0) {
			col = 0;
			coinc = false;

			while (!coinc && (col < colCount)) {
				value = (String) table.getValueAt(row, col);

				if (!strict) {
					if (value.toLowerCase().contains(filter.toLowerCase())) {
						if (-1 == referedColumn) {
							coinc = true;
						} else if (col == referedColumn) {
							coinc = true;
						}
					}
				} else {
					if (value.toLowerCase().equals(filter.toLowerCase())) {
						if (-1 == referedColumn) {
							coinc = true;
						} else if (col == referedColumn) {
							coinc = true;
						}
					}
				}
				col++;
			}

			if (!coinc) {
				tableModel.removeRow(row);
				filteredIndexes.remove(row);
			}
			row--;
		}
		printStatus(String.valueOf(tableModel.getRowCount()) + " " + I18N.getInstance().getString(I18N.RECORDS));
	}

	/**
	 * Genera los datos a exportar en formato texto. Crea 
	 * una tabla en formato html con la misma configuración 
	 * de la tabla de la ventana.
	 * 
	 * @param tableModel Modelo de la tabla que se va a 
	 * 						copiar dinámicamente.
	 * 
	 * @return Contenido del documento a exportar.
	 */
	protected String generateExportData(DefaultTableModel tableModel) {
		String data = "<html><head><title>"
				+ getFrame().getTitle()
				+ "</title></head><body style={color: #161d1e;}><center><table border=0>";
		int rowCount = tableModel.getRowCount();
		int colCount = tableModel.getColumnCount();
		int row, col;
		String value;
		boolean color = false;

		data += "<tr><h3>" + getFrame().getTitle() + "</h3></tr>";

		data += "<tr bgcolor=\"ffffcc\">";
		for (col = 0; col < colCount; col++) {
			value = tableModel.getColumnName(col);

			data += "<td><b>" + value + "</b></td>";
		}
		data += "</tr>";

		for (row = 0; row < rowCount; row++) {

			if (color) {
				data += "<tr bgcolor=\"#ffffcc\">";
			} else {
				data += "<tr>";
			}
			color = !color;

			for (col = 0; col < colCount; col++) {
				value = (String) table.getValueAt(row, col);

				data += "<td>" + value + "</td>";
			}

			data += "</tr>";
		}
		data += "</table></center></body></html>";
		return data;
	}

	/**
	 * Obtiene el índice referente a la fuente de datos del elemento con índice
	 * tableIndex en la tabla. Debido al filtro el índice de un elemento en la
	 * tabla no tiene por qué corresponder con el índice del elemento en la
	 * tabla.
	 * 
	 * @param tableIndex Índice del elemento en la tabla.
	 * 
	 * @return índice referente a la fuente de datos del elemento 
	 * 					seleccionado en la tabla.
	 */
	protected int getListIndexFromTableIndex(int tableIndex) {
		int index = tableIndex;
		if (tableIndex < filteredIndexes.size()) {
			index = filteredIndexes.get(tableIndex);
		}
		return index;
	}

	/**
	 * Obtiene el índice referente a la fuente de datos del elemento
	 * seleccionado en la tabla. Debido al filtro el índice de un elemento en la
	 * tabla no tiene por qué corresponder con el índice del elemento en la
	 * tabla.
	 * 
	 * @return Índice referente a la fuente de datos del elemento seleccionado
	 *         en la tabla.
	 */
	protected int getRealSelectedItemIndex() {
		int index = table.convertRowIndexToModel(table.getSelectedRow());
		return getListIndexFromTableIndex(index);
	}

	@Override
	protected void initialize() {
		super.initialize();
		pluginsMultiSelect = new ArrayList<JMenuItem>();
		pluginsSingleSelect = new ArrayList<JMenuItem>();
		createPopupMenu();

		menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		frmManager.getContentPane().add(menuBar, BorderLayout.NORTH);
		createMainMenu();

		JMenuBar statusBar = new JMenuBar();
		statusBar.setEnabled(false);
		statusBar.setBorderPainted(false);
		frmManager.getContentPane().add(statusBar, BorderLayout.SOUTH);

		lblStatus = new JLabel(I18N.getInstance().getString(I18N.READY));
		statusBar.add(lblStatus);

		panelRoot = new JPanel();
		frmManager.getContentPane().add(panelRoot, BorderLayout.CENTER);
		panelRoot.setLayout(new BorderLayout(0, 0));

		toolBar = new JToolBar();
		panelRoot.add(toolBar, BorderLayout.NORTH);

		createToolBarElements();
	}

	/**
	 * Imprime un texto en el estado de la ventana.
	 * @param text Texto.
	 */
	protected void printStatus(String text) {
		lblStatus.setText(text);
		lblStatus.getParent().repaint();
		lblStatus.repaint();
		frmManager.repaint();
	}

}
