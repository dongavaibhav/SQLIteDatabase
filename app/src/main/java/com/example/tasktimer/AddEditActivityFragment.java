package com.example.tasktimer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddEditActivityFragment extends Fragment {

    // declare the variable
    private static final String addEditActivity = "AddEditActivityFragment";
    public enum FragmentEditMode { EDIT, ADD }
    private FragmentEditMode mMode;
    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;
    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked {
        void onSaveClicked();
    }

    public AddEditActivityFragment() {
        Log.d(addEditActivity, "AddEditActivityFragment: constructor called");
    }

    public boolean canClose() {
        return false;
    }

    // Activities containing this fragment must implement it's callbacks.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
        if(!(activity instanceof OnSaveClicked)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                + " must implement AddEditActivityFragment.OnSaveClicked interface");
        }
        mSaveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSaveListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mNameTextView = (EditText) view.findViewById(R.id.addedit_name);
        mDescriptionTextView = (EditText) view.findViewById(R.id.addedit_description);
        mSortOrderTextView = (EditText) view.findViewById(R.id.addedit_sortorder);
        mSaveButton = (Button) view.findViewById(R.id.addedit_save);

        Bundle arguments = getArguments();

        final Task task;
        if (arguments != null) {
            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if (task != null) {
                mNameTextView.setText(task.getName());
                mDescriptionTextView.setText(task.getDescription());
                mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            } else {
                mMode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            mMode = FragmentEditMode.ADD;
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int so;
                if (mSortOrderTextView.length() > 0) {
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());
                } else {
                    so = 0;
                }
                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mMode) {
                    case EDIT:
                        if (!mNameTextView.getText().toString().equals(task.getName())) {
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                        }

                        if (!mDescriptionTextView.getText().toString().equals(task.getDescription())) {
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }

                        if (so != task.getSortOrder()) {
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                        }

                        if (values.size() != 0) {
                            contentResolver.update(TasksContract.buildTaskUri(task.getId()), values, null, null);
                        }

                        break;
                    case ADD:
                        if (mNameTextView.length() > 0) {
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(TasksContract.CONTENT_URI, values);
                        }
                        break;
                }
                if(mSaveListener != null) {
                    mSaveListener.onSaveClicked();
                }
            }
        });
        return view;
    }
}











