package br.com.proagende.util;

import br.com.proagende.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** CT-04 a CT-07: validações de cadastro. */
class ValidadorTest {

    @Test
    void exigirTextoRemoveEspacosDasPontas() {
        assertEquals("Ana Lima", Validador.exigirTexto("  Ana Lima  ", "Nome", 100));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void exigirTextoRejeitaVazioOuNulo(String valor) {
        RegraNegocioException e = assertThrows(RegraNegocioException.class,
                () -> Validador.exigirTexto(valor, "Nome", 100));
        assertEquals("O campo Nome é obrigatório.", e.getMessage());
    }

    @Test
    void exigirTextoRejeitaAcimaDoLimite() {
        assertThrows(RegraNegocioException.class, () -> Validador.exigirTexto("abcdef", "Nome", 5));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ana@email.com", "joao.silva+agenda@empresa.com.br"})
    void emailValidoEhAceito(String email) {
        assertEquals(email, Validador.exigirEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"email-invalido", "ana@", "@email.com", "ana@email"})
    void emailInvalidoEhRejeitado(String email) {
        assertThrows(RegraNegocioException.class, () -> Validador.exigirEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1133334444", "11999998888"})
    void telefoneComDddEhAceito(String telefone) {
        assertEquals(telefone, Validador.exigirTelefone(telefone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "11 99999-8888", "11999ABC888", "119999988889"})
    void telefoneInvalidoEhRejeitado(String telefone) {
        assertThrows(RegraNegocioException.class, () -> Validador.exigirTelefone(telefone));
    }

    @Test
    void valorPositivoAceitaMaiorQueZero() {
        assertEquals(50.0, Validador.exigirPositivo(50.0, "Preço"));
        assertEquals(30, Validador.exigirPositivo(30, "Duração"));
    }

    @Test
    void valorZeroOuNegativoEhRejeitado() {
        assertThrows(RegraNegocioException.class, () -> Validador.exigirPositivo(0.0, "Preço"));
        assertThrows(RegraNegocioException.class, () -> Validador.exigirPositivo(-5, "Duração"));
    }
}
