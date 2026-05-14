package com.luismisanve.langtosql.ui.run;

import android.os.Bundle;
import android.text.Layout;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.luismisanve.langtosql.*;
import com.luismisanve.langtosql.databinding.FragmentRunBinding;
import static android.view.View.*;

public class RunFragment extends Fragment {
    // Variables
    private FragmentRunBinding binding;
    private ImageButton sendButton;
    private LinearLayout queryLayout;
    public static int showQuery = GONE;

    // Initializer
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RunViewModel runViewModel =
                new ViewModelProvider(this).get(RunViewModel.class);

        binding = FragmentRunBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Layout Objects
        sendButton = root.findViewById(R.id.sendButton);
        queryLayout = root.findViewById(R.id.queryLayout);

        queryLayout.setVisibility(showQuery);

        // Events
        sendButton.setOnClickListener(v -> {
            // Send
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