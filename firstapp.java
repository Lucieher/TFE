package SudokuSolver;

import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;
import lejos.hardware.Button;
//import lejos.hardware.KeyListener;
//import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.*;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.robotics.RegulatedMotor;
//import lejos.hardware.sensor.SensorMode;

public class firstapp {
	// Definition des constantes
	// Nombre de degres pour avancer d'une case
	static final int CELL_HEIGHT = 533;
	static final int CELL_HEIGHT_Bic = 533;
	static final int CELL_HEIGHT_Scan = 540; // 400-18
	// Nombre de degres pour balayer une case
	// X = hauteur, roue
	// Y = largeur; bic
	static final int CELL_WIDTH = 125;// 440
	static final int CELL_WIDTH_Scan = 112;// 112;//128; //126
	static final int CELL_WIDTH_DECAL = 400;

	// Correction des angles du moteur Y pour le Scan
	static final int SCAN_CELL_Y_C1 = -463;
	static final int SCAN_CELL_Y_C2 = -342;
	static final int SCAN_CELL_Y_C3 = -225;
	static final int SCAN_CELL_Y_C4 = -112;
	static final int SCAN_CELL_Y_C5 = 0;
	static final int SCAN_CELL_Y_C6 = 112;
	static final int SCAN_CELL_Y_C7 = 225;
	static final int SCAN_CELL_Y_C8 = 342;
	static final int SCAN_CELL_Y_C9 = 463;

	// Correction des angles du moteur X pour le Scan
	static final int SCAN_CELL_X_C1 = -500;
	static final int SCAN_CELL_X_C2 = -275;
	static final int SCAN_CELL_X_C3 = -120;
	static final int SCAN_CELL_X_C4 = -30;
	static final int SCAN_CELL_X_C5 = 0;
	static final int SCAN_CELL_X_C6 = -30;
	static final int SCAN_CELL_X_C7 = -120;
	static final int SCAN_CELL_X_C8 = -275;
	static final int SCAN_CELL_X_C9 = -500;

	// Correction des angles du moteur Y pour le bic
	// 38=0.6
	static final int BIC_CELL_Y_C1 = -355;// 362
	static final int BIC_CELL_Y_C2 = -274;// 274
	static final int BIC_CELL_Y_C3 = -190;// 188
	static final int BIC_CELL_Y_C4 = -105;// 103
	static final int BIC_CELL_Y_C5 = -25;// 19
	static final int BIC_CELL_Y_C6 = 50;// 61
	static final int BIC_CELL_Y_C7 = 140;// 145
	static final int BIC_CELL_Y_C8 = 222;// 230
	static final int BIC_CELL_Y_C9 = 305;// 317

	// Correction des angles du moteur X pour le bic
	static final int BIC_CELL_X_C1 = -411;
	static final int BIC_CELL_X_C2 = -236;
	static final int BIC_CELL_X_C3 = -111;
	static final int BIC_CELL_X_C4 = -34;
	static final int BIC_CELL_X_C5 = -1;
	static final int BIC_CELL_X_C6 = -12;
	static final int BIC_CELL_X_C7 = -67;
	static final int BIC_CELL_X_C8 = -167;
	static final int BIC_CELL_X_C9 = -317;

	static final int N = 1;
	static final int NW = 2;
	static final int W = 3;
	static final int SW = 4;
	static final int S = 5;
	static final int SE = 6;
	static final int E = 7;
	static final int NE = 8;

	// largeur et hauteur d'un trai du bic
	static final int LINE_Y = 44;
	static final int LINE_X = 135;

	// 450/25 ==> 18 degre par ligne (440 + 10 pour la ligne du dessus et afin
	// d'avoir des pas entiers)
	static final int SIZE_CASE_ROW = 27;
	// 130/26 = 5 degré par colonne
	static final int SIZE_CASE_COLUMN = 28;// 32;// 14 ok mais pas pour 689. //26
	static final int SIZESCREEN_X = 177; // 178 pixels avec le zero
	static final int SIZESCREEN_Y = 127; // 128 avec le zero
	static final int SUDOKU_SIZE = SIZE_CASE_ROW * SIZE_CASE_COLUMN * 9 * 9;

	// Definition des Outils
	static GraphicsLCD g;
	static RegulatedMotor motorBicDown;
	static RegulatedMotor motorY;

	//positif : vers la droite quand on regarde au dessus du robot
	static RegulatedMotor motorX;
	static EV3ColorSensor sensor;
	static EV3ColorSensor colorSensor;
	static SampleProvider colorProvider;
	//static float[] colorSample;
	private static int sampleSize;

	//Definition des variables globale
	static boolean isBicDown = false;
	static int positionY = 0;
	static float[] bufferCase;
	static float[] bufferRow;
	static int[] roundbuffer;
	static int[] binarybuffer;
	static int[] ScanDegree;

	static int[] bufferScan = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static void main(String[] args) {
		g = BrickFinder.getDefault().getGraphicsLCD();
		motorBicDown = new EV3MediumRegulatedMotor(MotorPort.A);
		motorX = new EV3LargeRegulatedMotor(MotorPort.B);
		motorY = new EV3LargeRegulatedMotor(MotorPort.C);
		sensor = new EV3ColorSensor(SensorPort.S2);
		colorProvider = sensor.getRedMode();
		sampleSize = colorProvider.sampleSize();
		int bicSpeed = motorBicDown.getSpeed();
		motorBicDown.setSpeed(bicSpeed / 3);

		Sound.beepSequenceUp();
		Sound.beepSequenceUp();
		Button.waitForAnyPress();//DEPART
		
		CheckEmpty(); //BufferScan a été maj
		Sound.beepSequenceUp();
		Sound.beepSequenceUp();
		Button.waitForAnyPress();
		
		Scan();
		Sound.beepSequenceUp();
		Sound.beepSequenceUp();
		Button.waitForAnyPress();
		
		ResolveAndWrite(bufferScan);
		Sound.beepSequenceUp();
		Sound.beepSequence();
		Sound.beepSequenceUp();
		Button.waitForAnyPress();

		motorBicDown.close();
		motorX.close();
		motorY.close();
	}

	/*
	 * Resoud le Sudoku et ecrit les réponses dans la grille
	 * 
	 * @pre : prend en paramètre le sudoku non resolu
	 * 
	 * @post : a écrit toutes les réponses dans le sudoku
	 */
	public static void ResolveAndWrite(int[] sudoku) {
		int[] sudokuUnresolved = makeCopy(sudoku);
		Sudoku puzzle = new Sudoku(sudoku);
		puzzle.SolveNew();
		int posi = 0;
		motorX.resetTachoCount();
		MoveToColumnBic(1);
		motorX.rotate(80);
		for (int i = 0; i < 81; i++) {
			if (sudokuUnresolved[i / 9 + (i % 9) * 9] != puzzle.puzzle[i / 9 + (i % 9) * 9]) {
				MoveToWrite(posi, i);
				WriteNumber(puzzle.puzzle[i / 9 + (i % 9) * 9]);
				posi = i;
			}
		}
		moveXY(-motorX.getTachoCount(), -motorY.getTachoCount());
	}

