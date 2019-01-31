package ua.in.korneiko.testHiberLite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import ua.in.korneiko.hiberlite.DataBase;
import ua.in.korneiko.hiberlite.R;
import ua.in.korneiko.hiberlite.Table;
import ua.in.korneiko.hiberlite.TableFactory;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MyLog";
    private TableFactory tableFactory;
    private DataBase dataBase;
    private Table<Company> companyTable;
    private Table<Post> postTable;
    private Table<LegalAddress> legalAddressTable;

    private Post worker;
    private Post leader;
    private Post teamLeader;
    private Post ceo;

    private LegalAddress legalAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        worker = new Post("Worker", 1.0);
        leader = new Post("Leader", 1.1);
        teamLeader = new Post("Team Leader", 1.3);
        ceo = new Post("CEO", 1.6);

        legalAddress = new LegalAddress(66400, "Poland", "Gorzow WLKP", "Pilsudskiego 9, 511b");

        dataBase = new DataBase(this, "testDB");
        tableFactory = new TableFactory(dataBase.getDatabase());

        companyTable = tableFactory.createTable(Company.class);
        postTable = tableFactory.createTable(Post.class);
        legalAddressTable = tableFactory.createTable(LegalAddress.class);

        postTable.add(worker);
        postTable.add(leader);
        postTable.add(teamLeader);
        postTable.add(ceo);
        legalAddressTable.add(legalAddress);
    }

    public void onClickAdd(View view) {

        final Employee dvoretskaya = new Employee("Oksans", "Dvoretskaya", 22000, worker);
        final Employee tanya = new Employee("Tatyana", "Chorna", 31000, worker);
        final Employee teamLeaderLondonBranch = new Employee("Mariana", "Benca", 35000, teamLeader);
        final Employee teamLeaderUSABranch = new Employee("Anastasia", "Nic", 36000, teamLeader);

        final Branch londonBranch = new Branch("Europe-branch", "London, Holms street, 9", new ArrayList<Employee>() {{
            add(new Employee("Alex", "Rootoff", 50000, ceo));
            add(new Employee("Roman", "Krychun", 10000, leader));
        }}) {{
            setHeadmaster(teamLeaderLondonBranch);
            setOtherListData(new ArrayList<String>() {{
                add("first");
                add("second");
                add("third");
            }});
        }};

        final Branch usaBranch = new Branch("USA-branch", "NY, Wall Street, 22", new ArrayList<Employee>() {{
            add(new Employee("Andrey", "Korneiko", 40000, teamLeader));
            add(new Employee("Roman", "Miroshnik", 12000, worker));
        }}) {{
            setHeadmaster(teamLeaderUSABranch);
            setOtherListData(new ArrayList<String>() {{
                add("fourth");
                add("fifth");
                add("sixth");
            }});
        }};


        Company company = new Company("Alfa-Company", legalAddress, new ArrayList<Branch>() {{
            add(londonBranch);
            add(usaBranch);
        }});

        company.setMainOfficeEmployees(new ArrayList<Employee>() {{
            add(dvoretskaya);
            add(tanya);
        }});

        long id = companyTable.add(company);

        Log.d(LOG_TAG, "ID: " + id);
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
