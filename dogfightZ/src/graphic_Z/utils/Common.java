package graphic_Z.utils;

public class Common {

    private Common() {
        // TODO 自动生成的构造函数存根
    }

    public static String loopChar(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for(int i = 0; i < n; ++i) sb.append(c);
        return sb.toString();
    }
}
