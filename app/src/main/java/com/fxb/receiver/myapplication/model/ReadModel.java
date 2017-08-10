package com.fxb.receiver.myapplication.model;

import android.os.RemoteException;
import android.util.Log;

import com.fxb.receiver.myapplication.util.DevBeep;
import com.olc.uhf.tech.IUhfCallback;

import java.util.Iterator;
import java.util.List;

/**
 * zf 2033152950
 * Created by Administrator on 2017/8/4 0004.
 */

public class ReadModel extends AbstractUHFModel {

    private String strEpc;

    @Override
    public String inventory() {
        IUhfCallback callback = new IUhfCallback.Stub() {
            @Override
            public void doInventory(List<String> str) throws RemoteException {
                for (int i = 0; i < str.size(); i++) {
                    String strepc = (String) str.get(i);
                    Log.d("wyt", "RSSI=" + strepc.substring(0, 2));
                    Log.d("wyt", "PC=" + strepc.substring(2, 6));
                    Log.d("wyt", "EPC=" + strepc.substring(2, 6) + strepc.substring(6));
                    strEpc = strepc.substring(2, 6) + strepc.substring(6);
                    byteEpc = stringToBytes(strEpc);
                    DevBeep.PlayOK();
                }
            }

            @Override
            public void doTIDAndEPC(List<String> str) throws RemoteException {
                for (Iterator it2 = str.iterator(); it2.hasNext(); ) {
                    String strepc = (String) it2.next();
                    int nlen = Integer.valueOf(strepc.substring(0, 2), 16);
                }
            }
        };
        uhf_6c.inventory(callback);
        return strEpc;
    }

    @Override
    public void ReadDate(IResponse iResponse) {
        byte sem = 0;
        byte[] dataout = new byte[nDL * 2];
        if (byteEpc == null) {
            return;
        }
        if (uhf_6c == null) {
            return;
        }
        int result = uhf_6c.read(mimaStr, byteEpc.length, byteEpc, MemBank,
                nSA, nDL, dataout, 0, nDL);
        iResponse.Error(result, getErrorDescription(result));
        if (result == 0) {
            iResponse.Response(dataout);
        }
        DevBeep.PlayOK();

    }

    @Override
    public void WriteDate(byte[] date, IResponse iResponse) {
        if (byteEpc == null) {
            return;
        }
        int result = uhf_6c.write(mimaStr, byteEpc.length, byteEpc, MemBank,
                nSA, nDL, date);
        iResponse.Error(result, getErrorDescription(result));
        if (result == 0) {
            iResponse.Response(null);
        }
    }
}
