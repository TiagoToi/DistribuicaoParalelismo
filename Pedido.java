import java.io.Serializable;

public class Pedido extends Comunicado implements Serializable {
    private static final long serialVersionUID = 1L;
    byte[] numeros;
    byte byteProcurado;

    public Pedido(byte[] numeros, byte byteProcurado) {
        this.numeros = numeros;
        this.byteProcurado = byteProcurado;
    }

    public int contar() {
        int contador = 0;
        for (byte numero : numeros) {
            if (numero == byteProcurado) {
                contador++;
            }
        }
        return contador;
    }

    public byte getByteProcurado() {
        return byteProcurado;
    }

    public byte[] getNumeros() {
        return numeros;
    }
}