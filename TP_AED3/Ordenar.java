package TP_AED3;



import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;

/*
 * ESSA CLASSE É RESPONSÁVEL PELA ORDENAÇÃO DOS REGISTROS DO DB, SENDO DIVIDO EM 3 PARTES
 * 
 *  INTERCALAÇÃO INICIAL: 
 *      CARREGA OS REGISTROS DO DB PRINCIPAL PARA DOIS ARQUIVOS SECUNDÁRIOS, SEPARANDO-OS EM BLOCOS DE 10 SEGMENTOS E 
 *      ENTÃO ORDENANDO OS MESMO ANTES DE REALIZAR O ARMAZENAMENTO
 * 
 *  INTERCALAR: 
 *      REALIZA O PROCESSO DE INTERCALAÇÃO INTERMEDIÁRIO DOS REGISTROS, VARIANDO O FLUXO DE REGISTROS ENTRE 2 PARES DE ARQUIVOS:
 *      ARQ_AUX1/ARQ_AUX2 E ARQ_AUX3/ARQ_AUX4, O CICLO DE INTERCALAÇÕES CONTINUA ATÉ QUE O BLOCO DE REGISTROS TENHA UM TAMAMNHO 
 *      EQUIVALENTE A MAIS DA METADE DO NUMERO TOTAL DE REGISTRO DO DB
 * 
 *  INTERCALAÇÃO_FINAL:
 *      REALIZA A COMPARAÇÃO FINAL ENTRE OS REGISTROS DOS DOIS BLOCOS FINAIS, REARMAZENANDO-OS NO DB ORIGINAL
 */
public class Ordenar {

    // AS VARIÁVEIS num_reg_arq SERVEM PARA CONTAR QUANTOS REGISTROS ESTÃO
    // ARMAZENADOS NO RESPECTIVO ARQUIVO
    // AS VARIÁVEIS num_reg_arq GLOBAIS SERVEM PARA PASSAR ESSE VALORES PARA AS
    // SEMELHANTES DE ESCOPO LOCAL, EM TODOS OS MÉTODOS
    static int num_reg_arq01, num_reg_arq02, num_reg_arq03, num_reg_arq04;
    static boolean alterar_duo_arq;

    // MÉTODO PRINCIPAL QUE REALIZA A CHAMADA DOS OUTROS 3 MÉTODOS
    static public int ordenar(String src_db, int num_reg) throws Exception {
        intercalacao_inicial(num_reg, src_db);
        intercalar(num_reg);
        int contador = intercalacao_final(num_reg, src_db);
        return contador;
    }

    /*
     * SEPARA OS REGISTRO DO ARQUIVO PRINCIPAL EM BLOCOS DE 10 REGISTROS
     * ORDENA OS BLOCOS E ENTÃO REDIRECIONA-OS PARA 2 ARQUIVOS AUXILIARES
     */

