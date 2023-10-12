package TP_AED3;
import java.text.ParseException;

public class Chaves {
    public static ParChaves parChaves; 


    // public static void buscarChaves(String src_chaves)throws IOException{
    //     RandomAccessFile arq = new RandomAccessFile(src_chaves, "rw");

    //         String[] txt_split;

    //         try {
    //             String s;
    //             if (arq.length() > 0){
    //                     arq.seek(0);
    //                     s = arq.readLine();
    //                     txt_split = s.split("/");
    //                     // parChaves = new ParChaves(txt_split);
    //                 }
    //                 else{
    //                     arq.seek(0);
    //                     System.out.println("Nenhuma chave Registrada\nGerando chaves....\n");
    //                     parChaves = Cripto_assimetrica.gerarParChaves();
    //                     arq.writeUTF(parChaves.ChavePublica.e.toString() + "/" + parChaves.ChavePublica.n.toString() + "/" + parChaves.ChavePrivada.d.toString() + parChaves.ChavePrivada.n.toString());
    //                 }
    //             arq.close();
    //         } catch (FileNotFoundException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         } finally {
    //         }
    // }


    // public static void simular_sem_bd(String src_txt) throws ParseException{

    //     Main.parChaves = Cripto_assimetrica.gerarParChaves();

    //     Livro[] livros_criptografados = Converter.txt_obj(5, src_txt);

    //     System.out.println("Titulo criptografado: \n\n");
    //     for (Livro livro : livros_criptografados) {
    //         livro.TITULO = Cripto_assimetrica.criptografar(livro.TITULO, Main.parChaves.ChavePublica);
    //         System.out.println(livro);
    //     }

    //     System.out.println("\n\nTitulo descriptografado:\n\n");

    //     for (Livro livro : livros_criptografados) {
    //         livro.TITULO = Cripto_assimetrica.descriptografar(livro.TITULO, Main.parChaves.ChavePrivada);
    //         System.out.println(livro);
    //     }
    // }
}
