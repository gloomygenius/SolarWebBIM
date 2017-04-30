package edu.spbpu.util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileManager {
    public static void saveToGZ(String filePath, Object entity) {
        String name = filePath;
        File file = new File(name);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdir()) throw new RuntimeException("Path can't be create");
        }
        name = name.replaceAll("\\\\\\\\", "\\\\"); //меняем два слэша на один, если вдруг так получилось
        try (FileOutputStream stream = new FileOutputStream(name);
             GZIPOutputStream gz = new GZIPOutputStream(stream);
             ObjectOutputStream out = new ObjectOutputStream(gz)) {
            out.writeObject(entity);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static <E> E readFromGZ(String filePath) {
        E entity;
        try (FileInputStream stream = new FileInputStream(filePath);
             GZIPInputStream gz = new GZIPInputStream(stream);
             ObjectInputStream out = new ObjectInputStream(gz)) {
            //noinspection unchecked
            entity = (E) out.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}
