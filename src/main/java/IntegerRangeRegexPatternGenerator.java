import java.util.Arrays;
import java.util.regex.Pattern;

public class IntegerRangeRegexPatternGenerator {
    public static String integerValueRangeToRegexPattern(long minValue, long maxValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("^(");
        if (minValue >=0 && maxValue >=0) {
            sb.append(integerValueRangeToRegexPattern("", Long.toString(minValue).toCharArray(), Long.toString(maxValue).toCharArray()));
        } else if (minValue < 0 && maxValue >=0) {
            sb.append(integerValueRangeToRegexPattern("-", "1".toCharArray(), Long.toString(Math.abs(minValue)).toCharArray()));
            sb.append("|");
            sb.append(integerValueRangeToRegexPattern("", "0".toCharArray(), Long.toString(maxValue).toCharArray()));
        } else {
            sb.append(integerValueRangeToRegexPattern("-", Long.toString(Math.abs(maxValue)).toCharArray(), Long.toString(Math.abs(minValue)).toCharArray()));
        }
        sb.append(")$");
        return sb.toString();
    }

    private static String integerValueRangeToRegexPattern(String prefix, char[] minValue, char[] maxValue) {
        if (minValue.length == 0 && maxValue.length == 0) {
            return prefix;
        }

        if (minValue.length == maxValue.length && minValue[0] == maxValue[0]) {
            return integerValueRangeToRegexPattern(prefix + minValue[0], Arrays.copyOfRange(minValue, 1, minValue.length), Arrays.copyOfRange(maxValue, 1, maxValue.length));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(prefix + new String(minValue));
        for (int i = minValue.length - 1; i >= 0; i--) {
            int from = Integer.parseInt("" + minValue[i]);
            if (from == 9) {
                continue;
            }

            from += 1;
            int to = 9;
            if (i == 0 && minValue.length == maxValue.length) {
                to = Integer.parseInt("" + maxValue[0]) - 1;
            }
            sb.append("|" + prefix);
            for (int j = 0; j < i; j++) {
                sb.append(minValue[j]);
            }
            if (from < to) {
                sb.append("[" + from + "-" + to + "]");
            } else {
                sb.append(from);
            }
            int left = minValue.length - i - 1;
            if (left == 1) {
                sb.append("[0-9]");
            } else if (left > 1) {
                sb.append("[0-9]{" + left + "}");
            }
        }

        int start = 0;
        if (minValue.length == maxValue.length) {
            start = 1;
        } else if (maxValue.length - minValue.length >= 2) {
            sb.append("|" + prefix);
            sb.append("[1-9][0-9]{" + minValue.length + "," + (maxValue.length - 2) + "}");
        }

        for (int i = start; i < maxValue.length; i++) {
            int from = 0;
            if (i == 0 && start == 0) {
                from = 1;
            }
            int to = Integer.parseInt("" + maxValue[i]) - 1;

            if (from > to) {
                continue;
            }

            sb.append("|" + prefix);
            for (int j = 0; j < i; j++) {
                sb.append(maxValue[j]);
            }
            if (from == to) {
                sb.append(to);
            } else {
                sb.append("[" + from + "-" + to + "]");
            }
            int left = maxValue.length - i - 1;
            if (left == 1) {
                sb.append("[0-9]");
            } else if (left > 1) {
                sb.append("[0-9]{" + left + "}");
            }
        }
        sb.append("|" + prefix + new String(maxValue));

        return sb.toString();
    }

    public static void main(String[] args) {
        long min = -12345L;
        long max = 1944000L;
        int length = ("" + max).length() + 1;
        long testMax = Long.MAX_VALUE;
        long testMin = Long.MIN_VALUE;
        if (length < 19) {
            testMax = Long.valueOf("9999999999999999999".substring(0, length));
            testMin = -1 * testMax;
        }
        String patternStr = integerValueRangeToRegexPattern(min, max);
        Pattern pattern = Pattern.compile(patternStr);

        System.out.println(patternStr);

        for (long i = testMin; i < min; i++) {
            assert !pattern.matcher("" + i).matches();
        }
        for (long i = min; i <= max; i++) {
            assert pattern.matcher("" + i).matches();
        }
        for (long i = max + 1; i <= testMax; i++) {
            assert !pattern.matcher("" + i).matches();
        }

        System.out.println("Pattern verified.");
    }
}