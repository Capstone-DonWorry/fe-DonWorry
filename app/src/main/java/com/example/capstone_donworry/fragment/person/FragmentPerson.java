package com.example.capstone_donworry.fragment.person;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone_donworry.databinding.FragmentPersonBinding;
import com.example.capstone_donworry.fragment.calendar.PopAddItem;
import com.example.capstone_donworry.fragment.calendar.ViewModelCalendar;

public class FragmentPerson extends Fragment {

    private FragmentPersonBinding binding;
    private ViewModelPerson viewModelPerson;
    private TextView userName, setGoal;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModelPerson = new ViewModelProvider(requireActivity()).get(ViewModelPerson.class);

        binding = FragmentPersonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 닉네임 설정
        userName = binding.PersonUserName;
        viewModelPerson.getUserName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String name) {
                userName.setText(name);
            }
        });

        // 목표 설정 클릭
        setGoal = binding.SetExpenseGoalBtn;
        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 텍스트 클릭시 팝업 창 띄우기
                SetExpenseGoal setExpenseGoal = new SetExpenseGoal();
                // FragmentPerson을 타겟으로 설정
                setExpenseGoal.setTargetFragment(FragmentPerson.this, 0);
                setExpenseGoal.show(getParentFragmentManager(), "목표금액변경");
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}