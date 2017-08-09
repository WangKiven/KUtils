package com.kiven.kutils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.kiven.kutils.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by kiven on 2017/8/7.
 */

public class NumberEditText extends AppCompatEditText implements InputFilter {
    BigDecimal minValue;
    BigDecimal maxValue;
    int decimalLength = -1;
    int intLength = -1;

    public NumberEditText(Context context) {
        super(context);
    }

    public NumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NumberEditText);
        if (typedArray == null) {
            return;
        }

//        if (getInputType() != InputType.TYPE_CLASS_NUMBER)

        // 最小值
        if (typedArray.hasValue(R.styleable.NumberEditText_min_value)) {
            try {
                minValue = new BigDecimal(typedArray.getString(R.styleable.NumberEditText_min_value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 最大值
        if (typedArray.hasValue(R.styleable.NumberEditText_max_value)) {
            try {
                maxValue = new BigDecimal(typedArray.getString(R.styleable.NumberEditText_max_value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 小数点后长度
        if (typedArray.hasValue(R.styleable.NumberEditText_decimal_length)) {
            decimalLength = typedArray.getInt(R.styleable.NumberEditText_decimal_length, -1);
        }
        // 整数位长度
        if (typedArray.hasValue(R.styleable.NumberEditText_int_length)) {
            intLength = typedArray.getInt(R.styleable.NumberEditText_int_length, -1);
        }
        // 回收
        typedArray.recycle();
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        if (filters == null && filters.length == 0) {
            super.setFilters(new InputFilter[]{this});
        } else {
            InputFilter[] newFilters = new InputFilter[filters.length + 1];
            for (int i = 0; i < filters.length; i++) {
                if (filters[i] == this) {
                    super.setFilters(filters);
                    return;
                } else {
                    newFilters[i] = filters[i];
                }
            }
            newFilters[filters.length] = this;

            super.setFilters(newFilters);
        }
    }

    DecimalFormat formater = null;

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null || text.length() == 0) {
            super.setText(text, type);
        } else {
            BigDecimal decimal;
            try {
                decimal = new BigDecimal(text.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (formater == null) {
                formater = new DecimalFormat("#.##");
                formater.setGroupingUsed(false);//当为false时上述设置的分组大小无效，为true时才能进行分组
                formater.setRoundingMode(RoundingMode.HALF_UP);// 四舍五入
            }

            String fs = formater.format(decimal);
            if (isOk(fs)) {
                super.setText(fs, type);
            }
        }
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = new BigDecimal(maxValue);
    }

    public void setMinValue(String minValue) {
        this.minValue = new BigDecimal(minValue);
    }

    public void setDecimalLength(int decimalLength) {
        this.decimalLength = decimalLength;
    }

    public void setIntLength(int intLength) {
        this.intLength = intLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        int il = end - start;
        if (il > 0) {
            CharSequence s1 = dest.subSequence(0, dstart);
            CharSequence s2 = dest.subSequence(dend, dest.length());

            String ls = TextUtils.concat(s1, source.subSequence(start, end), s2).toString();


            int dot = ls.indexOf('.');
            if (dot > -1) {
                if ((intLength > -1 && dot > intLength) || (decimalLength > -1 && ls.length() - dot - 1 > decimalLength)) {
                    return "";
                }
            } else {
                if (intLength > -1 && ls.length() > intLength) {
                    return "";
                }
            }

            BigDecimal nowValue;
            try {
                nowValue = new BigDecimal(ls);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

            BigDecimal oldValue;
            if (dest.length() > 0) {
                try {
                    oldValue = new BigDecimal(dest.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                oldValue = new BigDecimal(0);
            }

            if (maxValue != null && (nowValue.compareTo(maxValue) == 1 && oldValue.compareTo(maxValue) != 1)) {
                return "";
            }
            // 最小值不做判断，因为初始化时最小值相当于0，用户输出数据时，也会小于最小值。
            /*if (minValue != null && (nowValue.compareTo(minValue) == -1 && oldValue.compareTo(minValue) != -1)) {
                return "";
            }*/
        }
        return null;
    }

    /**
     * 输入是否正确
     */
    public boolean isOk() {
        return isOk(getText().toString());
    }


    private boolean isOk(String ls) {
        int dot = ls.indexOf('.');
        if (dot > -1) {
            if ((intLength > -1 && dot > intLength) || (decimalLength > -1 && ls.length() - dot - 1 > decimalLength)) {
                return false;
            }
        } else {
            if (intLength > -1 && ls.length() > intLength) {
                return false;
            }
        }

        BigDecimal nowValue;
        try {
            nowValue = new BigDecimal(ls);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (maxValue != null && (nowValue.compareTo(maxValue) == 1)) {
            return false;
        }

        if (minValue != null && nowValue.compareTo(minValue) == -1) {
            return false;
        }

        return true;
    }
}
