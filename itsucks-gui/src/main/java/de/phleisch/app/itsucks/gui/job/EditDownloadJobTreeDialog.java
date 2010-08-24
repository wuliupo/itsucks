/*
 * EditDownloadJobTreeDialog.java
 *
 * Created on 27. April 2008, 19:25
 */

package de.phleisch.app.itsucks.gui.job;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.help.HelpBroker;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.phleisch.app.itsucks.gui.job.ifc.AddDownloadJobCapable;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobAuthenticationSettings;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobBasicPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobConnectionSettingsPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobContentFilterPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobCookieSettings;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobFileSizeRulesPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobHttpResponseBehaviourPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobRegExpRuleChainPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobSimpleRulesPanel;
import de.phleisch.app.itsucks.gui.util.HelpManager;
import de.phleisch.app.itsucks.gui.util.TreeVisitor;
import de.phleisch.app.itsucks.gui.util.TreeVisitor.TreeListener;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  olli
 */
public class EditDownloadJobTreeDialog extends javax.swing.JDialog {
    
	private static final long serialVersionUID = -3441346245316972387L;
	
	private AddDownloadJobCapable mDownloadJobManager = null;
	
	/** Creates new form EditDownloadJobTreeDialog */
    public EditDownloadJobTreeDialog(Frame pOwner,
			AddDownloadJobCapable pDownloadJobManager) {
		super(pOwner);
		init(pDownloadJobManager);
    }
    
	public EditDownloadJobTreeDialog(Dialog pOwner,
			AddDownloadJobCapable pDownloadJobManager) {

		super(pOwner);
		init(pDownloadJobManager);
	}
	
	private void init(AddDownloadJobCapable pDownloadJobManager) {
		mDownloadJobManager = pDownloadJobManager;
    	
        initComponents();
        registerTreeNodes();
        
		//register help
		HelpBroker helpBroker = HelpManager.getInstance().getHelpBroker();
		if(helpBroker != null) {
			helpBroker.enableHelpKey(this.getRootPane(), 
					"job configuration", helpBroker.getHelpSet());
		}
	}
    
    protected void registerTreeNodes() {
		
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Job Configuration");
    	
    	//basic parameters
    	DefaultMutableTreeNode basicParameters = new DefaultMutableTreeNode();
    	DownloadJobBasicPanel basicParametersPanel = new DownloadJobBasicPanel();
    	basicParameters.setUserObject(
    			new JobTreeNode("Basic Parameters", basicParametersPanel, basicParametersPanel));
    	root.add(basicParameters);

    	//connection
    	DefaultMutableTreeNode connection = new DefaultMutableTreeNode("Connection");
    	root.add(connection);
    	
    	//Basic settings
    	DefaultMutableTreeNode connectionSettings = new DefaultMutableTreeNode();
    	DownloadJobConnectionSettingsPanel basicConnectionSettingsPanel = new DownloadJobConnectionSettingsPanel();
    	connectionSettings.setUserObject(
    			new JobTreeNode("Basic Settings", basicConnectionSettingsPanel, basicConnectionSettingsPanel));
    	connection.add(connectionSettings);
    	
    	//Authentication settings
    	DefaultMutableTreeNode authenticationSettings = new DefaultMutableTreeNode();
    	DownloadJobAuthenticationSettings authenticationSettingsPanel = new DownloadJobAuthenticationSettings();
    	authenticationSettings.setUserObject(
    			new JobTreeNode("Authentication Settings", authenticationSettingsPanel, authenticationSettingsPanel));
    	connection.add(authenticationSettings);
    	
    	//Cookie settings
    	DefaultMutableTreeNode cookieSettings = new DefaultMutableTreeNode();
    	DownloadJobCookieSettings cookieSettingsPanel = new DownloadJobCookieSettings();
    	cookieSettings.setUserObject(
    			new JobTreeNode("Cookie Settings", cookieSettingsPanel, cookieSettingsPanel));
    	connection.add(cookieSettings);
    	
    	//http response behaviour
    	DefaultMutableTreeNode httpResponseBehaviour = new DefaultMutableTreeNode();
    	DownloadJobHttpResponseBehaviourPanel httpResponseBehaviourPanel = new DownloadJobHttpResponseBehaviourPanel();
    	httpResponseBehaviour.setUserObject(
    			new JobTreeNode("Http Response Behaviour", httpResponseBehaviourPanel, httpResponseBehaviourPanel));
    	connection.add(httpResponseBehaviour);
    	
    	//rules
    	DefaultMutableTreeNode rules = new DefaultMutableTreeNode("Rules");
    	root.add(rules);
    	
    	//simple rules
    	DefaultMutableTreeNode simpleRules = new DefaultMutableTreeNode();
    	DownloadJobSimpleRulesPanel simpleRulesPanel = new DownloadJobSimpleRulesPanel();
    	simpleRules.setUserObject(
    			new JobTreeNode("Simple Rules", simpleRulesPanel, simpleRulesPanel));
    	rules.add(simpleRules);

    	//file size rule
    	DefaultMutableTreeNode fileSizeRule = new DefaultMutableTreeNode();
    	DownloadJobFileSizeRulesPanel specialRulesPanel = new DownloadJobFileSizeRulesPanel();
    	fileSizeRule.setUserObject(
    			new JobTreeNode("File Size Rule", specialRulesPanel, specialRulesPanel));
    	rules.add(fileSizeRule);
    	
    	//regexp rulechain
    	DefaultMutableTreeNode regExpRuleChain = new DefaultMutableTreeNode();
    	DownloadJobRegExpRuleChainPanel advancedRegExpRulesPanel = new DownloadJobRegExpRuleChainPanel();
    	regExpRuleChain.setUserObject(
    			new JobTreeNode("RegExp Chain", advancedRegExpRulesPanel, advancedRegExpRulesPanel));
    	rules.add(regExpRuleChain);
    	
    	//filter
    	DefaultMutableTreeNode filter = new DefaultMutableTreeNode("Filter");
    	root.add(filter);
    	
    	//content filter
    	DefaultMutableTreeNode contentFilter = new DefaultMutableTreeNode();
    	DownloadJobContentFilterPanel contentFilterPanel = new DownloadJobContentFilterPanel();
    	contentFilter.setUserObject(
    			new JobTreeNode("Content Filter", contentFilterPanel, contentFilterPanel));
    	filter.add(contentFilter);

    	
    	DefaultTreeModel model = new DefaultTreeModel(root);
    	tree.setModel(model);
    	
        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new JobTreeListener());
        
