package tombola;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;

public class Game {
	private final short MAX_X_TABLE = 9;
	private final short MAX_Y_TABLE = 3;
	private final short BUFFER_PRINT_TABLE_SPACE = 4;
	private final short MIN_RAND = 1;
	private final short MAX_RAND = 90;
	private final short EMPTY_POSITION = 12;
	private final char EMPTY_SPACE_CHAR = '□';
	private final char REPLACE_NUMBER = '■';
	private short[][] table;
	private List<Short> numbers = new ArrayList<>();
	private final short N_LINE_SEPARETOR = 50;
	private final char CHAR_LINE_SEPARETOR = '▄';
	private final short MAX_PRINT_NUMBER = 5;
	public final short LENTO_SLEEP_TIME_MS = 3000;
	public final short MEDIO_SLEEP_TIME_MS = 2000;
	public final short VALOCE_SLEEP_TIME_MS = 1000;
	private short time_sleep;

	private enum Point {
		Ambo, Terna, Quaterna, Quintina, Tombola
	}

	public enum Sleep {
		Lento, Medio, Veloce
	}

	private enum EndMode {
		Win, Lose
	}

	public enum GameMode {
		Automatic, Manual
	}

	private short nextRand() {
		short rand = 0;

		while (rand < this.MIN_RAND || rand > this.MAX_RAND) {
			rand = (short) (Math.random() * (this.MAX_RAND + 1));
		}

		return rand;
	}

	private void printGreen(Object msg) {
		System.out.print("\u001B[32m" + msg + "\u001B[0m");
		System.out.flush();
	}

	private void printRed(Object msg) {
		System.out.print("\u001B[31m" + msg + "\u001B[0m");
		System.out.flush();
	}

	private void printTable(List<Short> notList) {
		if (this.table == null)
			return;

		for (short[] y : this.table) {
			for (short x : y) {

				String c_print = (x == -1) ? String.valueOf(this.EMPTY_SPACE_CHAR) : String.valueOf(x);

				if (!notList.isEmpty())
					if (notList.contains(x))
						c_print = String.valueOf(this.REPLACE_NUMBER);

				System.out.format("%4s", c_print);

			}

			System.out.println();
		}
	}

	private boolean find(short[][] o, short e) {
		for (short[] i : o) {
			for (short i2 : i) {
				if (i2 == e)
					return true;
			}
		}
		return false;
	}

	private short[][] genTable() {
		final short EMPTY_FOR_ROW = (short) (this.EMPTY_POSITION / this.MAX_Y_TABLE);
		short[][] t = new short[this.MAX_Y_TABLE][this.MAX_X_TABLE];
		this.numbers.clear();

		for (short y = 0; y < this.MAX_Y_TABLE; y++) {
			List<Short> empty_space_pos = new ArrayList<>();

			for (int i = 0; i < EMPTY_FOR_ROW; i++) {
				short pos = (short) (Math.random() * (this.MAX_X_TABLE));

				while (empty_space_pos.contains(pos))
					pos = (short) (Math.random() * (this.MAX_X_TABLE));

				empty_space_pos.add(pos);
			}

			empty_space_pos.sort(null);
			for (short x = 0; x < this.MAX_X_TABLE; x++) {
				short n = this.nextRand();

				while (this.find(t, n))
					n = this.nextRand();

				for (short pos : empty_space_pos) {
					if (pos == x) {
						n = -1;
						break;
					}
				}

				t[y][x] = n;
			}
		}

		return t;
	}

	private short genRandomForGame(List<Short> not) {
		short r = 0;

		short c = 0;
		while (r < this.MIN_RAND || r > this.MAX_RAND || not.contains(r)) {
			r = (short) (Math.random() * (this.MAX_RAND + 1));

			if (c == this.MAX_RAND)
				return -2;

			c++;
		}

		return r;
	}