    public static void intercalacao_inicial(int num_registros, String src_db) throws Exception {

        // VARIÁVEL QUE INTERMEDIA EM QUAL ARQ SERÃO ARMAZENADOS OS BLOCOS
        boolean alterar_arq = true;

        RandomAccessFile arq_aux0 = new RandomAccessFile(src_db, "rw");

        long ultimaPosicao = arq_aux0.readLong();
        long ultimoRegistro = arq_aux0.readLong();
        int num_linhas = arq_aux0.readInt();
        int ultimoId = arq_aux0.readInt();

        // ARQUIVOS AUXILIARES
        RandomAccessFile arq_aux1 = new RandomAccessFile("arq_aux1.db", "rw");
        RandomAccessFile arq_aux2 = new RandomAccessFile("arq_aux2.db", "rw");

        Livro[] l = new Livro[10];

        // CONTADORES DO NUMERO DE REGISTROS ARMAZENADOS EM CADA ARQUIVO
        int num_reg_arq1, num_reg_arq2;
        num_reg_arq1 = num_reg_arq2 = 0;

        System.out.println("INTERCALAÇÃO INICIAL");
        // CICLO CONTINUA ATÉ O ÚLTIMO REGISTRO SER CARREGADO
        for (int i = 0; i < num_registros;) {
            // CARREGA 10 REGISTROS PARA A MEMÓRIA PRINCIPAL
            for (int j = 0; (j < 10 && i < num_registros); j++) {
                l[j] = new Livro(Converter.db_obj(arq_aux0));
                i++;
            }

            // ORDENA O BLOCO DE REGISTROS
            l = ordLivros(l);

            // A CADA REPETIÇÃO DE 'i', ARMAZENA O VETOR DE LIVROS EM UM ARQUIVO DIFERENTE
            if (alterar_arq) {
                Converter.armazenar_vetor_Livros(arq_aux1, l);
                num_reg_arq1 += l.length;
                alterar_arq = false;
            } else {
                Converter.armazenar_vetor_Livros(arq_aux2, l);
                num_reg_arq2 += l.length;
                alterar_arq = true;
            }
        }

        // PASSA OS VALORES DOS CONTADORES PARA VARIÁVEIS GLOBAIS PARA SEREM USADAS NOS
        // PRÓXIMOS MÉTODOS
        num_reg_arq01 = num_reg_arq1;
        num_reg_arq02 = num_reg_arq2;
        arq_aux1.close();
        arq_aux2.close();
    }

