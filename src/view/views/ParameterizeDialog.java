package view.views;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

import view.I18N;
import model.PreferencesManager;
import model.views.Parameter;
import model.views.ViewParameter;

import java.awt.SystemColor;
import java.awt.Window.Type;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <h1>Parametrizador de vistas</h1>
 * 
 * Se asocia a un viewFrame para definir sus parámetros 
 * iniciales de filtrado. Carga la configuración de sus
 * parámetros de un archivo XML.
 */
public class ParameterizeDialog {

	/** Espaciado entre columnas para generación dinámica de la interface. */
	private static final int X_SPACE = 200;
	
	/** Botón aceptar. */
	private JButton btnAccept;
	
	/** Botón cancelar. */
	private JButton btnCancel;
		
	/** Lista de ComboBox asociados a parámetros. */
	private ArrayList<ParameterCombo> combos;
	
	/** Diálogo. */
	private JDialog dialog;
	
	/** Ventana padre. */
	private JFrame parent;
	
	/** Define si es llamado desde la propia ventana de vista para redefinir sus parámetros de filtrado. */
	private boolean refreshMode;
	
	/** Ventana de vista asociada al parametrizador. */
	private ViewFrame viewFrame;
	
	/** Lista de parámetros de vistas. */
	protected ArrayList<ViewParameter> params;	

	/**
	 * Costructor.
	 * @param parent Ventana padre.
	 */
	public ParameterizeDialog(JFrame parent) {
		combos = new ArrayList<ParameterCombo>();
		this.parent = parent;
		initialize();
		dialog.setVisible(true);
	}	
	
	/**
	 * Constructor.
	 * 
	 * @param parent Ventana padre.
	 * @param viewFrame Ventana de vista.
	 */
	public ParameterizeDialog(JFrame parent, ViewFrame viewFrame) {
		combos = new ArrayList<ParameterCombo>();
		this.viewFrame = viewFrame;
		this.parent = parent;
		initialize();
		this.refreshMode = false;
		dialog.setVisible(true);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param parent Ventana padre.
	 * @param viewFrame Ventana de vista.
	 * @param params Parámetros de vista.
	 */
	public ParameterizeDialog(JFrame parent, ViewFrame viewFrame, ArrayList<ViewParameter> params) {
		combos = new ArrayList<ParameterCombo>();
		this.viewFrame = viewFrame;
		this.parent = parent;
		initialize();
		this.refreshMode = true;	
		this.params = params;
		reloadParams();
		dialog.setVisible(true);
	}

	/**
	 * Acción de proceder en el diálogo.
	 */
	private void accept() {
		params = new ArrayList<ViewParameter>();
		Iterator<ParameterCombo> iter = combos.iterator();
		ParameterCombo current;
		
		while(iter.hasNext()) {
			current = iter.next();
			
			if(!I18N.getInstance().getString(I18N.ALL).equals(current.getSelectedValue())) {
				params.add(new ViewParameter(current.getName(), current.getSelectedValue(), current.getType()));
			}
		}
		
		viewFrame.setParams(params);
		
		if(!refreshMode) {
			viewFrame.show();
			try {
				viewFrame.getFrame().setMaximum(true);
			} catch (PropertyVetoException e) {	}
		}
		else {
			viewFrame.refresh();
			viewFrame.filter();
		}
		
		dialog.dispose();
	}

	/**
	 * Acción de cancelar el diálogo.
	 */
	private void cancel() {
		if(!refreshMode)
			viewFrame.getFrame().dispose();
		dialog.dispose();
	}

	/**
	 * Crea la interface dinámicamente a 
	 * partir de la configuración de los
	 * parámetros.
	 */
	private void createGUI() {

		Iterator<ParameterCombo> iter = combos.iterator();
		ParameterCombo current;

		int x = 10;
		int y = 10;
		int maxY = 10;
		int count = 0;

		while(iter.hasNext()) {
			current = iter.next();

			if(count%4 == 0 && count > 0) {
				x += X_SPACE;
				y = 10;
			}

			current.locate(dialog.getContentPane(), x, y);
			y += 50;
			count++;

			if(y > maxY)
				maxY = y;								
		}						


		x += X_SPACE;

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.controlHighlight);
		panel.setBounds(10, maxY, x-20, 1);
		dialog.getContentPane().add(panel);

		btnCancel.setBounds(x-89-10, maxY+7, 89, 23);
		btnAccept.setBounds(x-89-89-15, maxY+7, 89, 23);

		dialog.setBounds(100, 100, x+10, maxY+70);					
		dialog.setLocationRelativeTo(null);	
	}
	
