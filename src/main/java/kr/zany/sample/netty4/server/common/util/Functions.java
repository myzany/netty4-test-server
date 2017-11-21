package kr.zany.sample.netty4.server.common.util;

import com.google.common.base.Splitter;
import jodd.util.StringUtil;
import kr.zany.sample.netty4.server.common.data.CommonResultVo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * <b>공통 기능 모듈</b>
 * <p>&nbsp;</p>
 *
 * <p>Copyright ⓒ 2015 kt corp. All rights reserved.</p>
 *
 * @author Lee Sang-Hyun (zanylove@gmail.com)
 * @since 2015. 10. 01.
 */
public final class Functions {

    public static enum LogType {
        NONE, ERROR, WARNING, INFO, DEBUG, TRACE
    };

    // ------------------------------------------------------------------------
    // Common Result Object 관련
    // ------------------------------------------------------------------------

    /**
     * <b>범용 리턴 객체인 CommonResult 객체를 초기화 한다.</b>
     *
     * @return
     */
    public static CommonResultVo buildCommonResult() {
        return buildCommonResult(0, "ok", "", LogType.NONE);
    }

    public static CommonResultVo buildCommonResult(final int resultCode, final String resultMsg) {
        return buildCommonResult(resultCode, resultMsg, "", (resultCode != 0) ? LogType.WARNING : LogType.NONE);
    }

    public static CommonResultVo buildCommonResult(final int resultCode, final String resultMsg, final String errorDetail) {
        return buildCommonResult(resultCode, resultMsg, errorDetail, (resultCode != 0) ? LogType.WARNING : LogType.NONE);
    }

    /**
     * <b>범용 리턴 객체인 CommonResult 객체를 초기화 한다.</b>
     *
     * @param resultCode 응답 코드
     * @param resultMsg 응답 메시지
     * @param errorDetail 에러 상세 메시지
     * @param logType 로그 메시지 유형
     * @return
     */
    public static CommonResultVo buildCommonResult(final int resultCode, final String resultMsg, final String errorDetail, LogType logType) {

        if (logType != LogType.NONE) {

            final String logMsg    = String.format("[%d] %s", resultCode, resultMsg);
            final String logDetail = String.format("%s", errorDetail);

            System.out.println(logMsg);
            System.out.println(logDetail);
        }

        return CommonResultVo.builder()
                .resultCode(resultCode)
                .resultMsg(resultMsg)
                .detailMsg(errorDetail)
                .build();
    }




    // ------------------------------------------------------------------------
    // 랜덤 숫자, 문자열 관련
    // ------------------------------------------------------------------------

    /**
     * <b>지정한 범위의 랜덤 숫자를 만들어 낸다.</b><br/>
     * Integer Range, Integer Output
     *
     * @param inMin 최소 숫자
     * @param inMax 최대 숫자
     * @return int 만들어진 랜덤 숫자
     */
    public static int makeRandNumber(int inMin, int inMax) {
        return (int)((inMax - inMin + 1) * Math.random() + inMin);
    }

    /**
     * <b>지정한 범위의 랜덤 숫자를 만들어 낸다.</b>
     * Float Range, Float Output
     *
     * @param inMin 최소 숫자
     * @param inMax 최대 숫자
     * @return double 만들어진 랜덤 숫자
     */
    public static double makeRandNumber(float inMin, float inMax) {
        return (inMax - inMin) * Math.random() + inMin;
    }



    // ------------------------------------------------------------------------
    // 데이터 변환 관련
    // ------------------------------------------------------------------------

    /**
     * 지정한 밀리초를 사람이 읽기 쉬운 형태로 변환한다.
     *
     * @param currMillis
     * @return
     */
    public static String formatElapsedTime(final long currMillis) {
        final long hour   = TimeUnit.MILLISECONDS.toHours(currMillis);
        final long minute = TimeUnit.MILLISECONDS.toMinutes(currMillis - TimeUnit.HOURS.toMillis(hour));
        final long second = TimeUnit.MILLISECONDS.toSeconds(currMillis - TimeUnit.HOURS.toMillis(hour) - TimeUnit.MINUTES.toMillis(minute));
        final long millis = TimeUnit.MILLISECONDS.toMillis(currMillis - TimeUnit.HOURS.toMillis(hour) - TimeUnit.MINUTES.toMillis(minute) - TimeUnit.SECONDS.toMillis(second));
        return String.format("%02d:%02d:%02d.%03d", hour, minute, second, millis);
    }

    public static String mapToString(Map<?,?> map) {

        StringBuffer buffer = new StringBuffer();

        for (Map.Entry<?,?> entry : map.entrySet()) {
            buffer.append(String.format("%s=%s,", entry.getKey(), entry.getValue()));
        }

        return buffer.toString().substring(0, buffer.toString().length()-1);
    }

    public static Map<?,?> stringToMap(String value) {
        return Splitter.on(',').withKeyValueSeparator('=').split(value);
    }


