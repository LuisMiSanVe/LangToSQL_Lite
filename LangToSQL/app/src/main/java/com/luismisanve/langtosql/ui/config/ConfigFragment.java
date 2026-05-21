package com.luismisanve.langtosql.ui.config;

import static android.app.Activity.RESULT_OK;
import static android.view.View.*;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.net.*;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.*;
import androidx.activity.result.*;
import androidx.activity.result.contract.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.luismisanve.langtosql.*;
import com.luismisanve.langtosql.databinding.FragmentConfigBinding;
import com.luismisanve.langtosql.ui.run.RunFragment;
import java.io.*;
import java.util.regex.*;

public class ConfigFragment extends Fragment {
    // Variables
    private FragmentConfigBinding binding;
    private RadioButton useSQLite;
    private EditText fileText;
    private ImageButton fileButton;
    private RadioButton useApi;
    private EditText apiIpText;
    private EditText apiPortText;
    private RadioButton useGemini;
    private EditText geminiKeyText;
    private CheckBox showCheck;
    private RadioButton useLLM;
    private EditText llmIpText;
    private EditText llmPortText;
    private EditText llmModelText;
    private ImageButton saveButton;
    private CheckBox showQueryCheck;
    private FileManager fileManager;
    private String oldData;
    private final String ipFormat = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
    private final String portFormat = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]?\\d{1,4})$";
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        loadFile(result);
                    });

    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfigBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Layout objects

        // DB Settings
        // SQLite
        useSQLite = root.findViewById(R.id.useSQLite);
        fileText = root.findViewById(R.id.fileText);
        fileButton = root.findViewById(R.id.fileButton);
        // API
        useApi = root.findViewById(R.id.useApi);
        apiIpText = root.findViewById(R.id.apiIpText);
        apiPortText = root.findViewById(R.id.apiPortText);

        // AI Settings
        // Gemini
        useGemini = root.findViewById(R.id.useGemini);
        geminiKeyText = root.findViewById(R.id.geminiKeyText);
        showCheck = root.findViewById(R.id.showCheck);
        // LLM
        useLLM = root.findViewById(R.id.useLLM);
        llmIpText = root.findViewById(R.id.llmIpText);
        llmPortText = root.findViewById(R.id.llmPortText);
        llmModelText = root.findViewById(R.id.llmModelText);

        showQueryCheck = root.findViewById(R.id.showQueryCheck);
        saveButton = root.findViewById(R.id.saveButton);

        // Apply saved config after root is loaded
        root.post(() -> {
            fileManager = new FileManager(getContext());
            File db = new File(getContext().getFilesDir(), "dbsettings.cfg");
            File ai = new File(getContext().getFilesDir(), "aisettings.cfg");
            if (db.exists()) {
                String[] dbConfig = fileManager.readFromFile("dbsettings.cfg").split(";");
                if (Boolean.parseBoolean(dbConfig[0])) {
                    useSQLite.performClick();
                    useSQLite.setChecked(Boolean.parseBoolean(dbConfig[0]));
                } else {
                    useApi.performClick();
                    useApi.setChecked(!Boolean.parseBoolean(dbConfig[0]));
                }
                if (dbConfig.length > 1)
                    fileText.setText(dbConfig[1]);
                if (dbConfig.length > 2)
                    apiIpText.setText(dbConfig[2]);
                if (dbConfig.length > 3)
                    apiPortText.setText(dbConfig[3]);
            }
            if (ai.exists()) {
                String[] aiConfig = fileManager.readFromFile("aisettings.cfg").split(";");
                if (Boolean.parseBoolean(aiConfig[0])) {
                    useGemini.performClick();
                    useGemini.setChecked(Boolean.parseBoolean(aiConfig[0]));
                } else {
                    useLLM.performClick();
                    useLLM.setChecked(!Boolean.parseBoolean(aiConfig[0]));
                }
                if (aiConfig.length > 1)
                    geminiKeyText.setText(aiConfig[1]);
                if (aiConfig.length > 2)
                    llmIpText.setText(aiConfig[2]);
                if (aiConfig.length > 3)
                    llmPortText.setText(aiConfig[3]);
                if (aiConfig.length > 4)
                    llmModelText.setText(aiConfig[4]);
            }
        });

        // Events
        useSQLite.setOnClickListener(v -> {
            fileText.setEnabled(true);
            fileButton.setEnabled(true);
            fileButton.setImageResource(R.drawable.file);
            apiIpText.setEnabled(false);
            apiPortText.setEnabled(false);

            useGemini.setEnabled(true);
            geminiKeyText.setEnabled(true);
            showCheck.setEnabled(true);
            useLLM.setEnabled(true);
            llmIpText.setEnabled(true);
            llmPortText.setEnabled(true);
            llmModelText.setEnabled(true);
        });
        useApi.setOnClickListener(v -> {
            fileText.setEnabled(false);
            fileButton.setEnabled(false);
            fileButton.setImageResource(R.drawable.file_disabled);
            apiIpText.setEnabled(true);
            apiPortText.setEnabled(true);

            useGemini.setEnabled(false);
            geminiKeyText.setEnabled(false);
            showCheck.setEnabled(false);
            useLLM.setEnabled(false);
            llmIpText.setEnabled(false);
            llmPortText.setEnabled(false);
            llmModelText.setEnabled(false);

            Toast.makeText(getContext(), R.string.warning_api_override, Toast.LENGTH_LONG).show();
        });
        useGemini.setOnClickListener(v -> {
            geminiKeyText.setEnabled(true);
            showCheck.setEnabled(true);
            llmIpText.setEnabled(false);
            llmPortText.setEnabled(false);
            llmModelText.setEnabled(false);
        });
        useLLM.setOnClickListener(v -> {
            geminiKeyText.setEnabled(false);
            showCheck.setEnabled(false);
            llmIpText.setEnabled(true);
            llmPortText.setEnabled(true);
            llmModelText.setEnabled(true);
        });
        fileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                    "application/octet-stream",
                    "application/x-sqlite3"
            });

            filePickerLauncher.launch(intent);
        });
        saveButton.setOnClickListener(v -> {
            if (useSQLite.isChecked() && (fileText.getText().toString().isEmpty()) ||
                useApi.isChecked() && (apiIpText.getText().toString().isEmpty() || apiPortText.getText().toString().isEmpty()) ||
                useGemini.isChecked() && (geminiKeyText.getText().toString().isEmpty()) ||
                useLLM.isChecked() && (llmIpText.getText().toString().isEmpty() || llmPortText.getText().toString().isEmpty() || llmModelText.getText().toString().isEmpty())) {
                // If any checked settings has its fields empty, it doesn't save and require the user to fill those fields
                if (getContext() != null)
                    Toast.makeText(requireContext().getApplicationContext(), R.string.warning_config_requirement, Toast.LENGTH_LONG).show();
            } else {
                StringBuilder dbSettingsFormat = buildDbSettings();
                StringBuilder aiSettingsFormat = buildAiSettings();

                fileManager.writeToFile("dbsettings.cfg", dbSettingsFormat.toString());
                fileManager.writeToFile("aisettings.cfg", aiSettingsFormat.toString());

                if (getContext() != null)
                    Toast.makeText(getContext(), R.string.text_saved, Toast.LENGTH_SHORT).show();
            }
        });
        showQueryCheck.setOnClickListener(v -> {
            if (showQueryCheck.isChecked())
                RunFragment.showQuery = VISIBLE;
            else
                RunFragment.showQuery = GONE;
        });
        fileText.setOnFocusChangeListener((v, focused) -> {
            checkFormat(focused, fileText, R.string.error_file_format, "^content://.*");
        });
        apiIpText.setOnFocusChangeListener((v, focused) -> {
            checkFormat(focused, apiIpText, R.string.error_ip_format, ipFormat);
        });
        apiPortText.setOnFocusChangeListener((v, focused) -> {
            checkFormat(focused, apiPortText, R.string.error_port_format, portFormat);
        });
        geminiKeyText.setOnFocusChangeListener((v, focused) -> {
            checkFormat(focused, geminiKeyText, R.string.error_gemini_format, "^AIza[0-9A-Za-z_-]{35}$");
        });
        llmIpText.setOnFocusChangeListener((v, focused) -> {
            checkFormat(focused, llmIpText, R.string.error_ip_format, ipFormat);
        });
        llmPortText.setOnFocusChangeListener((v, focused) -> {
            checkFormat(focused, llmPortText, R.string.error_port_format, portFormat);
        });

        return root;
    }

    // Other methods
    private StringBuilder buildDbSettings(){
        return new StringBuilder().append(useSQLite.isChecked()).append(";")
                                  .append(fileText.getText()).append(";")
                                  .append(apiIpText.getText()).append(";")
                                  .append(apiPortText.getText());
    }

    private StringBuilder buildAiSettings(){
        return new StringBuilder().append(useGemini.isChecked()).append(";")
                                    .append(geminiKeyText.getText()).append(";")
                                    .append(llmIpText.getText()).append(";")
                                    .append(llmPortText.getText()).append(";")
                                    .append(llmModelText.getText());
    }

    private void checkFormat(boolean focusState, EditText editText, int errorMessageResource, String checkPattern){
        if(focusState)
            oldData = editText.getText().toString();
        else {
            if (!Pattern.compile(checkPattern).matcher(editText.getText().toString()).matches()) {
                editText.setText(oldData);
                Toast.makeText(getContext(), errorMessageResource, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void loadFile(ActivityResult result){
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            Uri uri = result.getData().getData();
            // Save permissions
            final int takeFlags = result.getData().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);

            String name = null;

            Cursor cursor = getContext().getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {

                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                name = cursor.getString(index);

                cursor.close();
            }

            if (name != null &&
                    (name.endsWith(".db") ||
                            name.endsWith(".sqlite") ||
                            name.endsWith(".sqlite3") ||
                            name.endsWith(".db3"))) {

                try {
                    InputStream is = getContext().getContentResolver().openInputStream(uri);

                    byte[] header = new byte[16];

                    is.read(header);

                    String headerText = new String(header);

                    if (headerText.startsWith("SQLite format 3")) {

                        String fileUri = uri.toString();

                        fileText.setText(fileUri);
                        fileText.post(() -> {
                            fileText.setSelection(fileText.getText().length());
                        });
                    } else
                        Toast.makeText(getContext(), R.string.error_sqlite_format, Toast.LENGTH_SHORT).show();

                    is.close();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error " + e.getClass() + R.string.error_file_check, Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(getContext(), R.string.error_sqlite_novalid, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), R.string.text_nodb, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        // Compare current settings with last saved to check for changes
        if (!buildDbSettings().toString().equals(fileManager.readFromFile("dbsettings.cfg")) ||
            !buildAiSettings().toString().equals(fileManager.readFromFile("aisettings.cfg"))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.text_unsaved)
                    .setMessage(R.string.warning_unsaved)
                    .setPositiveButton(R.string.text_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveButton.performClick();
                        }
                    })
                    .setNegativeButton(R.string.text_discard, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // Destroyer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}