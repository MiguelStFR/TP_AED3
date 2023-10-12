package TP_AED3.Criptografia;

import java.math.BigInteger;
// import java.nio.charset.StandardCharsets;
// import java.util.Random;
import java.util.Random;

public class Cripto_assimetrica {
    private static final int KEY_SIZE = 2048; // Tamanho da chave em bits

    // Função para gerar um par de chaves RSA
    public static ParChaves gerarParChaves() {
        Random random = new Random();

        BigInteger p = BigInteger.probablePrime(KEY_SIZE / 2, random);
        BigInteger q = BigInteger.probablePrime(KEY_SIZE / 2, random);

        BigInteger n = p.multiply(q);
        BigInteger phiN = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.valueOf(65537); // Valor típico para o expoente público
        BigInteger d = e.modInverse(phiN);

        ChavePublica ChavePublica = new ChavePublica(n, e);
        ChavePrivada ChavePrivada = new ChavePrivada(n, d);
        return new ParChaves(ChavePublica, ChavePrivada);
    }

    // Função para criptografar os dados usando a chave pública
    public static String criptografar(String registro, ChavePublica ChavePublica){

        //Recebe os valores da chave      
        BigInteger n = ChavePublica.n;
        BigInteger e = ChavePublica.e;

        //Converte byte para BigIntenger
        BigInteger message = new BigInteger(registro.getBytes());
        BigInteger texto_criptografado = message.modPow(e, n);

        // byte[] bytesCriptografados = texto_criptografado.toByteArray();
        return texto_criptografado.toString();
    }

    // Função para descriptografar os dados usando a chave privada
    public static String descriptografar(String texto_criptografado, ChavePrivada ChavePrivada){
        //Recebe os valores das chaves
        BigInteger n = ChavePrivada.n;
        BigInteger d = ChavePrivada.d;    
        //Converte a string para BigIntenger e aplica a fórmula
        BigInteger palavra_criptografada = new BigInteger(texto_criptografado);
        BigInteger palavra_descriptografada = palavra_criptografada.modPow(d, n);
        //retorna a variavel descriptografada
        return new String(palavra_descriptografada.toByteArray());
    }
}
