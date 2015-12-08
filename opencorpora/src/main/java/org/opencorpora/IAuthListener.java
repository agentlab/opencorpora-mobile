package org.opencorpora;

/**
 * Интерфейс слушателя сервиса авторизации.
 */
interface IAuthListener {
    void onSuccess();
    void onFail();
}
