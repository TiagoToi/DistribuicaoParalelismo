import java.util.*;

public class ThreadContadora extends Thread {
    private byte[] vetor;
    private byte procurado;
    private int inicio;
    private int fim;
    private int resultado;

    public ThreadContadora(byte[] vetor, byte procurado, int inicio, int fim) {
        this.vetor = vetor;
        this.procurado = procurado;
        this.inicio = inicio;
        this.fim = fim;
        this.resultado = 0;
    }

    public void run() {
        for (int i = inicio; i < fim; i++) {
            if (vetor[i] == procurado) {
                resultado++;
            }
        }
    }

    public int getResultado() {
        return resultado;
    }
}
