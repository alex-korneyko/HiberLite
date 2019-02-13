package ua.in.korneiko.testHiberLite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import ua.in.korneiko.hiberlite.R;
import ua.in.korneiko.hiberlite.Table;
import ua.in.korneiko.hiberlite.TableFactory;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MyLog";
    private TableFactory tableFactory;
    private Table<Company> companyTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableFactory = new TableFactory(this);

        companyTable = tableFactory.createTable(Company.class);
    }

    public void onClickAdd(View view) {

        ArrayList<Floor> floors = new ArrayList<>();

        floors.add(new Floor("Ground floor", 1, 5));
        floors.add(new Floor("First floor", 10, 15));
        floors.add(new Floor("Second floor", 20, 25));

        ArrayList<String> anyStrings = new ArrayList<String>() {{
            add("First string");
            add("Second string");
            add("Third string");
        }};

        Company company = new Company("Alpha company", anyStrings, floors);

        long id = companyTable.add(company);

        Log.d(LOG_TAG, "Added with id: " + id);
    }

    public void onClickGetId1(View view) {

        Company company = companyTable.find(1);

        Log.d(LOG_TAG, company.toString());
    }

    public void onClickGetAll(View view) {

    }

    public void onClickDeleteAll(View view) {

    }

    public void onClickDelTable(View view) {

    }
}
