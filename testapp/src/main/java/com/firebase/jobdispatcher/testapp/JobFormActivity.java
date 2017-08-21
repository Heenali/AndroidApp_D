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

package com.firebase.jobdispatcher.testapp;

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

public class JobFormActivity extends AppCompatActivity {

    private JobForm form = new JobForm();
    private FirebaseJobDispatcher jobDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_form);
        jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
        final ViewDataBinding binding = DataBindingUtil
            .setContentView(this, R.layout.activity_job_form);
        binding.setVariable(com.firebase.jobdispatcher.testapp.BR.form, form);



        AppCompatButton scheduleButton = (AppCompatButton) findViewById(R.id.schedule_button);
        assert scheduleButton != null;

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainss();
            }
        });



    }


    public  void mainss()
    {
        Log.e("form.tag.get()",form.tag.get()+"");
        Log.e("form.recurring.get()",form.recurring.get()+"");
        Log.e("form.persistent.get()",form.persistent.get()+"");

        Log.e("getWinStartSeconds()",form.getWinStartSeconds()+"");
        Log.e("form.getWinEndSeconds()",form.getWinEndSeconds()+"");

        Log.e("replaceCurrent.get()",form.replaceCurrent.get()+"");
        Log.e("retryStrategy.get()",form.retryStrategy.get()+"");
        Log.e("InitialBackoffSeconds()",form.getInitialBackoffSeconds()+"");
        Log.e("MaximumBackoffSeconds()",form.getMaximumBackoffSeconds()+"");
        Log.e("DeviceCharging.get()",form.constrainDeviceCharging.get()+"");
        Log.e("AnyNetwork.get()",form.constrainOnAnyNetwork.get()+"");
        Log.e("edNetwork.get()",form.constrainOnUnmeteredNetwork.get()+"");

        final Builder builder = jobDispatcher.newJobBuilder()
            .setTag(form.tag.get())

            .setRecurring(true)
            .setLifetime(true ? Lifetime.FOREVER : Lifetime.UNTIL_NEXT_BOOT)
            .setService(DemoJobService.class)
            .setTrigger(Trigger.executionWindow(30,60))
            .setReplaceCurrent(false)
            .setRetryStrategy(jobDispatcher.newRetryStrategy(1,30,3600));


        if (false)
        {
            builder.addConstraint(Constraint.DEVICE_CHARGING);
        }
        if (true) {
            builder.addConstraint(Constraint.ON_ANY_NETWORK);
        }
        if (false) {
            builder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        }

        Log.i("FJD.JobForm", "scheduling new job");
        jobDispatcher.mustSchedule(builder.build());
        mainss1();
        JobFormActivity.this.finish();

    }
    public  void mainss1()
    {
        Log.e("form.tag.get()",form.tag.get()+"");
        Log.e("form.recurring.get()",form.recurring.get()+"");
        Log.e("form.persistent.get()",form.persistent.get()+"");

        Log.e("getWinStartSeconds()",form.getWinStartSeconds()+"");
        Log.e("form.getWinEndSeconds()",form.getWinEndSeconds()+"");

        Log.e("replaceCurrent.get()",form.replaceCurrent.get()+"");
        Log.e("retryStrategy.get()",form.retryStrategy.get()+"");
        Log.e("InitialBackoffSeconds()",form.getInitialBackoffSeconds()+"");
        Log.e("MaximumBackoffSeconds()",form.getMaximumBackoffSeconds()+"");
        Log.e("DeviceCharging.get()",form.constrainDeviceCharging.get()+"");
        Log.e("AnyNetwork.get()",form.constrainOnAnyNetwork.get()+"");
        Log.e("edNetwork.get()",form.constrainOnUnmeteredNetwork.get()+"");

        final Builder builder = jobDispatcher.newJobBuilder()
            .setTag("Authoallocation")

            .setRecurring(true)
            .setLifetime(true ? Lifetime.FOREVER : Lifetime.UNTIL_NEXT_BOOT)
            .setService(DemoJobService_Autoallocation.class)
            .setTrigger(Trigger.executionWindow(30,60))
            .setReplaceCurrent(false)
            .setRetryStrategy(jobDispatcher.newRetryStrategy(1,30,3600));


        if (false)
        {
            builder.addConstraint(Constraint.DEVICE_CHARGING);
        }
        if (true) {
            builder.addConstraint(Constraint.ON_ANY_NETWORK);
        }
        if (false) {
            builder.addConstraint(Constraint.ON_UNMETERED_NETWORK);
        }

        Log.i("FJD.JobForm", "scheduling new job");
        jobDispatcher.mustSchedule(builder.build());

        JobFormActivity.this.finish();
    }
}