    // ------------------------------------------------------------------------
    // 디버그 출력 관련
    // ------------------------------------------------------------------------

    /**
     * Throwable 의 Stacktrace 를 출력 문자열로 변경한다.
     *
     * @param t java.lang.Throwable
     * @return String
     */
    public static String debugPrintStackTrace(Throwable t) {

        StringBuilder builder = new StringBuilder();

        if (t != null) {

            StackTraceElement[] elements = t.getStackTrace();
            builder.append(t.toString()).append("\n");

            for (StackTraceElement element : elements) {
                builder.append(element.toString()).append("\n");
            }
        }

        return builder.toString();
    }

    /**
     * <p>지정한 Value Object 를 출력 문자열로 변환</p>
     *
     * <p>Default ToStringStyle : ToStringStyle.SHORT_PREFIX_STYLE</p>
     *
     * @param vo Value Object
     * @return String
     */
    public static String debugPrintVo(Object vo) {

        if (vo instanceof Collection<?>) {

            StringBuilder builder = new StringBuilder();
            for (Object object : (Collection<?>)vo) {
                builder.append(String.format("[%s]", debugPrintVo(object, ToStringStyle.SHORT_PREFIX_STYLE)));
            }
            return builder.toString();
        }

        else {
            return debugPrintVo(vo, ToStringStyle.SHORT_PREFIX_STYLE);

        }
    }

    /**
     * <p>지정한 Value Object 를 출력 문자열로 변환 - 변환 스타일 지정 가능</p>
     *
     * @param vo Value Object
     * @param toStringStyle org.apache.commons.lang3.builder.ToStringStyle
     * @return String
     */
    public static String debugPrintVo(Object vo, ToStringStyle toStringStyle) {
        return ToStringBuilder.reflectionToString(vo, toStringStyle);
    }


    // ------------------------------------------------------------------------
    // Console Log
    // ------------------------------------------------------------------------

    public static void consoleLog(String message) {
        consoleLog(null, message);
    }

    public static void consoleLog(Logger logger, String message) {
        if (logger != null) {
            logger.info(message);
        }
        System.out.println(message);
    }



    // ------------------------------------------------------------------------
    // 기타
    // ------------------------------------------------------------------------

    public static File[] getFileList(String path) {

        if (StringUtils.isEmpty(path)) {
            return new File[]{};
        }

        final String fullPath = FilenameUtils.getFullPath(path);
        final String fileName = FilenameUtils.getName(path);

        // 경로에는 Wildcard 를 허용하지 않는다.
        if (fullPath.contains("*")) {
            return new File[]{};
        }

        if (fileName.contains("*")) {

            // 파일명에 Wilecard 가 포함되어 있으면, 정규식 형태로 바꾸어준다.
            String regex = StringUtils.replace(fileName, "*", "(.*)");

            if (!regex.startsWith("^")) {
                regex = "^" + regex;
            }

            if (!regex.endsWith("$")) {
                regex = regex + "$";
            }

            final Pattern pattern = Pattern.compile(regex);
            File dir = new File(fullPath);

            return dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pattern.matcher(pathname.getName()).find();
                }
            });

        } else {

            if (new File(path).exists()) {
                return new File[]{new File(path)};
            } else {
                return new File[]{};
            }
        }
    }

    public static boolean fileExists(String path) {

        if (StringUtils.isEmpty(path)) {
            return false;
        }

        final String fullPath = FilenameUtils.getFullPath(path);

        // 경로에는 Wildcard 를 허용하지 않는다.
        if (fullPath.contains("*")) {
            return false;
        }

        File[] files = getFileList(path);

        if (files != null) {
            return getFileList(path).length != 0;
        } else {
            return false;
        }

    }

    public static boolean directoryExist(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        } else {
            return new File(path).exists();
        }
    }

    public static boolean directoryEmpty(String path) {

        if (StringUtils.isEmpty(path)) {
            return false;
        } else {
            File dir = new File(path);
            File[] fileList = dir.listFiles();

            return (fileList.length == 0);
        }
    }

    public static String[] getSplitItems(String src) {
        return getSplitItems(src, "|");
    }

    /**
     * <b>구분자로 구분된 문자열을 배열로 리턴한다.</b>
     * <p>null safe.</p>
     *
     * @param src 구분자가 포함된 대상 문자열 (null 인 경우도 처리된다)
     * @param delimiter 구분자
     * @return
     */
    public static String[] getSplitItems(String src, String delimiter) {

        if (StringUtils.isBlank(src)) {
            src = "";
        }

        String[] res = StringUtil.split(src, delimiter);

        if ((res.length == 1) && (StringUtils.isBlank(res[0]))) {
            return null;
        } else {
            return res;
        }
    }


    public static String safeGetArrayItem(String[] src, int idx) {
        try {
            return src[idx];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
