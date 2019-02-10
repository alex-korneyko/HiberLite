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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MyLog";
    private TableFactory tableFactory;
    private DataBase dataBase;

    private Table<Branch> branchTable;

    private Post worker;
    private Post leader;
    private Post teamLeader;
    private Post ceo;

    private LegalAddress legalAddress;

    private Employee krychun;
    private Employee aKorneiko;
    private User krychunUser;
    private User korneikoUser;

    //TODO Need to implements! Check for uniqueness for fields with @SearchKey annotation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase = new DataBase(this, "testDB");
        tableFactory = new TableFactory(dataBase.getDatabase());

        worker = new Post("Worker", 1.0);
        leader = new Post("Leader", 1.1);
        teamLeader = new Post("Team Leader", 1.3);
        ceo = new Post("CEO", 1.6);

        krychunUser = new User("krychun", "111");
        krychun = new Employee("Roman", "Krychun", 10000, leader, krychunUser);

        korneikoUser = new User("korneiko", "123");
        aKorneiko = new Employee("Andrey", "Korneiko", 40000, teamLeader, korneikoUser);

        legalAddress = new LegalAddress(66400, "Poland", "Gorzow", "Pilsudskiego 9");

        Table<Post> postTable = tableFactory.createTable(Post.class);
        Table<User> userTable = tableFactory.createTable(User.class);
        Table<Employee> employeeTable = tableFactory.createTable(Employee.class);
        Table<LegalAddress> legalAddressTable = tableFactory.createTable(LegalAddress.class);
        branchTable = tableFactory.createTable(Branch.class);

        if (postTable.find(worker).isEmpty()) {
            postTable.add(worker);
            postTable.add(leader);
            postTable.add(teamLeader);
            postTable.add(ceo);

            userTable.add(krychunUser);
            userTable.add(korneikoUser);

            employeeTable.add(krychun);
            employeeTable.add(aKorneiko);

            legalAddressTable.add(legalAddress);
        }
    }

    public void onClickAdd(View view) {
        List<Employee> employees = new ArrayList<Employee>() {{
            add(krychun);
            add(aKorneiko);
        }};

        Branch mainBranch = new Branch("Main Branch", legalAddress, employees);

        long branchId = branchTable.add(mainBranch);

        Log.d(LOG_TAG, "Id: " + branchId);

    }

    public void onClickGetId1(View view) {


    }

    public void onClickGetAll(View view) {

    }

    public void onClickDeleteAll(View view) {

    }

    public void onClickDelTable(View view) {

    }
}
