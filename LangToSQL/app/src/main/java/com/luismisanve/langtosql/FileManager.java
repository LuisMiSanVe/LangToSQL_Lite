package com.luismisanve.langtosql;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileManager {
    private Context context;

    public FileManager(Context _context){
        context = _context;
    }

    public void writeToFile(String fileName, String data) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, MODE_PRIVATE);
            fos.write(data.getBytes(StandardCharsets.UTF_8));
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public String readFromFile(String fileName) {
        StringBuilder sb = new StringBuilder();

        try (FileInputStream fis = context.openFileInput(fileName);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
