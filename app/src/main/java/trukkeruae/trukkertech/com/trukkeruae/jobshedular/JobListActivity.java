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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import trukkeruae.trukkertech.com.trukkeruae.R;

/**
 * An activity representing a list of Jobs.
 */
public class JobListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_list);

        final ListView view = (ListView) findViewById(R.id.job_list);
        if (view != null) {
            view.setAdapter(
                new JobStoreAdapter(this, CentralContainer.getStore(getApplicationContext())));
        }
    }

    public void onAddButtonClicked(View v) {
        startActivity(new Intent(this, JobFormActivity.class));
    }
}
