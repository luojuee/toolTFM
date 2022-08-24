package Calc;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class Calculate {

public int tRetuen(int a, int b) {
	//单一返回  √
	return a + b;
}

public void tSampleExpression() {
	//简单节点  √
	int a = 1;
	int b = 2;
	a = a + b;
}
//---------------if 相关 -------------
public void tIf(int x, int y) {
	//if语句节点 √
	if (x < y){
		y = 0;
		x = x + 1;
	}
	x = y + 1;
} 
public void tIfReturn(int x, int y) {
	//if-return √
	if (x < y){
	   return;
	}
	System.out.println(x);
	return;
} 
public void tIfIf(int x, int y) {
	//if-if √
	if (x < y){
		y = 0;
		if (x < 0){
			x = x + 1;
		}
		//y = 0;
	}
	//y = 0;
}
public void tIfIf2(int x, int y) {
	//if-if	√
	if (x < y){
		y = 0;
	}
	if (x < 0){
		x = x + 1;
	}
}

public void tIfElse(int x, int y) {
//ifelse语句节点1 √
	if (x < y)
	{
	   y = 0;
	}
	else
	{
	   x = y;
	}
	x = x + 1;
} 
public void tIfElse2(int x, int y) {
	//if(if)else 对
	if (x < y){
		y = 0;//不影响
		if (x < 0){//while 对
			x = x + 1;
		}
		//后方添加正确，无简单句错误 已√
		/*else {
			y = y + 1;
		}*/
	}
	else{
		x--;
	}
	//y = y + 1;
}
public int tIfElse3(int x, int y) {
	//ifelse(if) 对了！！
	int z = 0;
	if (x < y){
		z = 1;
	}
	else{
		z = x;
		if (x < 0){
			z++;
		}
	}
	return z;
} 

//----------------------while--------------
public void tWhile(int a, int b) {
	//while语句节点 √
	int x = 0;
	while (x < y)
	{
		int aux = 0;
	   y = function(x,y);
	   x = x + 1;
	}
	int a = 0;
} 
public int tWhile2(int x, int y) {
	//while(while) √
	int q = x;
	int r = y;
	while(q!=r){
		while(q==0){//if √
			q++;
		}
	}
	return q;
}
public int tWhileIfs(int x, int y) {
	//while - if - return √  => 测试路径有问题
	int q = x;
	int r = y;
	while(q!=r){
		//if也ok
		if(q>r){
			q=q-r;
		}
		else{
			r=r-q;
		}
	}
	return q;
}
public void tWhileIfBreak(int x, int y) {
	//while + if + break; √
	while (x < y)
	{
		x = 0;
	   if(x <= 0) {
		   break;
	   }
	   else if (y < 0) {
		   x=1;
		   continue;
	   }
	   x++;
	}
	int z=x;//换成其他ok
}
public void tWhileBreak(int x, int y) {
	while (x < y)
	{
		if(x <= 0) {
			break;
		}
		else {
			x++;
		}
	}
	int z=x;//换成其他ok
}
public void tWhileIfContinue(int x, int y) {
	while (x < y)
	{
		if(x <= 0) {
			x++;
		}
		else {
			x=x+2;
			continue;
		}
		y--;
	}
	int z=x;//换成其他ok
}
	//----------------------Do while------------
public void tDoWhile(int x, int y) {
	//do-while语句节点 √ do(if) √
	x = 0;
	do
	{
	   y = f (x, y);
	   x = x + 1;
	} while (x < y);
	System.out.println(y);
}
public void tDoWhile2(int x, int y) {
	do
	{
		if(x<y){
			x = x + 1;
		}
		else{
			x = 2*x;
		}
	} while (x<=y);
	System.out.println(x);
	int z = y;
}
//-----------------------for---------------
public void tFor(int x, int y) {
	//for √
	for (x = 0; x < y; x++)
	{
	   y = function(x, y);
	}
}

public int tFor1(int y) {
	int z = 0;
	for (int x = 0; x < y; x++)
	{
		int aux = y - x;
		z += aux;
	}
	return z;
}
public void tFor2(int z) {
	//for(for) √
	for (int x = 0; x < z; x++)
	{
		String aux = "";
		for (int y = 0; y < z; y++)//while √ if,ifelse √
		{
			aux = aux + y;
		}
		System.out.println(aux);
	}
}

public void tSwitch(int i, int a) {
	//switch语句节点 √
	switch(i) {
	case 1:
		a = a + 1;
	case 2:
		a = a + 2;
	    break;
	default:
		a = a + 3;
	    break;
	}
	System.out.println(a);
}
//-----------trycatch---------未做------
public void tTrycatch(String s) {
	try
	{
	   s = br.readLine();
	   if (s.length() > 96)
	      throw new Exception
	          ("too long");
	} catch (IOException e) {
	   e.printStackTrace();
	} catch (Exception e) {
	   e.printStackTrace();
	}
	return (s);
}

public static void computeStats (int [ ] numbers){
	// 课件中示例
     int length = numbers.length;
     double med, var, sd, mean, sum, varsum;

     sum = 0;
     for (int i = 0; i < length; i++)
     {
          sum += numbers [ i ];
     } 
     med   = numbers [ length / 2 ];
     mean = sum / (double) length;

     varsum = 0;
     for (int i = 0; i < length; i++)
     {
          varsum = varsum  + ((numbers [i] - mean) * (numbers [i] - mean));
     }
     var = varsum / ( length - 1.0 );
     sd  = Math.sqrt ( var );

     System.out.println ("length:                   " + length);
     System.out.println ("mean:                    " + mean);
     System.out.println ("median:                 " + med);
     System.out.println ("variance:                " + var);
     System.out.println ("standard deviation: " + sd);
}

public int t(int x, int y) {
	while(x<0){
		x++;
		while(x<y){
			y--;
		}
	}
	return y;
}
public void arrayListTest(ArrayList x, ArrayList y){
	while(x.size() > y.size()){
		y.add(2);
		System.out.println(2);
	}
}
ArrayList x = new ArrayList();
x.add("1");
x.add("2");
ArrayList y = new ArrayList();
arrayListTest(x,y);
-------------------------------
ArrayList x = new ArrayList();
ArrayList y = new ArrayList();
y.add("1");
arrayListTest(x,y);

public int cal(){
	//正确
    int[] a = new int[]{0,1,-1,2,3};
    int i = 0;
    int sum = 0;
    while (i<a.length){
        if(a[i]!=0){
            if(a[i]>0){
                sum+=a[i];
            }else{
                sum-=a[i];
            }
        }
        i++;
    }
    return sum;
}

}