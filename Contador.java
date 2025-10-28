
public class Contador {
    private static final int min = -100;
    private static final int max = 100;
    public static void main(String[] args) {

        int tamanhoVetor = 100000000;
        
        byte[] vetor = new byte[tamanhoVetor];
        for (int i = 0; i < vetor.length; i++) {
            int aleatorio = ((int)(Math.random() * (max - min))) + min;
            vetor[i] = (byte) aleatorio;
        }

        System.out.print("[D] Digite o numero a ser procurado (ou 'encerrar'): ");
        String entrada = Teclado.getUmString().trim();

        long inicio = System.currentTimeMillis();
        int contador = 0;

        for(byte b : vetor) {
            if(Byte.toString(b).equals(entrada)) {
                contador++;
            }
        }
        System.out.printf("[D] Tempo total de contagem: %.2f segundos%n", (System.currentTimeMillis() - inicio) / 1000.0);
        System.out.println("[D] Total de ocorrencias do byte " + entrada + ": " + contador);
    }
}
