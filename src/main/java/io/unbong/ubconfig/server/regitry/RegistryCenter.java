package io.unbong.ubconfig.server.regitry;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-05-25 18:08
 */
public interface RegistryCenter {

    void start(); // p/c
    void stop(); // p/c
    void register();
    void unregister();
}
