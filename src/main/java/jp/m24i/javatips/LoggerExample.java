package jp.m24i.javatips;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerExample {

  private static final boolean FILE_APPENDS = false;

  public static void main(String[] args) throws IOException {
    Logger logger = Logger.getLogger(LoggerExample.class.getName());
    logger.setLevel(Level.ALL);
    logger.addHandler(xmlFileHandler());
    logger.addHandler(fileHandler());

    logger.severe("Level is severe.");
    logger.warning("Level is warning.");
    logger.info("Level is info.");
    logger.config("Level is config.");
    logger.fine("Level is fine.");
    logger.finer("Level is finer.");
    logger.finest("Level is finest.");
  }

  private static FileHandler xmlFileHandler() throws IOException {
    return new FileHandler("javatips.log.xml", FILE_APPENDS);
  }

  private static FileHandler fileHandler() throws IOException {
    FileHandler fileHandler = new FileHandler("javatips.log.txt", FILE_APPENDS);
    fileHandler.setFormatter(new SimpleFormatter());
    return fileHandler;
  }

}