    /*
     * REALIZA A INTERCALAÇÃO INTERMEDIÁRIA, ENTRE DOIS PARES DE ARQUIVOS AUXILIARES
     * ATÉ QUE O TAMANHO DO BLOCO DE REGISTROS SEJA MAIOR QUE A METADE DA QUANTIDADE
     * DE REGISTROS ARMAZENADOS NO DB
     */
    public static void intercalar(int num_registros) throws Exception {

        System.out.println("---------INTERCALAÇÃO INTERMEDIÁRIA:----------");
        System.out.println(num_reg_arq01 + num_reg_arq02 + " " + num_reg_arq01 + " " + num_reg_arq02);

        int num_reg_arq1 = num_reg_arq01;
        int num_reg_arq2 = num_reg_arq02;
        int num_reg_arq3, num_reg_arq4;
        boolean alterar_arq2 = true;
        alterar_duo_arq = true;

        Livro l1, l2;
        // int limite01, limite02;
        num_reg_arq3 = num_reg_arq4 = 0;

        // limite01 = limite02 = 0;
        int tam_bloco_registros = 20;

        // CICLO CONTINUA ATÉ O BLOCO DE REGISTRO SER MAIOR QUE A METADE DO NUMERO DE
        // REGISTROS. A CADA NOVO CICLO, O TAMANHO DO BLOCO É DUPLICADO
        do {

            System.out.println("\nTAM_BLOCO: " + tam_bloco_registros);

            /*
             * A VARIÁVEL 'alterar_duo_arq' SERVE PARA INTERMEDIAR QUAL PAR DE ARQUIVOS SERÁ
             * ORDENADO E QUAL PAR RECEBERÁ OS VALORES ORDENADOS.
             * SE ELA FOR 'true', OS ARQUIVOS 'arq_aux3/arq_aux4' RECEBERÃO OS VALORES
             * ORDENADOS DOS ARQUIVOS 'arq_aux1/arq_aux2', SENÃO, OCORRERÁ O CONTRÁRIO. SEU
             * VALOR INVERTE SEMPRE QUE HOUVER A LEITURA COMPLETA DOS REGISTROS;
             * 
             * 
             * A VARIÁVEL "alterar_arq2" SERVE PARA INTERMEDIAR QUAL ARQUIVO DO PAR RECEBERÁ
             * O BLOCO DE REGISTROS ORDENADOS
             * SE for "true", OS ARQUIVOS arq_aux1 ou arq_aux3 RECEBERÃO O BLOCO, SENÃO, O
             * PAR CONTRÁRIO RECEBERÁ.
             * SEU VALOR INVERTE SEMPRE QUE UM BLOCO DE REGISTRO FOR COMPLETAMENTE LIDO
             */
            if (alterar_duo_arq) {

                RandomAccessFile arq_aux1 = new RandomAccessFile("arq_aux1.db", "rw");
                RandomAccessFile arq_aux2 = new RandomAccessFile("arq_aux2.db", "rw");
                RandomAccessFile arq_aux3 = new RandomAccessFile("arq_aux3.db", "rw");
                RandomAccessFile arq_aux4 = new RandomAccessFile("arq_aux4.db", "rw");
                for (int i = 0; i < num_registros;) {

                    // ARMAZENA BLOCOS DE REGISTROS DOS ARQUIVOS 1/2 NOS ARQUIVOS 3/4
                    if (alterar_arq2) {
                        l1 = new Livro(Converter.db_obj(arq_aux1));
                        num_reg_arq1--;
                        l2 = new Livro(Converter.db_obj(arq_aux2));
                        num_reg_arq2--;
                        // ARMAZENA BLOCO NO ARQ_AUX 3
                        System.out.println("\nARMAZENAMENTO ARQ_03\n");
                        for (int j = 0; j < tam_bloco_registros && i < num_registros; j++, i++) {
                            if (l1.ID < l2.ID) {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l1.EXISTE) {
                                    System.out.println(l1.ID);
                                    Converter.armazenar_Livro(arq_aux3, l1);
                                    num_reg_arq3++;
                                }
                                // else {
                                // num_registros--;
                                // }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (/* limite01 < tam_bloco_registros / 2 && */num_reg_arq1 > 0) {
                                    l1 = new Livro(Converter.db_obj(arq_aux1));
                                    num_reg_arq1--;
                                    // //limite01++;
                                } else {
                                    l1 = new Livro(Converter.db_obj(arq_aux2));
                                    num_reg_arq2--;
                                    // //limite02++;
                                }
                            } else {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l2.EXISTE) {
                                    System.out.println(l2.ID);
                                    Converter.armazenar_Livro(arq_aux3, l2);
                                    num_reg_arq3++;
                                }
                                // else {
                                // num_registros--;
                                // }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (/* limite02 < tam_bloco_registros / 2 && */num_reg_arq2 > 0) {
                                    l2 = new Livro(Converter.db_obj(arq_aux2));
                                    num_reg_arq2--;
                                    // //limite02++;
                                } else {
                                    l2 = new Livro(Converter.db_obj(arq_aux1));
                                    num_reg_arq1--;
                                    // limite01++;
                                }
                            }
                        }

                        alterar_arq2 = false;
                    } else {

                        l1 = new Livro(Converter.db_obj(arq_aux1));
                        num_reg_arq1--;
                        l2 = new Livro(Converter.db_obj(arq_aux2));
                        num_reg_arq2--;

                        // ARMAZENA BLOCO NO ARQ_AUX 4
                        System.out.println("\nARMAZENAMENTO ARQ_04\n");
                        for (int j = 0; j < tam_bloco_registros && i < num_registros; j++, i++) {
                            if (l1.ID < l2.ID) {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l1.EXISTE) {
                                    System.out.println(l1.ID);
                                    Converter.armazenar_Livro(arq_aux4, l1);
                                    num_reg_arq4++;
                                }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (num_reg_arq1 > 0) {
                                    l1 = new Livro(Converter.db_obj(arq_aux1));
                                    num_reg_arq1--;
                                } else {
                                    l1 = new Livro(Converter.db_obj(arq_aux2));
                                    num_reg_arq2--;
                                }
                            } else {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l2.EXISTE) {
                                    System.out.println(l2.ID);
                                    Converter.armazenar_Livro(arq_aux4, l2);
                                    num_reg_arq4++;
                                }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (num_reg_arq2 > 0) {
                                    l2 = new Livro(Converter.db_obj(arq_aux2));
                                    num_reg_arq2--;
                                } else {
                                    l2 = new Livro(Converter.db_obj(arq_aux1));
                                    num_reg_arq1--;
                                }
                            }
                        }
                        alterar_arq2 = true;

                    }
                }

                arq_aux1.close();
                arq_aux2.close();
                arq_aux3.close();
                arq_aux4.close();

            } else {
                RandomAccessFile arq_aux1 = new RandomAccessFile("arq_aux1.db", "rw");
                RandomAccessFile arq_aux2 = new RandomAccessFile("arq_aux2.db", "rw");
                RandomAccessFile arq_aux3 = new RandomAccessFile("arq_aux3.db", "rw");
                RandomAccessFile arq_aux4 = new RandomAccessFile("arq_aux4.db", "rw");
                for (int i = 0; i < num_registros;) {
                    // ARMAZENA BLOCO DE REGISTROS DOS ARQUIVOS 3/4 NOS ARQUIVO 1/2
                    if (alterar_arq2) {
                        l1 = new Livro(Converter.db_obj(arq_aux3));
                        num_reg_arq3--;
                        l2 = new Livro(Converter.db_obj(arq_aux4));
                        num_reg_arq4--;

                        // ARMAZENA BLOCO NO ARQ_AUX 1
                        System.out.println("\nARMAZENAMENTO ARQ_01\n");
                        for (int j = 0; j < tam_bloco_registros && i < num_registros; j++, i++) {
                            if (l1.ID < l2.ID) {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l1.EXISTE) {
                                    System.out.println(l1.ID);
                                    Converter.armazenar_Livro(arq_aux1, l1);
                                    num_reg_arq1++;
                                }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (num_reg_arq3 > 0) {
                                    l1 = new Livro(Converter.db_obj(arq_aux3));
                                    num_reg_arq3--;
                                } else {
                                    l1 = new Livro(Converter.db_obj(arq_aux4));
                                    num_reg_arq4--;
                                }
                            } else {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l2.EXISTE) {
                                    System.out.println(l2.ID);
                                    Converter.armazenar_Livro(arq_aux1, l2);
                                    num_reg_arq1++;
                                }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (num_reg_arq4 > 0) {
                                    l2 = new Livro(Converter.db_obj(arq_aux4));
                                    num_reg_arq4--;
                                } else {
                                    l2 = new Livro(Converter.db_obj(arq_aux3));
                                    num_reg_arq3--;
                                }
                            }
                        }
                        alterar_arq2 = false;
                    } else {
                        l1 = new Livro(Converter.db_obj(arq_aux3));
                        num_reg_arq3--;
                        l2 = new Livro(Converter.db_obj(arq_aux4));
                        num_reg_arq4--;

                        System.out.println("\nARMAZENAMENTO ARQ_02\n");
                        // ARMAZENA BLOCO NO ARQ_AUX 2
                        for (int j = 0; j < tam_bloco_registros && i < num_registros; j++, i++) {
                            if (l1.ID < l2.ID) {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l1.EXISTE) {
                                    System.out.println(l1.ID);
                                    Converter.armazenar_Livro(arq_aux2, l1);
                                    num_reg_arq2++;
                                }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (num_reg_arq3 > 0) {
                                    l1 = new Livro(Converter.db_obj(arq_aux3));
                                    num_reg_arq3--;
                                } else {
                                    l1 = new Livro(Converter.db_obj(arq_aux4));
                                    num_reg_arq4--;
                                }
                            } else {

                                // TESTA SE O REGISTRO NÃO FOI EXCLUIDO E ENTÃO O ARMAZENA
                                if (l2.EXISTE) {
                                    System.out.println(l2.ID);
                                    Converter.armazenar_Livro(arq_aux2, l2);
                                    num_reg_arq2++;
                                }

                                // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                                // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                                if (num_reg_arq4 > 0) {
                                    l2 = new Livro(Converter.db_obj(arq_aux4));
                                    num_reg_arq4--;
                                } else {
                                    l2 = new Livro(Converter.db_obj(arq_aux3));
                                    num_reg_arq3--;
                                }
                            }
                        }
                        alterar_arq2 = true;
                    }
                }

                // FECHAR OS ARQUIVO UTILIZADOS
                arq_aux1.close();
                arq_aux2.close();
                arq_aux3.close();
                arq_aux4.close();
            }

            if (alterar_duo_arq)
                alterar_duo_arq = false;
            else
                alterar_duo_arq = true;

            tam_bloco_registros *= 2;
        } while (tam_bloco_registros < num_registros);

        num_reg_arq01 = num_reg_arq1;
        num_reg_arq02 = num_reg_arq2;
        num_reg_arq03 = num_reg_arq3;
        num_reg_arq04 = num_reg_arq4;
    }

    /*
     * REALIZA A INTERCALAÇÃO FINAL DOS REGISTROS DOS DOIS ARQUIVOS AUXILIARES
     * PARA O DB ORIGINAL
     */
    static public int intercalacao_final(int num_registros, String src_db) throws Exception {
        RandomAccessFile arq_aux0 = new RandomAccessFile(src_db, "rw");
        long filePointer = 0;
        arq_aux0.writeLong(filePointer);
        arq_aux0.writeLong(filePointer);
        arq_aux0.writeInt(0);
        arq_aux0.writeInt(0);

        RandomAccessFile arq_index = new RandomAccessFile("Index.db", "rw");

        System.out.println("-----------INTERCALAÇÃO FINAL---------------");
        // int limite01, limite02;
        // limite01 = limite02 = 0;

        int num_reg_arq1 = num_reg_arq01;
        int num_reg_arq2 = num_reg_arq02;
        int num_reg_arq3 = num_reg_arq03;
        int num_reg_arq4 = num_reg_arq04;

        // SERVER PARA CONTAR QUANTOS REGISTROS FORAM ORDENADOS NO FINAL
        int contador = 0;
        int ultimoId = 0;

        /*
         * SE A VARIÁVEL 'alterar_duo_arq' FOR 'true', ENTÃO O ÚLTIMO PAR DE ARQUIVOS
         * UTILIZADO
         * NA INTERCALAÇÃO INTERMEDIÁRIA FOI ' arq_aux1/arq_aux2 ', SENÃO, FOI O SEGUNDO
         * PAR;
         * 
         * TENDO ISTO COMO BASE, O ALGORITMO DECIDE QUAL PAR SERÁ ABERTO PARA A
         * INTERCALAÇÃO FINAL;
         */
        if (!alterar_duo_arq) {// SERÃO UTILIZADOS OS ARQUIVOS 3/4

            System.out.println("---------ARQUIVOS 3/4-------------");
            RandomAccessFile arq_aux3 = new RandomAccessFile("arq_aux3.db", "rw");
            RandomAccessFile arq_aux4 = new RandomAccessFile("arq_aux4.db", "rw");

            Livro l1 = new Livro(Converter.db_obj(arq_aux3));
            num_reg_arq3--;

            Livro l2 = new Livro(Converter.db_obj(arq_aux4));
            num_reg_arq4--;

            // REPETIÇÃO CONTINUA ENQUANTO HOUVER REGISTROS
            for (int i = 0; i < num_registros; i++) {
                // COMPARA QUAL DOS DOIS IDs É MAIOR
                if (l1.ID < l2.ID) {

                    // ANTES DE ARMAZENAR TESTA SE O REGISTRO EXISTE OU FOI MARCADO COMO DELETADO
                    if (l1.EXISTE) {
                        armazenar_registro(l1, arq_aux0, arq_index);
                        ultimoId = l1.ID;
                        contador++;
                    }

                    // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                    // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                    if (num_reg_arq3 > 0) {

                        l1 = new Livro(Converter.db_obj(arq_aux3));
                        num_reg_arq3--;
                        // //limite01++;
                    } else {

                        l1 = new Livro(Converter.db_obj(arq_aux4));
                        num_reg_arq4--;
                        // //limite02++;
                    }
                } else {

                    // ANTES DE ARMAZENAR TESTA SE O REGISTRO EXISTE OU FOI MARCADO COMO DELETADO
                    if (l2.EXISTE) {
                        armazenar_registro(l2, arq_aux0, arq_index);
                        contador++;
                        ultimoId = l2.ID;
                    }

                    // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                    // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                    if (num_reg_arq4 > 0) {

                        l2 = new Livro(Converter.db_obj(arq_aux4));
                        num_reg_arq4--;
                    } else {

                        l2 = new Livro(Converter.db_obj(arq_aux3));
                        num_reg_arq3--;
                    }
                }
            }
            arq_aux3.close();
            arq_aux4.close();
        } else {// SERÃO UTILIZADOS OS ARQUIVOS 2/3

            System.out.println("---------ARQUIVOS 1/2------------");
            RandomAccessFile arq_aux1 = new RandomAccessFile("arq_aux1.db", "rw");
            RandomAccessFile arq_aux2 = new RandomAccessFile("arq_aux2.db", "rw");

            Livro l1 = new Livro(Converter.db_obj(arq_aux1));
            num_reg_arq1--;

            Livro l2 = new Livro(Converter.db_obj(arq_aux2));
            num_reg_arq2--;

            for (int i = 0; i < num_registros; i++) {
                // COMPARA QUAL DOS DOIS IDs É MAIOR
                if (l1.ID < l2.ID) {

                    // ANTES DE ARMAZENAR TESTA SE O REGISTRO EXISTE OU FOI MARCADO COMO DELETADO
                    if (l1.EXISTE) {
                        armazenar_registro(l1, arq_aux0, arq_index);
                        contador++;
                        ultimoId = l1.ID;
                    }

                    // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                    // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                    if (/* limite01 < tam_bloco_registros / 2 && */ num_reg_arq1 > 0) {

                        l1 = new Livro(Converter.db_obj(arq_aux1));
                        num_reg_arq1--;
                        // //limite01++;
                    } else {

                        l1 = new Livro(Converter.db_obj(arq_aux2));
                        num_reg_arq2--;
                        // //limite02++;
                    }
                } else {

                    // ANTES DE ARMAZENAR TESTA SE O REGISTRO EXISTE OU FOI MARCADO COMO DELETADO
                    if (l2.EXISTE) {
                        armazenar_registro(l2, arq_aux0, arq_index);
                        contador++;
                        ultimoId = l2.ID;
                    }

                    // TESTA SE AINDA HÁ REGISTRO ARMAZENADO NO ARQUIVO PARA ENTÃO CARREGÁ-LO, SE
                    // NÃO TEM E A REPETIÇÃO CONTINUA, ENTÃO AINDA HÁ REGISTRO NO SEGUNDO ARQUIVO
                    if (/* limite02 < tam_bloco_registros / 2 && */num_reg_arq2 > 0) {

                        l2 = new Livro(Converter.db_obj(arq_aux2));
                        num_reg_arq2--;
                        // //limite02++;
                    } else {

                        l2 = new Livro(Converter.db_obj(arq_aux1));
                        num_reg_arq1--;
                        // //limite01++;
                    }
                }
            }
            arq_aux1.close();
            arq_aux2.close();
        }
        System.out.println("ARQUIVOS ORDENADOS: " + contador);
        arq_aux0.seek(0);
        arq_aux0.writeLong(filePointer);
        arq_aux0.writeLong(filePointer);
        arq_aux0.writeInt(contador);
        arq_aux0.writeInt(ultimoId);
        arq_aux0.close();
        arq_index.close();

        return contador;

    }

    /* Armazena o registro no arquivo e salva seu id e endereco no index */
    static public void armazenar_registro(Livro livro, RandomAccessFile arq_aux0, RandomAccessFile arq_index)
            throws Exception {
        System.out.println(livro.ID);
        long endereco = arq_aux0.getFilePointer();
        int tamanho = Converter.armazenar_Livro(arq_aux0, livro);
        Indice indice = new Indice(livro.ID, tamanho, endereco);
        Index.armazenar_indice(arq_index, indice);
    }

    // ORDENA UM VETOR DE LIVROS POR MEIO DA COMPARAÇÃO DOS IDs
    static public Livro[] ordLivros(Livro[] l) throws ParseException {
        Livro auxLivro;

        for (int i = 0; i < l.length; i++) {
            for (int j = i + 1; j < l.length; j++) {
                if (l[i].ID > l[j].ID) {
                    auxLivro = new Livro(l[i]);
                    l[i] = new Livro(l[j]);
                    l[j] = new Livro(auxLivro);
                }
            }
        }
        return l;
    }
}