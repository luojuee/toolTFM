package ucm.yifei.tooltfm.visitors;

import ucm.yifei.tooltfm.antlr4.Java8Parser;
import ucm.yifei.tooltfm.antlr4.Java8ParserBaseListener;
import ucm.yifei.tooltfm.model.GraStack;
import ucm.yifei.tooltfm.model.Line;
import ucm.yifei.tooltfm.model.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Java8CustomVisitor extends Java8ParserBaseListener {
	public List<Point> pointList = new ArrayList<>();
	public List<Line> lineList = new ArrayList<>();
	public StringBuilder stringBuilder = new StringBuilder();
	//存放开始结构的开始节点，如(1,if,Condition)
	public static LinkedList<GraStack> stack = new LinkedList<>();
	//存放结构结束节点(结束但未被连接). if, ifelse, ifthen??
	public static LinkedList<GraStack> endstack = new LinkedList<>();
	
	public String active = "start"; //刚处理完的节点，起始节点为start
	private int num = 0; //当前处理的节点，0表示start节点
	public static LinkedList<String> endAnyStruct = new LinkedList<>(); //存放结束的结构的结束节点，如IF, IFTHENELSE, LOOP

	private int tempNodeId = 0; //临时节点id

	public void initial() {
		pointList = new ArrayList<Point>();
		pointList.add(new Point("start",null));
		lineList = new ArrayList<Line>();
		stringBuilder = new StringBuilder();

		stack = new LinkedList<GraStack>();
		endstack = new LinkedList<GraStack>();
		active = "start";
	}
	//--------------------------------------------------------------------
	private void createNode(int num, String nodetype, String nodevalue) {
		pointList.add(new Point(num+"", nodetype, nodevalue));
	}
	public void addEdge(String start, String end, String msg) {
		if (start.equals("start")){
			tempNodeId = 0;
		}
		else {
			tempNodeId = Integer.parseInt(start);
		}

		if (!pointList.get(tempNodeId).msg.equals("return") &&
				!pointList.get(tempNodeId).msg.equals("throw") &&
				!pointList.get(tempNodeId).msg.equals("break") &&
				!pointList.get(tempNodeId).msg.equals("continue")){
			lineList.add(new Line(start, end, msg));
		}

	}

	/**
	 * 赋值语句的节点创建;
	 * 结构中：1.不是第一个赋值语句 2.可能在某些结构结束之后，
	 * 连接前判断是否和上一处理节点同type(是否合并),判断endstack是否为空是否是if形结束的下一个节点
	 * @param type VariableDeclaration or Expression
	 * @param value ctx.getText()
	 */
	private void createSimpleNode(String type, String value){
		if (endAnyStruct.isEmpty()){//简单句
			int pontPre = Integer.parseInt(active);
			pointList.get(pontPre).value = pointList.get(pontPre).value + " \n " + value;
			if(!type.equals("ForInit") && !type.equals("forUpdate")){
				setMsg(value);
			}
		}
		else{
			num++;
			createNode(num, type, value);
			setCode(num+"");
			if(!type.equals("ForInit") && !type.equals("forUpdate")){
				setMsg(value);
			}

			while (!endAnyStruct.isEmpty()){
				if(endAnyStruct.getFirst().equals("LOOP")){
					endAnyStruct.pop();
				}
				else if(endAnyStruct.getFirst().equals("SWITCH")){
					while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
					}
					endAnyStruct.pop();
				}
				else {
					addEdge(endstack.getFirst().id, num+"", "");
					endstack.pop();
					endAnyStruct.pop();
				}
			}
			addEdge(active, num+"", "");
		}
		active = num+"";
	}

	/**
	 * 判断是否在结构中，结构包括if, ifelse, ifthen, while
	 * @return true在结构中, false不在
	 */
	public boolean isInStructure(){
		return !stack.isEmpty();
	}
	//--------------------------------------------------------------------
	@Override
	public void enterLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx) {
		String expression = "";
		Boolean noVar = false;
		String tempStr0 = ctx.variableDeclaratorList().variableDeclarator(0).variableDeclaratorId().getText();
		String tempStr1 = "";
		try {
			tempStr1 = ctx.variableDeclaratorList().variableDeclarator(0).variableInitializer().getText();
		}
		catch (Exception e){
			noVar = true;
		}
		if (noVar==false && tempStr1.length()>3){
			if (tempStr1.startsWith("new")){
				tempStr1 = tempStr1.substring(3);
				expression = ctx.unannType().getText() + " " + tempStr0 + " = new " + tempStr1 + ";";
			}
			else expression = ctx.unannType().getText() + " " + ctx.variableDeclaratorList().getText() + ";";
		}
		else expression = ctx.unannType().getText() + " " + ctx.variableDeclaratorList().getText() + ";";

		//是否在结构中
		if(isInStructure()){//在结构中
			if(stack.getFirst().isFirst){//是开始句
				//有一些问题如for(x=0; ;){int i}这样的会少一个
				if(stack.getFirst().content.equals("for")){
					if (forState==1){
						//为for(int x=0; ;){int i}这样的
						forState = -1;//对第一个int x=0不做处理
					}
					else{
						//for(; ;){int i} 对第二个int 处理
						num++;
						createNode(num, "VariableDeclaration", expression);
						addEdge(active, num+"", "");
						active = num+"";
						stack.getFirst().isFirst = false;
						setCode(active);
						setMsg(expression);
					}
				}
				else{
					num++;
					createNode(num, "VariableDeclaration", expression);
					//判断是否Ifelse结构中, 避免从ifthen分支连接过来
					if (stack.getFirst().key.equals("Ifelse")){
						addEdge(stack.getFirst().id, num+"", "");
					}
					else addEdge(active, num+"", "");
					active = num+"";
					stack.getFirst().isFirst = false;
					setCode(active);
					setMsg(expression);
				}
			}
			else{//不是开始句
				createSimpleNode("VariableDeclaration", expression);
			}
		}
		else {//不在结构中
			if(active.equals("start")){
				num++;
				createNode(num, "VariableDeclaration", expression);
				addEdge(active, num+"", "");
				active = num+"";
				setCode(active);
				setMsg(expression);
			}
			else{
				createSimpleNode("VariableDeclaration", expression);
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, active, "break"));
					breakNode.pop();
				}
			}

		}
	}

	@Override
	public void enterIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
		num++;
		createNode(num, "IfCondition", "If");//ctx.expression().getText()
		setCode(num+"");
		setMsg(ctx.IF().getText()+"("+ctx.expression().getText()+")"+"{");
		//判断是否在结构中
		if(isInStructure()){//结构中
			//判断前方是否有语句
			if (stack.getFirst().isFirst){
				stack.getFirst().isFirst = false;
				stack.push(new GraStack(num+"", "If", stack.getFirst().id));

				if (stack.get(1).key.equals("Ifelse")){
					addEdge(stack.get(1).id, num+"", "");
				}
				else addEdge(active, num+"", "");
			}
			else{//判断前方结束的是什么
				stack.push(new GraStack(num+"", "If", stack.getFirst().id));
				if (endAnyStruct.isEmpty()){//简单句
					addEdge(active, num+"", "");
				}
				else{
					while (!endAnyStruct.isEmpty()){
						if(endAnyStruct.getFirst().equals("LOOP")){
							endAnyStruct.pop();
						}
						else if(endAnyStruct.getFirst().equals("SWITCH")){
							while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
								addEdge(endstack.getFirst().id, num+"", "");
								endstack.pop();
							}
							endAnyStruct.pop();
						}
						else {
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
							endAnyStruct.pop();
						}
					}
					addEdge(active, num+"", "");
				}
			}
		}
		else{//结构外
			stack.push(new GraStack(num+"", "If", ""));
			//不是第一个语句，则需要考虑之前结束的链接
			if(active.equals("start")){
				addEdge(active, num+"", "");
			}
			else{
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, num+"", "break"));
					breakNode.pop();
				}
				addEdge(active, num+"", "");
			}

		}

		active = num+"";
	}
	@Override
	public void exitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
		//push if(false)分支: If.id的节点。
		setMsg(" }");
		//setCode(stack.getFirst().id);

		endstack.push(new GraStack(stack.getFirst().id, "If", stack.getFirst().inWhichStruct));
		endAnyStruct.push("IF");
		stack.pop();
	}
	@Override
	public void enterIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
		setCode(num+1+"");
		setMsg(ctx.IF().getText()+"("+ctx.expression().getText()+")"+"{");
	}
	@Override
	public void enterStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx){
		num++;
		createNode(num, "IfCondition", "If");
		//判断是否在结构中
		if(isInStructure()){//结构中
			//判断前方是否有语句
			if (stack.getFirst().isFirst){
				stack.getFirst().isFirst = false;
				stack.push(new GraStack(num+"", "Ifthen", "Condition"));

				if (stack.get(1).key.equals("Ifelse")){
					addEdge(stack.get(1).id, num+"", "");
				}
				else addEdge(active, num+"", "");
			}
			else{//判断前方结束的是什么
				stack.push(new GraStack(num+"", "Ifthen", stack.getFirst().id));
				if (endAnyStruct.isEmpty()){//简单句
					addEdge(active, num+"", "");
				}
				else{
					while (!endAnyStruct.isEmpty()){
						if(endAnyStruct.getFirst().equals("LOOP")){
							endAnyStruct.pop();
						}
						else if(endAnyStruct.getFirst().equals("SWITCH")){
							while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
								addEdge(endstack.getFirst().id, num+"", "");
								endstack.pop();
							}
							endAnyStruct.pop();
						}
						else {
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
							endAnyStruct.pop();
						}
					}
					addEdge(active, num+"", "");
				}
			}
		}
		else{//结构外
			stack.push(new GraStack(num+"", "Ifthen", ""));
			//不是第一个语句，则需要考虑之前结束的链接
			if(active.equals("start")){
				addEdge(active, num+"", "");
			}
			else{
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, num+"", "break"));
					breakNode.pop();
				}
				addEdge(active, num+"", "");
			}

		}

		active = num+"";
	}
	@Override
	public void exitStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx){
		endstack.push(new GraStack(active, "IFTHENELSE"));
		stack.push(new GraStack(stack.getFirst().id, "Ifelse", "Ifelse"));
		setMsg(" }");
		setMsg("else{");
	}
	@Override
	public void exitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
		//endstack.push(new GraStack(active, "Ifelse", "Ifelse"));
		endAnyStruct.push("IFTHENELSE");
		stack.pop();
		stack.pop();

		setMsg(" }");
	}

	@Override
	public void enterStatementWithoutTrailingSubstatement(Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
		if(ctx.block() == null){
			String type = "";
			if(!stack.isEmpty()){
				type = stack.getFirst().key;
			}
			//String s = ctx.getChild(0).getChildCount()+ " "+ ctx.getChild(0).getChild(0).getText();
			if (ctx.getChild(0).getChildCount()>=2 && ctx.getChild(0).getChild(0).getText().equals("return")){
				System.out.println("Return Statement");
			}
			else if (ctx.getChild(0).getChildCount()>=2 && ctx.getChild(0).getChild(0).getText().equals("throw")){
				System.out.println("Throw Statement");
			}
			else if (ctx.getChild(0).getChildCount()>=2 && ctx.getChild(0).getChild(0).getText().equals("switch")){
				System.out.println("Switch Statement");
			}
			else if (ctx.getChild(0).getChildCount()>=2 && ctx.getChild(0).getChild(0).getText().equals("break")){
					System.out.println("Break Statement");
			}
			else if (ctx.getChild(0).getChildCount()>=2 && ctx.getChild(0).getChild(0).getText().equals("continue")){
				System.out.println("Continue Statement");
			}
			else {
				if(type.equals("While") || type.equals("If")||type.equals("Ifthen")){
					if(stack.getFirst().isFirst){
						num++;
						createNode(num,"Expression", ctx.getText());
						addEdge(active, num+"", "");
						active = num+"";
						stack.getFirst().isFirst = false;

						setCode(active);
						setMsg(ctx.getText());
					}
					else{
						createSimpleNode("Expression", ctx.getText());
					}
				}
				else if(type.equals("Ifelse")){
					if(stack.getFirst().isFirst && stack.getFirst().id.equals(stack.get(1).id)){
						num++;
						createNode(num,"Expression", ctx.getText());
						addEdge(stack.getFirst().id, num+"", "");
						active = num+"";
						//stack.getFirst().id = active;
						stack.getFirst().isFirst = false;
						//setMsg("else {");
						setCode(active);
						setMsg(ctx.getText());
					}
					else{
						createSimpleNode("Expression", ctx.getText());
						//stack.getFirst().id = active;
					}
				}
				else if(type.equals("case")){
					if(stack.getFirst().isFirst){
						num++;
						createNode(num,"Expression", ctx.getText());
						String label = ctx.getText();
						active = num+"";
						stack.getFirst().isFirst = false;
						setCode(active);
						setMsg(ctx.getText());
					}
					else{
						createSimpleNode("Expression", ctx.getText());
					}
				}
				else{//不在结构中的赋值语句(非声明变量
					if (ctx.getChild(0).getChildCount()>2){
						if (ctx.getChild(0).getChild(0).getText().equals("do")){
							System.out.println("do statement");
						}
					}else{
						//判断是否是在结构后
						if(active.equals("start")){
							num++;
							createNode(num, "Expression", ctx.getText());
							addEdge(active, num+"", "");
							active = num+"";

							setCode(active);
							setMsg(ctx.getText());
						}
						else{
							createSimpleNode("Expression", ctx.getText());
							//处理从循环中跳出的break
							while (!breakNode.isEmpty()){
								lineList.add(new Line(breakNode.getFirst().id, active, "break"));
								breakNode.pop();
							}
						}
					}
				}
			}
		}
	}
	private int forState = -1;
	@Override
	public void enterBasicForStatement(Java8Parser.BasicForStatementContext ctx) {
		//判断是否在结构中
		if(isInStructure()){//结构中
			//判断前方是否有语句
			if (stack.getFirst().isFirst){
				num++;
				createNode(num, "ForInit", "for: " + ctx.forInit().getText());
				setCode(num+"");
				//判断是否Ifelse结构中, 避免从ifthen分支连接过来
				if (stack.getFirst().key.equals("Ifelse")){
					addEdge(stack.getFirst().id, num+"", "");
				}
				else addEdge(active, num+"", "");
				//active = num+"";
				stack.getFirst().isFirst = false;
			}
			else{//判断前方结束的是什么
				createSimpleNode("ForInit", "for: " + ctx.forInit().getText());
			}
		}
		else{//结构外
			//不是第一个语句，则需要考虑之前结束的链接
			if(active.equals("start")){
				num++;
				createNode(num, "ForInit", "for: " + ctx.forInit().getText());
				setCode(num+"");
				addEdge(active, num+"", "");
			}
			else{
				createSimpleNode("ForInit", "for: " + ctx.forInit().getText());
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, num+"", "break"));
					breakNode.pop();
				}
			}

		}
		active = num+"";
		num++;
		createNode(num, "While", "for");
		setCode(num+"");
		String strDecla = "", str = "";
		forState = 1;
		try{
			strDecla = ctx.forInit().localVariableDeclaration().getText();
		}catch(Exception e){
			str = ctx.forInit().getText() +"; "+ ctx.expression().getText() +"; "+ ctx.forUpdate().getText();
			forState = 2;
		}
		if (forState == 1){
			str = ctx.forInit().localVariableDeclaration().unannType().getText()
					+ " " + ctx.forInit().localVariableDeclaration().variableDeclaratorList().getText() + ";" +ctx.expression().getText()
					+ "; "+ ctx.forUpdate().getText();
		}
		setMsg(ctx.FOR().getText()+"("+ str +"){");
		setCode(num+"");

		GraStack newGraStack = new GraStack(num+"", "While", "");
		newGraStack.setContent("for");
		stack.push(newGraStack);
		addEdge(active, num+"", "ForConditionalExpression");
		active = num+"";
	}
	@Override
	public void exitBasicForStatement(Java8Parser.BasicForStatementContext ctx) {
		//create forUpdate node. ej: for(int i; i<length; i++), i++
		if (!continueNode.isEmpty()){
			//处理从循环中的continue, 跳过循环体中余下的语句，对for语句中的“表达式3”求值，然后进行“表达式2”的条件测试
			num++;
			createNode(num, "forUpdate", ctx.forUpdate().getText());
			setCode(num+"");
			while (!endAnyStruct.isEmpty()){
				if(endAnyStruct.getFirst().equals("LOOP")){
					endAnyStruct.pop();
				}
				else if(endAnyStruct.getFirst().equals("SWITCH")){
					while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
					}
					endAnyStruct.pop();
				}
				else {
					addEdge(endstack.getFirst().id, num+"", "");
					endstack.pop();
					endAnyStruct.pop();
				}
			}
			addEdge(active, num+"", "");
			active = num + "";

			while (!continueNode.isEmpty()){
				lineList.add(new Line(continueNode.getFirst().id, active, "continue"));
				continueNode.pop();
			}
		}
		else{
			createSimpleNode("forUpdate", ctx.forUpdate().getText());
		}
		//正常loop
		addEdge(active, stack.getFirst().id, "");
		while (!endAnyStruct.isEmpty()){
			if(endAnyStruct.getFirst().equals("LOOP")){
				endAnyStruct.pop();
			}
			else if(endAnyStruct.getFirst().equals("SWITCH")){
				while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
					addEdge(endstack.getFirst().id, num+"", "");
					endstack.pop();
				}
				endAnyStruct.pop();
			}
			else {
				addEdge(endstack.getFirst().id, stack.getFirst().id, "");
				endstack.pop();
				endAnyStruct.pop();
			}
		}
		endAnyStruct.push("LOOP");
		active = stack.getFirst().id;
		stack.pop();

		setCode(active);
		setMsg(" }");
	}

	@Override
	public void enterDoStatement(Java8Parser.DoStatementContext ctx) {
		stack.push(new GraStack(num+1+"", "While"));
		setMsg(ctx.DO().getText()+"{");
	}
	private LinkedList<String> tempEndAnyStruct = new LinkedList<>();
	private LinkedList<GraStack> tempEndstack = new LinkedList<>();
	@Override
	public void exitDoStatement(Java8Parser.DoStatementContext ctx) {
		tempEndAnyStruct.addAll(endAnyStruct);
		tempEndstack.addAll(endstack);
		//处理从循环中的continue
		while (!continueNode.isEmpty()){
			lineList.add(new Line(continueNode.getFirst().id, stack.getFirst().id, "continue"));
			continueNode.pop();
		}
		//正常loop
		addEdge(active, stack.getFirst().id, "");
		while (!tempEndAnyStruct.isEmpty()){
			if(tempEndAnyStruct.getFirst().equals("LOOP")){
				tempEndAnyStruct.pop();
			}
			else if(endAnyStruct.getFirst().equals("SWITCH")){
				while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
					addEdge(endstack.getFirst().id, num+"", "");
					endstack.pop();
				}
				endAnyStruct.pop();
			}
			else {
				addEdge(tempEndstack.getFirst().id, stack.getFirst().id, "");
				tempEndstack.pop();
				tempEndAnyStruct.pop();
			}
		}
		endAnyStruct.push("LOOP");
		//active = stack.getFirst().id;
		setMsg(" }"+ctx.WHILE().getText()+"("+ctx.expression().assignmentExpression().getText()+");");
		stack.pop();
	}
	@Override
	public void enterWhileStatement(Java8Parser.WhileStatementContext ctx) {
		num++;
		createNode(num, "While", "while("+ctx.expression().getText()+")");
		//判断是否在结构中
		if(isInStructure()){//结构中
			//判断前方是否有语句
			if (stack.getFirst().isFirst){
				stack.getFirst().isFirst = false;
				stack.push(new GraStack(num+"", "While"));

				if (stack.get(1).key.equals("Ifelse")){
					addEdge(stack.get(1).id, num+"", "");
				}
				else addEdge(active, num+"", "");
			}
			else{//判断前方结束的是什么
				stack.push(new GraStack(num+"", "While", stack.getFirst().id));
				if (endAnyStruct.isEmpty()){//简单句
					addEdge(active, num+"", "");
				}
				else{
					while (!endAnyStruct.isEmpty()){
						if(endAnyStruct.getFirst().equals("LOOP")){
							endAnyStruct.pop();
						}
						else if(endAnyStruct.getFirst().equals("SWITCH")){
							while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
								addEdge(endstack.getFirst().id, num+"", "");
								endstack.pop();
							}
							endAnyStruct.pop();
						}
						else {
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
							endAnyStruct.pop();
						}
					}
					addEdge(active, num+"", "");
				}
			}
		}
		else{//结构外
			stack.push(new GraStack(num+"", "While", ""));
			//不是第一个语句，则需要考虑之前结束的链接
			if(active.equals("start")){
				addEdge(active, num+"", "");
			}
			else{
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, num+"", "break"));
					breakNode.pop();
				}
				addEdge(active, num+"", "");
			}

		}

		active = num+"";
		setCode(active);
		setMsg(ctx.WHILE().getText()+ctx.LPAREN().getText()+ctx.getChild(2).getText()+ctx.RPAREN().getText()+"{");
		setCode(active);
	}
	@Override
	public void exitWhileStatement(Java8Parser.WhileStatementContext ctx) {
		//处理从循环中的continue
		while (!continueNode.isEmpty()){
			lineList.add(new Line(continueNode.getFirst().id, stack.getFirst().id, "continue"));
			continueNode.pop();
		}
		//正常loop
		addEdge(active, stack.getFirst().id, "");
		while (!endAnyStruct.isEmpty()){
			if(endAnyStruct.getFirst().equals("LOOP")){
				endAnyStruct.pop();
			}
			else if(endAnyStruct.getFirst().equals("SWITCH")){
				while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
					addEdge(endstack.getFirst().id, num+"", "");
					endstack.pop();
				}
				endAnyStruct.pop();
			}
			else {
				addEdge(endstack.getFirst().id, stack.getFirst().id, "");
				endstack.pop();
				endAnyStruct.pop();
			}
		}
		endAnyStruct.push("LOOP");
		active = stack.getFirst().id;
		stack.pop();

		setCode(active);
		setMsg(" }");
	}

	@Override
	public void enterReturnStatement(Java8Parser.ReturnStatementContext ctx){
		num++;
		String str = "" + ctx.getText().substring(6);
		if(!str.equals(";")){
			str = " "+str;
		}
		createNode(num, "Return", ctx.RETURN().getText() + str);
		pointList.get(num).setMsg("return");
		setCode(num+"");
		setMsg(ctx.RETURN().getText() + str);

		//是否在结构中
		if(isInStructure()){//在结构中
			if(stack.getFirst().isFirst){//是开始句
				//判断是否Ifelse结构中, 避免从ifthen分支连接过来
				if (stack.getFirst().key.equals("Ifelse")){
					addEdge(stack.getFirst().id, num+"", "");
				}
				else addEdge(active, num+"", "");
				stack.getFirst().isFirst = false;
			}
			else{//不是开始句
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				addEdge(active, num+"", "");
			}
		}
		else {//不在结构中
			if(active.equals("start")){
				addEdge(active, num+"", "");
			}
			else{
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, num+"", "break"));
					breakNode.pop();
				}
				addEdge(active, num+"", "");
			}
		}

		active = num+"";
		lineList.add(new Line(active, "end", "return"));
		//addEdge(active, "end", "return");
	}

	@Override
	public void enterThrowStatement(Java8Parser.ThrowStatementContext ctx) {
		num++;
		String str = "throw new " + ctx.expression().getText().substring(3);
		createNode(num, "Return", str);//throw: same type with return,after throw jump to the end
		pointList.get(num).setMsg("throw");
		setCode(num+"");
		setMsg("return null;");
		//setMsg(str+";");

		//是否在结构中
		if(isInStructure()){//在结构中
			if(stack.getFirst().isFirst){//是开始句
				//判断是否Ifelse结构中, 避免从ifthen分支连接过来
				if (stack.getFirst().key.equals("Ifelse")){
					addEdge(stack.getFirst().id, num+"", "");
				}
				else addEdge(active, num+"", "");
				stack.getFirst().isFirst = false;
			}
			else{//不是开始句
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				addEdge(active, num+"", "");
			}
		}
		else {//不在结构中
			if(active.equals("start")){
				addEdge(active, num+"", "");
			}
			else{
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, num+"", "break"));
					breakNode.pop();
				}
				addEdge(active, num+"", "");
			}
		}

		active = num+"";
		lineList.add(new Line(active, "end", "throw"));
	}

	@Override
	public void enterSwitchStatement(Java8Parser.SwitchStatementContext ctx) {
		num++;
		createNode(num, "Switch", "switch("+ctx.expression().getText()+")");
		setCode(num+"");
		setMsg(" switch (" + ctx.expression().getText() + "){");
		//判断是否在结构中
		if(isInStructure()){//结构中
			//判断前方是否有语句
			if (stack.getFirst().isFirst){
				stack.getFirst().isFirst = false;
				stack.push(new GraStack(num+"", "Switch", stack.getFirst().id));

				if (stack.get(1).key.equals("Ifelse")){
					addEdge(stack.get(1).id, num+"", "");
				}
				else addEdge(active, num+"", "");
			}
			else{//判断前方结束的是什么
				stack.push(new GraStack(num+"", "Switch", stack.getFirst().id));
				if (endAnyStruct.isEmpty()){//简单句
					addEdge(active, num+"", "");
				}
				else{
					while (!endAnyStruct.isEmpty()){
						if(endAnyStruct.getFirst().equals("LOOP")){
							endAnyStruct.pop();
						}
						else if(endAnyStruct.getFirst().equals("SWITCH")){
							while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
								addEdge(endstack.getFirst().id, num+"", "");
								endstack.pop();
							}
							endAnyStruct.pop();
						}
						else {
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
							endAnyStruct.pop();
						}
					}
					addEdge(active, num+"", "");
				}
			}
		}
		else{//结构外
			stack.push(new GraStack(num+"", "Switch", ""));
			//不是第一个语句，则需要考虑之前结束的链接
			if(active.equals("start")){
				addEdge(active, num+"", "");
			}
			else{
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+"", "");
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				//处理从循环中跳出的break
				while (!breakNode.isEmpty()){
					lineList.add(new Line(breakNode.getFirst().id, num+"", "break"));
					breakNode.pop();
				}
				addEdge(active, num+"", "");
			}
		}

		active = num+"";
	}
	@Override
	public void exitSwitchStatement(Java8Parser.SwitchStatementContext ctx) {
		setMsg(" }");
		//endstack.push(new GraStack(stack.getFirst().id, "Switch", stack.getFirst().inSwitchStruct));
		if (stack.getFirst().key.equals("case")){//最后一个case没有break结尾
			stack.pop();
			while (!endCase.isEmpty()){
				endstack.push(endCase.pop());
			}
		}
		else {
			while (!endCase.isEmpty()){
				if (endCase.getFirst().id.equals(active)){
					endCase.pop();
				}
				else endstack.push(endCase.pop());
			}
		}
		endAnyStruct.push("SWITCH");

		stack.pop();
	}
	@Override
	public void enterSwitchLabels(Java8Parser.SwitchLabelsContext ctx) {
		//判断前方是否有case语句
		if (stack.getFirst().key.equals("case")){//上一个case结束没有break
			//判断上一个case结束时是什么语句
			if (endAnyStruct.isEmpty()){//简单句
				addEdge(active, num+1+"", ctx.getText());
			}
			else{
				while (!endAnyStruct.isEmpty()){
					if(endAnyStruct.getFirst().equals("LOOP")){
						endAnyStruct.pop();
					}
					else if(endAnyStruct.getFirst().equals("SWITCH")){
						while (!endstack.isEmpty() && endstack.getFirst().key.equals("case")){
							addEdge(endstack.getFirst().id, num+1+"", "");
							endstack.pop();
						}
						endAnyStruct.pop();
					}
					else {
						addEdge(endstack.getFirst().id, num+1+"", ctx.getText());
						endstack.pop();
						endAnyStruct.pop();
					}
				}
				addEdge(active, num+1+"", "");
			}
			stack.pop();
			addEdge(stack.getFirst().id, num+1+"", ctx.getText());
			stack.push(new GraStack(num+1+"","case", stack.getFirst().id));
			if (ctx.getChild(0).getChild(0).getText().equals("default")){
				setMsg("default:");
			}
			else{
				setMsg("case "+ ctx.getChild(0).getChild(1).getText()+":");
			}
		}
		else{
			addEdge(stack.getFirst().id, num+1+"", ctx.getText());
			stack.push(new GraStack(num+1+"","case", stack.getFirst().id));
			if (ctx.getChild(0).getChild(0).getText().equals("default")){
				setMsg("default:");
			}
			else{
				setMsg("case "+ ctx.getChild(0).getChild(1).getText()+":");
			}
		}

	}

	private static LinkedList<GraStack> endCase = new LinkedList<GraStack>();
	private static LinkedList<GraStack> breakNode = new LinkedList<GraStack>();
	private static LinkedList<GraStack> continueNode = new LinkedList<GraStack>();
	@Override
	public void enterBreakStatement(Java8Parser.BreakStatementContext ctx){
		//1.break出现在switch语句体内
		if (stack.getFirst().key.equals("case")){
			createSimpleNode("Break", ctx.getText());
			endCase.push(new GraStack(active,"case", stack.getFirst().id));
			stack.pop();
		}
		else{//2.break出现在不为switch的循环体中
			//创建break的单独节点
			if(stack.getFirst().isFirst){//是开始句
				//判断是否Ifelse结构中, 避免从ifthen分支连接过来
				num++;
				createNode(num, "Break", ctx.getText());
				if (stack.getFirst().key.equals("Ifelse")){
					addEdge(stack.getFirst().id, num+"", "");
				}
				else addEdge(active, num+"", "");
				active = num+"";
				stack.getFirst().isFirst = false;
				setCode(active);
				setMsg(ctx.getText());
			}
			else{//不是开始句
				createSimpleNode("Break", ctx.getText());
			}
			pointList.get(num).setMsg("break");
			//储存点,用于结构外的第一句的连接
			breakNode.push(new GraStack(active,"Break", stack.getFirst().id));
		}
	}
	@Override
	public void enterContinueStatement(Java8Parser.ContinueStatementContext ctx) {
		if(stack.getFirst().isFirst){//是开始句
			//判断是否Ifelse结构中, 避免从ifthen分支连接过来
			num++;
			createNode(num, "Continue", ctx.getText());
			if (stack.getFirst().key.equals("Ifelse")){
				addEdge(stack.getFirst().id, num+"", "");
			}
			else addEdge(active, num+"", "");
			active = num+"";
			stack.getFirst().isFirst = false;
			setCode(active);
			setMsg(ctx.getText());
		}
		else{//不是开始句
			createSimpleNode("Continue", ctx.getText());
		}
		pointList.get(num).setMsg("continue");
		//储存点,用于while, do-while, for 的连接
		continueNode.push(new GraStack(active,"Continue", stack.getFirst().id));
	}

	//---------------------用于覆蓋率部分--------------------------------
	private String mName="";
	public String getMethodName(){
		return mName;
	}
	@Override public void enterMethodHeader(Java8Parser.MethodHeaderContext ctx) {
		stringBuilder.append(" public static " + ctx.result().getText() +" ");
	}
	@Override public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
		mName = ctx.Identifier().getText() +"(";
		stringBuilder.append(mName);
	}
	@Override public void exitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
		stringBuilder.append(" ){");
	}
	@Override public void enterFormalParameterList(Java8Parser.FormalParameterListContext ctx) {
		if (ctx.children.size() > 1){
			//ctx child[0]
			for (int i = 0; i < ctx.getChild(0).getChildCount(); i++) {
				if (ctx.getChild(0).getChild(i).getText().equals(",")){
					stringBuilder.append(", ");
				}
				else{
					String chi0 = ctx.getChild(0).getChild(i).getChild(0).getText()+" "
							+ ctx.getChild(0).getChild(i).getChild(1).getText();
					stringBuilder.append(chi0);
				}
			}
			//ctx child[1] ,
			stringBuilder.append(", ");
			//ctx child[2] lastParameter
			String chi2 = ctx.lastFormalParameter().getChild(0).getChild(0).getText() + " "
						+ ctx.lastFormalParameter().getChild(0).getChild(1).getText();
			stringBuilder.append(chi2);
		}
		else {
			String variable = ctx.getChild(0).getChild(0).getChild(0).getText()+" "
							+ ctx.getChild(0).getChild(0).getChild(1).getText();
			stringBuilder.append(variable);
		}

	}
	private void setCode(String code){
//		stringBuilder.append("System.out.println("+code+");");
		stringBuilder.append("arrayList.add("+code+");");
	}
	private void setMsg(String msg){
		stringBuilder.append(msg);
	}
}
