import java.util.*;

public class takingTurns{

    public static void main(String[] args) {
        int[] arr = new int[5]; // board size
        int t = arr.length;
        List<List<Integer>> result = sumCombinations(t);
        result.remove(0);
        FinalCombos(result, t);
    }

    static Map<Integer, Integer> p1WinMap = new HashMap<>();
    static Map<Integer, Integer> p2WinMap = new HashMap<>();
    static Map<Integer, Integer> totalMap = new HashMap<>();

    public static List<List<Integer>> sumCombinations(int num) {
        List<List<Integer>> result = new ArrayList<>();
        findSums(num, 1, new ArrayList<>(), result);
        return result;
    }

    public static List<List<List<Integer>>> FinalCombos(List<List<Integer>> result, int t) {
        List<List<List<Integer>>> combosArr = new ArrayList<>();
        List<List<Integer>> list = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            List<Integer> p1 = result.get(i);
            int subNum = t - p1.size();

            for (int j = 0; j < result.size(); j++) {
                List<Integer> p2 = result.get(j);
                if (p2.size() <= subNum) {
                    List<List<Integer>> pair = new ArrayList<>();
                    pair.add(new ArrayList<>(p1));
                    pair.add(new ArrayList<>(p2));
                    combosArr.add(pair);

                    List<Integer> rawBoard = new ArrayList<>();
                    for (int val : p1) rawBoard.add(val);
                    for (int val : p2) rawBoard.add(val * 11);
                    while (rawBoard.size() < t) rawBoard.add(0);

                    Set<List<Integer>> uniquePerms = generatePermutations(rawBoard);
                    list.addAll(uniquePerms);
                }
            }
        }