	public static void ResolveAndWriteOneCol(int[] sudoku, int col) {
		int[] sudokuUnresolved = makeCopy(sudoku);
		Sudoku puzzle = new Sudoku(sudoku);
		puzzle.SolveNew();
		int posi = 0;
		motorX.resetTachoCount();
		// MoveToColumnBic(1);

		for (int i = 0; i < 81; i++) {
			if (sudokuUnresolved[i / 9 + (i % 9) * 9] != puzzle.puzzle[i / 9 + (i % 9) * 9] && (i / 9) == (col - 1)) {
				MoveToWrite(posi, i);
				WriteNumber(puzzle.puzzle[i / 9 + (i % 9) * 9]);
				posi = i;
			}
		}
		moveXY(-motorX.getTachoCount(), -motorY.getTachoCount());
	}

	/*
	 * Analyse si les cases d'une grille de Sudoku contiennent un chiffre ou non
	 * 
	 * @pre : -
	 * 
	 * @post : renvoi le buffer int[81] retour avec des -1 là où un chiffre a été
	 * détecté et des 0 dans les cases blanches
	 * 
	 */
	public static void CheckEmpty() {
		float[] valMid = new float[81];
		for (int i = 0; i < 81; i++) {
			valMid[i] = 0f;
		}
		motorX.resetTachoCount();
		motorY.resetTachoCount();
		// commencer a la case du milieu tout en haut

		for (int i = 1; i <= 9; i++) {
			MoveToColumnScan(i); // Etape 1 : On se positionne au début de la colonne
			valMid = CheckEmptyColumn(i, valMid); // Etape 2 : On scan la colonne et enregistre les valeurs obtenue dans
													// le buffer valMid
			moveXY(-motorX.getTachoCount(), -motorY.getTachoCount()); // Etape 3 : retour à la position initiale
		}

		// valmid contient la moyenne des valeurs observées dans les case
		// On effectue une moyenne sur les 81 cases
		// Les cases dont la valeur moyenne est inferieure à la moyenne generale sont
		// conssidérées comme contenant un chiffre (0f == noir)
		float mid = moyFromTab(valMid);
		for (int i = 0; i < 81; i++) {
			if (valMid[i] > mid) {
				bufferScan[(i % 9) * 9 + (i / 9)] = 0; // Case blanche
			} else {
				bufferScan[(i % 9) * 9 + (i / 9)] = -1; // Case contenant un chiffre
			}
		}
		motorX.setSpeed(300);

	}

	/*
	 * Analyse si les cases d'une colonne contiennent un chiffre ou non
	 * 
	 * @pre : column contient le numéro de colonne a analyser (compris entre 1 et 9)
	 * valMid est un buffer de taille 81 contenant les moyennes déja calculées des
	 * cases des colonnes précédentes
	 * 
	 * @post : renvoit le buffer valMid complété avec les moyennes des cases pour la
	 * colonne analysée.
	 * 
	 */
	public static float[] CheckEmptyColumn(int column, float[] valMid) {
		/*
		 * Une case correspond aproximativement à 533 degré du moteur X 11 captures sont
		 * réalisées par cases : après 100 degrés, 130, 160 etc... ScanDegree garde en
		 * mémoire les endroits ou les captures sont effectué
		 */
		int[] ScanDegree = { 100, 130, 160, 190, 220, 250, 280, 310, 340, 370, 400 };
		float[] valTemp = { -1f, -1f, -1f, -1f, -1f, -1f, -1f, -1f, -1f, -1f, -1f };

		motorX.setSpeed(300);
		int done = 0; // indique quand la boucle d'avancement sur une case est terminée
		int tacoCase = 0; // garde en mémoire le nombre de tachocount au début de chaque case
		int taco = 0; /*
						 * garde en mémoire la position actuelle en tachocount c'est en comparant cette
						 * variable au différentes valeurs de ScanDegree que l'on determine quand une
						 * capture doit etre effectuée
						 */
		int count = 0; // compte à quelle capture on en est
		for (int i = 0; i < 9; i++) {
			// au début de chaque case, maj des valeurs pour la nouvelle case
			tacoCase = motorX.getTachoCount();
			done = 0;
			count = 0;
			for (int w = 0; w < ScanDegree.length; w++) {
				valTemp[w] = -1f; // On reinitialise les valeurs de valTemp
			}
			motorX.setSpeed(250); // vitesse réduite pour le scan
			motorX.forward();
			while (done == 0) {
				taco = motorX.getTachoCount();
				if (taco == tacoCase + CELL_HEIGHT || taco > tacoCase + CELL_HEIGHT) {
					motorX.stop();
					// une fois la case finie
					if (minFromTab(valTemp) <= 0f) {
						// dans certain cas, le scan "bug" et detecte des valeurs negative
						// cette condition est là pour eviter ce genre de soucis
						count = 0;
						for (int w = 0; w < ScanDegree.length; w++) {
							valTemp[w] = -1f;
						}
						// On reinitialise les valeurs et le robot revient en arrière afin de rescanner
						// la case
						motorX.rotate(tacoCase - motorX.getTachoCount());
						motorX.forward();
					} else {
						done = 1;
					}
				}
				if (count < ScanDegree.length) {
					if (taco == tacoCase + ScanDegree[count]) {
						valTemp[count] = getSample()[0];
						count = count + 1;
					}
				}
			}
			// fin de la case
			/**
			 * System.out.println(column-1+" , "+i+" : "+valTemp[0]+" "+valTemp[1]+"
			 * "+valTemp[2]+" "+valTemp[3]+" "+valTemp[4]+" "+valTemp[5]+" "+valTemp[6]+"
			 * "+valTemp[7]+" "+valTemp[8]);
			 **/
			valMid[(column - 1) * 9 + i] = moyFromTab(valTemp);
			// On enregistre la moyenne des valeurs qui ont été observées
		}
		/**
		 * int c = column-1; System.out.println("colonne "+c+" : "+valMid[c*9]+"
		 * "+valMid[c*9+1]+" "+valMid[c*9+2]+" "+valMid[c*9+3]+" "+valMid[c*9+4]+"
		 * "+valMid[c*9+5]+" "+valMid[c*9+6]+" "+valMid[c*9+7]+" "+valMid[c*9+8]+" ");
		 **/
		motorX.setSpeed(400);
		return valMid;
	}

	/*
	 * Deplace le robot de manière à pouvoir scanner la colonne donnée en paramètre
	 * 
	 * @pre : col est lenuméro de la colonne a scanner (compris entre 1 et 9)
	 * 
	 * @post : -
	 */
	public static void MoveToColumnScan(int col) {
		if (col == 1)
			moveXY(SCAN_CELL_X_C1, SCAN_CELL_Y_C1);
		if (col == 2)
			moveXY(SCAN_CELL_X_C2, SCAN_CELL_Y_C2);
		if (col == 3)
			moveXY(SCAN_CELL_X_C3, SCAN_CELL_Y_C3);
		if (col == 4)
			moveXY(SCAN_CELL_X_C4, SCAN_CELL_Y_C4);
		if (col == 6)
			moveXY(SCAN_CELL_X_C6, SCAN_CELL_Y_C6);
		if (col == 7)
			moveXY(SCAN_CELL_X_C7, SCAN_CELL_Y_C7);
		if (col == 8)
			moveXY(SCAN_CELL_X_C8, SCAN_CELL_Y_C8);
		if (col == 9)
			moveXY(SCAN_CELL_X_C9, SCAN_CELL_Y_C9);
	}