	/**
	 * Devuelve un ComboBox asociado a un
	 * parámetro a partir de su nombre.
	 * 
	 * @param name Nombre.
	 * @return ComboBox asociado a un parámetro.
	 */
	private ParameterCombo getComboByName(String name) {
		ParameterCombo comp = null;
		Iterator<ParameterCombo> iter = combos.iterator();
		ParameterCombo current;

		while(comp == null && iter.hasNext()) {
			current = iter.next();
			if(current.getName().equals(name)) {
				comp = current;
			}
		}

		return comp;
	}

	/**
	 * Inicia el contenido del frame.
	 */
	private void initialize() {
		dialog = new JDialog(parent, true);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				if(!refreshMode)
					viewFrame.getFrame().dispose();
			}
		});
		
		dialog.setType(Type.UTILITY);
		dialog.setResizable(false);
		dialog.setBounds(100, 100, 303, 158);
		dialog.getContentPane().setLayout(null);
		dialog.setLocationRelativeTo(null);		
		dialog.setTitle(I18N.getInstance().getString(I18N.FILTER) + " - " + viewFrame.getFrame().getTitle());			

		btnAccept = new JButton(I18N.getInstance().getString(I18N.ACCEPT));
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				accept();
			}
		});
		btnAccept.setBounds(103, 100, 89, 23);
		dialog.getContentPane().add(btnAccept);

		btnCancel = new JButton(I18N.getInstance().getString(I18N.CANCEL));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancel();
			}
		});
		btnCancel.setBounds(201, 100, 89, 23);
		dialog.getContentPane().add(btnCancel);
		dialog.getRootPane().setDefaultButton(btnAccept);	

		loadConfig();
		createGUI();
	}

	/**
	 * Carga la configuración de
	 * la ventana.
	 */
	private void loadConfig() {
		String filename = PreferencesManager.getInstance().getParamsSelectorPath() + viewFrame.getViewInfo().getParameterizer();
		File file = new File(filename);

		if(file.exists()) {
			ParameterizeDialogConfig psc = new ParameterizeDialogConfig(viewFrame.getViewInfo().getParameterizer());
			Iterator<Parameter> iter = psc.getParams().iterator();
			Parameter current;
			
			while(iter.hasNext()) {
				current = iter.next();				

				ParameterCombo component = new ParameterCombo();
				component.setName(current.getName());
				component.setLabelText(I18N.getInstance().getString(current.getName()));				
				component.setType(current.getType());
				
				combos.add(component);
				ParameterCombo dependencyComp = getComboByName(current.getDependency());


				if(null == dependencyComp)
					component.loadComboWithQuery(current.getQuery());
				else {
					dependencyComp.createDependency(component, current.getQuery());
					dependencyComp.getComboBox().setSelectedIndex(0);
				}
			}
		}
	}

	/**
	 * Muestra los parámetros si existía alguno 
	 * previamente seleccionado.
	 */
	private void reloadParams() {
		Iterator<ViewParameter> iter = params.iterator();
		ViewParameter current;
		
		while(iter.hasNext()) {
			current = iter.next();
			
			ParameterCombo combo = getComboByName(current.getName());
			
			if(null != combo) {
				combo.getComboBox().setSelectedItem(String.valueOf(current.getValue()));
			}
		}
	}
}
