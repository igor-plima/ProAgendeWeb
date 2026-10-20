package br.com.proagende.exception;

/**
 * Lançada quando ocorre uma falha de acesso ao banco de dados.
 * Substitui o antigo System.out.println(e.getMessage()) dos DAOs, que escondia o erro da tela.
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