	/*
	 * Deplace le robot de manière à pouvoir ecrire dans la colonne donnée en
	 * paramètre
	 * 
	 * @pre : col est le numéro de la colonne où l'on veux écrire (compris entre 1
	 * et 9)
	 * 
	 * @post : -
	 */
	public static void MoveToColumnBic(int col) {
		if (col == 1)
			moveXY(BIC_CELL_X_C1, BIC_CELL_Y_C1);
		if (col == 2)
			moveXY(BIC_CELL_X_C2, BIC_CELL_Y_C2);
		if (col == 3)
			moveXY(BIC_CELL_X_C3, BIC_CELL_Y_C3);
		if (col == 4)
			moveXY(BIC_CELL_X_C4, BIC_CELL_Y_C4);
		if (col == 5)
			moveXY(BIC_CELL_X_C5, BIC_CELL_Y_C5);
		if (col == 6)
			moveXY(BIC_CELL_X_C6, BIC_CELL_Y_C6);
		if (col == 7)
			moveXY(BIC_CELL_X_C7, BIC_CELL_Y_C7);
		if (col == 8)
			moveXY(BIC_CELL_X_C8, BIC_CELL_Y_C8);
		if (col == 9)
			moveXY(BIC_CELL_X_C9, BIC_CELL_Y_C9);
	}

	/*
	 * Deplace le robot pour ecrire dans la case suivante. Si changement de colonne,
	 * il y a d'abord calibration par rapport à la position initiale puis
	 * déplacement dans la bonne colonne
	 * 
	 * @pre : previous est la position ou le robot est situé maintenant. next est la
	 * position où l'on veux aller
	 */
	public static void MoveToWrite(int previous, int next) {
		System.out.println("Ecriture en ligne " + next % 9 + " et en colonne " + next / 9);
		if (next / 9 != previous / 9) {
			moveXY(-motorX.getTachoCount(), -motorY.getTachoCount());
			MoveToColumnBic((next / 9) + 1);
			Sound.beepSequenceUp();
			motorX.rotate((next % 9) * CELL_HEIGHT_Bic + 60);
			// 80 = 2mm
			// 120 = 3mm
		} else {
			if (next % 9 != previous % 9) {
				motorX.rotate(((next % 9) - (previous % 9)) * CELL_HEIGHT_Bic);
			}
		}
	}

	/*
	 * Affiche le sudoku en parametre sur l'ecran
	 * 
	 * @pre : prend le sudoku en parametre
	 * 
	 * @post : affiche le sudoku, si un zero est en une case, il l'affiche vide, et
	 * renvoi un carré si la case contien une valeur negative
	 */
	public static void printSudoku(int[] sudoku) {
		g.clear();
		g.refresh();
		Button.waitForAnyPress();
		g.setFont(Font.getSmallFont());
		for (int i = 0; i < 9; i++) {
			g.drawRect(i * 14, 0, i * 14 + 14, 126);
			g.drawRect(0, i * 14, 126, i * 14 + 14);
			for (int j = 0; j < 9; j++) {
				if (sudoku[i * 9 + j] > 0) {
					g.drawString(String.valueOf(sudoku[i * 9 + j]), j * 14 + 7, i * 14 + 4, GraphicsLCD.HCENTER);
				} else if (sudoku[i * 9 + j] < 0) {
					g.drawRect(j * 14 + 4, i * 14 + 4, 6, 6);
				}
			}
		}
	}

	public static void MovePen() {
		int rotation = 40;
		if (isBicDown) {
			motorBicDown.rotate(rotation);
			isBicDown = false;
		} else {
			motorBicDown.rotate(-rotation);
			isBicDown = true;
		}

	}

	public static void WriteDir(int[] dir, int[] size) {
		// dir = 1 UP
		// dir = 2 DOWN
		// dir = 3 LEFT
		// dir = 4 RIGHT
		// size = 1 SHORT
		// size = 2 = LONG
		int X = 0;
		int Y = 0;
		int countX = motorX.getTachoCount();
		int countY = motorY.getTachoCount();
		MovePen();
		// motorY.resetTachoCount();
		// motorX.resetTachoCount();
		for (int i = 0; i < dir.length; i++) {
			if (dir[i] == 1) {
				X = -LINE_X;
				Y = 0;
				System.out.println("UP");
			}
			if (dir[i] == 2) {
				X = LINE_X;
				Y = 0;
				System.out.println("DOWN");
			}
			if (dir[i] == 3) {
				X = 0;
				Y = -LINE_Y;
				System.out.println("LEFT");
			}
			if (dir[i] == 4) {
				X = 0;
				Y = LINE_Y;
				System.out.println("RIGHT");
			}
			if (size[i] != 1) {
				X = 2 * X;
				Y = 2 * Y;
			}
			// 135 = 5*3*3*3 - 108
			// 36 = 3*2*2*3 - 29
			// 4mm en moteur X : 160

			// 4mm en moteur Y : 25
			System.out.println("X : " + X + ", Y : " + Y);
			moveXY(X, Y);

			// countX=countX+X;
			// countY=countY+Y;
		}
		MovePen();
		motorX.rotate(-motorX.getTachoCount() + countX);
		motorY.rotate(-motorY.getTachoCount() + countY);

	}

