package org.opencorpora;

/**
 * Интерфейс слушателя сервиса авторизации.
 */
public interface IAuthListener {
    void onSuccess();
    void onFail();
}
