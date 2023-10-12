package TP_AED3.funcoes;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;

public class Indice {
    public int ID;
    public int Tamanho_registro;
    public long posicaoAtual;
    // protected long posicaoAntiga;

    public Indice() {
        this.ID = -1;
        this.Tamanho_registro = -1;
        this.posicaoAtual = -1;
        // this.posicaoAntiga = -1;
    }

    public Indice(int ID, int Tamanho_registro, /* long posicaoAntiga, */ long posicaoAtual) {
        this.ID = ID;
        this.Tamanho_registro = Tamanho_registro;
        this.posicaoAtual = posicaoAtual;
        // this.posicaoAntiga = posicaoAntiga;
    }

    public Indice(Indice M) {
        this.ID = M.ID;
        this.Tamanho_registro = M.Tamanho_registro;
        // this.posicaoAntiga = M.posicaoAntiga;
        this.posicaoAtual = M.posicaoAtual;
    }

    public String toString() {
        return "\nID........: " + this.ID +
                "\nTamanho...: " + this.Tamanho_registro +
                // "\nP.Antiga..: " + this.posicaoAntiga +
                "\nP.Atual...:" + this.posicaoAtual +
                "\n\n";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(ID);
        dos.writeInt(Tamanho_registro);
        // dos.writeLong(posicaoAntiga);
        dos.writeLong(posicaoAtual);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException, ParseException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.ID = dis.readInt();
        this.Tamanho_registro = dis.readInt();
        // this.posicaoAntiga = dis.readLong();
        this.posicaoAtual = dis.readLong();
    }
}
