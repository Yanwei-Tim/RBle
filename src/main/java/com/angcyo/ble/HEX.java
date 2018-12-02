package com.angcyo.ble;

import org.jetbrains.annotations.NotNull;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/11/12
 */
public class HEX {


    /**
     * 将 0x AA BB CC 转换成 byte[]
     */
    public static byte[] cmd(int size, String... hex) {
        byte[] cmd = HEX.initBytes(size);
        for (int i = 0; i < hex.length; i++) {
            byte[] m = HEX.hexStringToByteArray(fixHexString(hex[i]));
            HEX.bindData(cmd, m, i);
        }
        return cmd;
    }

    /**
     * 用0x00, 初始化bytes数组
     */
    public static byte[] initBytes(int size) {
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = 0x00;
        }
        return data;
    }

    /**
     * @param fill 填充到数组中的数据
     * @param size 需要生成的数组大小
     */
    public static byte[] bindData(byte[] fill, int size) {
        size = Math.max(fill.length, size);
        byte[] data = initBytes(size);
        System.arraycopy(fill, 0, data, 0, fill.length);
        return data;
    }

    public static void bindData(byte[] src, byte[] fill, int offset) {
        System.arraycopy(fill, 0, src, offset, fill.length);
    }

    public static String hexString(@NotNull byte[] data) {
        return BytesHexStrTranslate.bytesToHexFun2(data);
    }

    public static String[] hexStringArray(@NotNull byte[] data) {
        return hexStringToStringArray(hexString(data));
    }

    /**
     * byte 十进制转换成 十六进制字符串
     */
    public static String hexString(@NotNull byte[] data, int start, int end) {
        return BytesHexStrTranslate.bytesToHexFun2(data, start, end);
    }

    /**
     * 十六进制字符串, 每个2个 添加一个空格. 美观输出
     */
    public static String formatHex(@NotNull String hex) {
        return formatHex(hex, ' ');
    }

    public static String formatHex(@NotNull String hex, char split) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= hex.length(); i += 2) {
            if (i > 0 && i % 2 == 0) {
                builder.append(hex.substring(i - 2, i));

                //最后面不需要添加分隔符
                if (i != hex.length()) {
                    builder.append(split);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 将 CE95160F -> 206.149.22.14
     */
    public static String formatHexToInt(@NotNull String hex, char split) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= hex.length(); i += 2) {
            if (i > 0 && i % 2 == 0) {
                try {
                    builder.append(Integer.parseInt(hex.substring(i - 2, i), 16));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    builder.append("00");
                }

                //最后面不需要添加分隔符
                if (i != hex.length()) {
                    builder.append(split);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 每2位十六进制 换成 十进制数据返回
     */
    public static int[] hexStringToIntArray(@NotNull String hex) {
        hex = fixHexString(hex);

        int size = hex.length() / 2;

        int[] result = new int[size];

        int index = 0;
        for (int i = 0; i <= hex.length(); i += 2) {
            if (i > 0 && i % 2 == 0) {
                String substring = hex.substring(i - 2, i);
                result[index++] = Integer.parseInt(substring, 16);
            }
        }
        return result;
    }

    public static byte[] hexStringToByteArray(@NotNull String hex) {
        hex = fixHexString(hex);

        int size = hex.length() / 2;

        byte[] result = new byte[size];

        int index = 0;
        for (int i = 0; i <= hex.length(); i += 2) {
            if (i > 0 && i % 2 == 0) {
                String substring = hex.substring(i - 2, i);
                result[index++] = (byte) Integer.parseInt(substring, 16);
            }
        }
        return result;
    }

    public static String[] hexStringToStringArray(@NotNull String hex) {
        hex = fixHexString(hex);

        int size = hex.length() / 2;

        String[] result = new String[size];

        int index = 0;
        for (int i = 0; i <= hex.length(); i += 2) {
            if (i > 0 && i % 2 == 0) {
                String substring = hex.substring(i - 2, i);
                result[index++] = substring;
            }
        }
        return result;
    }

    /**
     * 不足偶数位时, 向前补0
     */
    public static String fixHexString(@NotNull String hex) {
        hex = hex.replaceAll(" ", "");
        if (hex.length() % 2 != 0) {
            //补齐
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 小字节数据.
     */
    public static String reverse(@NotNull String[] hexArray) {
        StringBuilder builder = new StringBuilder();
        for (int i = hexArray.length - 1; i >= 0; i--) {
            builder.append(hexArray[i]);
        }
        return builder.toString();
    }

    public static String reverse(@NotNull String hexString) {
        return reverse(hexStringToStringArray(hexString));
    }

    /**
     * 将ip格式(192.168.0.0) 转换成 十六进制 C0A80000
     */
    public static String ipToHexString(@NotNull String ip) {
        StringBuilder result = new StringBuilder();
        String[] split = ip.split("\\.");
        for (int i = 0; i < 4; i++) {
            if (i < split.length) {
                String upperCase = Integer.toHexString(Integer.parseInt(split[i])).toUpperCase();
                if (upperCase.length() < 2) {
                    result.append("0");
                }
                result.append(upperCase);
            } else {
                result.append("00");
            }
        }
        return result.toString();
    }

    /**
     * int整型字符串, 转换成 十六进制字符
     * 3659533 (十进制) -> 0x37D70D
     */
    public static String stringToHexString(@NotNull String intString,
                                           int size /*需要输出多少个字节, 1个字节等于8位, 等于2位十六进制*/) {
        String hexString = "";
        try {
            hexString = Integer.toHexString(Integer.parseInt(intString));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size * 2 - hexString.length(); i++) {
            builder.append("0");
        }
        builder.append(hexString);
        return builder.toString();
    }
}
