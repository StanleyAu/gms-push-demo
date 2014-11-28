package com.genesys.gms.mobile.push.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by stau on 11/27/2014.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity)getActivity()).inject(this);
    }
}
