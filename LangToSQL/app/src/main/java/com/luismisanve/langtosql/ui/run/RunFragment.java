package com.luismisanve.langtosql.ui.run;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import okhttp3.*;

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
    private MapManager mapManager;
    private String file = "";
    private String apiIp = "";
    private String apiPort = "";
    private String geminiKey = "";
    private String llmIp = "";
    private String llmPort = "";
    private String llmModel = "";
    private String json = "";
    private String databaseName = "";

    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RunViewModel runViewModel = new ViewModelProvider(this).get(RunViewModel.class);
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
        mapManager = new MapManager();
        queryLayout.setVisibility(showQuery);
        File db = new File(getContext().getFilesDir(), "dbsettings.cfg");
        File ai = new File(getContext().getFilesDir(), "aisettings.cfg");
        if (db.exists()) {
            String[] dbConfig = fileManager.readFromFile("dbsettings.cfg").split(";");

            if (Boolean.parseBoolean(dbConfig[0])) {
                Object path = null;
                if (!dbConfig[1].isEmpty()) {
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

                        path = dbFile.getAbsolutePath();

                        // Get database file name
                        String name = "";
                        if (uri.getScheme().equals("content")) {
                            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                            try {
                                if (cursor != null && cursor.moveToFirst()) {
                                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    if (index != -1)
                                        name = cursor.getString(index);
                                }
                            } finally {
                                if (cursor != null)
                                    cursor.close();
                            }
                        }

                        if (name == null)
                            name = uri.getLastPathSegment();

                        databaseName = (name.contains(".")) ? name.split("\\.")[0] : name;
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getContext(), R.string.error_load_sqlite, Toast.LENGTH_SHORT).show();
                        path = "";
                    } catch (IOException e) {
                        Toast.makeText(getContext(), R.string.error_load_io, Toast.LENGTH_SHORT).show();
                        path = "";
                    }
                }
                else
                    path = "";

                file = path.toString();
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
                geminiKey = aiConfig[1];
                llmIp = "";
                llmPort = "";
                llmModel = "";
            } else {
                llmIp = aiConfig[2];
                llmPort = aiConfig[3];
                llmModel = aiConfig[4];
                geminiKey = "";
            }
        }

        JSONArray priorJson = runViewModel.getJson();
        if (priorJson != null) {
            try {
                Cursor priorCursor = jsonToCursor(priorJson);
                buildTable(priorCursor);
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.error_load_prior, Toast.LENGTH_SHORT).show();
            }
        }

        String priorMap = runViewModel.getMap();
        if (priorMap != null)
            json = priorMap;

        // Events
        sendButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.text_generate, Toast.LENGTH_SHORT).show();
            if (!file.isEmpty()) { // Generate the query using the AI selected
                // Map the database
                if (json.isEmpty()) {
                    // Search for the mapped file
                    if (!databaseName.isEmpty()) {
                        File mapsFolder = getContext().getFilesDir();
                        if (mapsFolder.exists()) {
                            if (mapsFolder.listFiles().length > 0) {
                                for (File map : mapsFolder.listFiles()) {
                                    if (map.getName().contains(databaseName) && map.getName().contains(".map")) {
                                        json = fileManager.readFromFile(map.getName());
                                    }
                                }
                            }
                        }
                    }
                    // Else, it maps it
                    if (json.isEmpty())
                        json = mapManager.mapDatabase(getContext());

                    runViewModel.setMap(json); // Save the map in memory
                }

                String context = "You're a database assistant, I'll send you requests and you'll return a PostgeSQL query to do my request and if what I request can't be found on the database, tell me, but don't use more words. " +
                                "This is the database: " +
                                json +
                                "\nAnd this is my request: ";

                OkHttpClient client = new OkHttpClient();
                String endpoint = "";
                String requestUri = "";
                Object aibody = null;

                if (!geminiKey.isEmpty()) {
                    endpoint = "https://generativelanguage.googleapis.com";
                    requestUri = "/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiKey;

                    try {
                        JSONObject part = new JSONObject();
                        part.put("text", context + requestText.getText());

                        JSONObject content = new JSONObject();
                        content.put("parts", new JSONArray().put(part));

                        JSONObject body = new JSONObject();
                        body.put("contents", new JSONArray().put(content));

                        aibody = body;
                    } catch (JSONException e){
                        aibody = null;
                        Toast.makeText(getContext(), R.string.error_ai, Toast.LENGTH_LONG).show();
                    }
                } else {
                    endpoint = "http://" + llmIp + ":" + llmPort;
                    requestUri = "/v1/chat/completions";

                    try {
                        JSONObject message = new JSONObject();
                        message.put("role", "user");
                        message.put("content", context + requestText.getText());

                        JSONArray messages = new JSONArray();
                        messages.put(message);

                        JSONObject body = new JSONObject();
                        body.put("model", llmModel);
                        body.put("messages", messages);
                        body.put("temperature", 0.2);
                        body.put("max_tokens", 512);

                        aibody = body;
                    } catch (JSONException e){
                        aibody = null;
                        Toast.makeText(getContext(), R.string.error_ai, Toast.LENGTH_LONG).show();
                    }
                }

                // Build request
                try {
                    Request requestHttp = new Request.Builder()
                            .url(endpoint + requestUri)
                            .post(RequestBody.create(
                                    aibody.toString(),
                                    MediaType.parse("application/json")
                            ))
                            .addHeader("Content-Type", "application/json")
                            .build();

                    client.newCall(requestHttp).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    String responseBody = response.body().string();

                                    JSONObject json = new JSONObject(responseBody);

                                    String generatedSql = "";

                                    if (!geminiKey.isEmpty()) {
                                        generatedSql =
                                                json.getJSONArray("candidates")
                                                        .getJSONObject(0)
                                                        .getJSONObject("content")
                                                        .getJSONArray("parts")
                                                        .getJSONObject(0)
                                                        .getString("text")
                                                        .replace("```sql", "")
                                                        .replace("```", "");

                                    } else {
                                        generatedSql =
                                                json.getJSONArray("choices")
                                                        .getJSONObject(0)
                                                        .getJSONObject("message")
                                                        .getString("content")
                                                        .replace("```sql", "")
                                                        .replace("```", "")
                                                        .replace("\n", " ")
                                                        .trim();
                                    }

                                    String finalSql = generatedSql;

                                    getActivity().runOnUiThread(() -> {
                                        queryText.setText(finalSql);
                                        runButton.performClick();
                                    });
                                } catch (JSONException e) {
                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(), R.string.error_ai_format, Toast.LENGTH_LONG).show();
                                    });
                                }
                            } else {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), R.string.error_ai_available, Toast.LENGTH_LONG).show();
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), R.string.error_ai_ask, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.error_ai_ask, Toast.LENGTH_LONG).show();
                }
            } else { // Generate the result in a PostgreSQL server using the LangToSQL REST API, overring its settings
                RestApiCall api = new RestApiClient("http://" + apiIp + ":" + apiPort.trim() + "/").getClient().create(RestApiCall.class);

                retrofit2.Call<ResponseBody> call = api.generateSQL(requestText.getText().toString());

                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String raw = response.body().string();
                                String[] parts = raw.split("Generated query:");
                                String json = parts[0];
                                queryText.setText(parts[1].trim());
                                JSONArray arr = new JSONArray(json);

                                runViewModel.setJson(arr); // Save the result in memory

                                MatrixCursor cursor = jsonToCursor(arr);

                                buildTable(cursor);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), R.string.error_api_format, Toast.LENGTH_LONG).show();
                            }
                        } else
                            Toast.makeText(getContext(), R.string.error_api_code + response.code(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), R.string.error_api_available, Toast.LENGTH_LONG).show();
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

                    runViewModel.setJson(cursorToJson(cursor));

                    buildTable(jsonToCursor(runViewModel.getJson()));
                    sqliteDb.close();
                } catch (SQLiteException e) {
                    Toast.makeText(getContext(), R.string.error_run_query + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    queryText.setEnabled(true);
                    runButton.setEnabled(true);
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.error_run + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else
                Toast.makeText(getContext(), R.string.warning_direct_run, Toast.LENGTH_SHORT).show();
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
                tv.setText(value.replace('\n', ' '));
                tv.setPadding(8, 8, 8, 8);
                tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.outline_cell));
                tv.setGravity(Gravity.CENTER);
                tv.setMaxLines(1);
                tv.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", value);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getContext(), R.string.text_clipboard, Toast.LENGTH_SHORT).show();
                });

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

    public static JSONArray cursorToJson(Cursor cursor) {

        JSONArray result = new JSONArray();

        if (cursor == null || !cursor.moveToFirst()) {
            return result;
        }

        // IMPORTANT: use column names directly (stable schema)
        String[] columns = cursor.getColumnNames();

        do {
            JSONObject obj = new JSONObject();

            for (String column : columns) {
                try {
                    int index = cursor.getColumnIndex(column);

                    if (cursor.isNull(index)) {
                        obj.put(column, JSONObject.NULL);
                        continue;
                    }

                    switch (cursor.getType(index)) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            obj.put(column, cursor.getLong(index));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            obj.put(column, cursor.getDouble(index));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            obj.put(column, cursor.getString(index));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            obj.put(column, null);
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                        default:
                            obj.put(column, JSONObject.NULL);
                            break;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            result.put(obj);
        } while (cursor.moveToNext());

        return result;
    }

    // Destroyer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}