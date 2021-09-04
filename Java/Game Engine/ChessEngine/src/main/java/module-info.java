module cz.chess.engine.view {
    requires javafx.controls;
    requires com.google.common;
    requires org.junit.jupiter.api;
    requires junit;
    requires java.logging;
    requires javafx.web;
    exports cz.chess.engine.view_controller;
    exports cz.chess.tests;
    exports cz.chess.engine;
}
