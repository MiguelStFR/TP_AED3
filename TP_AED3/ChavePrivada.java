package TP_AED3;



import java.math.BigInteger;

public class ChavePrivada {
    
    //GERAÇÃO CHAVE PRIVADA
        public BigInteger n;
        public BigInteger d;

        public ChavePrivada(BigInteger n, BigInteger d) {
            this.n = n;
            this.d = d;
        }
    }

