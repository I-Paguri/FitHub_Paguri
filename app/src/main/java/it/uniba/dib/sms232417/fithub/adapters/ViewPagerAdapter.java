package it.uniba.dib.sms232417.fithub.adapters;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.uniba.dib.sms232417.fithub.coach.fragments.MeasurementsFragment;
import it.uniba.dib.sms232417.fithub.coach.fragments.TreatmentFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private Bundle bundle;

    public ViewPagerAdapter(@NonNull Fragment fragment, Bundle bundle) {
        super(fragment);
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                TreatmentFragment treatmentFragment = new TreatmentFragment();
                treatmentFragment.setArguments(bundle);
                Log.d("SonoQui", bundle.toString());
                return treatmentFragment;
            case 1:
                return new MeasurementsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}