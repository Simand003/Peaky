package com.example.peaky.ui.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.peaky.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class RecordFragment extends Fragment {

    private View bottomSheet, bottomSheetContent;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView arrowIcon;

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

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setDraggable(true);

        bottomSheetContent.setVisibility(View.GONE);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Gestisci i vari stati del BottomSheet
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        // Quando il BottomSheet è completamente espanso, mostra il contenuto e cambia l'icona
                        bottomSheetContent.setVisibility(View.VISIBLE);  // Mostra il contenuto
                        //arrowIcon.setImageResource(R.drawable.ic_line);  // Linea dritta
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // Quando il BottomSheet è contratto, mostra solo la linea e nascondi il contenuto
                        bottomSheetContent.setVisibility(View.GONE);  // Nascondi il contenuto
                        //arrowIcon.setImageResource(R.drawable.ic_line);  // Linea dritta
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        // Quando il BottomSheet è in movimento, mostra il contenuto e la freccia giusta
                        bottomSheetContent.setVisibility(View.VISIBLE);  // Mostra il contenuto
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            //arrowIcon.setImageResource(R.drawable.ic_arrow_up);  // Freccia verso l'alto
                        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            //arrowIcon.setImageResource(R.drawable.ic_arrow_down);  // Freccia verso il basso
                        }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Monitora il movimento del BottomSheet per cambiare la freccia in tempo reale
                if (slideOffset > 0) {
                    // Movimento verso l'alto
                    //arrowIcon.setImageResource(R.drawable.ic_arrow_up);  // Freccia verso l'alto
                } else if (slideOffset < 0) {
                    // Movimento verso il basso
                    //arrowIcon.setImageResource(R.drawable.ic_arrow_down);  // Freccia verso il basso
                } else {
                    // Quando il BottomSheet è fermo (al massimo o minimo)
                    //arrowIcon.setImageResource(R.drawable.ic_line);  // Linea dritta
                }
            }
        });

        return view;
    }
}
