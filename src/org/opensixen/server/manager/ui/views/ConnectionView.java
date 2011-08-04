package org.opensixen.server.manager.ui.views;

import java.util.Properties;

import org.compiere.util.Ini;
import org.eclipse.swt.SWT;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.opensixen.os.PlatformDetails;
import org.opensixen.os.PlatformProvider;
import org.opensixen.os.ProviderFactory;
import org.opensixen.p2.applications.InstallJob;
import org.opensixen.server.manager.ui.Messages;

public class ConnectionView extends ViewPart implements SelectionListener, ModifyListener {

	public static final String ID = "org.opensixen.server.manager.ui.views.ConnectionView";
	
	private PlatformProvider provider;
	
	private Combo fDbType;
	private Text fDbHost;
	private Text fDBuser;
	private Text fDBPasswd;
	private Text fDbPort;
	private Text fDbName;
				
	// Hardcode. From Database.DB_NAMES
	private String DB_POSTGRES="PostgreSQL";
	private String PORT_POSTGRES ="5432";			
	private String DB_ORACLE = "Oracle";
	private String PORT_ORACLE = "1521";
	
	private String[] dbNames = {DB_POSTGRES, DB_ORACLE}; //$NON-NLS-1$ //$NON-NLS-2$
	private Text fASHost;
	private Text fASPort;

	private Properties config;
			
	/**
	 * @param pageName
	 */
	public ConnectionView() {
		provider = ProviderFactory.getProvider();
	}

	public void createPartControl(Composite parent) {	
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		
		Group setup = new Group(container, SWT.NONE);
		setup.setLayout(new GridLayout(2, false));
		setup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setup.setText(Messages.CONFIG);

		Label l = new Label(setup, SWT.NONE);
		l.setText(Messages.AS_HOST);
		fASHost = new Text(setup, SWT.BORDER);
		fASHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		fASHost.addModifyListener(this);
		
		// By default, the hostname is the same as the localhost
		//fASHost.setText(PlatformDetails.getHostname());
		
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.AS_PORT);
		fASPort = new Text(setup, SWT.BORDER);
		fASPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));				

		fASPort.setText("8080"); //$NON-NLS-1$
		// Not configurable
		//fASPort.addModifyListener(this);
		fASPort.setEnabled(false);
		
				
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DATABASE_TYPE);
		fDbType = new Combo(setup, SWT.DROP_DOWN);
		fDbType.setItems(dbNames);
		fDbType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		fDbType.setText(DB_POSTGRES);
		fDbType.addSelectionListener(this);
		
		
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DB_PORT);
		fDbPort = new Text(setup, SWT.BORDER);
		fDbPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		fDbPort.setText(PORT_POSTGRES);
		fDbPort.addModifyListener(this);
		
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DB_HOST);
		fDbHost = new Text(setup, SWT.BORDER);
		fDbHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		fDbHost.addModifyListener(this);
		
		// By default, the hostname is the same as the localhost
		//fDbHost.setText(PlatformDetails.getHostname());
		/*
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DB_USER_SYSTEM);
		
		fDBSystemuser = new Text(setup, SWT.BORDER);
		fDBSystemuser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		fDBSystemuser.addModifyListener(this);
		
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DB_PWD_SYSTEM);
		
		fDBSystemPasswd = new Text(setup, SWT.PASSWORD | SWT.BORDER);
		fDBSystemPasswd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));	
		fDBSystemPasswd.addModifyListener(this);
		*/
		
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DB_NAME);
		fDbName = new Text(setup, SWT.BORDER);
		fDbName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		fDbName.addModifyListener(this);
		
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DB_USER);
		
		fDBuser = new Text(setup, SWT.BORDER);
		fDBuser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		// Not configurable
		fDBuser.setText("adempiere"); //$NON-NLS-1$
		//fDBuser.addModifyListener(this);
		fDBuser.setEnabled(false);
		
		
		
		
		l = new Label(setup, SWT.NONE);
		l.setText(Messages.DB_PASSWORD);
		
		fDBPasswd = new Text(setup, SWT.PASSWORD | SWT.BORDER);
		fDBPasswd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		fDBPasswd.addModifyListener(this);
					
		init();
	}


	private void init()	{
		InstallJob job = InstallJob.getInstance();
		PlatformDetails details = provider.getPlatformDetails();
		if (fASHost.getText().length() == 0)	{
			fASHost.setText(details.getHostname());
		}
		
		if (fDbHost.getText().length() == 0)	{
			fDbHost.setText(details.getHostname());
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {	
						
		// If change combo, change default port
		if (e.getSource().equals(fDbType))		{		
			if (fDbType.getText().equals(DB_POSTGRES))	{ //$NON-NLS-1$
				fDbPort.setText(PORT_POSTGRES); //$NON-NLS-1$
			}
			else {
				fDbPort.setText(PORT_ORACLE); //$NON-NLS-1$
			}
		}				
		
	}

	/**
	 *  String representation.
	 *  Used also for Instanciation
	 *  @return string representation
	 *	@see #setAttributes(String) setAttributes
	 */
	public String getConnectionString ()
	{				
		StringBuffer sb = new StringBuffer ("CConnection["); //$NON-NLS-1$
		sb.append ("name=").append (config.getProperty("name")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",AppsHost=").append (config.getProperty("AppsHost")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",AppsPort=").append (config.getProperty("AppsPort")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",type=").append (config.getProperty("type")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",DBhost=").append (config.getProperty("DBhost")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",DBport=").append (config.getProperty("DBport")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",DBname=").append (config.getProperty("DBname")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",BQ=").append (config.getProperty("BQ")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",FW=").append (config.getProperty("FW")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",FWhost=").append (config.getProperty("FWhost")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",FWport=").append (config.getProperty("FWport")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",UID=").append (config.getProperty("UID")) //$NON-NLS-1$ //$NON-NLS-2$
		  .append (",PWD=").append (config.getProperty("PWD")) //$NON-NLS-1$ //$NON-NLS-2$
		  ;		//	the format is read by setAttributes
		sb.append ("]"); //$NON-NLS-1$
		return sb.toString ();
	}	//  toStringLong

	
	private boolean createProperties(String path)	{
		// Setup as server and setup path as adempiere home
		Ini.setClient(false);
		Ini.setAdempiereHome(path);
		Ini.loadProperties(true);		
		Ini.setProperty (Ini.P_CONNECTION, getConnectionString());
		
		Ini.saveProperties(false);
		return true;
		
	}
	
	public void setFocus() {
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyText(ModifyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
