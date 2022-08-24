package ucm.yifei.tooltfm.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAndDeleteFinder {
    public static void tempFileDelete() {
        FileAndDeleteFinder finder = new FileAndDeleteFinder();
        List<String> filenameList = new ArrayList<String>();
        //finder.findFiles(".iml", "F:\\后端\\ProTotal\\MyBatis", filenameList);
        finder.findFiles("D:\\IDEA\\toolTFM\\", filenameList);
        for (String filename : filenameList) {
            System.out.print(filename + ", ");
        }
    }
    /**
     * 寻找指定目录下，具有指定后缀名的所有文件。
     * @param currentDirUsed      : 当前使用的文件目录
     * @param currentFilenameList ：当前文件名称的列表
     */
    private void findFiles(String currentDirUsed,
                          List<String> currentFilenameList) {
        File dir = new File(currentDirUsed);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //filenameSuffix: 文件后缀为java, class
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                if (file.getAbsolutePath().endsWith(".java")||file.getAbsolutePath().endsWith(".class")) {
                    currentFilenameList.add(file.getAbsolutePath());
                    file.delete();
                }
            }
        }
    }
}
