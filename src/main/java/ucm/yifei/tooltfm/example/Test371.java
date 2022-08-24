package ucm.yifei.tooltfm.example;

public class Test371 {
    public static int Triang(int Side1, int Side2, int Side3){
        int triOut;
        if (Side1 <= 0 || Side2 <= 0 || Side3 <=0){
            triOut = 4;
            return (triOut);
        }
        triOut = 0;
        if(Side1 == Side2){
            triOut = triOut + 1;
        }
        if (Side1 == Side3){
            triOut = triOut + 2;
        }
        if (Side2 == Side3){
            triOut = triOut + 3;
        }

        if (triOut == 0){
            if (Side1+Side2 <= Side3 || Side2+Side3 <= Side1 || Side1+Side3 <= Side2){
                triOut = 4;
            }
            else{
                triOut = 0;
            }
            return (triOut);
        }

        if (triOut > 3){
            triOut = 3;
        }
        else if (triOut == 1 && Side1+Side2 > Side3){
            triOut = 2;
        }
        else if (triOut == 2 && Side1+Side3 > Side2){
            triOut = 2;
        }
        else if (triOut == 3 && Side2+Side3 > Side1){
            triOut = 2;
        }
        else {
            triOut = 4;
        }
        return (triOut);
    }
}
