package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import trukkeruae.trukkertech.com.trukkeruae.CommonClass.ConnectionDetector;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout otp_layout_forgotpw, change_pw_layout_forgotpw, mobile_no_layout_forgotpw, resend_otp;
    EditText ed_mobileno_forgotpw, ed_password_forgotpw, ed_confirmpw_forgotpw, otp_forgotpw;
    String mobileNumber, passWord, confirmPw, otp, otp_no;
    String jsonPostMobno, jsonCheckOtp, jsonPwChange;
    AppCompatButton send_mobno_forgotpw, send_otp_forgotpw, submit_newpw_forgotpw;
    TextView mobilenumberTv, forgot_pw_lable, lable, lable2, lable3;

    UserFunctions UF;
    SessionManager sm;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
    }

    private void init() {
        //set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");

        UF = new UserFunctions(ForgotPasswordActivity.this);
        sm = new SessionManager(ForgotPasswordActivity.this);
        cd = new ConnectionDetector(ForgotPasswordActivity.this);

        otp_layout_forgotpw = (LinearLayout) findViewById(R.id.otp_layout_forgotpw);
        otp_layout_forgotpw.setVisibility(View.GONE);
        mobile_no_layout_forgotpw = (LinearLayout) findViewById(R.id.mobile_no_layout_forgotpw);
        change_pw_layout_forgotpw = (LinearLayout) findViewById(R.id.change_pw_layout_forgotpw);
        resend_otp = (LinearLayout) findViewById(R.id.resend_otp);
        resend_otp.setOnClickListener(this);

        send_mobno_forgotpw = (AppCompatButton) findViewById(R.id.send_mobno_forgotpw);
        send_mobno_forgotpw.setOnClickListener(this);
        send_otp_forgotpw = (AppCompatButton) findViewById(R.id.send_otp_forgotpw);
        send_otp_forgotpw.setOnClickListener(this);
        submit_newpw_forgotpw = (AppCompatButton) findViewById(R.id.submit_newpw_forgotpw);
        submit_newpw_forgotpw.setOnClickListener(this);

        mobilenumberTv = (TextView) findViewById(R.id.mobilenumber);
        mobilenumberTv.setTypeface(font);
        forgot_pw_lable = (TextView) findViewById(R.id.forgot_pw_lable);
        forgot_pw_lable.setTypeface(font);
        lable = (TextView) findViewById(R.id.lable);
        lable.setTypeface(font);
        lable2 = (TextView) findViewById(R.id.lable2);
        lable2.setTypeface(font);
        lable3 = (TextView) findViewById(R.id.lable3);
        lable3.setTypeface(font);

        ed_mobileno_forgotpw = (EditText) findViewById(R.id.ed_mobileno_forgotpw);
        ed_mobileno_forgotpw.setTypeface(font);
        ed_password_forgotpw = (EditText) findViewById(R.id.ed_password_forgotpw);
        ed_password_forgotpw.setTypeface(font);
        ed_confirmpw_forgotpw = (EditText) findViewById(R.id.ed_confirmpw_forgotpw);
        ed_confirmpw_forgotpw.setTypeface(font);
        otp_forgotpw = (EditText) findViewById(R.id.otp_forgotpw);
        otp_forgotpw.setTypeface(font);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.send_mobno_forgotpw:
                sendMobNo();
                break;
            case R.id.send_otp_forgotpw:
                matchOTP();
                break;
            case R.id.submit_newpw_forgotpw:
                matchnewpw();
                break;
            case R.id.resend_otp:
                sendMobNo();
                break;
            default:
                break;
        }
    }

    private void matchnewpw() {

        passWord = ed_password_forgotpw.getText().toString().trim();
        confirmPw = ed_confirmpw_forgotpw.getText().toString().trim();
        if (!validate()) {

        } else {
            if (cd.isConnectingToInternet()) {
                if (confirmPw.equalsIgnoreCase(passWord)) {
                    new PwUpdate().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Password Not Match", Toast.LENGTH_SHORT).show();
                }

            } else {
                UF.msg("Please check your internet connection.");
            }
        }

    }

    private void matchOTP() {
        otp_no = otp_forgotpw.getText().toString();
        //Log.e("OTP...", "" + otp_no);
        if (!otp_no.equalsIgnoreCase("")) {
            if (cd.isConnectingToInternet()) {
                //json_save = "";
                new ValidateOTP().execute();

            } else {
                UF.msg("Please check your internet connection.");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Enter OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMobNo() {
        mobileNumber = ed_mobileno_forgotpw.getText().toString();
        if (!validate_mobno()) {

        } else {
            if (cd.isConnectingToInternet()) {
                new GetOtpNo().execute();
            } else {
                UF.msg("Please check your internet connection.");
            }
        }
    }

    public boolean validate() {
        boolean valid = true;
        if (passWord.isEmpty() || passWord.length() < 3 || passWord.length() > 10) {
            ed_password_forgotpw.setError("Please Enter Valid Password");
            valid = false;
        } else {
            ed_password_forgotpw.setError(null);
        }
        if (confirmPw.isEmpty() || confirmPw.length() < 3 || confirmPw.length() > 10) {
            ed_confirmpw_forgotpw.setError("Please Enter Valid Password");
            valid = false;
        } else {
            ed_confirmpw_forgotpw.setError(null);
        }

        return valid;
    }

    public boolean validate_mobno() {
        boolean valid = true;
        if (mobileNumber.isEmpty() || mobileNumber.length() < 10) {
            ed_mobileno_forgotpw.setError("Please Enter Valid Mobile Number");
            valid = false;
        } else {
            ed_mobileno_forgotpw.setError(null);
        }

        return valid;
    }

    //call service
    //call service for send mobile no & get OTP number for change password
    private class GetOtpNo extends AsyncTask<Void, Void, String> {
        //ProgressHUD mProgressHUD;
        ProgressDialog pDialog = new ProgressDialog(ForgotPasswordActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                JSONObject prms = new JSONObject();
                JSONArray jarray = new JSONArray();
                JSONObject prmsLogin = new JSONObject();
                prmsLogin.put("user_id", mobileNumber);
                jarray.put(prmsLogin);
                prms.put("User", jarray);
                //Log.e("Validuser-json", prms + "");
                jsonPostMobno = UF.LoginUser("login/ValidateuserAndsendOTP", prms);

               // Log.e("Validuser-response", jsonPostMobno + "");
            } catch (Exception e) {
                //UF.msg("Something went wrong");
            }
            return jsonPostMobno;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (jsonPostMobno != null) {
                    if (jsonPostMobno.equals("lost")) {
                        UF.msg("Internet connection lost or slow");
                    } else {
                        if (jsonPostMobno.equalsIgnoreCase("0")) {
                            UF.msg("Please Enter Register Mobile Number");

                        } else {
                            JSONObject jobj = new JSONObject(jsonPostMobno);
                            String status = jobj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                String servermsg = jobj.getString("message");
                                UF.msg(servermsg);
                                mobile_no_layout_forgotpw.setVisibility(View.GONE);
                                otp_layout_forgotpw.setVisibility(View.VISIBLE);

                                //mobilenumber.setText(mobileNumber);
                                String mob_num = ed_mobileno_forgotpw.getText().toString();
                                int length = mob_num.length();
                                if (length > 0) {
                                    char a = mob_num.charAt(length - (length));
                                    char a1 = mob_num.charAt(length - (length - 1));
                                    char a2 = mob_num.charAt(length - 3);
                                    char a3 = mob_num.charAt(length - 2);
                                    char a4 = mob_num.charAt(length - 1);
                                    mobilenumberTv.setText("+" + a + a1 + "*****" + a2 + a3 + a4);
                                } else {
                                    // mobilenumberTv.setText("+" + a + a1 + "*****" + a2 + a3 + a4);
                                    mobilenumberTv.setText("");
                                }


                            } else {
                                String servermsg = jobj.getString("message");
                                UF.msg(servermsg);
                                mobile_no_layout_forgotpw.setVisibility(View.VISIBLE);
                                otp_layout_forgotpw.setVisibility(View.GONE);
                            }

                        }
                    }
                } else {
                    UF.msg("Internet connection lost or slow");

                }
            } catch (Exception e) {
                UF.msg("Internet connection lost or slow");
            }

            pDialog.dismiss();
        }
    }

    //otp service call
    public class ResendOTP extends AsyncTask<Void, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(ForgotPasswordActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                JSONObject prms = new JSONObject();
                JSONObject prmsLogin = new JSONObject();
                JSONArray jarray = new JSONArray();
                prmsLogin.put("user_id", mobileNumber);
                prmsLogin.put("OTP", otp_no);
                jarray.put(prmsLogin);
                prms.put("User", jarray);
               // Log.e("ResendOTP-json", prms + "");
               // Log.e("otp_ed", otp_no);

                jsonCheckOtp = UF.LoginUser("Login/ResendOTP", prms);
            } catch (Exception e) {
                //UF.msg("Something went wrong");
            }
            Log.e("ResendOTP", jsonCheckOtp + "");
            return jsonCheckOtp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            try {
                if (jsonCheckOtp != null) {
                    if (jsonCheckOtp.equals("lost")) {
                        UF.msg("Internet connection loss or slow");
                    } else {
                        if (jsonCheckOtp.equalsIgnoreCase("0")) {
                            UF.msg("Invalid OTP");

                        } else {
                            JSONObject jobj = new JSONObject(jsonCheckOtp);
                            String status = jobj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                String servermsg = jobj.getString("message");

                                UF.msg("" + servermsg);

                                change_pw_layout_forgotpw.setVisibility(View.VISIBLE);
                                otp_layout_forgotpw.setVisibility(View.GONE);
                            } else {
                                String servermsg = jobj.getString("message");
                                UF.msg("" + servermsg);

                            }

                        }
                    }
                } else {
                    UF.msg("Internet connection loss or slow");

                }
            } catch (Exception e) {
                UF.msg("Internet connection loss or slow");

            }
        }
    }

    //service for update password
    private class ValidateOTP extends AsyncTask<Void, Void, String> {
        //ProgressHUD mProgressHUD;
        ProgressDialog pDialog = new ProgressDialog(ForgotPasswordActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                JSONObject prms = new JSONObject();
                JSONObject prmsLogin = new JSONObject();

                prmsLogin.put("user_id", mobileNumber);
                prmsLogin.put("password", "");
                prmsLogin.put("OTP", otp_no);
                prms.put("User", prmsLogin);
                Log.e("Updatepwd-json", prms + "");

                jsonPwChange = UF.LoginUser("Login/ValidateOTP", prms);
            } catch (Exception e) {
                UF.msg("Something went wrong");
            }
            Log.e("Updatepwd", jsonPwChange + "");
            return jsonPwChange;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (jsonPwChange != null) {
                    if (jsonPwChange.equals("lost")) {
                        UF.msg("Internet connection lost or slow");
                    } else {
                        if (jsonPwChange.equalsIgnoreCase("0")) {
                            UF.msg("Password Not Update");

                        } else {
                            JSONObject jobj = new JSONObject(jsonPwChange);
                            String status = jobj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                //String servermsg = jobj.getString("message");
                                change_pw_layout_forgotpw.setVisibility(View.VISIBLE);
                                otp_layout_forgotpw.setVisibility(View.GONE);
                            } else {
                                String servermsg = jobj.getString("message");
                                UF.msg(servermsg);
                            }

                        }
                    }
                } else {
                    UF.msg("Internet connection lost or slow");
                }
            } catch (Exception e) {
                UF.msg("Internet connection lost or slow");
            }

            pDialog.dismiss();
        }
    }

    //service for update password
    private class PwUpdate extends AsyncTask<Void, Void, String> {
        //ProgressHUD mProgressHUD;
        ProgressDialog pDialog = new ProgressDialog(ForgotPasswordActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                JSONObject prms = new JSONObject();
                JSONObject prmsLogin = new JSONObject();
                JSONArray jarray = new JSONArray();

                prmsLogin.put("user_id", mobileNumber);
                prmsLogin.put("password", confirmPw);
                jarray.put(prmsLogin);
                prms.put("User", jarray);
                Log.e("Updatepwd-json", prms + "");

                jsonPwChange = UF.LoginUser("Login/UpdateForgotpwd", prms);
            } catch (Exception e) {
                UF.msg("Something went wrong");
            }
            Log.e("Updatepwd", jsonPwChange + "");
            return jsonPwChange;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (jsonPwChange != null) {
                    if (jsonPwChange.equals("lost")) {
                        UF.msg("Internet connection lost or slow");

                    } else {
                        if (jsonPwChange.equalsIgnoreCase("0")) {
                            UF.msg("Password Not Update");

                        } else {
                            JSONObject jobj = new JSONObject(jsonPwChange);
                            String status = jobj.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                String servermsg = jobj.getString("message");

                                UF.msg("" + servermsg);
                                //goto login page
                                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(i);

                            } else {
                                String servermsg = jobj.getString("message");
                                UF.msg(servermsg);
                            }

                        }
                    }
                } else {
                    UF.msg("Internet connection lost or slow");
                }
            } catch (Exception e) {
                UF.msg("Internet connection lost or slow");
            }

            pDialog.dismiss();
        }
    }
}
