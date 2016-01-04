package app;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Created by TangJiong on 2015/12/14.
 * The UI and front-end of the program.
 */
public class Editor extends JFrame {
	JTextPane textPane = new JTextPane();
	JLabel fileNameLable= new JLabel("newfile");
	JTextArea consoleArea = new JTextArea();
	JFileChooser filechooser = new JFileChooser();
    Checkbox isShowLex = new Checkbox("show lexcial result",true);
    Checkbox isShowTree = new Checkbox("show parse tree",false);

	public Editor() {
		super("CMM Compiler");
		setMinimumSize(new Dimension(800,600));
		setPreferredSize(new Dimension(1000,800));
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Action[] actions = { 
				new NewAction(), 
				new OpenAction(), 
				new SaveAction(),
				new RunAction(),
				new AboutAction()};


		Container container = getContentPane();

		container.add(createJToolBar(actions),BorderLayout.NORTH);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2,1,0,10));

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(fileNameLable, BorderLayout.NORTH);
		textPane.setFont(new Font("宋体",0,14));
		textPane.setBorder(new LineNumberBorder());
		JScrollPane textScrollPane = new JScrollPane(textPane);
		inputPanel.add(textScrollPane, BorderLayout.CENTER);

		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BorderLayout());
		JPanel consoleMenu = new JPanel();
		consoleMenu.setLayout(new GridLayout(1,4,10,0));
		consoleMenu.add(new JLabel("Console"));

        consoleMenu.add(isShowLex);

        consoleMenu.add(isShowTree);

		JButton btnClearConsole = new JButton(new ClearAction());
		consoleMenu.add(btnClearConsole);
		outputPanel.add(consoleMenu, BorderLayout.NORTH);
		consoleArea.setLineWrap(true);
		consoleArea.setFont(new Font("宋体",0,14));
		JScrollPane consoleScrollPane = new JScrollPane(consoleArea);
		outputPanel.add(consoleScrollPane, BorderLayout.CENTER);

		mainPanel.add(inputPanel);
		mainPanel.add(outputPanel);

		container.add(mainPanel, BorderLayout.CENTER);
		container.add(new JLabel("TangJiong 330194737@qq.com"), BorderLayout.SOUTH);

	}

	private JToolBar createJToolBar(Action[] actions) {
		JToolBar toolBar = new JToolBar();
		for (int i = 0; i < actions.length; i++) {
			JButton bt = new JButton(actions[i]);
			bt.setRequestFocusEnabled(false);
			toolBar.add(bt);
		}
		return toolBar;
	}

	class NewAction extends AbstractAction {
		public NewAction() {
			super("New");
		}

		public void actionPerformed(ActionEvent e) {
			fileNameLable.setText("newfile");
			textPane.setDocument(new DefaultStyledDocument());
		}
	}

	class OpenAction extends AbstractAction {
		public OpenAction() {
			super("Open");
		}

		public void actionPerformed(ActionEvent e) {
			int i = filechooser.showOpenDialog(Editor.this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File f = filechooser.getSelectedFile();
				try {
					fileNameLable.setText(f.getName());
					InputStream is = new FileInputStream(f);
					textPane.read(is, "d");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	class SaveAction extends AbstractAction {
		public SaveAction() {
			super("Save");
		}

		public void actionPerformed(ActionEvent e) {
			int i = filechooser.showSaveDialog(Editor.this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File f = filechooser.getSelectedFile();
				try {
					FileOutputStream out = new FileOutputStream(f);
					out.write(textPane.getText().getBytes());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	class RunAction extends AbstractAction {
		public RunAction() {
			super("Run");
		}

		public void actionPerformed(ActionEvent e) {
			Compiler compiler = new Compiler(textPane.getText(), new ConsoleIO());
            compiler.setShowLexerResult(isShowLex.getState());
            compiler.setShowAST(isShowTree.getState());
            compiler.run();
		}
	}

	class AboutAction extends AbstractAction {
		public AboutAction() {
			super("About");
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(Editor.this, "A simple CMM compiler.\n Contact 330194737@qq.com for any problem."); // ��ʾ�����Ϣ
		}
	}

	class ClearAction extends AbstractAction{

		public ClearAction(){super("Clear");}

		@Override
		public void actionPerformed(ActionEvent e) {
			consoleArea.setText("");
		}
	}

	public static void main(String[] args) {
		new Editor();
	}

}
