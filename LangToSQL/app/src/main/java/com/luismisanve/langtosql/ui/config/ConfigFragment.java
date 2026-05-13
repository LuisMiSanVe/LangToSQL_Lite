package com.luismisanve.langtosql.ui.config;

import static android.app.Activity.RESULT_OK;
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
import androidx.lifecycle.ViewModelProvider;
import com.luismisanve.langtosql.*;
import com.luismisanve.langtosql.databinding.FragmentConfigBinding;
import java.io.InputStream;

public class ConfigFragment extends Fragment {
    // Variables
    private FragmentConfigBinding binding;
    public RadioButton useSQLite;
    public EditText fileText;
    private ImageButton fileButton;
    public RadioButton useApi;
    public EditText apiIpText;
    public EditText apiPortText;
    public RadioButton useGemini;
    public EditText geminiKeyText;
    public CheckBox rememberCheck;
    public RadioButton useLLM;
    public EditText llmIpText;
    public EditText llmPortText;
    public EditText llmModelText;
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        loadFile(result);
                    });

    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ConfigViewModel configViewModel =
                new ViewModelProvider(this).get(ConfigViewModel.class);

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
        rememberCheck = root.findViewById(R.id.rememberCheck);
        // LLM
        useLLM = root.findViewById(R.id.useLLM);
        llmIpText = root.findViewById(R.id.llmIpText);
        llmPortText = root.findViewById(R.id.llmPortText);
        llmModelText = root.findViewById(R.id.llmModelText);

        // Events
        useSQLite.setOnClickListener(v -> {
            fileText.setEnabled(true);
            fileButton.setEnabled(true);
            fileButton.setImageResource(R.drawable.file);
            apiIpText.setEnabled(false);
            apiPortText.setEnabled(false);
        });
        useApi.setOnClickListener(v -> {
            fileText.setEnabled(false);
            fileButton.setEnabled(false);
            fileButton.setImageResource(R.drawable.file_disabled);
            apiIpText.setEnabled(true);
            apiPortText.setEnabled(true);
        });
        useGemini.setOnClickListener(v -> {
            geminiKeyText.setEnabled(true);
            rememberCheck.setEnabled(true);
            llmIpText.setEnabled(false);
            llmPortText.setEnabled(false);
            llmModelText.setEnabled(false);
        });
        useLLM.setOnClickListener(v -> {
            geminiKeyText.setEnabled(false);
            rememberCheck.setEnabled(false);
            llmIpText.setEnabled(true);
            llmPortText.setEnabled(true);
            llmModelText.setEnabled(true);
        });
        fileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                    "application/octet-stream",
                    "application/x-sqlite3"
            });

            filePickerLauncher.launch(intent);
        });

        return root;
    }

    // Other methods
    private void loadFile(ActivityResult result){
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            Uri uri = result.getData().getData();

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
                        Toast.makeText(getContext(), "The database file doesn't have the correct SQLite format.", Toast.LENGTH_SHORT).show();

                    is.close();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error " + e.getClass() + ": The file couldn't be checked.", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(getContext(), "The selected file is not a valid SQLite database.", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "No database file has been selected.", Toast.LENGTH_SHORT).show();
    }

    // Destroyer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}