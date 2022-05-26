package dogfight_Z.dogLog.security;

import org.apache.commons.codec.digest.DigestUtils;

public class securityTest {
    public static void main(String[] args) {

        String text = "test text.";
        System.out.println(DigestUtils.md5Hex(DigestUtils.md5Hex(text)));
    }
}
