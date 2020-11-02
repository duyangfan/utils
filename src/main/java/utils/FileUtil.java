package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileUtil {

    public static String readFile(String path) throws Exception{
        StringBuffer str = new StringBuffer();
        BufferedReader in = null;
        File inputFile = null;
        String realPath = ClassLoader.getSystemResource(path).getPath();
        inputFile = new File(realPath);
        in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "GBK"));
        String line = null;
        str = new StringBuffer((int) inputFile.length());
        while ((line = in.readLine()) != null) {
            str.append(line);
        }
        in.close();
        return str.toString();
    }
}
