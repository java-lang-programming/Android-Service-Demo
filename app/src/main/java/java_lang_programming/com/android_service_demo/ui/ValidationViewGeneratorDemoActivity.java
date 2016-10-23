package java_lang_programming.com.android_service_demo.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java_lang_programming.com.android_service_demo.R;

public class ValidationViewGeneratorDemoActivity extends AppCompatActivity {

    private TextInputLayout mUsernameLayout;
    private AutoCompleteTextView mUsernameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_view_generator_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsernameLayout = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEND) {
                    String username = textView.getText().toString();
                    mUsernameLayout.setError(getInValidUsernameMessage(username));
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    /**
     * Return error message.
     * エラーがない場合は、nullを返す。
     *
     * @param str username
     * @return
     */
    public String getInValidUsernameMessage(String str) {
        if (TextUtils.isEmpty(str)) {
            return getString(R.string.validate_require, "username");
        }
        else if (str.length() < 4) {
            return getString(R.string.validate_minimun_word, "username", 4);
        }
        else if (str.length() > 15) {
            return getString(R.string.validate_maximun_word, "username", 15);
        }
        else {
            return null;
        }
    }

}
