package com.wiley.fordummies.androidsdk.tictactoe;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.wiley.fordummies.androidsdk.tictactoe.AccountDbSchema.AccountsTable;

/**
 * Created by adamcchampion on 2017/08/04.
 */

public class AccountCursorWrapper extends CursorWrapper {
    public AccountCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Account getAccount() {
        String name = getString(getColumnIndex(AccountsTable.Cols.NAME));
        String password = getString(getColumnIndex(AccountsTable.Cols.PASSWORD));

        Account account = new Account(name, password);

        return account;
    }
}
