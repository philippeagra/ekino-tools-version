FROM openjdk:8u131-jre-alpine
MAINTAINER ekino
ADD build/distributions/playBinary.tar /play/
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dhttp.port=8080 -classpath /play/playBinary/lib/ekino-tools-version.jar play.core.server.ProdServerStart"]
