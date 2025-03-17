package com.example.peaky.ui.home;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.peaky.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecordFragment extends Fragment {

    private View bottomSheet, bottomSheetContent;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView arrowIcon;
    private FloatingActionButton recordStartButton, recordEndButton;

    public RecordFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        bottomSheet = view.findViewById(R.id.bottomSheet);
        bottomSheetContent = view.findViewById(R.id.bottomSheetContent);
        arrowIcon = view.findViewById(R.id.arrowIcon);

        recordStartButton = view.findViewById(R.id.record_start);
        recordEndButton = view.findViewById(R.id.record_end);

        recordStartButton.setOnClickListener(v -> {
            recordStartButton.setVisibility(View.VISIBLE);
            animateSecondFab();
        });

        // Bottomsheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setDraggable(true);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        bottomSheetContent.setVisibility(View.VISIBLE);
                        arrowIcon.setImageResource(R.drawable.bs_ic_line);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetContent.setVisibility(View.GONE);
                        arrowIcon.setImageResource(R.drawable.bs_ic_line);
                        break;
                }
            }

            private float lastSlideOffset = 0;

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > lastSlideOffset) {
                    arrowIcon.setImageResource(R.drawable.bs_ic_arrow_up);
                } else if (slideOffset < lastSlideOffset) {
                    arrowIcon.setImageResource(R.drawable.bs_ic_arrow_down);
                }

                lastSlideOffset = slideOffset;
            }
        });

        return view;
    }

    private void animateSecondFab() {
        // Esegui una semplice animazione per il secondo FAB
        ObjectAnimator translationY = ObjectAnimator.ofFloat(recordEndButton, "translationY", -150f);
        translationY.setDuration(300);
        translationY.start();

        // Puoi anche aggiungere altre animazioni, come fade in o scale
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(recordEndButton, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        fadeIn.start();
    }
}