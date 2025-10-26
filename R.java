import java.io.*;
import java.net.*;

public class R {
    public static void main(String[] args) {
        try {
            int porta = 10000;
            System.out.println("[R] Receptor iniciado na porta " + porta);

            ServerSocket servidor = new ServerSocket(porta);
            Socket conexao = servidor.accept();
            System.out.println("[R] Conexao recebida de " + conexao.getInetAddress().getHostAddress());

            ObjectOutputStream transmissor = new ObjectOutputStream(conexao.getOutputStream());
            ObjectInputStream receptor = new ObjectInputStream(conexao.getInputStream());

            while (true) {
                try {
                    Comunicado comunicado = (Comunicado) receptor.readObject();

                    if (comunicado instanceof ComunicadoEncerramento) {
                        System.out.println("[R] Encerramento solicitado. Finalizando");
                        break;
                    }

                    if (comunicado instanceof Pedido) {
                        Pedido pedido = (Pedido) comunicado;
                        byte[] vetor = pedido.getNumeros();
                        byte procurado = pedido.getByteProcurado();

                        int numThreads = Runtime.getRuntime().availableProcessors();
                        ThreadContadora[] threads = new ThreadContadora[numThreads];
                        int parte = vetor.length / numThreads;

                        for (int i = 0; i < numThreads; i++) {
                            int inicio = i * parte;
                            int fim = (i == numThreads - 1) ? vetor.length : (i + 1) * parte;
                            threads[i] = new ThreadContadora(vetor, procurado, inicio, fim);
                            threads[i].start();
                        }

                        int total = 0;
                        for (ThreadContadora t : threads) {
                            t.join();
                            total += t.getResultado();
                        }

                        transmissor.writeObject(new Resposta(total));
                        transmissor.flush();

                        System.out.println("[R] Contagem concluida e enviada: " + total);
                    }
                } catch (java.io.EOFException eof) {
                    // Cliente fechou a conexão sem enviar ComunicadoEncerramento
                    // Apenas continue esperando por novas conexões
                }
            }

            receptor.close();
            transmissor.close();
            conexao.close();
            servidor.close();

        } catch (Exception e) {
            System.err.println("[R] Erro: " + e);
        }
    }
}
