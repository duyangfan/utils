package utils;

import com.sinosoft.app.base.utils.BeanUtil;
import com.sinosoft.app.base.utils.WebUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 最常用工具类
 *
 * @author 
 *
 */
public class Util {

    public static final String DOUBLE = ".##"; // double的小数位
    public static final String TT="yyyy-MM-dd HH:mm:ss";
    public static final String DD ="yyyy-MM-dd";
    public static final String SS ="HH:mm:ss";
    public static Environment _ENV=null;


    // 读取properties文件//springboot方试
    public static String getPropertie(String key) {
        if(_ENV==null){
            _ENV=BeanUtil.getBeanClass(Environment.class);
        }
        return _ENV.getProperty(key);
    }

    // 读取properties文件
    public static String readProperties(String key) throws IOException {
        Properties propert = new Properties();
        propert.load(Util.class.getClassLoader().getResourceAsStream("application.properties"));
        return propert.getProperty(key);
    }

    // 读取properties文件
    public static String readProperties(String key, String propertiesName) throws IOException {
        Properties propert = new Properties();
        propert.load(Util.class.getClassLoader().getResourceAsStream(propertiesName));
        return propert.getProperty(key);
    }

    public static String readPropertiesa(String key, String fileName) throws IOException {
        Properties propert = new Properties();
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(fileName).getPath());
        FileInputStream in = new FileInputStream(file);
        propert.load(in);
        in.close();
        return propert.getProperty(key);
    }

    /**
     * 设置properties文件中指定key的值
     *
     * @param key
     * @param value
     * @param fileName
     * @throws IOException
     */
    public static void setProperties(String key, String value, String fileName) throws IOException {
        Properties propert = new Properties();
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(fileName).getPath());
        FileInputStream in = new FileInputStream(file);
        propert.load(in);
        propert.setProperty(key, value);
        in.close();
    }

    /**
     * 文件名得到Properties
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Properties getProperties(String fileName) throws Exception {
        Properties propert = new Properties();
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(fileName).getPath());
        FileInputStream in = new FileInputStream(file);
        propert.load(in);
        in.close();
        return propert;
    }

    /**
     * 对象转为字节
     *
     * @param value
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Object value) throws IOException {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.writeObject(null);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Non-serializable object", e);
        } finally {
            if (os != null)
                os.close();
            if (bos != null)
                bos.close();
        }
        return rv;
    }

    /**
     * 读取字节数
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        BufferedInputStream bufin = new BufferedInputStream(in);
        byte[] content = null;
        try {
            int buffSize = 1024;
            ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);

            // System.out.println("Available bytes:" + in.available());

            byte[] temp = new byte[buffSize];
            int size = 0;
            while ((size = bufin.read(temp)) != -1) {
                out.write(temp, 0, size);
            }

            content = out.toByteArray();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            bufin.close();
        }
        return content;
    }

    /**
     * 字节转为对象
     *
     * @param
     * @return
     * @throws IOException
     */
    public static Object deserialize(byte[] in) throws IOException {
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        Object obj = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                obj = (Object) is.readObject();
                is.close();
                bis.close();
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        } finally {
            if (is != null)
                is.close();
            if (bis != null)
                bis.close();
        }
        return obj;
    }

    /**
     * 显示图片
     *
     * @param file
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String showPic(HttpServletResponse response, File file) throws IOException {
        BufferedInputStream bis = null;
        OutputStream out = null;
        FileInputStream fileinput = null;
        try {
            fileinput = new FileInputStream(file);
            bis = new BufferedInputStream(fileinput);
            response.reset();
            response.setContentType("image/jpeg;charset=UTF-8");

            response.addHeader("Content-Transfer-Encoding", "base64");
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("显示图片出错");
        } catch (IOException e) {
            throw new RuntimeException("显示图片出错");
        } finally {
            if (out != null) {
                out.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (fileinput != null) {
                fileinput.close();
            }
        }
        return null;
    }

    /**
     * 显示图片
     *
     * @param
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String showPic(HttpServletResponse response, InputStream input) throws IOException {
        BufferedInputStream bis = null;
        OutputStream out = null;
        try {
            bis = new BufferedInputStream(input);
            response.reset();
            response.setContentType("image/jpeg;charset=UTF-8");

            response.addHeader("Content-Transfer-Encoding", "base64");
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("显示图片出错");
        } catch (IOException e) {
            throw new RuntimeException("显示图片出错");
        } finally {
            if (out != null) {
                out.close();
                bis.close();
            }
        }
        return null;
    }

    /**
     * 显示pdf
     *
     * @param
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String showPdf(HttpServletResponse response, byte[] content) throws IOException {
        BufferedInputStream bis = null;
        OutputStream out = null;
        InputStream byteArrayInputStream = null;
        BufferedInputStream fileinput = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(content);
            fileinput = new BufferedInputStream(byteArrayInputStream);
            bis = new BufferedInputStream(fileinput);
            response.reset();
            response.setContentType("application/pdf;charset=UTF-8");

            response.addHeader("Content-Transfer-Encoding", "base64");
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("pdf出错");
        } catch (IOException e) {
            throw new RuntimeException("pdf出错");
        } finally {
            content = null;
            if (byteArrayInputStream != null)
                byteArrayInputStream.close();
            if (fileinput != null)
                fileinput.close();
            if (out != null)
                out.close();
            if (bis != null)
                bis.close();
        }
        return null;
    }

    /**
     * 返回给定时间的月的第一天
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getMondayFistDate(Date date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int monday = calendar.get(Calendar.MONTH) + 1;
        int day = 1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(year + "-" + monday + "-" + day + " 00:00:00");
    }

    /**
     * 返回给定时间的月的最后一天
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getMondayEndDate(Date date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, +1);
        calendar.setTime(getMondayFistDate(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        int year = calendar.get(Calendar.YEAR);
        int monday = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(year + "-" + monday + "-" + day + " 23:59:59");
    }

    /**
     * 根据指定的范围，得到以当前时间为基础前几个月或后几个月的时间
     *
     * @param num
     * @return
     */
    public static Date getMonDayByNum(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, num);
        return calendar.getTime();
    }

    /**
     * 根据指定的范围，得到以当前时间为基础前几个月或后几个月的时间
     *
     * @param num
     * @return
     */
    public static Date getMonDayByNum(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, num);
        return calendar.getTime();
    }

    /**
     * 根据指定的范围，得到以当前时间为基础前几天或后天的时间
     *
     * @param num
     * @return
     */
    public static Date getDateByNum(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, num);
        return calendar.getTime();
    }

    /**
     * 取指定年的所有的双休
     * @param year
     * @return
     * @author 
     */
    public static Set<String> getYearDoubleWeekend(int year) {
        Set<String> listDates = new HashSet<String>();
        Calendar calendar = Calendar.getInstance();// 当前日期
        calendar.set(year, 6, 1);
        Calendar nowyear = Calendar.getInstance();
        Calendar nexty = Calendar.getInstance();
        nowyear.set(year, 0, 1);// 2010-1-1
        nexty.set(year + 1, 0, 1);// 2011-1-1
        calendar.add(Calendar.DAY_OF_MONTH, -calendar.get(Calendar.DAY_OF_WEEK));// 周六
        Calendar c = (Calendar) calendar.clone();
        for (; calendar.before(nexty) && calendar.after(nowyear); calendar.add(Calendar.DAY_OF_YEAR, -7)) {
            listDates.add(calendar.get(Calendar.YEAR) + "-" + (1 + calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DATE));
            listDates.add(calendar.get(Calendar.YEAR) + "-" + (1 + calendar.get(Calendar.MONTH)) + "-" + (1 + calendar.get(Calendar.DATE)));
        }
        for (; c.before(nexty) && c.after(nowyear); c.add(Calendar.DAY_OF_YEAR, 7)) {
            listDates.add(c.get(Calendar.YEAR) + "-" + (1 + c.get(Calendar.MONTH)) + "-" + c.get(Calendar.DATE));
            listDates.add(c.get(Calendar.YEAR) + "-" + (1 + c.get(Calendar.MONTH)) + "-" + (1 + c.get(Calendar.DATE)));
        }
        return listDates;
    };

    /**
     * 取指定年的所有的双休日 有序
     * @param year
     * @return
     * @author 
     */
    public static List<String> getYearDoubleWeekendToList(int year) {
        Set<String> set=getYearDoubleWeekend(year);
        List<String> list=set.stream().collect(Collectors.toList());
        Collections.sort(list, new Comparator<String>() {
            public int compare(String dictVo1, String dictVo2) {
                return dictVo1.compareTo(dictVo2);
            }
        });
        return list;
    };

    /**
     * 根据指定的范围，得到以当前时间为基础前几天或后几天的时间
     *
     * @param num
     * @return
     */
    public static Date getDateByNum(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, num);
        return calendar.getTime();
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date formatDate(Date date, String format) throws ParseException {
        SimpleDateFormat from = new SimpleDateFormat(format);
        return formatDate(from.format(date), format);
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static String formatDateToString(Date date, String format) throws ParseException {
        SimpleDateFormat from = new SimpleDateFormat(format);
        return from.format(date);
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date formatDate(String date, String format) throws ParseException {
        SimpleDateFormat from = new SimpleDateFormat(format);
        return from.parse(date);
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static String cretaFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 当前是星期几
     *
     * @param calendar
     * @return
     */
    public static String getWeek(Calendar calendar) {
        int r = calendar.get(Calendar.DAY_OF_WEEK);
        switch (r) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return null;
    }

    /**
     * 得到给定日期所在月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirestDateOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setFirstDayOfWeek(Calendar.MONTH);
        c.set(Calendar.DAY_OF_MONTH, Calendar.MONTH - 1);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 得新设置当前天的时,分秒
     *
     * @return
     */
    public static Date getDate(Date date, int h, int s, int m) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), h, s, m);
        return cal.getTime();
    }

    /**
     * 得新设置当前天的年,月，日
     *
     * @return
     */
    public static Date getTime(Date time, int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(year, month, date, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getNewTime(Date time, int year, int month, int date, int h, int s, int m) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(year, month, date, h, s, m);
        return cal.getTime();
    }

    public static Date getNewTime(Date time, int month, int date, int h, int s, int m) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(cal.get(Calendar.YEAR), month, date, h, s, m);
        return cal.getTime();
    }

    /**
     * 去逗号
     *
     * @param str
     * @return
     */
    public static String delComma(String str) {
        if (StringUtils.endsWith(str, ","))
            str = str.substring(0, str.length() - 1);
        return str;
    }

    /**
     * 得到文件的字节数组
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] getBytes(File filePath) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
        byte[] content = null;
        try {
            content = new byte[bis.available()];
            bis.read(content);
        } finally {
            bis.close();
        }
        return content;
    }

    /**
     * struts或springmvc上传
     *
     * @param file
     *            上传文件
     * @param dir
     *            存放的目录
     * @return
     */
    public static String uploadMultipartFile(MultipartFile file, String dir) {
        // 判断文件是否为空
        if (!file.isEmpty()) {
            try {
                // 保存的文件路径(如果用的是Tomcat服务器，文件会上传到\\%TOMCAT_HOME%\\webapps\\YourWebProject\\upload\\文件夹中
                // )
                // String filePath = request.getSession().getServletContext()
                // .getRealPath("/") + "upload/" + file.getOriginalFilename();
                String fileName = file.getOriginalFilename();
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                String uuid = UUID.randomUUID().toString().replace("-", "").replace("_", "");
                dir = StringUtils.removeEnd(dir, "/");
                dir = StringUtils.removeEnd(dir, "//");
                dir = StringUtils.removeEnd(dir, "\\");
                String name = uuid + suffix;
                String filePath = dir + "/" + name;
                File saveDir = new File(filePath);
                if (!saveDir.getParentFile().exists()) {
                    // saveDir.getParentFile().mkdirs();
                    File f = saveDir.getParentFile();
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                }

                FileUtils.writeByteArrayToFile(saveDir, file.getBytes());
                // 转存文件
                //file.transferTo(saveDir);
                return name;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 上传文件
     *
     * @param path
     * @param serverPath
     * @return
     * @throws Exception
     */
    public static String uploadFile(File path, String serverPath) throws Exception {
        FileUtils.copyFile(path, new File(serverPath));
        return serverPath;
    }

    /**
     * 上传大文件
     *
     * @param path
     * @param serverPath
     * @return
     * @throws Exception
     */
    public static String uploadBigFile(File path, String serverPath) throws Exception {
        int byteSize = 1024 * 1024;
        long fileLong = path.length();
        if (fileLong < 1024 * 1024 * 10) {
            byteSize = 1024 * 1024;
        }
        if (fileLong < 1024 * 1024 * 100) {
            byteSize = byteSize * 2;
        }
        if (fileLong < 1024 * 1024 * 1000) {
            byteSize = byteSize * 4;
        } else {
            byteSize = byteSize * 8;
        }
        BufferedInputStream bis = null;
        FileOutputStream out = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[byteSize];
            int size = 0;
            out = new FileOutputStream(serverPath);
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
            if (bis != null)
                bis.close();
        }

        return serverPath;
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param content
     * @throws IOException
     *             response.setCharacterEncoding("UTF_8");//设置Response的编码方式为UTF-
     *             8
     *             response.setHeader("Content-type","text/html;charset=UTF-8")
     *             ;//向浏览器发送一个响应头，设置浏览器的解码方式为UTF-8,其实设置了本句，
     *             也默认设置了Response的编码方式为UTF-8，但是开发中最好两句结合起来使用
     */
    public static void download(HttpServletResponse response, String fileName, byte[] content) throws IOException {
        InputStream byteArrayInputStream = new ByteArrayInputStream(content);
        BufferedInputStream bis = new BufferedInputStream(byteArrayInputStream);
        response.reset();
        response.setContentType("application/x-download");
        OutputStream out = null;
        try {
//			byte[] byname = fileName.getBytes("gbk");
//			fileName = new String(byname, "8859_1");
            fileName = WebUtils.getBrowserCode(WebUtil.getRequest(),fileName);
            response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName+"\"");
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            content = null;
            if (byteArrayInputStream != null)
                byteArrayInputStream.close();
            if (out != null)
                out.close();
            if (bis != null)
                bis.close();
        }
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param filePath
     * @throws IOException
     *             response.setCharacterEncoding("UTF_8");//设置Response的编码方式为UTF-
     *             8
     *             response.setHeader("Content-type","text/html;charset=UTF-8")
     *             ;//向浏览器发送一个响应头，设置浏览器的解码方式为UTF-8,其实设置了本句，
     *             也默认设置了Response的编码方式为UTF-8，但是开发中最好两句结合起来使用
     */
    public static void download(HttpServletResponse response, String fileName, File filePath) throws IOException {
        FileInputStream fileinput = new FileInputStream(filePath);
        BufferedInputStream bis = new BufferedInputStream(fileinput);
        response.reset();
        response.setContentType("application/x-download");
        OutputStream out = null;
        try {
//			byte[] byname = fileName.getBytes("gbk");
//			fileName = new String(byname, "8859_1");
            fileName = WebUtils.getBrowserCode(WebUtil.getRequest(),fileName);
            response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName+"\"");
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
            if (fileinput != null)
                fileinput.close();
            if (bis != null)
                bis.close();
        }
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param
     * @throws IOException
     *             response.setCharacterEncoding("UTF_8");//设置Response的编码方式为UTF-
     *             8
     *             response.setHeader("Content-type","text/html;charset=UTF-8")
     *             ;//向浏览器发送一个响应头，设置浏览器的解码方式为UTF-8,其实设置了本句，
     *             也默认设置了Response的编码方式为UTF-8，但是开发中最好两句结合起来使用
     */
    public static void download(HttpServletResponse response, String fileName, InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        response.reset();
        response.setContentType("application/x-download");
        OutputStream out = null;
        try {
//			byte[] byname = fileName.getBytes("gbk");
//			fileName = new String(byname, "8859_1");
            fileName = WebUtils.getBrowserCode(WebUtil.getRequest(),fileName);
            response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName+"\"");
            out = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int size = 0;
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (bis != null)
                bis.close();
        }
    }


    /**
     * Springmvc 下载通用方法
     *
     * @param body
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    public static ResponseEntity<byte[]> todown(byte[] body, String fileName) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        // 为了解决中文名称乱码问题
        fileName = WebUtils.getBrowserCode(WebUtil.getRequest(),fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
    }


    /**
     * Springmvc 下载通用方法
     *
     * @param
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    public static ResponseEntity<byte[]> todown(String fileName,String filePath) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        File file = new File(filePath);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        fileName = WebUtils.getBrowserCode(WebUtil.getRequest(),fileName);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }

    /**
     * Springmvc 下载通用方法
     *
     * @param body
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    public static ResponseEntity<byte[]> todown(byte[] body, HttpHeaders headers, String fileName)
            throws UnsupportedEncodingException {
        // 为了解决中文名称乱码问题
        fileName = WebUtils.getBrowserCode(WebUtil.getRequest(),fileName);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
    }

    /**
     * Springmvc 下载通用方法
     *
     * @param body
     * @param
     * @return
     * @throws UnsupportedEncodingException
     */
    public static ResponseEntity<byte[]> todown(byte[] body, HttpHeaders headers) throws UnsupportedEncodingException {
        return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
    }

    /**
     * 下载网络文件
     * @param fileUrl
     * @param outFile
     * @throws IOException
     * @author 
     */
    public static void downloadNet(String fileUrl,String outFile) throws IOException {
        // 下载网络文件
        URL url = new URL(fileUrl);
        InputStream inStream =null;
        FileOutputStream fs =null;
        try {
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();
            fs = new FileOutputStream(outFile);
            byte[] buffer = new byte[1204];
            int byteread = 0;
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inStream!=null) {
                inStream.close();
            }
            if(fs!=null) {
                fs.close();
            }
        }
    }


    /**
     * 根据给定的 年 和 月 返回 当月的第一天和最后一天
     *
     * @param
     *            ，月
     * @return String[0]-->当月的第一天,String[1]-->当月的最后一天
     */
    public static String[] getStartAndEnd(String year, String month) {
        String[] str = new String[2];
        if (month.length() == 1) {
            month = "0" + month;
        }
        str[0] = year + "-" + month + "-" + "01";
        str[1] = getLastDays(str[0]);
        return str;
    }

    /**
     * 根据当月第一天获取当月的最后一天
     *
     * @param
     * @return 当月最后一天的日期
     */
    public static String getLastDays(String beginDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(beginDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            date = calendar.getTime();
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符转化日期
     *
     * @return//"yyyy-MM-dd"
     */
    public static Date stringToDate(String day, String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        Date date = new Date();
        try {
            date = sdf.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 得到年月
     *
     * @return
     */
    public static String getYearAndMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return String.valueOf(cal.get(Calendar.YEAR)) + String.valueOf((cal.get(Calendar.MONTH) + 1) < 10
                ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1));
    }

    /**
     * 得到年
     *
     * @return
     */
    public static String getYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    /**
     * 得到月
     *
     * @return
     */
    public static String getMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return String.valueOf(cal.get(Calendar.MONTH) + 1);
    }

    /**
     * 得到当前年
     *
     * @return
     */
    public static Long getYears() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return Long.parseLong(cal.get(Calendar.YEAR) + "");
    }

    /**
     * 得到指定当前年(如果date为null时刚为当前年份)
     *
     * @return
     */
    public static Long getYears(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date == null) {
            date = new Date();
        }
        cal.setTime(date);
        return Long.parseLong(cal.get(Calendar.YEAR) + "");
    }

    /**
     * 得到天
     *
     * @return
     */
    public static int getDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }

    /***
     * 计算2个日期之间的天数
     *
     * @param beginTime
     * @param endTime
     * @return 相差的天数
     */
    public static long getDateMinus(String beginTime, String endTime) throws Exception {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date begin = s.parse(beginTime);
            Date end = s.parse(endTime);
            long time = (end.getTime() - begin.getTime()) / 1000 / 60 / 60 / 24;
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /***
     * 计算2个日期之间的天数
     *
     * @param beginTime
     * @param endTime
     * @return 相差的天数
     */
    public static long getDateMinus(Date beginTime, Date endTime) throws Exception {
        long time = (endTime.getTime() - beginTime.getTime()) / 1000 / 60 / 60 / 24;
        return time;
    }

    /***
     * 计算2个日期之间的小时
     *
     * @param beginTime
     * @param endTime
     * @return 相差的天数
     */
    public static long getHourMinus(Date beginTime, Date endTime) throws Exception {
        long time = (endTime.getTime() - beginTime.getTime()) / 1000 / 60 / 60;
        return time;
    }

    /***
     * 计算2个日期之间的分钟
     *
     * @param beginTime
     * @param endTime
     * @return 相差的天数
     */
    public static long getMMMinus(Date beginTime, Date endTime) throws Exception {
        long time = (endTime.getTime() - beginTime.getTime()) / 1000 / 60;
        return time;
    }

    /***
     * 计算2个日期之间的天数
     *
     * @param beginTime
     * @param endTime
     * @return 相差的天数
     */
    public static long getDateMinus2(Date beginTime, Date endTime) {
        Calendar beginCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        beginCal.setTime(beginTime);
        endCal.setTime(endTime);
        beginCal.set(Calendar.HOUR_OF_DAY, 0);
        beginCal.set(Calendar.SECOND, 0);
        beginCal.set(Calendar.MINUTE, 0);
        beginCal.set(Calendar.MILLISECOND, 0);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        long time = (endCal.getTime().getTime() - beginCal.getTime().getTime()) / 1000 / 60 / 60 / 24;
        return time + 1;
    }

    /**
     * 获取两个日期之间的日期
     * @param start 开始日期
     * @param end 结束日期
     * @return 日期集合
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    /**
     * 根据当前的年、月获得上个月
     */
    public static int[] getUpMonth(int year, int month) {
        if (month == 1) {
            month = 12;
            year = year - 1;
        } else {
            month = month - 1;
        }
        int[] str = { year, month };
        return str;
    }

    /**
     * 根据当前的年、月获得下个月
     */
    public static int[] getDownMonth(int year, int month) {
        if (month == 12) {
            month = 1;
            year = year + 1;
        } else {
            month = month + 1;
        }
        int[] str = { year, month };
        return str;
    }

    /**
     * 判断字符串里是否包含数字
     *
     * @param code
     * @return
     */
    public static boolean var(String code) {
        for (int i = 0; i < 10; i++) {
            if (code.trim().indexOf(i + "") != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 普通上专图片
     *
     * @param file
     * @param fileName
     * @param name
     *            已经上传的图片名
     * @return 名字
     * @throws Exception
     * @throws IOException
     */
    public static String uploadImage(File file, String fileName, String name) throws IOException, Exception {
        if (StringUtils.isNotEmpty(name)) {
            File file1 = new File(readProperties("oa.file.path") + name);
            if (file1.exists())
                file1.delete();
        }
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        String path = UUID.randomUUID().toString() + fileType;
        FileUtils.copyFile(file, new File(readProperties("oa.file.path") + path));
        return path;
    }

    /**
     * 获得给定时间的时和分
     *
     * @return
     */
    public static String getTime(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        return cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
    }

    /**
     * 获得给定时间的时和分
     *
     * @return
     * @throws ParseException
     */
    public static String getSysTime() throws ParseException {
        Calendar cl = Calendar.getInstance();
        int d=cl.get(Calendar.HOUR_OF_DAY);
        int m= cl.get(Calendar.MINUTE);
        int s=cl.get(Calendar.SECOND);
        String dd=d<10?"0"+d:d+"";
        String mm=m<10?"0"+m:m+"";
        String ss=s<10?"0"+s:s+"";
        return dd + ":" + mm+":"+ss;
    }

    /**
     * 获得给定时间的时和分
     *
     * @return
     * @throws ParseException
     */
    public static Date getSysDate() throws ParseException {
        return formatDate(new Date(), DD);
    }


    /**
     * 获得当前时间，精确到分
     *
     * @return
     */
    public static Date getCurrentTime() {
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        Date date = cl.getTime();
        return date;
    }

    /**
     * 格式化小数
     *
     * @return str=".##"
     */
    public static Double preDouble(Double numb) {
        if (numb == null)
            return numb;
        DecimalFormat df = new DecimalFormat(DOUBLE);
        return Double.valueOf(df.format(numb));
    }

    /**
     * 金额类型数据 小数位转为两位
     *
     * @param decimal
     * @return
     */
    public static BigDecimal decimalTo2(BigDecimal decimal) {
        Double temp = decimal.doubleValue();
        DecimalFormat format = new DecimalFormat(DOUBLE);
        String string = format.format(temp);
        return new BigDecimal(string);
    }

    /**
     * 金额类型数据 小数位转为两位 不够两位加零
     *
     * @param decimal
     * @return
     */
    public static String decimalTo2ToString(BigDecimal decimal) {
        Double temp = decimal.doubleValue();
        DecimalFormat format = new DecimalFormat(DOUBLE);
        String string = format.format(temp);
        int i = string.indexOf(".");
        if (i != -1) {
            String s = string.substring(i + 1, string.length());
            if (s.length() < 2) {
                return string + "0";
            }
        } else {
            return string + "00";
        }
        return string;
    }

    /**
     * 金额类型格式化成分率
     *
     * @param decimal
     * @return
     */
    public static String preBigDecimalToPercent(BigDecimal decimal) {
        NumberFormat percent = NumberFormat.getPercentInstance(); // 建立百分比格式化引用
        percent.setMaximumFractionDigits(2); // 百分比小数点最多2位
        return percent.format(decimal);
    }

    /**
     * 金额类型格式化string
     *
     * @param decimal
     * @return
     */
    public static String preBigDecimalToString(BigDecimal decimal) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(); // 建立货币格式化引用
        return currency.format(decimal);
    }

    /**
     * 产生随机数
     *
     * @return
     */
    public static int getRandom() {
        int max = 9999;
        int min = 1000;
        int cardNumber = (int) (Math.random() * (max - min)) + min;
        return cardNumber;
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void method(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 出生与现在时间比，是否是18周岁
     *
     * @Title: pre18
     * @Description: TODO
     * @param birth
     *            “2017-01-11”
     * @param now
     *            “2017-01-11”
     * @return
     * @author -lwb 2017年5月5日 下午5:36:09
     * @throws ParseException
     */
    public static boolean pre18(Date birth, Date now) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birth);
        int year = calendar.get(Calendar.YEAR) + 18;
        int monday = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Date start = formatDate(year + "-" + monday + "-" + day, "yyyy-MM-dd");
        Date end = formatDate(now, "yyyy-MM-dd");
        return start.before(end);
    }

    /**
     * 截取银行大类
     *
     * @param bankName
     * @return
     */
    public static String subBankName(String bankName) {
        if (StringUtils.isNotEmpty(bankName) && bankName.indexOf("[") != -1) {
            return bankName.substring(bankName.indexOf("[") + 1, bankName.length() - 1);
        }
        return "";
    }

    /**
     * 数据字典排序
     *
     * @Title: getDictVoList
     * @Description: TODO
     * @param list
     * @return
     * @author -lwb 2017年5月22日 下午3:55:52
     */
    public static List<DictVo> getDictVoValueList(List<DictVo> list) {
        Collections.sort(list, new Comparator<DictVo>() {
            public int compare(DictVo dictVo1, DictVo dictVo2) {
                // 以下是按string排序
                String o1 = dictVo1.getValue();
                String o2 = dictVo2.getValue();
                if (o1 == null || o2 == null) {
                    return -1;
                }
                if (o1.length() > o2.length()) {
                    return 1;
                }
                if (o1.length() < o2.length()) {
                    return -1;
                }
                if (o1.compareTo(o2) > 0) {
                    return 1;
                }
                if (o1.compareTo(o2) < 0) {
                    return -1;
                }
                if (o1.compareTo(o2) == 0) {
                    return 0;
                }

                return 0;
            }
        });
        return list;
    }

    /**
     * 数据字典排序
     *
     * @Title: getDictVoList
     * @Description: TODO
     * @param list
     * @return
     * @author -lwb 2017年5月22日 下午3:55:52
     */
    public static List<DictVo> getDictVoSortList(List<DictVo> list) {
        Collections.sort(list, new Comparator<DictVo>() {
            public int compare(DictVo dictVo1, DictVo dictVo2) {
                Integer o1 = dictVo1.getSort();
                Integer o2 = dictVo2.getSort();
                return o1.compareTo(o2);
            }
        });
        return list;
    }

    // 把数值转为金钱大写
    public static String valuesToString(Double value) {
        if (value <= 0)
            return "";
        char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
        char[] vunit = { '万', '亿' }; // 段名表示
        char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
        BigDecimal db = new BigDecimal(value);
        db = new BigDecimal(decimalTo2ToString(db));// 保留两位小数
        String str = db.toPlainString().replace(".", "");// 去掉小数点
        String valStr = str; // 转化成字符串
        String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
        String rail = valStr.substring(valStr.length() - 2); // 取小数部分

        String prefix = ""; // 整数部分转化的结果
        String suffix = ""; // 小数部分转化的结果
        // 处理小数点后面的数 如果加整则去掉下面注释 把注释下面那行代码去掉
        // if (rail.equals("00")) { // 如果小数部分为0
        //// suffix = "整";
        // suffix = "";
        // } else {
        // suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) -
        // '0'] + "分"; // 否则把角分转化出来
        // }
        suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
        // 处理小数点前面的数
        char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
        boolean preZero = false; // 标志当前位的上一位是否为有效0位（如万位的0对千位无效）
        byte zeroSerNum = 0; // 连续出现0的次数
        for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
            int idx = (chDig.length - i - 1) % 4; // 取段内位置
            int vidx = (chDig.length - i - 1) / 4; // 取段位置
            if (chDig[i] == '0') { // 如果当前字符是0
                preZero = true;
                zeroSerNum++; // 连续0次数递增
                if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
                    prefix += vunit[vidx - 1];
                    preZero = false; // 不管上一位是否为0，置为无效0位
                }
            } else {
                zeroSerNum = 0; // 连续0次数清零
                if (preZero) { // 上一位为有效0位
                    prefix += digit[0]; // 只有在这地方用到'零'
                    preZero = false;
                }
                prefix += digit[chDig[i] - '0']; // 转化该数字表示
                if (idx > 0)
                    prefix += hunit[idx - 1];
                if (idx == 0 && vidx > 0) {
                    prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
                }
            }
        }

        if (prefix.length() > 0)
            prefix += '元'; // 如果整数部分存在,则有圆的字样
        return prefix + suffix; // 返回正确表示
    }

    public static String changeToBig(double value) {
        char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
        char[] vunit = { '万', '亿' }; // 段名表示
        char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
        long midVal = (long) (value * 100); // 转化成整形
        String valStr = String.valueOf(midVal); // 转化成字符串
        String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
        String rail = valStr.substring(valStr.length() - 2); // 取小数部分

        String prefix = ""; // 整数部分转化的结果
        String suffix = ""; // 小数部分转化的结果
        // 处理小数点后面的数
        if (rail.equals("00")) { // 如果小数部分为0
            // suffix = "整";
            suffix = "";
        } else {
            suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
        }
        // 处理小数点前面的数
        char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
        boolean preZero = false; // 标志当前位的上一位是否为有效0位（如万位的0对千位无效）
        byte zeroSerNum = 0; // 连续出现0的次数
        for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
            int idx = (chDig.length - i - 1) % 4; // 取段内位置
            int vidx = (chDig.length - i - 1) / 4; // 取段位置
            if (chDig[i] == '0') { // 如果当前字符是0
                preZero = true;
                zeroSerNum++; // 连续0次数递增
                if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
                    prefix += vunit[vidx - 1];
                    preZero = false; // 不管上一位是否为0，置为无效0位
                }
            } else {
                zeroSerNum = 0; // 连续0次数清零
                if (preZero) { // 上一位为有效0位
                    prefix += digit[0]; // 只有在这地方用到'零'
                    preZero = false;
                }
                prefix += digit[chDig[i] - '0']; // 转化该数字表示
                if (idx > 0)
                    prefix += hunit[idx - 1];
                if (idx == 0 && vidx > 0) {
                    prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
                }
            }
        }

        if (prefix.length() > 0)
            prefix += '元'; // 如果整数部分存在,则有圆的字样
        return prefix + suffix; // 返回正确表示
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("_", "").replace("-", "");
    }

    /**
     * 给定字符替换为*
     *
     * @Title: preStr
     * @Description: TODO
     * @param str
     *            要替换的字符
     * @param s
     *            开始位置
     * @param length
     *            替换的字符个数
     * @return
     * @author -lwb 2017年6月6日 下午5:52:15
     */
    public static String preStr(String str, int s, int length) {
        if (StringUtils.isNotEmpty(str)) {
            int l = str.length();
            if (l < length) {
                length = l;
            }
            String flag = "*";
            for (int i = 0; i < length; i++) {
                flag += "*";
            }
            String aa = StringUtils.substring(str, s, s + length);
            return StringUtils.replace(str, aa, flag);
        }
        return null;
    }

    /**
     * map中like筛选
     *
     * @Title: getMapLike
     * @Description: TODO
     * @param map
     * @param key
     * @return
     * @author -lwb 2017年6月15日 下午6:11:11
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List getMapLike(Map map, String key) {
        List values = new ArrayList();
        Set<String> set = map.keySet();
        for (String string : set) {
            if (string.indexOf(key.toString()) == -1 && values.size() < 1) {
                continue;
            } else if (string.toString().indexOf(key.toString()) != -1) {
                values.add(map.get(string));
            } else {
                break;
            }
        }
        return values;

    }

    /**
     * 根据正则找到内容
     *
     * @Title: findTextByReg
     * @Description: TODO
     * @param text
     * @param reg
     *            (?<!\\d)(?:(?:1[358]\\d{9})|(?:861[358]\\d{9}))(?!\\d) 手机号
     *            ([1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2
     *            ][1-9])|10|20|30|31)\\d{3}[0-9Xx]$*)|(^[1-9]\\d{5}\\d{2}((0[1-
     *            9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$*) 身份证 15和18位
     * @return
     * @author -lwb 2017年6月26日 下午2:43:58
     */
    public static List<String> findTextByReg(String text, String reg) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(text);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 利用正则表达式判断字符串是....
     * @param str   "[0-9]*"   否是数字
     * @return
     */
    public static boolean isReg(String str,String reg){
        Pattern pattern = Pattern.compile(reg);
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     *
     * @Title: formatterBigMoney
     * @Description: 大写金额格式化
     * @param bigMoney
     * @return
     * @author fuxiaodong-lhq 2017年7月5日 下午5:53:24
     */
    public static String formatterBigMoney(String bigMoney) {
        if (null != bigMoney) {
            // 去掉开头的零
            while (true) {
                if (bigMoney.startsWith("零")) {
                    bigMoney = bigMoney.substring(2);
                } else {
                    break;
                }
            }
            // 零百(千,万,十等) -> 零
            bigMoney = bigMoney.replace("零万", "零").replace("零仟", "零").replace("零佰", "零").replace("零拾", "零");
            while (true) {
                if (bigMoney.contains("零零")) {
                    bigMoney = bigMoney.replace("零零", "零");
                } else {
                    break;
                }
            }
        }
        return bigMoney;
    }


    /**
     * str转unicode
     * @param source
     * @return
     */
    public static String unicode(String source){
        StringBuffer sb = new StringBuffer();
        char [] source_char = source.toCharArray();
        String unicode = null;
        for (int i=0;i<source_char.length;i++) {
            unicode = Integer.toHexString(source_char[i]);
            if (unicode.length() <= 2) {
                unicode = "00" + unicode;
            }
            sb.append("\\u" + unicode);
        }
        return sb.toString();
    }

    /**
     * unicode转String
     * @param unicode
     * @return
     */
    public static String decodeUnicode(String unicode) {
        StringBuffer sb = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            sb.append((char) data);
        }
        return sb.toString();
    }

    /**
     * unicode转String
     * @param
     * @return
     */
    public static String decodeUnicode2(String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = null;
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    /**
     * 随机不重号
     * @return
     */
    public static  long getNo(){
        int max = 999;
        int min = 100;
        int cardNumber = (int) (Math.random() * (max - min)) + min;
        int max1 = 999;
        int min1 = 100;
        int cardNumber1 = (int) (Math.random() * (max1 - min1)) + min1;
        String ms=System.nanoTime()+"";
        ms=ms.substring(0,6)+ms.substring(ms.length()-6,ms.length());
        return Long.parseLong(cardNumber1+ms+ cardNumber);
    }

    /**
     *  按指定数，反回集合
     * @param num 每页个数
     * @param list 原集合
     * @return
     */
    public static <T> List<List<T>>  getPageList(int num,List<T> list){
        if(list!=null&&!list.isEmpty()){
            List<List<T>> all=new ArrayList<>();
            if(list.size()<=num){
                all.add(list);
                return all;
            }else{
                int sum=list.size();
                int pagesize=(int)Math.ceil(Double.parseDouble(sum+"")/Double.parseDouble(num+""));//页数
                for (int i = 0; i < pagesize; i++) {
                    int s=i*num;
                    int e=s+num;
                    if(e>sum)e=sum;
                    all.add(list.subList(s, e));
                }
                return all;
            }
        }
        return new ArrayList<>() ;
    }

    public static String getHostName() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostName();
//		System.out.println(address.getHostName());//主机名
//		System.out.println(address.getCanonicalHostName());//主机别名
//		System.out.println(address.getHostAddress());//获取IP地址
//		System.out.println("===============");
    }
    public static String getHostIP() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
