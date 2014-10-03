package view.builder;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import model.ExportableConfig;
import view.AbstractListFrame;
import view.I18N;
import view.Resources.Style;
import view.utils.GUIUtils;
import view.ThemeManager;

/**
 * Diálogo de construcción.
 *
 * @param <E> Tipo de dato.
 */
public abstract class AbstractBuilderDialog<E> {
	
	/** Botón guardar */
	protected JButton btnSave;
	
	/** Columnas de la tabla. */
	protected String[] columns;
	
	/** Diálogo. */
	protected JDialog dialog;
	
	/** Modo edición. */
	protected boolean editMode;
	
	/** Lista exportable. */
	protected ExportableConfig<E> exportable;
	
	/** Menú eliminar. */
	protected JMenuItem mntmDelete;
	
	/** Menú editar. */
	protected JMenuItem mntmEdit;
	
	/** Modificado. */
	protected boolean modified;
	
	/** Panel doble. */
	protected JSplitPane splitPane;
	
	/** Tabla. */
	protected JTable table;
	
	/** Modelo de la tabla. */
	protected DefaultTableModel tableModel;
	
	/** Título de la ventana. */
	protected String title;
	
	/**
	 * Constructor.
	 * @param exportable Lista de elementos exportables.
	 */
	public AbstractBuilderDialog(ExportableConfig<E> exportable) {
			this.exportable = exportable;
	}
	
