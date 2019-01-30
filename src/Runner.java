import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import tracks.ArcadeMachine;

public class Runner {
    public static int numLevels;
    public static int playLevels;

    public static Random random;
    public static int chosenGame;
    public static int chosenLevel;
    public static double win;
    public static double score;
    public static double time;
    public static boolean submissionDone;

    public static ArrayList<String> games;

    public static RunnerEnum mouseClick;

    public static String firstGenerator;
    public static String secondGenerator;
    public static String firstLevelNumber;
    public static String secondLevelNumber;
    public static String tutorialLevelNumber;
    public static boolean playedFirst;
    public static boolean playedSecond;

    public static void main(String[] args) {
	numLevels = 3;
	playLevels = 1;
	chosenGame = 0;

	File[] files = new File("examples/games/").listFiles();
	games = new ArrayList<String>();
	for (File f : files) {
	    String temp = f.getName().substring(0, f.getName().lastIndexOf('.'));
	    if (temp.trim().length() > 0)
		games.add(temp);
	}
	random = new Random();
	mouseClick = RunnerEnum.NONE;

	ReasonFrame reasonFrame = new ReasonFrame();
	HashMap<String, RunnerFrame> frames = new HashMap<>();
	for (String g : games) {
	    frames.put(g, new RunnerFrame(SurveyText.getSurveyText(g)));
	    frames.get(g).setVisible(false);
	    frames.get(g).setFocusable(false);
	    frames.get(g).setTitle(g);
	}
	reasonFrame.setVisible(true);
	reasonFrame.setFocusable(true);
	while (mouseClick == RunnerEnum.NONE) {
	    System.out.print("");
	}
	mouseClick = RunnerEnum.NONE;
	reasonFrame.setVisible(false);

	submissionDone = true;
	while (true) {
	    while(!submissionDone) {
		System.out.print("");
	    }
	    for (String g : games) {
		frames.get(g).setVisible(false);
		frames.get(g).setFocusable(false);
	    }
	    chosenGame = (chosenGame + random.nextInt(games.size() - 1) + 1) % games.size();
	    frames.get(games.get(chosenGame)).setVisible(true);
	    frames.get(games.get(chosenGame)).setFocusable(true);
	    frames.get(games.get(chosenGame)).pack();

	    for (int i = 0; i < playLevels; i++) {
		submissionDone = false;
		playedFirst = false;
		playedSecond = false;
		chosenLevel = Runner.random.nextInt(Runner.numLevels);

		do {
		    frames.get(games.get(chosenGame)).setSubmitEnable(playedFirst && playedSecond);

		    mouseClick = RunnerEnum.NONE;
		    frames.get(games.get(chosenGame)).setVisible(true);
		    frames.get(games.get(chosenGame)).setFocusable(true);
		    while (mouseClick == RunnerEnum.NONE) {
			System.out.print("");
		    }
		    switch (mouseClick) {
		    case TUTORIAL:
			for (String g : games) {
			    frames.get(g).setVisible(false);
			}
			playGoodDesignGame();
			playedFirst = true;
			playedSecond = true;
			break;
		    default:
			break;
		    }
		} while (mouseClick != RunnerEnum.SUBMIT);
	    }
	}
    }

    public static int getNumberOfFiles(String filePath, String fileName) {
	File[] files = new File(filePath).listFiles();
	int number = 0;
	if (files == null) {
	    return number;
	}
	for (File f : files) {
	    if (f.getName().contains(fileName)) {
		number += 1;
	    }
	}

	return number;
    }

    public static void playGoodDesignGame() {
	String gameFile = "examples/games/" + games.get(Runner.chosenGame) + ".txt";
	String levelFile = "examples/levels/" + games.get(Runner.chosenGame) + "_lvl" + chosenLevel + ".txt";
	String actionFile = "examples/actions/" + games.get(Runner.chosenGame) + "_lvl" + chosenLevel + ".txt";

	double[] result = ArcadeMachine.playOneGame(gameFile, levelFile, actionFile,
		Runner.random.nextInt(Integer.MAX_VALUE));
	Runner.win = result[0];
	Runner.score = result[1];
	Runner.time = result[2];
    }
}
