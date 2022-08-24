package ucm.yifei.tooltfm.example;

public class Test280 {
    public int[] moverUno(int[] inArray){
        if (inArray == null){
            throw new NullPointerException("NullPointerException");
        }
        int [] outArray = new int[inArray.length];
        outArray[0] = -1;
        for (int i = 1; i < inArray.length - 1; i++){
            outArray[i] = inArray[i-1];
        }
        return outArray;
    }
}