package jp.ac.kyudo.MainSelect;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class CalendarDialogFragment extends DialogFragment {
    private String confirmedStr = "";

    // ダイアログが生成された時に呼ばれるメソッド ※必須
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 今日の日付のカレンダーインスタンスを取得
        final Calendar calendar = Calendar.getInstance();

        // ダイアログ生成  DatePickerDialogのBuilderクラスを指定してインスタンス化
        final DatePickerDialog dateBuilder = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 選択された年・月・日を整形 ※月は0-11なので+1している
//                        String dateStr = String.valueOf(year*10000+(month + 1)*100 + dayOfMonth);
//                        SelectMenuActivity callingActivity=(SelectMenuActivity) getActivity();
//                        assert callingActivity != null;
//                        ReturnValue(dateStr);
                    }

                },

                calendar.get(Calendar.YEAR), // 初期選択年
                calendar.get(Calendar.MONTH), // 初期選択月
                calendar.get(Calendar.DAY_OF_MONTH) // 初期選択日


        );
        dateBuilder.setButton(DialogInterface.BUTTON_POSITIVE, "確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 選択された年・月・日を整形 ※月は0-11なので+1している
                String dateStr = String.valueOf(dateBuilder.getDatePicker().getYear() * 10000 + (dateBuilder.getDatePicker().getMonth() + 1) * 100 + dateBuilder.getDatePicker().getDayOfMonth());
                SelectMenuActivity callingActivity = (SelectMenuActivity) getActivity();
                assert callingActivity != null;
                callingActivity.onReturnValue(dateStr);
            }
        });
        dateBuilder.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // dateBulderを返す
        return dateBuilder;
    }

}




