package io.camunda.valkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalConnectorRuntime {

  public static void main(String[] args) {
    System.out.println("========================================");
    System.out.println("Jackson Databind loaded from:");
    System.out.println(
            com.fasterxml.jackson.databind.ObjectMapper.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
    );

    System.out.println("Jackson Databind version:");
    System.out.println(
            com.fasterxml.jackson.databind.ObjectMapper.class
                    .getPackage()
                    .getImplementationVersion()
    );
    try {
      Class<?> clazz =
              Class.forName("com.fasterxml.jackson.databind.ObjectMapper");

      System.out.println("ObjectMapper FOUND");
      System.out.println(
              clazz.getProtectionDomain()
                      .getCodeSource()
                      .getLocation()
      );

    } catch (Throwable e) {
      System.out.println("ObjectMapper NOT FOUND");
      e.printStackTrace();
    }

    System.out.println("========================================");

    SpringApplication.run(LocalConnectorRuntime.class, args);  }
}