        findPlayerWins(list);
        return combosArr;
    }

    static class Cell {
        Integer playerId;
        int troops;

        Cell(Integer playerId, int troops) {
            this.playerId = playerId;
            this.troops = troops;
        }

        boolean isEmpty() {
            return playerId == null;
        }

        boolean isOwnedBy(int id) {
            return playerId != null && playerId == id;
        }

        public String toString() {
            return isEmpty() ? "0" : "P" + playerId + "(" + troops + ")";
        }
    }

    public static void findPlayerWins(List<List<Integer>> perms) {
        for (List<Integer> perm : perms) {
            List<Cell> board = decodeBoard(perm);
            int emptySpaces = findNumOfSpaces(board);

            int p1Spaces = 0, p2Spaces = 0;
            for (Cell cell : board) {
                if (cell.isOwnedBy(1)) p1Spaces++;
                else if (cell.isOwnedBy(2)) p2Spaces++;
            }

            int firstPlayer = (p1Spaces < p2Spaces) ? 1 : (p2Spaces < p1Spaces) ? 2 : 1;
            int winner = simulateAttacks(board, firstPlayer);

            // 🔹 Print the permutation outcome
            System.out.println("Board: " + board + " | Winner: Player " + winner + " | Empty Spaces: " + emptySpaces);

            totalMap.put(emptySpaces, totalMap.getOrDefault(emptySpaces, 0) + 1);
            if (winner == 1) {
                p1WinMap.put(emptySpaces, p1WinMap.getOrDefault(emptySpaces, 0) + 1);
            } else if (winner == 2) {
                p2WinMap.put(emptySpaces, p2WinMap.getOrDefault(emptySpaces, 0) + 1);
            }
        }

        printLikelyWinners();
    }

    public static void printLikelyWinners() {
        System.out.println("\n=== Win Statistics by Empty Spaces ===");
        for (int spaceCount : totalMap.keySet()) {
            int p1Wins = p1WinMap.getOrDefault(spaceCount, 0);
            int p2Wins = p2WinMap.getOrDefault(spaceCount, 0);
            int total = totalMap.get(spaceCount);
            String likelyWinner = (p1Wins > p2Wins) ? "Player 1" :
                                  (p2Wins > p1Wins) ? "Player 2" : "Tie";
            double ratio = (double) Math.max(p1Wins, p2Wins) / total;

            System.out.printf("Spaces: %d | Most likely: %s | Ratio: %.2f (%d/%d)\n",
                    spaceCount, likelyWinner, ratio, Math.max(p1Wins, p2Wins), total);
        }
    }

    public static int findNumOfSpaces(List<Cell> board) {
        int num = 0;
        for (Cell item : board) {
            if (item != null && item.isEmpty()) num++;
        }
        return num;
    }

    public static List<Cell> decodeBoard(List<Integer> perm) {
        List<Cell> board = new ArrayList<>();
        for (int val : perm) {
            if (val == 0) {
                board.add(new Cell(null, 0));
            } else if (val % 11 == 0) {
                board.add(new Cell(2, val / 11));
            } else {
                board.add(new Cell(1, val));
            }
        }
        return board;
    }

    public static int simulateAttacks(List<Cell> board, int firstPlayer) {
        int currentPlayer = firstPlayer;
        int otherPlayer = (currentPlayer == 1) ? 2 : 1;
        boolean hasAttack;

        do {
            hasAttack = false;
            for (int i = 0; i < board.size(); i++) {
                Cell cell = board.get(i);
                if (!cell.isOwnedBy(currentPlayer)) continue;

                if (i > 0) hasAttack |= tryAttack(board, i, i - 1, currentPlayer);
                if (i < board.size() - 1) hasAttack |= tryAttack(board, i, i + 1, currentPlayer);
            }

            int temp = currentPlayer;
            currentPlayer = otherPlayer;
            otherPlayer = temp;
        } while (hasAttack);

        int[] area = new int[3];
        int[] troops = new int[3];
        for (Cell cell : board) {
            if (cell.playerId != null) {
                area[cell.playerId]++;
                troops[cell.playerId] += cell.troops;
            }
        }

        if (area[1] > area[2]) return 1;
        if (area[2] > area[1]) return 2;
        if (troops[1] > troops[2]) return 1;
        if (troops[2] > troops[1]) return 2;

        return firstPlayer;
    }

    public static boolean tryAttack(List<Cell> board, int fromIndex, int targetIndex, int currentPlayer) {
        if (targetIndex < 0 || targetIndex >= board.size()) return false;

        Cell target = board.get(targetIndex);
        if (target == null || target.isEmpty() || target.playerId == currentPlayer) return false;

        int combinedPower = 0;

        if (targetIndex > 0 && board.get(targetIndex - 1).isOwnedBy(currentPlayer))
            combinedPower += board.get(targetIndex - 1).troops;
        if (targetIndex < board.size() - 1 && board.get(targetIndex + 1).isOwnedBy(currentPlayer))
            combinedPower += board.get(targetIndex + 1).troops;
        if (Math.abs(fromIndex - targetIndex) == 1)
            combinedPower += board.get(fromIndex).troops;

        if (combinedPower > target.troops) {
            board.set(targetIndex, new Cell(null, 0));
            return true;
        }

        return false;
    }

    public static void findSums(int remaining, int start, List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i <= remaining; i++) {
            current.add(i);
            findSums(remaining - i, i, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static Set<List<Integer>> generatePermutations(List<Integer> items) {
        Set<List<Integer>> result = new HashSet<>();
        permute(items, 0, result);
        return result;
    }

    private static void permute(List<Integer> arr, int index, Set<List<Integer>> result) {
        if (index == arr.size()) {
            result.add(new ArrayList<>(arr));
            return;
        }
        Set<Integer> used = new HashSet<>();
        for (int i = index; i < arr.size(); i++) {
            if (used.contains(arr.get(i))) continue;
            used.add(arr.get(i));
            Collections.swap(arr, i, index);
            permute(arr, index + 1, result);
            Collections.swap(arr, i, index);
        }
    }
}
