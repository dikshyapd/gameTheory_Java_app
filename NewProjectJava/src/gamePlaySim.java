import java.util.*;

public class gamePlaySim{

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

        Cell cloneCell() {
            return new Cell(playerId, troops);
        }
    }

    static final int BOARD_SIZE = 7;
    static final int TOTAL_TROOPS = 7;
    static final int SIMULATIONS = 10000;

    public static void main(String[] args) {
        int p1Wins = 0;
        int p2Wins = 0;
        int ties = 0;

        for (int i = 0; i < SIMULATIONS; i++) {
            List<Cell> board = createRandomBoard();
            simulateAttacks(board);
            int winner = determineWinner(board);
            if (winner == 1) p1Wins++;
            else if (winner == 2) p2Wins++;
            else ties++;
        }

        System.out.println("After " + SIMULATIONS + " simulations:");
        System.out.println("Player 1 wins: " + p1Wins);
        System.out.println("Player 2 wins: " + p2Wins);
        System.out.println("Ties: " + ties);
    }

    static List<Cell> createRandomBoard() {
        Random rand = new Random();
        int[] p1 = randomDistribution(TOTAL_TROOPS, BOARD_SIZE);
        int[] p2 = randomDistribution(TOTAL_TROOPS, BOARD_SIZE);
        List<Cell> board = new ArrayList<>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (p1[i] > 0 && p2[i] == 0)
                board.add(new Cell(1, p1[i]));
            else if (p2[i] > 0 && p1[i] == 0)
                board.add(new Cell(2, p2[i]));
            else
                board.add(new Cell(null, 0)); // shared or empty
        }
        return board;
    }

    static int[] randomDistribution(int troops, int size) {
        int[] dist = new int[size];
        Random rand = new Random();
        for (int i = 0; i < troops; i++) {
            dist[rand.nextInt(size)]++;
        }
        return dist;
    }

    static void simulateAttacks(List<Cell> board) {
        int attacker = 1;
        int defender = 2;
        boolean changed;

        do {
            changed = false;
            for (int i = 0; i < board.size(); i++) {
                Cell cell = board.get(i);
                if (cell.isOwnedBy(attacker)) {
                    changed |= tryAttack(board, i, i - 1, attacker);
                    changed |= tryAttack(board, i, i + 1, attacker);
                }
            }
            int temp = attacker;
            attacker = defender;
            defender = temp;
        } while (changed);
    }

    static boolean tryAttack(List<Cell> board, int from, int to, int attacker) {
        if (to < 0 || to >= board.size()) return false;
        Cell target = board.get(to);
        if (target.playerId == null || target.playerId == attacker) return false;

        int strength = board.get(from).troops;
        if (to > 0 && board.get(to - 1).isOwnedBy(attacker))
            strength += board.get(to - 1).troops;
        if (to < board.size() - 1 && board.get(to + 1).isOwnedBy(attacker))
            strength += board.get(to + 1).troops;

        if (strength > target.troops) {
            board.set(to, new Cell(null, 0)); // conquer
            return true;
        }
        return false;
    }

    static int determineWinner(List<Cell> board) {
        int[] spaceCount = new int[3];
        int[] troopCount = new int[3];

        for (Cell cell : board) {
            if (cell.playerId != null) {
                spaceCount[cell.playerId]++;
                troopCount[cell.playerId] += cell.troops;
            }
        }

        if (spaceCount[1] > spaceCount[2]) return 1;
        if (spaceCount[2] > spaceCount[1]) return 2;
        if (troopCount[1] > troopCount[2]) return 1;
        if (troopCount[2] > troopCount[1]) return 2;
        return 0;
    }
}

