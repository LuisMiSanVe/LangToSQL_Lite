package com.luismisanve.langtosql;

import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.net.Uri;
import android.widget.Toast;
import com.google.gson.*;
import java.io.*;
import java.util.*;

public class MapManager {

    public String mapDatabase(Context context) {
        File db = new File(context.getFilesDir(), "dbsettings.cfg");
        FileManager fileManager = new FileManager(context);

        String file = "";
        if (db.exists()) {
            String[] dbConfig = fileManager.readFromFile("dbsettings.cfg").split(";");

            if (Boolean.parseBoolean(dbConfig[0])) {
                Object path = null;
                if (!dbConfig[1].isEmpty()) {
                    Uri uri = Uri.parse(dbConfig[1]);
                    File dbFile = null;
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(uri);
                        // Select current SQLite database and save it in cache
                        dbFile = new File(context.getCacheDir(), "current.db");

                        FileOutputStream outputStream = new FileOutputStream(dbFile);

                        byte[] buffer = new byte[8192];
                        int length;

                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();

                        path = dbFile.getAbsolutePath();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(context, R.string.error_load_sqlite, Toast.LENGTH_SHORT).show();
                        path = "";
                    } catch (IOException e) {
                        Toast.makeText(context, R.string.error_load_io, Toast.LENGTH_SHORT).show();
                        path = "";
                    }
                } else
                    path = "";

                file = path.toString();
            }
        }

        String map = "";
        if (!file.isEmpty()) {

            SQLiteDatabase sqliteDb = SQLiteDatabase.openDatabase(
                    file,
                    null,
                    SQLiteDatabase.OPEN_READONLY
            );
            try {
                // OBTAIN DB
                // Tables
                Cursor tablesDB = sqliteDb.rawQuery("SELECT name AS table_name FROM sqlite_schema WHERE type = 'table' AND name NOT LIKE 'sqlite_%' ORDER BY name;", null);
                // Table    Column
                Map<String, List<String>> tables = new Hashtable<String, List<String>>();

                while (tablesDB.moveToNext()) {
                    String tableName = tablesDB.getString(0);
                    tables.put(tableName, new ArrayList<>());
                }
                tablesDB.close();
                // Columns
                for (String tableName : tables.keySet())
                {
                    Cursor columnsDB = sqliteDb.rawQuery("PRAGMA table_info('" + tableName + "')", null);

                    List<String> columns = new ArrayList<String>();

                    while (columnsDB.moveToNext()) {
                        String columnName = columnsDB.getString(columnsDB.getColumnIndexOrThrow("name"));

                        String dataType = columnsDB.getString(columnsDB.getColumnIndexOrThrow("type"));

                        int pk =columnsDB.getInt(columnsDB.getColumnIndexOrThrow("pk"));

                        String columnInfo =columnName + "(" + dataType + ")";

                        if (pk == 1)
                            columnInfo += " (PK)";

                        columns.add(columnInfo);
                    }

                    tables.put(tableName,columns);

                    columnsDB.close();
                }

                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();
                map = gson.toJson(tables);
            } catch (SQLiteException e) {
                Toast.makeText(context, context.getString(R.string.error_run_query) + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            sqliteDb.close();
        } else {
            Toast.makeText(context, R.string.error_nosqlite, Toast.LENGTH_SHORT).show();
        }

        return map;
    }

}
