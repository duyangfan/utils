package utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Description: TODO
 * @ClassName: FileUtils
 * @author
 * @see
 */
public class FtpUtils {

    public static final String ARRACHDIR = "arrachdir"; // double的小数位

    /**
     * 连接sftp服务器
     *
     * @param host
     *            主机
     * @param port
     *            端口
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return
     */
    public static ChannelSftp connect(String host, int port, String username, String password) {
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sftp;
    }

    /**
     * 名称：transferFile<br>
     * 描述: <br>
     *
     * @param path
     *            路径
     * @param file
     *            文件
     * @return 文件对象
     * @throws IllegalStateException
     *             调用冲突异常
     * @throws IOException
     *             输入输出异常
     * @exception @since
     *                1.0.0
     */
    public static File transferFile(String path, File file) throws IllegalStateException, IOException {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        if (file.getName().lastIndexOf(".") > 0) {
            File aFile = new File(path + file.getName());

            String strLast = path.substring(0, path.lastIndexOf("/"));

            int nameLength = strLast.substring(strLast.lastIndexOf("/")).length() + 1 + aFile.getName().length();
            // 如果上传的文件的名字中含有中文字符或其他非单词字符，那么就进行重命名，并将其改为英文名字
            // 这里所说的单词字符为：[a-zA-Z_0-9]
            Boolean rename = false;
            String pattern = "[\u4e00-\u9fa5]+";
            Pattern p = Pattern.compile(pattern);
            Matcher result = p.matcher(file.getName());
            rename = result.find();
            if (aFile != null && aFile.exists() || nameLength > 30 || rename) {
                char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                        's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

                StringBuffer fileName = new StringBuffer("");
                Random r = new Random();
                int pos = -1;
                for (int i = 0; i < 15; i++) {
                    pos = Math.abs(r.nextInt(36));
                    fileName.append(str[pos]);
                }

                String newName = file.getName().substring(file.getName().lastIndexOf(".") + 1);

                aFile = new File(path + fileName.toString().trim() + "." + newName);

                System.out.println("***************" + path + fileName.toString().trim() + "." + newName);
            }
            return aFile;
        } else {
            return null;
        }
    }

