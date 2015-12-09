package org.opencorpora.authenticator;

/**
 * Интерфейс слушателя сервиса авторизации.
 */
public interface IAuthListener {
    void onSuccess();
    void onFail();
}
