package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import bg.sofia.uni.fmi.mjt.splitwise.model.Expense;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseJsonProcessor extends AbstractJsonProcessor<Map<String, List<Expense>>> {
    private static final String EXPENSE_FILE_PATH = "resources/expense.json";
    private static final Type EXPENSE_TYPE = new TypeToken<Map<String, List<Expense>>>() {
    }.getType();

    public ExpenseJsonProcessor() {
        super(EXPENSE_FILE_PATH, EXPENSE_TYPE);
    }

    @Override
    protected Map<String, List<Expense>> createDefaultData() {
        return new HashMap<>();
    }
}
