package dogfight_Z.dogLog.security;

public interface Reversible<Plaintext, Ciphertext, Key> {
    Ciphertext encrypt(Plaintext p, Key k);
    Plaintext  decrypt(Ciphertext c, Key k);
}
