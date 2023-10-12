package TP_AED3;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Livro {

    // UTILIZADO PARA A CONVERSÃO DE DATA
    SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");

    // VALORES REFERENTES À CLASSE DO OBJETO
    protected int ID;
    protected boolean EXISTE;
    protected String TITULO;
    protected String[] AUTOR;
    protected Date DATA_PUBLIC;
    protected String EDITORA;
    protected float NOTA;
    protected Integer NUM_PAGINAS;
    protected long iSBN13Integer;

    // CONSTRUTOR PADRÃO
    public Livro() throws ParseException {
        this.ID = -1;
        this.TITULO = "***";
        this.EXISTE = false;
        this.AUTOR = new String[1];
        this.AUTOR[0] = "***";
        this.EDITORA = "***";
        this.NOTA = -1;
        this.NUM_PAGINAS = -1;
        this.iSBN13Integer = -1;
        this.DATA_PUBLIC = form.parse("00/00/0000");
    }

    // INICIA O OBJETO RECEBENDO OS VALORES INDIVIDUAIS POR PARÂMETRO
    public Livro(Integer id, String t, String[] a, String e, Float n, Integer p, Long isbn13Integer, String txt_split)
            throws ParseException {
        this.ID = id;
        this.EXISTE = true;
        this.TITULO = t;
        this.AUTOR = a;
        this.EDITORA = e;
        this.NOTA = n;
        this.NUM_PAGINAS = p;
        this.iSBN13Integer = isbn13Integer;
        this.DATA_PUBLIC = form.parse(txt_split);
    }

    // INICIA O OBJETO RECEBENDO UM VALOR DO TIPO DA PRÓPRIA CLASSE COMO PARÂMETRO
    public Livro(Livro l)
            throws ParseException {
        this.ID = l.ID;
        this.EXISTE = l.EXISTE;
        this.TITULO = l.TITULO;
        this.AUTOR = l.AUTOR;
        this.EDITORA = l.EDITORA;
        this.NOTA = l.NOTA;
        this.NUM_PAGINAS = l.NUM_PAGINAS;
        this.iSBN13Integer = l.iSBN13Integer;
        this.DATA_PUBLIC = l.DATA_PUBLIC;
    }

    // CONVERTE OS VALORES DE UMA STRING DE TEXTO PARA O FORMATO UTILIZADO NA CLASSE
    // CASO OCORRA ERRO NA CONVERSÃO, O OBJETO É INICIADO COMO "EXCLUÍDO" E SERÁ
    // DELETADO
    // EM OPERAÇÕES FUTURAS
    public Livro(String txt_split[]) throws ParseException {

        try {
            this.ID = Integer.parseInt(txt_split[0]);
            this.EXISTE = true;
            this.TITULO = txt_split[1];
            this.AUTOR = txt_split[2].split("/");
            // for (String string : AUTOR) {
            // System.out.print(string + ", ");
            // }
            this.NOTA = Float.parseFloat(txt_split[3]);
            this.iSBN13Integer = Long.parseLong(txt_split[5]);
            this.NUM_PAGINAS = Integer.parseInt(txt_split[7]);
            this.DATA_PUBLIC = form.parse(txt_split[10]);
            this.EDITORA = txt_split[11];
        } catch (Exception e) {
            this.ID = -1;
            this.TITULO = "***";
            this.EXISTE = true;
            this.AUTOR = new String[1];
            this.AUTOR[0] = "***";
            this.EDITORA = "***";
            this.NOTA = -1;
            this.NUM_PAGINAS = -1;
            this.iSBN13Integer = -1;
            this.DATA_PUBLIC = form.parse("00/00/0000");
        } finally {
        }
    }

    // RETORNA A MESCLAGEM DOS VALORES DO VETOR DE AUTORES EM UMA UNICA STRING
    public String concat_autores(String[] A) {
        String CONC_AUTORES = "";
        CONC_AUTORES = CONC_AUTORES + A[0];
        for (int i = 1; i < A.length; i++) {
            CONC_AUTORES = CONC_AUTORES + "/" + A[i];
        }
        return CONC_AUTORES;
    }

    // RETORNA VALORES DO OBJETO NO FORMATO DE STRING
    public String toString() {

        String autores = concat_autores(this.AUTOR);

        return "\n ID........: " + this.ID +
                "\nTítulo....: " + this.TITULO +
                "\nAutor(es).: " + autores +
                "\nNota......: " + this.NOTA +
                "\nISBN......: " + this.iSBN13Integer +
                "\nPáginas...: " + this.NUM_PAGINAS +
                "\nLançamento: " + this.DATA_PUBLIC +
                "\nEditora...: " + this.EDITORA + "\n\n";
    }

    // CONVERTE E RETORNA VALORES DO OBJETO PARA O FORMATO byte
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeBoolean(EXISTE);
        dos.writeInt(ID);

        String titulo = (Cripto_assimetrica.criptografar(TITULO, Main.parChaves.ChavePublica)); 
        System.out.println("Titulo: " + titulo);
        dos.writeUTF(titulo);

        String autores = concat_autores(this.AUTOR);
        dos.writeUTF(Cripto_assimetrica.criptografar(autores, Main.parChaves.ChavePublica));

        dos.writeFloat(NOTA);
        dos.writeLong(iSBN13Integer);
        dos.writeInt(NUM_PAGINAS);
        dos.writeLong(DATA_PUBLIC.getTime());
        dos.writeUTF(EDITORA);
        return baos.toByteArray();
    }

    // CONVERTE VALORES byte PARA VALORES UTILIZADOS NO OBJETO
    public void fromByteArray(byte[] ba) throws IOException, Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.EXISTE = dis.readBoolean();
        this.ID = dis.readInt();
        this.TITULO = dis.readUTF();
        this.TITULO = Cripto_assimetrica.descriptografar(TITULO, Main.parChaves.ChavePrivada);

        String[] autor = Cripto_assimetrica.descriptografar( dis.readUTF(), Main.parChaves.ChavePrivada).split("/");
        this.AUTOR = autor;
        this.NOTA = dis.readFloat();
        this.iSBN13Integer = dis.readLong();
        this.NUM_PAGINAS = dis.readInt();
        long d = dis.readLong();
        DATA_PUBLIC = form.parse(String.format("%03d/%02d/%04d", d / 360000, d /
                86400000, d / (365 * 86400000)));
        this.EDITORA = dis.readUTF();
    }
}
