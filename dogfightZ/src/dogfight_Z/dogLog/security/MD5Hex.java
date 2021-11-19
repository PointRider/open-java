package dogfight_Z.dogLog.security;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Hex implements Irreversible<String, String>{

    @Override
    public String encrypt(String p) {
        return DigestUtils.md5Hex(p);
    }

    public static void main(String [] args) {
        MD5Hex x = new MD5Hex();
        System.out.println(x.encrypt("123456789"));
    }
}
