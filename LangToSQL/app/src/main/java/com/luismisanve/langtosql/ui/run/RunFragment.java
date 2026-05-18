package com.luismisanve.langtosql.ui.run;

import android.database.*;
import android.database.sqlite.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.luismisanve.langtosql.*;
import com.luismisanve.langtosql.databinding.FragmentRunBinding;
import static android.view.View.*;
import org.json.*;

import java.io.*;
import retrofit2.*;

public class RunFragment extends Fragment {
    // Variables
    private FragmentRunBinding binding;
    private EditText requestText;
    private ImageButton sendButton;
    private LinearLayout queryLayout;
    private ImageButton runButton;
    private EditText queryText;
    private TableLayout tableLayout;
    public static int showQuery = GONE;
    private FileManager fileManager;
    private String file = "";
    private String apiIp = "";
    private String apiPort = "";
    private String geminiKey = "";
    private String llmIp = "";
    private String llmPort = "";
    private String llmModel = "";

    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RunViewModel runViewModel =
                new ViewModelProvider(this).get(RunViewModel.class);

        binding = FragmentRunBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Layout Objects
        requestText = root.findViewById(R.id.requestText);
        sendButton = root.findViewById(R.id.sendButton);
        queryLayout = root.findViewById(R.id.queryLayout);
        queryText = root.findViewById(R.id.queryText);
        runButton = root.findViewById(R.id.runButton);
        tableLayout = root.findViewById(R.id.tableLayout);

        // Load config
        fileManager = new FileManager(getContext());
        queryLayout.setVisibility(showQuery);
        File db = new File(getContext().getFilesDir(), "dbsettings.cfg");
        File ai = new File(getContext().getFilesDir(), "aisettings.cfg");
        if (db.exists()) {
            String[] dbConfig = fileManager.readFromFile("dbsettings.cfg").split(";");

            if (Boolean.parseBoolean(dbConfig[0])) {
                Uri uri = Uri.parse(dbConfig[1]);
                File dbFile = null;
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                    // Select current SQLite database and save it in cache
                    dbFile = new File(getContext().getCacheDir(), "current.db");

                    FileOutputStream outputStream = new FileOutputStream(dbFile);

                    byte[] buffer = new byte[8192];
                    int length;

                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getContext(), "The configured SQLite database doesn't exist.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Failed to access the configured SQLite database.", Toast.LENGTH_SHORT).show();
                }

                file = dbFile.getAbsolutePath();
                apiIp = "";
                apiPort = "";
            } else {
                apiIp = dbConfig[2];
                apiPort = dbConfig[3];
                file = "";
            }
        }
        if (ai.exists()) {
            String[] aiConfig = fileManager.readFromFile("aisettings.cfg").split(";");

            if (Boolean.parseBoolean(aiConfig[0])) {
                geminiKey = aiConfig[2];
                llmIp = "";
                llmPort = "";
                llmModel = "";
            } else {
                llmIp = aiConfig[3];
                llmPort = aiConfig[4];
                llmModel = aiConfig[5];
                geminiKey = "";
            }
        }

        // Events
        sendButton.setOnClickListener(v -> {
            if (!file.isEmpty()) { // Generate the query using the AI selected

                runButton.performClick();
            } else { // Generate the result in a PostgreSQL server using the LangToSQL REST API, overring its settings
                RestApiCall api = new RestApiClient("http://" + apiIp + ":" + apiPort.trim() + "/").getClient().create(RestApiCall.class);

                Call<String> call = api.generateSQL(requestText.getText().toString());

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                String jsonString = response.body();

                                JSONArray jsonArray = new JSONArray(jsonString);

                                MatrixCursor cursor = jsonToCursor(jsonArray);

                                buildTable(cursor);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "The REST API's response couldn't be interpreted.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getContext(), "The REST API is not reachable.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        runButton.setOnClickListener(v -> {
            Cursor cursor = null;

            if (!file.isEmpty()) { // SQLite
                SQLiteDatabase sqliteDb = SQLiteDatabase.openDatabase(
                        file,
                        null,
                        SQLiteDatabase.OPEN_READONLY
                );
                try {
                    cursor = sqliteDb.rawQuery(queryText.getText().toString(), null);
                } catch (SQLiteException e) {
                    Toast.makeText(getContext(), "The query failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                sqliteDb.close();
            }

            if (cursor != null)
                buildTable(cursor);
        });

        return root;
    }

    // Other methods
    private void buildTable(Cursor cursor) {
        tableLayout.removeAllViews();

        if (cursor == null) return;

        String[] columns = cursor.getColumnNames();

        TableRow headerRow = new TableRow(getContext());

        for (String col : columns) {
            TextView tv = new TextView(getContext());
            tv.setText(col);
            tv.setPadding(8, 8, 8, 8);
            tv.setBackgroundColor(0xFFE7E7E7);
            tv.setGravity(Gravity.CENTER);
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.outline_box));

            headerRow.addView(tv);
        }

        tableLayout.addView(headerRow);

        while (cursor.moveToNext()) {

            TableRow row = new TableRow(getContext());

            for (int i = 0; i < columns.length; i++) {

                TextView tv = new TextView(getContext());

                int type = cursor.getType(i);

                String value;

                switch (type) {
                    case Cursor.FIELD_TYPE_NULL:
                        value = "NULL";
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        value = "[BLOB]";
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                    case Cursor.FIELD_TYPE_FLOAT:
                        value = String.valueOf(cursor.getString(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                    default:
                        value = cursor.getString(i);
                        break;
                }
                tv.setText(value);
                tv.setPadding(8, 8, 8, 8);
                tv.setBackgroundColor(0xFFEFEFEF);
                tv.setGravity(Gravity.CENTER);

                row.addView(tv);
            }

            tableLayout.addView(row);
        }

        cursor.close();
    }

    public MatrixCursor jsonToCursor(JSONArray array) throws Exception {

        if (array.length() == 0) return null;

        JSONObject first = array.getJSONObject(0);
        JSONArray keys = first.names();

        String[] columns = new String[keys.length()];
        for (int i = 0; i < keys.length(); i++) {
            columns[i] = keys.getString(i);
        }

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            Object[] row = new Object[columns.length];

            for (int j = 0; j < columns.length; j++) {
                String key = columns[j];
                row[j] = obj.opt(key); // safe get
            }

            cursor.addRow(row);
        }

        return cursor;
    }

    // Destroyer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}