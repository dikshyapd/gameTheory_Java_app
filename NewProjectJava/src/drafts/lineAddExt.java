package drafts;
import java.util.*;
import org.fusesource.jansi.AnsiConsole;

public class lineAddExt{
    public static void main(String[] args) {
      AnsiConsole.systemInstall();
            System.out.println("\u001B[31mThis is red text.\u001B[0m");
            AnsiConsole.systemUninstall();
        int[] arr = new int[7];
        int t = arr.length; 
        List<List<Integer>> result = sumCombinations(t);
        result.remove(0);
        FinalCombos(result,t);
    }
    public static List<List<Integer>> sumCombinations(int num) {
    List<List<Integer>> result = new ArrayList<>();
    findSums(num, 1, new ArrayList<>(), result); 
    return result;
}
public static final String ANSI_RESET = "\u001B[0m";
public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";

public static List<List<List<Integer>>> FinalCombos(List<List<Integer>> result, int t) {
    List<List<List<Integer>>> combosArr = new ArrayList<>();

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

                // Build a board of values only (without color)
                List<String> rawBoard = new ArrayList<>();

                for (int val : p1) rawBoard.add("P1 "+ val +" ");
                for (int val : p2) rawBoard.add("P2 "+ val +" ");

                // Fill the rest with blanks
                while (rawBoard.size() < t) rawBoard.add("0");

                // Generate all valid permutations of board
                Set<List<String>> uniquePerms = generatePermutations(rawBoard);

                // Print permutations
                for (List<String> perm : uniquePerms) {
                    System.out.println(perm);
                }
                System.out.println(); // Blank line between groups
            }
        }
    }

    return combosArr;
}


public static void findSums(int remaining, int start, List<Integer> current, List<List<Integer>> result) {
    if (remaining == 0) {
        result.add(new ArrayList<>(current));
        return;
    }

    for (int i = start; i <= remaining; i++) {
        current.add(i);
        findSums(remaining - i, i, current, result); 
        current.remove(current.size() -1);
    }
}

   public static Set<List<String>> generatePermutations(List<String> items) {
    Set<List<String>> result = new HashSet<>();
    permute(items, 0, result);
    return result;
}

private static void permute(List<String> arr, int index, Set<List<String>> result) {
    if (index == arr.size()) {
        result.add(new ArrayList<>(arr)); // Add a copy
        return;
    }
    Set<String> used = new HashSet<>();
    for (int i = index; i < arr.size(); i++) {
        if (used.contains(arr.get(i))) continue;
        used.add(arr.get(i));
        Collections.swap(arr, i, index);
        permute(arr, index + 1, result);
        Collections.swap(arr, i, index); // backtrack
    }
}
}
