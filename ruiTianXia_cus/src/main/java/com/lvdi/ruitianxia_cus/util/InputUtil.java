package com.lvdi.ruitianxia_cus.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.tsz.afinal.FinalActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class InputUtil {
	private static String nameFilter(String str) throws PatternSyntaxException {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	public static void filterName(final EditText inputEditText) {
		inputEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String editable = inputEditText.getText().toString();
				String str = nameFilter(editable); // 过滤特殊字符
				if (!editable.equals(str)) {
					inputEditText.setText(str);
				}
				inputEditText.setSelection(inputEditText.length());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public static void filterAddress(final EditText inputEditText) {
		inputEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String editable = inputEditText.getText().toString();
				String str = nameFilter(editable); // 过滤特殊字符
				if (!editable.equals(str)) {
					int selection = inputEditText.getSelectionStart();
					inputEditText.setText(str);
					inputEditText.setSelection(selection
							- (editable.length() - str.length()));
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
}
