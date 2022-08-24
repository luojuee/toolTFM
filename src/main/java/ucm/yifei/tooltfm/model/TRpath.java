package ucm.yifei.tooltfm.model;

import ucm.yifei.tooltfm.utils.StringUtils;
import ucm.yifei.tooltfm.visitors.Java8CustomVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TRpath {
    //test requirements, ej: TR = {[1, 2, 3], [1, 2, 4], [2, 3, 2], [3, 2, 3], [3, 2, 4]}
    private List<List<String>> trList = new ArrayList<>();//形成循环的路径和以start开头end结尾的路径存入trList中
    private List<Line> lineList;

    public TRpath(Java8CustomVisitor visitor) {
        this.lineList = visitor.lineList;
    }
    //用于TestPath计算循环路径
    public TRpath(List<List<String>> g2LineList) {
        edgeList.addAll(g2LineList);
        getAuxList1(edgeList);
        while (auxList1.size() > 0) {
            getAuxList2();
        }
    }

    List<String> aux = new ArrayList<>();
    //循环集合
    List<List<String>> loopList = new ArrayList<>();
    List<List<String>> edgeList = new ArrayList<>(); //节点之间连接情况, 用于长度延长
    List<List<String>> simplePaths = new ArrayList<>(); //存放路径，长度L2,L3,L4...
    List<List<String>> auxList1 = new ArrayList<>(); //不包含无法向下连接的元素的list1
    List<List<String>> auxList2 = new ArrayList<>(); //存放新的生成，list1 + edgeList

    public TRpath() {
    }

    //将lineList中每个的source, target一起提取存入edgeList中
    private void getLineSourceTarget(){
        for(Line line: lineList){
            aux = new ArrayList<>();
            aux.add(line.fromNode);
            aux.add(line.toNode);
            edgeList.add(aux);
        }
        //计算用于连接的auxList1
        getAuxList1(edgeList);
    }
    //计算auxList1并存入simplePaths中, 元素首尾相等的元素存入循环集合loopList和trList, 尾元素是‘end’的元素不存入auxList1, 其他元素存入auxList1
    private void getAuxList1(List<List<String>> pathList){
        auxList1.clear();
        for(int i = 0; i < pathList.size(); i++){
            if(pathList.get(i).get(0).equals(pathList.get(i).get(pathList.get(i).size() - 1))){
                loopList.add(pathList.get(i));
                trList.add(pathList.get(i));
            }
            else if(!pathList.get(i).get(pathList.get(i).size() - 1).equals("end")){
                auxList1.add(pathList.get(i));
                simplePaths.add(pathList.get(i));
            }
            else {
                simplePaths.add(pathList.get(i));
            }
        }
    }
    //计算auxList2, 将auxList1中的元素延长，存入auxList2
    private void getAuxList2(){
        auxList2.clear();
        for (int i = 0; i < auxList1.size(); i++) {
            for (int j = 0; j < edgeList.size(); j++) {
                //auxList1每个元素的末尾与auxList1元素的第一个相等，则组合存入auxList2中, 存入时相等的元素删除
                if (auxList1.get(i).get(auxList1.get(i).size() - 1).equals(edgeList.get(j).get(0))) {
                    aux = new ArrayList<>();
                    aux.addAll(auxList1.get(i));
                    aux.add(edgeList.get(j).get(1));
                    //‘start’开始‘end’结束的存在trList中, 不存入auxList2
                    if (aux.get(0).equals("start") && aux.get(aux.size() - 1).equals("end")) {
                        trList.add(aux);
                    }
                    else auxList2.add(aux);
                }
            }
        }
        //修正auxList2, 如果元素中包含有loopList中的元素，则删除
        if (loopList.size() > 0) {
            for (int i = 0; i < auxList2.size(); i++) {
                String strAuxList2 = StringUtils.listToString(auxList2.get(i));
                for (int j = 0; j < loopList.size(); j++) {
                    String strLoopList = StringUtils.listToString(loopList.get(j));
                    if (strAuxList2.contains(strLoopList)) {
                        auxList2.remove(i);
                        i--;
                    }
                }
            }
        }
        getAuxList1(auxList2);
    }

    //生成TRpath
    public void generateTRpath(){
        trList.clear();
        simplePaths.clear();

        getLineSourceTarget();
        while (auxList1.size() > 0) {
            getAuxList2();
        }
        //simplePaths从后向前处理元素, 如trList的元素不包含s正在处理的元素, 则将元素加入trList
        for (int i = simplePaths.size() - 1; i >= 0; i--) {
            if (trList.isEmpty()) {
                trList.add(simplePaths.get(i));
            }
            else {
                String strSimplePaths = StringUtils.listToString(simplePaths.get(i));
                boolean flag = false;
                int j = 0;
                while ((flag != true) && (j < trList.size())){
                    String strTrList = StringUtils.listToString(trList.get(j));
                    if (strTrList.contains(strSimplePaths)) {
                        flag = true;
                    }
                    j++;
                }
                if (flag == false) {
                    trList.add(simplePaths.get(i));
                }
            }
        }
        //重新整理去除start 和end
        for (int i = 0; i < trList.size(); i++) {
            if (trList.get(i).get(0).equals("start")){
                trList.get(i).remove(0);
            }
            if (trList.get(i).get(trList.get(i).size()-1).equals("end")){
                trList.get(i).remove(trList.get(i).size()-1);
            }
        }
    }
    //输出TRpath
    public String printTRpath(){
        //将trList中的元素按长度排序
        Collections.sort(trList, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o2.size() - o1.size();
            }
        });
        //trList中的元素换行存入str
        String str = "";
        for (int i = 0; i < trList.size(); i++) {
            //字符串16位，空格补齐
            str += StringUtils.padRightSpaces("trPath"+(i+1)+": ", 24) + trList.get(i).toString() + "\n";
        }
        return str;
    }
    //获得trList
    public List<List<String>> getTrList(){
        return trList;
    }
    //获取循环路径
    public List<List<String>> getLoopList(){
        return loopList;
    }
    //获取edgeList
    public List<List<String>> getEdgeList(){
        return edgeList;
    }
}
