package eu.knoker.rpi.lights;

import io.vertx.core.Vertx;

/**
 * Created by eduardo on 22/03/2017.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main");
        Vertx.vertx().deployVerticle(new Verticle());
    }
}
