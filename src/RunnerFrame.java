import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import tools.IO;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class RunnerFrame extends JFrame implements KeyListener {
    private static final long serialVersionUID = 1L;
    public JSeparator submissionSep;
    public JLabel optionalLabel;
    public JLabel tutorialLabel;
    public JButton startButton;
    private JLabel question;
    private JCheckBox[] checkboxes;
    private JButton submitButton;

    public RunnerFrame(String[] mechanics) {
	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	this.setLocation(300, 100);
	this.addKeyListener(this);
	this.checkboxes = new JCheckBox[mechanics.length];
	for (int i = 0; i < mechanics.length; i++) {
	    this.checkboxes[i] = new JCheckBox(mechanics[i]);
	}

	startButton = new JButton("Play Hand Crafted Level");
	startButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		Runner.mouseClick = RunnerEnum.TUTORIAL;
	    }
	});
	startButton.setFocusable(false);

	optionalLabel = new JLabel(
		"<html><div align=\"center\">(Mandatory) Play this handcrafted level to<br/>understand the game</div></html>");
	optionalLabel.setHorizontalAlignment(JLabel.CENTER);

	tutorialLabel = new JLabel();
	tutorialLabel.setHorizontalAlignment(JLabel.CENTER);

	submitButton = new JButton("Submit");
	submitButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		Runner.mouseClick = RunnerEnum.SUBMIT;
		replyToGoogleForm();
	    }
	});
	submitButton.setFocusable(false);
	question = new JLabel("Which level do you prefer?");
	submissionSep = new JSeparator();

	JPanel pane = (JPanel) getContentPane();
	pane.setAlignmentX(JPanel.CENTER_ALIGNMENT);
	pane.setAlignmentY(JPanel.CENTER_ALIGNMENT);
	GroupLayout gl = new GroupLayout(pane);
	pane.setLayout(gl);

	pane.setToolTipText("Content pane");
	gl.setAutoCreateContainerGaps(true);
	gl.setAutoCreateGaps(true);

	ParallelGroup horizontalGroup = gl.createParallelGroup(Alignment.CENTER).addComponent(tutorialLabel)
		.addComponent(optionalLabel).addComponent(startButton).addComponent(submissionSep)
		.addComponent(question);
	for (JCheckBox box : this.checkboxes) {
	    horizontalGroup = horizontalGroup.addComponent(box);
	}
	horizontalGroup = horizontalGroup.addComponent(submitButton);
	gl.setHorizontalGroup(horizontalGroup);

	SequentialGroup verticalGroup = gl.createSequentialGroup().addComponent(tutorialLabel)
		.addComponent(optionalLabel).addComponent(startButton).addComponent(submissionSep)
		.addComponent(question);
	for (JCheckBox box : this.checkboxes) {
	    verticalGroup = verticalGroup.addComponent(box);
	}
	verticalGroup = verticalGroup.addComponent(submitButton);
	gl.setVerticalGroup(verticalGroup);

	pack();
    }

    public void setSubmitEnable(boolean enable) {
	submitButton.setEnabled(enable);
	for (JCheckBox box : this.checkboxes) {
	    box.setEnabled(enable);
	}
    }

    public void resetCheckBoxes() {
	this.setSubmitEnable(false);
	for (JCheckBox box : this.checkboxes) {
	    box.setSelected(false);
	}
    }

    private void replyToGoogleForm() {
	this.setSubmitEnable(false);

	IO linkReader = new IO();
	String[] data = linkReader.readFile("submissionLinks.txt");
	try {
	    String response = "entry." + data[1] + "=" + Runner.games.get(Runner.chosenGame) + "&entry." + data[2] + "="
		    + Runner.chosenLevel + "&entry." + data[3] + "=" + getActions() + "&entry." + data[4] + "="
		    + getMechanics() + "&entry." + data[5] + "=" + getChoices() + "&entry." + data[6] + "=" + Runner.win
		    + "," + Runner.score + "," + Runner.time;
	    URL url = new URL(data[0]);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    connection.setDoOutput(true);
	    connection.setDoInput(true);
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	    DataOutputStream dataStream = new DataOutputStream(connection.getOutputStream());
	    dataStream.writeBytes(response);
	    dataStream.flush();
	    dataStream.close();

	    InputStream dataInput = connection.getInputStream();
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInput));

	    System.out.println(bufferedReader.readLine());
	    dataInput.close();
	    // JOptionPane.showMessageDialog(this,
	    // "Data is submitted.");
	    // this.setVisible(true);
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(this, "Can not connect to the server! Check your internet connection.");
	}
	Runner.submissionDone = true;
	this.resetCheckBoxes();
    }

    public String getMechanics() {
	return "";
    }

    public String getActions() {
	String actionFile = "examples/actions/" + Runner.games.get(Runner.chosenGame) + "_lvl" + Runner.chosenLevel + ".txt";
	String result = "";
	IO reader = new IO();
	String[] lines = reader.readFile(actionFile);
	for (int i = 1; i < lines.length; i++) {
	    result += lines[i] + ",";
	}
	return result;
    }

    public String getChoices() {
	String result = "";
	for (JCheckBox box : this.checkboxes) {
	    if (box.isSelected()) {
		result += box.getText() + ",";
	    }
	}
	return result;
    }

    @Override
    public void keyTyped(KeyEvent e) {
	// TODO Auto-generated method stub
    }

    @Override
    public void keyPressed(KeyEvent e) {
	// TODO Auto-generated method stub
    }

    @Override
    public void keyReleased(KeyEvent e) {
	// TODO Auto-generated method stub
    }
}
