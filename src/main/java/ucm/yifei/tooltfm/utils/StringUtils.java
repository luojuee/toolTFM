package ucm.yifei.tooltfm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static boolean noStartWith(String str){
        if (str.startsWith("fail(") || str.startsWith("verifyException")|| str.startsWith("try")){
            return false;
        }
        else return true;
    }
    //分割字符串，返回字符串数组，分割符为str1
    public static String[] split(String str, String str1) {
        String[] strs = str.split(str1);
        return strs;
    }
    //test文件拆分
    public static List<List<String>> splitTestFile(BufferedReader in) throws IOException {
        List<List<String>> testList = new ArrayList<>();
        List<String> codeList;
        String str;
        boolean startWithT = false;
        while ((str = in.readLine()) != null) {
            str = str.trim();//去除首尾空格
            if (str.startsWith("@Test") || startWithT){
                codeList = new ArrayList<>();
                startWithT = false;
                while ((str = in.readLine()) != null){
                    str = str.trim();
                    if (str.startsWith("@Test")){
                        startWithT = true;
                        break;
                    }
                    else if (!str.startsWith("@") && !str.startsWith("//") && !str.startsWith("public")){
                        codeList.add(str);
                    }
                }
                while (!codeList.get(codeList.size()-1).equals("}")){//可能读取到“”
                    codeList.remove(codeList.size()-1);
                }
                codeList.remove(codeList.size()-1);//去除与public匹配的"}"
                testList.add(codeList);
            }
        }
        for (List<String> c: testList){
            for (int i = 0; i < c.size(); i++){
                if (c.get(i).equals("")){
                    c.remove(i);
                    i--;
                }
            }
        }
        codeList = testList.get(testList.size()-1);
        codeList.remove(codeList.size()-1);//去除与class匹配的"}"
        testList.remove(testList.size()-1);
        testList.add(codeList);

        return testList;
    }
    //检测字符串是否是“-”开头且“-”连续出现大于3次判定为分割线
    public static boolean isSplitLine(String str) {
        if (str.startsWith("-") && str.length() >= 3) {
            int count = 0;
            for (int i = 1; i < str.length() && count < 2; i++) {
                if (str.charAt(i) == '-') {
                    count++;
                }
                else {
                    break;
                }
            }
            if (count >= 2) {
                return true;
            }
        }
        return false;
    }
    //字符串按换行符切割,当某行为分割线时，将该行前面的字符串放入list中，并将该行后面的字符串放入新的list中
    public static List<String> splitByLine(String str) {
        List<String> list = new ArrayList<>();
        String[] strs = str.split("\n");
        String str1 = "";
        for (int i = 0; i < strs.length; i++) {
            if (isSplitLine(strs[i])) {
                list.add(str1);
                str1 = "";
            }
            else {
                str1 += strs[i]+"\n";
            }
        }
        list.add(str1);
        return list;
    }
    //String的最后一个字符是否为大写I或大写O
    public static boolean isCapital(String str) {
        if (str.length() == 0)
            return false;
        char last = str.charAt(str.length() - 1);
        if (last == 'I' || last == 'O')
            return true;
        return false;
    }
    //String除去最后一个字符
    public static String removeLast(String str) {
        if (str.equals("start")||str.equals("end")){
            return str;
        }
        else return str.substring(0, str.length() - 1);
    }
    //List<String>转String
    public static String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        return sb.toString();
    }
    //合并两个List<String>,返回是否重合和新的List<String>,删去重叠部分。如：["1","2","3"],["2","3","2"]->["1","2","3","2"]
    public static List<String> mergeList(List<String> list1, List<String> list2) {
        List<String> list = new ArrayList<>();
        List<String> l1 = new ArrayList<>();
        List<String> l2 = new ArrayList<>();
        //list1长度
        int len1 = list1.size(), len2 = list2.size(), diff = len1 - len2;
        boolean isOverlap = false;
        if (diff >= 0) {//list1长度大于list2
            l2.addAll(list2);
            l2.remove(len2-1);
            if (l2.size()>0){ //list2不是“end”
                for (int i = 0; i < len2 - 1; i++) {
                    l1.add(0, list1.get(len1 - i - 1));
                }
                String s1, s2;
                while (!l2.isEmpty()){
                    s1 = listToString(l1);
                    s2 = listToString(l2);
                    if (s1.equals(s2)) {
                        isOverlap = true;
                        list.addAll(list1);
                        for (int i = l2.size(); i < len2; i++) {
                            list.add(list2.get(i));
                        }
                        break;
                    }
                    else{
                        l1.remove(0);
                        l2.remove(l2.size() - 1);
                    }
                }
            }
            else{ //list2是“end”
                if (list1.get(len1 - 1).equals("end")){
                    isOverlap = true;
                    list.addAll(list1);
                }
            }
        }
        else{
            l1.addAll(list1);
            l1.remove(0);
            if (l1.size()>0){ //list1不是“start”
                for (int i = 0; i < len1 - 1; i++) {
                    l2.add(list2.get(i));
                }
                String s1, s2;
                while (!l1.isEmpty()){
                    s1 = listToString(l1);
                    s2 = listToString(l2);
                    if (s1.equals(s2)) {
                        isOverlap = true;
                        list.addAll(list2);
                        for (int i = len1 - l1.size() - 1; i >= 0; i--) {
                            list.add(0, list1.get(i));
                        }
                        break;
                    }
                    else{
                        l1.remove(0);
                        l2.remove(l2.size() - 1);
                    }
                }
            }
            else{ //list1是“start”
                if (list2.get(0).equals("start")){
                    isOverlap = true;
                    list.addAll(list2);
                }
            }
        }
        if (isOverlap == false){
            list.clear();
        }
        return list;
    }
    //如接收的字符串中有双引号替换成&quot;
    public static String replaceDoubleQuotes(String str) {
        String newStr = str.replaceAll("\"", "&quot;");
        return newStr;
    }
    //字符串填充
    public static String padRightSpaces(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);
        while (sb.length() < length - inputString.length()) {
            sb.append(' ');
        }
        return sb.toString();
    }
    //返回一个字符串在另一个字符串中出现的每次重复的位置, 如果没有出现返回空
    /*public static List<Integer> getRepeatPos(String str, String subStr) {
        //第一个node起始为start, pos需要减去“tart”长度
        List<Integer> pos = new ArrayList<>();

        int index = str.indexOf(subStr);
        int count = 0;
        while (index != -1) {
            count++;
            pos.add(index - 4);
            index = str.indexOf(subStr, index + 1);
        }

        if (count == 0) {
            return null;
        }
        return pos;
    }*/
    //返回一个list<String>在另一个list<String>中出现的每次重复的位置, 如果没有出现返回空
    public static List<Integer> getRepeatPos(List<String> list, List<String> subList) {
        //第一个node起始为start, pos需要减去“tart”长度
        List<Integer> pos = new ArrayList<>();
        int count = 0;
        int len = subList.size();
        for (int i = 0; i < list.size() - len + 1; i++) {
            int j = 0;
            boolean isRepeat = true;
            while (isRepeat && j < len) {
                if (!subList.get(j).equals(list.get(i+j))) {
                    isRepeat = false;
                }
                j++;
            }
            if (isRepeat) {
                pos.add(i);
                count++;
            }
        }

        if (count == 0) {
            return null;
        }
        return pos;
    }
    //返回一个list<String>在另一个list<String>中出现的次数
    public static int getRepeatCount(List<String> list, List<String> subList) {
        int count = 0;
        int len = subList.size();
        for (int i = 0; i < list.size() - len + 1; i++) {
            int j = 0;
            boolean isRepeat = true;
            while (isRepeat && j < len) {
                if (!subList.get(j).equals(list.get(i+j))) {
                    isRepeat = false;
                }
                j++;
            }
            if (isRepeat) {
                count++;
            }
        }
        return count;
    }
    //返回一个字符串在另一个字符串中出现的次数
    public static int getRepeatTimes(String str, String subStr) {
        int count = 0;
        int index = str.indexOf(subStr);
        while (index != -1) {
            count++;
            index = str.indexOf(subStr, index + 1);
        }
        return count;
    }
    //测试用main
    public static void main(String[] args) {
        String str = "int[] actualMoverUnoResult = (new Test280()).moverUno(new int[]{1, 1, 1, 1});";
        String []l = split(str, "moverUno");
        System.out.println(l.length);
    }
}