//		System.out.println(address.getHostName());//主机名
//		System.out.println(address.getCanonicalHostName());//主机别名
//		System.out.println(address.getHostAddress());//获取IP地址
//		System.out.println("===============");
    }

    /**
     * 获取年
     * @param date
     * @ret
     */
    public static String getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(1));
    }

    /**
     * 获取月
     * @param date
     * @ret
     */
    public static String getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(2) + 1);
    }

    /**
     * 获取日
     * @param date
     * @ret
     */
    public static String getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(5));
    }

    /**
     * 获取年月日
     * @param date
     * @return
     */
    public static Integer[] getYearMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer[] str = new Integer[3];
        str[0] = cal.get(1);
        str[1] = cal.get(2) + 1;
        str[2] = cal.get(5);
        return str;
    }

    /**
     * 根据生日获取被保人的年龄
     *
     * @param birthday
     * @return
     */
    public static int getInsurandAge(String birthday) throws ParseException {

        Date birth = Util.formatDate(birthday, "yyyy-MM-dd");
        Integer[] birthdays = getYearMonthDay(birth);
        Integer[] nows = getYearMonthDay(new Date());

        int num = 0;
        num = nows[0] - birthdays[0]-1;

        if (nows[1] > birthdays[1]) {
            num++;
        } else if (nows[1] == birthdays[1]) {
            if (nows[2] == birthdays[2]) {
                num++;
            } else if (nows[2] > birthdays[2]) {
                num++;
            }

        }
        return num;

    }

    /**
     * 复制类对象
     * @param source  原对象
     * @param target   目标对象
     * @return
     */
    public static void copyProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 复制类对象
     * @param source  原对象
     * @param target  目标对象
     * @param ignoreProperties  用需要拷贝的属性
     */
    public static void copyProperties(Object source, Object target,String... ignoreProperties ){
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 复制类对象
     * @param source  原对象
     * @param target   目标对象
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void copyBean(Object source, Object target) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
        PropertyUtils.copyProperties(target, source);
    }

    /**
     * 得到文件路径
     * @param fileName
     * @return
     */
    public static String getDefaultPath(String fileName) {
        return ClassUtils.getDefaultClassLoader().getResource(fileName).getPath();
    }

    /**
     *
     * @return
     */
    public static String getDefaultPath() {
        return ClassUtils.getDefaultClassLoader().getResource("").getPath();
    }

    /**
     * 判断输入的是不是中文
     *
     * @param str
     */
    public static boolean isChinese(String str) {
        if (str.length() < str.getBytes().length) {
            return true;
        } else {
            return false;
        }
    }


    public static void render(HttpServletResponse response, String text, String contentType) throws IOException {
        response.setContentType(contentType);
        response.getWriter().write(text);
    }

    public static void renderText(HttpServletResponse response, String text) throws IOException {
        render(response, text, "text/plain;charset=UTF-8");
    }

    public static void renderHtml(HttpServletResponse response, String html) throws IOException {
        render(response, html, "text/html;charset=UTF-8");
    }

    public static void renderXML(HttpServletResponse response, String xml) throws IOException {
        render(response, xml, "text/xml;charset=UTF-8");
    }

    public static void renderJSON(HttpServletResponse response, String xml) throws IOException {
        render(response, xml, "application/json;charset=UTF-8");
    }

    public static String URLEncoder(String str) throws UnsupportedEncodingException{
        return URLEncoder.encode(str,"UTF-8");
    }

    public static String URLDecoder(String str) throws UnsupportedEncodingException{
        return URLDecoder.decode(str,"UTF-8");
    }

    /**
     * 中文专utf-8
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String toUtf8(String str) throws UnsupportedEncodingException{
        String mstr=str;
        if (str.length() < str.getBytes().length) {
            mstr= new String(str.getBytes(),"utf-8");
        }
        System.out.println(str+"============"+mstr);
        return mstr;
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }
}
