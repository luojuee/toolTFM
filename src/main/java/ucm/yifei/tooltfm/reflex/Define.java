package ucm.yifei.tooltfm.reflex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Define {
    public static String className= "Test111";

    public Define() {
    }
    public Define(String className) {
        this.className = className;
    }
    public String getClassName() {
        return className;
    }

    public static Class loadClass(byte[] classBytes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method mdDefineClass = ClassLoader.class.getDeclaredMethod(
                "defineClass",String.class, byte[].class, int.class, int.class);
        mdDefineClass.setAccessible(true);

        Class c1 = (Class)mdDefineClass.invoke(ClassLoader.getSystemClassLoader(),new Object[]{
                className,classBytes,0,classBytes.length
        });

        return c1;
    }

    //将文件转换成Byte数组
    public byte[] getBytesByFile(String pathStr) {
        File file = new File(pathStr);
        System.out.println("The file size is: " + file.length());
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            byte[] data = bos.toByteArray();
            bos.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
