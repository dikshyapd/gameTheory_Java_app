public class Cocombos{
    public static void main(String[] args) {
        int[] arr = new int[7];
        int t = arr.length; 
        List<List<Integer>> result = sumCombinations(t);
        result.remove(0);
        FinalCombos(result,t);
        //for (int x=0; )
    }
public static final String ANSI_RESET = "\u001B[0m";
    // Declaring the background color
public static final String ANSI_RED_BACKGROUND
        = "\u001B[41m";

public static List<List<List<Integer>>> FinalCombos(List<List<Integer>> result, int t) {
    List<List<List<Integer>>> combosArr = new ArrayList<>();

    for (int i = 0; i < result.size(); i++) {
        List<Integer> first = result.get(i);
        int subNum = t - first.size();
        
        for (int j = 0; j < result.size(); j++) {
            List<Integer> second = result.get(j);

            if (second.size() <= subNum) {
                List<List<Integer>> pair = new ArrayList<>();
                pair.add(new ArrayList<>(first));
                pair.add(new ArrayList<>(second));
                
                int totalLength = first.size() + second.size();
                List<Integer> spaces = new ArrayList<>();
                while (totalLength < t) {
                  spaces.add(0);
                  totalLength++;
                  
                }
                System.out.print(first + " " + second + " ");
                if (spaces.size() > 0){
                  System.out.print(spaces);
                }
                System.out.println(" ");
                
                combosArr.add(pair);
            }
        }
    }

    // Print combos with colored second list and 0s for blank spaces
    for (List<List<Integer>> combo : combosArr) {
        List<Integer> p1 = combo.get(0); // Player 1
        List<Integer> p2 = combo.get(1); // Player 2

        List<Integer> board = new ArrayList<>();

        for (int val : p1) board.add(val);
        for (int val : p2) board.add(val);
    }

    return combosArr;
}


    /*
    go through the result array and find all items that have less than t number, 
    which all should have at least 1 
    With the number that has less than t, go through the list results and find 
    sizes that are the same as the missing pieces
    make combinations of ways we can combine the two 
    finalize the array that holds the combos
    
    with the values that have less than certain spaces, add a 0 to say that the space 
    is empty
    make some indication of the 
    */
    
    public static List<List<Integer>> sumCombinations(int num) {
        List<List<Integer>> result = new ArrayList<>();
        findSums(num, 1, new ArrayList<>(), result); 
        return result;
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
} 