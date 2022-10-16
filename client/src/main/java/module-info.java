open module SE206Client {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.swing;
  requires javafx.media;
  requires freetts;
  requires ai.djl.api;
  requires imgscalr.lib;
  requires org.slf4j;
  requires com.opencsv;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.annotation;
  requires javafx.graphics;
  requires okhttp3;
  requires org.json;
  requires org.apache.commons.lang3;
  requires javafx.base;
  requires SE206Core;

  exports nz.ac.auckland.se206.client;
}
