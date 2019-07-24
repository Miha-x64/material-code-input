package com.raycoarana.codeinputview;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.UiThreadTestRule;
import static com.raycoarana.codeinputview.CodeInputMatchers.withCode;
import static org.junit.Assert.assertEquals;
import android.view.KeyEvent;

import com.raycoarana.codeinputview.test.R;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CodeInputViewTest {

    @Rule
    public ActivityTestRule<AppCompatActivity> mActivityRule = new ActivityTestRule<>(AppCompatActivity.class);

    @Rule
    public UiThreadTestRule mUiThreadRule = new UiThreadTestRule();

    private OnCodeCompleteListener mOnCodeCompleteListener;
    private AppCompatActivity mActivity;
    private String mActualCode;

    @Before
    public void setUpLayout() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void shouldReturnTypedCode() throws Throwable {
        givenAnActivityWithLayout(R.layout.simple_code_input);

        onView(withId(R.id.code_input)).perform(typeText("1234"));

        onView(withId(R.id.code_input)).check(matches(withCode("1234")));
    }

    @Test
    public void shouldNotLetEnterNonNumericCodeWhenInNumericMode() throws Throwable {
        givenAnActivityWithLayout(R.layout.simple_code_input);
        givenThatIsSetUpWith(CodeInputView.INPUT_TYPE_NUMERIC);

        onView(withId(R.id.code_input)).perform(typeText("ab12cd"));

        onView(withId(R.id.code_input)).check(matches(withCode("12")));
    }

    @Test
    public void shouldNotLetEnterNonAlphaNumericCodeWhenInTextMode() throws Throwable {
        givenAnActivityWithLayout(R.layout.simple_code_input);
        givenThatIsSetUpWith(CodeInputView.INPUT_TYPE_TEXT);

        onView(withId(R.id.code_input)).perform(typeText("*#12cd"));

        onView(withId(R.id.code_input)).check(matches(withCode("12cd")));
    }

    @Test
    public void shouldDeleteTypedCode() throws Throwable {
        givenAnActivityWithLayout(R.layout.simple_code_input);
        givenThatUserTypedCode("123");

        onView(withId(R.id.code_input)).perform(
                pressKey(KeyEvent.KEYCODE_DEL),
                pressKey(KeyEvent.KEYCODE_DEL)
        );

        onView(withId(R.id.code_input)).check(matches(withCode("1")));
    }

    @Test
    public void shouldExecuteListenerOnceCodeIsCompleted() throws Throwable {
        givenAnActivityWithLayout(R.layout.simple_code_input);
        givenThatACodeCompleteListenerIsAdded();

        onView(withId(R.id.code_input)).perform(typeText("123456"));

        assertEquals("123456", mActualCode);
    }

    @Test
    public void shouldExecuteListenerOnceCodeIsCompletedWithNonDefaultNumberOfCodes() throws Throwable {
        givenAnActivityWithLayout(R.layout.simple_code_input);
        givenThatACodeCompleteListenerIsAdded();
        givenThatNumberOfCodesAreFour();

        onView(withId(R.id.code_input)).perform(typeText("123456"));

        assertEquals("1234", mActualCode);
    }

    private void givenAnActivityWithLayout(@LayoutRes final int layoutResId) throws Throwable {
        mUiThreadRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.setContentView(layoutResId);
            }
        });
    }

    private void givenThatACodeCompleteListenerIsAdded() throws Throwable {
        mOnCodeCompleteListener = new OnCodeCompleteListener() {
            @Override
            public void onCompleted(String code) {
                mActualCode = code;
            }
        };
        mUiThreadRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CodeInputView codeInputView = findTargetView();
                codeInputView.setOnCompleteEventDelay(0);
                codeInputView.addOnCompleteListener(mOnCodeCompleteListener);
            }
        });
    }

    @NonNull
    private CodeInputView findTargetView() {
        CodeInputView view = mActivity.findViewById(R.id.code_input);
        if (view == null) {
            throw new IllegalStateException("Target view not found!");
        }
        return view;
    }

    private void givenThatUserTypedCode(String code) {
        onView(withId(R.id.code_input)).perform(typeText(code));
    }

    private void givenThatIsSetUpWith(final int inputType) throws Throwable {
        mUiThreadRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CodeInputView codeInputView = findTargetView();
                codeInputView.setInputType(inputType);
            }
        });
    }

    private void givenThatNumberOfCodesAreFour() throws Throwable {
        mUiThreadRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CodeInputView codeInputView = findTargetView();
                codeInputView.setLengthOfCode(4);
            }
        });
    }

}
