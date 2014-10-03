package view.builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import view.I18N;
import view.Resources.Style;

/**
 * Diálogo de configuración a partir de 
 * una lista de archivos.
 */
public abstract class AbstractConfigFileListDialog {

	/** Diálogo. */
	protected JDialog dialog;
	
	/** Carpeta contenedora de los archivos que gestiona. */
	protected String folder;
	
	/** Lista de archivos. */
	protected JList<String> list;
	
	/** Modelo de la lista. */
	protected DefaultListModel<String> listModel;
	
	/** Título de la ventana. */
	protected String title;

	/**
	 * Constructor.
	 */
	public AbstractConfigFileListDialog() {
		super();
	}

	/**
	 * Inicia los componentes del frame.
	 * @param parent Ventana padre.
	 */
	protected void initialize(JFrame parent) {
		dialog = new JDialog(parent, true);
		dialog.setResizable(false);
		dialog.setBounds(100, 100, 400, 329);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().setLayout(null);
		dialog.setTitle(title);
		
		list = new JList<String>();
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listModel = new DefaultListModel<String>();
		list.setModel(listModel);
		list.setBounds(10, 11, 251, 278);
		list.setSelectionBackground(Style.SELECTION_BACKGROUND);
		list.setSelectionForeground(Style.SELECTION_FOREGROUND);
		dialog.getContentPane().add(list);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(271, 11, 111, 102);
		dialog.getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnNew = new JButton(I18N.getInstance().getString(I18N.NEW));
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				insert();
			}
		});
		btnNew.setBounds(10, 11, 89, 23);
		panel.add(btnNew);
		
		JButton btnEdit = new JButton(I18N.getInstance().getString(I18N.EDIT));
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				edit();
			}
		});
		btnEdit.setBounds(10, 39, 89, 23);
		panel.add(btnEdit);
		
		JButton btnDelete = new JButton(I18N.getInstance().getString(I18N.DELETE));
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = list.getSelectedIndex();
				
				if(i > -1) {
					if(JOptionPane.showConfirmDialog(dialog, I18N.getInstance().getString(I18N.DELETE_SINGLE_PROMPT), I18N.getInstance().getString(I18N.DELETE_PROMPT_TITLE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						File file = new File(folder + listModel.get(i));
						file.delete();
						refreshList();
					}
				}
			}
		});
		btnDelete.setBounds(10, 67, 89, 23);
		panel.add(btnDelete);
		
		refreshList();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	/**
	 * Acción editar.
	 */
	public abstract void edit();
	
	/**
	 * Acción insertar.
	 */
	public abstract void insert();
	
	/**
	 * Refresca la lista de archivos.
	 */
	public void refreshList() {
		listModel.removeAllElements();
		
		File file = new File(folder);
		File[] files = file.listFiles();
		
		for (File currentFile : files) {
			listModel.addElement(currentFile.getName());
		}
	}

}