	private Point calculatePoint(List<Short> spawned) {
		if (this.table == null)
			return null;

		Point p = null;

		List<List<Short>> tableList = new ArrayList<>();

		short[] emptySpace = new short[3];

		short i = 0;
		for (short[] a : this.table) {
			List<Short> t = new ArrayList<>();
			short c = 0;
			for (short a2 : a)
				if (a2 != -1)
					t.add(a2);
				else
					c++;
			emptySpace[i] = c;
			tableList.add(t);
			i++;
		}

		List<Short> pointCol = new ArrayList<>();

		for (List<Short> col : tableList) {
			short cont = 0;
			for (short row : col) {
				if (spawned.contains(row))
					cont++;
			}

			pointCol.add(cont);
		}

		short lastPunto = 0;
		for (short point : pointCol)
			lastPunto += point;
		if (lastPunto == (this.MAX_X_TABLE - this.BUFFER_PRINT_TABLE_SPACE) * this.MAX_Y_TABLE)
			return Point.Tombola;

		List<Short> op = new ArrayList<>();

		for (short punto : pointCol)
			op.add(punto);

		op.sort(null);

		if (op.get(op.size() - 1) == 2)
			p = Point.Ambo;
		else if (op.get(op.size() - 1) == 3)
			p = Point.Terna;
		else if (op.get(op.size() - 1) == 4)
			p = Point.Quaterna;
		else if (op.get(op.size() - 1) == 5)
			p = Point.Quintina;

		return p;
	}

	private void printSeparetor() {
		System.out.println();
		for (short i = 0; i <= this.N_LINE_SEPARETOR; i++)
			System.out.print(this.CHAR_LINE_SEPARETOR);
		System.out.println();
	}

	private void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	private void startGame(GameMode m) {
		EndMode mode = EndMode.Win;
		List<Point> points = new ArrayList<>();
		this.table = this.genTable();
		List<Short> spawnedNumbers = new ArrayList<>();
		Scanner scannerInput = new Scanner(System.in);

		while (!points.contains(Point.Tombola)) {
			// gen bot

			clearScreen();
			System.out.println("\n");

			short newBotRandomNumber = this.genRandomForGame(spawnedNumbers);

			if (newBotRandomNumber == -2) {
				mode = EndMode.Lose;
				break;
			}

			spawnedNumbers.add(newBotRandomNumber);

			Point punto = this.calculatePoint(spawnedNumbers);
			if (!points.contains(punto) && punto != null)
				points.add(punto);

			System.out.print("Numeri usciti: ");
			for (short i = 0; i < spawnedNumbers.size(); i++) {
				System.out.print(spawnedNumbers.get(i));

				if (i >= this.MAX_PRINT_NUMBER - 1) {
					System.out.print(". . .");
					break;
				}

				if (i != spawnedNumbers.size() - 1)
					System.out.print(", ");
			}

			this.printSeparetor();
			System.out.println();

			this.printTable(spawnedNumbers);

			this.printSeparetor();

			if (!points.isEmpty()) {
				System.out.println();
				System.out.print("Punti: ");
				for (int i = 0; i < points.size(); i++) {
					this.printGreen(points.get(i));

					if (i != points.size() - 1)
						System.out.print(", ");
				}
			}

			if (m == GameMode.Automatic) {
				try {
					System.out.format("\n\nProssima mossa tra %d secondi. . .", (int) (this.time_sleep / 1000));
					Thread.sleep(this.time_sleep);
				} catch (Exception e) {
				}
			} else {
				System.out.println("\n\nPremere un tasto per continuare. . .");
				scannerInput.nextLine();
			}
		}

		scannerInput.close();
		this.End(mode);
	}

	public void End(EndMode mode) {
		System.out.println();
		if (mode == EndMode.Win)
			this.printGreen("Hai vinto :)\n");
		else
			this.printRed("Hai perso :(\n");
	}

	public void Start(GameMode gameMode, Sleep sleep) {
		if (sleep == Sleep.Veloce)
			this.time_sleep = this.VALOCE_SLEEP_TIME_MS;
		else if (sleep == Sleep.Medio)
			this.time_sleep = this.MEDIO_SLEEP_TIME_MS;
		else if (sleep == Sleep.Lento)
			this.time_sleep = this.LENTO_SLEEP_TIME_MS;

		this.startGame(gameMode);
	}
}