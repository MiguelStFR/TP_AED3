package TP_AED3;

import java.math.BigInteger;

public class ParChaves {
    
        //Classe para que ambas as chaves fiquem em um objeto
        public ChavePublica ChavePublica;
        public ChavePrivada ChavePrivada;

        public ParChaves(ChavePublica ChavePublica, ChavePrivada ChavePrivada) {
            this.ChavePublica = ChavePublica;
            this.ChavePrivada = ChavePrivada;
        }

        // public ParChaves(String[] txt) {
        //     this.ChavePublica.e = new BigInteger(txt[0]);
        //     this.ChavePublica.n = new BigInteger(txt[1]);
        //     this.ChavePrivada.d = new BigInteger(txt[2]);
        //     this.ChavePrivada.n = new BigInteger(txt[3]);
        // }

        // public ChavePublica getChavePublica() {
        //     return ChavePublica;
        // }

        // public ChavePrivada getChavePrivada() {
        //     return ChavePrivada;
        // }
    
}