	public static void WriteNumber(int number) {
		if (number == 1) {
			System.out.println("Chiffre : " + 1);
			motorY.rotate(LINE_Y / 2);
			int[] dir = { 2 }; // Down
			int[] size = { 2 }; // Long
			WriteDir(dir, size);
			motorY.rotate(-LINE_Y / 2);
		}
		if (number == 2) {
			System.out.println("Chiffre : " + 2);
			int[] dir = { 4, 2, 3, 2, 4 }; // Right, Down, Left, Down, Right
			int[] size = { 1, 1, 1, 1, 1 }; // short, short, short, short, short
			WriteDir(dir, size);
		}
		if (number == 3) {
			System.out.println("Chiffre : " + 3);
			int[] dir = { 4, 2, 3, 4, 2, 3 }; // Right, Down, Left, Right, Down, Left
			int[] size = { 1, 1, 1, 1, 1, 1 }; // short, short, short, short, short, short
			WriteDir(dir, size);
		}
		if (number == 4) {
			System.out.println("Chiffre : " + 4);
			int[] dir = { 2, 4, 1, 2 }; // Down, Right, Up, Down
			int[] size = { 1, 1, 1, 2 }; // Short, short short, long
			WriteDir(dir, size);
		}
		if (number == 5) {
			System.out.println("Chiffre : " + 5);
			motorY.rotate(LINE_Y);
			int[] dir = { 3, 2, 4, 2, 3 }; // Left, Down, Right, Down, Left
			int[] size = { 1, 1, 1, 1, 1 }; // short, short, short, short, short
			WriteDir(dir, size);
			motorY.rotate(-LINE_Y);
		}
		if (number == 6) {
			System.out.println("Chiffre : " + 6);
			motorY.rotate(LINE_Y);
			int[] dir = { 3, 2, 4, 1, 3 }; // Left, Down, Right, Up, Left
			int[] size = { 1, 2, 1, 1, 1 }; // Short, Long, short, short, short
			WriteDir(dir, size);
			motorY.rotate(-LINE_Y);
		}
		if (number == 7) {
			System.out.println("Chiffre : " + 7);
			int[] dir = { 4, 2 }; // Right, Down
			int[] size = { 1, 2 };// Short, Long
			WriteDir(dir, size);
		}
		if (number == 8) {
			System.out.println("Chiffre : " + 8);
			int[] dir = { 2, 4, 1, 3, 2, 4 }; // Down, Right, Up, Left, Down, Right
			int[] size = { 2, 1, 2, 1, 1, 1 }; // Long, Short, Long, Short, short, short
			WriteDir(dir, size);
		}
		if (number == 9) {
			System.out.println("Chiffre : " + 9);
			int[] dir = { 2, 4, 1, 3, 4, 2, 3 }; // Down, Right, Up, Left, Right,Down,Left
			int[] size = { 1, 1, 1, 1, 1, 2, 1 }; // Short,short,short,short,short,Long,short
			WriteDir(dir, size);
		}

	}

	/*
	 * Imprime sur l'écran du robot en position row, la ligne passée en paramètre le
	 * résultat a déja été thresholder, ce qui signifie que les pixels sont noir ou
	 * blanc (pas de différents niveau de gris
	 *
	 * @pre : row, entre 0 et SIZE_CASE_ROW bufferRow, contient le buffer de la
	 * ligne a imprimer
	 * 
	 * @post : -
	 */
	public static void LCDprintRow(int row, float[] bufferRow) {
		float highest = 0;
		float lowest = 1;
		for (int i = 0; i < SIZE_CASE_COLUMN; i++) {
			if (bufferRow[i] > highest && bufferRow[i] < 1) {
				highest = bufferRow[i];
			}
			if (bufferRow[i] < lowest && bufferRow[i] > 0) {
				lowest = bufferRow[i];
			}
		}
		float mid = (highest + lowest) / 2;
		int r = 3;
		for (int i = 0; i < SIZE_CASE_COLUMN; i++) {
			if (bufferRow[i] < mid) {
				g.drawRect(120 - (row * r), 100 - (i * r), r, r);
				g.drawRect(120 - (row * r), 100 - (i * r), r - 1, r - 1);
			}
		}
	}

