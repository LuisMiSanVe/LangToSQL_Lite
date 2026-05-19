package com.luismisanve.langtosql.ui.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.luismisanve.langtosql.*;
import com.luismisanve.langtosql.databinding.FragmentMapsBinding;
import java.io.*;

public class MapsFragment extends Fragment {
    // Variables
    private FragmentMapsBinding binding;
    private TextView currentDbMapText;
    private ImageButton mapButton;
    private LinearLayout mapsLayout;
    private FileManager fileManager;
    private MapManager mapManager;
    public static String currentDb = "";


    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapsViewModel mapsViewModel =
                new ViewModelProvider(this).get(MapsViewModel.class);

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Layout Objects
        currentDbMapText = root.findViewById(R.id.currentDbMapText);
        mapButton = root.findViewById(R.id.mapButton);
        mapsLayout = root.findViewById(R.id.mapsLayout);

        // Load config
        fileManager = new FileManager(getContext());
        mapManager = new MapManager();
        File db = new File(getContext().getFilesDir(), "dbsettings.cfg");
        if (db.exists()) {
            String[] dbConfig = fileManager.readFromFile("dbsettings.cfg").split(";");

            if (!dbConfig[1].isEmpty()) {
                String name = "";
                Uri uri = Uri.parse(dbConfig[1]);

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

                currentDb = (name.contains(".")) ? name.split("\\.")[0] : name;

                currentDbMapText.setText(currentDb);
            }
        }
        buildList();

        // Events
        mapButton.setOnClickListener(v -> {
            fileManager.writeToFile(currentDb + ".map", mapManager.mapDatabase());

            Toast.makeText(getContext(), "The database has been mapped.", Toast.LENGTH_SHORT).show();

            buildList();
        });

        return root;
    }

    // Other methods
    public void buildList() {
        mapsLayout.removeAllViews();

        File mapsFolder = getContext().getFilesDir();

        if (mapsFolder.exists()) {
            if (mapsFolder.listFiles().length > 0) {
                for (File map : mapsFolder.listFiles()) {
                    if (map.getName().contains(".map")) {
                        TextView tv = new TextView(getContext());

                        String mapName = map.getName().split("\\.")[0];

                        tv.setText(mapName);
                        tv.setPadding(8, 8, 8, 8);
                        tv.setBackgroundColor(0xFFEFEFEF);
                        tv.setGravity(Gravity.CENTER);
                        tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.outline_box));
                        tv.setOnLongClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Delete map")
                            .setMessage("Do you want to delete the map of " + mapName + "?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        map.delete();
                                        buildList();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            return false;
                        });

                        mapsLayout.addView(tv);
                    }
                }
            }
        }
    }

    // Destroyer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}