package TP_AED3;

// import java.io.File;
// import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Scanner;

public class Main {

    public static String src_txt = "C:\\Users\\NovaY\\OneDrive\\Documentos\\PUC-\\AED3\\Java\\AED3\\src\\TP_AED3_soqMelhor\\TP_AED3_soqMelhor\\books.csv";
    public static String src_index = "Index.db";
    public static String src_db = "livros02.db";
    public static String src_meta = "meta_dados.db";
    public static String src_temp = "src_temp.db";
    public static String src_chaves = "chaves.txt";
    // public static ParChaves parChaves;
    public static int num_linhas; 
    public static ParChaves parChaves;

    public static void main(String[] args) throws Exception{
        parChaves = Cripto_assimetrica.gerarParChaves();
        Menu();
    }

    // CONVERTE O ARQUIVO EM CSV PARA UM VETOR DE OBJETO DO TIPO LIVRO
    static Livro[] Converter_CSV_OBJ() throws ParseException {

        Livro[] livro = new Livro[num_linhas];
        livro = Converter.txt_obj(num_linhas, src_txt);
        System.out.println("\n+++++++++ CONVERTIDO +++++++++\n");
        return livro;
    }

    public static void MOSTRAR_INDEX() throws IOException, ParseException {

        Indice[] indice = Index.db_obj_index(src_index, num_linhas);
        for (int i = 0; i < indice.length; i++) {
            System.out.println(indice[i]);
        }
    }

    public static void Menu() throws Exception {

        Scanner eScanner = new Scanner(System.in);
        int ESCOLHA = 0;
        // num_linhas = Converter.tam_arquivo(src_txt);
        num_linhas = 20;
        Livro[] livro = new Livro[num_linhas];

        // livro = Converter_CSV_OBJ();
        // Converter.obj_db(livro, src_db, src_meta);

        do {
            System.out.println("\n+++++++++ MENU PRINCIPAL +++++++++\n");
            System.out.println("    OPÇÕES: " +
                    "\n\t1    -   CONVERTER ARQUIVO CSV PARA OBJETO" +
                    "\n\t2    -   CONVERTER OBJETO PARA DB" +
                    "\n\t3    -   ORDENAR REGISTROS DO DB" +
                    "\n\t4    -   MOSTRAR REGISTROS ARMAZENADOS NO DB" +
                    "\n\t5    -   ABRIR CRUD" +
                    "\n\t6    -   MOSTRAR INDEX" +
                    "\n\t0    -   SAIR\n");

            ESCOLHA = eScanner.nextInt();

            switch (ESCOLHA) {
                case 1: {
                    System.out.println("\t+++++++++ CONVERTER CSV PARA OBJ +++++++++\n");
                    livro = Converter_CSV_OBJ();
                    break;
                }

                case 2: {
                    System.out.println("\t+++++++++ CONVERTER OBJ PARA DB +++++++++\n");
                    Converter.obj_db(livro, src_db, src_index, num_linhas);
                    break;
                }

                case 3: {
                    System.out.println("\t+++++++++ ORDENAR REGISTROS DO DB +++++++++\n");
                    num_linhas = Ordenar.ordenar(src_db, num_linhas);
                    break;
                }

                case 4: {
                    System.out.println("\t+++++++++ MOSTRAR REGISTROS DO DB +++++++++\n");
                    Converter.mostrar_db(src_db, num_linhas);
                    break;
                }

                case 5: {
                    System.out.println("\t+++++++++ ABRIR CRUD +++++++++\n");
                    CRUD.CRUD(src_db, src_index, num_linhas);
                    break;
                }

                case 6: {
                    System.out.println("\t+++++++++ MOSTRAR INDEX +++++++++\n");
                    Index.MOSTRAR(src_index, src_db, num_linhas);
                    break;
                }

                // case 7: {
                // System.out.println("\t+++++++++ TESTAR CRIPTOGRAFIA +++++++\n");
                // Chaves.simular_sem_bd(src_txt);
                // break;
                // }

                default:
                    System.out.println("\n+++++++++ OPÇÃO INVÁLIDA +++++++++\n");
                    break;
            }
        } while (ESCOLHA != 0);
    }

}
