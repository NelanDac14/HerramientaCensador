package nelandac.app.herramientacensador.sync;

public interface Callback<T> {
    void onComplete(T result);
    void onError(Exception e);
}