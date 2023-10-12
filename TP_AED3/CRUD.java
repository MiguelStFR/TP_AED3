package TP_AED3.CRUD;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import TP_AED3.Converter;
import TP_AED3.Index;
import TP_AED3.Indice;
import TP_AED3.Livro;

public class CRUD {

    /*
     * BUSCA UM OBJETO DO ARQUIVO DB ATRAVÉS DO SEU ID
     */
    public static void getFromId(int id, String src_db, String src_index) throws IOException, ParseException {
        RandomAccessFile livro_db = new RandomAccessFile(src_db, "rw");
        RandomAccessFile index_db = new RandomAccessFile(src_index, "rw");

        try {
            Indice indice = new Indice(Index.pesquisa_binaria(src_index, src_db, id));
            if (indice.ID == -1) {
                System.out.println("REGISTRO NÃO ENCONTRADO OU DELETADO");

            } else {
                Livro l = Converter.seek_db(src_db, indice.posicaoAtual);
                System.out.println(l);
                livro_db.seek(21);
                livro_db.writeInt(l.ID);
            }

        } catch (NullPointerException e) {
            System.out.println("NullPointerException thrown!");
        }

        livro_db.close();
        index_db.close();
    }

    /*
     * ADICIONA UM LIVRO PARA O ARQUIVO DB
     */
    public static void addLivro(String src_db, String src_index, Scanner in) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        RandomAccessFile livro_db = new RandomAccessFile(src_db, "rw");
        RandomAccessFile index_db = new RandomAccessFile(src_index, "rw");

        livro_db.seek(0);
        long ultimaPosicao = livro_db.readLong();
        long ultimoRegistro = livro_db.readLong();
        int num_linhas = livro_db.readInt();
        int ultimoId = livro_db.readInt();
        boolean adicionado = false;

