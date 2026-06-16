package rsa_alg;

import java.math.BigInteger;
class RSA{
    public BigInteger pn1;
    public BigInteger pn2;
    public BigInteger e = BigInteger.valueOf(65537);
    public void set_numbers(BigInteger p1, BigInteger p2){
        this.pn1 = p1;
        this.pn2 = p2;
    }
    public BigInteger get_n(){
        return this.pn1.multiply(this.pn2);
    }
    public BigInteger get_z(){
        BigInteger z1 = this.pn1.subtract(BigInteger.valueOf(1));
        BigInteger z2 = this.pn2.subtract(BigInteger.valueOf(1));
        return z1.multiply(z2);
    }
    public BigInteger get_d(){
        return this.e.modInverse(get_z());
    }
    public BigInteger encrypt(BigInteger d){
        return d.modPow(this.e, get_n());
    }
    public BigInteger decrypt(BigInteger encrypt_value){
        return encrypt_value.modPow(get_d(), get_n());
    }
}

public class rsa_code {
    public static void main(String[] args) {
        RSA r = new RSA();
        r.set_numbers(new BigInteger(String.valueOf(99971)),new BigInteger(String.valueOf(99991)));
        System.out.println("public key - " + r.e + "," + r.get_n());
        System.out.println("private key - : " + r.get_d() + "," + r.get_n());

        BigInteger msg = new BigInteger("12345");
        BigInteger enc = r.encrypt(msg);
        BigInteger dec = r.decrypt(enc);
        System.out.println("msg = " + msg);
        System.out.println("enc = " + enc);
        System.out.println("dec = " + dec);
    }
}
