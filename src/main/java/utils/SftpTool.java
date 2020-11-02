package utils;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import java.io.*;
import java.util.*;

public class SftpTool {
    // 服务器连接ip
    private String host;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 端口号
    private int port = 22;

    private ChannelSftp sftp = null;
    private Session session = null;

    public SftpTool() {

    }

    public SftpTool(String host, int port, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public SftpTool(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * 通过SFTP连接服务器
     */
    public void connect() {
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            session = jsch.getSession(username, host, port);
            System.out.println("Session created.");

            session.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            session.connect();
            System.out.println("Session connected.");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("Opening Channel.");
            sftp = (ChannelSftp) channel;
            // 中文乱码
            sftp.setFilenameEncoding("GBK");
            System.out.println("Connected to " + host + ".");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (this.sftp != null) {
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
                System.out.println("sftp is closed already.");
            }
        }

        if (this.session != null) {
            if (this.session.isConnected()) {
                this.session.disconnect();
                System.out.println("session is closed already.");
            }
        }
    }

    /**
     * 批量下载文件
     *
     * @param remotPath
     *            远程下载目录(以路径符号结束, 可以为相对路径)
     * @param localPath
     *            本地保存目录(以路径符号结束)
     * @param fileFormat
     *            下载文件格式(以特定字符开头, 为空不做检验)
     * @param fileEndFormat
     *            下载文件格式(文件格式)
     * @param del
     *            下载后是否删除sftp文件
     * @return
     */
    public List<String> batchDownLoadFile(String remotePath, String localPath, String fileFormat, String fileEndFormat,
                                          boolean del) {
        List<String> filenames = new ArrayList<String>();
        try {
            if (!sftp.isConnected()) {
                connect();
            }

            Vector<LsEntry> v = listFiles(remotePath);
            // sftp.cd(remotePath);
            if (v.size() > 0) {
                System.out.println("本次处理文件个数不为零, 开始下载... fileSize = " + v.size());
                Iterator<LsEntry> it = v.iterator();
                while (it.hasNext()) {
                    LsEntry entry = it.next();
                    String filename = entry.getFilename();
                    SftpATTRS attrs = entry.getAttrs();
                    if (!attrs.isDir()) {
                        boolean flag = false;
                        String localFileName = localPath + File.separator + filename;
                        fileFormat = fileFormat == null ? "" : fileFormat.trim();
                        fileEndFormat = fileEndFormat == null ? "" : fileEndFormat.trim();
                        // 三种情况
                        if (fileFormat.length() > 0 && fileEndFormat.length() > 0) {
                            if (filename.startsWith(fileFormat) && filename.endsWith(fileEndFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (flag && del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else if (fileFormat.length() > 0 && "".equals(fileEndFormat)) {
                            if (filename.startsWith(fileFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (flag && del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else if (fileEndFormat.length() > 0 && "".equals(fileFormat)) {
                            if (filename.endsWith(fileEndFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (flag && del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else {
                            flag = downloadFile(remotePath, filename, localPath, filename);
                            if (flag) {
                                filenames.add(localFileName);
                                if (flag && del) {
                                    deleteSFTP(remotePath, filename);
                                }
                            }
                        }
                    } else {
                        // 再次调用
                        mkdirs(localPath + "/" + filename);
                        batchDownLoadFile(remotePath + "/" + filename, localPath + "/" + filename, fileFormat,
                                fileEndFormat, del);
                    }
                }
            }
            System.out.println("download file is success, remotePath = " + remotePath + " and localPath=" + localPath
                    + ", file size is " + v.size());
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            // this.disconnect();
        }
        return filenames;
    }

    /**
     * 下载单个文件, 以流的形式返回
     *
     * @param remotePath
     *            远程下载目录(以路径符号结束)
     * @param remoteFileName
     *            下载文件名
     * @return byte
     * @throws Exception
     */
    public byte[] downloadAsByte(String remotePath, String remoteFileName) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            sftp.get(remotePath + "/" + remoteFileName, os);

        } catch (Exception e) {
            throw e;
        } finally {
            os.close();
        }
        return os.toByteArray();
    }

    /**
     * 下载单个文件, 以字符串的形式返回
     *
     * @param remotePath
     *            远程下载目录(以路径符号结束)
     * @param remoteFileName
     *            下载文件名
     * @return byte
     * @throws Exception
     */
    public String downloadAsString(String remotePath, String remoteFileName) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            sftp.get(remotePath + "/" + remoteFileName, os);

        } catch (Exception e) {
            throw e;
        } finally {
            os.close();
        }
        return os.toString();
    }

    /**
     * 下载单个文件, 返回File的形式
     *
     * @param remotPath
     *            远程下载目录(以路径符号结束)
     * @param remoteFileName
     *            下载文件名
     * @param localPath
     *            本地保存目录(以路径符号结束)
     * @param localFileName
     *            保存文件名
     * @return File 返回file的形式
     */
    public File download(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FileOutputStream fieloutput = null;
        File file = null;
        try {
            // sftp.cd(remotePath);
            file = new File(localPath + "/" + localFileName);
            // mkdirs(localPath + localFileName);
            fieloutput = new FileOutputStream(file);
            sftp.get(remotePath + "/" + remoteFileName, fieloutput);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (null != fieloutput) {
                try {
                    fieloutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 下载单个文件
     *
     * @param remotPath
     *            远程下载目录(以路径符号结束)
     * @param remoteFileName
     *            下载文件名
     * @param localPath
     *            本地保存目录(以路径符号结束)
     * @param localFileName
     *            保存文件名
     * @return
     */
    public boolean downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FileOutputStream fieloutput = null;
        try {
            // sftp.cd(remotePath);
            File file = new File(localPath + "/" + localFileName);
            // mkdirs(localPath + localFileName);
            fieloutput = new FileOutputStream(file);
            sftp.get(remotePath + "/" + remoteFileName, fieloutput);
            System.out.println("===DownloadFile: " + remoteFileName + " success from sftp.");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (null != fieloutput) {
                try {
                    fieloutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 上传单个文件, 以字符串内容的形式进行上传.<br>
     * 这个方法用来解决把字符串内容以文件的方式上传到sftp服务器
     *
     * @param remotePath
     *            sftp服务器保存目录
     * @param remoteFileName
     *            sftp服务器保存文件名
     * @param content
     *            实际上传的字符串内容
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFileName, String content) {
        InputStream is = null;
        try {
            createDir(remotePath);
            is = new ByteArrayInputStream(content.getBytes());
            sftp.put(is, remoteFileName);

            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 上传单个文件, 以输入流的形式进行上传.<br>
     * 这个方法用来解决把输入流以文件的方式上传到sftp服务器
     *
     * @param remotePath
     *            sftp服务器保存目录
     * @param remoteFileName
     *            sftp服务器保存文件名
     * @param is
     *            实际上传的字符串内容
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFileName, InputStream is) {
        try {
            createDir(remotePath);
            sftp.put(is, remoteFileName);

            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 上传单个文件
     *
     * @param remotePath
     *            远程保存目录
     * @param remoteFileName
     *            保存文件名
     * @param localPath
     *            本地上传目录(以路径符号结束)
     * @param localFileName
     *            上传的文件名
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FileInputStream in = null;
        try {
            createDir(remotePath);
            File file = new File(localPath + "/" + localFileName);
            in = new FileInputStream(file);
            sftp.put(in, remoteFileName);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 批量上传文件
     *
     * @param remotePath
     *            远程保存目录
     * @param localPath
     *            本地上传目录(以路径符号结束)
     * @param del
     *            上传后是否删除本地文件
     * @return
     */
    public boolean bacthUploadFile(String remotePath, String localPath, boolean del) {
        try {
            if (!sftp.isConnected()) {
                connect();
            }

            File file = new File(localPath);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (this.uploadFile(remotePath, files[i].getName(), localPath, files[i].getName()) && del) {
                        deleteFile(localPath + files[i].getName());
                    }
                } else {
                    // 文件夹目录
                    bacthUploadFile(remotePath + "/" + files[i].getName(), localPath + "/" + files[i].getName(), del);
                }
            }
            System.out.println("upload file is success: remotePath = " + remotePath + " and localPath = " + localPath
                    + ", file size is " + files.length);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return false;
    }

    /**
     * 删除本地文件
     *
     * @param filePath
     * @return
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        if (!file.isFile()) {
            return false;
        }
        boolean rs = file.delete();
        if (rs) {
            System.out.println("delete file success from local.");
        }
        return rs;
    }

    /**
     * 创建目录
     *
     * @param createpath
     * @return
     */
    public boolean createDir(String createpath) {
        try {
            if (!sftp.isConnected()) {
                connect();
            }

            if (isDirExist(createpath)) {
                this.sftp.cd(createpath);
                return true;
            }

            String pathArry[] = createpath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if (path.equals("")) {
                    continue;
                }

                filePath.append(path + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }

            this.sftp.cd(createpath);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断目录是否存在
     *
     * @param directory
     * @return
     */
    public boolean isDirExist(String directory) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    /**
     * 删除stfp文件
     *
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     * @param sftp
     */
    public void deleteSFTP(String directory, String deleteFile) {
        try {
            if (!sftp.isConnected()) {
                connect();
            }

            sftp.rm(directory + File.separator + deleteFile);
            System.out.println("delete file success from sftp.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果目录不存在就创建目录
     *
     * @param path
     */
    public void mkdirs(String path) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory
     *            要列出的目录
     * @param sftp
     * @return
     * @throws SftpException
     */
    @SuppressWarnings("unchecked")
    public Vector<LsEntry> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ChannelSftp getSftp() {
        return sftp;
    }

    public void setSftp(ChannelSftp sftp) {
        this.sftp = sftp;
    }

    public static void main(String[] args) throws SftpException {
//		SftpTool tool = new SftpTool("192.168.42.2", 8890, "zdy", "zdy");
//		tool.connect();
//		ChannelSftp sftp = tool.getSftp();
//
//		String str = "123456677我们都使孩子我們都是孩子";
//
//		InputStream ins = new ByteArrayInputStream(str.getBytes());
//		sftp.cd("/");
//		// sftp.put(ins, "test.txt");
//
//		// 输出流转字符串
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		OutputStream ows = new ByteArrayOutputStream();
//		sftp.cd("/");
//		sftp.get("test.xml", ows);
//
//		System.out.println("输出： \n" + ows.toString());
//
//		String sTempOneLine = new String("");
//
//		StringBuffer buf = new StringBuffer();
//
//		buf.append("<?xml version='1.0' encoding='UTF-8'?>\n");
//		buf.append(ows.toString());
//
//		System.out.println("输出_1： \n" + buf.toString());

    }

}