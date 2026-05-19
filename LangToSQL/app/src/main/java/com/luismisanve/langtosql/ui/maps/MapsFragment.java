package com.luismisanve.langtosql.ui.maps;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.luismisanve.langtosql.*;
import com.luismisanve.langtosql.databinding.FragmentMapsBinding;

public class MapsFragment extends Fragment {
    // Variables
    private FragmentMapsBinding binding;
    private ImageButton mapButton;

    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapsViewModel mapsViewModel =
                new ViewModelProvider(this).get(MapsViewModel.class);

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Layout Objects
        mapButton = root.findViewById(R.id.mapButton);

        // Events
        mapButton.setOnClickListener(v -> {
            // Map
        });

        return root;
    }

    // Other methods

    // Destroyer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}