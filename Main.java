/*
Gioco della tombola in Java
Alpha 05/02/2024

coded by Francesco Santaniello
*/

package tombola;

import tombola.Game.GameMode;
import tombola.Game.Sleep;

import java.util.Scanner;
public class Main {
	public static void main(String[] args) {
		Game game = new Game();
		Scanner scanner = new Scanner(System.in);
		
		short sc = -1;
		while(sc < 1 || sc > 2) {
			System.out.println("Scegli una modalità di gioco\n[1] Automatica (proseguimento gioco automatico)");
			System.out.println("[2] Manuale (premi inivio ogni volta che esce un numero)");
			sc = scanner.nextShort();
		}
		
		if (sc == 2)
			game.Start(GameMode.Manual, null);
		else {
			System.out.println();
			sc = -1;
			while(sc < 1 || sc > 3) {
				System.out.println("Scegli la velocità di gioco\n[1] Veloce (1 secondo)");
				System.out.println("[2] Media (2 secondi)");
				System.out.println("[3] Lenta (3 secondi)");
				sc = scanner.nextShort();
			}
			
			switch(sc) {
			case 1:
				game.Start(GameMode.Automatic,Sleep.Veloce);
				break;
			case 2:
				game.Start(GameMode.Automatic,Sleep.Medio);
				break;
			case 3:
				game.Start(GameMode.Automatic,Sleep.Lento);
				break;
			}
		}
		
		scanner.close();
	}

}
