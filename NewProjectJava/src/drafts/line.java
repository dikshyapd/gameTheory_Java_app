package drafts;
import java.util.*;

class line{
  public static void main (String args[]){
    int[] arr = new int[9];
    int t = arr.length; 
    List<List<Integer>> result = dividens(t);
    for (int x=0; x <= result.size(); x++){
      System.out.println(result.get(x));
    }
    arr[2] = 2; 
    System.out.println(t);
  }
  public static List<List<Integer>> dividens(int num){
    List<List<Integer>> result = new ArrayList<>();
    Finddividens(num, 2, new ArrayList<>(), result);
    return result; 
  }
  public static void Finddividens(int num, int start, List<Integer> current, List<List<Integer>> result ){
    if (num == 1){
      result.add(new ArrayList<>(current));
      return;
    }
    for (int i = start; i <= num; i++){
      if (num % i == 0){
        current.add(i);
        Finddividens(num/i, i, current, result);
        current.remove(current.size()-1);
      }
    }
  }
}