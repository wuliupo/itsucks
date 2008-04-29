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

import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.phleisch.app.itsucks.gui.job.ifc.AddDownloadJobCapable;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobBasicPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobContentFilterPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobRegExpRulesPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobSimpleRulesPanel;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobSpecialRulesPanel;
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
	}
    
    protected void registerTreeNodes() {
		
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Job Configuration");
    	
    	//basic parameters
    	DownloadJobBasicPanel basicParametersPanel = new DownloadJobBasicPanel();
    	DefaultMutableTreeNode basicParameters = new DefaultMutableTreeNode();
    	basicParameters.setUserObject(
    			new JobTreeNode("Basic Parameters", basicParametersPanel, basicParametersPanel));
    	root.add(basicParameters);

    	//simple rules
    	DownloadJobSimpleRulesPanel simpleRulesPanel = new DownloadJobSimpleRulesPanel();
    	DefaultMutableTreeNode simpleRules = new DefaultMutableTreeNode();
    	simpleRules.setUserObject(
    			new JobTreeNode("Simple Rules", simpleRulesPanel, simpleRulesPanel));
    	root.add(simpleRules);

    	//special rules
    	DownloadJobSpecialRulesPanel specialRulesPanel = new DownloadJobSpecialRulesPanel();
    	DefaultMutableTreeNode specialRules = new DefaultMutableTreeNode();
    	specialRules.setUserObject(
    			new JobTreeNode("Special Rules", specialRulesPanel, specialRulesPanel));
    	root.add(specialRules);
    	
    	//reg exp rules
    	DownloadJobRegExpRulesPanel advancedRegExpRulesPanel = new DownloadJobRegExpRulesPanel();
    	DefaultMutableTreeNode advancedRegExpRules = new DefaultMutableTreeNode();
    	advancedRegExpRules.setUserObject(
    			new JobTreeNode("Advanced RegExp Rules", advancedRegExpRulesPanel, advancedRegExpRulesPanel));
    	root.add(advancedRegExpRules);
    	
    	//content filter
    	DownloadJobContentFilterPanel contentFilterPanel = new DownloadJobContentFilterPanel();
    	DefaultMutableTreeNode contentFilterRules = new DefaultMutableTreeNode();
    	contentFilterRules.setUserObject(
    			new JobTreeNode("Content Filter", contentFilterPanel, contentFilterPanel));
    	root.add(contentFilterRules);

    	
    	DefaultTreeModel model = new DefaultTreeModel(root);
    	tree.setModel(model);
    	
        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new JobTreeListener());
        tree.setSelectionPath(new TreePath(basicParameters.getPath()));
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

        buttonPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        panelArea = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(633, 750));

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

        tree.setBorder(null);
        tree.setAutoscrolls(true);
        tree.setToggleClickCount(1);
        jScrollPane1.setViewportView(tree);

        panelArea.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelArea.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE))
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelArea;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton startButton;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
    
}
