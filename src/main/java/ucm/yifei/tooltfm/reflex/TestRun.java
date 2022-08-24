package ucm.yifei.tooltfm.reflex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 动态执行一段代码(生成文件->编译->执行)
 */
public class TestRun {
    //    private String fileName = "Test.java";
//    private String className= "Test.class";
    Define define;
    public TestRun(Define define) {
        this.define = define;
    }

    /**
     * 创建java文件
     */
    public void createJavaFile(String body, String method) {
        String head = "import java.util.ArrayList;\n"+
                "public class "+ define.getClassName()+" {\n " +
                "public static ArrayList arrayList = new ArrayList();\n " +
                "public static ArrayList runCode(){ \n";
        String end = "\n }";

        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(
                    define.getClassName()+".java"));
            dos.writeBytes(head);
            dos.writeBytes(body);
            dos.writeBytes(end);
            dos.writeBytes(method);
            dos.writeBytes("}");
            dos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createJavaFile1(String method) {
        String head = "package ucm.yifei.tooltfm.example;\n"+
                "import java.util.ArrayList; \n public class "+define.getClassName()+" { \n";
        String end = "\n }";
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(
                    "src/main/java/ucm/yifei/tooltfm/example/"+
                    define.getClassName()+".java"));
            dos.writeBytes(head);

            dos.writeBytes(method);

            dos.writeBytes(end);
            dos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String javacDir = "cmd /c C:\\Users\\ASUS\\.jdks\\temurin-1.8.0_322\\bin\\javac ";

    public int makeJavaFile2() {
        int ret = 0;
        try {
            Runtime rt = Runtime.getRuntime();
            String name =define.getClassName();
            Process ps = rt.exec(javacDir + name +".java");
            ps.waitFor();
            byte[] out = new byte[1024];
            DataInputStream dos = new DataInputStream(ps.getInputStream());
            dos.read(out);
            String s = new String(out);
            if (s.indexOf("Exception") > 0) {
                ret = -1;
            }
        }
        catch (Exception e) {
            ret = -1;
            e.printStackTrace();
        }
        return ret;
    }
    /**
     * 编译
     */
    public int makeJavaFile() {
        int ret = 0;
        try {
            Runtime rt = Runtime.getRuntime();
            Process ps = rt.exec(javacDir + define.getClassName() +".java");
            ps.waitFor();
            byte[] out = new byte[1024];
            DataInputStream dos = new DataInputStream(ps.getInputStream());
            dos.read(out);
            String s = new String(out);
            if (s.indexOf("Exception") > 0) {
                ret = -1;
            }
        }
        catch (Exception e) {
            ret = -1;
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 反射执行
     */
    public ArrayList run() {
        try {
            byte[] classBytes = define.getBytesByFile(define.getClassName()+".class");

            Class runCode = Define.loadClass(classBytes);
            Object obj = runCode.newInstance();
            // 获取Method
            Method loginMethod = runCode.getDeclaredMethod("runCode");
            //  Method loginMethod = userServiceClass.getDeclaredMethod("login");//注：没有形参就不传
            // 调用方法
            // 调用方法有几个要素？ 也需要4要素。
            // 反射机制中最最最最最重要的一个方法，必须记住。

            ArrayList resValues = (ArrayList) loginMethod.invoke(obj);//注：方法返回值是void 结果是null
            return resValues;
        }
        catch (InvocationTargetException | IllegalAccessException iE) {//测试用例exception
            //e.printStackTrace();
            ArrayList resValues = new ArrayList<>();
            resValues.add("start");
            resValues.add("end");
            return resValues;
        } catch (NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
