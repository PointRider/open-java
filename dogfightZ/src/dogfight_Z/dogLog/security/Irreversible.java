package dogfight_Z.dogLog.security;

public interface Irreversible<Plaintext, Ciphertext> {
    Ciphertext encrypt(Plaintext p);
}