    /**
     * 删除文件或者文件夹，对于文件夹遍历其子文件夹进行递归删除
     *
     * @param f
     *            - File对象
     * @return 删除是否成功
     */
    public static boolean deleteFile(File f) {
        if (f.exists()) {
            if (f.isFile()) {
                return f.delete();
            } else if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (!deleteFile(files[i])) {
                        return false;
                    }
                }
                return f.delete();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * deleteFile 描述: ftp删除文件
     *
     * @param pathName
     *            文件路径
     * @return boolean
     * @throws IOException
     * @throws SocketException
     * @throws
     * @exception @since
     *                1.0.0
     */
    public boolean deleteFile(String ip, int port, String username, String password, String pathName)
            throws SocketException, IOException {
        FTPClient ftpClient = loginFtpServer(ip, port, username, password);
        boolean flag = false;
        try {
            flag = ftpClient.deleteFile(pathName);
            // System.out.println(flag);
        } catch (IOException e) {
            System.out.println("删除失败");
        }
        return flag;
    }

    /**
     * createRandomFileName 描述：生产文件名称
     *
     * @return 文件名称
     */
    public static String createRandomFileName() {
        return UUID.randomUUID() + "";
    }

    public static void createDirs(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
    }

    /**
     *
     * @Title: fileUpload
     * @Description: TODO
     * @param ip
     * @param port
     * @param username
     * @param password
     * @param ins
     * @param fileName
     * @param productNumber  文件夹
     * @return
     * @throws Exception
     * @author
     */
    public static String fileUpload(String ip, int port, String username, String password, InputStream ins,
                                    String fileName, String productNumber) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("GBK");
            FTPClientConfig conf = new FTPClientConfig("WINDOWS");
            conf.setServerLanguageCode("zh");
            ftpClient.configure(conf);
            if(port==80){
                ftpClient.connect(ip);
            }else{
                ftpClient.connect(ip, port);
            }

            boolean result = ftpClient.login(username, password);
            if (!result) {
                throw new Exception("文件服务器用户名或密码不正确");
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            String year = productNumber;
            boolean yearPath = ftpClient.changeWorkingDirectory(year);
            if (!yearPath) {
                boolean change = createDirectory(year, ftpClient);
                if (!change) {
                    throw new Exception("文件服务器切换工作目录失败");
                }
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(1024 * 1024 * 20);// 设置上传ftp的速度为20M，默认为1kb
            if (ftpClient.storeFile(fileName, ins)) {
            } else {
                throw new Exception("文件上传失败!");
            }
            return year + "/" + fileName;
        } finally {
            if (ftpClient != null) {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        throw ioe;
                    }
                }
            }
            if (ins != null) {
                ins.close();
            }
        }
    }

    /**
     *
     * @Title: fileUpload
     * @Description: TODO
     * @param ip
     * @param port
     * @param username
     * @param password
     * @param
     * @param fileName
     * @param productNumber 文件夹
     * @return
     * @throws Exception
     *
     */
    public static String fileUpload(String ip, int port, String username, String password, File file, String fileName,
                                    String productNumber) throws Exception {
        FileInputStream ins = new FileInputStream(file);
        try {
            return fileUpload(ip, port, username, password, ins, fileName, productNumber);
        } finally {
            if (ins != null) {
                ins.close();
            }
        }
    }

    public static byte[] downloadFile(String ip, int port, String username, String password, String dir,
                                      String fileName) throws Exception {
        FTPClient ftpClient = loginFtpServer(ip, port, username, password);
        boolean changeWorkPath = false;
        boolean change = false;
        InputStream ins=null;
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            changeWorkPath = ftpClient.changeWorkingDirectory(dir);
            if (!changeWorkPath) {
                change = createDirectory(dir, ftpClient);
                if (!change) {
                    throw new Exception("文件服务器切换工作目录失败");
                }
            }
            ins = ftpClient.retrieveFileStream(new String(fileName.getBytes("gbk")));
            return Util.readBytes(ins);
        } catch (IOException e) {
            throw new Exception("文件服务器切换工作目录失败");
        } finally {
            if(ins!=null)
                ins.close();
            if (ftpClient != null) {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }

    }


    public static InputStream downloadFileToInputStream(String ip, int port, String username, String password, String dir,
                                                        String fileName) throws Exception {
        FTPClient ftpClient = loginFtpServer(ip, port, username, password);
        boolean changeWorkPath = false;
        boolean change = false;
        InputStream ins;
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            changeWorkPath = ftpClient.changeWorkingDirectory(dir);
            if (!changeWorkPath) {
                change = createDirectory(dir, ftpClient);
                if (!change) {
                    throw new Exception("文件服务器切换工作目录失败");
                }
            }
            ins = ftpClient.retrieveFileStream(new String(fileName.getBytes("gbk")));
        } catch (IOException e) {
            throw new Exception("文件服务器切换工作目录失败");
        } finally {
            if (ftpClient != null) {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }

        return ins;
    }

    public static boolean createDirectory(String path, FTPClient ftpClient) throws IOException {
        boolean flag = false;
        String[] pathes = path.split("/");
        for (int i = 0; i < pathes.length; i++) {
            ftpClient.makeDirectory(pathes[i]);
            flag = ftpClient.changeWorkingDirectory(pathes[i]);
        }
        return flag;
    }


    /**
     * loginFtpServer 描述: 登录ftp
     *
     * @param ip
     *            ftp的ip
     * @param port
     *            ftp的端口
     * @param username
     *            登录用户名
     * @param password
     *            登录密码
     * @return 返回FTPClient的实例
     * @throws IOException
     * @throws SocketException
     * @throws
     *             FTPClient
     * @exception @since
     *                1.0.0
     */
    public static FTPClient loginFtpServer(String ip, int port, String username, String password)
            throws SocketException, IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("GBK");
        FTPClientConfig conf = new FTPClientConfig("WINDOWS");
        conf.setServerLanguageCode("zh");
        ftpClient.configure(conf);
        if(port==80){
            ftpClient.connect(ip);
        }else{
            ftpClient.connect(ip, port);
        }
        ftpClient.login(username, password);
        return ftpClient;
    }

    /**
     * 上传文件
     * @Title: upload
     * @Description: TODO
     * @param dir
     * @param fileName
     * @param file
     * @return
     * @throws NumberFormatException
     * @throws Exception
     *
     */
    public static String upload(String dir,String fileName,File file) throws NumberFormatException, Exception{
        String ip = Util.readProperties("ftp.ip");
        String port = Util.readProperties("ftp.port");
        String username = Util.readProperties("ftp.username");
        String password = Util.readProperties("ftp.password");
        return fileUpload(ip, Integer.parseInt(port), username, password, file, fileName, dir);
    }
    public static String upload(String dir,String fileName,InputStream in) throws NumberFormatException, Exception{
        String ip = Util.readProperties("ftp.ip");
        String port = Util.readProperties("ftp.port");
        String username = Util.readProperties("ftp.username");
        String password = Util.readProperties("ftp.password");
        return fileUpload(ip, Integer.parseInt(port), username, password, in, fileName, dir);
    }

    /**
     * 下载
     * @Title: download
     * @Description: TODO
     * @param dir
     * @param fileName
     * @return
     * @throws NumberFormatException
     * @throws Exception
     * @author zhengzhenglei  2017年3月2日 下午7:03:50
     */
    public static byte[] download(String dir,String fileName) throws NumberFormatException, Exception{
        String ip = Util.readProperties("ftp.ip");
        String port = Util.readProperties("ftp.port");
        String username = Util.readProperties("ftp.username");
        String password = Util.readProperties("ftp.password");
        return downloadFile(ip, Integer.parseInt(port), username, password, dir, fileName);
    }

    public static InputStream downloadToInputStream(String dir,String fileName) throws NumberFormatException, Exception{
        String ip = Util.readProperties("ftp.ip");
        String port = Util.readProperties("ftp.port");
        String username = Util.readProperties("ftp.username");
        String password = Util.readProperties("ftp.password");
        return downloadFileToInputStream(ip, Integer.parseInt(port), username, password, dir, fileName);
    }

    /**
     * 只给ftp用
     * @Title: ftpUplod
     * @Description: TODO
     * @param file
     * @return  文件名称
     *
     */
    public static String ftpUplod(MultipartFile file) {
        // 判断文件是否为空
        if (!file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                String uuid = UUID.randomUUID().toString().replace("-", "").replace("_", "");
                String name = uuid + suffix;
                upload(ARRACHDIR, name, file.getInputStream());
                return name;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * ftp下载
     * @Title: ftpDowload
     * @Description: TODO
     * @param fileName
     * @return
     * @throws NumberFormatException
     * @throws Exception
     * @author zhengzhenglei  2017年3月3日 上午10:25:03
     */
    public static byte[] ftpDowload(String fileName) throws NumberFormatException, Exception{
        return download(ARRACHDIR, fileName);
    }

    public static InputStream ftpDowloadToInputStream(String fileName) throws NumberFormatException, Exception{
        return downloadToInputStream(ARRACHDIR, fileName);
    }

}
