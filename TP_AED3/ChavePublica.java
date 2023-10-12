package TP_AED3;

import java.math.BigInteger;

public class ChavePublica {
        //GERAÇÃO CHAVE PÚBLICA

        public BigInteger n;
        public BigInteger e;

        public ChavePublica(BigInteger n, BigInteger e) {
            this.n = n;
            this.e = e;
        }
    }