        //set basic parameters as default
        tree.setSelectionPath(new TreePath(basicParameters.getPath()));
        EditDownloadJobTreeDialog.this.pack();
    }
    
	public void loadJob(final SerializableJobPackage pJobPackage) {
		List<EditJobCapable> allEditJobCapable = getAllEditJobCapable();
		
		for (EditJobCapable editJobCapable : allEditJobCapable) {
			editJobCapable.loadJobPackage(pJobPackage);
		}
	}
    
	protected List<EditJobCapable> getAllEditJobCapable() {
		
		final List<EditJobCapable> list = new ArrayList<EditJobCapable>();
		
		TreeVisitor treeVisitor = new TreeVisitor();
		treeVisitor.registerListener(new TreeListener() {

			public void processNode(TreeNode pTreeNode) {
				JobTreeNode jobNode = getJobTreeNode(pTreeNode);
				if(jobNode != null && jobNode.getEditJobCapable() != null) {
					list.add(jobNode.getEditJobCapable());
				}
			}
		});
		
		treeVisitor.visit((TreeNode) tree.getModel().getRoot());
		
		return list;
	}
	
	protected JobTreeNode getJobTreeNode(TreeNode pTreeNode) {
		
		JobTreeNode jobNode = null;
		
		if(pTreeNode instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) pTreeNode;
			Object userObject = node.getUserObject();
			if(userObject != null && userObject instanceof JobTreeNode) {
				jobNode = (JobTreeNode) userObject;
			}
		}
	
		return jobNode;
	}
	
	protected TreeNode findNodeByEditJobCapable(final EditJobCapable pJobCapable) {
		
		final List<TreeNode> result = new ArrayList<TreeNode>();
		
		TreeVisitor treeVisitor = new TreeVisitor();
		treeVisitor.registerListener(new TreeListener() {

			public void processNode(TreeNode pTreeNode) {
				JobTreeNode jobNode = getJobTreeNode(pTreeNode);
				if(jobNode != null && jobNode.getEditJobCapable() == pJobCapable) {
					result.add(pTreeNode);
				}
			}
		});
		
		treeVisitor.visit((TreeNode) tree.getModel().getRoot());
		
		if(result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}
	
    protected class JobTreeListener implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent pEvent) {
			
			Object pathComponent = tree.getLastSelectedPathComponent();
			if(!(pathComponent instanceof DefaultMutableTreeNode)) {
				return;
			}
			
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode)pathComponent;

		    panelArea.removeAll();
		    Object userObject = node.getUserObject();
		    
		    if(userObject instanceof JobTreeNode) {
			    JobTreeNode jobNode = (JobTreeNode) userObject;
			    if(jobNode != null && jobNode.getEditPanel() != null) {
			    	panelArea.add(jobNode.getEditPanel());
			    }
		    }
		    
		    panelArea.revalidate();
		    panelArea.repaint();
		}
    	
    }
    
    protected class JobTreeNode {
    	
    	private String mTitle;
    	private Component mEditPanel;
    	private EditJobCapable mEditJobCapable;

    	public JobTreeNode(String pTitle) {
    		this(pTitle, null, null);
    	}
    	
    	public JobTreeNode() {
    	}
    	
    	public JobTreeNode(String pTitle, Component pEditPanel, EditJobCapable pEditJobCapable) {
    		mTitle = pTitle;
    		mEditPanel = pEditPanel;
    		mEditJobCapable = pEditJobCapable;
    	}
    	
		public Component getEditPanel() {
			return mEditPanel;
		}

		public void setEditPanel(Component pEditPanel) {
			mEditPanel = pEditPanel;
		}

		public String getTitle() {
			return mTitle;
		}

		public void setTitle(String pTitle) {
			mTitle = pTitle;
		}

		public EditJobCapable getEditJobCapable() {
			return mEditJobCapable;
		}

		public void setEditJobCapable(EditJobCapable pEditJobCapable) {
			mEditJobCapable = pEditJobCapable;
		}
		
		public String toString() {
			return mTitle;
		}
    	
    }
    

	public SerializableJobPackage buildJob() {

		List<EditJobCapable> allEditJobCapable = getAllEditJobCapable();
		
		if (!validatePanels(allEditJobCapable))
			return null;
		
		SerializableJobPackage result = new SerializableJobPackage();
		
		for (EditJobCapable jobPanel : allEditJobCapable) {
			jobPanel.saveJobPackage(result);
		}

		return result;
	}

	private boolean validatePanels(List<EditJobCapable> pEditJobCapable) {

		boolean result = true;
		
		for (EditJobCapable jobPanel : pEditJobCapable) {
			List<String> errors = jobPanel.validateFields();
			if(errors != null && errors.size() > 0) {
				result = false;
				
				DefaultMutableTreeNode node = 
					(DefaultMutableTreeNode) findNodeByEditJobCapable(jobPanel);
				tree.setSelectionPath(new TreePath(node.getPath()));
				
				displayErrors(errors);
				
				break;
			}
		}

		return result;
	}

	private void displayErrors(List<String> errorsBasicPanel) {
		StringBuffer buffer = new StringBuffer();
		for (String string : errorsBasicPanel) {
			buffer.append(string + '\n');
		}

		JOptionPane.showMessageDialog(this, buffer.toString(),
				"Validation errors", JOptionPane.ERROR_MESSAGE);
	}


	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        treeScrollPane = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        buttonPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        panelAreaScrollPane = new javax.swing.JScrollPane();
        panelArea = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit download job");
        setLocationByPlatform(true);
        setName("editDownloadJobTreeDialog"); // NOI18N

        tree.setBorder(null);
        tree.setAutoscrolls(true);
        treeScrollPane.setViewportView(tree);

        startButton.setText("Start download");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(startButton);

        saveButton.setText("Save as template");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(saveButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        panelAreaScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelAreaScrollPane.setViewportBorder(null);
        panelAreaScrollPane.setDoubleBuffered(true);
        panelAreaScrollPane.setPreferredSize(new java.awt.Dimension(600, 12));

        panelArea.setBorder(null);
        panelArea.setLayout(new java.awt.BorderLayout());
        panelAreaScrollPane.setViewportView(panelArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(treeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelAreaScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(treeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                    .addComponent(panelAreaScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        SerializableJobPackage job = buildJob();
        if (job == null)
            return;
        
        boolean savePathOk = true;
        DownloadJob downloadJob = (DownloadJob) job.getJobs().get(0);
        File path = downloadJob.getSavePath();
        if (!path.exists()) {
            
            int result = JOptionPane.showConfirmDialog(this, "The save path does not exists. " +
                    "Should it be created?",
                    "Create save path?", JOptionPane.YES_NO_OPTION);
            
            if(result == JOptionPane.YES_OPTION) {
                boolean success = path.mkdirs();
                if(!success) {
                    JOptionPane.showMessageDialog(this, "The save path could not be created!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    savePathOk = false;
                }
                
            } else {
                savePathOk = false;
            }
            
        }
        
        if (savePathOk && !path.canWrite()) {
            
            JOptionPane.showMessageDialog(this, "The save path is not writable!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            
            savePathOk = false;
        }
        
        if(!savePathOk) {
            return;
        }
        
        mDownloadJobManager.addDownload(job);
        
        this.dispose();
    }//GEN-LAST:event_startButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
		SerializableJobPackage downloadJobList = buildJob();
		if (downloadJobList == null)
			return;
		
		EditDownloadJobHelper helper = new EditDownloadJobHelper(this);
		helper.saveDownloadTemplate(downloadJobList);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel panelArea;
    private javax.swing.JScrollPane panelAreaScrollPane;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton startButton;
    private javax.swing.JTree tree;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables
    
}
