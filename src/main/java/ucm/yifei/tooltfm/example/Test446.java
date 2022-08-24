package ucm.yifei.tooltfm.example;

public class Test446 {
    public static int t(int x, int y) {
         while(x<0){
             x++;
             while(x<y){
                 y--;
             }
         }
         return y;
    }
 }