	/**
	 * Elimina los campos seleccionados.
	 */
	private void deleteSelecteds() {
		int[] selected = table.getSelectedRows();
		LinkedList<E> deleteBatch = new LinkedList<E>();


		String title = I18N.getInstance().getString(I18N.DELETE_PROMPT_TITLE);
		String msg = (selected.length > 1)? I18N.getInstance().getString(I18N.DELETE_MULTIPLE_PROMPT) : I18N.getInstance().getString(I18N.DELETE_SINGLE_PROMPT);


		if(JOptionPane.showConfirmDialog(dialog, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			for (int i : selected) {
				deleteBatch.add(exportable.getExportableElements().get(table.convertRowIndexToModel(i)));
			}
			Iterator<E> iter = deleteBatch.iterator();
			E current;
			while(iter.hasNext()) {
				current = iter.next();
				exportable.getExportableElements().remove(current);
			}			
			load();
			setModified(true);
		}
	}
	
	/**
	 * Esta función se ejecuta justo antes de cerrar la ventana.
	 * @return true si la ventana puede cerrarse, false si no.
	 */
	private boolean saveChanges() {
		boolean success = true;
		
		if(isModified()) {
			int result = JOptionPane.showConfirmDialog(dialog, I18N.getInstance().getString(I18N.SAVE_CHANGES_PROMPT), 
					I18N.getInstance().getString(I18N.SAVE), JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(result == JOptionPane.YES_OPTION) {
				success = save();
			} else if(result == JOptionPane.CANCEL_OPTION)
				success = false;
		}
		return success;
	}
	
	/**
	 * Chequea la disponibilidad de los elementos del menú contextual.
	 */
	protected void checkMenuItemAccesibility() {
		int selectedElements = table.getSelectedRowCount();
		
		mntmEdit.setEnabled(false);
		mntmDelete.setEnabled(false);
		
		if(selectedElements == 1)
			mntmEdit.setEnabled(true);
		
		if(selectedElements > 0)
			mntmDelete.setEnabled(true);
	}
	
	/**
	 * Crea el menú contextual.
	 */
	protected void createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		GUIUtils.addPopup(table, popupMenu);		
		
		mntmEdit = new JMenuItem(I18N.getInstance().getString(I18N.EDIT));
		mntmDelete = new JMenuItem(I18N.getInstance().getString(I18N.DELETE));				
		
		mntmEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				edit();
			}
		});
		
		mntmDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteSelecteds();
			}
		});

		popupMenu.add(mntmEdit);
		popupMenu.add(mntmDelete);
		
		popupMenu.addPopupMenuListener(new PopupMenuListener() {			
			@Override public void popupMenuCanceled(PopupMenuEvent arg0) {}

			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {}
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				checkMenuItemAccesibility();
			}			
		});
	}
	
	/**
	 * Crea la tabla.
	 */
	protected void createTable() {
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		table = new JTable() {
			private static final long serialVersionUID = -8826733137099010616L;
			@Override
			public boolean isCellEditable(int row, int column) {                
				return false;               
			};
		};
				
		tableModel = new DefaultTableModel(new Object[][] {}, columns);
		table.setModel(tableModel);
		table.setGridColor(Style.TABLE_GRID);
		table.setSelectionBackground(Style.SELECTION_BACKGROUND);
		table.setSelectionForeground(Style.SELECTION_FOREGROUND);
		scrollPane.setViewportView(table);
	}
	
	/**
	 * Crea la barra de herramientas.
	 */
	protected void createToolBar() {
		JToolBar toolBar = new JToolBar();
		dialog.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnNew = new JButton("");
		btnNew.setToolTipText(I18N.getInstance().getString(I18N.NEW_FIELD));
		btnNew.setMnemonic('N');
		btnNew.setFocusable(false);
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				insert();
			}
		});
		toolBar.add(btnNew);
		btnNew.setIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.NEW))));
		
		btnSave = new JButton("");
		btnSave.setEnabled(false);
		btnSave.setToolTipText(I18N.getInstance().getString(I18N.SAVE));
		btnSave.setMnemonic('S');
		btnSave.setFocusable(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		toolBar.add(btnSave);
		btnSave.setIcon(new ImageIcon(AbstractListFrame.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.EXPORT))));
	}
	
	/**
	 * Inicia el contenido del panel superior.
	 * @param topPanel Panel superior.
	 */
	protected abstract void createTopPanel(JPanel topPanel);
	
	/**
	 * Acción editar.
	 */
	protected abstract void edit();

	/**
	 * Inicia.
	 */
	protected void init() {
		initialize(null);
		setModified(false);
	}	
	
	/**
	 * Inicia el contenido del frame.
	 * @param parent Ventana padre.
	 */
	protected void initialize(JFrame parent) {
		dialog = new JDialog(parent, true);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				if(!saveChanges()) {
					dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				} else {
					dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
			}
		});
		
		dialog.setResizable(false);
		dialog.setBounds(50, 50, 693, 469);		
		dialog.setTitle(title);
		
		dialog.getContentPane().setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setBorder(new LineBorder(SystemColor.scrollbar));
		splitPane.setDividerSize(0);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		dialog.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(60);
		
		JPanel topPanel = new JPanel();
		topPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		splitPane.setLeftComponent(topPanel);
		topPanel.setLayout(null);
		
		createTopPanel(topPanel);
								
		createToolBar();
		createTable();
		createPopupMenu();								
		load();
		
		dialog.setLocationRelativeTo(null);								
		dialog.setVisible(true);
	}
	
	/**
	 * Acción insertar.
	 */
	protected abstract void insert();	
	
	/**
	 * Carga la lista de elementos.
	 */
	protected abstract void load();
	
	/**
	 * Guarda la lista de elementos.
	 * @return true si el proceso es satisfactorio,
	 * 			false si no. 
	 */
	protected abstract boolean save();
	
	/**
	 * Indica si se encuentra en modo edición.
	 * @return true si se encuentra en modo edición,
	 * 			false si no.
	 */
	public boolean isEditMode() {
		return editMode;
	}
	
	/**
	 * Indica si ha ocurrido alguna modificación.
	 * @return true si ha ocurrido alguna modificación,
	 * 			false si no.
	 */
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * Define si ha ocurrido alguna modificación.
	 * @param modified Modificado.
	 */
	public void setModified(boolean modified) {
		btnSave.setEnabled(modified);
		this.modified = modified;
	}

	
}