        try {
            System.out.printf("\n");
            System.out.printf("\t.----------------------.\n");
            System.out.printf("\t|      Novo Livro      |\n");
            System.out.printf("\t'----------------------'\n");
            System.out.printf("\n");

            System.out.println("\t\nTítulo: ");
            String titulo = in.nextLine();
            System.out.println("\t\nAutor(a): ");
            String[] autor = new String[1];
            autor[0] = in.nextLine();
            System.out.println("\t\nData de publicação em MM/dd/yyyy, exemplo: 2/16/1994: ");
            String data = in.nextLine();
            System.out.println("\t\nEditora: ");
            String editora = in.nextLine();
            System.out.println("\t\nNota do livro: ");
            float nota = in.nextFloat();
            System.out.println("\t\nNúmero de páginas: ");
            Integer paginas = in.nextInt();
            System.out.println("\t\nCódigo iSBN13: ");
            Long isbn = in.nextLong();

            Livro livro = new Livro(ultimoId + 1, titulo, autor, editora, nota, paginas, isbn, data);
            Livro livro_aux = new Livro();

            byte byteArray_livro[] = livro.toByteArray();
            byte byteArray_aux[];
            byte byteArray_indice[];

            Indice indice = new Indice();

            int tamanho_novo_registro = byteArray_livro.length;
            int tamanho_registro_antigo;

            for (int i = 0; i < num_linhas; i++) {
                try {
                    ultimaPosicao = livro_db.getFilePointer();
                    ultimoRegistro = ultimaPosicao;
                    tamanho_registro_antigo = livro_db.readInt();
                    byteArray_aux = new byte[tamanho_registro_antigo];
                    livro_db.read(byteArray_aux);
                    livro_aux.fromByteArray(byteArray_aux);
                    if (!livro_aux.EXISTE) {
                        if (tamanho_novo_registro <= tamanho_registro_antigo) {
                            indice.ID = livro.ID;
                            indice.Tamanho_registro = byteArray_livro.length;
                            indice.posicaoAtual = ultimaPosicao;
                            byteArray_indice = indice.toByteArray();

                            index_db.seek(index_db.length());
                            index_db.write(byteArray_indice);

                            livro_db.seek(ultimoRegistro + 4);
                            livro_db.write(byteArray_livro);

                            livro_db.seek(0);
                            livro_db.writeLong(ultimaPosicao);
                            livro_db.writeLong(ultimoRegistro);
                            livro_db.writeInt(num_linhas);
                            livro_db.writeInt(ultimoId);

                            adicionado = true;
                            livro_db.close();
                            index_db.close();
                            return;
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println("NullPointerException thrown!");
                }
            }
            if (adicionado == false) {
                livro_db.seek(livro_db.length());
                ultimaPosicao = livro_db.getFilePointer();
                indice.ID = livro.ID;
                indice.Tamanho_registro = byteArray_livro.length;
                indice.posicaoAtual = ultimaPosicao;
                byteArray_indice = indice.toByteArray();

                index_db.seek(index_db.length());
                index_db.write(byteArray_indice);

                livro_db.writeInt(tamanho_novo_registro);
                livro_db.write(byteArray_livro);

                livro_db.seek(0);
                livro_db.writeLong(ultimaPosicao);
                livro_db.writeLong(ultimoRegistro);
                livro_db.writeInt(num_linhas + 1);
                livro_db.writeInt(ultimoId);

                System.out.println("\n" + livro.ID);
                livro_db.close();
                index_db.close();
                return;
            }
        } catch (NullPointerException e) {
            System.out.println("NullPointerException thrown!");
        }
        System.out.println("console.log");
    }

    /*
     * BUSCA UM OBJETO DO ARQUIVO DB ATRAVÉS DO SEU ID E TENTA ALTERÁ-LO
     */
    public static void updateLivro(int id, String src_db, String src_index, Scanner in)
            throws Exception {
        SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
        RandomAccessFile livro_db = new RandomAccessFile(src_db, "rw");
        RandomAccessFile index_db = new RandomAccessFile(src_index, "rw");

        boolean inativo = false;

        livro_db.seek(0);
        long ultimaPosicao = livro_db.readLong();
        long ultimoRegistro = livro_db.readLong();
        int num_linhas = livro_db.readInt();
        int ultimoId = livro_db.readInt();

        int tamanho_livro_original = 0;
        byte[] byteArray_livro;
        int tamanho_indice = 0;
        long posicao_indice = 0;
        byte[] byteArray_indice;

        Livro livro = new Livro();
        Livro livro_aux = new Livro();

        Indice indice_aux = new Indice();

        for (int i = 0; i < num_linhas; i++) {
            try {
                posicao_indice = index_db.getFilePointer();
                byteArray_indice = new byte[16];
                index_db.read(byteArray_indice);
                indice_aux.fromByteArray(byteArray_indice);
                if (indice_aux.ID == id) {
                    ultimoRegistro = indice_aux.posicaoAtual;
                    livro_db.seek(indice_aux.posicaoAtual + 4);
                    ultimaPosicao = livro_db.getFilePointer();
                    tamanho_livro_original = indice_aux.Tamanho_registro;
                    byteArray_livro = new byte[tamanho_livro_original];
                    livro_db.read(byteArray_livro);
                    livro_aux.fromByteArray(byteArray_livro);

                    if (livro_aux.EXISTE) {
                        livro = new Livro(livro_aux);
                        System.out.println("Livro Original: \n" + livro);
                        ultimaPosicao = livro_db.getFilePointer();
                    } else if (!livro_aux.EXISTE) {
                        System.out.println("\nRegistro Deletado");

                        livro_db.seek(0);
                        livro_db.writeLong(ultimaPosicao);
                        livro_db.writeLong(indice_aux.posicaoAtual + 4);
                        livro_db.writeInt(num_linhas);
                        livro_db.writeInt(ultimoId);

                        livro_db.close();
                        index_db.close();
                        return;
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown!");
            }
        }
        if (livro.ID != id) {
            System.out.println("\nRegistro Não Encontrado");

            livro_db.seek(0);
            livro_db.writeLong(ultimaPosicao);
            livro_db.writeLong(indice_aux.posicaoAtual);
            livro_db.writeInt(num_linhas);
            livro_db.writeInt(ultimoId);

            livro_db.close();
            index_db.close();
            return;
        }

        String stringValidation;
        String[] stringValidationArray = new String[1];
        float floatValidation;
        Integer integerValidation;
        long longValidation;

        String titulo = new String();
        String[] autor = new String[1];
        String data = new String();
        String editora = new String();
        float nota = 1;
        Integer paginas = 1;
        Long isbn = 1L;

        System.out.println(
                "\t\nPreencha as informações à serem alteradas, se não quiser alterar uma informação, digite 0: ");
        System.out.println("Título: ");
        stringValidation = in.nextLine();
        if (stringValidation != "0") {
            titulo = stringValidation;
        } else {
            titulo = livro.TITULO;
        }
        System.out.println("\t\nAutor(a): ");
        stringValidationArray[0] = in.nextLine();
        if (stringValidationArray[0] != "0")
            autor[0] = stringValidationArray[0];
        else {
            autor = livro.AUTOR;
        }
        System.out.println("\t\nData de Publicação: ");
        stringValidation = in.nextLine();
        if (stringValidation != "0")
            data = stringValidation;
        else {
            data = form.format(livro.DATA_PUBLIC);
        }
        System.out.println("\t\nEditora: ");
        stringValidation = in.nextLine();
        if (stringValidation != "0")
            editora = stringValidation;
        else {
            editora = livro.EDITORA;
        }
        System.out.println("\t\nNota do Livro: ");
        floatValidation = in.nextFloat();
        if (floatValidation != 0)
            nota = floatValidation;
        else {
            nota = livro.NOTA;
        }
        System.out.println("\t\nNúmero de Páginas: ");
        integerValidation = in.nextInt();
        if (integerValidation != 0)
            paginas = integerValidation;
        else {
            paginas = livro.NUM_PAGINAS;
        }
        System.out.println("\t\nCódigo iSBN13: ");
        longValidation = in.nextLong();
        if (longValidation != 0)
            isbn = longValidation;
        else {
            isbn = livro.iSBN13Integer;
        }

        Livro livro_novo = new Livro(indice_aux.ID, titulo, autor, editora, nota, paginas, isbn, data);

        byte[] byteArray_novo = livro_novo.toByteArray();
        int tamanho_livro_novo = byteArray_novo.length;

        if (tamanho_livro_original <= tamanho_livro_novo) {
            livro_db.seek(indice_aux.posicaoAtual + 4);
            livro_db.write(byteArray_novo);

            index_db.seek(posicao_indice);
            index_db.writeInt(livro.ID);
            index_db.writeInt(tamanho_livro_novo);
            index_db.writeLong(indice_aux.posicaoAtual);

            livro_db.seek(0);
            livro_db.writeLong(ultimaPosicao);
            livro_db.writeLong(indice_aux.posicaoAtual);
            livro_db.writeInt(num_linhas);
            livro_db.writeInt(ultimoId);

            System.out.println("Livro Alterado: \n" + livro_novo);
            livro_db.close();
            index_db.close();
            return;
        } else {
            livro_db.seek(indice_aux.posicaoAtual + 4);
            livro_db.writeBoolean(inativo);

            index_db.seek(posicao_indice);
            index_db.writeInt(livro.ID);
            index_db.writeInt(tamanho_livro_novo);
            index_db.writeLong(ultimoRegistro);

            livro_db.seek(livro_db.length());
            ultimaPosicao = livro_db.getFilePointer();
            ultimoRegistro = ultimaPosicao;

            livro_db.writeInt(tamanho_livro_novo);
            livro_db.write(byteArray_novo);

            ultimaPosicao = livro_db.getFilePointer();

            livro_db.seek(0);
            livro_db.writeLong(ultimaPosicao);
            livro_db.writeLong(ultimoRegistro);
            livro_db.writeInt(num_linhas);
            livro_db.writeInt(ultimoId);

            livro_db.close();
            index_db.close();
        }
    }

    public static void deleteLivro(int id, String src_db, String src_index, Scanner in)
            throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        RandomAccessFile livro_db = new RandomAccessFile(src_db, "rw");
        RandomAccessFile index_db = new RandomAccessFile(src_index, "rw");

        livro_db.seek(0);
        long ultimaPosicao = livro_db.readLong();
        long ultimoRegistro = livro_db.readLong();
        int num_linhas = livro_db.readInt();
        int ultimoId = livro_db.readInt();
        boolean inativo = false;

        int tamanho_livro;
        byte[] byteArray_livro;
        int tamanho_indice;
        byte[] byteArray_indice;

        Livro livro = new Livro();
        Livro livro_aux = new Livro();

        Indice indice_aux = new Indice();

        for (int i = 0; i < num_linhas; i++) {
            try {
                byteArray_indice = new byte[16];
                index_db.read(byteArray_indice);
                indice_aux.fromByteArray(byteArray_indice);

                if (indice_aux.ID == id) {
                    livro_db.seek(indice_aux.posicaoAtual + 4);
                    ultimaPosicao = livro_db.getFilePointer();
                    tamanho_livro = indice_aux.Tamanho_registro;
                    byteArray_livro = new byte[tamanho_livro];
                    livro_db.read(byteArray_livro);
                    livro_aux.fromByteArray(byteArray_livro);

                    if (livro_aux.EXISTE) {
                        livro_db.seek(indice_aux.posicaoAtual + 4);
                        livro_db.writeBoolean(inativo);

                        System.out.println("\nRegistro deletado com sucesso");

                        ultimaPosicao = livro_db.getFilePointer();

                        livro_db.seek(0);
                        livro_db.writeLong(ultimaPosicao);
                        livro_db.writeLong(indice_aux.posicaoAtual + 4);
                        livro_db.writeInt(num_linhas);
                        livro_db.writeInt(ultimoId);

                        livro_db.close();
                        index_db.close();
                        return;
                    } else if (!livro_aux.EXISTE) {
                        System.out.println("\nRegistro não existe pois já tinha sido Deletado");

                        livro_db.seek(0);
                        livro_db.writeLong(ultimaPosicao);
                        livro_db.writeLong(indice_aux.posicaoAtual + 4);
                        livro_db.writeInt(num_linhas);
                        livro_db.writeInt(ultimoId);

                        livro_db.close();
                        index_db.close();
                        return;
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown!");
            }
        }
        if (livro.ID != id) {
            System.out.println("\nRegistro Não Encontrado");

            livro_db.seek(0);
            livro_db.writeLong(ultimaPosicao);
            livro_db.writeLong(indice_aux.posicaoAtual + 4);
            livro_db.writeInt(num_linhas);
            livro_db.writeInt(ultimoId);

            livro_db.close();
            index_db.close();
        }
    }

    public static void deleteLivro_2(int id, String src_db, String src_index, Scanner in)
            throws Exception {
        RandomAccessFile livro_db = new RandomAccessFile(src_db, "rw");
        RandomAccessFile index_db = new RandomAccessFile(src_index, "rw");

        livro_db.seek(0);
        long ultimaPosicao = livro_db.readLong();
        boolean inativo = false;
        Livro livro;

        try {
            Indice indice_aux = new Indice(Index.pesquisa_binaria(src_index, src_db, id));

            if (indice_aux.ID != -1) {
                livro = new Livro(Converter.seek_db(livro_db, indice_aux.posicaoAtual));
                ultimaPosicao = livro_db.getFilePointer();

                if (livro.EXISTE) {
                    livro_db.seek(indice_aux.posicaoAtual + 4);
                    livro_db.writeBoolean(inativo);

                    System.out.println("\nRegistro deletado com sucesso");

                    ultimaPosicao = livro_db.getFilePointer();
                    livro_db.seek(0);
                    livro_db.writeLong(ultimaPosicao);
                    livro_db.close();
                    index_db.close();
                    return;
                } else {
                    System.out.println("\nRegistro não existe pois já tinha sido Deletado");
                    livro_db.seek(0);
                    livro_db.writeLong(ultimaPosicao);
                    livro_db.close();
                    index_db.close();
                    return;
                }
            } else
                System.out.println("REGISTRO NÃO ENCONTRADO");

        } catch (NullPointerException e) {
            System.out.println("NullPointerException thrown!");
        }
    }

    public static void CRUD(String src_db, String src_index, int num_linhas) throws Exception {

        Scanner eScanner = new Scanner(System.in);
        int ESCOLHA = 0;
        int ID = 50000;

        do {
            System.out.println("\n+++++++++ CRUD +++++++++\n");
            System.out.println("    OPÇÕES: " +
                    "\n\t1    -   MOSTRAR REGISTROS" +
                    "\n\t2    -   LER UM REGISTRO POR ID" +
                    "\n\t3    -   ADICIONAR UM REGISTRO" +
                    "\n\t4    -   ATUALIZAR UM REGISTRO" +
                    "\n\t5    -   DELETAR UM REGISTRO" +
                    "\n\t0    -   SAIR\n");

            ESCOLHA = eScanner.nextInt();

            switch (ESCOLHA) {
                case 1: {
                    System.out.println("\t+++++++++ MOSTRAR REGISTROS +++++++++\n");
                    Converter.mostrar_db(src_db, num_linhas);
                    break;
                }

                case 2: {
                    System.out.println("\t+++++++++ LER UM REGISTRO POR ID +++++++++\n");
                    System.out.print("\t\nDigite um ID: ");
                    int id = eScanner.nextInt();
                    getFromId(id, src_db, src_index);
                    break;
                }

                case 3: {
                    System.out.println("\t+++++++++ ADICIONAR UM REGISTRO +++++++++\n");
                    System.out.println("\t\n");
                    eScanner.nextLine();
                    addLivro(src_db, src_index, eScanner);
                    ID++;
                    break;
                }

                case 4: {
                    System.out.println("\t+++++++++ ATUALIZAR UM REGISTRO +++++++++\n");
                    System.out.println("ID: ");
                    int id = eScanner.nextInt();
                    updateLivro(id, src_db, src_index, eScanner);
                    break;
                }

                case 5: {
                    System.out.println("\t+++++++++ DELETAR UM REGISTRO +++++++++\n");
                    System.out.print("\t\nDigite um ID: ");
                    int id = eScanner.nextInt();
                    deleteLivro(id, src_db, src_index, eScanner);
                    break;
                }

                case 0: {
                    System.out.println("\n+++++++++ ADEUS! +++++++++");
                    break;
                }

                default:
                    System.out.println("\n+++++++++ OPÇÃO INVÁLIDA +++++++++\n");
                    break;
            }
        } while (ESCOLHA != 0);
    }
}
