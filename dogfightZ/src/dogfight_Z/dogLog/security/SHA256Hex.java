package dogfight_Z.dogLog.security;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA256Hex implements Irreversible<String, String> {

    @Override
    public String encrypt(String p) {
        return DigestUtils.sha256Hex(p);
    }
}
