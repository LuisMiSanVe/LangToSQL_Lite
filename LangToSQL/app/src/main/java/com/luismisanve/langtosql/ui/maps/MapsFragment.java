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
import com.luismisanve.langtosql.ui.config.ConfigViewModel;
import com.luismisanve.langtosql.ui.run.RunFragment;
import java.io.*;

public class MapsFragment extends Fragment {
    // Variables
    private FragmentMapsBinding binding;
    private LinearLayout mapsLayout;
    private FileManager fileManager;
    private MapManager mapManager;
    private String currentDb = "";

    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConfigViewModel configViewModel = new ViewModelProvider(requireActivity()).get(ConfigViewModel.class);
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Layout Objects
        TextView currentDbMapText = root.findViewById(R.id.currentDbMapText);
        ImageButton mapButton = root.findViewById(R.id.mapButton);
        mapsLayout = root.findViewById(R.id.mapsLayout);

        // Load config
        fileManager = new FileManager(getContext());
        mapManager = new MapManager();
        try {
            File db = new File(getContext().getFilesDir(), "dbsettings.cfg");
            if (db.exists()) {
                String[] dbConfig = fileManager.readFromFile("dbsettings.cfg").split(";");

                if (Boolean.parseBoolean(dbConfig[0])) {
                    if (!dbConfig[1].isEmpty()) {
                        String name = "";
                        Uri uri = Uri.parse(dbConfig[1]);
                        try {
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
                        } catch (Exception e) {
                            Toast.makeText(getContext(), R.string.error_load_io, Toast.LENGTH_SHORT).show();
                        }
                        mapButton.setEnabled(true);
                        mapButton.setImageResource(R.drawable.map);
                    }
                } else {
                    mapButton.setEnabled(false);
                    mapButton.setImageResource(R.drawable.map_disabled);
                }
            }

            buildList();
        }catch (NullPointerException e) {
            Toast.makeText(getContext(), R.string.error_data, Toast.LENGTH_SHORT).show();
        }

        // Events
        mapButton.setOnClickListener(v -> {

            String map = "";
            if (!currentDb.isEmpty()) {
                Toast.makeText(getContext(), R.string.text_mapping, Toast.LENGTH_SHORT).show();
                map = mapManager.mapDatabase(getContext());

                if (!map.isEmpty()) {
                    fileManager.writeToFile(currentDb + ".map", map);
                    Toast.makeText(getContext(), R.string.text_mapped, Toast.LENGTH_SHORT).show();
                    buildList();
                }
            } else
                Toast.makeText(getContext(), R.string.text_nodb, Toast.LENGTH_SHORT).show();
        });
        configViewModel.getSavedOutside().observe(getViewLifecycleOwner(), saved ->{
            if (saved) {
                configViewModel.setSavedOutside(false);
                getActivity().recreate();
            }
        });

        return root;
    }

    // Other methods
    public void buildList() {
        mapsLayout.removeAllViews();

        try {
            File mapsFolder = getContext().getFilesDir();

            if (mapsFolder.exists()) {
                if (mapsFolder.listFiles().length > 0) {
                    for (File map : mapsFolder.listFiles()) {
                        if (map.getName().contains(".map")) {
                            TextView tv = new TextView(getContext());

                            String mapName = map.getName().split("\\.")[0];

                            tv.setText(mapName);
                            tv.setPadding(8, 8, 8, 8);
                            if (mapName.contains(currentDb))
                                tv.setTextColor(0xFF5972F9);
                            tv.setGravity(Gravity.CENTER);
                            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.outline_box));
                            tv.setOnLongClickListener(v -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle(R.string.text_map_deletion)
                                        .setMessage(getString(R.string.text_map_delete) + " " + mapName + "?")
                                        .setPositiveButton(R.string.text_map_deletechoice, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                map.delete();
                                                buildList();
                                            }
                                        })
                                        .setNegativeButton(R.string.text_map_cancelchoice, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                                return false;
                            });
                            tv.setOnClickListener(view -> {
                                RunFragment.json = "";
                                RunFragment.selectedMap = tv.getText().toString();
                                Toast.makeText(getContext(), tv.getText().toString() + " " + getString(R.string.text_map_selected), Toast.LENGTH_SHORT).show();
                            });

                            mapsLayout.addView(tv);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            Toast.makeText(getContext(), R.string.error_data, Toast.LENGTH_SHORT).show();
            mapsLayout.removeAllViews();
        }
    }

    // Destroyer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}