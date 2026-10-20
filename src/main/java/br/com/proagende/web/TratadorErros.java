package br.com.proagende.web;

import br.com.proagende.exception.PersistenciaException;
import br.com.proagende.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Converte as exceções das camadas da Etapa 6 em respostas HTTP no formato { "mensagem": "..." },
 * que o api.js exibe na tela.
 */
@RestControllerAdvice
public class TratadorErros {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> regra(RegraNegocioException e) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("mensagem", "Dados inválidos. Verifique os campos (data/hora no formato aaaa-MM-ddTHH:mm)."));
    }

    @ExceptionHandler(PersistenciaException.class)
    public ResponseEntity<Map<String, String>> banco(PersistenciaException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensagem", "Erro no banco de dados: " + e.getMessage()));
    }
}
