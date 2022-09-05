package ucm.yifei.tooltfm.model;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import ucm.yifei.tooltfm.antlr4.Java8Lexer;
import ucm.yifei.tooltfm.antlr4.Java8Parser;
import ucm.yifei.tooltfm.reflex.Define;
import ucm.yifei.tooltfm.reflex.TestRun;
import ucm.yifei.tooltfm.utils.StringUtils;
import ucm.yifei.tooltfm.visitors.Java8CustomVisitor;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CfgGenerator {
	// 用于 界面版 读取分析
    public Java8CustomVisitor analyze(String text){
        Lexer lexer = new Java8Lexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        Java8Parser.MethodDeclarationContext method = parser.methodDeclaration();

        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        Java8CustomVisitor visitor = new Java8CustomVisitor();
        visitor.initial();
        parseTreeWalker.walk(visitor, method);
        
        System.out.println("output: " + method.toStringTree(parser));
        
        visitor.pointList.add(new Point("end", null));
		while (!visitor.endAnyStruct.isEmpty()){
			if(visitor.endAnyStruct.getFirst().equals("LOOP")){
				visitor.endAnyStruct.pop();
			}
			else if(visitor.endAnyStruct.getFirst().equals("SWITCH")){
				while (!visitor.endstack.isEmpty() && visitor.endstack.getFirst().key.equals("case")){
					visitor.addEdge(visitor.endstack.getFirst().id, "end", "");
					visitor.endstack.pop();
				}
				visitor.endAnyStruct.pop();
			}
			else {
				visitor.addEdge(visitor.endstack.getFirst().id, "end", "");
				visitor.endstack.pop();
				visitor.endAnyStruct.pop();
			}
		}
		visitor.addEdge(visitor.active+"", "end", "");

        return visitor;
    }

	public void createCFGGraph(Java8CustomVisitor visitor) {
		String filePath = System.getProperty("user.dir") + "\\";
		//System.out.println(filePath);
		GraphViz gv = new GraphViz();
		gv.setdir(filePath);
		
		gv.addln(gv.start_graph());
		gv.addln("bgcolor=\"transparent\"");
		gv.addln("nodesep=\"0.7\"");
		
		String nodo = "node [shape=circle, style=\"filled\", fontcolor=dodgerblue4, fillcolor=white]";
		String edge = "edge [color=\"#5d5d5d\"]";
		gv.addln(nodo);
		gv.addln(edge);
		
		for(int i = 0; i<visitor.pointList.size(); i++) {
			if(visitor.pointList.get(i).node.equalsIgnoreCase("start")) {
				gv.addln("start [label=<<b>start</b>>,style=filled,fontcolor=white,fillcolor=\"#7a5365\"]");
			}
			else if(visitor.pointList.get(i).node.equalsIgnoreCase("end")) {
				gv.addln("end [label=<<b>end</b>>,style=filled,fontcolor=white,fillcolor=\"#7a5365\"]");
			}
			else {
				gv.addln(addNode(visitor.pointList.get(i).node,
						visitor.pointList.get(i).value));
			}
        }
		
		for(int i = 0; i<visitor.lineList.size(); i++) {
			if(!visitor.lineList.get(i).fromNode.equalsIgnoreCase("null")) {
				gv.addln(addLine(visitor.lineList.get(i).fromNode, 
						visitor.lineList.get(i).toNode));
			}
        }
		
		gv.addln(gv.end_graph());
		//显示生成的dot文件
		//System.out.println(gv.getDotSource());
		exportGraph(gv);
	}
	
	private String addLine(String p1, String p2) {
		return p1 + " -> " + p2;
	}
	
	private String addNode(String n, String v) {
		String newV = StringUtils.replaceDoubleQuotes(v);
		return n + " [label=<<b>" + n + "</b>>, xlabel=\""
					+ n + ": " + newV + "\"]";
	}
	
	public static void exportGraph(GraphViz graph) {
		String type = "png";//svg,png
		//String dir = System.getProperty("user.dir");
		File out = new File("src\\main\\resources\\static\\out." + type);   // out.gif in this example
		graph.writeGraphToFile(graph.getGraph(graph.getDotSource(), type), out);
    }

	public String EVOTestName = "";
	public String DiffblueTestName = "";

	public void createUserJavaFile(Java8CustomVisitor visitor, String code){
		String method = visitor.stringBuilder.toString()+"}";

		String className = "Test"+ (int)Math.ceil(Math.random()*1000);
		Define define = new Define(className);
		EVOTestName = className+"_ESTest";
		DiffblueTestName = className+"Test";

		TestRun testRun = new TestRun(define);
		//创建java文件
		code = code.replaceAll("public","public static");
		testRun.createJavaFile1(code);
	}
	private void createTempTestFile(Java8CustomVisitor visitor, String code){
		String className = "UserTestFile";
		Define define = new Define(className);
		TestRun testRun = new TestRun(define);
		//创建java文件
		testRun.createJavaFileTest(code);
	}

	/**
	 * 计算覆盖情况(人为输入测试用例)
	 */
	public String calculateCU(Java8CustomVisitor visitor, TRpath trPaths, String testCode) throws IOException {
		createTempTestFile(visitor, testCode);

		String mName = visitor.getMethodName();
		mName = mName.substring(0,mName.length()-1);
		//查找测试文件并切割
		BufferedReader in = new BufferedReader(new FileReader(
				"UserTestFile.java"));;

		return calculateCoverage(visitor, mName, in, trPaths);
	}

	private final String spliteLine = "--------------------\n";

	/**
	 * 计算使用测试工具生成的测试集的TR的覆盖率和覆盖情况
	 * @param visitor 经过Antlr分析后的代码
	 * @param testTool 测试工具
	 * @param trPaths 被测代码的需求路径
	 * @return
	 * @throws IOException
	 */
	public String calculateCT(Java8CustomVisitor visitor, String testTool, TRpath trPaths) throws IOException {
		String mName = visitor.getMethodName();
		mName = mName.substring(0,mName.length()-1);
		//查找测试文件并切割
		BufferedReader in;
		if (testTool.equals("EVO")){
			in = new BufferedReader(new FileReader(
					"D:\\IDEA\\toolTFM\\src\\evo\\ucm\\yifei\\tooltfm\\example\\"
					+EVOTestName +".java"));
					//"D:\\IDEA\\toolTFM\\src\\test\\java\\ucm\\yifei\\tooltfm\\ +"Test21_ESTest.java"));
		}
		else{
			in = new BufferedReader(new FileReader("D:\\IDEA\\toolTFM\\src\\test\\java\\ucm\\yifei\\tooltfm\\example\\"
					+DiffblueTestName +".java"));
					//+"Test21Test.java"));
		}
		return calculateCoverage(visitor, mName, in, trPaths);
	}

	private String calculateCoverage(Java8CustomVisitor visitor, String mName, BufferedReader in, TRpath trPaths) throws IOException {
		List<String> codeList;
		List<List<String>> testList;
		testList = StringUtils.splitTestFile(in);

		for (int i = 0; i < testList.size(); i++) {
			List<String> codeBlock = testList.get(i);
			codeList = new ArrayList<>();
			for (int j = 0; j < codeBlock.size(); j++){
				String str = codeBlock.get(j);
				if (str.startsWith("assert")){
					if (str.contains("."+ mName)){//assert开头，并呼叫函数
						str = modifyCode(str, 1, mName);
						codeList.add(str+"\n");
						codeList.add(spliteLine);
					}
					//else: assert开头，但没有呼叫函数 => 无视
				}
				else {
					if (str.contains("." + mName)){//不是assert开头，但是呼叫了函数
						str = modifyCode(str, 2, mName);
						codeList.add(str+"\n");
						codeList.add(spliteLine);
					}
					else {//不是assert开头且没有呼叫函数 => 如参数声明
						//包含测试文件名通常为调用被测试的class，如: Test21 test21_0 = new Test21(); [该代码最终不需要
						if (!str.contains("Test21") && StringUtils.noStartWith(str)){//不是junit包中句子(测试语句无法生成class)
							codeList.add(str+"\n");
						}
					}
				}
			}
			testList.set(i, codeList);
		}
		//-------------------------------------------------------------
		String realPath="";
		String fgResult = "";	//覆盖路径
		String wfgResult = "";	//未覆盖路径
		StringBuilder strResult = new StringBuilder();
		String method = visitor.stringBuilder.toString()+"}";
		int c = 0;
		for (List<String> s : testList) {
			System.out.println(c);
			realPath="";fgResult = "";wfgResult = "";

			String testCode = "";
			for (String value : s) {
				testCode += value;
			}

			List<String> listTestcode = StringUtils.splitByLine(testCode);
			if (listTestcode.size()>1){
				listTestcode.remove(listTestcode.size()-1);
			}
			for (String subT : listTestcode) {
				String className = "Test"+ (int)Math.ceil(Math.random()*1000);
				Define define = new Define(className);

				String cmd = " arrayList.add(\"start\");  "+ subT +" arrayList.add(\"end\");return arrayList;";
				//String cmd = " "+ subT;
				TestRun testRun = new TestRun(define);
				//创建java文件
				testRun.createJavaFile(cmd, method);
				//编译
				if (testRun.makeJavaFile2() == 0) {
					realPath = reflex(testRun, realPath);
					//System.out.println(realPath);
				}
			}
			strResult.append(resultCov(trPaths, realPath, fgResult, wfgResult, c));
			strResult.append(spliteLine);
			c++;
		}

		return strResult.toString();
	}
	private String modifyCode(String str, int c, String mName){//1:assest+M 2:不是assest+M
		String []l = StringUtils.split(str, mName);
		String newStr="";
		if (c == 1){
			String subL = l[l.length-1];
			subL = subL.substring(0,subL.length()-2);//长度待定
			newStr = mName + subL +";";
		}
		else {
			if (str.contains("=")){//int[] actualMoverUnoResult = (new Test280()).moverUno(new int[]{1, 1, 1, 1});
				String []r = StringUtils.split(str, "=");
				newStr = r[0] + " = " + mName + l[l.length-1];
			}
			else{//test21_0.moverUno((int[]) null);或(new Test21()).moverUno(new int[]{});
				newStr = mName + l[l.length-1];
			}
		}
		return newStr;
	}
	private String reflex(TestRun testRun, String realPath){
		//反射执行
		ArrayList res =  testRun.run();
		ArrayList arrayList = new ArrayList();
		arrayList.add(res.get(0));
		for (int i = 0; i < res.size(); i++) {
			if(arrayList.get(arrayList.size()-1).equals((res.get(i)+"").trim())){

			}else{
				arrayList.add((res.get(i)+"").trim());
			}
		}
		realPath += "        "+arrayList.toString().replaceAll("\\[","").replaceAll("]","")+"\n";
		return realPath;
	}

	private String resultCov(TRpath trPaths, String realPath, String fgResult, String wfgResult, int c){
		List<List<String>> pathList = trPaths.getTrList();
		for (int i = 0; i <pathList.size() ; i++) {
			List<String> path = pathList.get(i);
			//                System.out.println(path.toString());
			String paStr = path.toString().replaceAll("\\[","").replaceAll("]","");
			if(realPath.indexOf(paStr)>-1){
				fgResult+="trPath" + (i+1) + ", ";
			}else{
				wfgResult+="trPath" + (i+1) + ", ";
			}
		}

		DecimalFormat df = new DecimalFormat("0.00%");
		String percent = df.format((float)(StringUtils.split(fgResult, ",").length-1)/(pathList.size()));
		String subResult = "Test" + c +": \n"
				//+ "Camino de ejecución: " + "\n" + realPath
				+ "Caminos cubiertos: " + percent +"\n        " + fgResult + "\n"
				+ "Caminos no cubiertos: " + "\n        " + wfgResult +" \n";
		System.out.println(subResult);
		return subResult;
	}
}