import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//import java.util.Random;

/*
 * things to fix: 
 * when there is an instance of attacking an enmy area next door, the chances should say something like 
 * "Safe IF you're able to attack, mayebe make it 60% with a maybe"
 */

public class TempGamePlay {

    static class Slot {
        int player;
        int troops;

        Slot(int player, int troops) {
            this.player = player;
            this.troops = troops;
        }

        @Override
        public String toString() {
            return "[P" + player + ":" + troops + "]";
        }
    }
    public static int firstToFinish = 0;  // set during placement phase
    public static int attackRound = 0;

    public static List<Slot> mainBoard = new ArrayList<>();
    public static List<Integer> chancesP1 = new ArrayList<>();
    public static List<Integer> chancesP2 = new ArrayList<>();

    public static int boardSize;
    public static boolean p1Finished = false;
    public static boolean p2Finished = false;
    public static int currentPlayer = 1;
    public static boolean gameFinished = false;
    public static int p1Troops = 0;
    public static int p2Troops = 0;
    public static int playerToAttack = 0;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter board size: ");
        boardSize = input.nextInt();
        input.nextLine(); // clear newline
        setBoardSize(boardSize);
        System.out.print("Enter troop size: "); 
        p1Troops = input.nextInt();
        input.nextLine(); 
        p2Troops = p1Troops; 
        while (!gameFinished) {
            printBoard();

            if (mainBoardFull() || (p1Finished && p2Finished)) {
    System.out.println("All troops placed. ATTACK PHASE BEGINS!");
    gameFinished = true;

    currentPlayer = firstToFinish;  // Set attacker
    int otherPlayer = (currentPlayer == 1) ? 2 : 1;

    while (true) {
        System.out.println("\n=============================");
        System.out.println("      ATTACK ROUND: " + (attackRound + 1));
        System.out.println("=============================");

        boolean currentCanAttack = canAttack(currentPlayer);
        boolean otherCanAttack = canAttack(otherPlayer);

        displayTerritories();
        printBoard();

        if (!currentCanAttack && !otherCanAttack) {
            System.out.println("No more possible attacks for either player.");
            break;
        }

        // Current Player's Turn
        if (currentCanAttack) {
            System.out.println("Player " + currentPlayer + ", it's your attack turn.");

            while (canAttack(currentPlayer)) {
                System.out.print("Select a slot to attack (0 to " + (boardSize - 1) + "): ");
                int attackSlot = input.nextInt();
                input.nextLine();
                int beforeTerritories = countTerritories(otherPlayer);
                attack(mainBoard, attackSlot);
                int afterTerritories = countTerritories(otherPlayer);

                if (afterTerritories < beforeTerritories) {
                    System.out.println("Territory captured!");
                }

                printBoard();

                // Ask if player wants to try another attack
                if (!canAttack(currentPlayer)) {
                    System.out.println("No more valid attacks available.");
                    break;
                }
                System.out.print("Attack again? (y/n): ");
                String again = input.nextLine();
                if (!again.equalsIgnoreCase("y")) break;
            }
        } else {
            System.out.println("Player " + currentPlayer + " has no available attacks.");
        }

        // Switch player
        int temp = currentPlayer;
        currentPlayer = otherPlayer;
        otherPlayer = temp;

        attackRound++;
    }

    // After loop ends, determine winner
    determineWinner();
}

System.out.println("Current Player: Player " + currentPlayer);
int availableTroops = (currentPlayer == 1) ? p1Troops : p2Troops;

if (availableTroops == 0) {
    if (currentPlayer == 1) {
        p1Finished = true;
        if (firstToFinish == 0) firstToFinish = 1;
    } else {
        p2Finished = true;
        if (firstToFinish == 0) firstToFinish = 2;
    }
    System.out.println("Player " + currentPlayer + " has no troops left.");
    switchPlayer();
    continue;
}


    if (availableTroops == 0) {
        System.out.println("Player " + currentPlayer + " has no troops left.");
        if (currentPlayer == 1) {
            p1Finished = true;
        } else {
        p2Finished = true;
        }
        switchPlayer();
        continue;
     }

    System.out.println("Troops left: " + availableTroops);

    int troopsToPlace = -1;
    while (troopsToPlace < 1 || troopsToPlace > availableTroops) {
        System.out.print("Enter number of troops to place (1 to " + availableTroops + "): ");
        troopsToPlace = input.nextInt();
        input.nextLine(); // clear newline
        }

            displaySafetyBoard(troopsToPlace);

int position = -1;
while (position < 0 || position >= boardSize || mainBoard.get(position) != null) {
    System.out.print("Based on safety board, choose position to place troops (0 to " + (boardSize - 1) + "): ");
    position = input.nextInt();
    input.nextLine(); // clear newline
}


            System.out.print("Confirm placement (y/n): ");
            String confirm = input.nextLine();
            if (confirm.equalsIgnoreCase("y") == false) {
                System.out.println("Placement cancelled.");
                continue;
            }

            if (currentPlayer == 1) {
                mainBoard.set(position, new Slot(1, troopsToPlace));
                p1Troops = p1Troops - troopsToPlace;
            } else {
                mainBoard.set(position, new Slot(2, troopsToPlace));
                p2Troops = p2Troops - troopsToPlace;
            }
            switchPlayer();
        }

