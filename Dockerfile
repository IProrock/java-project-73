FROM eclipse-temurin:20-jdk

RUN apt-get update

RUN apt-get install unzip

#RUN wget -q https://services.gradle.org/distributions/gradle-8.3-bin.zip
#
#RUN unzip gradle-8.3-bin.zip
#
#RUN rm gradle-8.3-bin.zip
#
#RUN mv gradle-8.3 /opt/gradle

ENV PATH=$PATH:/opt/gradle/gradle-8.3/bin

WORKDIR ./prj5

COPY . /prj5

RUN chmod +x gradlew

RUN ./gradlew stage

CMD ./build/install/app/bin/app