/**
* Copyright (C) 2013-2014 Christian M. Schmid
*
* This file is part of the jdotxt.
*
* jdotxt is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.chschmid.jdotxt.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.chschmid.jdotxt.Jdotxt;
import com.chschmid.jdotxt.gui.controls.JdotxtImageButton;
import com.todotxt.todotxttouch.util.Util;

@SuppressWarnings("serial")
public class JdotxtPreferencesDialog extends JDialog{
	final static String SETTINGS_PANEL = "Settings Panel";
	final static String HELP_PANEL     = "Help Panel";
	final static String ABOUT_PANEL    = "About Panel";
	
	private Box topPanel, okCancelBar;
	private JdotxtImageButton buttonSettings, buttonHelp, buttonAbout;
	private JPanel mainPanel;
	private JComboBox<String> language;
	
	// Settings controls
	private JTextField directory;
	private JCheckBox compactMode, projectsPanel, contextsPanel, switchPanels, prependMetadata, copyMetadata;
	
	public JdotxtPreferencesDialog() {
		super();
		initGUI();
	}

	private void initGUI() {
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle(JdotxtGUI.lang.getWord("jdotxt") + " " + JdotxtGUI.lang.getWord("Preferences"));
		this.setResizable(false);
		
		// Top bar (Settings, Help, Info, ...)
		topPanel = new Box(BoxLayout.X_AXIS);
		topPanel.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		topPanel.setOpaque(true);
		buttonSettings = new JdotxtImageButton(Util.createImageIcon("/res/drawable/settings.png"));
		buttonHelp     = new JdotxtImageButton(Util.createImageIcon("/res/drawable/help.png"));
		buttonAbout    = new JdotxtImageButton(Util.createImageIcon("/res/drawable/about.png"));
		
		styleJdotxtImageButton(buttonSettings, JdotxtGUI.lang.getWord("Settings"));
		styleJdotxtImageButton(buttonHelp, JdotxtGUI.lang.getWord("Help"));
		styleJdotxtImageButton(buttonAbout, JdotxtGUI.lang.getWord("About"));
		
		buttonSettings.setBackgroundColor(JdotxtGUI.COLOR_HOVER);
		buttonSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSettings.setBackgroundColor(JdotxtGUI.COLOR_HOVER);
				buttonSettings.requestFocus();
				buttonHelp.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
				buttonAbout.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
				((CardLayout)(mainPanel.getLayout())).show(mainPanel, SETTINGS_PANEL);
			}
		});
		buttonHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSettings.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
				buttonHelp.setBackgroundColor(JdotxtGUI.COLOR_HOVER);
				buttonHelp.requestFocus();
				buttonAbout.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
				((CardLayout)(mainPanel.getLayout())).show(mainPanel, HELP_PANEL);
			}
		});
		buttonAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSettings.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
				buttonHelp.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
				buttonAbout.setBackgroundColor(JdotxtGUI.COLOR_HOVER);
				buttonAbout.requestFocus();
				((CardLayout)(mainPanel.getLayout())).show(mainPanel, ABOUT_PANEL);
			}
		});
		
		topPanel.add(buttonSettings);
		topPanel.add(buttonHelp);
		topPanel.add(buttonAbout);
		
		// Main panel
		mainPanel = new JPanel(new CardLayout());
		mainPanel.add(getSettingsPanel(), SETTINGS_PANEL);
		mainPanel.add(getHelpPanel(), HELP_PANEL);
		mainPanel.add(getAboutPanel(), ABOUT_PANEL);
		
		// OK and Cancel button
		okCancelBar = new Box(BoxLayout.X_AXIS);
		okCancelBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 3));
		JButton ok = new JButton(JdotxtGUI.lang.getWord("OK"));
		JButton cancel = new JButton(JdotxtGUI.lang.getWord("Cancel"));
		
		ok.setFont(JdotxtGUI.fontR);
		cancel.setFont(JdotxtGUI.fontR);
		ok.setPreferredSize(cancel.getPreferredSize());
		
		okCancelBar.add(Box.createHorizontalGlue());
		okCancelBar.add(ok);
		okCancelBar.add(cancel);
		okCancelBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		okCancelBar.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		okCancelBar.setOpaque(true);
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JdotxtPreferencesDialog.this.dispose();
			}
		});
		
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
				JdotxtPreferencesDialog.this.dispose();
			}
		});

		// Compose all components
		this.add(topPanel, BorderLayout.PAGE_START);
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(okCancelBar, BorderLayout.PAGE_END);
		this.pack();
		
		// Center window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);
		
		loadSettings();
	}
	
	private JTabbedPane getSettingsPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(JdotxtGUI.fontR);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setOpaque(true);
		
		// File Panel
		JPanel filePanel = new JPanel();
		filePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.setBackground(Color.WHITE);
		filePanel.setOpaque(true);
		
		JLabel labelDirectory = new JLabel(JdotxtGUI.lang.getWord("Todo_file_location"));
		labelDirectory.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelDirectory.setFont(JdotxtGUI.fontR);
		
		directory = new JTextField();
		directory.setFont(JdotxtGUI.fontR);
		directory.setColumns(40);
		directory.setMaximumSize(directory.getPreferredSize());
		
		directory.setEnabled(false);
		directory.setAlignmentX(LEFT_ALIGNMENT);
		
		JButton btnChooseDir = new JButton(JdotxtGUI.lang.getWord("Choose_directory"));
		btnChooseDir.setFont(JdotxtGUI.fontR);
		btnChooseDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
		        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        chooser.setCurrentDirectory(new File(directory.getText()));
		        int returnVal = chooser.showDialog(JdotxtPreferencesDialog.this, null);
		        if(returnVal == JFileChooser.APPROVE_OPTION) {
		            directory.setText(chooser.getSelectedFile().getAbsolutePath());
		         }
			}
		});
		
		JLabel labelAutoSave = new JLabel(JdotxtGUI.lang.getWord("Autosave_options"));
		labelAutoSave.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelAutoSave.setFont(JdotxtGUI.fontR);
		
		JCheckBox enableAutosave = new JCheckBox("Enable autosave/autoreload");
		enableAutosave.setFont(JdotxtGUI.fontR);
		
		JLabel labelConflictResolution = new JLabel(JdotxtGUI.lang.getWord("Conflict_resolution"));
		labelConflictResolution.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelConflictResolution.setFont(JdotxtGUI.fontR);
		
		ButtonGroup group = new ButtonGroup();
		JRadioButton crAsk = new JRadioButton(JdotxtGUI.lang.getWord("CR_Ask"));
		JRadioButton crLocal = new JRadioButton(JdotxtGUI.lang.getWord("CR_Local"));
		JRadioButton crRemote = new JRadioButton(JdotxtGUI.lang.getWord("CR_Remote"));
		crAsk.setFont(JdotxtGUI.fontR);
		crLocal.setFont(JdotxtGUI.fontR);
		crRemote.setFont(JdotxtGUI.fontR);
		group.add(crAsk);
		group.add(crLocal);
		group.add(crRemote);
		
        JPanel conflictResolution = new JPanel(new GridLayout(0, 1));
        conflictResolution.add(crAsk);
        conflictResolution.add(crLocal);
        conflictResolution.add(crRemote);
        conflictResolution.setAlignmentX(Component.LEFT_ALIGNMENT);
        conflictResolution.setBackground(Color.WHITE);
        
        int crWidt = Math.max(crAsk.getPreferredSize().width, crLocal.getPreferredSize().width);
        crWidt = Math.max(crWidt, crRemote.getPreferredSize().width);
        
        conflictResolution.setMaximumSize(new Dimension(crWidt, crLocal.getPreferredSize().height));
        conflictResolution.revalidate();
        conflictResolution.repaint();
        
		filePanel.add(labelDirectory);
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.add(directory);
		filePanel.add(Box.createVerticalStrut(5));
		filePanel.add(btnChooseDir);
		filePanel.add(Box.createVerticalStrut(20));
		/*filePanel.add(labelAutoSave);
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.add(enableAutosave);
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.add(labelConflictResolution);
		filePanel.add(Box.createVerticalStrut(10));
		filePanel.add(conflictResolution);
		filePanel.add(Box.createVerticalGlue());*/
		
		// Display Panel
		JPanel displayPanel = new JPanel();
		displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		displayPanel.add(Box.createVerticalStrut(10));
		displayPanel.setBackground(Color.WHITE);
		displayPanel.setOpaque(true);
		
		compactMode = new JCheckBox();
		compactMode.setText(JdotxtGUI.lang.getWord("Text_compact_mode"));
		compactMode.setFont(JdotxtGUI.fontR);
		compactMode.setOpaque(false);
		
		copyMetadata = new JCheckBox();
		copyMetadata.setText(JdotxtGUI.lang.getWord("Copy_projects_contexts"));
		copyMetadata.setFont(JdotxtGUI.fontR);
		copyMetadata.setOpaque(false);
		
		prependMetadata = new JCheckBox();
		prependMetadata.setText(JdotxtGUI.lang.getWord("Prepend_projects_contexts"));
		prependMetadata.setFont(JdotxtGUI.fontR);
		prependMetadata.setOpaque(false);
		
		projectsPanel = new JCheckBox();
		projectsPanel.setText(JdotxtGUI.lang.getWord("Show_projects_panel"));
		projectsPanel.setFont(JdotxtGUI.fontR);
		projectsPanel.setOpaque(false);
		
		contextsPanel = new JCheckBox();
		contextsPanel.setText(JdotxtGUI.lang.getWord("Show_contexts_panel"));
		contextsPanel.setFont(JdotxtGUI.fontR);
		contextsPanel.setOpaque(false);
		
		switchPanels = new JCheckBox();
		switchPanels.setText(JdotxtGUI.lang.getWord("Switch_filter_panels"));
		switchPanels.setFont(JdotxtGUI.fontR);
		switchPanels.setOpaque(false);
		
		JLabel labelLanguage = new JLabel(JdotxtGUI.lang.getWord("Language"));
		labelLanguage.setFont(JdotxtGUI.fontR);
		
		language = new JComboBox<String>(JdotxtGUI.languages);
		language.setMaximumSize(language.getPreferredSize());
		language.setAlignmentX(Component.LEFT_ALIGNMENT);
		language.setFont(JdotxtGUI.fontR);
		
		displayPanel.add(compactMode);
		displayPanel.add(copyMetadata);
		displayPanel.add(prependMetadata);
		displayPanel.add(projectsPanel);
		displayPanel.add(contextsPanel);
		displayPanel.add(switchPanels);
		//displayPanel.add(Box.createVerticalStrut(20));
		//displayPanel.add(labelLanguage);
		//displayPanel.add(Box.createVerticalStrut(10));
		//displayPanel.add(language);
		
		tabbedPane.addTab(JdotxtGUI.lang.getWord("File"), filePanel);
		tabbedPane.addTab(JdotxtGUI.lang.getWord("Display"), displayPanel);
		return tabbedPane;
	}
	
	private JPanel getHelpPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);
		
		JLabel labelIcon = new JLabel(new ImageIcon(JdotxtGUI.icon.getImage().getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH)));
		labelIcon.setVerticalAlignment(SwingConstants.TOP);
		panel.add(labelIcon, BorderLayout.WEST);
		labelIcon.setPreferredSize(new Dimension(100, 100));
		
		JPanel panelInfo = new JPanel();
		panel.add(panelInfo, BorderLayout.CENTER);
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.add(Box.createVerticalStrut(10));
		panelInfo.setBackground(Color.WHITE);
		panelInfo.setOpaque(true);
		
		JLabel labelTitle = new JLabel(JdotxtGUI.lang.getWord("jdotxt") + " (Version " + Jdotxt.VERSION + ")");
		labelTitle.setFont(JdotxtGUI.fontB.deriveFont(16f));
		panelInfo.add(labelTitle);
		
		panelInfo.add(Box.createVerticalStrut(20));
		
		JEditorPane textInfo = new JEditorPane();
		textInfo.setContentType("text/html");
		textInfo.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textInfo.setFont(JdotxtGUI.fontR);
		textInfo.setText(JdotxtGUI.lang.getWord("Text_help"));
		textInfo.setEditable(false);
		textInfo.setFocusable(false);
		textInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		textInfo.setBorder(BorderFactory.createEmptyBorder());
		textInfo.setMaximumSize(textInfo.getPreferredSize());
		
		panelInfo.add(textInfo);
		
		panelInfo.add(Box.createVerticalStrut(20));
		
		JLabel labelShortcuts = new JLabel(JdotxtGUI.lang.getWord("Shortcuts"));
		labelShortcuts.setFont(JdotxtGUI.fontB.deriveFont(14f));
		panelInfo.add(labelShortcuts);
		
		panelInfo.add(Box.createVerticalStrut(20));
		
		JEditorPane textShortcuts = new JEditorPane();
		textShortcuts.setContentType("text/html");
		textShortcuts.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textShortcuts.setFont(JdotxtGUI.fontR);
		textShortcuts.setText(JdotxtGUI.lang.getWord("Text_shortcuts"));
		textShortcuts.setEditable(false);
		textShortcuts.setFocusable(false);
		textShortcuts.setAlignmentX(Component.LEFT_ALIGNMENT);
		textShortcuts.setBorder(BorderFactory.createEmptyBorder());
		textShortcuts.setMaximumSize(textShortcuts.getPreferredSize());
		
		panelInfo.add(textShortcuts);
		panelInfo.add(Box.createVerticalGlue());
		
		panel.revalidate();
		return panel;
	}
	
	private JPanel getAboutPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);
		
		JLabel labelIcon = new JLabel(new ImageIcon(JdotxtGUI.icon.getImage().getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH)));
		labelIcon.setVerticalAlignment(SwingConstants.TOP);
		panel.add(labelIcon, BorderLayout.WEST);
		labelIcon.setPreferredSize(new Dimension(100, 100));
		
		JPanel panelInfo = new JPanel();
		panel.add(panelInfo, BorderLayout.CENTER);
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.add(Box.createRigidArea(new Dimension(0, 10)));
		panelInfo.setBackground(Color.WHITE);
		panelInfo.setOpaque(true);
		
		JLabel labelTitle = new JLabel(JdotxtGUI.lang.getWord("jdotxt") + " (Version " + Jdotxt.VERSION + ")");
		labelTitle.setFont(JdotxtGUI.fontB.deriveFont(16f));
		panelInfo.add(labelTitle);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 20)));
		
		JEditorPane textInfo = new JEditorPane();
		textInfo.setContentType("text/html");
		textInfo.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textInfo.setFont(JdotxtGUI.fontR);
		textInfo.setText(JdotxtGUI.lang.getWord("Text_about"));
		textInfo.setEditable(false);
		textInfo.setFocusable(false);
		textInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		textInfo.setBorder(BorderFactory.createEmptyBorder());
		textInfo.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	if(Desktop.isDesktopSupported()) {
		        		try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							// Let's just not open the website
						} catch (URISyntaxException e1) {
							// Let's just not open the website
						}
		        	}
		        }
		    }
		});
		panelInfo.add(textInfo);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 20)));
		
		JLabel labelLicense = new JLabel(JdotxtGUI.lang.getWord("License"));
		labelLicense.setFont(JdotxtGUI.fontB.deriveFont(14f));
		panelInfo.add(labelLicense);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JEditorPane textLicense = new JEditorPane();
		textLicense.setContentType("text/html");
		textLicense.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textLicense.setFont(JdotxtGUI.fontR);
		textLicense.setText(JdotxtGUI.lang.getWord("Text_license"));
		textLicense.setEditable(false);
		textLicense.setFocusable(false);
		textLicense.setAlignmentX(Component.LEFT_ALIGNMENT);
		textLicense.setBorder(BorderFactory.createEmptyBorder());
		textLicense.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	if(Desktop.isDesktopSupported()) {
		        		try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							// Let's just not open the website
						} catch (URISyntaxException e1) {
							// Let's just not open the website
						}
		        	}
		        }
		    }
		});
		panelInfo.add(textLicense);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 20)));
		
		panel.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST);
		return panel;
	}
	
	private void saveSettings() {
		Jdotxt.userPrefs.put("dataDir", directory.getText());
		Jdotxt.userPrefs.putBoolean("compactMode", compactMode.isSelected());
		Jdotxt.userPrefs.putBoolean("copyMetadata", copyMetadata.isSelected());
		Jdotxt.userPrefs.putBoolean("prependMetadata", prependMetadata.isSelected());
		Jdotxt.userPrefs.putBoolean("showProjectsPanel", projectsPanel.isSelected());
		Jdotxt.userPrefs.putBoolean("showContextsPanel", contextsPanel.isSelected());
		Jdotxt.userPrefs.putBoolean("switchPanels", switchPanels.isSelected());
	}
	
	private void loadSettings() {
		directory.setText(Jdotxt.userPrefs.get("dataDir", Jdotxt.DEFAULT_DIR));
		compactMode.setSelected(Jdotxt.userPrefs.getBoolean("compactMode", false));
		copyMetadata.setSelected(Jdotxt.userPrefs.getBoolean("copyMetadata", false));
		prependMetadata.setSelected(Jdotxt.userPrefs.getBoolean("prependMetadata", false));
		projectsPanel.setSelected(Jdotxt.userPrefs.getBoolean("showProjectsPanel", true));
		contextsPanel.setSelected(Jdotxt.userPrefs.getBoolean("showContextsPanel", true));
		switchPanels.setSelected(Jdotxt.userPrefs.getBoolean("switchPanels", false));
	}
	
	private void styleJdotxtImageButton(JdotxtImageButton button, String toolTipText) {
		button.setToolTipText(toolTipText);
		button.setHoverColor(JdotxtGUI.COLOR_HOVER);
		button.setPressedColor(JdotxtGUI.COLOR_PRESSED);
		button.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
	}
}
