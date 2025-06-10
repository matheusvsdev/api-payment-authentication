package com.matheusvsdev.apipaymentauthentication.exceptions;

@SuppressWarnings("serial")
public class MaxWalletsLimitException extends RuntimeException {

	/**
     * Exceção personalizada para erros de acesso negado.
     * Estende `RuntimeException` para ser usada em regras de negócio onde
     * 		o usuário não tem permissão para executar determinada ação.
     * A anotação `@SuppressWarnings("serial")` evita alertas de serialização.
     *
     * @param msg Mensagem de erro informando a razão do bloqueio.
     */
	public MaxWalletsLimitException(String msg) {
        super(msg);
    }
}
