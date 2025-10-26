import java.io.*;
import java.net.*;


public class D {
    private static final String[] IPS = {"localhost"};
    private static final int PORTA = 10000;
    private static final int min = -100;
    private static final int max = 100;

    public static void main(String[] args) {
        try {
            int tamanhoVetor = 100_000;
            byte[] vetor = new byte[tamanhoVetor];
            for (int i = 0; i < vetor.length; i++) {
                int aleatorio = ((int)(Math.random() * (max - min))) + min;
                vetor[i] = (byte) aleatorio;
            }

            int parte = vetor.length / IPS.length;
            ContagemThread[] threads = new ContagemThread[IPS.length];
            for (int i = 0; i < IPS.length; i++) {
                int inicioParte = i * parte;
                int fimParte = (i == IPS.length - 1) ? vetor.length : (i + 1) * parte;
                byte[] subVetor = new byte[fimParte - inicioParte];
                System.arraycopy(vetor, inicioParte, subVetor, 0, subVetor.length);
                threads[i] = new ContagemThread(IPS[i], PORTA, subVetor);
                threads[i].start();
            }

            while (true) {
                System.out.print("[D] Digite o numero a ser procurado (ou 'encerrar'): ");
                String entrada = Teclado.getUmString().trim();
                if (entrada.equalsIgnoreCase("encerrar")) {
                    for (ContagemThread t : threads) {
                        t.encerrar();
                    }
                    break;
                }
                try {
                    long inicio = System.currentTimeMillis();
                    int procuradoInt = Integer.parseInt(entrada);
                    if (procuradoInt < -100 || procuradoInt > 100) {
                        System.out.println("[D] Por favor, digite um número entre -100 e 100.");
                        continue;
                    }
                    byte procurado = (byte) procuradoInt;
                    for (ContagemThread t : threads) {
                        t.buscar(procurado);
                    }
                    int total = 0;
                    for (ContagemThread t : threads) {
                        t.aguardarResultado();
                        total += t.getResultado();
                    }
                    System.out.printf("[D] Tempo total de contagem: %.2f segundos%n", (System.currentTimeMillis() - inicio) / 1000.0);
                    System.out.println("[D] Total de ocorrencias do byte " + procurado + ": " + total);

                } catch (NumberFormatException ex) {
                    System.out.println("[D] Entrada inválida. Digite um número ou 'encerrar'.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ContagemThread extends Thread {
    private final String ip;
    private final int porta;
    private final byte[] vetor;
    private Socket socket;
    private ObjectOutputStream transmissor;
    private ObjectInputStream receptor;
    private volatile boolean encerrar = false;
    private volatile boolean buscar = false;
    private volatile byte procurado;
    private volatile int resultado;
    private final Object lock = new Object();

    public ContagemThread(String ip, int porta, byte[] vetor) {
        this.ip = ip;
        this.porta = porta;
        this.vetor = vetor;
    }

    public void buscar(byte procurado) {
        synchronized (lock) {
            this.procurado = procurado;
            this.buscar = true;
            lock.notify();
        }
    }

    public void encerrar() {
        synchronized (lock) {
            this.encerrar = true;
            lock.notify();
        }
    }

    public void aguardarResultado() throws InterruptedException {
        synchronized (lock) {
            while (buscar) {
                lock.wait();
            }
        }
    }

    public int getResultado() {
        return resultado;
    }

    public void run() {
        try {
            socket = new Socket(ip, porta);
            transmissor = new ObjectOutputStream(socket.getOutputStream());
            receptor = new ObjectInputStream(socket.getInputStream());
            while (true) {
                synchronized (lock) {
                    while (!buscar && !encerrar) {
                        lock.wait();
                    }
                    if (encerrar) {
                        transmissor.writeObject(new ComunicadoEncerramento());
                        transmissor.flush();
                        break;
                    }
                    transmissor.writeObject(new Pedido(vetor, procurado));
                    transmissor.flush();
                    buscar = false;
                }
                Resposta resposta = (Resposta) receptor.readObject();
                resultado = resposta.getContagem();
                System.out.println("[D] Thread para IP " + ip + " contou: " + resultado);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
            transmissor.close();
            receptor.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("[D] Erro na comunicacao com " + ip + ": " + e.getMessage());
        }
    }
}