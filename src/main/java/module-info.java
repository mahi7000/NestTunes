module com.example.app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires javafx.media;
    requires java.sql;
//    requires jbcrypt;
//    requires google.api.services.oauth2.v2.rev157;
//    requires google.api.client;
//    requires com.google.api.client.extensions.jetty.auth;
//    requires com.google.api.client.auth;
//    requires com.google.api.client.extensions.java6.auth;
//    requires com.google.api.client.json.jackson2;
//    requires com.fasterxml.jackson.core;
//    requires com.google.api.client.json.gson;
//    requires com.google.api.client;
    requires jdk.httpserver;
    requires javafx.base;
    requires javafx.graphics;
    requires jbcrypt;
    requires java.desktop;


    opens com.example.app to javafx.fxml;
    exports com.example.app;
    exports com.example.app.controllers;
    opens com.example.app.controllers to javafx.fxml;
    exports com.example.app.services;
    opens com.example.app.services to javafx.fxml;
}