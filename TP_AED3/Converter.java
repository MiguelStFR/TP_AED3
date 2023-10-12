package TP_AED3;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Scanner;

public class Converter {

    /*
     * RECEBE O ENDEREÇO DE UMA ARQUIVO
     * RETORNA O NUMERO TOTAL DE LINHAS DO ARQUIVO
     */
    public static long tam_arq(String src) throws FileNotFoundException {
        Path arq = Paths.get(src);
        long i = 0;

        try {
            i = Files.lines(arq).count();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return i;
    }

    /*
     * RECEBE O ENDEREÇO DE UMA ARQUIVO
     * RETORNA O NUMERO DE LINHAS LEGÍVEIS DO ARQUIVO
     */
    public static int tam_arquivo(String src) throws ParseException {
        File arq = new File(src);
        int i = 0;
        if (arq.exists()) {
            Scanner in;

            try {
                in = new Scanner(arq);
                String s = in.nextLine();
                while (in.hasNextLine()) {
                    s = in.nextLine();
                    i++;
                    // System.out.println("J : " + j);
                }
                in.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("erro ao abrir o arquivo");
        }
        return i;
    }

    /*
     * RECEBER O NUMERO DE LINHAS E ENDEREÇO DO ARQUIVO
     * RETORNA OS DADOS DO ARQUIVO NO FORMATO DE UM VETOR DO OBJETO 'Livro'
     */
    public static Livro[] txt_obj(int num_linhas, String src) throws ParseException {
        Livro[] livro = new Livro[num_linhas];
        // System.out.println("Tamanho Livro:" + livro.length);
        File arq = new File(src);
        int j = 0;
        if (arq.exists()) {
            Scanner in;
            int i = 0;
            String[] txt_split;

            try {
                in = new Scanner(arq);
                String s = in.nextLine();
                while (in.hasNextLine() && i < num_linhas) {
                    s = in.nextLine();
                    txt_split = s.split(",");
                    livro[i] = new Livro(txt_split);
                    // System.out.println(livro[i].ID);
                    i++;
                    j++;
                    // System.out.println("J : " + j);
                }
                i = 0;
                in.close();
                System.out.println("NUM ARQUIVOS CONVERTIDOS: " + j);

                return livro;

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
            }
        } else {
            System.out.println("erro ao abrir o arquivo");
        }
        return livro;
    }

    /*
     * RECEBE UM VETOR DO TIPO "Livro"
     * ARMAZENA OS DADOS DO VETOR EM UM DB
     * obs: PROVALVEMENTE RECEBERÁ O ENDERECO DO DB
     */
    public static void obj_db(Livro[] livro, String src_db, String src_index, int num_linhas) throws Exception {
        try (RandomAccessFile arq = new RandomAccessFile(src_db, "rw")) {
            // Objeto e arquivos para salvar ID, TITULO E ENDEREÇO do livro
            Indice[] indice = new Indice[livro.length];
            FileOutputStream arq_index = new FileOutputStream(src_index);
            DataOutputStream dos2 = new DataOutputStream(arq_index);

            byte[] l_ba, M_ba;
            int contador = livro.length;
            long filePointer = -1;
            int i = 0, j = 0;

            arq.getFilePointer();
            arq.writeLong(filePointer);
            arq.writeLong(filePointer);
            arq.writeInt(num_linhas);

            int maiorId = 0;
            for (i = 0; i < num_linhas; i++) {
                if (livro[i].ID > maiorId) {
                    maiorId = livro[i].ID;
                }
            }
            arq.writeInt(maiorId);

            for (i = 0; i < num_linhas; i++) {

                // Criar e salvar metadados no obj M(classe Meta_Livros)
                if (livro[i].EXISTE != false) {
                    // Converter e salvar livros no db
                    filePointer = arq.getFilePointer();
                    l_ba = livro[i].toByteArray();
                    arq.writeInt(l_ba.length);
                    arq.write(l_ba);

                    // Salva o id e endereço do Livro no index
                    indice[i] = new Indice(livro[i].ID, l_ba.length, filePointer);
                    M_ba = indice[i].toByteArray();
                    dos2.write(M_ba);
                    j++;
                }
            }
            System.out.println("NUM REGISTROS ARMAZENADOS: " + j);
            arq.seek(0);
            arq.writeLong(indice[i - 1].posicaoAtual);
            arq.close();
            arq_index.close();
        }
    }

    // RECEBE O ENDEREÇO DO DB E NUMERO DE REGISTROS A SEREM LIDOS
    // CONVERTER OS REGISTROS DO 'DB' PARA O OBJETO 'LIVRO'
    // RETORNA UM VETOR DE LIVROS
    public static Livro[] db_obj_vet(String src_db, int num_reg) throws Exception {
        Livro[] livro = new Livro[num_reg];
        Livro livro_aux = new Livro();

        int tam;
        byte[] l_ba;

        RandomAccessFile arq = new RandomAccessFile(src_db, "rw");
        // FileInputStream arq = new FileInputStream(src_db);
        // DataInputStream dis = new DataInputStream(arq);
        Long filePointer = arq.readLong();

        for (int i = 0; i < num_reg; i++) {
            try {
                filePointer = arq.getFilePointer();
                tam = arq.readInt();
                if (tam < 100000) {
                    l_ba = new byte[tam];
                    arq.read(l_ba);
                    livro_aux.fromByteArray(l_ba);
                }
                if (livro_aux.EXISTE != false)
                    livro[i] = new Livro(livro_aux);

            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown!");
            }
        }
        arq.seek(0);
        arq.writeLong(filePointer);
        arq.close();

        return livro;
    }

    public static void mostrar_db(String src_db, int num_reg) throws Exception {

        Livro livro_aux = new Livro();
        int tam;
        byte[] l_ba;

        RandomAccessFile arq = new RandomAccessFile(src_db, "rw");
        Long filePointer = arq.readLong();
        long ultimoRegistro = arq.readLong();
        int num_linhas = arq.readInt();
        int maiorID = arq.readInt();

        for (int i = 0; i < num_linhas; i++) {
            try {
                filePointer = arq.getFilePointer();
                ultimoRegistro = filePointer;
                tam = arq.readInt();
                l_ba = new byte[tam];
                arq.read(l_ba);
                filePointer = arq.getFilePointer();
                livro_aux.fromByteArray(l_ba);
                if (livro_aux.EXISTE != false)
                    System.out.println(livro_aux);

            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown!");
            }
        }

        arq.seek(0);
        arq.writeLong(filePointer);
        arq.writeLong(ultimoRegistro);
        arq.close();
    }

    // CONVERTER UM REGISTRO DO 'DB' PARA O OBJETO 'LIVRO' E RETORNA O LIVRO
    // RECEBE O ENDEREÇO DO DB E O ENDEREÇO DO REGISTRO COMO PARAMETRO
    public static Livro seek_db(String src_db, long filePointer) throws IOException, ParseException {
        Livro l = new Livro();

        RandomAccessFile arq = new RandomAccessFile(src_db, "rw");
        try {
            arq.seek(filePointer);
            l = new Livro(db_obj(arq));
        } catch (NullPointerException e) {
            System.out.println("NullPointerException thrown!");
        }

        arq.seek(0);
        arq.writeLong(filePointer);
        arq.close();

        return l;
    }

    // ARMAZENA UM LIVRO NA ÚLTIMA POSIÇÃO DO ARQUIVO REFERENCIADO
    public static int armazenar_Livro(RandomAccessFile arq_aux, Livro l) throws Exception {
        byte[] l_ba;
        l_ba = l.toByteArray();
        arq_aux.writeInt(l_ba.length);
        arq_aux.write(l_ba);

        return l_ba.length;
    }

    // ARMAZENA UM VETOR DE REGISTROS LIVROS NO ARQUIVO DB DESEJADO
    public static void armazenar_vetor_Livros(RandomAccessFile arq_aux, Livro[] l) throws Exception {
        byte[] l_ba;
        for (int i = 0; i < l.length; i++) {
            System.out.println(l[i].ID);
            l_ba = l[i].toByteArray();
            arq_aux.writeInt(l_ba.length);
            arq_aux.write(l_ba);
        }
        System.out.println("\n");
    }

    // RETORNA O PRÓXIMO REGISTRO DE UM ARQUIVO DB ABERTO
    // RECEBE O ENDEREÇO DO ARQUIVO COMO PARAMETRO
    public static Livro db_obj(RandomAccessFile arq_aux) throws IOException, ParseException {
        Livro livro_aux = new Livro();
        int tam_registro;
        byte[] l_ba;

        try {
            tam_registro = arq_aux.readInt();
            if (tam_registro < 100000) {
                l_ba = new byte[tam_registro];
                arq_aux.read(l_ba);
                livro_aux.fromByteArray(l_ba);
            } else {
                livro_aux.EXISTE = false;
            }
        } catch (Exception e) {
            livro_aux.EXISTE = false;
            // TODO: handle exception
        } finally {
        }
        return livro_aux;
    }

    // CONVERTER UM REGISTRO DO 'DB' PARA O OBJETO 'LIVRO' E RETORNA O LIVRO
    // RECEBE O ENDEREÇO DO REGISTRO COMO PARAMETRO
    public static Livro seek_db(RandomAccessFile arq, long filePointer) throws Exception {
        Livro l = new Livro();
        int tam;
        byte[] l_ba;

        try {
            arq.seek(filePointer);
            tam = arq.readInt();
            l_ba = new byte[tam];
            arq.read(l_ba);
            l.fromByteArray(l_ba);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException thrown!");
        }
        arq.close();

        return l;
    }

}
