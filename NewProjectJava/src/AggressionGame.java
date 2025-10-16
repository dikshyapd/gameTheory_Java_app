import java.util.*;

public class AggressionGame{

    static class Cell {
        Integer playerId;
        int troops;

        Cell(Integer playerId, int troops) {
            this.playerId = playerId;
            this.troops = troops;
        }

        boolean isOwnedBy(int player) {
            return playerId != null && playerId == player;
        }

        boolean isEmpty() {
            return playerId == null || troops == 0;
        }

        public String toString() {
            if (isEmpty()) return "[  ]";
            return "[" + playerId + ":" + troops + "]";
        }
    }

    static List<Cell> board = new ArrayList<>();
    static int boardSize = 12;
    static int totalTroops = 15;
    static int p1Troops = totalTroops;
    static int p2Troops = totalTroops;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < boardSize; i++) {
            board.add(new Cell(null, 0));
        }

        int currentPlayer = 1;

        while (p1Troops > 0 || p2Troops > 0) {
            printBoard();

            if ((currentPlayer == 1 && p1Troops == 0) || (currentPlayer == 2 && p2Troops == 0)) {
                System.out.println("Player " + currentPlayer + " has no troops left.");
                currentPlayer = 3 - currentPlayer;
                continue;
            }

            System.out.println("Player " + currentPlayer + "'s turn. Troops left: " + (currentPlayer == 1 ? p1Troops : p2Troops));

            int troops, index;

            while (true) {
                System.out.print("Enter number of troops to place: ");
                troops = scanner.nextInt();
                if (troops < 1 || troops > (currentPlayer == 1 ? p1Troops : p2Troops)) {
                    System.out.println("Invalid troop number.");
                    continue;
                }

                System.out.print("Enter board index (0-" + (boardSize - 1) + "): ");
                index = scanner.nextInt();
                if (index < 0 || index >= boardSize) {
                    System.out.println("Invalid index.");
                    continue;
                }

                if (!board.get(index).isEmpty()) {
                    System.out.println("Cell already occupied.");
                    continue;
                }

                break;
            }

            board.set(index, new Cell(currentPlayer, troops));
            if (currentPlayer == 1) {
                p1Troops -= troops;
            } else {
                p2Troops -= troops;
            }

            currentPlayer = 3 - currentPlayer;
        }

        System.out.println("\n--- All troops placed. Starting combat phase ---");
        simulateAttacks();

        printBoard();
        determineWinner();
    }

    static void printBoard() {
        System.out.println("\nBoard:");
        for (int i = 0; i < board.size(); i++) {
            System.out.print(board.get(i).toString() + " ");
        }
        System.out.println("\n");
    }

    static void simulateAttacks() {
        int attacker = 1;
        int defender = 2;

        boolean changed;

        do {
            changed = false;

            for (int i = 0; i < board.size(); i++) {
                Cell cell = board.get(i);
                if (cell.isOwnedBy(attacker)) {
                    // Check left
                    if (i > 0) changed |= tryAttack(i, i - 1, attacker);
                    // Check right
                    if (i < board.size() - 1) changed |= tryAttack(i, i + 1, attacker);
                }
            }

            int tmp = attacker;
            attacker = defender;
            defender = tmp;

        } while (changed);
    }

    static boolean tryAttack(int from, int to, int attacker) {
        if (to < 0 || to >= board.size()) return false;

        Cell target = board.get(to);
        if (target.isEmpty() || target.playerId == attacker) return false;

        int strength = 0;
        if (from > 0 && board.get(from - 1).isOwnedBy(attacker))
            strength += board.get(from - 1).troops;
        if (from < board.size() - 1 && board.get(from + 1).isOwnedBy(attacker))
            strength += board.get(from + 1).troops;
        strength += board.get(from).troops;

        if (strength > target.troops) {
            board.set(to, new Cell(null, 0));
            System.out.println("Player " + attacker + " conquered cell " + to);
            return true;
        }

        return false;
    }

    static void determineWinner() {
        int[] owned = new int[3];
        int[] totalTroops = new int[3];

        for (Cell cell : board) {
            if (cell.playerId != null) {
                owned[cell.playerId]++;
                totalTroops[cell.playerId] += cell.troops;
            }
        }

        System.out.println("Player 1 owns " + owned[1] + " cells, " + totalTroops[1] + " troops.");
        System.out.println("Player 2 owns " + owned[2] + " cells, " + totalTroops[2] + " troops.");

        if (owned[1] > owned[2]) {
            System.out.println("🎉 Player 1 wins by territory!");
        } else if (owned[2] > owned[1]) {
            System.out.println("🎉 Player 2 wins by territory!");
        } else if (totalTroops[1] > totalTroops[2]) {
            System.out.println("🎉 Player 1 wins by troops!");
        } else if (totalTroops[2] > totalTroops[1]) {
            System.out.println("🎉 Player 2 wins by troops!");
        } else {
            System.out.println("It's a tie!");
        }
    }
}