        input.close();
    }

    public static int countTroops(int player) {
    int count = 0;
    for (Slot s : mainBoard) {
        if (s != null && s.player == player) {
            count += s.troops;
        }
    }
    return count;
}

public static void determineWinner() {
    int p1Territories = countTerritories(1);
    int p2Territories = countTerritories(2);

    int p1Troops = countTroops(1);
    int p2Troops = countTroops(2);

    System.out.println("\n========= FINAL SCORES =========");
    System.out.println("Player 1  Territories: " + p1Territories + ", Troops: " + p1Troops);
    System.out.println("Player 2  Territories: " + p2Territories + ", Troops: " + p2Troops);
    System.out.println("Attack Rounds Played: " + attackRound);
    System.out.println("First to Attack: Player " + firstToFinish);

    int winner = 0;

    if (p1Territories > p2Territories) {
        winner = 1;
    } else if (p2Territories > p1Territories) {
        winner = 2;
    } else {
        // Territories tied → check total troops
        if (p1Troops > p2Troops) {
            winner = 1;
        } else if (p2Troops > p1Troops) {
            winner = 2;
        } else {
            // Total troops also tied → first attacker wins
            winner = firstToFinish;
        }
    }

    System.out.println("\n====== GAME OVER ======");
    System.out.println("Player " + winner + " wins!");
}

    public static void attack(List<Slot> board, int pos) {
    if (pos < 0 || pos >= boardSize) {
        System.out.println("Invalid position.");
        return;
    }

    Slot targetSlot = board.get(pos);
    if (targetSlot == null) {
        System.out.println("There is no troop at this position.");
        return;
    }

    if (targetSlot.player == currentPlayer) {
        System.out.println("You cannot attack your own troop.");
        return;
    }

    int attacker1 = -1, attacker2 = -1;
    int pos1 = -1, pos2 = -1;

    // Check left
    if (pos > 0) {
        Slot left = board.get(pos - 1);
        if (left != null && left.player == currentPlayer) {
            attacker1 = left.troops;
            pos1 = pos - 1;
        }
    }

    // Check right
    if (pos < boardSize - 1) {
        Slot right = board.get(pos + 1);
        if (right != null && right.player == currentPlayer) {
            attacker2 = right.troops;
            pos2 = pos + 1;
        }
    }

    int totalAttack = 0;
    if (attacker1 != -1) totalAttack += attacker1;
    if (attacker2 != -1) totalAttack += attacker2;

    if (totalAttack == 0) {
        System.out.println("You must be adjacent to an enemy to attack.");
        return;
    }

    int defenderTroops = targetSlot.troops;

    if (totalAttack <= defenderTroops) {
        System.out.println("Attack failed! Your combined troops are not strong enough.");
        return;
    }

    // Attack is successful
    System.out.println("Attack successful! Enemy at position " + pos + " defeated.");
    board.set(pos, new Slot(currentPlayer, totalAttack - defenderTroops));
}



