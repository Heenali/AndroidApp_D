// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package trukkeruae.trukkertech.com.trukkeruae.jobshedular;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job.Builder;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import trukkeruae.trukkertech.com.trukkeruae.R;

public class JobFormActivity extends AppCompatActivity {

    private JobForm form = new JobForm();
    private FirebaseJobDispatcher jobDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_form);

        jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
        final ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_job_form);
        binding.setVariable(trukkeruae.trukkertech.com.trukkeruae.BR.form, form);



        AppCompatButton scheduleButton = (AppCompatButton) findViewById(R.id.schedule_button);
        assert scheduleButton != null;

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calljobshedular();
            }
        });



    }


    public  void calljobshedular()
    {
        Log.e("form.tag.get()",form.tag.get()+"");
        Log.e("form.recurring.get()",form.recurring.get()+"");
        Log.e("form.persistent.get()",form.persistent.get()+"");



        final Builder builder = jobDispatcher.newJobBuilder()
            .setTag(form.tag.get())

            .setRecurring(true)
            .setLifetime(true ? Lifetime.FOREVER : Lifetime.UNTIL_NEXT_BOOT)
            .setService(DemoJobService.class)
            .setTrigger(Trigger.executionWindow(form.getWinStartSeconds(), form.getWinEndSeconds()))
            .setReplaceCurrent(form.replaceCurrent.get())
            .setRetryStrategy(jobDispatcher.newRetryStrategy(
                form.retryStrategy.get(),
                form.getInitialBackoffSeconds(),
                form.getMaximumBackoffSeconds()));


        if (form.constrainDeviceCharging.get())
        {
            builder.addConstraint(Constraint.DEVICE_CHARGING);
        }
        if (form.constrainOnAnyNetwork.get()) {
            builder.addConstraint(Constraint.ON_ANY_NETWORK);
        }
        if (form.constrainOnUnmeteredNetwork.get()) {
            builder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        }

        Log.i("FJD.JobForm", "scheduling new job");
        jobDispatcher.mustSchedule(builder.build());

        JobFormActivity.this.finish();
    }
}
