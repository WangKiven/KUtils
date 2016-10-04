package com.kiven.kutils.activityHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kiven.kutils.logHelper.KLog;

import java.lang.reflect.Constructor;

public class KHelperActivity extends AppCompatActivity {

	private KActivityHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

		getHelper(savedInstanceState);
        super.onCreate(savedInstanceState);

        if (helper == null){
            finish();
        }else {
            helper.onCreate(this, savedInstanceState);
        }
    }

	@SuppressWarnings({ "rawtypes" })
	protected KActivityHelper getHelper(Bundle savedInstanceState) {
    	if (helper == null) {//此判断是为了防止子类已设置helper的值
            if (savedInstanceState == null) {
                helper = (KActivityHelper) KActivityHelper.getStackValue(getIntent().getStringExtra("BaseActivityHelper"));
    		} else {
    			try {
    				Constructor[] constructors = Class.forName(savedInstanceState.getString("helper_name")).getConstructors();
    				if (constructors != null && constructors.length > 0) {
    					Constructor constructor = constructors[0];
    					Class[] types = constructor.getParameterTypes();
    					if (types != null && types.length > 0) {
							Object[] parameters = new Object[types.length];
							for(int i = 0; i < types.length; i++){
								parameters[i] = getDefaultValue(types[i]);
							}
							helper = (KActivityHelper) constructor.newInstance(parameters);
						}else {
							helper = (KActivityHelper) constructor.newInstance();
						}
					}
    				
    			} catch (Exception e) {
    				KLog.e(e);
    			}
    		}
		}
    	return helper;
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getDefaultValue(Class class1){
    	if (class1.isAssignableFrom(int.class) || class1.isAssignableFrom(Integer.class)
    			|| class1.isAssignableFrom(float.class) || class1.isAssignableFrom(Float.class)
    			|| class1.isAssignableFrom(double.class) || class1.isAssignableFrom(Double.class)) {
			return 0;
		}else if (class1.isAssignableFrom(boolean.class) || class1.isAssignableFrom(Boolean.class)) {
			return false;
		}
    	
    	return null;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	// TODO Auto-generated method stub
    	super.onSaveInstanceState(outState);
    	if (helper != null) {
        	outState.putString("helper_name", helper.getClass().getName());
        	helper.onSaveInstanceState(outState);
		}
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onRestoreInstanceState(savedInstanceState);
    	if (helper != null) {
        	helper.onRestoreInstanceState(savedInstanceState);
		}
    }

    public void onClick(View view){
    	if (helper != null) {
            helper.onClick(view);
		}
    }

    @Override
    protected void onStart() {
        super.onStart();
    	if (helper != null) {
            helper.onStart();
		}
    }

    @Override
    protected void onResume() {
        super.onResume();
    	if (helper != null) {
            helper.onResume();
		}
    }

    @Override
    protected void onPause() {
    	if (helper != null) {
            helper.onPause();
		}
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    	if (helper != null) {
            helper.onStop();
		}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    	if (helper != null) {
            helper.onDestroy();
		}
        KActivityHelper.removeStack(getIntent().getStringExtra("BaseActivityHelper"));//将helper移除
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	if (helper != null) {
            helper.onWindowFocusChanged(hasFocus);
		}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (helper == null) {
    		return onKeyDown(keyCode, event);
    	}else {
        	boolean b = super.onKeyDown(keyCode, event);
        	return b && helper.onKeyDown(keyCode, event);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (helper == null) {
			return false;
		} else {
			return helper.onCreateOptionsMenu(menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (helper == null) {
			return false;
		} else {
			return helper.onOptionsItemSelected(item);
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (helper != null) {
            helper.onActivityResult(requestCode, resultCode, data);
		}
    }
}
