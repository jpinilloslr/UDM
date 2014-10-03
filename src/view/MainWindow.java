package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import model.db.auth.AuthenticationService;
import model.db.auth.User;
import model.managers.ManagerInfo;
import model.managers.ManagerLoader;
import model.plugin.Plugin;
import model.plugin.PluginManager;
import model.views.ViewInfo;
import model.views.ViewLoader;
import view.Resources.RequiredAccess;
import view.Resources.Style;
import view.builder.EntityFileListDialog;
import view.builder.ManagersListBuilderDialog;
import view.builder.PDCFileListDialog;
import view.builder.ViewsListBuilderDialog;
import view.managers.RoleManagerFrame;
import view.managers.TableManagerFrame;
import view.managers.UserManagerFrame;
import view.tools.SQLQueryDialog;
import view.views.ParameterizeDialog;
import view.views.ViewFrame;

/**
 * <h1>Ventana principal</h1>
 */
public class MainWindow {

	/** Botón acerca de. */
	private JButton btnAbout;
	
	/** Botón despliegue en cascada de ventanas internas. */
	private JButton btnCascade;
	
	/** Botón de configuración de conexión a la base de datos */
	private JButton btnDatabaseConnection;
	
	/** Botón de despliegue horizontal de ventanas internas. */
	private JButton btnHLayout;
	
	/** Botón de opciones. */
	private JButton btnOptions;
	
	/** Botón de despliegue vertical de ventanas internas. */
	private JButton btnVLayout;
	
	/** Contenedor de ventanas internas. */
	private JDesktopPane desktopPane;
	
	/** Frame. */
	private JFrame frmMain;
	
	/** Label de la barra de estado. */
	private JLabel lblStatus;
	
	/** Menú debug. */
	private JMenu mnDebug;
	
	/** Menú ventana. */
	private JMenu mnWindow;
	
	/** Árbol de navegación. */
	private JTree navTree;
	
	/** Panel doble. */
	private JSplitPane splitPane;

	/**
	 * Constructor.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Reposiciona todas las ventanas internas
	 * en cascada.
	 */
	private void cascade() {
		Component[] childs = desktopPane.getComponents();
		int i = childs.length - 1;
		int x = 20;
		int y = 20;
		int step = 20;

		while (i >= 0) {
			final JInternalFrame frame = (JInternalFrame) childs[i];
			try {
				frame.setMaximum(false);
			} catch (PropertyVetoException e) {
			}

			frame.setLocation(x, y);
			frame.setSize(756, 550);
			x += step;
			y += step;
			i--;
		}
	}

	/**
	 * Chequea la disponibilidad de los botones de la barra de
	 * herramientas.
	 */
	private void checkToolBarAccesibility() {
		int count = getInternalFramesCount();

		btnCascade.setEnabled(false);
		btnHLayout.setEnabled(false);
		btnVLayout.setEnabled(false);

		if (count > 1) {
			btnCascade.setEnabled(true);
			btnHLayout.setEnabled(true);
			btnVLayout.setEnabled(true);
		}
	}

	/**
	 * Crea el menú principal.
	 */
	private void createMainMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(new LineBorder(SystemColor.controlHighlight));
		frmMain.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu(I18N.getInstance().getString(I18N.FILE));
		menuBar.add(mnFile);