	/*
	 * Imprime la case du buffer donné en paramètre
	 *
	 * @pre : bufferCase, contient le buffer de la case a imprimmer
	 * 
	 * @post : -
	 */
	public static void LCDprintCase(float[] bufferCase) {
		float highest = 0;
		float lowest = 1;
		for (int i = 0; i < SIZE_CASE_COLUMN * SIZE_CASE_ROW; i++) {
			if (bufferCase[i] > highest && bufferCase[i] < 1) {
				highest = bufferCase[i];
			}
			if (bufferCase[i] < lowest && bufferCase[i] > 0) {
				lowest = bufferCase[i];
			}
		}
		float mid = (highest + lowest) / 2;
		int r = 3;
		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			for (int j = 0; j < SIZE_CASE_COLUMN; j++) {
				if (bufferCase[i * SIZE_CASE_COLUMN + j] < mid) {
					g.drawRect(120 - (i * r), 100 - (j * r), r, r);
					g.drawRect(120 - (i * r), 100 - (j * r), r - 1, r - 1);
				}
			}
		}
	}

	/*
	 * Prend un certain nombre d'echantillon de la caméra
	 *
	 * @pre : -
	 * 
	 * @post : renvoi un tableau de float contenant sampleSize échantillons
	 */
	private static float[] getSample() {
		// Initializes the array for holding samples
		float[] sample = new float[sampleSize];

		// Gets the sample an returns it
		colorProvider.fetchSample(sample, 0);
		return sample;
	}

	public static void LCDprintCaseBinary() {
		g.clear();
		// g.drawRect(22, 43, 98, 77);
		g.drawRect(43, 22, 77, 98);
		// g.drawRect(19, 15, 102, 106);
		int r = 3;
		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			for (int j = 0; j < SIZE_CASE_COLUMN; j++) {
				if (binarybuffer[i * SIZE_CASE_COLUMN + j] == 1) {
					g.fillRect(117 - (i * r), 117 - (j * r), r, r);
				}
			}
		}
	}

	public static void Scan() {
		for (int i = 0; i < 9; i++) {
			MoveToColumnScan(i + 1);
			for (int j = 0; j < 9; j++) {
				motorX.rotate(CELL_HEIGHT);
				if (bufferScan[i * 9 + j] == -1) {
					bufferScan[i * 9 + j] = ScanCell();
				}
			}
			moveXY(-motorX.getTachoCount(), -motorY.getTachoCount());
		}
	}

	public static int ScanCell() {
		g.refresh();
		g.clear();

		bufferCase = new float[SIZE_CASE_ROW * SIZE_CASE_COLUMN];
		binarybuffer = new int[SIZE_CASE_ROW * SIZE_CASE_COLUMN];

		int number = 0;

		motorY.setSpeed(100);
		motorY.rotate(-CELL_WIDTH_Scan / 2);

		int tacoY = motorY.getTachoCount();

		ScanDegree = new int[SIZE_CASE_COLUMN];
		for (int i = 0; i < SIZE_CASE_COLUMN; i++) {
			ScanDegree[i] = (CELL_WIDTH_Scan / SIZE_CASE_COLUMN) / 2 + CELL_WIDTH_Scan / SIZE_CASE_COLUMN * i + tacoY;
		}

		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			int stop = 0;
			int count = 0;
			int tacoCount = tacoY;

			motorY.setSpeed(30);
			motorY.forward();
			while (stop == 0) {
				tacoCount = motorY.getTachoCount();
				if (tacoCount == CELL_WIDTH_Scan + tacoY || tacoCount > CELL_WIDTH_Scan + tacoY) { // AJOUTTER UNE
																									// CONDITION POUR
																									// PAS QUE CA BUG ?
					stop = 1;
					motorY.stop();
				}
				if (count < SIZE_CASE_COLUMN) {
					if (tacoCount == ScanDegree[count]) {
						bufferCase[(SIZE_CASE_COLUMN * i) + count] = getSample()[0];
						count = count + 1;
					}
				}
			}

			// motorY.rotate(-motorY.getTachoCount());
			motorY.setSpeed(250);
			motorY.rotate(-(motorY.getTachoCount() - tacoY));
			motorX.rotate(-CELL_HEIGHT_Scan / SIZE_CASE_ROW);
		}

		g.refresh();
		g.clear();

		LCDprintCase(bufferCase);

		motorY.rotate(CELL_WIDTH_Scan / 2);

		System.out.println("Intensity");

		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			System.out.println();
			for (int j = 0; j < SIZE_CASE_COLUMN; j++) {
				System.out.print(bufferCase[(SIZE_CASE_ROW - 1 - i) * SIZE_CASE_COLUMN + j] + ",");
			}
		}

		thresholdCase();
		LCDprintCaseBinary();
		g.drawString("Thresh", 40, 0, 20);

		System.out.println();
		System.out.println("Threshold");
		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			System.out.println();
			for (int j = 0; j < SIZE_CASE_COLUMN; j++) {
				System.out.print(binarybuffer[(SIZE_CASE_ROW - 1 - i) * SIZE_CASE_COLUMN + j] + ",");
			}
		}

		segmentationCase();
		g.clear();
		LCDprintCaseBinary();
		g.drawString("Segmentation", 10, 0, 20);

		System.out.println();
		System.out.println("Segmentation");
		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			System.out.println();
			for (int j = 0; j < SIZE_CASE_COLUMN; j++) {
				System.out.print(binarybuffer[(SIZE_CASE_ROW - 1 - i) * SIZE_CASE_COLUMN + j] + ",");
			}
		}

		thinningCase();
		g.clear();
		LCDprintCaseBinary();
		g.drawString("Thinning", 27, 0, 20);

		System.out.println();
		System.out.println("Thinning");
		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			System.out.println();
			for (int j = 0; j < SIZE_CASE_COLUMN; j++) {
				System.out.print(binarybuffer[(SIZE_CASE_ROW - 1 - i) * SIZE_CASE_COLUMN + j] + ",");
			}
		}

		number = RecognizeNumber();

		motorX.rotate(CELL_HEIGHT_Scan);
		return number;
	}

	public static void thresholdCase() {
		int threshold = 0;
		int nW = 1000; // Nombre de pixels blancs
		long sW; // Somme des intensité des pixels blanc
		int nB = 0; // Nombre de pixels noirs
		long sB; // Somme des intensité des pixels blanc
		long uW; // Moyenne d'intensité des pixels blanc (sW/nW)
		long uB; // Moyenne d'intensité des pixels noirs (sB/nB)
		long BetweenClassVariance;
		long max = 0;
		int darkest = 1000;
		roundbuffer = new int[SIZE_CASE_ROW * SIZE_CASE_COLUMN];
		for (int i = 0; i < SIZE_CASE_COLUMN * SIZE_CASE_ROW; i++) {
			roundbuffer[i] = Math.round(bufferCase[i] * 1000);
		}
		for (int i = 0; i < SIZE_CASE_COLUMN * SIZE_CASE_ROW; i++) {
			if (roundbuffer[i] < darkest)
				darkest = roundbuffer[i];
		}
		for (int d = darkest + 1; nW > 3 * nB; d++) {
			nW = 0;
			sW = 0;
			nB = 0;
			sB = 0;
			for (int i = 0; i < SIZE_CASE_COLUMN * SIZE_CASE_ROW; i++) {
				if (roundbuffer[i] < d) {
					nB++;
					sB += roundbuffer[i];
				} else {
					nW++;
					sW += roundbuffer[i];
				}
			}
			if (nW != 0)
				uW = sW / nW;
			else
				uW = 0;
			if (nB != 0)
				uB = sB / nB;
			else
				uB = 0;
			BetweenClassVariance = ((uW - uB) ^ 2) * nB * nW;
			if (BetweenClassVariance > max) {
				max = BetweenClassVariance;
				threshold = d;
			}
		}
		for (int i = 0; i < SIZE_CASE_COLUMN * SIZE_CASE_ROW; i++) {
			if (roundbuffer[i] < threshold) {
				binarybuffer[i] = 1;
				// check if peak (holes in 6,8,9 maybe not recognized by threshold)
				for (int j = i - 1; j > 0; j--) {
					if (roundbuffer[j] > roundbuffer[i])
						break;
					// Si le pixel de gauche est plus sombre d'au moins 2 d'intensité
					else if (roundbuffer[i] - roundbuffer[j] > 2) {
						// On parcourt vers la droite pour voir si on va rencontrer des pixel plus
						// sombre à nouveau (dans ce cas là, on ne doit pas le prendre)
						for (int k = i + 1; k < SIZE_CASE_COLUMN * SIZE_CASE_ROW; k++) {

							if (roundbuffer[k] > roundbuffer[i])
								break;
							// Si le pixel à droite est plus sombre d'au moins 2 d'intensité
							else if (roundbuffer[i] - roundbuffer[k] > 2) {
								binarybuffer[i] = 0;
								break;
							}
						}
					}
				}
			} else
				binarybuffer[i] = 0;
		}

		// le contour doit être blanc
		for (int i = 0; i < SIZE_CASE_COLUMN; i++) {
			// Bas de la case
			binarybuffer[i] = 0;
			// Haut de la case
			binarybuffer[SIZE_CASE_COLUMN * SIZE_CASE_ROW - 1 - i] = 0;
		}
		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			// Gauche de la case
			binarybuffer[i * SIZE_CASE_COLUMN] = 0;
			// Droite de la case
			binarybuffer[(i + 1) * SIZE_CASE_COLUMN - 1] = 0;
		}
	}

	/*
	 * Segmente le buffer d'une case (etape 1)
	 * 
	 * @pre : bufferCase, contient le buffer de la case a imprimmer
	 * 
	 * @post : renvoi le buffer updater après segmentation
	 */
	private static void segmentationCase() {
		int noir = 1;
		int blanc = 0;
		int mid = 0;
		// if((SIZE_CASE_ROW % 2) == 0){
		mid = (SIZE_CASE_ROW * SIZE_CASE_COLUMN) / 2;
		// }
		// else {
		// mid = (SIZE_CASE_ROW * SIZE_CASE_COLUMN) / 2 + SIZE_CASE_COLUMN/2 ;
		int base = mid;

		// Etape 1 : trouver le pixel noir le plus proche du centre
		for (int i = 0; i < mid; i++) {
			base = mid + i;
			if (binarybuffer[base] == noir) {
				break;
			}
			base = mid - i;
			if (binarybuffer[base] == noir) {
				break;
			}
		}
		// a ce stade, base contient le pixel noir le plus au centre de la case

		// Etape 2 : ajouter au buffer le Chiffre, tout les éléments du chiffre detecté
		boolean modification = true;
		binarybuffer[base] = -1;
		while (modification) {
			modification = false;
			for (int i = SIZE_CASE_COLUMN + 1; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN - SIZE_CASE_COLUMN - 1; i++) {
				if (binarybuffer[i] == -noir) {
					if (binarybuffer[i - SIZE_CASE_COLUMN] == noir) {
						modification = true;
						binarybuffer[i - SIZE_CASE_COLUMN] = -noir;
					}
					if (binarybuffer[i - SIZE_CASE_COLUMN + 1] == noir) {
						modification = true;
						binarybuffer[i - SIZE_CASE_COLUMN + 1] = -noir;
					}
					if (binarybuffer[i + 1] == noir) {
						modification = true;
						binarybuffer[i + 1] = -noir;
					}
					if (binarybuffer[i + SIZE_CASE_COLUMN + 1] == noir) {
						modification = true;
						binarybuffer[i + SIZE_CASE_COLUMN + 1] = -noir;
					}
					if (binarybuffer[i + SIZE_CASE_COLUMN] == noir) {
						modification = true;
						binarybuffer[i + SIZE_CASE_COLUMN] = -noir;
					}
					if (binarybuffer[i + SIZE_CASE_COLUMN - 1] == noir) {
						modification = true;
						binarybuffer[i + SIZE_CASE_COLUMN - 1] = -noir;
					}
					if (binarybuffer[i - 1] == noir) {
						modification = true;
						binarybuffer[i - 1] = -noir;
					}
					if (binarybuffer[i - SIZE_CASE_COLUMN - 1] == noir) {
						modification = true;
						binarybuffer[i - SIZE_CASE_COLUMN - 1] = -noir;
					}
				}
			}
		}
		// à ce stade, tout les pixels du chiffre détecté ont été assigné de la valeur
		// -1

		// Etape 3 : effacer tout les pixels qui ne sont pas dans le buffer chiffre
		// les pixels du chiffre ont tous
		for (int i = 0; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN; i++) {
			if (binarybuffer[i] == -noir) {
				binarybuffer[i] = noir;
			} else {
				binarybuffer[i] = blanc;
			}
		}
	}

	/*
	 * Affine le buffer d'une case (etape 2)
	 * 
	 * pre : bufferCase, contient le buffer de la case a imprimmer post : renvoi le
	 * buffer updater après affinement
	 */
	private static void thinningCase() {
		int noir = 1;
		int blanc = 0;
		int np;
		int sp;
		int done = 0;
		int surround[];
		// Etape 1-1 :
		for (int i = SIZE_CASE_COLUMN + 1; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN - SIZE_CASE_COLUMN - 1; i++) {
			if (binarybuffer[i] == noir) {
				np = 0; // number of non-zero neighbors
				sp = 0;
				surround = new int[8];
				surround[0] = binarybuffer[i - SIZE_CASE_COLUMN];
				surround[1] = binarybuffer[i - SIZE_CASE_COLUMN + 1];
				surround[2] = binarybuffer[i + 1];
				surround[3] = binarybuffer[i + SIZE_CASE_COLUMN + 1];
				surround[4] = binarybuffer[i + SIZE_CASE_COLUMN];
				surround[5] = binarybuffer[i + SIZE_CASE_COLUMN - 1];
				surround[6] = binarybuffer[i - 1];
				surround[7] = binarybuffer[i - SIZE_CASE_COLUMN - 1];

				for (int m = 0; m < 8; m++) {
					// Est ce qu'on change de 0 à 1
					if (m == 7) {
						if (surround[m] == 0 && Math.abs(surround[0]) == 1)
							sp++;
					} else {
						if (surround[m] == 0 && Math.abs(surround[m + 1]) == 1)
							sp++;
					}
					np += Math.abs(surround[m]);
				}
				if (np >= 5 && np < 6 && sp == 1 && (surround[0] * surround[2] * surround[4]) == 0
						&& (surround[2] * surround[4] * surround[6]) == 0 && surround[2] == 1 && surround[6] == 1) {
					binarybuffer[i] = -noir;
				}
			}
		}
		// Etape 1-2 :
		for (int i = 0; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN; i++) {
			if (binarybuffer[i] == -noir) {
				binarybuffer[i] = blanc;
			}
		}
		// Etape 2-1 :
		for (int i = SIZE_CASE_COLUMN + 1; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN - SIZE_CASE_COLUMN - 1; i++) {
			if (binarybuffer[i] == noir) {
				np = 0; // number of non-zero neighbors
				sp = 0;
				surround = new int[8];
				surround[0] = binarybuffer[i - SIZE_CASE_COLUMN];
				surround[1] = binarybuffer[i - SIZE_CASE_COLUMN + 1];
				surround[2] = binarybuffer[i + 1];
				surround[3] = binarybuffer[i + SIZE_CASE_COLUMN + 1];
				surround[4] = binarybuffer[i + SIZE_CASE_COLUMN];
				surround[5] = binarybuffer[i + SIZE_CASE_COLUMN - 1];
				surround[6] = binarybuffer[i - 1];
				surround[7] = binarybuffer[i - SIZE_CASE_COLUMN - 1];

				for (int m = 0; m < 8; m++) {
					// Est ce qu'on change de 0 à 1
					if (m == 7) {
						if (surround[m] == 0 && Math.abs(surround[0]) == 1)
							sp++;
					} else {
						if (surround[m] == 0 && Math.abs(surround[m + 1]) == 1)
							sp++;
					}
					np += Math.abs(surround[m]);
				}
				if (np >= 5 && np < 6 && sp == 1 && (surround[0] * surround[2] * surround[6]) == 0
						&& (surround[0] * surround[4] * surround[6]) == 0 && surround[2] == 1 && surround[6] == 1) {
					binarybuffer[i] = -noir;
				}
			}
		}

		// Etape 2-2 : supprimer tout les points marqué

		for (int i = 0; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN; i++) {
			if (binarybuffer[i] == -noir) {
				binarybuffer[i] = blanc;
			}
		}
		// Etape 3-1 :
		while (done == 0) {
			done = 1;
			for (int i = SIZE_CASE_COLUMN + 1; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN - SIZE_CASE_COLUMN - 1; i++) {
				if (binarybuffer[i] == noir) {
					np = 0; // number of non-zero neighbors
					sp = 0;
					surround = new int[8];
					surround[0] = binarybuffer[i - SIZE_CASE_COLUMN];
					surround[1] = binarybuffer[i - SIZE_CASE_COLUMN + 1];
					surround[2] = binarybuffer[i + 1];
					surround[3] = binarybuffer[i + SIZE_CASE_COLUMN + 1];
					surround[4] = binarybuffer[i + SIZE_CASE_COLUMN];
					surround[5] = binarybuffer[i + SIZE_CASE_COLUMN - 1];
					surround[6] = binarybuffer[i - 1];
					surround[7] = binarybuffer[i - SIZE_CASE_COLUMN - 1];

					for (int m = 0; m < 8; m++) {
						// Est ce qu'on change de 0 à 1
						if (m == 7) {
							if (surround[m] == 0 && Math.abs(surround[0]) == 1)
								sp++;
						} else {
							if (surround[m] == 0 && Math.abs(surround[m + 1]) == 1)
								sp++;
						}
						np += Math.abs(surround[m]);
					}
					// if (np >= 2 && np <= 6 && sp == 1 && (surround[0] * surround[2] *
					// surround[4]) == 0
					// && (surround[0] * surround[2] * surround[6]) == 0 && surround[7] != 0) {
					// if (np >= 2 && np <= 6 && sp == 1 && (surround[2] * surround[4] *
					// surround[6]) == 0 //VL
					// && (surround[0] * surround[2] * surround[6]) == 0 && surround[7] != 0) {
					if (np >= 2 && np <= 6 && sp == 1 && (surround[0] * surround[2] * surround[4]) == 0
							&& (surround[2] * surround[4] * surround[6]) == 0) {
						binarybuffer[i] = -noir;
					}
				}
			}

			// Etape 3-2 : supprimer les points marqué

			for (int i = 0; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN; i++) {
				if (binarybuffer[i] == -noir) {
					done = 0;
					binarybuffer[i] = blanc;
				}
			}

			// Etape 4-1 : suprimer tous les points supprimable dans les limites N, W, SE,
			// SW
			for (int i = SIZE_CASE_COLUMN + 1; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN - SIZE_CASE_COLUMN - 1; i++) {
				if (binarybuffer[i] == noir) {
					np = 0; // number of non-zero neighbors
					sp = 0;
					surround = new int[8];
					surround[0] = binarybuffer[i - SIZE_CASE_COLUMN];
					surround[1] = binarybuffer[i - SIZE_CASE_COLUMN + 1];
					surround[2] = binarybuffer[i + 1];
					surround[3] = binarybuffer[i + SIZE_CASE_COLUMN + 1];
					surround[4] = binarybuffer[i + SIZE_CASE_COLUMN];
					surround[5] = binarybuffer[i + SIZE_CASE_COLUMN - 1];
					surround[6] = binarybuffer[i - 1];
					surround[7] = binarybuffer[i - SIZE_CASE_COLUMN - 1];

					for (int m = 0; m < 8; m++) {
						// Est ce qu'on change de 0 à 1
						if (m == 7) {
							if (surround[m] == 0 && Math.abs(surround[0]) == 1)
								sp++;
						} else {
							if (surround[m] == 0 && Math.abs(surround[m + 1]) == 1)
								sp++;
						}
						np += Math.abs(surround[m]);
					}
					// if (np >= 2 && np <= 6 && sp == 1 && (surround[4] * surround[2] *
					// surround[6]) == 0
					// && (surround[0] * surround[4] * surround[6]) == 0 && surround[3] != 0) {
					// if (np >= 2 && np <= 6 && sp == 1 && (surround[0] * surround[2] *
					// surround[6]) == 0
					// && (surround[0] * surround[4] * surround[6]) == 0 && surround[7] != 0) {
					if (np >= 2 && np <= 6 && sp == 1 && (surround[0] * surround[2] * surround[6]) == 0
							&& (surround[0] * surround[4] * surround[6]) == 0) {
						binarybuffer[i] = -noir;
					}
				}
			}

			// Etape 4, partie 2 : supprimer tout les points marqué

			for (int i = 0; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN; i++) {
				if (binarybuffer[i] == -noir) {
					done = 0;
					binarybuffer[i] = blanc;
				}
			}
		}

		System.out.println();
		System.out.println("Thinning");
		for (int i = 0; i < SIZE_CASE_ROW; i++) {
			System.out.println();
			for (int j = 0; j < SIZE_CASE_COLUMN; j++) {
				System.out.print(binarybuffer[(SIZE_CASE_ROW - 1 - i) * SIZE_CASE_COLUMN + j] + ",");
			}
		}

		for (int i = SIZE_CASE_COLUMN + 1; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN - SIZE_CASE_COLUMN - 1; i++) {
			if (binarybuffer[i] == 1) {
				np = 0; // number of non-zero neighbors
				sp = 0;
				surround = new int[8];
				surround[0] = binarybuffer[i - SIZE_CASE_COLUMN];
				surround[1] = binarybuffer[i - SIZE_CASE_COLUMN + 1];
				surround[2] = binarybuffer[i + 1];
				surround[3] = binarybuffer[i + SIZE_CASE_COLUMN + 1];
				surround[4] = binarybuffer[i + SIZE_CASE_COLUMN];
				surround[5] = binarybuffer[i + SIZE_CASE_COLUMN - 1];
				surround[6] = binarybuffer[i - 1];
				surround[7] = binarybuffer[i - SIZE_CASE_COLUMN - 1];

				for (int m = 0; m < 8; m++) {
					np += Math.abs(surround[m]);
				}
				if (surround[0] == 0 && surround[1] == 0 && surround[2] == 1)
					sp++;
				if (surround[2] == 0 && surround[3] == 0 && surround[4] == 1)
					sp++;
				if (surround[4] == 0 && surround[5] == 0 && surround[6] == 1)
					sp++;
				if (surround[6] == 0 && surround[7] == 0 && surround[0] == 1)
					sp++;
				if (surround[0] == 0 && surround[1] == 1)
					sp++;
				if (surround[2] == 0 && surround[3] == 1)
					sp++;
				if (surround[4] == 0 && surround[5] == 1)
					sp++;
				if (surround[6] == 0 && surround[7] == 1)
					sp++;
				if (np >= 2 && sp == 1) {
					binarybuffer[i] = 0;
				}
			}
		}
	}

	private static int RecognizeNumber() {
		int top = SIZE_CASE_ROW - 1;
		int bot = 0;
		int left = SIZE_CASE_COLUMN;
		int right = 0;

		int number = 0;

		for (int i = 0; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN; i++) {
			if (binarybuffer[i] == 1) {
				if (bot == 0)
					bot = i;
				top = i;
				if (i % SIZE_CASE_COLUMN < left)
					left = i % SIZE_CASE_COLUMN;
				if (i % SIZE_CASE_COLUMN > right)
					right = i % SIZE_CASE_COLUMN;
			}
		}

		int topRow = top / SIZE_CASE_COLUMN;
		int botRow = bot / SIZE_CASE_COLUMN;

		int width = right - left;

		int segmentLeftCount = 0;
		int segmentTop = 0;
		int segmentBot = 0;

		int botOrientation = 0;
		int topOrientation = 0;

		int[] segment = new int[2];

		for (int i = 0; i < SIZE_CASE_ROW * SIZE_CASE_COLUMN; i++) {
			if (binarybuffer[i] == 1) {
				segment = segment(i);
				if (segment[1] != 0) {
					System.out.println(segment[0] + "   " + segment[1]);
					if (segment[0] >= 7) {
						if (botOrientation == 0)
							botOrientation = segment[1];
						else
							topOrientation = segment[1];
					}
					// Usefull to detect the 3
					if (segment[1] == W || segment[1] == NW || segment[1] == SW)
						segmentLeftCount++;
					if (i / SIZE_CASE_COLUMN == botRow) {
						segmentBot = segment[1];
						System.out.println(segment[0] + "   " + segment[1]);
					}
					if (i / SIZE_CASE_COLUMN == topRow) {
						segmentTop = segment[1];
						System.out.println(segment[0] + "   " + segment[1]);
					}
				}
			}
		}
		if (width < 5)
			number = 1;
		else if ((botOrientation == E || botOrientation == NE || botOrientation == SE)
				&& (topOrientation == W || topOrientation == SW || topOrientation == NW))
			number = 2;
		else if ((topOrientation == E || topOrientation == NE || topOrientation == SE)
				&& (botOrientation == W || botOrientation == SW || botOrientation == NW))
			number = 5;
		// Première partie si la barre du milieu est bien détectée. Deuxième partie si
		// elle ne l'est pas (C à l'envers)
		else if ((segmentLeftCount == 3))
			number = 3;
		else if ((topOrientation == W || topOrientation == NW || topOrientation == SW)
				&& (botOrientation == S || botOrientation == SW || botOrientation == W))
			number = 7;
		else if (segmentBot == W || segmentBot == NW || segmentBot == SW || botOrientation == W || botOrientation == NW
				|| botOrientation == SW)
			number = 9;
		else if (segmentTop == E || segmentTop == NE || segmentTop == SE || botOrientation == E || botOrientation == NE
				|| botOrientation == SE)
			number = 6;
		else if ((segmentBot == S))
			number = 4;
		else
			number = 8;

		System.out.println();
		System.out.println(number);

		return number;
	}

	public static int[] segment(int i) {
		int[] v = new int[2];

		// Nombre de pixel
		int np = 0;
		int length = 0;
		int surround[];
		surround = new int[8];
		surround[0] = binarybuffer[i - SIZE_CASE_COLUMN];
		surround[1] = binarybuffer[i - SIZE_CASE_COLUMN + 1];
		surround[2] = binarybuffer[i + 1];
		surround[3] = binarybuffer[i + SIZE_CASE_COLUMN + 1];
		surround[4] = binarybuffer[i + SIZE_CASE_COLUMN];
		surround[5] = binarybuffer[i + SIZE_CASE_COLUMN - 1];
		surround[6] = binarybuffer[i - 1];
		surround[7] = binarybuffer[i - SIZE_CASE_COLUMN - 1];
		for (int m = 0; m < 8; m++) {
			np += surround[m];
		}
		// Si il n'y a qu'un seul pixel voisin, on a peut-être un segment.
		if (np == 1) {
			length = segmentLenght(i);
			v[0] = length;
			// Pour être considéré comme un segment, il faut une certaine longueur.
			if (length < 3)
				v[1] = 0;
			else if (surround[0] == 1)
				v[1] = N;
			else if (surround[1] == 1)
				v[1] = NW;
			else if (surround[2] == 1)
				v[1] = W;
			else if (surround[3] == 1)
				v[1] = SW;
			else if (surround[4] == 1)
				v[1] = S;
			else if (surround[5] == 1)
				v[1] = SE;
			else if (surround[6] == 1)
				v[1] = E;
			else if (surround[7] == 1)
				v[1] = NE;
		} else {
			v[0] = length;
			v[1] = 0;
		}
		return v;
	}

	public static int segmentLenght(int i) {

		int length = 1;
		int old = -1;
		int current = i;
		int next = -1;
		int np = 1;
		int surround[];

		while (np == 1) {
			np = 0;
			surround = new int[8];

			if (old != (current - SIZE_CASE_COLUMN)) {
				surround[0] = binarybuffer[current - SIZE_CASE_COLUMN];
				if (surround[0] == 1)
					next = current - SIZE_CASE_COLUMN;
			}
			if (old != (current - SIZE_CASE_COLUMN + 1)) {
				surround[1] = binarybuffer[current - SIZE_CASE_COLUMN + 1];
				if (surround[1] == 1)
					next = current - SIZE_CASE_COLUMN + 1;
			}
			if (old != (current + 1)) {
				surround[2] = binarybuffer[current + 1];
				if (surround[2] == 1)
					next = current + 1;
			}
			if (old != (current + SIZE_CASE_COLUMN + 1)) {
				surround[3] = binarybuffer[current + SIZE_CASE_COLUMN + 1];
				if (surround[3] == 1)
					next = current + SIZE_CASE_COLUMN + 1;
			}
			if (old != (current + SIZE_CASE_COLUMN)) {
				surround[4] = binarybuffer[current + SIZE_CASE_COLUMN];
				if (surround[4] == 1)
					next = current + SIZE_CASE_COLUMN;
			}
			if (old != (current + SIZE_CASE_COLUMN - 1)) {
				surround[5] = binarybuffer[current + SIZE_CASE_COLUMN - 1];
				if (surround[5] == 1)
					next = current + SIZE_CASE_COLUMN - 1;
			}
			if (old != (current - 1)) {
				surround[6] = binarybuffer[current - 1];
				if (surround[6] == 1)
					next = current - 1;
			}
			if (old != (current - SIZE_CASE_COLUMN - 1)) {
				surround[7] = binarybuffer[current - SIZE_CASE_COLUMN - 1];
				if (surround[7] == 1)
					next = current - SIZE_CASE_COLUMN - 1;
			}

			for (int m = 0; m < 8; m++) {
				np += surround[m];
			}

			if (np == 1)
				length++;

			if (length > 7)
				break;

			old = current;
			current = next;
		}

		return length;
	}


	/*
	 * Trouve la valeur maximale d'un tableau de float
	 * 
	 * @pre : prend le tableau dont le maximum doit etre trouvé
	 * 
	 * @post : renvoi le maximum
	 */
	public static float maxFromTab(float[] val) {
		float max = val[0];
		for (int i = 0; i < val.length; i++) {
			if (val[i] < max) {
				max = val[i];
			}
		}

		return max;
	}

	/*
	 * Trouve la valeur minimale d'un tableau de float
	 * 
	 * @pre : prend le tableau dont le minimum doit etre trouvé
	 * 
	 * @post : renvoi le minimum
	 */
	public static float minFromTab(float[] val) {
		float min = val[0];
		for (int i = 0; i < val.length; i++) {
			if (val[i] < min) {
				min = val[i];
			}
		}

		return min;
	}

	/*
	 * Calcule la valeur moyenne d'un tableau de float
	 * 
	 * @pre : prend le tableau dont la moyenne doit etre calculé
	 * 
	 * @post : renvoi la moyenne
	 */
	public static float moyFromTab(float[] tab) {
		float moy = 0;
		for (int c = 0; c < tab.length; c++) {
			moy = moy + tab[c];
		}
		moy = moy / tab.length;
		return moy;
	}

	/*
	 * Créé une copie du tableau donné en argument et la renvoi
	 * 
	 * @pre : prend le tableau a copier en argument
	 * 
	 * @post : renvoi la copie du tableau
	 */
	public static int[] makeCopy(int[] tab) {
		int[] copy = new int[tab.length];
		for (int i = 0; i < tab.length; i++) {
			copy[i] = tab[i];
		}
		return copy;
	}

	/*
	 * Deplace les moteurs X et Y des angles donné en argument
	 * 
	 * @pre : X, angle que le moteur X doit executer Y, angle que le moteur Y doit
	 * executer
	 * 
	 * @post : -
	 */
	public static void moveXY(int X, int Y) {
		motorX.rotate(X);
		motorY.rotate(Y);
	}

}