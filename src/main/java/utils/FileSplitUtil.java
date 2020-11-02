package utils;

import java.io.*;

/**
 * 拆分文件
 * @author
 *
 */
public class FileSplitUtil {



    /**
     * 安文件流拆分
     * @param ifile 将要分割的文件
     * @param ofile 将要存放的文件夹
     * @param filesize 单个文件最大大小byte(字节)
     * @return 是否分割成功
     */
    public static  boolean fileSplitMethod(File	ifile,File ofile,long filesize){
        if(!ifile.exists() || !ifile.isFile() || !ofile.exists() || !ofile.isDirectory() ||  filesize <= 0)
            return false;
        int bufl = 1024; //缓冲字节数组的长度
        if(filesize < bufl)
            bufl = (int) filesize;
        byte[] buf = new byte[bufl];//字节缓冲数组
        int length = 0;//记录当前读取的字节数
        int size = 0;//记录当前文件字节数
        long readsize = 0;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        int nameend = 1;//记录将要在旧名字上加上的编号以生成新文件
        try {
            fis = new FileInputStream(ifile);
            fos = new FileOutputStream(getNewFile(ifile,ofile,nameend));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            while((length = fis.read(buf)) != -1){
                fos.write(buf,0,length);
                size += length;
                readsize += length;//记录总已经读取字节数
                if((size + bufl) > filesize && readsize < ifile.length()){//如果再读一个数组会大于最大单个文件大小，并且还有未读字节，则创建新的fos。
                    fos.flush();//将缓冲中的写入文件
                    fos.close();//关闭这个输出流
                    nameend++;
                    fos = new FileOutputStream(getNewFile(ifile,ofile,nameend));//重新创建新的输出流
                    size = 0;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    private static File getNewFile(File	ifile,File ofile,int nameend){
        File file = null;
        String oldname = ifile.getName();//得到旧名字，新名字将在旧名字上加上数字
        String newfilepath = ofile.getPath() + "/";//用于保存新的file路径
        int endIndex = oldname.lastIndexOf(".");
        if(endIndex > 0){
            String oldnameend = oldname.substring(endIndex, oldname.length());//保存扩展名
            oldname = oldname.substring(0, endIndex);//保存最后一个点左边的名字
            newfilepath += oldname + nameend + oldnameend;//新路径
        }else
            newfilepath += oldname + nameend;
        file = new File(newfilepath);
        return file;
    }


    /**
     * 按行数拆分
     * @param ifile  原文件
     * @param ofile  目标路径  会自动以原文件名加流水号生成
     * @param filesize  多少行一个文件
     * @return
     */
    public static boolean readData(File ifile,File ofile,int filesize) {
        if(!ifile.exists() || !ifile.isFile() || !ofile.exists() || !ofile.isDirectory() ||  filesize <= 0)
            return false;
        try {
            FileReader read = new FileReader(ifile);
            BufferedReader br = new BufferedReader(read);
            String row;
            int rownum = 1;
            int nameend = 1;//记录将要在旧名字上加上的编号以生成新文件
            FileWriter fw1 = new FileWriter(getNewFile(ifile, ofile, nameend));
            while ((row = br.readLine()) != null) {
                if(rownum>=filesize){
                    nameend++;
                    rownum=1;
                    fw1.close();
                    fw1=new FileWriter(getNewFile(ifile, ofile, nameend));
                }
                fw1.append(row + "\r\n");
                rownum++;
            }
            fw1.close();
            br.close();
            read.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}