		JMenuItem mntmConnectionSettings = new JMenuItem(I18N.getInstance()
				.getString(I18N.CONNECTION_SETTINGS));
		mntmConnectionSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ConnectionSettingsDialog(frmMain);
			}
		});

		JMenuItem mntmOptions = new JMenuItem(I18N.getInstance().getString(
				I18N.OPTIONS));
		mntmOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new OptionsDialog(frmMain);
			}
		});
		mnFile.add(mntmOptions);
		mnFile.add(mntmConnectionSettings);

		JSeparator sep = new JSeparator();
		sep.setForeground(SystemColor.controlHighlight);
		sep.setBackground(SystemColor.menu);
		mnFile.add(sep);

		JMenuItem mntmClose = new JMenuItem(I18N.getInstance().getString(
				I18N.CLOSE));
		mntmClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				InputEvent.CTRL_MASK));
		mntmClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Component[] childs = desktopPane.getComponents();
				if (childs.length > 0) {
					JInternalFrame frame = (JInternalFrame) childs[0];
					desktopPane.getDesktopManager().closeFrame(frame);
				}
			}
		});

		JMenuItem mntmDisconnect = new JMenuItem(I18N.getInstance().getString(
				I18N.DISCONNECT));
		mntmDisconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_MASK));
		mntmDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AuthenticationFrame();
				frmMain.dispose();
			}
		});

		mnFile.add(mntmDisconnect);

		JSeparator sep2 = new JSeparator();
		sep2.setForeground(SystemColor.controlHighlight);
		sep2.setBackground(SystemColor.menu);
		mnFile.add(sep2);
		mnFile.add(mntmClose);

		JMenuItem mntmExit = new JMenuItem(I18N.getInstance().getString(
				I18N.EXIT));
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frmMain.dispose();
			}
		});
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				InputEvent.ALT_MASK));
		mnFile.add(mntmExit);

		mnDebug = new JMenu(I18N.getInstance().getString(I18N.DEEBUG));
		if (AuthenticationService.getLoggedUser().getRole()
				.hasAccess(RequiredAccess.DEVELOPER_DEBUG)) {
			menuBar.add(mnDebug);
			mnDebug.setIcon(new ImageIcon(MainWindow.class
					.getResource(ThemeManager.getInstance().getImage(
							ThemeManager.DEBUG))));
		}

		JMenuItem mntmSQLQuery = new JMenuItem(I18N.getInstance().getString(
				I18N.SQL_QUERY));
		mntmSQLQuery.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SQLQueryDialog();
			}
		});
		mnDebug.add(mntmSQLQuery);

		mnWindow = new JMenu(I18N.getInstance().getString(I18N.WINDOW));
		mnWindow.addMenuListener(new MenuListener() {
			@Override
			public void menuCanceled(MenuEvent arg0) {
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				generateWindowMenu();
			}

		});

		JMenu mnBuild = new JMenu(I18N.getInstance().getString(I18N.DEVELOP));

		if (AuthenticationService.getLoggedUser().getRole()
				.hasAccess(RequiredAccess.ANY_DEVELOPER)) {
			menuBar.add(mnBuild);
		}

		JMenuItem mntmEntitiesList = new JMenuItem(I18N.getInstance()
				.getString(I18N.ENTITIES));
		mntmEntitiesList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EntityFileListDialog(frmMain);
			}
		});
		mnBuild.add(mntmEntitiesList);

		JMenuItem mntmManagersList = new JMenuItem(I18N.getInstance()
				.getString(I18N.MANAGERS));
		mntmManagersList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ManagersListBuilderDialog(MainWindow.this);
			}
		});
		mnBuild.add(mntmManagersList);

		JSeparator sep3 = new JSeparator();
		sep3.setForeground(SystemColor.controlHighlight);
		sep3.setBackground(SystemColor.menu);
		mnBuild.add(sep3);

		JMenuItem mntmParamsSelectorList = new JMenuItem(I18N.getInstance()
				.getString(I18N.PARAMETERIZERS));
		mntmParamsSelectorList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PDCFileListDialog(frmMain);
			}
		});
		mnBuild.add(mntmParamsSelectorList);

		JMenuItem mntmViewsList = new JMenuItem(I18N.getInstance().getString(
				I18N.LISTS));
		mntmViewsList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ViewsListBuilderDialog(MainWindow.this);
			}
		});
		mnBuild.add(mntmViewsList);

		menuBar.add(mnWindow);

		JMenu mnHelp = new JMenu(I18N.getInstance().getString(I18N.HELP));
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem(I18N.getInstance().getString(
				I18N.ABOUT));
		mntmAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutDialog(frmMain);
			}
		});
		mnHelp.add(mntmAbout);

		JPanel panelRoot = new JPanel(new BorderLayout());
		frmMain.getContentPane().add(panelRoot, BorderLayout.CENTER);

		JMenuBar statusBar = new JMenuBar();
		statusBar.setBackground(SystemColor.menu);
		statusBar.setEnabled(false);
		statusBar.setBorderPainted(false);
		panelRoot.add(statusBar, BorderLayout.SOUTH);

		lblStatus = new JLabel(I18N.getInstance().getString(I18N.READY));
		statusBar.add(lblStatus);

		splitPane = new JSplitPane();
		splitPane.setBorder(new LineBorder(SystemColor.controlHighlight));
		splitPane.setDividerSize(2);
		panelRoot.add(splitPane, BorderLayout.CENTER);

		desktopPane = new JDesktopPane();
		desktopPane.addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent arg0) {
				checkToolBarAccesibility();
			}

			@Override
			public void componentRemoved(ContainerEvent arg0) {
				checkToolBarAccesibility();
			}
		});
		desktopPane.setBackground(Color.WHITE);
		splitPane.setRightComponent(desktopPane);
		splitPane.setDividerLocation(200);

		JToolBar toolBar = new JToolBar();
		frmMain.getContentPane().add(toolBar, BorderLayout.NORTH);

		btnDatabaseConnection = new JButton("");
		btnDatabaseConnection.setToolTipText(I18N.getInstance().getString(
				I18N.CONNECTION_SETTINGS));
		btnDatabaseConnection.setIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.DATABASE))));
		toolBar.add(btnDatabaseConnection);
		btnDatabaseConnection.setFocusable(false);

		btnOptions = new JButton("");
		btnOptions.setToolTipText(I18N.getInstance().getString(I18N.OPTIONS));
		btnOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new OptionsDialog(frmMain);
			}
		});
		btnOptions.setIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.OPTIONS))));
		toolBar.add(btnOptions);
		btnOptions.setFocusable(false);

		btnDatabaseConnection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ConnectionSettingsDialog(frmMain);
			}
		});

		JToolBar toolBarWindow = new JToolBar();
		toolBar.add(toolBarWindow);

		btnCascade = new JButton("");
		btnCascade.setToolTipText(I18N.getInstance().getString(I18N.CASCADE));
		toolBarWindow.add(btnCascade);
		btnCascade.setEnabled(false);
		btnCascade.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cascade();
			}
		});
		btnCascade.setIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.CASCADE))));
		btnCascade.setDisabledIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.CASCADE_DISABLED))));
		btnCascade.setFocusable(false);

		btnHLayout = new JButton("");
		btnHLayout.setToolTipText(I18N.getInstance().getString(
				I18N.HORIZONTAL_LAYOUT));
		toolBarWindow.add(btnHLayout);
		btnHLayout.setEnabled(false);
		btnHLayout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				horizontalLayout();
			}
		});
		btnHLayout.setIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.HOR_LAYOUT))));
		btnHLayout.setDisabledIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.HOR_LAYOUT_DISABLED))));
		btnHLayout.setFocusable(false);

		btnVLayout = new JButton("");
		btnVLayout.setToolTipText(I18N.getInstance().getString(
				I18N.VERTICAL_LAYOUT));
		toolBarWindow.add(btnVLayout);
		btnVLayout.setEnabled(false);
		btnVLayout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				verticalLayout();
			}
		});
		btnVLayout.setIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.VER_LAYOUT))));
		btnVLayout.setDisabledIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.VER_LAYOUT_DISABLED))));
		btnVLayout.setFocusable(false);

		JToolBar toolBarAbout = new JToolBar();
		toolBar.add(toolBarAbout);

		btnAbout = new JButton("");
		btnAbout.setToolTipText(I18N.getInstance().getString(I18N.ABOUT));
		toolBarAbout.add(btnAbout);
		btnAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutDialog(frmMain);
			}
		});
		btnAbout.setIcon(new ImageIcon(MainWindow.class
				.getResource(ThemeManager.getInstance().getImage(
						ThemeManager.ABOUT))));
		btnAbout.setFocusable(false);
		toolBar.setVisible(true);

		loadPluginsForMenu(mnFile);
		loadPluginsForMenu(mnBuild);
		loadPluginsForMenu(mnDebug);
	}

	/**
	 * Crea el árbol de navegación.
	 */
	private void createNavigationTree() {
		navTree = new JTree();
		JScrollPane leftPanel = new JScrollPane();
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
		leftPanel.setViewportView(navTree);
		navTree.setShowsRootHandles(true);
		navTree.setRootVisible(false);
		splitPane.setLeftComponent(leftPanel);

		DefaultMutableTreeNode root 	= new DefaultMutableTreeNode("");
		DefaultMutableTreeNode admin 	= new DefaultMutableTreeNode(I18N.getInstance().getString(I18N.ADMINISTRATION));
		DefaultMutableTreeNode users 	= new DefaultMutableTreeNode(I18N.getInstance().getString(I18N.USERS));
		DefaultMutableTreeNode roles 	= new DefaultMutableTreeNode(I18N.getInstance().getString(I18N.ROLES));
		DefaultMutableTreeNode manager 	= new DefaultMutableTreeNode(I18N.getInstance().getString(I18N.MANAGERS));
		DefaultMutableTreeNode view 	= new DefaultMutableTreeNode(I18N.getInstance().getString(I18N.LISTS));

		if (AuthenticationService.getLoggedUser().getRole().hasAccess(RequiredAccess.ANY_ADMIN)) {
			root.add(admin);
		}

		if (AuthenticationService.getLoggedUser().getRole().hasAccess(RequiredAccess.ADMIN_USER)) {
			admin.add(users);
		}

		if (AuthenticationService.getLoggedUser().getRole().hasAccess(RequiredAccess.ADMIN_ROLE)) {
			admin.add(roles);
		}

		if (AuthenticationService.getLoggedUser().getRole().hasAccess(RequiredAccess.ANY_TABLE)) {
			root.add(manager);

			ManagerLoader loader = new ManagerLoader();
			ArrayList<ManagerInfo> list = loader.getAvailableManagers();
			Iterator<ManagerInfo> iter = list.iterator();
			ManagerInfo current;

			while (iter.hasNext()) {
				current = iter.next();
				if (AuthenticationService.getLoggedUser().getRole().hasAccess(current.getRequiredAccess())) {
					manager.add(new DefaultMutableTreeNode(I18N.getInstance().getString(current.getName())));
				}
			}
		}

		if (AuthenticationService.getLoggedUser().getRole().hasAccess(RequiredAccess.ANY_VIEW)) {
			root.add(view);

			ViewLoader loader = new ViewLoader();
			ArrayList<ViewInfo> list = loader.getAvailableViews();
			Iterator<ViewInfo> iter = list.iterator();
			ViewInfo current;

			while (iter.hasNext()) {
				current = iter.next();
				if (AuthenticationService.getLoggedUser().getRole().hasAccess(current.getRequiredAccess())) {
					view.add(new DefaultMutableTreeNode(I18N.getInstance().getString(current.getName())));
				}
			}
		}

		DefaultTreeModel model = new DefaultTreeModel(root);
		navTree.setModel(model);

		navTree.setCellRenderer(new TreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {

				JPanel panel = new JPanel();
				panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

				JLabel icon = new JLabel();
				JLabel text = new JLabel();
				text.setText(value.toString());
				text.setBorder(new EmptyBorder(2, 2, 2, 2));
				String img = "";
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

				if (node.toString().equals(I18N.getInstance().getString(I18N.MANAGERS)) || 
						((node.getParent() != null) && node.getParent().toString().equals(I18N.getInstance().getString(I18N.MANAGERS)))) {
					img = ThemeManager.getInstance().getImage(ThemeManager.ENTITY);
				} else 
				if (node.toString().equals(I18N.getInstance().getString(I18N.LISTS)) || 
						((node.getParent() != null) && node.getParent().toString().equals(I18N.getInstance().getString(I18N.LISTS)))) {
					img = ThemeManager.getInstance().getImage(ThemeManager.VIEW);
				} else 
				if (node.toString().equals(I18N.getInstance().getString(I18N.ADMINISTRATION))) {
					img = ThemeManager.getInstance().getImage(ThemeManager.ADMIN);
				} else 
				if (node.toString().equals(I18N.getInstance().getString(I18N.USERS))) {
					img = ThemeManager.getInstance().getImage(ThemeManager.USERS);
				} else 
				if (node.toString().equals(I18N.getInstance().getString(I18N.ROLES))) {
					img = ThemeManager.getInstance().getImage(ThemeManager.ROLES);
				}

				Icon image = new ImageIcon(MainWindow.class.getResource(img));
				icon.setIcon(image);

				if (selected) {
					text.setBackground(Style.SELECTION_BACKGROUND);
					text.setForeground(Style.SELECTION_FOREGROUND);
					text.setOpaque(true);
				}
				icon.setSize(5, 5);
				panel.add(icon);
				panel.add(text);
				panel.setOpaque(false);
				return panel;
			}
		});

		navTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) navTree
							.getLastSelectedPathComponent();

					if (node != null) {
						if (((node.getParent() != null) && node
								.getParent()
								.toString()
								.equals(I18N.getInstance().getString(
										I18N.MANAGERS)))) {
							showTable(node.toString());
						}
						if (((node.getParent() != null) && node
								.getParent()
								.toString()
								.equals(I18N.getInstance()
										.getString(I18N.LISTS)))) {
							showView(node.toString());
						}
						if (node.toString().equals(
								I18N.getInstance().getString(I18N.USERS))) {
							showTable(node.toString());
						}
						if (node.toString().equals(
								I18N.getInstance().getString(I18N.ROLES))) {
							showTable(node.toString());
						}
					}
				}
			}
		});

		navTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) navTree
						.getLastSelectedPathComponent();

				if (node != null) {
					if (((node.getParent() != null) && node
							.getParent()
							.toString()
							.equals(I18N.getInstance().getString(I18N.MANAGERS)))) {
						showTable(node.toString());
					}
					if (((node.getParent() != null) && node.getParent()
							.toString()
							.equals(I18N.getInstance().getString(I18N.LISTS)))) {
						showView(node.toString());
					}
					if (node.toString().equals(
							I18N.getInstance().getString(I18N.USERS))) {
						showTable(node.toString());
					}
					if (node.toString().equals(
							I18N.getInstance().getString(I18N.ROLES))) {
						showTable(node.toString());
					}
				}
			}
		});
		navTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		navTree.expandPath(new TreePath(new DefaultMutableTreeNode[] { root,
				admin }));
		navTree.expandPath(new TreePath(new DefaultMutableTreeNode[] { root,
				manager }));
		navTree.expandPath(new TreePath(new DefaultMutableTreeNode[] { root,
				view }));

		if (root.getChildCount() == 0) {
			splitPane.getLeftComponent().setVisible(false);
			mnWindow.setVisible(false);
			btnCascade.setVisible(false);
			btnHLayout.setVisible(false);
			btnVLayout.setVisible(false);
		}
	}

	/**
	 * Genera dinámicamente el menú Ventana en dependencia de las 
	 * ventanas internas que se encuentren abiertas.
	 */
	private void generateWindowMenu() {
		mnWindow.removeAll();
		Component[] childs = desktopPane.getComponents();
		int i = 0;

		while (i < childs.length) {
			final JInternalFrame frame = (JInternalFrame) childs[i];
			JMenuItem item = new JMenuItem();
			item.setText(frame.getTitle());
			item.setIcon(frame.getFrameIcon());
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					desktopPane.getDesktopManager().activateFrame(frame);
				}
			});
			mnWindow.add(item);
			i++;
		}

		if (childs.length > 0) {
			JSeparator sep = new JSeparator();
			sep.setForeground(SystemColor.controlHighlight);
			sep.setBackground(SystemColor.menu);
			mnWindow.add(sep);
		}

		JMenuItem cascade = new JMenuItem();
		cascade.setText(I18N.getInstance().getString(I18N.CASCADE));
		cascade.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
		cascade.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cascade();
			}
		});
		mnWindow.add(cascade);

		JMenuItem horizontal = new JMenuItem();
		horizontal.setText(I18N.getInstance().getString(I18N.HORIZONTAL_LAYOUT));
		horizontal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK));
		horizontal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				horizontalLayout();
			}
		});
		mnWindow.add(horizontal);

		JMenuItem vertical = new JMenuItem();
		vertical.setText(I18N.getInstance().getString(I18N.VERTICAL_LAYOUT));
		vertical.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_MASK));
		vertical.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				verticalLayout();
			}
		});
		mnWindow.add(vertical);

		JMenuItem closeAll = new JMenuItem();
		closeAll.setText(I18N.getInstance().getString(I18N.CLOSE_ALL));
		closeAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_MASK));
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Component[] childs = desktopPane.getComponents();
				int i = 0;

				while (i < childs.length) {
					final JInternalFrame frame = (JInternalFrame) childs[i];
					frame.dispose();
					i++;
				}
			}
		});
		mnWindow.add(closeAll);

		if (childs.length == 0) {
			closeAll.setEnabled(false);
			cascade.setEnabled(false);
			vertical.setEnabled(false);
			horizontal.setEnabled(false);
		}
	}

	/**
	 * Devuelve la cantidad de ventanas internas abiertas.
	 * @return Cantidad de ventanas internas abiertas.
	 */
	private int getInternalFramesCount() {
		return desktopPane.getComponents().length;
	}

	/**
	 * Reposiciona todas las ventanas internas horizontalmente.
	 */
	private void horizontalLayout() {
		Component[] childs = desktopPane.getComponents();
		int height = desktopPane.getHeight() / childs.length;
		int i = 0;

		for (Component component : childs) {
			JInternalFrame frame = (JInternalFrame) component;
			
			try {
				frame.setMaximum(false);
			} catch (PropertyVetoException e) {
			}
			
			frame.setBounds(0, i, desktopPane.getWidth(), height);
			i += height;
		}
	}

	/**
	 * Inicia el contenido del frame.
	 */
	private void initialize() {
		frmMain = new JFrame();
		
		frmMain.setIconImage(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource(ThemeManager.getInstance().getImage(ThemeManager.ICON))));
		
		User loggedUser = AuthenticationService.getLoggedUser();
		frmMain.setTitle(I18N.UDM + " [" + loggedUser.getUsername() + ":" + loggedUser.getRole().getName() + "]");
		
		frmMain.setBounds(100, 100, 1024, 768);
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMain.setLocationRelativeTo(null);
		frmMain.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		createMainMenu();
		createNavigationTree();
		frmMain.setVisible(true);
		
		printStatus(I18N.getInstance().getString(I18N.READY));
	}

	/**
	 * Permite saber si un MDIFrame se encuentra presente, en tal caso le
	 * pasa el foco.
	 * 
	 * @param internal MDIFrame.
	 * @return Devuelve true si el MDIFrame se encuentra presente, false si no.
	 */
	private boolean isMDIFramePresent(MDIFrame internal) {
		Component[] childs = desktopPane.getComponents();
		boolean found = false;
		int i = 0;

		while (!found && (i < childs.length)) {
			JInternalFrame frame = (JInternalFrame) childs[i];
			String t1 = frame.getTitle();
			String t2 = internal.getFrame().getTitle();

			if (t1.equalsIgnoreCase(t2)) {
				desktopPane.getDesktopManager().activateFrame(frame);
				found = true;
			}
			i++;
		}

		return found;
	}

	/**
	 * Carga la lsta de plugins asociados a un menú.
	 * @param menu Menú.
	 */
	private void loadPluginsForMenu(JMenu menu) {
		ArrayList<Plugin> plugins = PluginManager.getInstance().getPluginsForMenu(menu.getText());
		Iterator<Plugin> iter = plugins.iterator();

		while (iter.hasNext()) {
			final Plugin current = iter.next();

			JMenuItem mntmPlugin = new JMenuItem(current.getText());
			menu.add(mntmPlugin);

			mntmPlugin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					current.load();
				}
			});
		}
	}

	/**
	 * Imprime un mensaje de estatus en esta ventana.
	 * @param text Texto
	 */
	private void printStatus(String text) {
		lblStatus.setText(text);
		lblStatus.getParent().repaint();
		lblStatus.repaint();
		frmMain.repaint();
	}

	/**
	 * Crea una tabla a partir de su nombre.
	 * @param name Nombre
	 */
	private void showTable(String name) {
		ManagerLoader managerLoader = new ManagerLoader();
		ManagerInfo managerInfo = managerLoader.getManagerInfoByName(name);

		if (null != managerInfo) {
			showTableFrame(new TableManagerFrame(name, managerInfo));
		}

		if (name.equals(I18N.getInstance().getString(I18N.USERS))) {
			showTableFrame(new UserManagerFrame());
		}
		if (name.equals(I18N.getInstance().getString(I18N.ROLES))) {
			showTableFrame(new RoleManagerFrame());
		}
	}

	/**
	 * Muestra un TableManagerFrame.
	 * @param tableFrame TableManagerFrame
	 */
	private void showTableFrame(TableManagerFrame tableFrame) {

		if (!isMDIFramePresent(tableFrame)) {
			JInternalFrame frame = tableFrame.getFrame();
			desktopPane.add(frame);
			desktopPane.getDesktopManager().activateFrame(frame);
			try {
				frame.setMaximum(true);
			} catch (PropertyVetoException e) {
			}
		}
	}

	/**
	 * Crea una vista a partir de su nombre.
	 * @param viewName Nombre
	 */
	private void showView(String viewName) {

		ViewLoader viewLoader = new ViewLoader();
		ViewInfo viewInfo = viewLoader.getViewInfoByText(viewName);

		if (null != viewInfo) {
			showViewFrame(new ViewFrame(frmMain, viewInfo),
					viewInfo.useParameterizer());
		}
	}

	/**
	 * Muestra un ViewFrame.
	 * 
	 * @param viewFrame ViewFrame.
	 * @param useParameterizer Utiliza el selector de parámetros para 
	 * 								definir los parámetros iniciales de la vista.
	 */
	private void showViewFrame(ViewFrame viewFrame, boolean useParameterizer) {
		if (!isMDIFramePresent(viewFrame)) {
			JInternalFrame frame = viewFrame.getFrame();
			desktopPane.add(frame);

			if (useParameterizer) {
				new ParameterizeDialog(frmMain, viewFrame);
			} else {
				desktopPane.getDesktopManager().activateFrame(frame);
				viewFrame.show();
				try {
					viewFrame.getFrame().setMaximum(true);
				} catch (PropertyVetoException e) {
				}
			}
		}
	}

	/**
	 * Reposiciona todas las ventanas internas verticalmente.
	 */
	private void verticalLayout() {
		Component[] childs = desktopPane.getComponents();
		int width = desktopPane.getWidth() / childs.length;
		int i = 0;

		for (Component component : childs) {
			JInternalFrame frame = (JInternalFrame) component;
			
			try {
				frame.setMaximum(false);
			} catch (PropertyVetoException e) {
			}
			
			frame.setBounds(i, 0, width, desktopPane.getHeight());
			i += width;
		}
	}

	/**
	 * Refresca el árbol de navegación.
	 */
	public void refreshNavigationTree() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) navTree.getModel().getRoot();
		DefaultMutableTreeNode manager = null;
		DefaultMutableTreeNode view = null;
		int childsCount = root.getChildCount();

		for (int i = 0; i < childsCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);

			if (child.toString().equals(I18N.getInstance().getString(I18N.MANAGERS))) {
				manager = child;
				child.removeAllChildren();
			}

			if (child.toString().equals(I18N.getInstance().getString(I18N.LISTS))) {
				view = child;
				child.removeAllChildren();
			}
		}

		if (null == manager) {
			manager = new DefaultMutableTreeNode(I18N.getInstance().getString(I18N.MANAGERS));
		} else {
			root.remove(manager);
		}

		if (null == view) {
			view = new DefaultMutableTreeNode(I18N.getInstance().getString(I18N.LISTS));
		} else {
			root.remove(view);
		}

		if (AuthenticationService.getLoggedUser().getRole().hasAccess(RequiredAccess.ANY_TABLE)) {
			root.add(manager);

			ManagerLoader loader = new ManagerLoader();
			ArrayList<ManagerInfo> list = loader.getAvailableManagers();
			Iterator<ManagerInfo> iter = list.iterator();
			ManagerInfo current;

			while (iter.hasNext()) {
				current = iter.next();
				if (AuthenticationService.getLoggedUser().getRole().hasAccess(current.getRequiredAccess())) {
					manager.add(new DefaultMutableTreeNode(I18N.getInstance().getString(current.getName())));
				}
			}
		}

		if (AuthenticationService.getLoggedUser().getRole().hasAccess(RequiredAccess.ANY_VIEW)) {
			root.add(view);

			ViewLoader loader = new ViewLoader();
			ArrayList<ViewInfo> list = loader.getAvailableViews();
			Iterator<ViewInfo> iter = list.iterator();
			ViewInfo current;

			while (iter.hasNext()) {
				current = iter.next();
				if (AuthenticationService.getLoggedUser().getRole().hasAccess(current.getRequiredAccess())) {
					view.add(new DefaultMutableTreeNode(I18N.getInstance().getString(current.getName())));
				}
			}
		}
		DefaultTreeModel model = (DefaultTreeModel) navTree.getModel();
		model.reload(root);
		navTree.expandPath(new TreePath(new DefaultMutableTreeNode[] { root, manager }));
		navTree.expandPath(new TreePath(new DefaultMutableTreeNode[] { root, view }));
	}
}
