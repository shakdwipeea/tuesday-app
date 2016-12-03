package com.shakdwipeea.tuesday.people;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.databinding.ActivityPeopleListBinding;
import com.shakdwipeea.tuesday.home.home.ContactAdapter;

public class PeopleListActivity extends AppCompatActivity implements PeopleListContract.View {

    ActivityPeopleListBinding binding;
    ContactAdapter adapter;

    PeopleListPresenter presenter;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_people_list);
        setSupportActionBar(binding.toolbar);

        presenter = new PeopleListPresenter(this);

        adapter = new ContactAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        binding.content.peopleList.setLayoutManager(layoutManager);
        binding.content.peopleList.setAdapter(adapter);
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void displayProgressBar(boolean enable) {

    }

    @Override
    public void addPerson(User person) {
        adapter.addUser(person);
    }

    @Override
    public void clearPeople() {
        adapter.clearUsers();
    }
}
