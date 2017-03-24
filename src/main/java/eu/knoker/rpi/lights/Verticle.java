package eu.knoker.rpi.lights;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.RaspiPin;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * Created by eduardo on 22/03/2017.
 */
public class Verticle extends AbstractVerticle {

    public static Vertx vertxInstance;
    private static GpioPinPwmOutput redPin;
    private static GpioPinPwmOutput greenPin;
    private static GpioPinPwmOutput bluePin;
    private GpioController gpio;

    public static void setRed(int r) {
        redPin.setPwm(r);
    }

    public static void setGreen(int g) {
        greenPin.setPwm(g);
    }

    public static void setBlue(int b) {
        bluePin.setPwm(b);
    }

    @Override
    public void start() throws Exception {
        super.start();
        System.out.println("start");
        Verticle.vertxInstance = vertx;

        gpio = GpioFactory.getInstance();
        redPin = gpio.provisionSoftPwmOutputPin(RaspiPin.GPIO_00);
        greenPin = gpio.provisionSoftPwmOutputPin(RaspiPin.GPIO_03);
        bluePin = gpio.provisionSoftPwmOutputPin(RaspiPin.GPIO_05);

        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(255);
        com.pi4j.wiringpi.Gpio.pwmSetClock(5000);


        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.DELETE)
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization"));
        addEndpoints(router);
        vertx.createHttpServer().requestHandler(router::accept).listen(5000);
    }

    @Override
    public void stop() {
        redPin.setPwm(0);
        greenPin.setPwm(0);
        bluePin.setPwm(0);
        gpio.shutdown();
    }


    private void addEndpoints(Router router) {
        ColorHandler colorHandler = new ColorHandler();
        router.get("/set/:color").handler(colorHandler::set);
        router.get("/cycle/:colors").handler(colorHandler::cycle);
    }
}