public static void setBoardSize(int n) {
    for (int i = 0; i < n; i++) {
        mainBoard.add(null);
        chancesP1.add(50);
        chancesP2.add(50);
    }
}

 public static int computeSafetyForSlot(int pos, int troopsToPlace) {
    int leftTroops = -1;
    int leftPlayer = 0;
    if (pos > 0 && mainBoard.get(pos - 1) != null) {
        leftTroops = mainBoard.get(pos - 1).troops;
        leftPlayer = mainBoard.get(pos - 1).player;
    }

    int rightTroops = -1;
    int rightPlayer = 0;
    if (pos < mainBoard.size() - 1 && mainBoard.get(pos + 1) != null) {
        rightTroops = mainBoard.get(pos + 1).troops;
        rightPlayer = mainBoard.get(pos + 1).player;
    }

    boolean leftIsEnemy = (leftPlayer != 0 && leftPlayer != currentPlayer);
    boolean rightIsEnemy = (rightPlayer != 0 && rightPlayer != currentPlayer);
    int enemyTroopsTotal = (currentPlayer == 1) ? p2Troops : p1Troops;

   

    if (leftIsEnemy && leftTroops > troopsToPlace) {
        return 10;
    } else if (rightIsEnemy && rightTroops > troopsToPlace) {
        return 10;
    }

    boolean enemyCanCombineAttack = false;
    if (leftIsEnemy) {
        int helper = (pos - 2 >= 0 && mainBoard.get(pos - 2) != null && mainBoard.get(pos - 2).player == leftPlayer) ? mainBoard.get(pos - 2).troops : 0;
        if (leftTroops + helper > troopsToPlace) enemyCanCombineAttack = true;
    }

    if (rightIsEnemy) {
        int helper = (pos + 2 < boardSize && mainBoard.get(pos + 2) != null && mainBoard.get(pos + 2).player == rightPlayer) ? mainBoard.get(pos + 2).troops : 0;
        if (rightTroops + helper > troopsToPlace) enemyCanCombineAttack = true;
    }

    if (enemyCanCombineAttack) {
        return 30;
    }

    if (enemyTroopsTotal == 0 && !leftIsEnemy && !rightIsEnemy) {
        return 100;
    }

    return 70; // fallback safety
}


public static void displaySafetyBoard(int troopsToPlace) {
    System.out.println("\n--- Safety Board for " + troopsToPlace + " troops ---");
    for (int i = 0; i < boardSize; i++) {
        if (mainBoard.get(i) != null) {
            System.out.println("Position " + i + ": FILLED");
        } else {
            int chance = computeSafetyForSlot(i, troopsToPlace);
            String status;

            if (chance < 40) {
                status = "NOT SAFE";
            } else if (chance <= 60) {
                status = "Needs Defense";
            } else {
                status = "Safe";
            }

            // Check for attack opportunities:
            String attackNote = getAttackOpportunityNote(i, troopsToPlace);
            System.out.println("Position " + i + ": " + chance + "% - " + status + attackNote);
        }
    }
    System.out.println("------------------------------------------\n");
}

public static String getAttackOpportunityNote(int pos, int troopsToPlace) {
    StringBuilder sb = new StringBuilder();

    // Check left
    if (pos > 0 && mainBoard.get(pos - 1) != null) {
        Slot left = mainBoard.get(pos - 1);
        if (left.player != currentPlayer && troopsToPlace > left.troops) {
            sb.append(" - Can likely attack enemy at slot " + (pos - 1));
        }
    }

    // Check right
    if (pos < boardSize - 1 && mainBoard.get(pos + 1) != null) {
        Slot right = mainBoard.get(pos + 1);
        if (right.player != currentPlayer && troopsToPlace > right.troops) {
            if (sb.length() > 0) sb.append(" &");
            else sb.append(" - Can likely attack enemy at slot ");
            sb.append(" " + (pos + 1));
        }
    }

    return sb.toString();
}




    public static void printBoard() {
        System.out.println("\nCurrent Board:");
        for (int i = 0; i < mainBoard.size(); i++) {
            Slot s = mainBoard.get(i);
            if (s == null) {
                System.out.print("[ ]");
            } else {
                if (s.player == 1) {
                    System.out.print("[" + s.troops + "A]");
                } else {
                    System.out.print("[" + s.troops + "B]");
                }
            }
        }
        System.out.println("\n");
    }

    public static boolean canAttack(int player) {
    for (int i = 0; i < boardSize; i++) {
        Slot s = mainBoard.get(i);
        if (s == null || s.player != 3 - player) continue; // Only enemy slots

        boolean left = (i > 0 && mainBoard.get(i - 1) != null && mainBoard.get(i - 1).player == player);
        boolean right = (i < boardSize - 1 && mainBoard.get(i + 1) != null && mainBoard.get(i + 1).player == player);

        int leftTroops = (left) ? mainBoard.get(i - 1).troops : 0;
        int rightTroops = (right) ? mainBoard.get(i + 1).troops : 0;
        int total = leftTroops + rightTroops;

        if (total > s.troops) return true;
    }
    return false;
}

public static void displayTerritories() {
    int p1 = countTerritories(1);
    int p2 = countTerritories(2);
    System.out.println("Territories : Player 1: " + p1 + " | Player 2: " + p2);
}

public static int countTerritories(int player) {
    int count = 0;
    for (Slot s : mainBoard) {
        if (s != null && s.player == player) {
            count++;
        }
    }
    return count;
}


    public static void switchPlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
    }

    public static boolean mainBoardFull() {
        for (Slot s : mainBoard) {
            if (s == null) {
                return false;
            }
        }
        return true;
    }
}
