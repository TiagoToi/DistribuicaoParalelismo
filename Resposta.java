import java.io.Serializable;

public class Resposta extends Comunicado implements Serializable {
    private static final long serialVersionUID = 1L;
    int contagem;

    public Resposta(int contagem) {
        this.contagem = contagem;
    }

    public int getContagem() {
        return contagem;
    }
}