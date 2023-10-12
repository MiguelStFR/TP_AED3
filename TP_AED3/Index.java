package TP_AED3;


import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;

public class Index {

    public static int tamanho_registro = 16;

    // ARMAZENA UM VETOR CONTENDO O ID, E ENDEREÇO DOS INDICES EM UM DB
    public static Indice[] db_obj_index(String src_index, int num_reg) throws IOException, ParseException {
        Indice[] indices = new Indice[num_reg];
        Indice indice_aux = new Indice();

        byte[] M_ba;

        FileInputStream arq = new FileInputStream(src_index);
        DataInputStream dis = new DataInputStream(arq);

        for (int i = 0; i < num_reg; i++) {
            M_ba = new byte[tamanho_registro];
            dis.read(M_ba);
            indice_aux.fromByteArray(M_ba);
            indices[i] = new Indice(indice_aux);
        }
        arq.close();
        return indices;
    }

    // RETORNA O PRÓXIMO REGISTRO DE INDICE DE UM ARQUIVO DB ABERTO
    // RECEBE O ENDEREÇO DO ARQUIVO COMO PARAMETRO
    public static Indice db_obj(RandomAccessFile arq_index) throws IOException, ParseException {
        Indice indice = new Indice();
        byte[] l_ba;

        l_ba = new byte[tamanho_registro];
        arq_index.read(l_ba);
        indice.fromByteArray(l_ba);
        return indice;
    }

    // ARMAZENA UM REGISTRO DE INDICE NA ÚLTIMA POSIÇÃO DO ARQUIVO REFERENCIADO
    public static void armazenar_indice(RandomAccessFile arq_index, Indice indice) throws IOException {
        byte[] l_ba;

        System.out.println(indice);
        l_ba = indice.toByteArray();
        arq_index.write(l_ba);
    }

    // EXIBE TODOS OS INDEX ARMAZENADOS
    public static void MOSTRAR(String src_index, String src_db, int num_reg) throws IOException, ParseException {
        RandomAccessFile livro_db = new RandomAccessFile(src_db, "rw");
        Indice indice_aux = new Indice();
        byte[] l_ba;

        livro_db.seek(0);
        long ultimaPosicao = livro_db.readLong();
        long ultimoRegistro = livro_db.readLong();
        int num_linhas = livro_db.readInt();
        int ultimoId = livro_db.readInt();

        RandomAccessFile arq = new RandomAccessFile(src_index, "rw");

        for (int i = 0; i < num_linhas; i++) {
            try {
                l_ba = new byte[tamanho_registro];
                arq.read(l_ba);
                indice_aux.fromByteArray(l_ba);
                System.out.println(indice_aux);

            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown!");
            }
        }
        arq.close();
    }

    // REALIZA UMA PESQUISA BINARIA DOS INDICES COM BASE NO ID
    public static Indice pesquisa_binaria(String src_index, String src_livro, int ID)
            throws IOException, ParseException {
        long filePointer;
        long low = 0;
        Indice indice = new Indice();

        RandomAccessFile livro_db = new RandomAccessFile(src_livro, "rw");

        long ultimaPosicao = livro_db.readLong();
        long ultimoRegistro = livro_db.readLong();
        int num_reg = livro_db.readInt();
        int ultimoId = livro_db.readInt();

        long high = num_reg;

        RandomAccessFile index = new RandomAccessFile(src_index, "rw");

        while (low <= high) {
            filePointer = ((low + high) / 2) * tamanho_registro;
            index.seek(filePointer);
            indice = new Indice(db_obj(index));

            if (indice.ID > ID) {
                high = (filePointer / tamanho_registro) - 1;
            } else if (indice.ID < ID) {
                low = (filePointer / tamanho_registro) + 1;
            } else
                return indice;
        }
        indice = new Indice();
        return indice;
    }
}
