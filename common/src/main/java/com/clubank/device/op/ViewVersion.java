package com.clubank.device.op;

import android.content.Context;

import com.clubank.domain.C;
import com.clubank.domain.Result;
import com.clubank.util.MyRow;

public class ViewVersion extends OPBase {
    public ViewVersion(Context context) {
        super(context);
    }

    @Override
    public Result execute(Object... args) throws Exception {
        MyRow row = new MyRow();
//        row.put("curNO", args[0]);
        Result result = operateHttp(C.HTTP_POST, "ViewVersion", row);
        return result;
    }
}
