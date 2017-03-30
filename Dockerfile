FROM knoker/rpi-vertx

COPY . /verticles

WORKDIR /verticles

ENV VERTICLE_NAME eu.knoker.rpi.lights.Verticle

ENTRYPOINT ["sh", "-c"]
#ENTRYPOINT ["tail","-f","/dev/null"]
CMD ["export CLASSPATH=`find $VERTICLE_HOME -printf '%p:' | sed 's/:$//'`; /root/.sdkman/candidates/vertx/current/bin/vertx run $VERTICLE_NAME"]