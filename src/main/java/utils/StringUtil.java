package utils;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class StringUtil {

    private static class CompareString {
        private String compareContent;
        private String type;


        public String getCompareContent() {
            return compareContent;
        }

        public void setCompareContent(String compareContent) {
            this.compareContent = compareContent;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    // 判断是否整数
    public static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }

    /**
     * 判断字符串中是否存在重复字符,前提，该字符串需要切割
     *
     * @param input
     * @return
     */
    public static boolean isNotDumpNeedSplit(String s, String splitFlag) {
        String[] strArr = s.split(splitFlag);
        int i = 0, j = 1;
        while (i < strArr.length) {
            while (j < strArr.length) {
                if (strArr[i].equals(strArr[j])) {
                    return false;
                }
                j++;
            }
            i++;
        }
        return true;
    }

    /**
     * 判断字符串中是否存在重复字符
     *
     * @param input
     * @return
     */
    public static boolean isNotDumpVCommStr(String s) {
        int i = 0, j = 1;
        while (i < s.length()) {
            while (j < s.length()) {
                if (s.charAt(i) == s.charAt(j)) {
                    return false;
                }
                j++;
            }
            i++;
        }
        return true;
    }

    /**
     *
     * 目前支持分隔累计数
     *
     * @param text
     * @param separator
     * @param isPrefix
     * @param isPostfix
     * @param step
     * @return
     * @throws Exception
     */
    public static String counter(String text, String separator, boolean isPrefix, boolean isPostfix, String step)
            throws Exception {
        int subContentSize = 2;
        if (separator == null) {
            return null;
        }
        String tmpSeparator = null;
        if (separator.equals(".") || separator.equals("|")) {
            tmpSeparator = "//" + separator;
        } else {
            tmpSeparator = separator;
        }
        String[] contentArray = text.split(tmpSeparator);
        if (contentArray == null || contentArray.length != subContentSize) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (isPrefix) {
            String oldPrefix = contentArray[0];
            String prefix = "" + (Integer.parseInt(oldPrefix) + Integer.parseInt(step));
            sb.append(prefix);
        } else {
            sb.append(contentArray[0]);
        }
        sb.append(separator);
        if (isPostfix) {
            String oldPostfix = contentArray[0];
            String postfix = "" + (Integer.parseInt(oldPostfix) + Integer.parseInt(step));
            sb.append(postfix);
        } else {
            sb.append(contentArray[1]);
        }

        return sb.toString();
    }

    private static String[] chineseDigits = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
    // ===============suizhiwei add 货币数字中文转换end
    /**
     * 阳光安盛产品全球国家或地区的可选国家中代码与名称的对应关系 增加“日本” yuxiaomei ，2012-6-25
     */
    public static String[] COUNTRY_CODE = new String[] { "奥地利Austria ", "比利时Belgium ", "丹麦Denmark ", "芬兰Finland ",
            "法国France ", "德国Germany ", "希腊Greece ", "冰岛Iceland ", "意大利Italy ", "卢森堡Luxembourg ", "荷兰Netherlands ",
            "挪威Norway ", "葡萄牙Portugal ", "西班牙Spain ", "瑞典Sweden ", "爱沙尼亚Estonia ", "匈牙利Hungary ", "拉托维亚Latvia ",
            "立陶宛Lithuania ", "波兰Poland ", "斯洛伐克Slovakia ", "斯洛文尼亚Slovenia ", "捷克Czech ", "马耳他Malta ", "瑞士Switzerland ",
            "其它 ", "日本Japan " };

    public static boolean isNull(String str) {
        boolean re = false;
        if (str == null || "".equals(str.trim()) || "null".equalsIgnoreCase(str.trim())) {
            re = true;
        }
        return re;
    }

    public static boolean notNull(String str) {
        return !isNull(str);
    }

    public static boolean notAllNull(String... strings) {
        for (String s : strings) {
            if (notNull(s))
                return true;
        }
        return false;
    }

    /**
     * 将yyyy-mm-dd或者yyyy-mm-dd HH:mm:ss格式的日期字符串转换成yyyy年mm月dd日的格式
     *
     * @param fromDate
     * @return 转换后的日期字符串
     * @throws IllegalArgumentException
     */
    public static String changeDateFormat(String fromDate) throws IllegalArgumentException {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date toDate = formatDate.parse(fromDate);
            String date = formatDate.format(toDate);
            String separator = "-";
            StringTokenizer token = new StringTokenizer(date, separator);
            String year = token.nextToken();
            String month = token.nextToken();
            String day = token.nextToken();
            StringBuilder sb = new StringBuilder();
            return sb.append(year).append("年").append(month).append("月").append(day).append("日").toString();
        } catch (ParseException e) {
            throw new IllegalArgumentException("传入的日期不符合转换格式！", e);
        }
    }

    /**
     * 生成发送给短信平台的id，根据要求如下形式：INYYYYMMDDHHMMSSSSS+SEQ(6位)
     *
     * @param 从数据库取出来的短信seq:SEQ_BIZ_SMS
     * @return
     */
    public static String getMsgSendID(String seq) {
        String id = "";
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        String strdate = formatDate.format(date);
        id = "IN" + strdate + getSequence(seq);
        return id;
    }

    /**
     * 获取6位流水号，不足6位左边补0，大于6位，截取后6位
     *
     * @param sequence
     * @return
     */
    public static String getSequence(String sequence) {
        String rtnStr = "";
        int len = sequence.length();
        if (len < 6) {
            rtnStr = StringUtils.leftPad(sequence, 6, '0');
        } else {
            rtnStr = sequence.substring(len - 6, len);
        }

        return rtnStr;
    }

    /**
     * 将yyyy-mm-dd或者yyyy-mm-dd HH:mm:ss格式的日期字符串转换成*年*月*日 HH:mm:ss的格式 add by
     * gxy,2010-09-07
     *
     * @param date
     * @return
     */
    public static String formatInsurencePeriod(String indate) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String rtnDate = "";
        try {
            Date inTodate = formatDate.parse(indate);
            String date = formatDate.format(inTodate);
            String separator = "-";
            StringTokenizer token = new StringTokenizer(date, separator);
            String year = token.nextToken();
            String month = token.nextToken();
            String day = token.nextToken();
            rtnDate = year + "年" + month + "月" + day + "日";
            try {
                Date inTodate2 = formatDate2.parse(indate);
                String date2 = formatDate2.format(inTodate2);
                rtnDate = rtnDate + date2.substring(10);
            } catch (Exception e) {
                rtnDate += " 00:00:00";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return rtnDate;
    }

    /**
     * 阳光安盛产品投保人与被保人关系转换成中英文全包含的方式
     *
     * @param relation
     * @return
     */
    public static String ASholderRelationToInsured(String relation) {
        String relationship = "";
        try {
            switch (Integer.parseInt(relation)) {
                case 10:
                    relationship = "本人(Policy Owner)";
                    break;
                case 61:
                    relationship = "配偶(Spouse)";
                    break;
                case 62:
                    relationship = "父母(Parent)";
                    break;
                case 63:
                    relationship = "子女(Child)";
                    break;
                case 64:
                    relationship = "其他具有抚养或赡养关系的家庭成员或近亲属(Relation)";
                    break;
                case 65:
                    relationship = "劳动关系(Employer)";
                    break;
                case 66:
                    relationship = "丈夫(Husband)";
                    break;
                case 67:
                    relationship = "妻子(Wife)";
                    break;
                case 68:
                    relationship = "父亲(Father)";
                    break;
                case 69:
                    relationship = "母亲(Mother)";
                    break;
                case 70:
                    relationship = "儿子(Son)";
                    break;
                case 71:
                    relationship = "女儿(Daughter)";
                    break;
                case 99:
                    relationship = "其它(Others)";
                    break;
            }
        } catch (NumberFormatException nfe) {
            relationship = "其它(Others)";
        }
        return relationship;

    }

    /**
     * 投保人与被保人关系转为中文
     *
     * @param relation
     * @author zhanglichao-ghq
     * @return
     */
    public static String convertRelationTypeToCH(String relation) {
        String relationship = "";
        try {
            switch (Integer.parseInt(relation)) {
                case 10:
                    relationship = "本人";
                    break;
                case 61:
                    relationship = "配偶";
                    break;
                case 62:
                    relationship = "父母";
                    break;
                case 63:
                    relationship = "子女";
                    break;
                case 64:
                    relationship = "其他具有抚养或赡养关系的家庭成员或近亲属";
                    break;
                case 65:
                    relationship = "劳动关系";
                    break;
                case 66:
                    relationship = "丈夫";
                    break;
                case 67:
                    relationship = "妻子";
                    break;
                case 68:
                    relationship = "父亲";
                    break;
                case 69:
                    relationship = "母亲";
                    break;
                case 70:
                    relationship = "儿子";
                    break;
                case 71:
                    relationship = "女儿";
                    break;
                case 99:
                    relationship = "其它";
                    break;
                default:
                    relationship = "其它";
                    break;
            }
        } catch (NumberFormatException nfe) {
            relationship = "其它";
        }
        return relationship;
    }

    /**
     * 将证件类型转换为中文
     *
     * @param 证件类型
     */
    public static String convertCertificateTypeToCH(String certificateType) {
        String certificate = "";
        try {
            switch (Integer.parseInt(certificateType)) {
                case 10:
                    certificate = "身份证";
                    break;
                case 11:
                    certificate = "户口薄";
                    break;
                case 12:
                    certificate = "驾驶证";
                    break;
                case 13:
                    certificate = "军官证";
                    break;
                case 14:
                    certificate = "士兵证";
                    break;
                case 17:
                    certificate = "港澳通行证";
                    break;
                case 18:
                    certificate = "台湾通行证";
                    break;
                // TODO 所有渠道都升级完了再删除60
                case 60:
                case 51:
                    certificate = "护照";
                    break;
                case 61:
                    certificate = "港澳台同胞证";
                    break;
                case 99:
                    certificate = "其它";
                    break;
                default:
                    certificate = "其它";
                    break;
            }
        } catch (NumberFormatException nfe) {
            certificate = "其它";
        }
        return certificate;
    }

    /**
     * 将证件类型转换为电子保单系统需要的证件类型码
     *
     * @param 证件类型
     *            电子保单系统所需对应码值：身份证:0转为1
     *            ；护照：1转为3；军官证：2转为4；港台同胞证:6转为5；户口本：5转为11；其他：8转为10 author wxy
     *            2013-12-11
     */
    public static String convertCertificateTypeToForEform(String certificateType) {
        String certificate = "";
        try {
            switch (Integer.parseInt(certificateType)) {
                case 0:
                    certificate = "1";
                    break;
                case 1:
                    certificate = "3";
                    break;
                case 2:
                    certificate = "4";
                    break;
                case 5:
                    certificate = "11";
                    break;
                case 6:
                    certificate = "5";
                    break;
                case 8:
                    certificate = "10";
                    break;

                default:
                    certificate = certificateType;
                    break;
            }
        } catch (NumberFormatException nfe) {
            certificate = certificateType;
        }
        return certificate;
    }

    /**
     * 将阳光安盛中全球国家和地区中选择的国家代码转换为可传给电子保单系统的国家中英文名称， 并根据电子保单的长度适当加入换行符
     *
     * @param routeCode：各国家以数字代码标注，多个代码间以逗号分隔
     * @return：加入换行符后的各国家中英文字符串
     */
    public static String getTourRoute(String routeCode) {
        int maxLength = 60;
        String rtn = "";
        String originStr = "";
        String[] codeStr = routeCode.split(",");
        String[] countryCode = StringUtil.COUNTRY_CODE;
        String[] countryName = new String[codeStr.length];
        for (int i = 0; i < codeStr.length; i++) {
            int index = Integer.parseInt(codeStr[i]);
            countryName[i] = countryCode[index - 1];
            originStr += countryName[i];// 选择国家组成的原始串
        }
        // 将原始串适当的加入换行符
        int rows = originStr.length() / maxLength;
        if (originStr.length() % maxLength != 0) {
            rows++;
        }
        String[] arr = new String[rows];
        for (int a = 0; a < rows; a++) {
            arr[a] = "";
        }
        int j = 0;
        for (int t = 0; t < countryName.length;) {
            int preLen = arr[j].length() + countryName[t].length();
            if (preLen < maxLength) {
                arr[j] += countryName[t];
                t++;
            } else {
                if (j == rows - 1) {
                    break;
                } else {
                    j++;
                }
            }
        }
        for (int k = 0; k < arr.length; k++) {
            if (k < arr.length - 1) {
                rtn = rtn + arr[k] + "\n";
            } else
                rtn = rtn + arr[k];
        }
        return rtn;
    }

    /**
     * 校验字符串长度是否超过限制，汉字长度为2
     *
     * @param str
     * @param limitLength
     * @return 未超过返回true，否则返回false
     */
    public static boolean isNotOverLength(String str, int limitLength) {
        try {
            if (StringUtils.isEmpty(str) || str.getBytes("GBK").length <= limitLength) {
                return true;
            }
            return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static boolean isOverLength(String str, int limitLength) {
        return !isNotOverLength(str, limitLength);
    }

    /**
     *
     * @param intStr
     * @return inStr为null、""和空白字符时返回0
     * @throws NumberFormatException
     *             不能转换为整数时抛出
     */
    public static int parseInt(String intStr) throws NumberFormatException {
        if (StringUtils.isBlank(intStr)) {
            return 0;
        }
        return Integer.parseInt(intStr);
    }

    /**
     *
     * @param bigDecimalStr
     * @return bigDecimalStr为null、""和空白字符时返回null
     * @throws NumberFormatException
     */
    public static BigDecimal parseBigDecimal(String bigDecimalStr) throws NumberFormatException {
        if (StringUtils.isBlank(bigDecimalStr)) {
            return null;
        }
        return new BigDecimal(bigDecimalStr);
    }

    /**
     * 验证电话号码是否符合规范
     *
     * @param 电话号码
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b> add by zhanglichao
     *         ,2011-02-22
     */
    public static boolean isMobile(String mobileNo) {
        if (StringUtils.isBlank(mobileNo)) {
            return true;
        }
        String regex = "^((13[0-9])|(15[0-9])|(18[0-9])|(14[0-9])|(17[0-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobileNo);
        return matcher.matches();
    }

    /**
     * 验证邮箱地址是否符合规范
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return true;
        }
        // String regex =
        // "^([a-z0-9A-Z]+[\\w-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        // 修改邮箱的校验正则表达式 modifed by yuxiaomei,2012-9-13
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 去掉xml字符串前面的回车换行
     *
     * @param xmlStr
     * @return added by yuxiaomei 2011.6.1
     */
    public static String removeLFbeforeXML(String xmlStr) {

        Pattern p = Pattern.compile("\n<?xml|\r<?xml|\r\n<?xml|\n\r<?xml");
        Matcher m = p.matcher(xmlStr.trim());
        String after = m.replaceAll("<?xml");

        return after;
    }

    // @SuppressWarnings("unchecked")
    // public static Map<String,Object> parserToMap(String s){
    // Map<String,Object> map=new HashMap<String,Object>();
    // JSONObject json=JSONObject.fromObject(s);
    // Iterator<String> keys = json.keys();
    // while(keys.hasNext()){
    // String key=(String) keys.next();
    // String value=json.get(key).toString();
    // if(value.startsWith("{")&&value.endsWith("}")){
    // map.put(key, parserToMap(value));
    // }else{
    // map.put(key, value);
    // }
    // }
    // return map;
    // }
    public static String bigDecimalToFen(BigDecimal bigDecimal) {
        BigDecimal unit = new BigDecimal(100);
        BigDecimal fenDecimal = bigDecimal.multiply(unit);
        int fen = fenDecimal.intValue();
        return String.valueOf(fen);
    }

    /**
     * 将中英文混合的省名转换为中文 ， 如 贵州/GUIZHOU,返回 贵州
     *
     * @param province
     * @return
     */
    public static String convertProvince(String province) {
        if (StringUtil.isNull(province)) {
            return "";
        }

        if (province.indexOf('/') < 0) {
            return province;
        } else {
            int index = province.indexOf('/');
            return province.substring(0, index);
        }
    }

    public static String getNodeText(byte[] xmlBytes, String nodePath) {
        String rtnStr = "";
        InputStream is = null;
        Document doc = null;
        try {
            is = new ByteArrayInputStream((byte[]) xmlBytes);
            InputStreamReader strInStream = new InputStreamReader(is, "GBK");
            // 解析器
            SAXReader reader = new SAXReader();
            // reader.setEncoding("GBK");
            // 读取并解析文件
            doc = reader.read(strInStream);
            Node node = doc.selectSingleNode(nodePath);
            rtnStr = node.getText();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is!=null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rtnStr;
    }

    /**
     * 去掉字符串中的空格和回车换行
     *
     * @param originalStr
     * @return
     */
    public static String removeLF(String originalStr) {
        return originalStr.trim().replaceAll("\r\n", "").replaceAll("\n\r", "").replaceAll("\r", "").replaceAll("\n",
                "");
    }

    public static String getBankName(String bankCode) throws Exception {
        if ("ICBC".equals(bankCode) || "0102".equals(bankCode) || "102".equals(bankCode)) {
            return "工商银行";
        } else if ("ABC".equals(bankCode) || "0103".equals(bankCode) || "103".equals(bankCode)) {
            return "农业银行";
        } else if ("CCB".equals(bankCode) || "0105".equals(bankCode) || "105".equals(bankCode)) {
            return "建设银行";
        } else if ("BOC".equals(bankCode) || "0104".equals(bankCode) || "104".equals(bankCode)) {
            return "中国银行";
        } else if ("PSBC".equals(bankCode) || "0100".equals(bankCode) || "403".equals(bankCode)) {
            return "邮政储蓄银行";
        } else if ("CEB".equals(bankCode) || "0303".equals(bankCode) || "303".equals(bankCode)) {
            return "光大银行";
        } else if ("CIB".equals(bankCode) || "0309".equals(bankCode) || "309".equals(bankCode)) {
            return "兴业银行";
        } else if ("CMB".equals(bankCode) || "0308".equals(bankCode) || "308".equals(bankCode)) {
            return "招商银行";
        } else if ("CMBC".equals(bankCode) || "0305".equals(bankCode) || "305".equals(bankCode)) {
            return "民生银行";
        } else if ("PABC".equals(bankCode) || "0410".equals(bankCode) || "783".equals(bankCode)) {
            return "平安银行";////
        } else if ("0302".equals(bankCode) || "302".equals(bankCode)) {
            return "中信银行";
        } else if ("0401".equals(bankCode)) {
            return "上海银行";///
        } else if ("BOB".equals(bankCode) || "0403".equals(bankCode)) {
            return "北京银行";///
        } else if ("SPDB".equals(bankCode) || "0310".equals(bankCode) || "310".equals(bankCode)) {
            return "浦发银行";
        }
        throw new Exception("银行代码传入错误,bankCode:" + bankCode);

    }

    /**
     * 将学校类型转换为中文
     *
     * @param 学校类型
     */
    public static String convertSchoolTypeToCH(String schoolType) {
        String schoolTypeName = "";
        try {

            switch (Integer.parseInt(schoolType)) {
                case 0:
                    schoolTypeName = "其他";
                    break;
                case 1:
                    schoolTypeName = "中职";
                    break;
                case 2:
                    schoolTypeName = "高职";
                    break;
                case 3:
                    schoolTypeName = "幼儿园";
                    break;
                case 4:
                    schoolTypeName = "小学";
                    break;
                case 5:
                    schoolTypeName = "中学";
                    break;
                case 6:
                    schoolTypeName = "大学";
                    break;
                case 7:
                    schoolTypeName = "成人学校（不含各级各类职业院校）";
                    break;
                case 8:
                    schoolTypeName = "工读学校";
                    break;
                case 9:
                    schoolTypeName = "特殊教育机构";
                    break;
                default:
                    schoolTypeName = "其他";
                    break;
            }
        } catch (NumberFormatException nfe) {
            schoolTypeName = "其他";
        }
        return schoolTypeName;
    }

    /**
     * 邮编，六位数字
     *
     * @param zipCode
     * @return
     * @author yuxiaomei-ghq，2014-10-28
     */
    public static boolean isZipCode(String zipCode) {
        if (StringUtils.isBlank(zipCode)) {
            return true;
        }
        String rule = "[0-9]{6}";
        return Pattern.compile(rule).matcher(zipCode).matches();
    }

    // ======================suizhiwei add begin===================
    /**
     * * 把金额转换为汉字表示的数量，小数点后四舍五入保留两位
     *
     * @param amount
     *            *
     * @return
     */
    public static String amountToChinese(BigDecimal amount) {
        return amountToChinese(amount.doubleValue());
    }

    /**
     * * 把金额转换为汉字表示的数量，小数点后四舍五入保留两位
     *
     * @param amount
     *            *
     * @return
     */
    public static String amountToChinese(double amount) {
        if (amount > 99999999999999.99 || amount < -99999999999999.99)
            throw new IllegalArgumentException("参数值超出允许范围 (-99999999999999.99 ～ 99999999999999.99)！");
        boolean negative = false;
        if (amount < 0) {
            negative = true;
            amount = amount * (-1);
        }
        long temp = Math.round(amount * 100);
        int numFen = (int) (temp % 10);
        // 分
        temp = temp / 10;
        int numJiao = (int) (temp % 10);
        // 角
        temp = temp / 10;
        // temp 目前是金额的整数部分
        int[] parts = new int[20];
        // 其中的元素是把原来金额整数部分分割为值在 0~9999 之间的数的各个部分
        int numParts = 0;
        // 记录把原来金额整数部分分割为了几个部分（每部分都在 0~9999 之间）
        for (int i = 0;; i++) {
            if (temp == 0)
                break;
            int part = (int) (temp % 10000);
            parts[i] = part;
            numParts++;
            temp = temp / 10000;
        }
        boolean beforeWanIsZero = true;
        // 标志“万”下面一级是不是 0
        String chineseStr = "";
        for (int i = 0; i < numParts; i++) {
            String partChinese = partTranslate(parts[i]);
            if (i % 2 == 0) {
                if ("".equals(partChinese))
                    beforeWanIsZero = true;
                else
                    beforeWanIsZero = false;
            }
            if (i != 0) {
                if (i % 2 == 0)
                    chineseStr = "亿" + chineseStr;
                else {
                    if ("".equals(partChinese) && !beforeWanIsZero)
                        // 如果“万”对应的 part 为 0，而“万”下面一级不为 0，则不加“万”，而加“零”
                        chineseStr = "零" + chineseStr;
                    else {
                        if (parts[i - 1] < 1000 && parts[i - 1] > 0)
                            // 如果"万"的部分不为 0, 而"万"前面的部分小于 1000 大于 0， 则万后面应该跟“零”
                            chineseStr = "零" + chineseStr;
                        chineseStr = "萬" + chineseStr;
                    }
                }
            }
            chineseStr = partChinese + chineseStr;
        }
        if ("".equals(chineseStr))
            // 整数部分为 0, 则表达为"零元"
            chineseStr = chineseDigits[0];
        else if (negative)
            // 整数部分不为 0, 并且原金额为负数
            chineseStr = "负" + chineseStr;
        chineseStr = chineseStr + "圆";
        if (numFen == 0 && numJiao == 0) {
            chineseStr = chineseStr + "整";
        } else if (numFen == 0) {
            // 0 分，角数不为 0
            chineseStr = chineseStr + chineseDigits[numJiao] + "角";
        } else { // “分”数不为 0
            if (numJiao == 0)
                chineseStr = chineseStr + "零" + chineseDigits[numFen] + "分";
            else
                chineseStr = chineseStr + chineseDigits[numJiao] + "角" + chineseDigits[numFen] + "分";
        }
        return chineseStr;
    }

    /**
     * * 把一个 0~9999 之间的整数转换为汉字的字符串，如果是 0 则返回 "" * @param amountPart * @return
     */
    private static String partTranslate(int amountPart) {
        if (amountPart < 0 || amountPart > 10000) {
            throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！");
        }
        String[] units = new String[] { "", "拾", "佰", "仟" };
        int temp = amountPart;
        String amountStr = new Integer(amountPart).toString();
        int amountStrLength = amountStr.length();
        boolean lastIsZero = true;
        // 在从低位往高位循环时，记录上一位数字是不是 0
        String chineseStr = "";
        for (int i = 0; i < amountStrLength; i++) {
            if (temp == 0) // 高位已无数据
                break;
            int digit = temp % 10;
            if (digit == 0) { // 取到的数字为 0
                if (!lastIsZero) // 前一个数字不是 0，则在当前汉字串前加“零”字;
                    chineseStr = "零" + chineseStr;
                lastIsZero = true;
            } else {
                // 取到的数字不是 0
                chineseStr = chineseDigits[digit] + units[i] + chineseStr;
                lastIsZero = false;
            }
            temp = temp / 10;
        }
        return chineseStr;
    }
}
