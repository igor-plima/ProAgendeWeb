package br.com.proagende.exception;

/**
 * Lançada quando uma regra de negócio é violada (dado inválido, conflito de horário,
 * exclusão bloqueada etc.). A camada de apresentação (Swing ou web) decide como exibir a mensagem.
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
