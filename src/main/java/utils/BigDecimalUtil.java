package utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;

public class BigDecimalUtil {

    public static final String DOUBLE = ".##"; // double的小数位

    public static BigDecimal add(BigDecimal... v) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal d : v) {
            result = result.add(d);
        }
        return result;
    }

    public static BigDecimal minus(BigDecimal d1, BigDecimal d2) {
        return d1.subtract(d2);
    }

    public static BigDecimal multiply(BigDecimal... v) {
        BigDecimal result = BigDecimal.ONE;
        for (BigDecimal d : v) {
            result = result.multiply(d);
        }
        return result;
    }

    public static BigDecimal pow(BigDecimal v, int num) {
        BigDecimal result = BigDecimal.ONE;
        for (int i = 0; i < num; i++) {
            result = multiply(result, v);
        }
        return result;
    }

    public static BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("精度要求大于0的整数");
        }
        if (v2 == BigDecimal.ZERO) {
            throw new IllegalArgumentException("分母不能为0");
        }
        return v1.divide(v2, scale, 4);
    }

    public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {
        if (v2 == BigDecimal.ZERO) {
            throw new IllegalArgumentException("分母不能为0");
        }
        return v1.divide(v2);
    }

    public static Double formatD10(Double d) {
        d = Double.valueOf(round(d.doubleValue(), 10));
        Format format = new DecimalFormat("#0.0000000000");
        return Double.valueOf(new BigDecimal(format.format(d)).doubleValue());
    }

    public static Double formatD6(Double d) {
        d = Double.valueOf(round(d.doubleValue(), 6));
        Format format = new DecimalFormat("#0.000000");
        return Double.valueOf(new BigDecimal(format.format(d)).doubleValue());
    }

    public static Double formatD4(Double d) {
        d = Double.valueOf(round(d.doubleValue(), 4));
        Format format = new DecimalFormat("#0.0000");
        return Double.valueOf(new BigDecimal(format.format(d)).doubleValue());
    }

    public static Double formatD2(BigDecimal d) {
        Format format = new DecimalFormat("#0.00");
        return Double.valueOf(new BigDecimal(format.format(d)).doubleValue());
    }

    public static double round(double value, int scale) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(value));
        return bigDecimal.setScale(scale, 4).doubleValue();
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

    public static void main(String[] args) {
        BigDecimal a =new BigDecimal("5");
        BigDecimal b =new BigDecimal("7");
        System.out.println(a.divide(b,4,4));
    }

}
